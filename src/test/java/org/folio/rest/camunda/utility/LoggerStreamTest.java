package org.folio.rest.camunda.utility;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

@ExtendWith(MockitoExtension.class)
class LoggerStreamTest {

  private static final String LINE_1 = "1";
  private static final String LINE_2 = "2";
  private static final String LINE_3 = "3";
  private static final String LINES = LINE_1 + "\n" + LINE_2 + "\n" + LINE_3;

  @Mock
  private Logger logger;

  @Mock
  private LoggingEventBuilder builder;

  @Captor
  private ArgumentCaptor<String> printed;

  @Captor
  private ArgumentCaptor<Level> level;

  private LoggerStream stream;

  @BeforeEach
  void beforeEach() {

    when(logger.atLevel(Level.INFO)).thenReturn(builder);
  }

  @Test
  void loggingWorksWithDataTest() throws IOException {

    try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {

      factory.when(() -> LoggerFactory.getLogger(any(Class.class)))
        .thenReturn(logger);

      stream = new LoggerStream(logger, Level.INFO);

      stream.write(LINES.getBytes());
      stream.flush();

      verify(logger).atLevel(any());
      verify(builder).log(LINES);
    }
  }

  @Test
  void loggingWorksWithoutDataTest() {

    try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {

      factory.when(() -> LoggerFactory.getLogger(any(Class.class)))
        .thenReturn(logger);

      stream = new LoggerStream(logger, Level.INFO);

      stream.flush();

      verify(logger).atLevel(any());
      verify(builder, never()).log(anyString());
    }
  }

}

