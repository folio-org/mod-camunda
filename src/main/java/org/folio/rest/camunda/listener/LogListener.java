package org.folio.rest.camunda.listener;

import org.folio.rest.camunda.provider.LoggerProvider;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * For providing a system log message for when the listener gets executed.
 */
@Service
@Scope("prototype")
public class LogListener implements ExecutionListener {

  private final Logger logger;

  /**
   * Initializer.
   *
   * This initializes logger this way so that unit tests are easier to write.
   *
   * @param loggerProvider The logger provider used to initialize the logger.
   */
  public LogListener(LoggerProvider loggerProvider) {

    this.logger = loggerProvider.forClass(LogListener.class);
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception {

    final String activityId = execution.getCurrentActivityId();
    final String activityName = execution.getCurrentActivityName();
    final String processId = execution.getProcessDefinitionId();
    final String processName = execution.getProcessEngineServices()
      .getRepositoryService()
      .createProcessDefinitionQuery()
      .processDefinitionId(processId)
      .singleResult()
      .getName();

    logger.info("Workflow '{}', Definition ID '{}', Activity '{}' ({}).", processName, processId, activityName, activityId);
  }

}
