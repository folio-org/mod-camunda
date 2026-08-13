package org.folio.rest.camunda.delegate;

import static org.folio.rest.camunda.utility.FileUtility.createBufferedInputStream;
import static org.folio.rest.camunda.utility.FileUtility.createBufferedOutputStream;
import static org.folio.rest.camunda.utility.FileUtility.createCompressorOutputStream;
import static org.folio.rest.camunda.utility.FileUtility.createFile;
import static org.folio.rest.camunda.utility.FileUtility.createFileInputStream;
import static org.folio.rest.camunda.utility.FileUtility.createFileOutputStream;
import static org.folio.rest.camunda.utility.FileUtility.createTarArchiveOutputStream;
import static org.folio.rest.camunda.utility.FileUtility.createZipOutputStream;
import static org.folio.rest.camunda.utility.FileUtility.filesCopy;
import static org.folio.rest.camunda.utility.FileUtility.filesGetLastModifiedTime;
import static org.folio.rest.camunda.utility.FileUtility.filesSize;
import static org.folio.rest.camunda.utility.FileUtility.iOUtilsCopyAndClose;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.camunda.utility.FileUtility;
import org.folio.rest.workflow.enums.CompressFileContainer;
import org.folio.rest.workflow.enums.CompressFileFormat;
import org.folio.rest.workflow.model.CompressFileTask;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import tools.jackson.databind.json.JsonMapper;

/**
 * Compress file delegate.
 */
@Service
@Scope("prototype")
public class CompressFileDelegate extends AbstractWorkflowIODelegate {

  private static final String ENCODING = "UTF8";
  private static final int BLOCK_SIZE = 8192;

  private static final String EXT_BZIP2 = ".bz2";
  private static final String EXT_GZIP = ".gz";
  private static final String EXT_ZIP = ".zip";
  private static final String EXT_TAR = ".tar";

  private Expression source;

  private Expression destination;

  private Expression format;

  private Expression container;

