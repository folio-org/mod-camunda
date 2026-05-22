package org.folio.rest.camunda.delegate;

import org.operaton.bpm.engine.delegate.Expression;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract database delegate.
 */
public abstract class AbstractDatabaseDelegate extends AbstractWorkflowDelegate {

  Expression designation;

  @Autowired
  DatabaseConnectionService connectionService;

  public void setDesignation(Expression designation) {
    this.designation = designation;
  }

}
