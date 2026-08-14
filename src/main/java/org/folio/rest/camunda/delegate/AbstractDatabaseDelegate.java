package org.folio.rest.camunda.delegate;

import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.Expression;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract database delegate.
 */
public abstract class AbstractDatabaseDelegate extends AbstractWorkflowDelegate {

  Expression designation;

  DatabaseConnectionService connectionService;

  /**
   * Initializer.
   */
  AbstractDatabaseDelegate(JsonMapper mapper, RuntimeService runtimeService, DatabaseConnectionService connectionService) {

    super(mapper, runtimeService);

    this.connectionService = connectionService;
  }

  public void setDesignation(Expression designation) {
    this.designation = designation;
  }

}
