package org.folio.rest.camunda.config;

import org.operaton.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.operaton.bpm.spring.boot.starter.configuration.Ordering;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class CamundaConfig {

  @Bean
  @Order(Ordering.DEFAULT_ORDER + 1)
  static ProcessEnginePlugin processEnginePlugin() {
    return new CamundaEngineConfig();
  }

}
