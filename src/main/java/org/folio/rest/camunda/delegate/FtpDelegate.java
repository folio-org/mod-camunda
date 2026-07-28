package org.folio.rest.camunda.delegate;

import static org.folio.rest.camunda.utility.FileUtility.createFile;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.workflow.enums.SftpOp;
import org.folio.rest.workflow.model.FtpTask;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class FtpDelegate extends AbstractWorkflowIODelegate {

  private Expression originPath;

  private Expression destinationPath;

  private Expression op;

  private Expression scheme;

  private Expression host;

  private Expression port;

  private Expression username;

  private Expression password;

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    String originPathValue = this.originPath.getValue(execution).toString();

    String destinationPathValue = this.destinationPath.getValue(execution).toString();

    SftpOp opValue = SftpOp.valueOf(this.op.getValue(execution).toString());

    String schemeValue = this.scheme.getValue(execution).toString();

    String hostValue = this.host.getValue(execution).toString();

    int portValue = Integer.parseInt(this.port.getValue(execution).toString());

    Optional<String> usernameValue = Objects.nonNull(this.username)
      ? Optional.ofNullable(this.username.getValue(execution).toString())
      : Optional.empty();

    Optional<String> passwordValue = Objects.nonNull(this.password)
      ? Optional.ofNullable(this.password.getValue(execution).toString())
      : Optional.empty();

    final FileSystemManager manager;

    try {
      manager = VFS.getManager();
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }

    String userInfo = null;

    if (usernameValue.isPresent()) {
      userInfo = usernameValue.get();
    }

    if (passwordValue.isPresent()) {
      userInfo += ":" + passwordValue.get();
    }

    switch (opValue) {
      case GET: {

        final File file = createFile(destinationPathValue);
        final URI uri;

        try {
          uri = new URI(
            schemeValue,
            userInfo,
            hostValue,
            portValue,
            originPathValue,
            null,
            null
          );
        } catch (Exception e) {
          throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
        }

        try (
          final FileObject local = manager.resolveFile(file.getAbsolutePath());
          final FileObject remote = manager.resolveFile(uri);
        ) {
          local.copyFrom(remote, Selectors.SELECT_SELF);
        } catch (Exception e) {
          throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
        }

        break;
      }

      case PUT: {

        final File file = createFile(originPathValue);
        final URI uri;

        try {
          uri = new URI(
            schemeValue,
            userInfo,
            hostValue,
            portValue,
            destinationPathValue,
            null,
            null
          );
        } catch (Exception e) {
          throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
        }

        try (
          final FileObject local = manager.resolveFile(file.getAbsolutePath());
          final FileObject remote = manager.resolveFile(uri);
        ) {
          remote.copyFrom(local, Selectors.SELECT_SELF);
        } catch (Exception e) {
          throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
        }

        break;
      }

      default:
        break;
    }
  }

  public void setOriginPath(Expression originPath) {
    this.originPath = originPath;
  }

  public void setDestinationPath(Expression destinationPath) {
    this.destinationPath = destinationPath;
  }

  public void setOp(Expression op) {
    this.op = op;
  }

  public void setScheme(Expression scheme) {
    this.scheme = scheme;
  }

  public void setHost(Expression host) {
    this.host = host;
  }

  public void setPort(Expression port) {
    this.port = port;
  }

  public void setUsername(Expression username) {
    this.username = username;
  }

  public void setPassword(Expression password) {
    this.password = password;
  }

  @Override
  public Class<?> fromTask() {
    return FtpTask.class;
  }

}
