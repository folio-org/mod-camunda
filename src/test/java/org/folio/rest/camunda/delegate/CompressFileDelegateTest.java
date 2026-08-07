package org.folio.rest.camunda.delegate;

import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.folio.rest.camunda.utility.FileUtility;
import org.folio.rest.workflow.enums.CompressFileContainer;
import org.folio.rest.workflow.enums.CompressFileFormat;
import org.folio.rest.workflow.model.CompressFileTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.bpm.model.bpmn.instance.FlowElement;

@ExtendWith(MockitoExtension.class)
class CompressFileDelegateTest {

  @Mock
  private DelegateExecution delegateExecution;

  @Mock
  private Expression expression;

  @Mock
  private FlowElement flowElement;

  @Spy
  private CompressFileDelegate compressFileDelegate;

  @Test
  void testSetSourceWorks() {
    setField(compressFileDelegate, "source", null);

    compressFileDelegate.setSource(expression);
    assertEquals(expression, getField(compressFileDelegate, "source"));
  }

  @Test
  void testSetDestinationWorks() {
    setField(compressFileDelegate, "destination", null);

    compressFileDelegate.setDestination(expression);
    assertEquals(expression, getField(compressFileDelegate, "destination"));
  }

  @Test
  void testSetFormatWorks() {
    setField(compressFileDelegate, "format", null);

    compressFileDelegate.setFormat(expression);
    assertEquals(expression, getField(compressFileDelegate, "format"));
  }

  @Test
  void testSetContainerWorks() {
    setField(compressFileDelegate, "container", null);

    compressFileDelegate.setContainer(expression);
    assertEquals(expression, getField(compressFileDelegate, "container"));
  }

  @Test
  void testFromTaskWorks() {
    assertEquals(CompressFileTask.class, compressFileDelegate.fromTask());
  }

  @Test
  void testExecuteBzipNoContainerWorks() throws Exception {
    setField(compressFileDelegate, "format", expression);
    setField(compressFileDelegate, "container", expression);
    setField(compressFileDelegate, "destination", expression);
    setField(compressFileDelegate, "inputVariables", null);
    setField(compressFileDelegate, "source", expression);

    try (MockedStatic<FileUtility> fileUtility = mockStatic(FileUtility.class)) {

      File sourceFile = mock(File.class);
      File destinationFile = mock(File.class);

      FileInputStream fileInputStream = mock(FileInputStream.class);
      BufferedInputStream bufferedInputStream = mock(BufferedInputStream.class);
      FileOutputStream fileOutputStream = mock(FileOutputStream.class);
      BufferedOutputStream bufferedOutputStream = mock(BufferedOutputStream.class);
      CompressorOutputStream<?> compressorOutputStream = mock(CompressorOutputStream.class);

      when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
      when(flowElement.getName()).thenReturn("delegateName");
      when(flowElement.getId()).thenReturn("delegateId");

      when(expression.getValue(delegateExecution))
        .thenReturn((Object) "sourcePathTemplate")
        .thenReturn((Object) "destinationPathTemplate")
        .thenReturn((Object) CompressFileFormat.BZIP2)
        .thenReturn((Object) CompressFileContainer.NONE);

      fileUtility.when(() -> FileUtility.createFile(any()))
        .thenReturn(sourceFile)
        .thenReturn(destinationFile)
        .thenReturn(destinationFile);

      when(destinationFile.isDirectory()).thenReturn(true);
      when(sourceFile.exists()).thenReturn(true);
      when(sourceFile.canRead()).thenReturn(true);
      when(sourceFile.isDirectory()).thenReturn(false);

      fileUtility.when(() -> FileUtility.createFileInputStream(any())).thenReturn(fileInputStream);
      fileUtility.when(() -> FileUtility.createBufferedInputStream(any())).thenReturn(bufferedInputStream);
      fileUtility.when(() -> FileUtility.createFileOutputStream(any())).thenReturn(fileOutputStream);
      fileUtility.when(() -> FileUtility.createBufferedOutputStream(any())).thenReturn(bufferedOutputStream);
      fileUtility.when(() -> FileUtility.createCompressorOutputStream(any(), any())).thenReturn(compressorOutputStream);
      // FileUtility.iOUtilsCopyAndClose() does not need to be mocked with a doNothing() for static mocks.

      compressFileDelegate.execute(delegateExecution);

      fileUtility.verify(() -> FileUtility.iOUtilsCopyAndClose(any(), any()));
    }
  }

