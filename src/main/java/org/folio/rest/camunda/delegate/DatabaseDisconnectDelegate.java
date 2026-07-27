package org.folio.rest.camunda.delegate;

import org.folio.rest.workflow.model.DatabaseDisconnectTask;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Database disconnect delegate.
 */
@Service
@Scope("prototype")
public class DatabaseDisconnectDelegate extends AbstractDatabaseDelegate {

  /**
   * Perform the delegate execution.
   *
   * @param execution The delegate execution data.
   * @param name      The delegate name.
   *
   * @throws Exception On any error.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name) throws Exception {

    String key = this.designation.getValue(execution).toString();

    connectionService.destroyConnection(key);
  }

  @Override
  public Class<?> fromTask() {
    return DatabaseDisconnectTask.class;
  }

}
