package org.folio.rest.camunda.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.folio.spring.test.helper.MapperHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractDelegateTest {

  private JsonMapper mapper;

  @Spy
  private Impl abstractDelegate;

  public void beforeEach() {
    mapper = Mockito.spy(MapperHelper.build());
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
    setField(abstractDelegate, "mapper", mapper);

    assertEquals(mapper, abstractDelegate.getMapper());
  }

  private static class Impl extends AbstractDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      // This is a test and should not do anything.
    }
  }

}
