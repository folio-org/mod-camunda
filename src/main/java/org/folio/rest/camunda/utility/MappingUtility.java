package org.folio.rest.camunda.utility;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.vertx.core.json.JsonObject;
import org.folio.Instance;
import org.folio.processing.mapping.defaultmapper.MarcToInstanceMapper;
import org.folio.processing.mapping.defaultmapper.processor.parameters.MappingParameters;

public class MappingUtility {

  private static final MarcToInstanceMapper marcToInstanceMapper = new MarcToInstanceMapper();

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static OkapiRestTemplate restTemplate = OkapiRestTemplate.build();

  private MappingUtility() {

  }

  public static String mapCsvToJson(String csv) throws IOException {
    CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
    CsvMapper csvMapper = new CsvMapper();
    MappingIterator<Map<String, String>> mappingIterator = csvMapper.reader()
        .forType(Map.class)
        .with(csvSchema)
        .readValues(csv);

    return objectMapper.writeValueAsString(mappingIterator.readAll());
  }

  public static String mapRecordToInsance(String marcJson, String okapiUrl, String tenant, String token)
      throws JsonProcessingException {
    return mapRecordToInsance(restTemplate.at(okapiUrl).with(tenant, token), marcJson);
  }

  private static String mapRecordToInsance(OkapiRestTemplate restTemplate, String marcJson)
      throws JsonProcessingException {
    JsonObject parsedRecord = new JsonObject(marcJson);
    JsonObject mappingRules = MappingParametersUtility.fetchRules(restTemplate);
    MappingParameters mappingParameters = MappingParametersUtility.getMappingParamaters(restTemplate);
    Instance instance = marcToInstanceMapper.mapRecord(parsedRecord, mappingParameters, mappingRules);

    return objectMapper.writeValueAsString(instance);
  }

}
