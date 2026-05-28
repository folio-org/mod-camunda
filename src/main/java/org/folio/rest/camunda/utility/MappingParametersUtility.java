package org.folio.rest.camunda.utility;

import static java.lang.String.format;

import org.folio.MarcFieldProtectionSettingsCollection;
import org.folio.processing.mapping.defaultmapper.processor.parameters.MappingParameters;
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
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

/**
 * Utility class for managing and retrieving mapping parameters in a library
 * management system.
 *
 * This utility provides centralized methods for fetching configuration data and
 * mapping rules for various library-specific entities. It supports retrieving
 * comprehensive parameters for:
 * <ul>
 *   <li>Alternative title types</li>
 *   <li>Contributor types</li>
 *   <li>Instance formats</li>
 *   <li>And many other library-related type configurations</li>
 * </ul>
 *
 * The class utilizes an {@link OkapiRestTemplate} to dynamically fetch
 * parameters from predefined REST end points, supporting a flexible and
 * extensible approach to parameter management.
 *
 * Key features:
 * <ul>
 *   <li>Generic parameter retrieval for different entity types</li>
 *   <li>Centralized mapping rules fetching</li>
 *   <li>Comprehensive parameter collection initialization</li>
 * </ul>
 *
 * @see MappingParameters
 * @see MappingParametersType
 * @see OkapiRestTemplate
 */
public class MappingParametersUtility {

  /**
   * Maximum limit for fetching parameters from end points.
   * This constant ensures that parameter retrieval does not exceed 1000 items,
   * preventing excessive data transfer and potential performance issues.
   */
  public static final int LIMIT = 1000;

  /**
   * Base path for mapping rules related to MARC bibliographic records.
   * Used as a constant end point for retrieving mapping configuration specific to
   * MARC bibliographic records.
   */
  @SuppressWarnings("java:S1075") // SonarQube path false positive.
  static final String MAPPING_RULES_PATH = "/mapping-rules/marc-bib";

  private MappingParametersUtility() {

  }

  /**
   * Fetches mapping rules from a predefined REST end point for MARC bibliographic
   * records.
   *
   * This method retrieves the mapping rules using the provided REST template,
   * handling potential empty responses gracefully.
   *
   * @param restTemplate The REST template used to make the HTTP request
   *                     (must not be null)
   * @return A {@link JsonNode} containing the mapping rules, or NULL on empty body.
   */
  public static JsonNode fetchRules(@NonNull OkapiRestTemplate restTemplate) {
    ResponseEntity<JsonNode> response = restTemplate.getForEntity(MAPPING_RULES_PATH, JsonNode.class);

    return response.hasBody()
      ? response.getBody()
      : null;
  }

