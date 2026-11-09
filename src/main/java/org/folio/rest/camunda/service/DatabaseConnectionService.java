package org.folio.rest.camunda.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.folio.spring.tenant.service.SchemaService;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class DatabaseConnectionService {

  private final Map<String, HikariDataSource> pools;

  private final SchemaService schemaService;

  public DatabaseConnectionService(SchemaService schemaService) {
    this.pools = new HashMap<>();
    this.schemaService = schemaService;
  }

  public synchronized void createPool(String key, String url, Properties info) throws SQLException {
    if (!this.pools.containsKey(key)) {
      HikariConfig config = new HikariConfig();

      config.setLeakDetectionThreshold(0);
      config.setJdbcUrl(url);
      config.setUsername(info.getProperty("user"));
      config.setPassword(info.getProperty("password"));
      // config.setDriverClassName();
      config.setMaximumPoolSize(8);
      config.addDataSourceProperty("oracle.jdbc.implicitStatementCacheSize", 10);

      config.setConnectionTimeout(30000); // default
      config.setIdleTimeout(600000); // default
      config.setMaxLifetime(7200000); // 2 hours as default is 30 minutes

      // would be nice to just use this but have to ensure properties match
      // config.setDataSourceProperties(info);

      HikariDataSource dataSource = new HikariDataSource(config);

      final String tenant = info.getProperty("tenant");
      dataSource.setConnectionInitSql(String.format("SET search_path TO %s, public", schemaService.getSchema(tenant)));

      this.pools.put(key, dataSource);
    }
  }

  public synchronized Connection getConnection(String poolKey) throws SQLException {
    return this.pools.get(poolKey).getConnection();
  }

  public synchronized void destroyConnection(String poolKey) throws SQLException {
    // do nothing
  }

}
