package org.folio.rest.camunda.delegate;

/**
 * Abstract workflow delegate.
 */
public abstract class AbstractWorkflowDelegate extends AbstractRuntimeDelegate {

  public abstract Class<?> fromTask();

}
