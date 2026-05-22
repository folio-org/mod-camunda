package org.folio.rest.camunda.delegate;

import java.util.Objects;
import java.util.Set;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.folio.rest.workflow.model.EmbeddedVariable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

/**
 * Abstract workflow input delegate.
 */
public abstract class AbstractWorkflowInputDelegate extends AbstractWorkflowDelegate implements Input {

  private Expression inputVariables;

  protected AbstractWorkflowInputDelegate() {
    super();
  }

  public Set<EmbeddedVariable> getInputVariables(DelegateExecution execution) throws JacksonException {
    return getObjectMapper().readValue(inputVariables.getValue(execution).toString(),
        new TypeReference<Set<EmbeddedVariable>>() {});
  }

  public boolean hasInputVariables(DelegateExecution execution) {
    return Objects.nonNull(inputVariables) && Objects.nonNull(inputVariables.getValue(execution));
  }

  public void setInputVariables(Expression inputVariables) {
    this.inputVariables = inputVariables;
  }

}
