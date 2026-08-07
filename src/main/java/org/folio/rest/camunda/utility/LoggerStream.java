package org.folio.rest.camunda.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

/**
 * Provide a means to expose SLF4J logging to BPMN.
 */
public class LoggerStream extends OutputStream {

  private final ByteArrayOutputStream stream;

  private LoggingEventBuilder builder;

  public LoggerStream(Logger logger, Level level) {

    builder = logger.atLevel(level);
    stream = new ByteArrayOutputStream();
  }

  @Override
  public void write(int b) throws IOException {

    stream.write(b);
  }

  @Override
  public void write(byte[] bytes, int off, int length) throws IOException {

    Objects.checkFromIndexSize(off, length, bytes.length);

    // The length == 0 condition is implicitly handled by loop bounds.
    for (int i = 0 ; i < length ; i++) {
        write(bytes[off + i]);
    }
  }

  @Override
  public void flush() {

    if (stream.size() > 0) {
      final String output = stream.toString();

      stream.reset();
      builder.log(output);
    }
  }

}
