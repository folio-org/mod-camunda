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

public class MappingParametersUtility {

  private static final int LIMIT = 1000;

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

  public static JsonObject fetchRules(OkapiRestTemplate restTemplate) {
    ResponseEntity<String> response = restTemplate.getForEntity(MAPPING_RULES_PATH, String.class);

    return new JsonObject(response.getBody());
  }

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

  @SuppressWarnings("unchecked")
  public static <P, C> C getParameters(OkapiRestTemplate okapiRestRemplate, P parametersType) {
    return (C) PARAMETER_DESCRIPTOR_MAP.get(parametersType)
        .getParameters(okapiRestRemplate);
  }

}
