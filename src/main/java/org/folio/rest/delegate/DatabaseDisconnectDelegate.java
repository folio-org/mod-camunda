package org.folio.rest.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.folio.rest.workflow.model.DatabaseDisconnectTask;

public class DatabaseDisconnectDelegate extends AbstractDatabaseDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    long startTime = System.nanoTime();
    FlowElement bpmnModelElement = execution.getBpmnModelElementInstance();
    String delegateName = bpmnModelElement.getName();

    logger.info("{} started", delegateName);

    String identifier = this.identifier.getValue(execution).toString();

    connectionService.destroyConnection(identifier);

    long endTime = System.nanoTime();
    logger.info("{} finished in {} milliseconds", delegateName, (endTime - startTime) / (double) 1000000);
  }

  @Override
  public Class<?> fromTask() {
    return DatabaseDisconnectTask.class;
  }

}