  /**
   * Retrieves comprehensive mapping parameters for various library entity types.
   *
   * This method performs a bulk retrieval of parameters for multiple
   * library-specific entity types, including:
   * <ul>
   *   <li>Alternative title types</li>
   *   <li>Contributor types</li>
   *   <li>Instance formats</li>
   *   <li>Holdings types</li>
   *   <li>And many more library configuration parameters</li>
   * </ul>
   *
   * The method uses the provided REST template to fetch parameters from different
   * end points and constructs a fully initialized {@link MappingParameters}
   * object.
   *
   * @param restTemplate Template for fetching parameters from different end points.
   * @return A fully initialized {@link MappingParameters} object containing all
   *         retrieved parameters
   */
  public static MappingParameters getMappingParamaters(OkapiRestTemplate restTemplate) {
    final MappingParameters map = new MappingParameters()
      .withInitializedState(true);

    final AlternativeTitleTypes alternativeTitleTypes = getParameters(restTemplate, AlternativeTitleType.class);
    if (alternativeTitleTypes != null) {
      map.setAlternativeTitleTypes(alternativeTitleTypes.getAlternativeTitleTypes());
    }

    final CallNumberTypes callNumberTypes = getParameters(restTemplate, CallNumberType.class);
    if (callNumberTypes != null) {
      map.setCallNumberTypes(callNumberTypes.getCallNumberTypes());
    }

    final ClassificationTypes classificationTypes = getParameters(restTemplate, ClassificationType.class);
    if (classificationTypes != null) {
      map.setClassificationTypes(classificationTypes.getClassificationTypes());
    }

    final ContributorNameTypes contributorNameTypes = getParameters(restTemplate, ContributorNameType.class);
    if (contributorNameTypes != null) {
      map.setContributorNameTypes(contributorNameTypes.getContributorNameTypes());
    }

    final ContributorTypes contributorTypes = getParameters(restTemplate, ContributorType.class);
    if (contributorTypes != null) {
      map.setContributorTypes(contributorTypes.getContributorTypes());
    }

    final ElectronicAccessRelationships electronicAccessRelationships = getParameters(restTemplate, ElectronicAccessRelationship.class);
    if (electronicAccessRelationships != null) {
      map.setElectronicAccessRelationships(electronicAccessRelationships.getElectronicAccessRelationships());
    }

    final HoldingsNoteTypes holdingsNoteTypes = getParameters(restTemplate, HoldingsNoteType.class);
    if (holdingsNoteTypes != null) {
      map.setHoldingsNoteTypes(holdingsNoteTypes.getHoldingsNoteTypes());
    }

    final HoldingsTypes holdingsTypes = getParameters(restTemplate, HoldingsType.class);
    if (holdingsTypes != null) {
      map.setHoldingsTypes(holdingsTypes.getHoldingsTypes());
    }

    final IdentifierTypes identifierTypes = getParameters(restTemplate, IdentifierType.class);
    if (identifierTypes != null) {
      map.setIdentifierTypes(identifierTypes.getIdentifierTypes());
    }

    final IllPolicies illPolicies = getParameters(restTemplate, IllPolicy.class);
    if (illPolicies != null) {
      map.setIllPolicies(illPolicies.getIllPolicies());
    }

    final InstanceFormats instanceFormats = getParameters(restTemplate, InstanceFormat.class);
    if (instanceFormats != null) {
      map.setInstanceFormats(instanceFormats.getInstanceFormats());
    }

    final InstanceNoteTypes instanceNoteTypes = getParameters(restTemplate, InstanceNoteType.class);
    if (instanceNoteTypes != null) {
      map.setInstanceNoteTypes(instanceNoteTypes.getInstanceNoteTypes());
    }

    final InstanceRelationshipTypes instanceRelationshipTypes = getParameters(restTemplate, InstanceRelationshipType.class);
    if (instanceRelationshipTypes != null) {
      map.setInstanceRelationshipTypes(instanceRelationshipTypes.getInstanceRelationshipTypes());
    }

    final InstanceStatuses instanceStatuses = getParameters(restTemplate, InstanceStatus.class);
    if (instanceStatuses != null) {
      map.setInstanceStatuses(instanceStatuses.getInstanceStatuses());
    }

    final InstanceTypes instanceTypes = getParameters(restTemplate, InstanceType.class);
    if (instanceTypes != null) {
      map.setInstanceTypes(instanceTypes.getInstanceTypes());
    }

    final IssuanceModes issuanceModes = getParameters(restTemplate, IssuanceMode.class);
    if (issuanceModes != null) {
      map.setIssuanceModes(issuanceModes.getIssuanceModes());
    }

    final ItemDamageStatuses itemDamageStatuses = getParameters(restTemplate, ItemDamageStatus.class);
    if (itemDamageStatuses != null) {
      map.setItemDamageStatuses(itemDamageStatuses.getItemDamageStatuses());
    }

    final ItemNoteTypes itemNoteTypes = getParameters(restTemplate, ItemNoteType.class);
    if (itemNoteTypes != null) {
      map.setItemNoteTypes(itemNoteTypes.getItemNoteTypes());
    }

    final LoanTypes loanTypes = getParameters(restTemplate, LoanType.class);
    if (loanTypes != null) {
      map.setLoanTypes(loanTypes.getLoantypes());
    }

    final Locations locations = getParameters(restTemplate, Location.class);
    if (locations != null) {
      map.setLocations(locations.getLocations());
    }

    final MarcFieldProtectionSettingsCollection marcFieldProtectionSettings = getParameters(restTemplate, MarcFieldProtectionSetting.class);
    if (marcFieldProtectionSettings != null) {
      map.setMarcFieldProtectionSettings(marcFieldProtectionSettings.getMarcFieldProtectionSettings());
    }

    final MaterialTypes materialTypes = getParameters(restTemplate, MaterialType.class);
    if (materialTypes != null) {
      map.setMaterialTypes(materialTypes.getMtypes());
    }

    final NatureOfContentTerms natureOfContentTerms = getParameters(restTemplate, NatureOfContentTerm.class);
    if (natureOfContentTerms != null) {
      map.setNatureOfContentTerms(natureOfContentTerms.getNatureOfContentTerms());
    }

    final StatisticalCodes statisticalCodes = getParameters(restTemplate, StatisticalCode.class);
    if (statisticalCodes != null) {
      map.setStatisticalCodes(statisticalCodes.getStatisticalCodes());
    }

    final StatisticalCodeTypes statisticalCodeTypes = getParameters(restTemplate, StatisticalCodeType.class);
    if (statisticalCodeTypes != null) {
      map.setStatisticalCodeTypes(statisticalCodeTypes.getStatisticalCodeTypes());
    }

    return map;
  }

