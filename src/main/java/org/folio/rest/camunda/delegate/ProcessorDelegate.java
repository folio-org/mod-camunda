package org.folio.rest.camunda.delegate;

import java.util.Map;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.service.ScriptEngineService;
import org.folio.rest.workflow.model.EmbeddedProcessor;
import org.folio.rest.workflow.model.ProcessorTask;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
@Scope("prototype")
public class ProcessorDelegate extends AbstractWorkflowIODelegate {

  @Autowired
  private ScriptEngineService scriptEngineService;

  private Expression processor;

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    EmbeddedProcessor processorValue = mapper.readValue(this.processor.getValue(execution).toString(), EmbeddedProcessor.class);

    String scriptName = processorValue.getFunctionName();

    String scriptTypeExtension = processorValue.getScriptType().getExtension();

    Map<String, Object> inputs = getInputs(execution);

    JsonNode input = mapper.valueToTree(inputs);

    try {
      final String output = (String) scriptEngineService.runScript(scriptTypeExtension, scriptName, input);
  
      setOutput(execution, output);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

  public void setProcessor(Expression processor) {
    this.processor = processor;
  }

  @Override
  public Class<?> fromTask() {
    return ProcessorTask.class;
  }

}
