package org.folio.rest.camunda.delegate;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRuntimeDelegate extends AbstractDelegate {

  @Autowired
  protected RuntimeService runtimeService;

}
