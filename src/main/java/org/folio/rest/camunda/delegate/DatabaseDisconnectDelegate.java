package org.folio.rest.camunda.delegate;

import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.folio.rest.workflow.model.DatabaseDisconnectTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Database disconnect delegate.
 */
@Service
@Scope("prototype")
public class DatabaseDisconnectDelegate extends AbstractDatabaseDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    final long startTime = determineStartTime(execution);

    String key = this.designation.getValue(execution).toString();

    connectionService.destroyConnection(key);

    determineEndTime(execution, startTime);
  }

  @Override
  public Class<?> fromTask() {
    return DatabaseDisconnectTask.class;
  }

}
