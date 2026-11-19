package org.folio.rest.camunda.provisioner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.folio.spring.tenant.config.HibernateMultiTenantConfig;
import org.folio.spring.tenant.hibernate.HibernateSchemaDataSource;
import org.folio.spring.tenant.interfaces.TenantDatabaseProvisioner;
import org.folio.spring.tenant.service.SchemaService;
import org.folio.spring.tenant.storage.ThreadLocalStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Workflow-specific implementation of TenantDatabasProvisioner.
 */
public class CamundaTenantDatabaseProvisioner implements TenantDatabaseProvisioner {

  private static final Logger LOG = LoggerFactory.getLogger(CamundaTenantDatabaseProvisioner.class);

  private final ApplicationContext applicationContext;

  private final DataSource dataSource;

  private final SchemaService schemaService;

  /**
   * Initializer.
   *
   * @param applicationContext The application context.
   * @param dataSource         The provisioning data source.
   * @param schemaService      The schema service.
   */
  public CamundaTenantDatabaseProvisioner(ApplicationContext applicationContext, DataSource dataSource, SchemaService schemaService) {

    this.applicationContext = applicationContext;
    this.dataSource = dataSource;
    this.schemaService = schemaService;
  }

  @Override
  public void provisionSchema(String schema, String tenant) throws SQLException {

    LOG.debug("Provisioning schema '{}' for tenant '{}'.", schema, tenant);

    final String originalTlsTenant = ThreadLocalStorage.getTenant();

    // Set the tenant context so the MultiTenantConnectionProvider routes correctly.
    ThreadLocalStorage.setTenant(tenant);

    try {
      provisionSchema(schema);
    } finally {
      ThreadLocalStorage.unload();
    }

    if (originalTlsTenant != null) {
      ThreadLocalStorage.setTenant(originalTlsTenant);
    }
  }

  /**
   * Perform the provisioning for the schema using the custom provisioning data source.
   *
   * @param schema The schema to provision.
   */
  private void provisionSchema(String schema) {

    final LocalContainerEntityManagerFactoryBean primaryEmf
      = (LocalContainerEntityManagerFactoryBean) applicationContext.getBean("&entityManagerFactory");

    try (
      final Connection connection = dataSource.getConnection();
      final Statement statement = connection.createStatement()
    ) {
      statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s", schema));
    } catch (SQLException e) {
      throw new RuntimeException(String.format("Failed to create schema '%s'.", schema), e);
    }

    final Map<String, Object> jpaProps = new HashMap<>();
    jpaProps.put("hibernate.dialect", primaryEmf.getJpaPropertyMap().get("hibernate.dialect"));
    jpaProps.put("hibernate.default_schema", schema);
    jpaProps.put("hibernate.hbm2ddl.auto", "update"); // TODO: expose this as a custom configuration setting.

    // Must be explicitly set to override and take precedence.
    final DataSource tenantDataSource = new HibernateSchemaDataSource(dataSource, schemaService);
    jpaProps.put("hibernate.connection.datasource", tenantDataSource);

    final LocalContainerEntityManagerFactoryBean tenantEmf = new LocalContainerEntityManagerFactoryBean();
    tenantEmf.setDataSource(tenantDataSource);
    tenantEmf.setJpaVendorAdapter(primaryEmf.getJpaVendorAdapter());
    tenantEmf.setPackagesToScan(HibernateMultiTenantConfig.PACKAGES_TO_SCAN);
    tenantEmf.setJpaPropertyMap(jpaProps);
    tenantEmf.setPersistenceUnitName(String.format("%s_%s", CamundaTenantDatabaseProvisioner.class, schema));

    try {
      tenantEmf.afterPropertiesSet();
      //final EntityManagerFactory emf = tenantEmf.getObject();
    } catch (Exception e) {
      throw new RuntimeException(String.format("Failed to provision schema '%s'.", schema), e);
    } finally {
      tenantEmf.destroy();
    }
  }

}
