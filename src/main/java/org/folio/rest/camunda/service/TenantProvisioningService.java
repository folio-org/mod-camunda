package org.folio.rest.camunda.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.folio.spring.tenant.config.SpringDataSourceConfig;
import org.folio.spring.tenant.service.SchemaService;
import org.springframework.stereotype.Service;

/**
 * Provision the tenant.
 */
@Service 
public class TenantProvisioningService {

  private final SchemaService schemaService;

  private final SpringDataSourceConfig springDatasourceConfig;

  private final Set<String> tenants;

  /**
   * Initializer.
   *
   * @param schemaService          The schema service.
   * @param springDatasourceConfig The Spring data source configuration.
   */
  public TenantProvisioningService(SchemaService schemaService, SpringDataSourceConfig springDatasourceConfig) {

    this.schemaService = schemaService;
    this.springDatasourceConfig = springDatasourceConfig;

    tenants = ConcurrentHashMap.newKeySet();
  }

  /**
   * Ensure that the schema exists.
   *
   * @param tenant The tenant to use.
   *
   * @return The name of the schema that is ensured or NULL if no schema is ensured.
   *
   * @throws SQLException On error.
   */
  public String ensureSchema(String tenant) throws SQLException {

    if (tenants.contains(tenant)) {
      return null;
    }

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
      throw new SQLException(String.format("Failed to provision a schema '%s' for tenant '%s': %s", schema, tenant, e.getMessage()));
    }

    return schema;
  }

}
