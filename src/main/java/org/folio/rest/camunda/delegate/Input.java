package org.folio.rest.camunda.delegate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.rest.workflow.enums.VariableType;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.spin.impl.json.jackson.JacksonJsonNode;
import org.slf4j.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Input type.
 */
public interface Input {

  public abstract Logger getLogger();

  public abstract JsonMapper getMapper();

  public abstract Set<EmbeddedVariable> getInputVariables(DelegateExecution execution) throws JacksonException;

  public abstract boolean hasInputVariables(DelegateExecution execution);

  public abstract void setInputVariables(Expression inputVariables);

  public default Map<String, Object> getInputs(DelegateExecution execution) throws JacksonException {
    final Map<String, Object> inputs = new HashMap<>();

    if (!hasInputVariables(execution)) {
      getLogger().warn("Input variables for execution {} is null", execution.getId());
      return inputs;
    }

    for (EmbeddedVariable variable : getInputVariables(execution)) {
      String key = variable.getKey();
      VariableType type = variable.getType();

      if (key == null) {
        getLogger().warn("Input key is null");
      } else if (type == null) {
        getLogger().warn("Variable type not present for {}", key);
      } else if (type == VariableType.LOCAL || type == VariableType.PROCESS) {
        final Object value = type == VariableType.LOCAL
          ? execution.getVariableLocal(key)
          : execution.getVariable(key);

        defaultGetInputsLoop(variable, key, type, value, inputs);
      } else {
        getLogger().warn("Could not find value for {} from {}", key, type);
      }
    }

    return inputs;
  }

  /**
   * Helper function for getInputs() to help solve "S3776" coding practice.
   *
   * @param variable The not-null variable.
   * @param key The not-null key.
   * @param type The not-null type.
   * @param value The not-null value.
   * @param inputs The inputs array to append the value to.
   *
   * @throws JacksonException Failed to process JSON.
   */
  private void defaultGetInputsLoop(EmbeddedVariable variable, String key, VariableType type, Object value, Map<String, Object> inputs) throws JacksonException {
    if (Boolean.FALSE.equals(variable.getSpin())) {
      inputs.put(key, value);
      return;
    }

    final JacksonJsonNode node = (JacksonJsonNode) value;

    Object input = null;

    if (node == null) {
      getLogger().warn("Could not find node for value for {} from {}", key, type);
    } else if (Boolean.TRUE.equals(variable.getAsJson())) {
      input = node.unwrap();
    } else if (Boolean.TRUE.equals(node.isArray())) {
      input = getMapper().convertValue(node.unwrap(), new TypeReference<List<Object>>() {});
    } else if (node.isObject()) {
      input = getMapper().convertValue(node.unwrap(), new TypeReference<Map<String, Object>>() {});
    } else if (Boolean.TRUE.equals(node.isValue())) {
      // E-mails may embed JSON into a string, so attempt to treat as JSON and if its not then this will fail.
      // On Failure, put the value directly as is.
      try {
        input = getMapper().readTree((String) node.value());
      } catch (Exception e) {
        input = node.value();
      }
    } else {
      getLogger().debug("Unknown condition for Jackson deserialization of node for value for {} from {}", key, type);
    }

    inputs.put(key, input);
  }

}
