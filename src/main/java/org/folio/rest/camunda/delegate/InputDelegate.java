package org.folio.rest.camunda.delegate;

import org.folio.rest.workflow.model.InputTask;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class InputDelegate extends AbstractWorkflowIODelegate {

  @Value("${okapi.url}")
  private String okapiUrl;

  /**
   * Initializer.
   */
  public InputDelegate() {
    // Should be empty.
  }

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
    // Should be empty.
  }

  @Override
  public Class<?> fromTask() {
    return InputTask.class;
  }

}
