package org.folio.rest.camunda.exception;

import static org.folio.spring.test.mock.MockMvcConstant.ID;
import static org.folio.spring.test.mock.MockMvcConstant.UUID;
import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DelegateExecutionFailureTest {

  private static final Exception EXCEPTION = new RuntimeException();

  @Test
  void delegateExecutionFailureWorksTest() {
    DelegateExecutionFailure exception = Assertions.assertThrows(DelegateExecutionFailure.class, () -> {
      throw new DelegateExecutionFailure(VALUE, UUID, ID);
    });

    assertNotNull(exception);
    assertTrue(exception.getMessage().contains(VALUE));
    assertTrue(exception.getMessage().contains(UUID));
    assertTrue(exception.getMessage().contains(ID));
  }

  @Test
  void delegateExecutionFailureWorksWithParameterTest() {
    DelegateExecutionFailure exception = Assertions.assertThrows(DelegateExecutionFailure.class, () -> {
      throw new DelegateExecutionFailure(VALUE, UUID, ID, EXCEPTION);
    });

    assertNotNull(exception);
    assertTrue(exception.getMessage().contains(VALUE));
    assertTrue(exception.getMessage().contains(UUID));
    assertTrue(exception.getMessage().contains(ID));
  }
}
