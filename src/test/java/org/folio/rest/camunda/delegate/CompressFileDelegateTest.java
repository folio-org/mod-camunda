package org.folio.rest.camunda.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.folio.rest.camunda.utility.FileUtility;
import org.folio.rest.workflow.enums.CompressFileContainer;
import org.folio.rest.workflow.enums.CompressFileFormat;
import org.folio.rest.workflow.model.CompressFileTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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

    try (MockedStatic<FileUtility> fileUtility = Mockito.mockStatic(FileUtility.class)) {

      File sourceFile = Mockito.mock(File.class);
      File destinationFile = Mockito.mock(File.class);
      FileInputStream fileInputStream = Mockito.mock(FileInputStream.class);
      BufferedInputStream bufferedInputStream = Mockito.mock(BufferedInputStream.class);
      FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);
      BufferedOutputStream bufferedOutputStream = Mockito.mock(BufferedOutputStream.class);
      CompressorOutputStream<?> compressorOutputStream = Mockito.mock(CompressorOutputStream.class);

      when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
      when(flowElement.getName()).thenReturn("determineStartTime");

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

}
