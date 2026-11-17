package org.folio.rest.camunda.config;

import java.sql.SQLException;
import javax.script.ScriptEngineManager;
import org.folio.rest.camunda.resolver.ScriptEngineResolver;
import org.folio.rest.camunda.service.TenantProvisioningService;
import org.folio.spring.tenant.storage.ThreadLocalStorage;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.operaton.bpm.spring.boot.starter.configuration.OperatonProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaEngineConfig implements OperatonProcessEngineConfiguration  {

  private static final Logger logger = LoggerFactory.getLogger(CamundaEngineConfig.class);

  private final TenantProvisioningService tenantProvisioningService;

  /**
   * Initializer.
   *
   * @param tenantProvisioningService The provisioning service.
   */
  public CamundaEngineConfig(TenantProvisioningService tenantProvisioningService) {
    super();

    this.tenantProvisioningService = tenantProvisioningService;
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

    try {
      final String schema = tenantProvisioningService.ensureSchema(tenant);

      if (schema == null) {
        logger.debug("Did not ensure schema, tenant is '{}'.", tenant);
      } else {
        logger.debug("Ensured schema '{}' with tenant of '{}'.", schema, tenant);
      }
    } catch (SQLException e) {
      throw new RuntimeException(String.format("Failed to ensure schema for tenant '%s'.", tenant), e);
    }
  }

}
