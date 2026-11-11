package org.folio.rest.camunda.delegate;

import java.util.Properties;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.camunda.service.TenantProvisioningService;
import org.folio.rest.workflow.model.FolioDatabaseConnectionTask;
import org.folio.spring.tenant.service.SchemaService;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Database connection delegate.
 */
@Service
@Scope("prototype")
public class FolioDatabaseConnectionDelegate extends AbstractDatabaseDelegate {

  private Expression url;

  private Expression username;

  private Expression password;

  private Expression searchPath;

  private final SchemaService schemaService;

  private final TenantProvisioningService tenantProvisioningService;

  /**
   * Initializer.
   */
  public FolioDatabaseConnectionDelegate(JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService, SchemaService schemaService, TenantProvisioningService tenantProvisioningService) {

    super(mapper, runtimeService, connectionService);

    this.schemaService = schemaService;
    this.tenantProvisioningService = tenantProvisioningService;
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

    final String usernameExp = this.username.getValue(execution).toString();
    final String passwordExp = this.password.getValue(execution).toString();
    final String urlValue = this.url.getValue(execution).toString();
    final String key = this.designation.getValue(execution).toString();
    final String searchPathExpr = this.searchPath.getValue(execution).toString();
    final String tenant = execution.getTenantId();

    final String searchPathValue = searchPathExpr == null || searchPathExpr.trim().isEmpty()
      ? schemaService.getSchema(tenant) + ", public"
      : searchPathExpr;

    Properties info = new Properties();
    info.setProperty("user", usernameExp);
    info.setProperty("password", passwordExp);
    info.setProperty("tenant", tenant);
    info.setProperty("searchPath", searchPathValue);

    try {
      tenantProvisioningService.ensureSchema(tenant);
      connectionService.createPool(key, urlValue, info);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

  public void setUrl(Expression url) {
    this.url = url;
  }

  public void setUsername(Expression username) {
    this.username = username;
  }

  public void setPassword(Expression password) {
    this.password = password;
  }

  public void setSearchPath(Expression searchPath) {
    this.searchPath = searchPath;
  }

  @Override
  public Class<?> fromTask() {
    return FolioDatabaseConnectionTask.class;
  }

}
