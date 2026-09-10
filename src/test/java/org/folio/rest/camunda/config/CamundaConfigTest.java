package org.folio.rest.camunda.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { CamundaConfig.class })
class CamundaConfigTest {

  @Autowired
  private ApplicationContext context;

  @ParameterizedTest
  @MethodSource("provideBeanExistsTestValues")
  void beanExistsTest(String name) {

    assertNotNull(context.getBean(CamundaConfig.class));

    assertTrue(context.containsBean(name));
  }

  /**
   * Provide variables for beanExistsTest.
   *
   * @return
   *   The arguments array stream with the stream columns as:
   *     - name: The bean name (essentially the name of the function with the `@Bean`).
   */
  private static Stream<Arguments> provideBeanExistsTestValues() {

    final List<Arguments> arguments = new ArrayList<>();

    arguments.add(Arguments.of("clock"));
    arguments.add(Arguments.of("concurrentFolioTokensRecordHashMap"));
    arguments.add(Arguments.of("loggerProvider"));
    arguments.add(Arguments.of("processEnginePlugin"));

    return arguments.stream();
  }

}
