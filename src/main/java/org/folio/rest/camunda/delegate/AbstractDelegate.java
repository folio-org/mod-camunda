package org.folio.rest.camunda.delegate;

import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.JavaDelegate;
import org.operaton.bpm.model.bpmn.instance.FlowElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract delegate.
 */
public abstract class AbstractDelegate implements JavaDelegate {

  private final Logger log;

  @Autowired
  protected JsonMapper mapper;

  /**
   * Initializer.
   */
  AbstractDelegate() {
    // The logger is non-static to ensure that the implementing class name is used for the logger.
    log = LoggerFactory.getLogger(this.getClass());
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   *
   * @throws Exception on error.
   */
  @Override
  public void execute(DelegateExecution execution) throws Exception {

    final FlowElement flow = execution.getBpmnModelElementInstance();
    final String name = flow.getName();
    final String id = flow.getId();
    final long startTime = determineStartTime(execution, name);

    try {
      performExecute(execution, name, id);
      determineEndTime(execution, startTime, name, false);
    } catch (Exception e) {
      determineEndTime(execution, startTime, name, true);

      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

  /**
   * Perform the execution operation with class specific details.
   *
   * This is intended to be called by the execute() method and should not need to be directly called.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  protected abstract void performExecute(DelegateExecution execution, String name, String id);

  /**
   * Get the delegate class name.
   *
   * @return The delegate name.
   */
  protected String getDelegateClass() {
    String simpleName = getClass().getSimpleName();

    return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
  }

  /**
   * Get the expression.
   *
   * @return The formatted expression string.
   */
  public String getExpression() {
    return String.format("${%s}", getDelegateClass());
  }

  /**
   * Get the logger.
   *
   * @return The logger for this class.
   */
  public Logger getLogger() {
    return log;
  }

  /**
   * Get the JSON Mapper.
   *
   * @return The JSON Mapper for this class.
   */
  public JsonMapper getMapper() {
    return mapper;
  }

  /**
   * Determine the start time of the query and print log.
   *
   * @param execution The delegate execution data.
   * @param name      The delegate name.
   * @param args      Additional string arguments to pass to the logger.
   *
   * @return The start time.
   */
  protected long determineStartTime(DelegateExecution execution, String name, Object ...args) {

    if (args == null) {
      getLogger().info("{} started", name);
    } else {
      getLogger().info("{} {} started", name, args);
    }

    return System.nanoTime();
  }

  /**
   * Given the start time, determine the total time spent.
   *
   * @param execution The delegate execution data.
   * @param startTime The time the process started.
   * @param name      The delegate name.
   * @param failed    If TRUE, then this is failed, otherwise this is success.
   */
  protected void determineEndTime(DelegateExecution execution, long startTime, String name, boolean failed) {

    getLogger().info("{} {} in {} milliseconds", name, failed ? "failed" : "finished", (System.nanoTime() - startTime) / (double) 1000000);
  }

}
