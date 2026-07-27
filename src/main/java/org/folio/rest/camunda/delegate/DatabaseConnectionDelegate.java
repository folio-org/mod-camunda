package org.folio.rest.camunda.delegate;

import java.util.Properties;
import org.folio.rest.workflow.model.DatabaseConnectionTask;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Database connection delegate.
 */
@Service
@Scope("prototype")
public class DatabaseConnectionDelegate extends AbstractDatabaseDelegate {

  private Expression url;
  private Expression username;
  private Expression password;

  /**
   * Perform the delegate execution.
   *
   * @param execution The delegate execution data.
   * @param name      The delegate name.
   *
   * @throws Exception On any error.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name) throws Exception {

    String urlValue = this.url.getValue(execution).toString();
    String key = this.designation.getValue(execution).toString();

    Properties info = new Properties();
    info.setProperty("user", this.username.getValue(execution).toString());
    info.setProperty("password", this.password.getValue(execution).toString());

    connectionService.createPool(key, urlValue, info);
  }

  public void setUrl(Expression url) {
    this.url = url;
  }

  public void setUsername(Expression username) {
    this.username = username;
  }

  public void setPassword(Expression password) {
    this.password = password;
  }

  @Override
  public Class<?> fromTask() {
    return DatabaseConnectionTask.class;
  }

}