  /**
   * Generic method to retrieve parameters for a specific library entity type.
   *
   * This method dynamically fetches parameters based on the provided parameter
   * type, using a pre-configured mapping of parameter types to their respective
   * end points and collection types.
   *
   * Key features:
   * <ul>
   * <li>Supports retrieval of various library entity type parameters</li>
   * <li>Dynamically determines the appropriate end point and collection type</li>
   * <li>Handles potential retrieval failures</li>
   * </ul>
   *
   * @param <C>            The type of the collection or parameters to be returned
   * @param <P>            The type of the parameter class
   * @param restTemplate   The REST template used to fetch parameters
   * @param parametersType The class of the parameters to retrieve
   *
   * @return Parameters of the specified type
   *
   * @throws ParametersTypeLookupException if no descriptor is found for the given
   *                                       parameters type
   * @throws ParametersRetrievalException  if parameters cannot be retrieved
   * @throws RestClientException if there's an error communicating
   */
  public static <C, P> C getParameters(OkapiRestTemplate restTemplate, Class<P> parametersType) {
    MappingParametersType type = MappingParametersType.fromParametersType(parametersType);
    String path = type.getPath(LIMIT);

    Class<C> collectionType = type.getCollectionType();
    ResponseEntity<C> response = restTemplate.getForEntity(path, collectionType);

    if (!response.hasBody()) {
      throw new ParametersRetrievalException(type);
    }

    return response.getBody();
  }

  /**
   * Custom exception indicating a failure in retrieving library configuration
   * parameters.
   *
   * @see RuntimeException
   * @see MappingParametersType
   */
  private static class ParametersRetrievalException extends RuntimeException {

    private static final long serialVersionUID = 717934762826254910L;

    /**
     * Template for generating detailed error messages about parameter retrieval
     * failures.
     * Includes place holders for the parameter type and the end point path.
     */
    private static final String MESSAGE_TEMPLATE = "Failed to retrieve %s parameters from path %s";

    /**
     * Constructs a new {@code ParametersRetrievalException} with a detailed error
     * message based on the specific mapping parameters type that failed to
     * retrieve.
     *
     * @param type The {@link MappingParametersType} that failed to retrieve
     *             parameters
     */
    public ParametersRetrievalException(MappingParametersType type) {
      super(format(MESSAGE_TEMPLATE, type.getCollectionType().getSimpleName(), type.getPath(LIMIT)));
    }

  }

}
