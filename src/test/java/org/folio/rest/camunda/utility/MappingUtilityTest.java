package org.folio.rest.camunda.utility;

import static org.folio.rest.camunda.utility.MappingParametersUtility.ALTERNATIVE_TITLE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.CALL_NUMBER_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.CLASSIFICATION_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.CONTRIBUTOR_NAME_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.CONTRIBUTOR_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.ELECTRONIC_ACCESS_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.HOLDINGS_NOTE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.HOLDINGS_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.IDENTIFIER_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.ILL_POLICIES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.INSTANCE_FORMATS_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.INSTANCE_NOTE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.INSTANCE_RELATIONSHIP_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.INSTANCE_STATUSES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.INSTANCE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.ISSUANCE_MODES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.ITEM_DAMAGED_STATUSES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.ITEM_NOTE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.LOAN_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.LOCATIONS_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.MAPPING_RULES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.MARC_FIELD_PROTECTION_SETTINGS_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.MATERIAL_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.NATURE_OF_CONTENT_TERMS_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.STATISTICAL_CODES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.STATISTICAL_CODE_TYPES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.SUBJECT_SOURCES_PATH;
import static org.folio.rest.camunda.utility.MappingParametersUtility.SUBJECT_TYPES_PATH;
import static org.folio.rest.camunda.utility.TestUtility.i;
import static org.folio.rest.camunda.utility.TestUtility.om;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.json.DecodeException;
import org.folio.Alternativetitletypes;
import org.folio.Callnumbertypes;
import org.folio.Classificationtypes;
import org.folio.Contributornametypes;
import org.folio.Contributortypes;
import org.folio.Electronicaccessrelationships;
import org.folio.Holdingsnotetypes;
import org.folio.Holdingstypes;
import org.folio.Identifiertypes;
import org.folio.Illpolicies;
import org.folio.Instanceformats;
import org.folio.Instancenotetypes;
import org.folio.Instancerelationshiptypes;
import org.folio.Instancestatuses;
import org.folio.Instancetypes;
import org.folio.Issuancemodes;
import org.folio.Itemdamagedstatuses;
import org.folio.Itemnotetypes;
import org.folio.Loantypes;
import org.folio.Locations;
import org.folio.MarcFieldProtectionSettingsCollection;
import org.folio.Materialtypes;
import org.folio.Natureofcontentterms;
import org.folio.Statisticalcodes;
import org.folio.Statisticalcodetypes;
import org.folio.SubjectSources;
import org.folio.SubjectTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class MappingUtilityTest {

  private final static String OKAPI_URL = "http://localhost:9130";

  @Spy
  private OkapiRestTemplate mockRestTemplate;

  @BeforeEach
  void beforeEach() throws RestClientException, IOException {
    mockJsonResponse("Alternativetitletypes.json", ALTERNATIVE_TITLE_TYPES_PATH, Alternativetitletypes.class);
    mockJsonResponse("Callnumbertypes.json", CALL_NUMBER_TYPES_PATH, Callnumbertypes.class);
    mockJsonResponse("Classificationtypes.json", CLASSIFICATION_TYPES_PATH, Classificationtypes.class);
    mockJsonResponse("Contributornametypes.json", CONTRIBUTOR_NAME_TYPES_PATH, Contributornametypes.class);
    mockJsonResponse("Contributortypes.json", CONTRIBUTOR_TYPES_PATH, Contributortypes.class);
    mockJsonResponse("Electronicaccessrelationships.json", ELECTRONIC_ACCESS_PATH, Electronicaccessrelationships.class);
    mockJsonResponse("Holdingsnotetypes.json", HOLDINGS_NOTE_TYPES_PATH, Holdingsnotetypes.class);
    mockJsonResponse("Holdingstypes.json", HOLDINGS_TYPES_PATH, Holdingstypes.class);
    mockJsonResponse("Identifiertypes.json", IDENTIFIER_TYPES_PATH, Identifiertypes.class);
    mockJsonResponse("Illpolicies.json", ILL_POLICIES_PATH, Illpolicies.class);
    mockJsonResponse("Instanceformats.json", INSTANCE_FORMATS_PATH, Instanceformats.class);
    mockJsonResponse("Instancenotetypes.json", INSTANCE_NOTE_TYPES_PATH, Instancenotetypes.class);
    mockJsonResponse("Instancerelationshiptypes.json", INSTANCE_RELATIONSHIP_TYPES_PATH, Instancerelationshiptypes.class);
    mockJsonResponse("Instancestatuses.json", INSTANCE_STATUSES_PATH, Instancestatuses.class);
    mockJsonResponse("Instancetypes.json", INSTANCE_TYPES_PATH, Instancetypes.class);
    mockJsonResponse("Issuancemodes.json", ISSUANCE_MODES_PATH, Issuancemodes.class);
    mockJsonResponse("Itemdamagedstatuses.json", ITEM_DAMAGED_STATUSES_PATH, Itemdamagedstatuses.class);
    mockJsonResponse("Itemnotetypes.json", ITEM_NOTE_TYPES_PATH, Itemnotetypes.class);
    mockJsonResponse("Loantypes.json", LOAN_TYPES_PATH, Loantypes.class);
    mockJsonResponse("Locations.json", LOCATIONS_PATH, Locations.class);
    mockJsonResponse("MarcFieldProtectionSettingsCollection.json", MARC_FIELD_PROTECTION_SETTINGS_PATH, MarcFieldProtectionSettingsCollection.class);
    mockJsonResponse("Materialtypes.json", MATERIAL_TYPES_PATH, Materialtypes.class);
    mockJsonResponse("Natureofcontentterms.json", NATURE_OF_CONTENT_TERMS_PATH, Natureofcontentterms.class);
    mockJsonResponse("rules.json", MAPPING_RULES_PATH, String.class);
    mockJsonResponse("Statisticalcodes.json", STATISTICAL_CODES_PATH, Statisticalcodes.class);
    mockJsonResponse("Statisticalcodetypes.json", STATISTICAL_CODE_TYPES_PATH, Statisticalcodetypes.class);
    mockJsonResponse("Subjectsources.json", SUBJECT_SOURCES_PATH, SubjectSources.class);
    mockJsonResponse("Subjecttypes.json", SUBJECT_TYPES_PATH, SubjectTypes.class);

    MappingUtility.restTemplate = mockRestTemplate;
  }

  @ParameterizedTest
  @MethodSource("testMapRecordToInsanceStream")
  void testMapRecordToInsance(Parameters<String, String> data) throws JsonProcessingException {
    String marcJson = data.input;
    String okapiUrl = OKAPI_URL;
    String tenant = "diku";
    String token = "";

    if (Objects.nonNull(data.exception)) {
      assertThrows(data.exception.getClass(),
          () -> MappingUtility.mapRecordToInsance(marcJson, okapiUrl, tenant, token));
    } else {
      ObjectNode expected = (ObjectNode) om(data.expected);
      expected.remove("id");
      ObjectNode actual = (ObjectNode) om(MappingUtility.mapRecordToInsance(marcJson, okapiUrl, tenant, token));
      actual.remove("id");

      assertEquals(expected, actual);
    }
  }

  /**
   * Stream parameters for testing mapRecordToInsance.
   *
   * @return
   *         The test method parameters:
   *         - input of type String (MARC JSON)
   *         - expected output of type String (JSON FOLIO instance).
   *         - exception expected to be thrown
   */
  static Stream<Parameters<String, String>> testMapRecordToInsanceStream() throws IOException {
    return Stream.of(
        Parameters.of(null, null, new NullPointerException()),
        Parameters.of("", null, new DecodeException()),
        Parameters.of(i("/marc4j/54-56-008008027-0.mrc.json"), i("/folio/instances/54-008008027.json")),
        Parameters.of(i("/marc4j/54-56-008008027-1.mrc.json"), i("/folio/instances/55-008008027.json")),
        Parameters.of(i("/marc4j/54-56-008008027-2.mrc.json"), i("/folio/instances/56-008008027.json")),
        Parameters.of(i("/marc4j/54-56-008008027-3.mrc.json"), i("/folio/instances/57-008008027.json")),
        Parameters.of(i("/marc4j/54-56-008008027-4.mrc.json"), i("/folio/instances/58-008008027.json")));
  }

  /**
   * Mock the JSON response in a lenient manner.
   *
   * This is intended to simplify the mock code.
   *
   * @param name
   *              The name of the JSON file from the FOLIO settings path.
   * @param path
   *              The Okapi path for this JSON.
   * @param clazz
   *              The class type the JSON file is being returned as.
   *
   * @throws IOException         On I/O error.
   * @throws RestClientException On Rest Client error.
   */
  private void mockJsonResponse(String name, String path, Class<?> clazz) throws RestClientException, IOException {
    lenient()
        .doReturn(i("/folio/settings/" + name, clazz))
        .when(mockRestTemplate).getForEntity(eq(path), eq(clazz));
  }

}
