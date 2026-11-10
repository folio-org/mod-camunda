package org.folio.rest.camunda.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.folio.rest.camunda.config.SpringDatasourceConfig;
import org.folio.spring.tenant.service.SchemaService;
import org.springframework.stereotype.Service;

@Service 
public class TenantProvisioningService {
    
  private final SchemaService schemaService;
  private final SpringDatasourceConfig springDatasourceConfig;

  private final Set<String> tenants;

  public TenantProvisioningService(SchemaService schemaService, SpringDatasourceConfig springDatasourceConfig) {
    this.schemaService = schemaService;
    this.springDatasourceConfig = springDatasourceConfig;
    tenants = ConcurrentHashMap.newKeySet();
  }

  public void ensureSchema(String tenant) throws SQLException {
    if (tenants.contains(tenant)) return;

    final String schema = schemaService.getSchema(tenant);

    try (
      final Connection connection = DriverManager.getConnection(
        springDatasourceConfig.getUrl(),
        springDatasourceConfig.getUsername(),
        springDatasourceConfig.getPassword()
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
