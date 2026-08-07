package org.folio.rest.camunda.utility;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.folio.MarcFieldProtectionSettingsCollection;
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

/**
 * Represents different types of mapping parameters used in the FOLIO library
 * management system.
 *
 * This enum defines various configuration and metadata types that can be used
 * during record mapping and transformation processes. Each enum constant is
 * associated with:
 * <ul>
 *   <li>A specific parameter type class</li>
 *   <li>A corresponding collection type class</li>
 *   <li>A RESTful API end point path</li>
 * </ul>
 *
 * The enum provides methods to retrieve the parameter type, collection type,
 * and API path for each mapping parameter type.
 */
public enum MappingParametersType {

  /** Mapping type for alternative title types. */
  ALTERNATIVE_TITLE_TYPES(AlternativeTitleType.class, AlternativeTitleTypes.class, "/alternative-title-types"),

  /** Mapping type for call number types. */
  CALL_NUMBER_TYPES(CallNumberType.class, CallNumberTypes.class, "/call-number-types"),

  /** Mapping type for classification types. */
  CLASSIFICATION_TYPES(ClassificationType.class, ClassificationTypes.class, "/classification-types"),

  /** Mapping type for contributor name types. */
  CONTRIBUTOR_NAME_TYPES(ContributorNameType.class, ContributorNameTypes.class, "/contributor-name-types"),

  /** Mapping type for contributor types. */
  CONTRIBUTOR_TYPES(ContributorType.class, ContributorTypes.class, "/contributor-types"),

  /** Mapping type for electronic access relationships. */
  ELECTRONIC_ACCESS_RELATIONSHIPS(ElectronicAccessRelationship.class, ElectronicAccessRelationships.class, "/electronic-access-relationships"),

  /** Mapping type for holdings note types. */
  HOLDINGS_NOTE_TYPES(HoldingsNoteType.class, HoldingsNoteTypes.class, "/holdings-note-types"),

  /** Mapping type for holdings types. */
  HOLDINGS_TYPES(HoldingsType.class, HoldingsTypes.class, "/holdings-types"),

  /** Mapping type for identifier types. */
  IDENTIFIER_TYPES(IdentifierType.class, IdentifierTypes.class, "/identifier-types"),

  /** Mapping type for Inter-library Loan (ILL) policies. */
  ILL_POLICIES(IllPolicy.class, IllPolicies.class, "/ill-policies"),

  /** Mapping type for instance formats. */
  INSTANCE_FORMATS(InstanceFormat.class, InstanceFormats.class, "/instance-formats"),

  /** Mapping type for instance note types. */
  INSTANCE_NOTE_TYPES(InstanceNoteType.class, InstanceNoteTypes.class, "/instance-note-types"),

  /** Mapping type for instance relationship types. */
  INSTANCE_RELATIONSHIP_TYPES(InstanceRelationshipType.class, InstanceRelationshipTypes.class, "/instance-relationship-types"),

  /** Mapping type for instance statuses. */
  INSTANCE_STATUSES(InstanceStatus.class, InstanceStatuses.class, "/instance-statuses"),

  /** Mapping type for instance types. */
  INSTANCE_TYPES(InstanceType.class, InstanceTypes.class, "/instance-types"),

  /** Mapping type for issuance modes. */
  ISSUANCE_MODES(IssuanceMode.class, IssuanceModes.class, "/modes-of-issuance"),

  /** Mapping type for item damaged statuses. */
  ITEM_DAMAGED_STATUSES(ItemDamageStatus.class, ItemDamageStatuses.class, "/item-damaged-statuses"),

  /** Mapping type for item note types. */
  ITEM_NOTE_TYPES(ItemNoteType.class, ItemNoteTypes.class, "/item-note-types"),

  /** Mapping type for loan types. */
  LOAN_TYPES(LoanType.class, LoanTypes.class, "/loan-types"),

  /** Mapping type for locations. */
  LOCATIONS(Location.class, Locations.class, "/locations"),

