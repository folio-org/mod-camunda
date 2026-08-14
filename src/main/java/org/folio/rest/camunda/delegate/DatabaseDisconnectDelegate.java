package org.folio.rest.camunda.delegate;

import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.workflow.model.DatabaseDisconnectTask;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Database disconnect delegate.
 */
@Service
@Scope("prototype")
public class DatabaseDisconnectDelegate extends AbstractDatabaseDelegate {

  /**
   * Initializer.
   */
  public DatabaseDisconnectDelegate(JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService) {

    super(mapper, runtimeService, connectionService);
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

    String key = this.designation.getValue(execution).toString();

    try {
      connectionService.destroyConnection(key);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

  @Override
  public Class<?> fromTask() {
    return DatabaseDisconnectTask.class;
  }

}
