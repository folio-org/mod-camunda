package org.folio.rest.camunda.utility;

import static org.folio.rest.camunda.utility.MappingUtility.CALL_NUMBER_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.CLASSIFICATION_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.CONTRIBUTOR_NAME_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.CONTRIBUTOR_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.ELECTRONIC_ACCESS_URL;
import static org.folio.rest.camunda.utility.MappingUtility.FIELD_PROTECTION_SETTINGS_URL;
import static org.folio.rest.camunda.utility.MappingUtility.HOLDINGS_NOTE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.HOLDINGS_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.IDENTIFIER_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.ILL_POLICIES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_ALTERNATIVE_TITLE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_FORMATS_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_NOTE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_RELATIONSHIP_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_STATUSES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.INSTANCE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.ISSUANCE_MODES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.ITEM_DAMAGED_STATUSES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.ITEM_NOTE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.LOAN_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.LOCATIONS_URL;
import static org.folio.rest.camunda.utility.MappingUtility.MAPPING_RULES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.MATERIAL_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.NATURE_OF_CONTENT_TERMS_URL;
import static org.folio.rest.camunda.utility.MappingUtility.STATISTICAL_CODES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.STATISTICAL_CODE_TYPES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.SUBJECT_SOURCES_URL;
import static org.folio.rest.camunda.utility.MappingUtility.SUBJECT_TYPES_URL;
import static org.folio.rest.camunda.utility.TestUtility.i;
import static org.folio.rest.camunda.utility.TestUtility.om;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.json.DecodeException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
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
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class MappingUtilityTest {

  @Spy
  private RestTemplate mockRestTemplate;

  private final static String OKAPI_URL = "http://localhost:9130";

  @BeforeEach
  void beforeEach() throws RestClientException, IOException {
    mockJsonResponse("Alternativetitletypes.json", INSTANCE_ALTERNATIVE_TITLE_TYPES_URL, Alternativetitletypes.class);
    mockJsonResponse("Callnumbertypes.json", CALL_NUMBER_TYPES_URL, Callnumbertypes.class);
    mockJsonResponse("Classificationtypes.json", CLASSIFICATION_TYPES_URL, Classificationtypes.class);
    mockJsonResponse("Contributornametypes.json", CONTRIBUTOR_NAME_TYPES_URL, Contributornametypes.class);
    mockJsonResponse("Contributortypes.json", CONTRIBUTOR_TYPES_URL, Contributortypes.class);
    mockJsonResponse("Electronicaccessrelationships.json", ELECTRONIC_ACCESS_URL, Electronicaccessrelationships.class);
    mockJsonResponse("Holdingsnotetypes.json", HOLDINGS_NOTE_TYPES_URL, Holdingsnotetypes.class);
    mockJsonResponse("Holdingstypes.json", HOLDINGS_TYPES_URL, Holdingstypes.class);
    mockJsonResponse("Identifiertypes.json", IDENTIFIER_TYPES_URL, Identifiertypes.class);
    mockJsonResponse("Illpolicies.json", ILL_POLICIES_URL, Illpolicies.class);
    mockJsonResponse("Instanceformats.json", INSTANCE_FORMATS_URL, Instanceformats.class);
    mockJsonResponse("Instancenotetypes.json", INSTANCE_NOTE_TYPES_URL, Instancenotetypes.class);
    mockJsonResponse("Instancerelationshiptypes.json", INSTANCE_RELATIONSHIP_TYPES_URL, Instancerelationshiptypes.class);
    mockJsonResponse("Instancestatuses.json", INSTANCE_STATUSES_URL, Instancestatuses.class);
    mockJsonResponse("Instancetypes.json", INSTANCE_TYPES_URL, Instancetypes.class);
    mockJsonResponse("Issuancemodes.json", ISSUANCE_MODES_URL, Issuancemodes.class);
    mockJsonResponse("Itemdamagedstatuses.json", ITEM_DAMAGED_STATUSES_URL, Itemdamagedstatuses.class);
    mockJsonResponse("Itemnotetypes.json", ITEM_NOTE_TYPES_URL, Itemnotetypes.class);
    mockJsonResponse("Loantypes.json", LOAN_TYPES_URL, Loantypes.class);
    mockJsonResponse("Locations.json", LOCATIONS_URL, Locations.class);
    mockJsonResponse("MarcFieldProtectionSettingsCollection.json", FIELD_PROTECTION_SETTINGS_URL, MarcFieldProtectionSettingsCollection.class);
    mockJsonResponse("Materialtypes.json", MATERIAL_TYPES_URL, Materialtypes.class);
    mockJsonResponse("Natureofcontentterms.json", NATURE_OF_CONTENT_TERMS_URL, Natureofcontentterms.class);
    mockJsonResponse("rules.json", MAPPING_RULES_URL, String.class);
    mockJsonResponse("Statisticalcodes.json", STATISTICAL_CODES_URL, Statisticalcodes.class);
    mockJsonResponse("Statisticalcodetypes.json", STATISTICAL_CODE_TYPES_URL, Statisticalcodetypes.class);
    mockJsonResponse("Subjectsources.json", SUBJECT_SOURCES_URL, SubjectSources.class);
    mockJsonResponse("Subjecttypes.json", SUBJECT_TYPES_URL, SubjectTypes.class);

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
      assertThrows(data.exception.getClass(), () -> MappingUtility.mapRecordToInsance(marcJson, okapiUrl, tenant, token));
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
   *   The test method parameters:
   *     - input of type String (MARC JSON)
   *     - expected output of type String (JSON FOLIO instance).
   *     - exception expected to be thrown
   */
  static Stream<Parameters<String, String>> testMapRecordToInsanceStream() throws IOException {
    return Stream.of(
      Parameters.of(null, null, new NullPointerException()),
      Parameters.of("", null, new DecodeException()),
      Parameters.of(i("/marc4j/54-56-008008027-0.mrc.json"), i("/folio/instances/54-008008027.json")),
      Parameters.of(i("/marc4j/54-56-008008027-1.mrc.json"), i("/folio/instances/55-008008027.json")),
      Parameters.of(i("/marc4j/54-56-008008027-2.mrc.json"), i("/folio/instances/56-008008027.json")),
      Parameters.of(i("/marc4j/54-56-008008027-3.mrc.json"), i("/folio/instances/57-008008027.json")),
      Parameters.of(i("/marc4j/54-56-008008027-4.mrc.json"), i("/folio/instances/58-008008027.json"))
    );
  }

  /**
   * Mock the JSON response in a lenient manner.
   *
   * This is intended to simplify the mock code.
   *
   * @param name
   *   The name of the JSON file from the FOLIO settings path.
   * @param url
   *   The URL this JSON file is being returned from.
   * @param clazz
   *   The class type the JSON file is being returned as.
   *
   * @throws IOException On I/O error.
   * @throws RestClientException On Rest Client error.
   */
  private void mockJsonResponse(String name, String url, Class<?> clazz) throws RestClientException, IOException {
    lenient()
      .doReturn(i("/folio/settings/" + name, clazz))
      .when(mockRestTemplate).exchange(eq(OKAPI_URL + url), eq(HttpMethod.GET), any(), eq(clazz));
  }

}
