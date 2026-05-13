package org.folio.rest.camunda.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractDelegateTest {

  private ObjectMapper objectMapper;

  @Spy
  private Impl abstractDelegate;

  public void beforeEach() {
    objectMapper = Mockito.spy(JsonMapper.builder().build());
  }

  @Test
  void getExpressionWorksTest() {
    final String simpleName = Impl.class.getSimpleName();
    final String delegateName = "${" + simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "}";

    assertEquals(delegateName, abstractDelegate.getExpression());
  }

  @Test
  void getLoggerWorksTest() {
    final Logger expectLog = LoggerFactory.getLogger(Impl.class);

    assertEquals(expectLog.getName(), abstractDelegate.getLogger().getName());
  }

  @Test
  void getObjectMapperWorksTest() {
    setField(abstractDelegate, "objectMapper", objectMapper);

    assertEquals(objectMapper, abstractDelegate.getObjectMapper());
  }

  private static class Impl extends AbstractDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
    }
  };

}
