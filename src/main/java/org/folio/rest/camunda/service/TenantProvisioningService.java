package org.folio.rest.camunda.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.folio.rest.camunda.config.SpringConfig;
import org.folio.spring.tenant.service.SchemaService;

public class TenantProvisioningService {
    
  private final SchemaService schemaService;
  private final SpringConfig springConfig;

  private final Set<String> tenants;

  public TenantProvisioningService(SchemaService schemaService, SpringConfig springConfig) {
    this.schemaService = schemaService;
    this.springConfig = springConfig;
    tenants = ConcurrentHashMap.newKeySet();
  }

  public void ensureSchema(String tenant) throws SQLException {
    if (tenants.contains(tenant)) return;

    final String schema = schemaService.getSchema(tenant);

    try (
      final Connection connection = DriverManager.getConnection(
        springConfig.getSpringDatasourceConfig().getUrl(),
        springConfig.getSpringDatasourceConfig().getUsername(),
        springConfig.getSpringDatasourceConfig().getPassword()
      );
      final Statement statement = connection.createStatement();
    ) {

      statement.execute("CREATE SCHEMA IF NOT EXISTS " + schema);

      tenants.add(tenant);
    } catch (SQLException e) {
      throw new SQLException(String.format("Failed to provision a schema \"%s\" for tenant \"%s\": %s", schema, tenant, e.getMessage()));
    }
  }

}
