package org.folio.rest.camunda.delegate;

import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.JacksonException;

/**
 * Abstract database I/O delegate.
 */
public abstract class AbstractDatabaseIODelegate extends AbstractWorkflowInputDelegate implements Output {

  Expression designation;

  private Expression outputVariable;

  @Autowired
  DatabaseConnectionService connectionService;

  public void setDesignation(Expression designation) {
    this.designation = designation;
  }

  public boolean hasOutputVariable(DelegateExecution execution) {
    return Objects.nonNull(outputVariable) && Objects.nonNull(outputVariable.getValue(execution));
  }

  public EmbeddedVariable getOutputVariable(DelegateExecution execution) throws JacksonException {
    return objectMapper.readValue(outputVariable.getValue(execution).toString(), EmbeddedVariable.class);
  }

  public void setOutputVariable(Expression outputVariable) {
    this.outputVariable = outputVariable;
  }

}
