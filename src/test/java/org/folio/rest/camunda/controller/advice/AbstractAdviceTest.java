package org.folio.rest.camunda.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractAdviceTest {

  private static final RuntimeException runtimeException = new RuntimeException("A runtime failure.");

  private ObjectMapper objectMapper;

  private MockAdvice abstractAdvice;

  @BeforeEach
  void beforeEach() {
    objectMapper = Mockito.spy(JsonMapper.builder().build());
    abstractAdvice = new MockAdvice();
  }

  @Test
  void handleBuildErrorWorksTest() {
    ResponseEntity<String> response = abstractAdvice.handleException(runtimeException);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
  }

  @Test
  void handleBuildErrorThrowsJacksonExceptionTest() throws JacksonException {
    when(objectMapper.writeValueAsString(any())).thenThrow(new MockJacksonException());

    ResponseEntity<String> response = abstractAdvice.handleException(runtimeException);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
  }

  /**
   * Provide a concrete class to perform the testing.
   *
   * This is done to avoid using actual implementations while testing the abstract.
   */
  private class MockAdvice extends AbstractAdvice {

    @Override
    protected ObjectMapper getObjectMapper() {
      return objectMapper;
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<String> handleException(RuntimeException ex) {
      return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  /**
   * Provide a way to throw a type of JacksonException.
   *
   * The JacksonException does not have a public initializer.
   */
  private class MockJacksonException extends JacksonException {

    private static final long serialVersionUID = 812355666324245L;

    public MockJacksonException() {
      super("mock");
    }

  }

}
