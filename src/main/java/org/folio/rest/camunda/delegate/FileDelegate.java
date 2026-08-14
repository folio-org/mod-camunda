package org.folio.rest.camunda.delegate;

import static org.folio.rest.camunda.utility.FileUtility.createFile;
import static org.folio.rest.camunda.utility.FileUtility.fileUtilsCopyFile;

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
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.workflow.enums.FileOp;
import org.folio.rest.workflow.model.FileTask;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.bpm.model.bpmn.instance.FlowElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import tools.jackson.databind.json.JsonMapper;

@Service
@Scope("prototype")
public class FileDelegate extends AbstractWorkflowIODelegate {

  private static final String LINE_KEY = "line";
  private static final String PATH_KEY = "path";
  private static final String TARGET_KEY = "target";

  private static final String MSG_NOT_EXIST = "{} does not exist";
  private static final String MSG_READ = "{} read";

  private Expression path;

  private Expression line;

  private Expression op;

  private Expression target;

  /**
   * Initializer.
   */
  public FileDelegate(JsonMapper mapper, RuntimeService runtimeService) {

    super(mapper, runtimeService);
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   *
   * @throws Exception On error.
   */
  @Override
  public void execute(DelegateExecution execution) throws Exception {

    final FileOp fileOp = FileOp.valueOf(this.op.getValue(execution).toString());
    final FlowElement flow = execution.getBpmnModelElementInstance();
    final String name = flow.getName();
    final String id = flow.getId();
    final long startTime = determineStartTime(execution, name, fileOp);

    try {
      performExecute(execution, name, id);
      determineEndTime(execution, startTime, name, false);
    } catch (Exception e) {
      determineEndTime(execution, startTime, name, true);

      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    final FileOp fileOp = FileOp.valueOf(this.op.getValue(execution).toString());

    String pathTemplate = this.path.getValue(execution).toString();
    String lineTemplate = this.line != null ? this.line.getValue(execution).toString() : "0";

    StringTemplateLoader templateLoader = new StringTemplateLoader();
    templateLoader.putTemplate(PATH_KEY, pathTemplate);
    templateLoader.putTemplate(LINE_KEY, lineTemplate);

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    cfg.setTemplateLoader(templateLoader);

    Map<String, Object> inputs = getInputs(execution);

    final String filePath;
    final Integer lineValue;
    final File file;

    try {
      filePath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(PATH_KEY), inputs);
      lineValue = Integer.parseInt(FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(LINE_KEY), inputs));
      file = createFile(filePath);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }

    switch (fileOp) {
      case COPY:
        if (file.exists()) {
          try {
            String targetTemplate = this.target.getValue(execution).toString();
            templateLoader.putTemplate(TARGET_KEY, targetTemplate);
            String targetPath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(TARGET_KEY), inputs);
  
            File targetFile = createFile(targetPath);
  
            fileUtilsCopyFile(file, targetFile);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case MOVE:
        if (file.exists()) {
          try {
            String targetTemplate = this.target.getValue(execution).toString();
            templateLoader.putTemplate(TARGET_KEY, targetTemplate);
            String targetPath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(TARGET_KEY), inputs);
  
            File targetFile = createFile(targetPath);
  
            FileUtils.moveFile(file, targetFile);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case DELETE:
        if (file.exists()) {
          boolean deleted = file.delete();
          if (deleted) {
            getLogger().info("{} has been deleted", filePath);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case LINE_COUNT:
        if (file.exists()) {
          try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            long lineCount = reader.lines().count();
            setOutput(execution, lineCount);
            getLogger().info(MSG_READ, filePath);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case READ_LINE:
        if (file.exists() && lineValue > 0) {
          try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            int lineCount = 0;
            String currerntLine = "";

            while ((currerntLine = reader.readLine()) != null && (++lineCount) < lineValue);

            setOutput(execution, currerntLine);
            getLogger().info(MSG_READ, filePath);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case READ:
        if (file.exists()) {
          try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            setOutput(execution, content);
            getLogger().info(MSG_READ, filePath);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else {
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      case WRITE:
        // iterate over `target` input varaible
        // writing entry per line
        String targetInputVariable = this.target.getValue(execution).toString();
        StringBuilder content = new StringBuilder();
        Object obj = inputs.get(targetInputVariable);

        if (obj == null) {
          getLogger().warn("The target parameter '{}' of the WRITE operation is missing from the {} '{}'.", targetInputVariable, getDelegateClass(), name);
        } else if (obj instanceof List) {
          List<?> objects = (List<?>) obj;
          getLogger().info("{} {} has {} entries to write",
            obj.getClass().getSimpleName(), targetInputVariable, objects.size());

          for (Object value : objects) {
              if (value instanceof String) {
                content.append(value);
              } else {
                content.append(mapper.writeValueAsString(value));
              }
              content.append("\n");
            }
        } else if (obj instanceof String) {
          getLogger().info("{} {} has a single string to write.", obj.getClass().getSimpleName(), targetInputVariable);

          content.append(obj);
          content.append("\n");
        } else {
          getLogger().warn("The target parameter '{}' of the WRITE operation is unsupported for the {} '{}'.", targetInputVariable, getDelegateClass(), name);
        }

        try {
          FileUtils.writeStringToFile(file, content.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
          throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
        }

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
          getLogger().info(MSG_NOT_EXIST, filePath);
        }
        break;

      default:
        break;
    }
  }

  public void setPath(Expression path) {
    this.path = path;
  }

  public void setLine(Expression line) {
      this.line = line;
  }

  public void setOp(Expression op) {
    this.op = op;
  }

  public void setTarget(Expression target) {
    this.target = target;
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
