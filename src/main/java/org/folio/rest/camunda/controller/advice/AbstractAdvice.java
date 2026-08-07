package org.folio.rest.camunda.controller.advice;

import org.folio.spring.web.utility.ErrorUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

abstract class AbstractAdvice extends RequestMappingHandlerMapping {

  /**
   * Get the object mapper
   *
   * @return mapper The object mapper.
   */
  protected abstract JsonMapper getMapper();

  /**
   * Build the error message, with default JSON media type.
   *
   * @param ex The exception.
   * @param code The HTTP Status Code.
   *
   * @return The built error response entity.
   */
  protected ResponseEntity<String>  buildError(Exception ex, HttpStatus code) {
    return buildError(ex, code, MediaType.APPLICATION_JSON);
  }

  /**
   * Build the error message.
   *
   * @param ex The exception.
   * @param code The HTTP Status Code.
   * @param type The media type to use.
   *
   * @return The built error response entity.
   */
  protected ResponseEntity<String>  buildError(Exception ex, HttpStatus code, MediaType type) {
    String message = ex.getMessage();

    logger.error(message, logger.isDebugEnabled() ? ex : null);

    // The exception handler should ideally not throw its own exceptions.
    // Catch the exceptions and report it, then fall back to a plain text error message.
    try {
      message = getMapper().writeValueAsString(ErrorUtility.buildError(ex, code));
    } catch (JacksonException e) {
      logger.error("Mapping error to JSON Object failed.", e);

      type = MediaType.TEXT_PLAIN;
    }

    return ResponseEntity.status(code)
      .contentType(type)
      .body(message);
  }

}
