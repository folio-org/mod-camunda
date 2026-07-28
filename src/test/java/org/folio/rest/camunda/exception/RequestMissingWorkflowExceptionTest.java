package org.folio.rest.camunda.exception;

import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestMissingWorkflowExceptionTest {

  private static final Exception EXCEPTION = new RuntimeException();

  @Test
  void workflowAlreadyActiveExceptionWorksTest() {
    RequestMissingWorkflowException exception = Assertions.assertThrows(RequestMissingWorkflowException.class, () -> {
      throw new RequestMissingWorkflowException(VALUE);
    });

    assertNotNull(exception);
    assertTrue(exception.getMessage().contains(VALUE));
  }

  @Test
  void workflowAlreadyActiveExceptionWorksWithParameterTest() {
    RequestMissingWorkflowException exception = Assertions.assertThrows(RequestMissingWorkflowException.class, () -> {
      throw new RequestMissingWorkflowException(VALUE, EXCEPTION);
    });

    assertNotNull(exception);
    assertTrue(exception.getMessage().contains(VALUE));
  }

}
