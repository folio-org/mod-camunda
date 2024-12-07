package org.folio.rest.camunda.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;
import org.folio.AlternativeTitleType;
import org.folio.Alternativetitletypes;
import org.folio.CallNumberType;
import org.folio.Callnumbertypes;
import org.folio.ClassificationType;
import org.folio.Classificationtypes;
import org.folio.ContributorNameType;
import org.folio.ContributorType;
import org.folio.Contributornametypes;
import org.folio.Contributortypes;
import org.folio.ElectronicAccessRelationship;
import org.folio.Electronicaccessrelationships;
import org.folio.HoldingsNoteType;
import org.folio.HoldingsType;
import org.folio.Holdingsnotetypes;
import org.folio.Holdingstypes;
import org.folio.IdentifierType;
import org.folio.Identifiertypes;
import org.folio.IllPolicy;
import org.folio.Illpolicies;
import org.folio.InstanceFormat;
import org.folio.InstanceNoteType;
import org.folio.InstanceRelationshipType;
import org.folio.InstanceStatus;
import org.folio.InstanceType;
import org.folio.Instanceformats;
import org.folio.Instancenotetypes;
import org.folio.Instancerelationshiptypes;
import org.folio.Instancestatuses;
import org.folio.Instancetypes;
import org.folio.IssuanceMode;
import org.folio.Issuancemodes;
import org.folio.ItemDamageStatus;
import org.folio.ItemNoteType;
import org.folio.Itemdamagedstatuses;
import org.folio.Itemnotetypes;
import org.folio.Loantype;
import org.folio.Loantypes;
import org.folio.Location;
import org.folio.Locations;
import org.folio.MarcFieldProtectionSettingsCollection;
import org.folio.Materialtypes;
import org.folio.Mtype;
import org.folio.NatureOfContentTerm;
import org.folio.Natureofcontentterms;
import org.folio.StatisticalCode;
import org.folio.StatisticalCodeType;
import org.folio.Statisticalcodes;
import org.folio.Statisticalcodetypes;
import org.folio.SubjectSource;
import org.folio.SubjectSources;
import org.folio.SubjectType;
import org.folio.SubjectTypes;
import org.folio.processing.mapping.defaultmapper.processor.parameters.MappingParameters;
import org.folio.rest.jaxrs.model.MarcFieldProtectionSetting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MappingParametersBuilder {

  private static final int LIMIT = 1000;

  static final String MAPPING_RULES_PATH = "/mapping-rules/marc-bib";
  static final String IDENTIFIER_TYPES_PATH = "/identifier-types?limit=" + LIMIT;
  static final String CLASSIFICATION_TYPES_PATH = "/classification-types?limit=" + LIMIT;
  static final String INSTANCE_TYPES_PATH = "/instance-types?limit=" + LIMIT;
  static final String ELECTRONIC_ACCESS_PATH = "/electronic-access-relationships?limit=" + LIMIT;
  static final String INSTANCE_FORMATS_PATH = "/instance-formats?limit=" + LIMIT;
  static final String CONTRIBUTOR_TYPES_PATH = "/contributor-types?limit=" + LIMIT;
  static final String CONTRIBUTOR_NAME_TYPES_PATH = "/contributor-name-types?limit=" + LIMIT;
  static final String INSTANCE_NOTE_TYPES_PATH = "/instance-note-types?limit=" + LIMIT;
  static final String INSTANCE_ALTERNATIVE_TITLE_TYPES_PATH = "/alternative-title-types?limit=" + LIMIT;
  static final String NATURE_OF_CONTENT_TERMS_PATH = "/nature-of-content-terms?limit=" + LIMIT;
  static final String INSTANCE_STATUSES_PATH = "/instance-statuses?limit=" + LIMIT;
  static final String INSTANCE_RELATIONSHIP_TYPES_PATH = "/instance-relationship-types?limit=" + LIMIT;
  static final String HOLDINGS_TYPES_PATH = "/holdings-types?limit=" + LIMIT;
  static final String HOLDINGS_NOTE_TYPES_PATH = "/holdings-note-types?limit=" + LIMIT;
  static final String ILL_POLICIES_PATH = "/ill-policies?limit=" + LIMIT;
  static final String CALL_NUMBER_TYPES_PATH = "/call-number-types?limit=" + LIMIT;
  static final String STATISTICAL_CODES_PATH = "/statistical-codes?limit=" + LIMIT;
  static final String STATISTICAL_CODE_TYPES_PATH = "/statistical-code-types?limit=" + LIMIT;
  static final String SUBJECT_SOURCES_PATH = "/subject-sources?limit=" + LIMIT;
  static final String SUBJECT_TYPES_PATH = "/subject-types?limit=" + LIMIT;
  static final String LOCATIONS_PATH = "/locations?limit=" + LIMIT;
  static final String MATERIAL_TYPES_PATH = "/material-types?limit=" + LIMIT;
  static final String ITEM_DAMAGED_STATUSES_PATH = "/item-damaged-statuses?limit=" + LIMIT;
  static final String LOAN_TYPES_PATH = "/loan-types?limit=" + LIMIT;
  static final String ITEM_NOTE_TYPES_PATH = "/item-note-types?limit=" + LIMIT;
  static final String FIELD_PROTECTION_SETTINGS_PATH = "/field-protection-settings/marc?limit=" + LIMIT;
  static final String ISSUANCE_MODES_PATH = "/modes-of-issuance?limit=" + LIMIT;

  public static final Map<Class<?>, ParametersDescriptor> PARAMETER_DESCRIPTOR_MAP = new HashMap<>();

  static {
    PARAMETER_DESCRIPTOR_MAP.put(IdentifierType.class, new ParametersDescriptor(Identifiertypes.class, IDENTIFIER_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ClassificationType.class, new ParametersDescriptor(Classificationtypes.class, CLASSIFICATION_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceType.class, new ParametersDescriptor(Instancetypes.class, INSTANCE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ElectronicAccessRelationship.class, new ParametersDescriptor(Electronicaccessrelationships.class, ELECTRONIC_ACCESS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceFormat.class, new ParametersDescriptor(Instanceformats.class, INSTANCE_FORMATS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ContributorType.class, new ParametersDescriptor(Contributortypes.class, CONTRIBUTOR_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ContributorNameType.class, new ParametersDescriptor(Contributornametypes.class, CONTRIBUTOR_NAME_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceNoteType.class, new ParametersDescriptor(Instancenotetypes.class, INSTANCE_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(AlternativeTitleType.class, new ParametersDescriptor(Alternativetitletypes.class, INSTANCE_ALTERNATIVE_TITLE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(NatureOfContentTerm.class, new ParametersDescriptor(Natureofcontentterms.class, NATURE_OF_CONTENT_TERMS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceStatus.class, new ParametersDescriptor(Instancestatuses.class, INSTANCE_STATUSES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceRelationshipType.class, new ParametersDescriptor(Instancerelationshiptypes.class, INSTANCE_RELATIONSHIP_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(HoldingsType.class, new ParametersDescriptor(Holdingstypes.class, HOLDINGS_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(HoldingsNoteType.class, new ParametersDescriptor(Holdingsnotetypes.class, HOLDINGS_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(IllPolicy.class, new ParametersDescriptor(Illpolicies.class, ILL_POLICIES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(CallNumberType.class, new ParametersDescriptor(Callnumbertypes.class, CALL_NUMBER_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(StatisticalCode.class, new ParametersDescriptor(Statisticalcodes.class, STATISTICAL_CODES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(StatisticalCodeType.class, new ParametersDescriptor(Statisticalcodetypes.class, STATISTICAL_CODE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(SubjectSource.class, new ParametersDescriptor(SubjectSources.class, SUBJECT_SOURCES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(SubjectType.class, new ParametersDescriptor(SubjectTypes.class, SUBJECT_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Location.class, new ParametersDescriptor(Locations.class, LOCATIONS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Mtype.class, new ParametersDescriptor(Materialtypes.class, MATERIAL_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ItemDamageStatus.class, new ParametersDescriptor(Itemdamagedstatuses.class, ITEM_DAMAGED_STATUSES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Loantype.class, new ParametersDescriptor(Loantypes.class, LOAN_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ItemNoteType.class, new ParametersDescriptor(Itemnotetypes.class, ITEM_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(MarcFieldProtectionSetting.class, new ParametersDescriptor(MarcFieldProtectionSettingsCollection.class, FIELD_PROTECTION_SETTINGS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(IssuanceMode.class, new ParametersDescriptor(Issuancemodes.class, ISSUANCE_MODES_PATH));
  }

  public static JsonObject fetchRules(OkapiRestTemplate restTemplate) {
    ResponseEntity<String> response = restTemplate.getForEntity(MAPPING_RULES_PATH, String.class);

    return new JsonObject(response.getBody());
  }

  public static MappingParameters getMappingParamaters(OkapiRestTemplate restTemplate) {
    return new MappingParameters()
        .withInitializedState(true)
        .withIdentifierTypes(fetchParameters(restTemplate, IdentifierType.class))
        .withClassificationTypes(fetchParameters(restTemplate, ClassificationType.class))
        .withInstanceTypes(fetchParameters(restTemplate, InstanceType.class))
        .withElectronicAccessRelationships(fetchParameters(restTemplate, ElectronicAccessRelationship.class))
        .withInstanceFormats(fetchParameters(restTemplate, InstanceFormat.class))
        .withContributorTypes(fetchParameters(restTemplate, ContributorType.class))
        .withContributorNameTypes(fetchParameters(restTemplate, ContributorNameType.class))
        .withInstanceNoteTypes(fetchParameters(restTemplate, InstanceNoteType.class))
        .withAlternativeTitleTypes(fetchParameters(restTemplate, AlternativeTitleType.class))
        .withIssuanceModes(fetchParameters(restTemplate, IssuanceMode.class))
        .withInstanceStatuses(fetchParameters(restTemplate, InstanceStatus.class))
        .withNatureOfContentTerms(fetchParameters(restTemplate, NatureOfContentTerm.class))
        .withInstanceRelationshipTypes(fetchParameters(restTemplate, InstanceRelationshipType.class))
        .withInstanceRelationshipTypes(fetchParameters(restTemplate, InstanceRelationshipType.class))
        .withHoldingsTypes(fetchParameters(restTemplate, HoldingsType.class))
        .withHoldingsNoteTypes(fetchParameters(restTemplate, HoldingsNoteType.class))
        .withIllPolicies(fetchParameters(restTemplate, IllPolicy.class))
        .withCallNumberTypes(fetchParameters(restTemplate, CallNumberType.class))
        .withStatisticalCodes(fetchParameters(restTemplate, StatisticalCode.class))
        .withStatisticalCodeTypes(fetchParameters(restTemplate, StatisticalCodeType.class))
        .withSubjectSources(fetchParameters(restTemplate, SubjectSource.class))
        .withSubjectTypes(fetchParameters(restTemplate, SubjectType.class))
        .withLocations(fetchParameters(restTemplate, Location.class))
        .withMaterialTypes(fetchParameters(restTemplate, Mtype.class))
        .withItemDamagedStatuses(fetchParameters(restTemplate, ItemDamageStatus.class))
        .withLoanTypes(fetchParameters(restTemplate, Loantype.class))
        .withItemNoteTypes(fetchParameters(restTemplate, ItemNoteType.class))
        .withMarcFieldProtectionSettings(fetchParameters(restTemplate, MarcFieldProtectionSetting.class));
  }

  private static <P> List<P> fetchParameters(RestTemplate okapiRestRemplate, Class<P> parametersType) {
    ParametersDescriptor parameterDescriptor = PARAMETER_DESCRIPTOR_MAP.get(parametersType);
    ResponseEntity<?> response = okapiRestRemplate.getForEntity(
        parameterDescriptor.getPath(), parameterDescriptor.getCollectionType());

    return getParameters(response.getBody(), parametersType);
  }

  @SuppressWarnings("unchecked")
  private static <C, P> List<P> getParameters(C body, Class<P> parametersType) {
    List<P> parameters = new ArrayList<>();
    for (Method method : parametersType.getDeclaredMethods()) {
      Type[] parameterTypes = method.getGenericParameterTypes();
      if (parameterTypes.length == 1 && parameterTypes[0] instanceof ParameterizedType) {
        ParameterizedType paramType = (ParameterizedType) parameterTypes[0];
        Type rawType = paramType.getRawType();
        if (rawType == List.class) {
          Type[] typeArguments = paramType.getActualTypeArguments();
          if (typeArguments.length == 1 && typeArguments[0] == parametersType) {
            try {
              parameters = (List<P>) method.invoke(body);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    return parameters;
  }

  private static class ParametersDescriptor {

    private final Class<?> collectionType;

    private final String path;

    private ParametersDescriptor(Class<?> collectionType, String path) {
      this.collectionType = collectionType;
      this.path = path;
    }

    public Class<?> getCollectionType() {
      return collectionType;
    }

    public String getPath() {
      return path;
    }

  }

}
