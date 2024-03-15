package org.folio.rest.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.folio.rest.workflow.model.EmbeddedVariable;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class AbstractWorkflowIODelegate extends AbstractWorkflowInputDelegate implements Output {

  private Expression outputVariable;

  public AbstractWorkflowIODelegate() {
    super();
  }

  public EmbeddedVariable getOutputVariable(DelegateExecution execution) throws JsonProcessingException {
    return objectMapper.readValue(outputVariable.getValue(execution).toString(), EmbeddedVariable.class);
  }

  public void setOutputVariable(Expression outputVariable) {
    this.outputVariable = outputVariable;
  }

}
