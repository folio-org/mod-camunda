package org.folio.rest.camunda.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class LoggerProviderTest {

  private LoggerProvider loggerProvider;

  @BeforeEach()
  void beforeEach() {

    loggerProvider = new LoggerProvider();
  }

  @Test
  void forClassCreatesLoggerTest() {

    final Logger logger = loggerProvider.forClass(LoggerProviderTest.class);

    assertNotNull(logger);
  }
}
