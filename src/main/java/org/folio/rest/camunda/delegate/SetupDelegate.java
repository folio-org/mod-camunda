package org.folio.rest.camunda.delegate;

import static org.operaton.spin.Spin.JSON;

import java.util.List;
import java.util.Map;
import org.folio.rest.camunda.config.FolioEnvConfig;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.model.FolioEnvDefaultsItem;
import org.folio.rest.camunda.service.ScriptEngineService;
import org.folio.rest.workflow.model.EmbeddedProcessor;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.spin.json.SpinJsonNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Service
@Scope("prototype")
public class SetupDelegate extends AbstractDelegate {

  private static final String TIMESTAMP = "timestamp";
  private static final String TENANT_ID = "tenantId";

  private ScriptEngineService scriptEngineService;

  private FolioEnvConfig folioEnvConfig;

  private Expression initialContext;

  private Expression processors;

  /**
   * Initializer.
   */
  public SetupDelegate(JsonMapper mapper, RuntimeService runtimeService, FolioEnvConfig folioEnvConfig, ScriptEngineService scriptEngineService) {

    super(mapper, runtimeService);

    this.folioEnvConfig = folioEnvConfig;
    this.scriptEngineService = scriptEngineService;
  }

  public void setInitialContext(Expression initialContext) {
    this.initialContext = initialContext;
  }

  public void setProcessors(Expression processors) {
    this.processors = processors;
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    getLogger().info("loading initial context with definition id {}", execution.getProcessDefinitionId());

    Map<String, Object> context = mapper.readValue(initialContext.getValue(execution).toString(),
      new TypeReference<Map<String, Object>>() {
    });

    for (Map.Entry<String, Object> entry : context.entrySet()) {
      SpinJsonNode node = JSON(mapper.writeValueAsString(entry.getValue()));
      execution.setVariable(entry.getKey(), node);
      getLogger().info("{}: {}", entry.getKey(), node);
    }

    String timestamp = String.valueOf(System.currentTimeMillis());

    execution.setVariable(TIMESTAMP, timestamp);
    execution.setVariable(TENANT_ID, execution.getTenantId());

    loadEnvConfig(execution);

    getLogger().info("loading scripts");

    List<EmbeddedProcessor> processorsValue = mapper.readValue(this.processors.getValue(execution).toString(),
      new TypeReference<List<EmbeddedProcessor>>() {
    });

    for (EmbeddedProcessor processor : processorsValue) {
      String extension = processor.getScriptType().getExtension();
      String functionName = processor.getFunctionName();
      String code = processor.getCode();

      try {
        scriptEngineService.registerScript(extension, functionName, code);
        getLogger().info("{}: {}", processor.getFunctionName(), processor.getCode());
      } catch (Exception e) {
        throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
      }
    }
  }

  /**
   * Load the environment variable configuration.
   *
   * All variables are loaded and exported to the Operaton engine.
   *
   * The Tenant ID is also exposed.
   *
   * @param execution The delegate execution data.
   */
  private void loadEnvConfig(final DelegateExecution execution) {

    folioEnvConfig.eachItem((key, item) -> loadEnvConfigItem(execution, item));

    execution.setVariable(TENANT_ID, execution.getTenantId());
  }

  /**
   * Load items into the variables, but only if exposed.
   *
   * @param execution The delegate execution data.
   * @param item      The item to load into a Operaton variable.
   */
  private void loadEnvConfigItem(final DelegateExecution execution, final FolioEnvDefaultsItem item) {

    if (Boolean.TRUE.equals(item.getExpose())) {
      execution.setVariable(item.getName(), item.getValue());
    }
  }

}
