package org.folio.rest.camunda.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractDatabaseDelegateTest {

  @Mock
  private Expression designation;

  @Spy
  private Impl abstractDatabaseDelegate;

  @Test
  void setMessageWorksTest() {
    setField(abstractDatabaseDelegate, "designation", null);

    abstractDatabaseDelegate.setDesignation(designation);
    assertEquals(designation, getField(abstractDatabaseDelegate, "designation"));
  }

  private static class Impl extends AbstractDatabaseDelegate {

    Impl() {
      super(null, null, null);
    }

    Impl(JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService) {
      super(mapper, runtimeService, connectionService);
    }

    @Override
    protected void performExecute(DelegateExecution execution, String name, String id) {
      // This is a test and should not do anything.
    }

    @Override
    public Class<?> fromTask() {
      return null;
    }
  };

}
