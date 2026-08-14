package org.folio.rest.camunda.delegate;

import java.util.Objects;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract workflow I/O delegate.
 */
public abstract class AbstractWorkflowIODelegate extends AbstractWorkflowInputDelegate implements Output {

  private Expression outputVariable;

  /**
   * Initializer.
   */
  AbstractWorkflowIODelegate(JsonMapper mapper, RuntimeService runtimeService) {

    super(mapper, runtimeService);
  }

  public EmbeddedVariable getOutputVariable(DelegateExecution execution) throws JacksonException {
    return mapper.readValue(outputVariable.getValue(execution).toString(), EmbeddedVariable.class);
  }

  public boolean hasOutputVariable(DelegateExecution execution) {
    return Objects.nonNull(outputVariable) && Objects.nonNull(outputVariable.getValue(execution));
  }

  public void setOutputVariable(Expression outputVariable) {
    this.outputVariable = outputVariable;
  }

}
