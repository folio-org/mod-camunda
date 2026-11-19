package org.folio.rest.camunda.config;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import org.folio.rest.camunda.provider.LoggerProvider;
import org.folio.spring.tenant.service.SchemaProvisionerService;
import org.folio.spring.tenant.service.SchemaService;
import org.operaton.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.operaton.bpm.spring.boot.starter.configuration.Ordering;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class CamundaConfig {

  @Bean
  Clock clock() {

    return Clock.systemDefaultZone();
  }

  @Bean
  ConcurrentHashMap<String, String> concurrentFolioTokensRecordHashMap() {

    return new ConcurrentHashMap<>();
  }

  /*
  @Bean
  @Primary
  DataSource dataSource(DataSourceProperties props, SchemaService schemaService) {

    return new HibernateRoutingDataSource(props.initializeDataSourceBuilder().build(), schemaService);
  }*/

  @Bean
  LoggerProvider loggerProvider() {

    return new LoggerProvider();
  }

  @Bean
  @Order(Ordering.DEFAULT_ORDER + 1)
  static ProcessEnginePlugin processEnginePlugin(SchemaService schemaService, SchemaProvisionerService schemaProvisionerService) {

    return new CamundaEngineConfig(schemaService, schemaProvisionerService);
  }

}
