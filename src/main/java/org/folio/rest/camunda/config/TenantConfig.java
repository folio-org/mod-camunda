package org.folio.rest.camunda.config;

import org.folio.spring.tenant.properties.TenantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Provide special case Tenant information.
 */
@Configuration
public class TenantConfig {

  private static TenantProperties tenantProperties;

  @Autowired
  public TenantConfig(TenantProperties tp) {
    if (tenantProperties == null) {
      tenantProperties = tp;
    }
  }

  /**
   * Provide a static way get the `TENANT_HEADERNAME` value.
   *
   * The yaml file names this `tenant.header-name`.
   *
   * The OkapiRestTemplate design prevents auto-injection because non-Java code will run the static methods via the MappingUtility.
   * Load the settings in such a way that a static method may access settings injected by Spring.
   *
   * These settings are normally defined in the Spring-Module-Core Spring-Tenant `TenantProperties` class
   *
   * @return the tenant header.
   */
  public static String getHeaderName() {
    return tenantProperties.getHeaderName();
  }
}
