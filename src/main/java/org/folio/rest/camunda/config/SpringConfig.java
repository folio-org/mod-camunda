package org.folio.rest.camunda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring")
public class SpringConfig {

  /**
   * The spring.datasource configuration.
   */
  private SpringDatasourceConfig springDatasource = new SpringDatasourceConfig();

  /**
   * The spring.jpa.properties.hibernate configuration.
   */
  private SpringHibernateConfig springHibernate = new SpringHibernateConfig();

  public SpringDatasourceConfig getSpringDatasourceConfig() {
    return springDatasource;
  }

  public void setSpringDatasourceConfig(SpringDatasourceConfig springDatasource) {
    this.springDatasource = springDatasource;
  }

  public SpringHibernateConfig getSpringHibernateConfig() {
    return springHibernate;
  }

  public void setSpringHibernateConfig(SpringHibernateConfig springHibernate) {
    this.springHibernate = springHibernate;
  }

}