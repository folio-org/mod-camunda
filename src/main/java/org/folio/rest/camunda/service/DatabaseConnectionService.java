package org.folio.rest.camunda.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.folio.spring.tenant.config.JpaPropertiesHibernateConfig;
import org.folio.spring.tenant.properties.TenantProperties;
import org.springframework.stereotype.Service;

/**
 * Provide a custom database connections for both FOLIO tenants as well as external connections.
 */
@Service
public class DatabaseConnectionService {

  /**
   * A prefix to any map key for the FOLIO database connections.
   */
  public static final String PREFIX_FOLIO = "folio_";

  /**
   * A prefix to any map key for the non-FOLIO database connections.
   */
  public static final String PREFIX_OTHER = "other_";
  
  /**
   * A key in properties for the connection pool id.
   */
  public static final String POOL_ID = "id";

  /**
   * A key in properties for the connection pool pass word.
   */
  public static final String POOL_PASS = "password";

  /**
   * A key in properties for the connection pool tenant.
   */
  public static final String POOL_TENANT = "tenant";

  /**
   * A key in properties for the connection pool search_path.
   */
  public static final String POOL_SEARCH_PATH = "searchPath";

  /**
   * A key in properties for the connection pool URL.
   */
  public static final String POOL_URL = "url";

  /**
   * A key in properties for the connection pool user name.
   */
  public static final String POOL_USER = "user";

  /**
   * A regular expression pattern for ensuring that the schema name and pool id are safe.
   *
   * TODO: make the spring module core pattern public so this can just be called.
   */ 
  public static final Pattern SCHEMA_REGEXP = Pattern.compile("[a-z0-9_]+");

  private final ConcurrentHashMap<String, HikariDataSource> pools;

  private final JpaPropertiesHibernateConfig jpaPropertiesHibernateConfig;

  private final TenantProperties tenantProperties;

  /**
   * Initializer.
   *
   * @param tenantProperties The Tenant properties configuration.
   */
  public DatabaseConnectionService(JpaPropertiesHibernateConfig jpaPropertiesHibernateConfig, TenantProperties tenantProperties) {

    this.jpaPropertiesHibernateConfig = jpaPropertiesHibernateConfig;
    this.tenantProperties = tenantProperties;

    this.pools = new ConcurrentHashMap<>();
  }

  public synchronized void createPool(boolean isFolio, Properties info) {

    this.pools.computeIfAbsent(buildPoolId(isFolio, info), key -> {
      final HikariConfig config = new HikariConfig();
      final String searchPath = buildSearchPath(isFolio, info);

      config.setLeakDetectionThreshold(0);
      config.setJdbcUrl(info.getProperty(POOL_URL));
      config.setUsername(info.getProperty(POOL_USER));
      config.setPassword(info.getProperty(POOL_PASS));
      // config.setDriverClassName();
      config.setMaximumPoolSize(8);
      //config.setDataSourceProperties(datasourceProperties);
      //config.addDataSourceProperty("oracle.jdbc.implicitStatementCacheSize", 10);

      config.setConnectionTimeout(30000); // default
      config.setIdleTimeout(600000); // default
      config.setMaxLifetime(7200000); // 2 hours as default is 30 minutes


      // would be nice to just use this but have to ensure properties match
      // config.setDataSourceProperties(info);

      final HikariDataSource dataSource = new HikariDataSource(config);

      if (!searchPath.isBlank()) {
        dataSource.setConnectionInitSql(String.format("SET search_path TO %s", searchPath));
      }

      return dataSource;
    });
  }

  public synchronized Connection getConnection(String poolKey) throws SQLException {
    return this.pools.get(poolKey).getConnection();
  }

  public synchronized void destroyConnection(String poolKey) throws SQLException {
    // do nothing
  }

  /**
   * Load the pool ID, append the prefix, and fall back to a default tenant if need be.
   *
   * @param isFolio TRUE if this is a FOLIO connection and false otherwise.
   * @param info    An array of all properties.
   *
   * @return The constructed pool ID.
   */
  private String buildPoolId(boolean isFolio, Properties info) {

    final String name;

    if (info.containsKey(POOL_ID)) {
      name = info.getProperty(POOL_ID);
    } else if (isFolio) {
      name = tenantProperties.getDefaultTenant();
    } else {
      name = "";
    }

    if (name.isBlank()) {
      throw new RuntimeException("TODO: Add a custom exception here for connection id (name/key) is not properly defined");
    }
    // TODO: regex sanitize the name.

    return isFolio
      ? PREFIX_FOLIO + name
      : PREFIX_OTHER + name;
  }

  /**
   * Build the search path.
   *
   * @param isFolio TRUE if this is a FOLIO connection and false otherwise.
   * @param info    An array of all properties.
   *
   * @return The constructed search path where a blank string represents no custom search path to be set.
   */
  private String buildSearchPath(boolean isFolio, Properties info) {

    final String path;

    if (info.containsKey(POOL_SEARCH_PATH)) {
      path = info.getProperty(POOL_SEARCH_PATH);
    } else if (isFolio) {
      path = tenantProperties.getDefaultTenant() + "_" + jpaPropertiesHibernateConfig.getDefaultSchema();
    } else {
      path = "";
    }

    if (!path.isBlank() && !SCHEMA_REGEXP.matcher(path).matches()) {
      throw new IllegalArgumentException("Illegal character in search path name: " + path);
    }

    return path;
  }

}
