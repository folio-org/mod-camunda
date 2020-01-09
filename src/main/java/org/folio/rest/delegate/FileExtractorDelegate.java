package org.folio.rest.delegate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.folio.rest.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class FileExtractorDelegate extends AbstractReportableDelegate {

  @Value("${tenant.default-tenant}")
  private String DEFAULT_TENANT;

  @Autowired
  private StreamService streamService;

  private Expression path;

  private Expression workflow;

  private Expression delay;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    super.execute(execution);
    String delegateName = execution.getBpmnModelElementInstance().getName();

    String tenant = execution.getTenantId() != null ? execution.getTenantId() : DEFAULT_TENANT;

    String path = this.path.getValue(execution).toString();

    String workflow = this.workflow.getValue(execution).toString();

    File workflowDirectory = new File(String.join(File.separator, path, tenant, workflow));
    if (workflowDirectory.exists()) {
      long delay = Long.parseLong(this.delay.getValue(execution).toString());

      String primaryStreamId = (String) execution.getVariable("primaryStreamId");

      updateReport(primaryStreamId, String.format("%s started at %s", delegateName, Instant.now()));

      long count = getFileStream(workflowDirectory).count();

      execution.setVariable("count", count);

      Stream<String> stream = getFileStream(workflowDirectory)
        .map(file -> {
          Optional<String> data = Optional.empty();
          try {
            data = Optional.of(IOUtils.toString(file.toURI(), StandardCharsets.UTF_8));
          } catch (IOException e) {
            String errmsg = String.format("Failed to write file %s: %s", file.getAbsolutePath(), e.getMessage());
            log.error(errmsg);
            updateReport(primaryStreamId, errmsg);
          }
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return data;
        })
        .filter(data -> data.isPresent())
        .map(data -> data.get());

      streamService.concatenateStream(primaryStreamId, stream);
    } else {
      log.error("%s directory does not exists!", String.join(File.separator, path, tenant, workflow));
    }
  }

  public Expression getPath() {
    return path;
  }

  public void setWorkflow(Expression workflow) {
    this.workflow = workflow;
  }

  public void setDelay(Expression delay) {
    this.delay = delay;
  }

  private Stream<File> getFileStream(File directory) throws IOException {
    return Files.list(directory.toPath())
      .map(p -> p.toFile())
      .filter(file -> file.isFile() && !file.getName().startsWith("."));
  }

}