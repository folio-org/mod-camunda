package org.folio.rest.camunda.delegate;

import java.util.Objects;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract database I/O delegate.
 */
public abstract class AbstractDatabaseIODelegate extends AbstractWorkflowInputDelegate implements Output {

  Expression designation;

  private Expression outputVariable;

  DatabaseConnectionService connectionService;

  /**
   * Initializer.
   */
  AbstractDatabaseIODelegate(JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService) {

    super(mapper, runtimeService);

    this.connectionService = connectionService;
  }

  public void setDesignation(Expression designation) {
    this.designation = designation;
  }

  public boolean hasOutputVariable(DelegateExecution execution) {
    return Objects.nonNull(outputVariable) && Objects.nonNull(outputVariable.getValue(execution));
  }

  public EmbeddedVariable getOutputVariable(DelegateExecution execution) throws JacksonException {
    return mapper.readValue(outputVariable.getValue(execution).toString(), EmbeddedVariable.class);
  }

  public void setOutputVariable(Expression outputVariable) {
    this.outputVariable = outputVariable;
  }

}