  /** Mapping type for MARC field protection settings. */
  MARC_FIELD_PROTECTION_SETTINGS(MarcFieldProtectionSetting.class, MarcFieldProtectionSettingsCollection.class, "/field-protection-settings/marc"),

  /** Mapping type for material types. */
  MATERIAL_TYPES(MaterialType.class, MaterialTypes.class, "/material-types"),

  /** Mapping type for nature of content terms. */
  NATURE_OF_CONTENT_TERMS(NatureOfContentTerm.class, NatureOfContentTerms.class, "/nature-of-content-terms"),

  /** Mapping type for statistical code types. */
  STATISTICAL_CODE_TYPES(StatisticalCodeType.class, StatisticalCodeTypes.class, "/statistical-code-types"),

  /** Mapping type for statistical codes. */
  STATISTICAL_CODES(StatisticalCode.class, StatisticalCodes.class, "/statistical-codes");

  /** The specific parameter type class for this mapping type. */
  private final Class<?> parametersType;

  /** The collection type class corresponding to this mapping type. */
  private final Class<?> collectionType;

  /** The API end point path for retrieving this mapping type's data. */
  private final String path;

  private static final Map<Class<?>, MappingParametersType> BY_PARAMETERS_TYPE = Arrays.stream(values())
      .collect(Collectors.toMap(MappingParametersType::getParametersType, Function.identity()));

  /**
   * Constructs a MappingParametersType with the specified parameter type,
   * collection type, and API path.
   *
   * @param parametersType The class representing the specific parameter type
   * @param collectionType The class representing the collection of this parameter
   *                       type
   * @param path           The API end point path for retrieving this mapping
   *                       type's data
   */
  MappingParametersType(Class<?> parametersType, Class<?> collectionType, String path) {
    this.parametersType = parametersType;
    this.collectionType = collectionType;
    this.path = path;
  }

  /**
   * Retrieves the parameter type class for this mapping type.
   *
   * @return The class representing the specific parameter type
   */
  public Class<?> getParametersType() {
    return parametersType;
  }

  /**
   * Retrieves the collection type class for this mapping type.
   *
   * @return The class representing the collection of this parameter type
   */
  @SuppressWarnings("unchecked")
  public <C> Class<C> getCollectionType() {
    return (Class<C>) collectionType;
  }

  /**
   * Retrieves the base API end point path for this mapping type.
   *
   * @return The API end point path
   */
  public String getPath() {
    return path;
  }

  /**
   * Retrieves the API end point path with a specified limit parameter.
   *
   * @param limit The maximum number of records to retrieve
   * @return The API end point path with limit query parameter
   */
  public String getPath(int limit) {
    return path + "?limit=" + limit;
  }

  /**
   * Retrieves the MappingParametersType associated with the given parameters type
   * class.
   *
   * @param parametersType The class representing the parameters type to look up
   * @return The corresponding MappingParametersType for the given class
   * @throws ParametersTypeLookupException if no MappingParametersType is found
   *                                       for the provided class
   */
  public static MappingParametersType fromParametersType(Class<?> parametersType) {
    if (!BY_PARAMETERS_TYPE.containsKey(parametersType)) {
      throw new ParametersTypeLookupException(parametersType);
    }

    return BY_PARAMETERS_TYPE.get(parametersType);
  }

  /**
   * Custom exception indicating a failure in looking up parameters type.
   *
   * @see RuntimeException
   */
  protected static class ParametersTypeLookupException extends RuntimeException {

    private static final long serialVersionUID = -9152302641261345898L;

    /**
     * Template for generating detailed error messages about parameters type lookup
     * failures.
     * Includes place holders for the parameter type and the end point path.
     */
    private static final String MESSAGE_TEMPLATE = "Invalid parameters type:  %s";

    /**
     * Constructs a new {@code ParametersTypeLookupException} with a detailed error
     * message based on the specific mapping parameters type that failed to
     * retrieve.
     *
     * @param parametersType The {@link Class} that failed to retrieve parameters
     */
    public ParametersTypeLookupException(Class<?> parametersType) {
      super(format(MESSAGE_TEMPLATE, parametersType));
    }

  }

}
