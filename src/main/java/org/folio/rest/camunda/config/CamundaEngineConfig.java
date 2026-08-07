package org.folio.rest.camunda.config;

import javax.script.ScriptEngineManager;
import org.operaton.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.operaton.bpm.spring.boot.starter.configuration.OperatonProcessEngineConfiguration;
import org.operaton.bpm.spring.boot.starter.configuration.Ordering;
import org.folio.rest.camunda.resolver.ScriptEngineResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordering.DEFAULT_ORDER + 1)
public class CamundaEngineConfig implements OperatonProcessEngineConfiguration  {

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    processEngineConfiguration.setScriptEngineResolver(new ScriptEngineResolver(new ScriptEngineManager()));
  }

}
