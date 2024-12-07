package org.folio.rest.camunda.utility;

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
import org.folio.rest.jaxrs.model.MarcFieldProtectionSetting;

public enum MappingParametersType {

  ALTERNATIVE_TITLE_TYPES(AlternativeTitleType.class, Alternativetitletypes.class, "/alternative-title-types"),
  CALL_NUMBER_TYPES(CallNumberType.class, Callnumbertypes.class, "/call-number-types"),
  CLASSIFICATION_TYPES(ClassificationType.class, Classificationtypes.class, "/classification-types"),
  CONTRIBUTOR_NAME_TYPES(ContributorNameType.class, Contributornametypes.class, "/contributor-name-types"),
  CONTRIBUTOR_TYPES(ContributorType.class, Contributortypes.class, "/contributor-types"),
  ELECTRONIC_ACCESS_RELATIONSHIPS(ElectronicAccessRelationship.class, Electronicaccessrelationships.class, "/electronic-access-relationships"),
  HOLDINGS_NOTE_TYPES(HoldingsNoteType.class, Holdingsnotetypes.class, "/holdings-note-types"),
  HOLDINGS_TYPES(HoldingsType.class, Holdingstypes.class, "/holdings-types"),
  IDENTIFIER_TYPES(IdentifierType.class, Identifiertypes.class, "/identifier-types"),
  ILL_POLICIES(IllPolicy.class, Illpolicies.class, "/ill-policies"),
  INSTANCE_FORMATS(InstanceFormat.class, Instanceformats.class, "/instance-formats"),
  INSTANCE_NOTE_TYPES(InstanceNoteType.class, Instancenotetypes.class, "/instance-note-types"),
  INSTANCE_RELATIONSHIP_TYPES(InstanceRelationshipType.class, Instancerelationshiptypes.class, "/instance-relationship-types"),
  INSTANCE_STATUSES(InstanceStatus.class, Instancestatuses.class, "/instance-statuses"),
  INSTANCE_TYPES(InstanceType.class, Instancetypes.class, "/instance-types"),
  ISSUANCE_MODES(IssuanceMode.class, Issuancemodes.class, "/modes-of-issuance"),
  ITEM_DAMAGED_STATUSES(ItemDamageStatus.class, Itemdamagedstatuses.class, "/item-damaged-statuses"),
  ITEM_NOTE_TYPES(ItemNoteType.class, Itemnotetypes.class, "/item-note-types"),
  LOAN_TYPES(Loantype.class, Loantypes.class, "/loan-types"),
  LOCATIONS(Location.class, Locations.class, "/locations"),
  MARC_FIELD_PROTECTION_SETTINGS(MarcFieldProtectionSetting.class, MarcFieldProtectionSettingsCollection.class, "/field-protection-settings/marc"),
  MATERIAL_TYPES(Mtype.class, Materialtypes.class, "/material-types"),
  NATURE_OF_CONTENT_TERMS(NatureOfContentTerm.class, Natureofcontentterms.class, "/nature-of-content-terms"),
  STATISTICAL_CODE_TYPES(StatisticalCodeType.class, Statisticalcodetypes.class, "/statistical-code-types"),
  STATISTICAL_CODES(StatisticalCode.class, Statisticalcodes.class, "/statistical-codes"),
  SUBJECT_SOURCES(SubjectSource.class, SubjectSources.class, "/subject-sources"),
  SUBJECT_TYPES(SubjectType.class, SubjectTypes.class, "/subject-types");

  private final Class<?> parametersType;
  private final Class<?> collectionType;
  private final String path;

  MappingParametersType(Class<?> parametersType, Class<?> collectionType, String path) {
    this.parametersType = parametersType;
    this.collectionType = collectionType;
    this.path = path;
  }

  public Class<?> getParametersType() {
    return parametersType;
  }

  public Class<?> getCollectionType() {
    return collectionType;
  }

  public String getPath() {
    return path;
  }

  public String getPath(int limit) {
    return path + "?limit=" + limit;
  }

}