  @Test
  void testExecuteBzipWithContainerWorks() throws Exception {
    setField(compressFileDelegate, "format", expression);
    setField(compressFileDelegate, "container", expression);
    setField(compressFileDelegate, "destination", expression);
    setField(compressFileDelegate, "inputVariables", null);
    setField(compressFileDelegate, "source", expression);

    try (
      MockedStatic<FileUtility> fileUtility = mockStatic(FileUtility.class);
      MockedStatic<Path> pathUtility = mockStatic(Path.class);
    ) {

      File destinationFile = mock(File.class);
      File directoryFile = mock(File.class);
      File regularFile = mock(File.class);
      File sourceFile = mock(File.class);
      File tarFile = mock(File.class);

      FileTime fileTime = mock(FileTime.class);

      Path filePath = mock(Path.class);

      FileInputStream fileInputStream = mock(FileInputStream.class);
      BufferedInputStream bufferedInputStream = mock(BufferedInputStream.class);
      FileOutputStream fileOutputStream = mock(FileOutputStream.class);
      BufferedOutputStream bufferedOutputStream = mock(BufferedOutputStream.class);
      CompressorOutputStream<?> compressorOutputStream = mock(CompressorOutputStream.class);
      TarArchiveEntry tarArchiveEntry = mock(TarArchiveEntry.class);
      TarArchiveOutputStream tarArchiveOutputStream = mock(TarArchiveOutputStream.class);

      when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
      when(flowElement.getName()).thenReturn("delegateName");
      when(flowElement.getId()).thenReturn("delegateId");

      when(expression.getValue(delegateExecution))
        .thenReturn((Object) "sourcePathTemplate")
        .thenReturn((Object) "destinationPathTemplate")
        .thenReturn((Object) CompressFileFormat.GZIP)
        .thenReturn((Object) CompressFileContainer.TAR);

      fileUtility.when(() -> FileUtility.createFile(any()))
        .thenReturn(sourceFile)
        .thenReturn(destinationFile)
        .thenReturn(destinationFile)
        .thenReturn(tarFile)
        .thenReturn(directoryFile)
        .thenReturn(regularFile);

      when(destinationFile.isDirectory()).thenReturn(true);
      when(sourceFile.exists()).thenReturn(true);
      when(sourceFile.canRead()).thenReturn(true);

      when(tarFile.isDirectory())
        .thenReturn(true)
        .thenReturn(false);

      final File[] tarDirectoryFiles = { directoryFile };

      when(fileTime.toMillis()).thenReturn(1L);

      when(tarFile.listFiles()).thenReturn(tarDirectoryFiles);
      when(tarFile.getName()).thenReturn(VALUE);

      when(directoryFile.getAbsolutePath()).thenReturn(VALUE);

      doNothing().when(tarArchiveEntry).setModTime(anyLong());

      doNothing().when(tarArchiveOutputStream).closeArchiveEntry();
      doNothing().when(tarArchiveOutputStream).putArchiveEntry(any());
      doNothing().when(tarArchiveOutputStream).finish();

      fileUtility.when(() -> FileUtility.createFileInputStream(any())).thenReturn(fileInputStream);
      fileUtility.when(() -> FileUtility.createBufferedInputStream(any())).thenReturn(bufferedInputStream);
      fileUtility.when(() -> FileUtility.createFileOutputStream(any())).thenReturn(fileOutputStream);
      fileUtility.when(() -> FileUtility.createBufferedOutputStream(any())).thenReturn(bufferedOutputStream);
      fileUtility.when(() -> FileUtility.createCompressorOutputStream(any(), any())).thenReturn(compressorOutputStream);
      fileUtility.when(() -> FileUtility.createTarArchiveEntry(any(), anyString())).thenReturn(tarArchiveEntry);
      fileUtility.when(() -> FileUtility.createTarArchiveOutputStream(any(), anyInt(), anyString())).thenReturn(tarArchiveOutputStream);
      fileUtility.when(() -> FileUtility.filesSize(any())).thenReturn(1L);
      fileUtility.when(() -> FileUtility.filesGetLastModifiedTime(any())).thenReturn(fileTime);
      // FileUtility.iOUtilsCopyAndClose() does not need to be mocked with a doNothing() for static mocks.

      pathUtility.when(() -> Path.of(any(URI.class))).thenReturn(filePath);

      fileUtility.when(() -> FileUtility.filesSize(any())).thenReturn(1L);

      compressFileDelegate.execute(delegateExecution);

      fileUtility.verify(() -> FileUtility.iOUtilsCopyAndClose(any(), any()));
    }
  }

}
