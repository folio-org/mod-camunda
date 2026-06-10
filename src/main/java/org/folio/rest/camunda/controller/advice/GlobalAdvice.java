package org.folio.rest.camunda.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.folio.rest.camunda.exception.BpmnModelFailure;
import org.folio.rest.camunda.exception.DelegateSpinFailure;
import org.folio.rest.camunda.exception.EmailDelegateAddressFailure;
import org.folio.rest.camunda.exception.ScriptEngineLoadFailed;
import org.folio.rest.camunda.exception.ScriptEngineUnsupported;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@RestControllerAdvice
public class GlobalAdvice extends AbstractAdvice {

  JsonMapper mapper;

  public GlobalAdvice() {
    this.mapper = JsonMapper
      .builderWithJackson2Defaults()
      .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES, true)
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
      .changeDefaultPropertyInclusion(incl -> incl
        .withValueInclusion(JsonInclude.Include.NON_NULL)
        .withContentInclusion(JsonInclude.Include.NON_NULL)
      )
      .findAndAddModules()
      .build();
  }

  @Override
  protected JsonMapper getMapper() {
    return mapper;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(BpmnModelFailure.class)
  public ResponseEntity<String> handleBpmnModelFailure(BpmnModelFailure exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(DelegateSpinFailure.class)
  public ResponseEntity<String> handleDelegateSpinFailure(DelegateSpinFailure exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(EmailDelegateAddressFailure.class)
  public ResponseEntity<String> handleEmailDelegateAddressFailure(EmailDelegateAddressFailure exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ScriptEngineLoadFailed.class)
  public ResponseEntity<String> handleScriptEngineLoadFailed(ScriptEngineLoadFailed exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ScriptEngineUnsupported.class)
  public ResponseEntity<String> handleScriptEngineUnsupported(ScriptEngineUnsupported exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(WorkflowAlreadyActiveException.class)
  public ResponseEntity<String> handleWorkflowAlreadyActiveException(WorkflowAlreadyActiveException exception) {
    return buildError(exception, HttpStatus.BAD_REQUEST, MediaType.APPLICATION_JSON);
  }

  /**
   * Catch all exception handler.
   *
   * @param exception The exception.
   *
   * @return The generated response.
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception exception) {
    return buildError(exception, HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON);
  }

}