  /**
   * Initializer.
   */
  public CompressFileDelegate(JsonMapper mapper, RuntimeService runtimeService) {

    super(mapper, runtimeService);
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

    final Object sourcePathTemplateValue = this.source == null ? null : this.source.getValue(execution);
    final Object destinationPathTemplateValue = this.destination == null ? null : this.destination.getValue(execution);

    final String sourcePathTemplate = sourcePathTemplateValue == null ? "" : sourcePathTemplateValue.toString();
    final String destinationPathTemplate = destinationPathTemplateValue == null ? "" : destinationPathTemplateValue.toString();

    StringTemplateLoader pathLoader = new StringTemplateLoader();
    pathLoader.putTemplate("sourcePath", sourcePathTemplate);
    pathLoader.putTemplate("destinationPath", destinationPathTemplate);

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    cfg.setTemplateLoader(pathLoader);

    Map<String, Object> inputs = getInputs(execution);
    String sourcePath;
    String destinationPath;

    try {
      sourcePath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate("sourcePath"), inputs);
      destinationPath = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate("destinationPath"), inputs);
    } catch (Exception e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }

    CompressFileFormat compressFormat = CompressFileFormat.valueOf(this.format.getValue(execution).toString());
    CompressFileContainer useContainer = CompressFileContainer.valueOf(this.container.getValue(execution).toString());
    String formatType = null;
    String extension = "";
    File sourceFile = createFile(sourcePath);
    File destinationFile = createFile(destinationPath);

    if (destinationFile.isDirectory()) {
      if (!destinationPath.endsWith(File.separator)) {
        destinationPath += File.separator;
      }

      destinationFile = createFile(destinationPath + sourceFile.getName() + extension);
    }

    // see: https://commons.apache.org/proper/commons-compress/limitations.html
    switch (compressFormat) {
      case BZIP2:
        formatType = CompressorStreamFactory.BZIP2;
        extension = EXT_BZIP2;
        break;

      case GZIP:
        formatType = CompressorStreamFactory.GZIP;
        extension = EXT_GZIP;
        break;

      case ZIP:
        extension = EXT_ZIP;
        break;

      default:
        break;
    }

    if (useContainer == CompressFileContainer.TAR) {
      extension = EXT_TAR + extension;
    }

    if (soureFileHasProblems(sourceFile, sourcePath,  useContainer)) {
      formatType = null;
    }

    getLogger().info("Destination: {}", destinationFile.getPath());
    getLogger().info("Source: {}", sourceFile.getPath());
    getLogger().info("Compress format: {}", compressFormat);

    if (compressFormat == CompressFileFormat.ZIP) {
      try (ZipOutputStream zipOut = createZipOutputStream(createFileOutputStream(destinationFile))) {
        zipOut.putNextEntry(new ZipEntry(sourceFile.getName()));
        filesCopy(sourceFile.toPath(), zipOut);
      } catch (Exception e) {
        throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
      }
    } else {
      getLogger().info("Format type: {}", formatType);
      getLogger().info("Use container: {}", useContainer);

      if (formatType != null) {
        if (useContainer == CompressFileContainer.NONE) {
          try (
            FileInputStream inputFile = createFileInputStream(sourceFile);
            BufferedInputStream input = createBufferedInputStream(inputFile);
            FileOutputStream outputFile = createFileOutputStream(destinationFile);
            BufferedOutputStream output = createBufferedOutputStream(outputFile);
            CompressorOutputStream<?> compress = createCompressorOutputStream(formatType, output);
          ) {
            iOUtilsCopyAndClose(input, compress);
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        } else if (useContainer == CompressFileContainer.TAR) {
          try (
            final FileOutputStream outputFile = createFileOutputStream(destinationFile);
            final BufferedOutputStream output = createBufferedOutputStream(outputFile);
            final CompressorOutputStream<?> compress = createCompressorOutputStream(formatType, output);
            final TarArchiveOutputStream tar = createTarArchiveOutputStream(compress, BLOCK_SIZE, ENCODING);
          ) {
            tar.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            addPaths(sourcePath, "", tar);

            tar.finish();
          } catch (Exception e) {
            throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
          }
        }

        getLogger().info("{} written to {} as {}", sourcePath, destinationPath, compressFormat);
      }
    }
  }

  public void setSource(Expression source) {
    this.source = source;
  }

  public void setDestination(Expression destination) {
    this.destination = destination;
  }

  public void setFormat(Expression format) {
    this.format = format;
  }

  public void setContainer(Expression container) {
    this.container = container;
  }

  @Override
  public Class<?> fromTask() {
    return CompressFileTask.class;
  }

  private void addPaths(String path, String parentPath, TarArchiveOutputStream tar) throws IOException {
    File file = createFile(path);

    setupEntryHeader(path, parentPath, tar, file);

    if (file.isDirectory()) {
      tar.closeArchiveEntry();

      for (File f : file.listFiles()) {
        addPaths(f.getAbsolutePath(), parentPath + file.getName() + File.separator, tar);
      }
    } else {
      try (BufferedInputStream input = createBufferedInputStream(createFileInputStream(file))) {
        iOUtilsCopyAndClose(input, tar);
      }

      tar.closeArchiveEntry();
    }
  }

  private void setupEntryHeader(String path, String parentPath, TarArchiveOutputStream tar, File file) throws IOException {

    final Path filePath = Path.of(path);
    final TarArchiveEntry entry = FileUtility.createTarArchiveEntry(file, parentPath + file.getName());

    if (file.isFile()) {
      entry.setSize(filesSize(filePath));
    }

    entry.setModTime(filesGetLastModifiedTime(filePath).toMillis());

    tar.putArchiveEntry(entry);
  }

  /**
   * Helper function for execute() to help solve "S3776" coding practice.
   *
   * @param sourceFile The source file.
   * @param sourcePath The source path.
   * @param useContainer The container.
   *
   * @return True if the source file has problems and false otherwise.
   */
  private boolean soureFileHasProblems(File sourceFile, String sourcePath, CompressFileContainer useContainer) {
    boolean hasProblems = false;

    if (sourceFile.exists()) {
      if (!sourceFile.canRead()) {
        getLogger().info("{} could not be read", sourcePath);
        hasProblems = true;
      }

      if (useContainer == CompressFileContainer.NONE && sourceFile.isDirectory()) {
        getLogger().info("{} is a directory and cannot be compressed when container is NONE", sourcePath);
        hasProblems = true;
      }
    } else {
      getLogger().info("{} does not exist", sourcePath);
      hasProblems = true;
    }

    return hasProblems;
  }

}
