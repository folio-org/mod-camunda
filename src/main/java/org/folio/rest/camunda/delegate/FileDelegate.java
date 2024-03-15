package org.folio.rest.camunda.delegate;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.folio.rest.workflow.enums.FileOp;
import org.folio.rest.workflow.model.FileTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@Scope("prototype")
public class FileDelegate extends AbstractWorkflowIODelegate {

  private Expression path;

  private Expression line;

  private Expression op;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    long startTime = System.nanoTime();
    FlowElement bpmnModelElement = execution.getBpmnModelElementInstance();
    String delegateName = bpmnModelElement.getName();

    getLogger().info("{} started", delegateName);

    String pathTemplate = this.path.getValue(execution).toString();
    String lineTemplate = this.line != null ? this.line.getValue(execution).toString() : "0";

    StringTemplateLoader templateLoader = new StringTemplateLoader();
    templateLoader.putTemplate("path", pathTemplate);
    templateLoader.putTemplate("line", lineTemplate);

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    cfg.setTemplateLoader(templateLoader);

    Map<String, Object> inputs = getInputs(execution);

    String filePath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate("path"), inputs);
    Integer line = Integer.parseInt(FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate("line"), inputs));

    FileOp fileOp = FileOp.valueOf(this.op.getValue(execution).toString());

    File file = new File(filePath);

    switch (fileOp) {
      case DELETE:
        if (file.exists()) {
          boolean deleted = file.delete();
          if (deleted) {
            getLogger().info("{} has been deleted", filePath);
          }
        } else {
          getLogger().info("{} does not exist", filePath);
        }
        break;
      case LINE_COUNT:
        if (file.exists()) {
          try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            long lineCount = reader.lines().count();
            reader.close();
            setOutput(execution, lineCount);
            getLogger().info("{} read", filePath);
          }
        } else {
          getLogger().info("{} does not exist", filePath);
        }
        break;
      case READ_LINE:
        if (file.exists() && line > 0) {
          try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            int lineCount = 0;
            String currerntLine = "";
            while ((currerntLine = reader.readLine()) != null && (++lineCount) < line) {}
            reader.close();
            setOutput(execution, currerntLine);
            getLogger().info("{} read", filePath);
          }
        } else {
          getLogger().info("{} does not exist", filePath);
        }
        break;
      case READ:
        if (file.exists()) {
          String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
          setOutput(execution, content);
          getLogger().info("{} read", filePath);
        } else {
          getLogger().info("{} does not exist", filePath);
        }
        break;
      case WRITE:
        StringBuilder content = new StringBuilder();
        for (Object value : inputs.values()) {
          if (value instanceof String) {
            content.append(value);
          } else {
            content.append(objectMapper.writeValueAsString(value));
          }
          content.append("\n");
        }
        FileUtils.writeStringToFile(file, content.toString(), StandardCharsets.UTF_8);
        getLogger().info("{} written", filePath);
        break;
      case LIST:
        if (file.exists()) {
          if (file.isDirectory()) {
            List<String> listing = new ArrayList<>();
            traverseDirectory(file, listing);
            setOutput(execution, listing);
          } else {
            getLogger().info("{} is not a directory to list", filePath);
          }
        } else {
          getLogger().info("{} does not exist", filePath);
        }
        break;
      default:
        break;
    }

    long endTime = System.nanoTime();
    getLogger().info("{} finished in {} milliseconds", delegateName, (endTime - startTime) / (double) 1000000);
  }

  public void setPath(Expression path) {
    this.path = path;
  }

  public void setOp(Expression op) {
    this.op = op;
  }

  @Override
  public Class<?> fromTask() {
    return FileTask.class;
  }

  private void traverseDirectory(File directory, List<String> listing) {
    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      Arrays.sort(files, Comparator.comparingLong(File::lastModified));
      for (File file : files) {
        if (file.isFile()) {
          listing.add(file.getAbsolutePath());
        } else if (file.isDirectory()) {
          traverseDirectory(file, listing);
        }
      }
    }
  }

}
