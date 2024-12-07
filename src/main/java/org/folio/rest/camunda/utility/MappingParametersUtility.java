package org.folio.rest.camunda.utility;

import java.util.HashMap;
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
import org.springframework.web.client.RestClientException;

/**
 * Utility class for managing mapping parameters and fetching configuration data
 * for various library-related types in a library management system.
 *
 * This class provides methods to retrieve mapping rules and parameters for different
 * library-specific entities such as alternative title types, contributor types,
 * instance formats, and more.
 *
 * The utility uses an {@link OkapiRestTemplate} to fetch data from predefined REST endpoints,
 * and maintains a static mapping of parameter types to their respective descriptors
 * and endpoint paths.
 *
 * @see MappingParametersDescriptor
 * @see MappingParameters
 */
public class MappingParametersUtility {

  /**
   * Maximum limit for fetching parameters from endpoints.
   * This constant ensures that parameter retrieval does not exceed 1000 items.
   */
  private static final int LIMIT = 1000;

  /**
   * Base path for mapping rules related to MARC bibliographic records.
   */
  static final String MAPPING_RULES_PATH = "/mapping-rules/marc-bib";

  static final String ALTERNATIVE_TITLE_TYPES_PATH = "/alternative-title-types?limit=" + LIMIT;
  static final String CALL_NUMBER_TYPES_PATH = "/call-number-types?limit=" + LIMIT;
  static final String CLASSIFICATION_TYPES_PATH = "/classification-types?limit=" + LIMIT;
  static final String CONTRIBUTOR_NAME_TYPES_PATH = "/contributor-name-types?limit=" + LIMIT;
  static final String CONTRIBUTOR_TYPES_PATH = "/contributor-types?limit=" + LIMIT;
  static final String ELECTRONIC_ACCESS_PATH = "/electronic-access-relationships?limit=" + LIMIT;
  static final String HOLDINGS_NOTE_TYPES_PATH = "/holdings-note-types?limit=" + LIMIT;
  static final String HOLDINGS_TYPES_PATH = "/holdings-types?limit=" + LIMIT;
  static final String IDENTIFIER_TYPES_PATH = "/identifier-types?limit=" + LIMIT;
  static final String ILL_POLICIES_PATH = "/ill-policies?limit=" + LIMIT;
  static final String INSTANCE_FORMATS_PATH = "/instance-formats?limit=" + LIMIT;
  static final String INSTANCE_NOTE_TYPES_PATH = "/instance-note-types?limit=" + LIMIT;
  static final String INSTANCE_RELATIONSHIP_TYPES_PATH = "/instance-relationship-types?limit=" + LIMIT;
  static final String INSTANCE_STATUSES_PATH = "/instance-statuses?limit=" + LIMIT;
  static final String INSTANCE_TYPES_PATH = "/instance-types?limit=" + LIMIT;
  static final String ISSUANCE_MODES_PATH = "/modes-of-issuance?limit=" + LIMIT;
  static final String ITEM_DAMAGED_STATUSES_PATH = "/item-damaged-statuses?limit=" + LIMIT;
  static final String ITEM_NOTE_TYPES_PATH = "/item-note-types?limit=" + LIMIT;
  static final String LOAN_TYPES_PATH = "/loan-types?limit=" + LIMIT;
  static final String LOCATIONS_PATH = "/locations?limit=" + LIMIT;
  static final String MARC_FIELD_PROTECTION_SETTINGS_PATH = "/field-protection-settings/marc?limit=" + LIMIT;
  static final String MATERIAL_TYPES_PATH = "/material-types?limit=" + LIMIT;
  static final String NATURE_OF_CONTENT_TERMS_PATH = "/nature-of-content-terms?limit=" + LIMIT;
  static final String STATISTICAL_CODE_TYPES_PATH = "/statistical-code-types?limit=" + LIMIT;
  static final String STATISTICAL_CODES_PATH = "/statistical-codes?limit=" + LIMIT;
  static final String SUBJECT_SOURCES_PATH = "/subject-sources?limit=" + LIMIT;
  static final String SUBJECT_TYPES_PATH = "/subject-types?limit=" + LIMIT;

  /**
   * A static map that associates parameter classes with their corresponding
   * mapping parameter descriptors and endpoint paths.
   *
   * This map is used to dynamically retrieve parameters for various library
   * entity types during initialization.
   */
  public static final Map<Class<?>, MappingParametersDescriptor<?,?>> PARAMETER_DESCRIPTOR_MAP = new HashMap<>();

