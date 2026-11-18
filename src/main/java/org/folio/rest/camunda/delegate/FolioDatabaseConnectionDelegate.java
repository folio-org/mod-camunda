package org.folio.rest.camunda.delegate;

import java.util.Properties;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.workflow.model.FolioDatabaseConnectionTask;
import org.folio.spring.tenant.service.SchemaProvisionerService;
import org.folio.spring.tenant.service.SchemaService;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Database connection delegate specific to FOLIO connections.
 *
 * This brings in more specialized and automatic functionality, such as schema per tenant.
 */
@Service
@Scope("prototype")
public class FolioDatabaseConnectionDelegate extends DatabaseConnectionDelegate {

  private final SchemaProvisionerService schemaProvisionerService;

  /**
   * Initializer.
   *
   * @param mapper            The JSON mapper.
   * @param runtimeService    The run time service.
   * @param connectionService The connection service.
   * @param schemaService     The schema service.
   */
  public FolioDatabaseConnectionDelegate(
    JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService, SchemaService schemaService,
    SchemaProvisionerService schemaProvisionerService
  ) {

    super(mapper, runtimeService, connectionService, schemaService);

    this.schemaProvisionerService = schemaProvisionerService;
  }

  /**
   * Get the task class associated with this delegate.
   *
   * @param The class task.
   */
  @Override
  public Class<?> fromTask() {

    return FolioDatabaseConnectionTask.class;
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    final String tenant = execution.getTenantId();
    final String schema = schemaService.getSchema(tenant);

    schemaProvisionerService.ensureProvisionedSchema(schema, tenant);

    final Object searchPathExpr = this.searchPath.getValue(execution);

    final String searchPathString = searchPathExpr == null
      ? ""
      : searchPathExpr.toString();

    final String searchPathValue = searchPathString.isBlank()
      ? schema + ", public"
      : searchPathString;

    final Properties properties = buildProperties(execution, searchPathValue);

    try {
      connectionService.createPool(true, properties);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

}
