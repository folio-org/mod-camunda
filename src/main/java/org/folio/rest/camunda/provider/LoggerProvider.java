package org.folio.rest.camunda.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for injecting the class name via a bean for a logger.
 */
public class LoggerProvider {

  /**
   * Create logger using the given class.
   */
  public Logger forClass(Class<?> clazz) {

    return LoggerFactory.getLogger(clazz);
  }
}
