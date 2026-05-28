package org.folio.rest.camunda.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.nio.file.attribute.FileTime;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.h2.util.IOUtils;

/**
 * Provide common file creation tasks to help make writing unit tests easier via mocking.
 */
public class FileUtility {

  /**
   * Prevent instantiation of utility.
   */
  private FileUtility() {
    // Must do nothing.
  }

  /**
   * Wrap new BufferedInputStream() method in such a way that unit tests can be more easily written.
   *
   * @param stream The InputStream.
   *
   * @return The new BufferedInputStream.
   */
  public static BufferedInputStream createBufferedInputStream(InputStream stream) {

    return new BufferedInputStream(stream);
  }

  /**
   * Wrap new FileOutputStream() method in such a way that unit tests can be more easily written.
   *
   * @param file The file.
   *
   * @return The new FileOutputStream.
   *
   * @throws FileNotFoundException On error.
   */
  public static FileOutputStream createFileOutputStream(File file) throws FileNotFoundException {

    return new FileOutputStream(file);
  }

  /**
   * Wrap new CompressorStreamFactory().createCompressorOutputStream() method in such a way that unit tests can be more easily written.
   *
   * @param name   The compressor type name.
   * @param stream The OutputStream.
   *
   * @return The new CompressorOutputStream.
   *
   * @throws CompressorException On error.
   */
  public static CompressorOutputStream<? extends OutputStream> createCompressorOutputStream(String name, OutputStream stream) throws CompressorException {

    return new CompressorStreamFactory().createCompressorOutputStream(name, stream);
  }

  /**
   * Wrap new BufferedOutputStream() method in such a way that unit tests can be more easily written.
   *
   * @param stream The OutputStream.
   *
   * @return The new BufferedOutputStream.
   */
  public static BufferedOutputStream createBufferedOutputStream(OutputStream stream) {

    return new BufferedOutputStream(stream);
  }

  /**
   * Wrap new File() method in such a way that unit tests can be more easily written.
   *
   * @param path The file path.
   *
   * @return The new File.
   */
  public static File createFile(String path) {

    return new File(path);
  }

  /**
   * Wrap new FileInputStream() method in such a way that unit tests can be more easily written.
   *
   * @param file The file.
   *
   * @return The new FileInputStream.
   *
   * @throws FileNotFoundException On error.
   */
  public static FileInputStream createFileInputStream(File file) throws FileNotFoundException {

    return new FileInputStream(file);
  }

  /**
   * Wrap new TarArchiveOutputStream() method in such a way that unit tests can be more easily written.
   *
   * @param stream     The OutputStream.
   * @param blockSize  the block size to use
   * @param recordSize the record size to use. Must be 512 bytes.
   *
   * @return The new TarArchiveOutputStream.
   */
  public static TarArchiveOutputStream createTarArchiveOutputStream(OutputStream stream, int blockSize, String charset) {

    return new TarArchiveOutputStream(stream, blockSize, charset);
  }

  /**
   * Wrap new ZipOutputStream() method in such a way that unit tests can be more easily written.
   *
   * @param stream The OutputStream.
   *
   * @return The new ZipOutputStream.
   */
  public static ZipOutputStream createZipOutputStream(OutputStream stream) {

    return new ZipOutputStream(stream);
  }

  /**
   * Wrap FileUtils.copyFile() method in such a way that unit tests can be more easily written.
   *
   * @param input  The file to read.
   * @param output The file to write.
   *
   * @throws IOException On error.
   */
  public static void fileUtilsCopyFile(File input, File output) throws IOException {

    FileUtils.copyFile(input, output);
  }

  /**
   * Wrap Files.copy() method in such a way that unit tests can be more easily written.
   *
   * @param path    The path to the file.
   * @param options Additional options.
   *
   * @return The total bytes written.
   *
   * @throws IOException On error.
   */
  public static long filesCopy(Path source, OutputStream stream) throws IOException {

    return Files.copy(source, stream);
  }

  /**
   * Wrap Files.getLastModifiedTime() method in such a way that unit tests can be more easily written.
   *
   * @param path    The path to the file.
   * @param options Additional options.
   *
   * @return The file size.
   *
   * @throws IOException On error.
   */
  public static FileTime filesGetLastModifiedTime(Path path, LinkOption... options) throws IOException {

    return Files.getLastModifiedTime(path, options);
  }

  /**
   * Wrap Files.size() method in such a way that unit tests can be more easily written.
   *
   * @param path The path to the file.
   *
   * @return The file size.
   *
   * @throws IOException On error.
   */
  public static long filesSize(Path path) throws IOException {

    return Files.size(path);
  }

  /**
   * Wrap IOUtils.copyAndClose() method in such a way that unit tests can be more easily written.
   *
   * @param input  The stream to read.
   * @param output The stream to write.
   *
   * @return The total bytes copied.
   *
   * @throws IOException On error.
   */
  public static long iOUtilsCopyAndClose(InputStream input, OutputStream output) throws IOException {

    return IOUtils.copyAndClose(input, output);
  }

}