  static {
    PARAMETER_DESCRIPTOR_MAP.put(AlternativeTitleType.class, MappingParametersDescriptor.of(Alternativetitletypes.class, ALTERNATIVE_TITLE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(CallNumberType.class, MappingParametersDescriptor.of(Callnumbertypes.class, CALL_NUMBER_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ClassificationType.class, MappingParametersDescriptor.of(Classificationtypes.class, CLASSIFICATION_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ContributorNameType.class, MappingParametersDescriptor.of(Contributornametypes.class, CONTRIBUTOR_NAME_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ContributorType.class, MappingParametersDescriptor.of(Contributortypes.class, CONTRIBUTOR_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ElectronicAccessRelationship.class, MappingParametersDescriptor.of(Electronicaccessrelationships.class, ELECTRONIC_ACCESS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(HoldingsNoteType.class, MappingParametersDescriptor.of(Holdingsnotetypes.class, HOLDINGS_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(HoldingsType.class, MappingParametersDescriptor.of(Holdingstypes.class, HOLDINGS_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(IdentifierType.class, MappingParametersDescriptor.of(Identifiertypes.class, IDENTIFIER_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(IllPolicy.class, MappingParametersDescriptor.of(Illpolicies.class, ILL_POLICIES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceFormat.class, MappingParametersDescriptor.of(Instanceformats.class, INSTANCE_FORMATS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceNoteType.class, MappingParametersDescriptor.of(Instancenotetypes.class, INSTANCE_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceRelationshipType.class, MappingParametersDescriptor.of(Instancerelationshiptypes.class, INSTANCE_RELATIONSHIP_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceStatus.class, MappingParametersDescriptor.of(Instancestatuses.class, INSTANCE_STATUSES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(InstanceType.class, MappingParametersDescriptor.of(Instancetypes.class, INSTANCE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(IssuanceMode.class, MappingParametersDescriptor.of(Issuancemodes.class, ISSUANCE_MODES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ItemDamageStatus.class, MappingParametersDescriptor.of(Itemdamagedstatuses.class, ITEM_DAMAGED_STATUSES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(ItemNoteType.class, MappingParametersDescriptor.of(Itemnotetypes.class, ITEM_NOTE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Loantype.class, MappingParametersDescriptor.of(Loantypes.class, LOAN_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Location.class, MappingParametersDescriptor.of(Locations.class, LOCATIONS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(MarcFieldProtectionSetting.class, MappingParametersDescriptor.of(MarcFieldProtectionSettingsCollection.class, MARC_FIELD_PROTECTION_SETTINGS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(Mtype.class, MappingParametersDescriptor.of(Materialtypes.class, MATERIAL_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(NatureOfContentTerm.class, MappingParametersDescriptor.of(Natureofcontentterms.class, NATURE_OF_CONTENT_TERMS_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(StatisticalCodeType.class, MappingParametersDescriptor.of(Statisticalcodetypes.class, STATISTICAL_CODE_TYPES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(StatisticalCode.class, MappingParametersDescriptor.of(Statisticalcodes.class, STATISTICAL_CODES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(SubjectSource.class, MappingParametersDescriptor.of(SubjectSources.class, SUBJECT_SOURCES_PATH));
    PARAMETER_DESCRIPTOR_MAP.put(SubjectType.class, MappingParametersDescriptor.of(SubjectTypes.class, SUBJECT_TYPES_PATH));
  }

  /**
   * Fetches the mapping rules from the predefined REST endpoint.
   *
   * @param restTemplate The REST template used to make the HTTP request
   * @return A {@link JsonObject} containing the mapping rules
   * @throws RestClientException if there's an error fetching the rules
   */
  public static JsonObject fetchRules(OkapiRestTemplate restTemplate) {
    ResponseEntity<String> response = restTemplate.getForEntity(MAPPING_RULES_PATH, String.class);

    return response.hasBody()
      ? new JsonObject(response.getBody())
      : new JsonObject();
  }

  /**
   * Retrieves comprehensive mapping parameters for various library entity types.
   *
   * This method fetches parameters for multiple types such as alternative title types,
   * contributor types, instance formats, and more using the provided REST template.
   *
   * @param restTemplate The REST template used to fetch parameters from different endpoints
   * @return A fully initialized {@link MappingParameters} object with all parameters
   */
  public static MappingParameters getMappingParamaters(OkapiRestTemplate restTemplate) {
    return new MappingParameters()
        .withInitializedState(true)
        .withAlternativeTitleTypes(((Alternativetitletypes) getParameters(restTemplate, AlternativeTitleType.class)).getAlternativeTitleTypes())
        .withCallNumberTypes(((Callnumbertypes) getParameters(restTemplate, CallNumberType.class)).getCallNumberTypes())
        .withClassificationTypes(((Classificationtypes) getParameters(restTemplate, ClassificationType.class)).getClassificationTypes())
        .withContributorNameTypes(((Contributornametypes) getParameters(restTemplate, ContributorNameType.class)).getContributorNameTypes())
        .withContributorTypes(((Contributortypes) getParameters(restTemplate, ContributorType.class)).getContributorTypes())
        .withElectronicAccessRelationships(((Electronicaccessrelationships) getParameters(restTemplate, ElectronicAccessRelationship.class)).getElectronicAccessRelationships())
        .withHoldingsNoteTypes(((Holdingsnotetypes) getParameters(restTemplate, HoldingsNoteType.class)).getHoldingsNoteTypes())
        .withHoldingsTypes(((Holdingstypes) getParameters(restTemplate, HoldingsType.class)).getHoldingsTypes())
        .withIdentifierTypes(((Identifiertypes) getParameters(restTemplate, IdentifierType.class)).getIdentifierTypes())
        .withIllPolicies(((Illpolicies) getParameters(restTemplate, IllPolicy.class)).getIllPolicies())
        .withInstanceFormats(((Instanceformats) getParameters(restTemplate, InstanceFormat.class)).getInstanceFormats())
        .withInstanceNoteTypes(((Instancenotetypes) getParameters(restTemplate, InstanceNoteType.class)).getInstanceNoteTypes())
        .withInstanceRelationshipTypes(((Instancerelationshiptypes) getParameters(restTemplate, InstanceRelationshipType.class)).getInstanceRelationshipTypes())
        .withInstanceStatuses(((Instancestatuses) getParameters(restTemplate, InstanceStatus.class)).getInstanceStatuses())
        .withInstanceTypes(((Instancetypes) getParameters(restTemplate, InstanceType.class)).getInstanceTypes())
        .withIssuanceModes(((Issuancemodes) getParameters(restTemplate, IssuanceMode.class)).getIssuanceModes())
        .withItemDamagedStatuses(((Itemdamagedstatuses) getParameters(restTemplate, ItemDamageStatus.class)).getItemDamageStatuses())
        .withItemNoteTypes(((Itemnotetypes) getParameters(restTemplate, ItemNoteType.class)).getItemNoteTypes())
        .withLoanTypes(((Loantypes) getParameters(restTemplate, Loantype.class)).getLoantypes())
        .withLocations(((Locations) getParameters(restTemplate, Location.class)).getLocations())
        .withMarcFieldProtectionSettings(((MarcFieldProtectionSettingsCollection) getParameters(restTemplate, MarcFieldProtectionSetting.class)).getMarcFieldProtectionSettings())
        .withMaterialTypes(((Materialtypes) getParameters(restTemplate, Mtype.class)).getMtypes())
        .withNatureOfContentTerms(((Natureofcontentterms) getParameters(restTemplate, NatureOfContentTerm.class)).getNatureOfContentTerms())
        .withStatisticalCodes(((Statisticalcodes) getParameters(restTemplate, StatisticalCode.class)).getStatisticalCodes())
        .withStatisticalCodeTypes(((Statisticalcodetypes) getParameters(restTemplate, StatisticalCodeType.class)).getStatisticalCodeTypes())
        .withSubjectSources(((SubjectSources) getParameters(restTemplate, SubjectSource.class)).getSubjectSources())
        .withSubjectTypes(((SubjectTypes) getParameters(restTemplate, SubjectType.class)).getSubjectTypes());
  }

  /**
   * Generic method to retrieve parameters for a specific type using the
   * pre-configured parameter descriptor map.
   *
   * @param <C> The type of the collection or parameters to be returned
   * @param <P> The type of the parameter class
   * @param okapiRestRemplate The REST template used to fetch parameters
   * @param parametersType The class of the parameters to retrieve
   * @return Parameters of the specified type
   * @throws IllegalArgumentException if no descriptor is found for the given type
   */
  @SuppressWarnings("unchecked")
  public static <C, P> C getParameters(OkapiRestTemplate okapiRestRemplate, P parametersType) {
    MappingParametersDescriptor<C, P> descriptor = (MappingParametersDescriptor<C, P>) PARAMETER_DESCRIPTOR_MAP.get(parametersType);
    if (descriptor == null) {
        throw new IllegalArgumentException("No descriptor found for type: " + parametersType);
    }

    return descriptor.getParameters(okapiRestRemplate);
  }

}
