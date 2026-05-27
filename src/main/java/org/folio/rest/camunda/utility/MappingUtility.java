package org.folio.rest.camunda.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.folio.Instance;
import org.folio.processing.mapping.defaultmapper.MarcToInstanceMapper;
import org.folio.processing.mapping.defaultmapper.processor.parameters.MappingParameters;
import org.jspecify.annotations.NonNull;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

/**
 * Utility class for mapping between different data formats and transforming
 * records in the FOLIO library management system context.
 *
 * This class provides static methods for converting CSV to JSON and mapping
 * MARC records to FOLIO Instance records using predefined mapping rules and
 * parameters.
 */
public class MappingUtility {

  /** Error message for null or empty CSV input. */
  private static final String ILLEGAL_CSV_ARGUMENT_MESSAGE = "CSV cannot be null or empty";

  /** Error message for null or empty MARC JSON input. */
  private static final String ILLEGAL_MARC_JSON_ARGUMENT_MESSAGE = "MARC JSON record cannot be null or empty";

  /** Error message for null or empty OKAPI URL input. */
  private static final String ILLEGAL_OKAPI_URL_ARGUMENT_MESSAGE = "Okapi URL cannot be null or empty";

  /** Error message for null or empty tenant identifier input. */
  private static final String ILLEGAL_TENANT_ARGUMENT_MESSAGE = "Tenant identifier cannot be null or empty";

  /** Error message for null or empty authentication token input. */
  private static final String ILLEGAL_TOKEN_ARGUMENT_MESSAGE = "Authentication token cannot be null or empty";

  /** Mapper for converting MARC records to FOLIO Instance records. */
  private static final MarcToInstanceMapper marcToInstanceMapper = new MarcToInstanceMapper();

  /** Jackson JsonMapper for JSON serialization and de-serialization. */
  private static final JsonMapper mapper = JsonMapper
    .builderWithJackson2Defaults()
    .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES, true)
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
    .changeDefaultPropertyInclusion(incl -> incl
      .withValueInclusion(JsonInclude.Include.NON_NULL)
      .withContentInclusion(JsonInclude.Include.NON_NULL)
    )
    .findAndAddModules()
    .build();

  /** Rest template for making Okapi-based REST calls. */
  static OkapiRestTemplate restTemplate = new OkapiRestTemplate();

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private MappingUtility() {

  }

  /**
   * Converts a CSV string to a JSON array representation.
   *
   * <p>
   * This method reads a CSV with headers and transforms it into a JSON array
   * where each element is an object representing a row from the CSV.
   * </p>
   *
   * @param csv The input CSV string to be converted
   *
   * @return A JSON string representing the CSV data as an array of objects
   *
   * @throws IllegalArgumentException If the CSV input is null or empty
   */
  public static String mapCsvToJson(String csv) {
    if (StringUtils.isEmpty(csv)) {
      throw new IllegalArgumentException(ILLEGAL_CSV_ARGUMENT_MESSAGE);
    }
    CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
    CsvMapper csvMapper = new CsvMapper();
    MappingIterator<Map<String, String>> mappingIterator = csvMapper.reader()
      .forType(Map.class)
      .with(csvSchema)
      .readValues(csv);

    return mapper.writeValueAsString(mappingIterator.readAll());
  }

  /**
   * Maps a MARC record to a FOLIO Instance record using OKAPI services.
   *
   * <p>
   * This method converts a MARC JSON record to a FOLIO Instance record by:
   * </p>
   * <ol>
   * <li>Fetching mapping rules from OKAPI</li>
   * <li>Retrieving mapping parameters</li>
   * <li>Applying the MARC to Instance mapping</li>
   * </ol>
   *
   * @param marcJson The MARC record in JSON format.
   * @param okapiUrl The base URL for OKAPI services.
   * @param tenant   The FOLIO tenant identifier.
   * @param token    Authentication token for OKAPI services.
   *
   * @return A JSON string representation of the mapped FOLIO Instance.
   *\
   * @throws IllegalArgumentException If any of the input parameters are null or empty.
   */
  public static String mapRecordToInstance(String marcJson, String okapiUrl, String tenant, String token) {

    if (StringUtils.isEmpty(marcJson)) {
      throw new IllegalArgumentException(ILLEGAL_MARC_JSON_ARGUMENT_MESSAGE);
    }
    if (StringUtils.isEmpty(okapiUrl)) {
      throw new IllegalArgumentException(ILLEGAL_OKAPI_URL_ARGUMENT_MESSAGE);
    }
    if (StringUtils.isEmpty(tenant)) {
      throw new IllegalArgumentException(ILLEGAL_TENANT_ARGUMENT_MESSAGE);
    }
    if (StringUtils.isEmpty(token)) {
      throw new IllegalArgumentException(ILLEGAL_TOKEN_ARGUMENT_MESSAGE);
    }

    return mapRecordToInstance(restTemplate.at(okapiUrl).with(tenant, token), marcJson);
  }

  /**
   * Internal method to map a MARC record to a FOLIO Instance using a pre-configured rest template.
   *
   * @param restTemplate The configured OkapiRestTemplate.
   * @param marcJson     The MARC record in JSON format (must not be null).
   *
   * @return A JSON string representation of the mapped FOLIO Instance
   */
  private static String mapRecordToInstance(OkapiRestTemplate restTemplate, @NonNull String marcJson) {

    JsonObject parsedRecord = new JsonObject(marcJson);
    JsonNode mappingRulesNode = MappingParametersUtility.fetchRules(restTemplate);
    JsonObject mappingRules = mappingRulesNode == null
        ? JsonObject.of()
        : new JsonObject(mapper.writeValueAsString(mappingRulesNode));

    MappingParameters mappingParameters = MappingParametersUtility.getMappingParamaters(restTemplate);
    Instance instance = marcToInstanceMapper.mapRecord(parsedRecord, mappingParameters, mappingRules);

    return mapper.writeValueAsString(instance);
  }

}
