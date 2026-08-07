package org.folio.rest.camunda.utility;

import static org.folio.rest.camunda.utility.MappingParametersUtility.LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import java.util.stream.Stream;
import org.folio.MarcFieldProtectionSettingsCollection;
import org.folio.rest.camunda.utility.MappingParametersType.ParametersTypeLookupException;
import org.folio.rest.jaxrs.model.AlternativeTitleType;
import org.folio.rest.jaxrs.model.AlternativeTitleTypes;
import org.folio.rest.jaxrs.model.CallNumberType;
import org.folio.rest.jaxrs.model.CallNumberTypes;
import org.folio.rest.jaxrs.model.ClassificationType;
import org.folio.rest.jaxrs.model.ClassificationTypes;
import org.folio.rest.jaxrs.model.ContributorNameType;
import org.folio.rest.jaxrs.model.ContributorNameTypes;
import org.folio.rest.jaxrs.model.ContributorType;
import org.folio.rest.jaxrs.model.ContributorTypes;
import org.folio.rest.jaxrs.model.ElectronicAccessRelationship;
import org.folio.rest.jaxrs.model.ElectronicAccessRelationships;
import org.folio.rest.jaxrs.model.HoldingsNoteType;
import org.folio.rest.jaxrs.model.HoldingsNoteTypes;
import org.folio.rest.jaxrs.model.HoldingsType;
import org.folio.rest.jaxrs.model.HoldingsTypes;
import org.folio.rest.jaxrs.model.IdentifierType;
import org.folio.rest.jaxrs.model.IdentifierTypes;
import org.folio.rest.jaxrs.model.IllPolicies;
import org.folio.rest.jaxrs.model.IllPolicy;
import org.folio.rest.jaxrs.model.InstanceFormat;
import org.folio.rest.jaxrs.model.InstanceFormats;
import org.folio.rest.jaxrs.model.InstanceNoteType;
import org.folio.rest.jaxrs.model.InstanceNoteTypes;
import org.folio.rest.jaxrs.model.InstanceRelationshipType;
import org.folio.rest.jaxrs.model.InstanceRelationshipTypes;
import org.folio.rest.jaxrs.model.InstanceStatus;
import org.folio.rest.jaxrs.model.InstanceStatuses;
import org.folio.rest.jaxrs.model.InstanceType;
import org.folio.rest.jaxrs.model.InstanceTypes;
import org.folio.rest.jaxrs.model.IssuanceMode;
import org.folio.rest.jaxrs.model.IssuanceModes;
import org.folio.rest.jaxrs.model.ItemDamageStatus;
import org.folio.rest.jaxrs.model.ItemDamageStatuses;
import org.folio.rest.jaxrs.model.ItemNoteType;
import org.folio.rest.jaxrs.model.ItemNoteTypes;
import org.folio.rest.jaxrs.model.LoanType;
import org.folio.rest.jaxrs.model.LoanTypes;
import org.folio.rest.jaxrs.model.Location;
import org.folio.rest.jaxrs.model.Locations;
import org.folio.rest.jaxrs.model.MarcFieldProtectionSetting;
import org.folio.rest.jaxrs.model.MaterialType;
import org.folio.rest.jaxrs.model.MaterialTypes;
import org.folio.rest.jaxrs.model.NatureOfContentTerm;
import org.folio.rest.jaxrs.model.NatureOfContentTerms;
import org.folio.rest.jaxrs.model.StatisticalCode;
import org.folio.rest.jaxrs.model.StatisticalCodeType;
import org.folio.rest.jaxrs.model.StatisticalCodeTypes;
import org.folio.rest.jaxrs.model.StatisticalCodes;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingParametersTypeTest {

  @ParameterizedTest
  @MethodSource("testGetParametersTypeStream")
  void testGetParametersType(MappingParametersType type, Class<?> parametersType) {
    assertEquals(parametersType, type.getParametersType());
  }

  static Stream<Arguments> testGetParametersTypeStream() {
    return Stream.of(
      Arguments.of(MappingParametersType.ALTERNATIVE_TITLE_TYPES, AlternativeTitleType.class),
      Arguments.of(MappingParametersType.CALL_NUMBER_TYPES, CallNumberType.class),
      Arguments.of(MappingParametersType.CLASSIFICATION_TYPES, ClassificationType.class),
      Arguments.of(MappingParametersType.CONTRIBUTOR_NAME_TYPES, ContributorNameType.class),
      Arguments.of(MappingParametersType.CONTRIBUTOR_TYPES, ContributorType.class),
      Arguments.of(MappingParametersType.ELECTRONIC_ACCESS_RELATIONSHIPS, ElectronicAccessRelationship.class),
      Arguments.of(MappingParametersType.HOLDINGS_NOTE_TYPES, HoldingsNoteType.class),
      Arguments.of(MappingParametersType.HOLDINGS_TYPES, HoldingsType.class),
      Arguments.of(MappingParametersType.IDENTIFIER_TYPES, IdentifierType.class),
      Arguments.of(MappingParametersType.ILL_POLICIES, IllPolicy.class),
      Arguments.of(MappingParametersType.INSTANCE_FORMATS, InstanceFormat.class),
      Arguments.of(MappingParametersType.INSTANCE_NOTE_TYPES, InstanceNoteType.class),
      Arguments.of(MappingParametersType.INSTANCE_RELATIONSHIP_TYPES, InstanceRelationshipType.class),
      Arguments.of(MappingParametersType.INSTANCE_STATUSES, InstanceStatus.class),
      Arguments.of(MappingParametersType.INSTANCE_TYPES, InstanceType.class),
      Arguments.of(MappingParametersType.ISSUANCE_MODES, IssuanceMode.class),
      Arguments.of(MappingParametersType.ITEM_DAMAGED_STATUSES, ItemDamageStatus.class),
      Arguments.of(MappingParametersType.ITEM_NOTE_TYPES, ItemNoteType.class),
      Arguments.of(MappingParametersType.LOAN_TYPES, LoanType.class),
      Arguments.of(MappingParametersType.LOCATIONS, Location.class),
      Arguments.of(MappingParametersType.MARC_FIELD_PROTECTION_SETTINGS, MarcFieldProtectionSetting.class),
      Arguments.of(MappingParametersType.MATERIAL_TYPES, MaterialType.class),
      Arguments.of(MappingParametersType.NATURE_OF_CONTENT_TERMS, NatureOfContentTerm.class),
      Arguments.of(MappingParametersType.STATISTICAL_CODE_TYPES, StatisticalCodeType.class),
      Arguments.of(MappingParametersType.STATISTICAL_CODES, StatisticalCode.class));
  }

  @ParameterizedTest
  @MethodSource("testGetCollectionTypeStream")
  void testGetCollectionType(MappingParametersType type, Class<?> collectionType) {
    assertEquals(collectionType, type.getCollectionType());
  }

  static Stream<Arguments> testGetCollectionTypeStream() {
    return Stream.of(
      Arguments.of(MappingParametersType.ALTERNATIVE_TITLE_TYPES, AlternativeTitleTypes.class),
      Arguments.of(MappingParametersType.CALL_NUMBER_TYPES, CallNumberTypes.class),
      Arguments.of(MappingParametersType.CLASSIFICATION_TYPES, ClassificationTypes.class),
      Arguments.of(MappingParametersType.CONTRIBUTOR_NAME_TYPES, ContributorNameTypes.class),
      Arguments.of(MappingParametersType.CONTRIBUTOR_TYPES, ContributorTypes.class),
      Arguments.of(MappingParametersType.ELECTRONIC_ACCESS_RELATIONSHIPS, ElectronicAccessRelationships.class),
      Arguments.of(MappingParametersType.HOLDINGS_NOTE_TYPES, HoldingsNoteTypes.class),
      Arguments.of(MappingParametersType.HOLDINGS_TYPES, HoldingsTypes.class),
      Arguments.of(MappingParametersType.IDENTIFIER_TYPES, IdentifierTypes.class),
      Arguments.of(MappingParametersType.ILL_POLICIES, IllPolicies.class),
      Arguments.of(MappingParametersType.INSTANCE_FORMATS, InstanceFormats.class),
      Arguments.of(MappingParametersType.INSTANCE_NOTE_TYPES, InstanceNoteTypes.class),
      Arguments.of(MappingParametersType.INSTANCE_RELATIONSHIP_TYPES, InstanceRelationshipTypes.class),
      Arguments.of(MappingParametersType.INSTANCE_STATUSES, InstanceStatuses.class),
      Arguments.of(MappingParametersType.INSTANCE_TYPES, InstanceTypes.class),
      Arguments.of(MappingParametersType.ISSUANCE_MODES, IssuanceModes.class),
      Arguments.of(MappingParametersType.ITEM_DAMAGED_STATUSES, ItemDamageStatuses.class),
      Arguments.of(MappingParametersType.ITEM_NOTE_TYPES, ItemNoteTypes.class),
      Arguments.of(MappingParametersType.LOAN_TYPES, LoanTypes.class),
      Arguments.of(MappingParametersType.LOCATIONS, Locations.class),
      Arguments.of(MappingParametersType.MARC_FIELD_PROTECTION_SETTINGS, MarcFieldProtectionSettingsCollection.class),
      Arguments.of(MappingParametersType.MATERIAL_TYPES, MaterialTypes.class),
      Arguments.of(MappingParametersType.NATURE_OF_CONTENT_TERMS, NatureOfContentTerms.class),
      Arguments.of(MappingParametersType.STATISTICAL_CODE_TYPES, StatisticalCodeTypes.class),
      Arguments.of(MappingParametersType.STATISTICAL_CODES, StatisticalCodes.class));
  }

  @ParameterizedTest
  @MethodSource("testGetPathStream")
  void testGetPath(MappingParametersType type, String path) {
    assertEquals(path, type.getPath());
  }

  static Stream<Arguments> testGetPathStream() {
    return Stream.of(
      Arguments.of(MappingParametersType.ALTERNATIVE_TITLE_TYPES, "/alternative-title-types"),
      Arguments.of(MappingParametersType.CALL_NUMBER_TYPES, "/call-number-types"),
      Arguments.of(MappingParametersType.CLASSIFICATION_TYPES, "/classification-types"),
      Arguments.of(MappingParametersType.CONTRIBUTOR_NAME_TYPES, "/contributor-name-types"),
      Arguments.of(MappingParametersType.CONTRIBUTOR_TYPES, "/contributor-types"),
      Arguments.of(MappingParametersType.ELECTRONIC_ACCESS_RELATIONSHIPS, "/electronic-access-relationships"),
      Arguments.of(MappingParametersType.HOLDINGS_NOTE_TYPES, "/holdings-note-types"),
      Arguments.of(MappingParametersType.HOLDINGS_TYPES, "/holdings-types"),
      Arguments.of(MappingParametersType.IDENTIFIER_TYPES, "/identifier-types"),
      Arguments.of(MappingParametersType.ILL_POLICIES, "/ill-policies"),
      Arguments.of(MappingParametersType.INSTANCE_FORMATS, "/instance-formats"),
      Arguments.of(MappingParametersType.INSTANCE_NOTE_TYPES, "/instance-note-types"),
      Arguments.of(MappingParametersType.INSTANCE_RELATIONSHIP_TYPES, "/instance-relationship-types"),
      Arguments.of(MappingParametersType.INSTANCE_STATUSES, "/instance-statuses"),
      Arguments.of(MappingParametersType.INSTANCE_TYPES, "/instance-types"),
      Arguments.of(MappingParametersType.ISSUANCE_MODES, "/modes-of-issuance"),
      Arguments.of(MappingParametersType.ITEM_DAMAGED_STATUSES, "/item-damaged-statuses"),
      Arguments.of(MappingParametersType.ITEM_NOTE_TYPES, "/item-note-types"),
      Arguments.of(MappingParametersType.LOAN_TYPES, "/loan-types"),
      Arguments.of(MappingParametersType.LOCATIONS, "/locations"),
      Arguments.of(MappingParametersType.MARC_FIELD_PROTECTION_SETTINGS, "/field-protection-settings/marc"),
      Arguments.of(MappingParametersType.MATERIAL_TYPES, "/material-types"),
      Arguments.of(MappingParametersType.NATURE_OF_CONTENT_TERMS, "/nature-of-content-terms"),
      Arguments.of(MappingParametersType.STATISTICAL_CODE_TYPES, "/statistical-code-types"),
      Arguments.of(MappingParametersType.STATISTICAL_CODES, "/statistical-codes"));
  }

  @ParameterizedTest
  @MethodSource("testGetPathWithLimitStream")
  void testGetPathWithLimit(MappingParametersType type, String path) {
    assertEquals(path, type.getPath(LIMIT));
  }

  static Stream<Arguments> testGetPathWithLimitStream() {
    return Stream.of(
      Arguments.of(MappingParametersType.ALTERNATIVE_TITLE_TYPES, "/alternative-title-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.CALL_NUMBER_TYPES, "/call-number-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.CLASSIFICATION_TYPES, "/classification-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.CONTRIBUTOR_NAME_TYPES, "/contributor-name-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.CONTRIBUTOR_TYPES, "/contributor-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.ELECTRONIC_ACCESS_RELATIONSHIPS, "/electronic-access-relationships?limit=" + LIMIT),
      Arguments.of(MappingParametersType.HOLDINGS_NOTE_TYPES, "/holdings-note-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.HOLDINGS_TYPES, "/holdings-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.IDENTIFIER_TYPES, "/identifier-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.ILL_POLICIES, "/ill-policies?limit=" + LIMIT),
      Arguments.of(MappingParametersType.INSTANCE_FORMATS, "/instance-formats?limit=" + LIMIT),
      Arguments.of(MappingParametersType.INSTANCE_NOTE_TYPES, "/instance-note-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.INSTANCE_RELATIONSHIP_TYPES, "/instance-relationship-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.INSTANCE_STATUSES, "/instance-statuses?limit=" + LIMIT),
      Arguments.of(MappingParametersType.INSTANCE_TYPES, "/instance-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.ISSUANCE_MODES, "/modes-of-issuance?limit=" + LIMIT),
      Arguments.of(MappingParametersType.ITEM_DAMAGED_STATUSES, "/item-damaged-statuses?limit=" + LIMIT),
      Arguments.of(MappingParametersType.ITEM_NOTE_TYPES, "/item-note-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.LOAN_TYPES, "/loan-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.LOCATIONS, "/locations?limit=" + LIMIT),
      Arguments.of(MappingParametersType.MARC_FIELD_PROTECTION_SETTINGS, "/field-protection-settings/marc?limit=" + LIMIT),
      Arguments.of(MappingParametersType.MATERIAL_TYPES, "/material-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.NATURE_OF_CONTENT_TERMS, "/nature-of-content-terms?limit=" + LIMIT),
      Arguments.of(MappingParametersType.STATISTICAL_CODE_TYPES, "/statistical-code-types?limit=" + LIMIT),
      Arguments.of(MappingParametersType.STATISTICAL_CODES, "/statistical-codes?limit=" + LIMIT));
  }

  @ParameterizedTest
  @MethodSource("testFromParametersTypeStream")
  void testFromParametersType(MappingParametersType type, Class<?> parametersType, Class<Exception> exception) {
    if (Objects.nonNull(exception)) {
      assertThrows(exception, () -> {
        MappingParametersType.fromParametersType(parametersType);
      });
    } else {
      assertEquals(type, MappingParametersType.fromParametersType(parametersType));
    }
  }

  static Stream<Arguments> testFromParametersTypeStream() {
    return Stream.of(
      Arguments.of(null, Object.class, ParametersTypeLookupException.class),
      Arguments.of(MappingParametersType.ALTERNATIVE_TITLE_TYPES, AlternativeTitleType.class, null),
      Arguments.of(MappingParametersType.CALL_NUMBER_TYPES, CallNumberType.class, null),
      Arguments.of(MappingParametersType.CLASSIFICATION_TYPES, ClassificationType.class, null),
      Arguments.of(MappingParametersType.CONTRIBUTOR_NAME_TYPES, ContributorNameType.class, null),
      Arguments.of(MappingParametersType.CONTRIBUTOR_TYPES, ContributorType.class, null),
      Arguments.of(MappingParametersType.ELECTRONIC_ACCESS_RELATIONSHIPS, ElectronicAccessRelationship.class, null),
      Arguments.of(MappingParametersType.HOLDINGS_NOTE_TYPES, HoldingsNoteType.class, null),
      Arguments.of(MappingParametersType.HOLDINGS_TYPES, HoldingsType.class, null),
      Arguments.of(MappingParametersType.IDENTIFIER_TYPES, IdentifierType.class, null),
      Arguments.of(MappingParametersType.ILL_POLICIES, IllPolicy.class, null),
      Arguments.of(MappingParametersType.INSTANCE_FORMATS, InstanceFormat.class, null),
      Arguments.of(MappingParametersType.INSTANCE_NOTE_TYPES, InstanceNoteType.class, null),
      Arguments.of(MappingParametersType.INSTANCE_RELATIONSHIP_TYPES, InstanceRelationshipType.class, null),
      Arguments.of(MappingParametersType.INSTANCE_STATUSES, InstanceStatus.class, null),
      Arguments.of(MappingParametersType.INSTANCE_TYPES, InstanceType.class, null),
      Arguments.of(MappingParametersType.ISSUANCE_MODES, IssuanceMode.class, null),
      Arguments.of(MappingParametersType.ITEM_DAMAGED_STATUSES, ItemDamageStatus.class, null),
      Arguments.of(MappingParametersType.ITEM_NOTE_TYPES, ItemNoteType.class, null),
      Arguments.of(MappingParametersType.LOAN_TYPES, LoanType.class, null),
      Arguments.of(MappingParametersType.LOCATIONS, Location.class, null),
      Arguments.of(MappingParametersType.MARC_FIELD_PROTECTION_SETTINGS, MarcFieldProtectionSetting.class, null),
      Arguments.of(MappingParametersType.MATERIAL_TYPES, MaterialType.class, null),
      Arguments.of(MappingParametersType.NATURE_OF_CONTENT_TERMS, NatureOfContentTerm.class, null),
      Arguments.of(MappingParametersType.STATISTICAL_CODE_TYPES, StatisticalCodeType.class, null),
      Arguments.of(MappingParametersType.STATISTICAL_CODES, StatisticalCode.class, null));
  }

}
