package org.folio.rest.camunda.delegate;

import java.util.Objects;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.folio.rest.workflow.model.EmbeddedVariable;
import tools.jackson.core.JacksonException;

/**
 * Abstract workflow output delegate.
 */
public abstract class AbstractWorkflowOutputDelegate extends AbstractWorkflowDelegate implements Output {

  private Expression outputVariable;

  protected AbstractWorkflowOutputDelegate() {
    super();
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
