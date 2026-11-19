package org.folio.rest.camunda.config;

import javax.script.ScriptEngineManager;
import org.folio.rest.camunda.resolver.ScriptEngineResolver;
import org.folio.spring.tenant.service.SchemaProvisionerService;
import org.folio.spring.tenant.service.SchemaService;
import org.folio.spring.tenant.storage.ThreadLocalStorage;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.operaton.bpm.spring.boot.starter.configuration.OperatonProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide custom configuration for Operaton process engines.
 */
public class CamundaEngineConfig implements OperatonProcessEngineConfiguration  {

  private static final Logger logger = LoggerFactory.getLogger(CamundaEngineConfig.class);

  private final SchemaService schemaService;

  private final SchemaProvisionerService schemaProvisionerService;

  /**
   * Initializer.
   *
   * @param schemaService             The schema service.
   * @param tenantProvisioningService The provisioning service.
   */
  public CamundaEngineConfig(SchemaService schemaService, SchemaProvisionerService schemaProvisionerService) {
    super();

    this.schemaService = schemaService;
    this.schemaProvisionerService = schemaProvisionerService;
  }

  @Override
  public void preInit(ProcessEngineConfigurationImpl config) {

    logger.debug(
      "Initializing engine with schema '{}' and schema update '{}'; thread local has tenant of '{}'.",
      config.getDatabaseSchema(),
      config.getDatabaseSchemaUpdate(),
      ThreadLocalStorage.getTenant()
    );

    config.setScriptEngineResolver(new ScriptEngineResolver(new ScriptEngineManager()));

    // The multi-tenancy approach design requires this.
    config.setUseSharedSqlSessionFactory(true);
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine engine) {

    final String tenant = ThreadLocalStorage.getTenant();
    final String schema = schemaService.getSchema(tenant);

    schemaProvisionerService.ensureProvisionedSchema(schema, tenant);
  }

}
