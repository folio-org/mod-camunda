package org.folio.rest.camunda.utility;

import static org.folio.spring.test.mock.MockMvcConstant.INT_VALUE;
import static org.folio.spring.test.mock.MockMvcConstant.LONG_VALUE;
import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.h2.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileUtilityTest {

  @Test
  void createBufferedInputStreamTest() {

    try (MockedConstruction<BufferedInputStream> construct = mockConstruction(BufferedInputStream.class)) {

      final InputStream arg1 = mock(InputStream.class);

      assertNotNull(FileUtility.createBufferedInputStream(arg1));
    }
  }

  @Test
  void createFileOutputStreamTest() throws FileNotFoundException {

    try (MockedConstruction<FileOutputStream> construct = mockConstruction(FileOutputStream.class)) {

      final File arg1 = mock(File.class);

      assertNotNull(FileUtility.createFileOutputStream(arg1));
    }
  }

  @Test
  void createCompressorOutputStreamTest() throws CompressorException {

    final CompressorOutputStream<?> stream = mock(CompressorOutputStream.class);

    try (MockedConstruction<CompressorStreamFactory> construct = mockConstruction(
      CompressorStreamFactory.class,
      (mock, context) -> {
        when(mock.createCompressorOutputStream(anyString(), any(OutputStream.class))).thenAnswer(invocation -> {
          return stream;
        });
      }
    )) {

      final OutputStream arg1 = mock(OutputStream.class);

      assertNotNull(FileUtility.createCompressorOutputStream(VALUE, arg1));
    }
  }

  @Test
  void createBufferedOutputStreamTest() {

    try (MockedConstruction<FileOutputStream> construct = mockConstruction(FileOutputStream.class)) {

      final OutputStream arg1 = mock(OutputStream.class);

      assertNotNull(FileUtility.createBufferedOutputStream(arg1));
    }
  }

  @Test
  void createFileTest() {

    try (MockedConstruction<File> construct = mockConstruction(File.class)) {

      assertNotNull(FileUtility.createFile(VALUE));
    }
  }

  @Test
  void createFileInputStreamTest() throws FileNotFoundException {

    try (MockedConstruction<FileInputStream> construct = mockConstruction(FileInputStream.class)) {

      final File arg1 = mock(File.class);

      assertNotNull(FileUtility.createFileInputStream(arg1));
    }
  }

  @Test
  void createTarArchiveEntryTest() {

    try (MockedConstruction<TarArchiveEntry> construct = mockConstruction(TarArchiveEntry.class)) {

      final File arg1 = mock(File.class);

      assertNotNull(FileUtility.createTarArchiveEntry(arg1, VALUE));
    }
  }

  @Test
  void createTarArchiveOutputStreamTest() {

    try (MockedConstruction<TarArchiveOutputStream> construct = mockConstruction(TarArchiveOutputStream.class)) {

      final OutputStream arg1 = mock(OutputStream.class);

      assertNotNull(FileUtility.createTarArchiveOutputStream(arg1, INT_VALUE, VALUE));
    }
  }

  @Test
  void createZipOutputStreamTest() {

    try (MockedConstruction<ZipOutputStream> construct = mockConstruction(ZipOutputStream.class)) {

      final OutputStream arg1 = mock(OutputStream.class);

      assertNotNull(FileUtility.createZipOutputStream(arg1));
    }
  }

  @Test
  void fileUtilsCopyFileTest() throws IOException {

    try (MockedStatic<FileUtils> utility = mockStatic(FileUtils.class)) {

      final File arg1 = mock(File.class);
      final File arg2 = mock(File.class);

      FileUtility.fileUtilsCopyFile(arg1, arg2);

      utility.verify(() -> FileUtils.copyFile(arg1, arg2));
    }
  }

  @Test
  void filesCopyTest() throws IOException {

    try (MockedStatic<Files> utility = mockStatic(Files.class)) {

      final Path arg1 = mock(Path.class);
      final OutputStream arg2 = mock(OutputStream.class);

      FileUtility.filesCopy(arg1, arg2);

      utility.verify(() -> Files.copy(arg1, arg2));
    }
  }

  @Test
  void filesGetLastModifiedTimeTest() throws IOException {

    try (MockedStatic<Files> utility = mockStatic(Files.class)) {

      final Path arg1 = mock(Path.class);
      final LinkOption arg2 = mock(LinkOption.class);

      FileUtility.filesGetLastModifiedTime(arg1, arg2);

      utility.verify(() -> Files.getLastModifiedTime(arg1, arg2));
    }
  }

  @Test
  void filesSizeTest() throws IOException {

    try (MockedStatic<Files> utility = mockStatic(Files.class)) {

      final Path arg1 = mock(Path.class);

      utility.when(() -> Files.size(any())).thenReturn(LONG_VALUE);

      final long result = FileUtility.filesSize(arg1);

      utility.verify(() -> Files.size(arg1));

      assertEquals(LONG_VALUE, result);
    }
  }

  @Test
  void iOUtilsCopyAndCloseTest() throws IOException {

    try (MockedStatic<IOUtils> utility = mockStatic(IOUtils.class)) {

      final InputStream arg1 = mock(InputStream.class);
      final OutputStream arg2 = mock(OutputStream.class);

      utility.when(() -> IOUtils.copyAndClose(any(), any())).thenReturn(LONG_VALUE);

      final long result = FileUtility.iOUtilsCopyAndClose(arg1, arg2);

      utility.verify(() -> IOUtils.copyAndClose(arg1, arg2));

      assertEquals(LONG_VALUE, result);
    }
  }

}
