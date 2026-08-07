package org.folio.rest.camunda.delegate;

import static org.operaton.spin.Spin.JSON;

import org.folio.rest.workflow.enums.VariableType;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Output type.
 */
public interface Output {

  public abstract Logger getLogger();

  public abstract JsonMapper getMapper();

  public abstract EmbeddedVariable getOutputVariable(DelegateExecution execution) throws JacksonException;

  public abstract void setOutputVariable(Expression outputVariable);

  public abstract boolean hasOutputVariable(DelegateExecution execution);

  public default void setOutput(DelegateExecution execution, Object output) throws JacksonException {
    if (!hasOutputVariable(execution)) {
      getLogger().warn("Output variable for execution {} is null", execution.getId());
      return;
    }

    EmbeddedVariable variable = getOutputVariable(execution);
    String key = variable.getKey();

    if (key == null) {
      getLogger().warn("Output key is null");
      return;
    }

    if (output == null) {
       getLogger().warn("Output not present for {}", key);
      return;
    }

    VariableType type = variable.getType();
    Object value = Boolean.TRUE.equals(variable.getSpin())
      ? JSON(getMapper().writeValueAsString(output))
      : Variables.objectValue(output, variable.getAsTransient()).create();

    if (type == VariableType.LOCAL) {
      execution.setVariableLocal(key, value);
    } else if (type == VariableType.PROCESS) {
      execution.setVariable(key, value);
    } else {
      getLogger().warn("Variable type not present for {}", key);
    }
  }

}
