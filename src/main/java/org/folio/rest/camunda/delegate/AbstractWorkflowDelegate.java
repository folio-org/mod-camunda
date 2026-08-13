package org.folio.rest.camunda.delegate;

import org.operaton.bpm.engine.RuntimeService;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract workflow delegate.
 */
public abstract class AbstractWorkflowDelegate extends AbstractDelegate {

  public abstract Class<?> fromTask();

  /**
   * Initializer.
   */
  AbstractWorkflowDelegate(JsonMapper mapper, RuntimeService runtimeService) {

    super(mapper, runtimeService);
  }

}
