package org.folio.rest.camunda.delegate;

import static org.folio.rest.camunda.utility.TestUtility.i;
import static org.folio.rest.workflow.enums.FileOp.COPY;
import static org.folio.rest.workflow.enums.FileOp.DELETE;
import static org.folio.rest.workflow.enums.FileOp.LINE_COUNT;
import static org.folio.rest.workflow.enums.FileOp.LIST;
import static org.folio.rest.workflow.enums.FileOp.MOVE;
import static org.folio.rest.workflow.enums.FileOp.READ;
import static org.folio.rest.workflow.enums.FileOp.READ_LINE;
import static org.folio.rest.workflow.enums.FileOp.WRITE;
import static org.folio.spring.test.mock.MockMvcConstant.JSON_ARRAY;
import static org.folio.spring.test.mock.MockMvcConstant.JSON_OBJECT;
import static org.folio.spring.test.mock.MockMvcConstant.NULL_STR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.folio.rest.camunda.service.ScriptEngineService;
import org.folio.rest.camunda.utility.FileUtility;
import org.folio.rest.workflow.enums.FileOp;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.folio.rest.workflow.model.FileTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import org.operaton.bpm.model.bpmn.instance.FlowElement;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileDelegateTest {

  private static final String EXISTS = "/exists.txt";

  private static final String NOT_DIR = "/not_dir.txt";

  @Spy
  protected JsonMapper mapper;

  @Spy
  protected RuntimeService runtimeService;

  @Mock
  private ScriptEngineService scriptEngineService;

  @Mock
  Expression inputVariables;

  @Mock
  Expression outputVariable;

  @Mock
  Expression path;

  @Mock
  Expression line;

  @Mock
  Expression op;

  @Mock
  Expression target;

  @Mock
  File file1;

  @Mock
  File file2;

  @Mock
  File fileTarget;

  @Mock
  File directory1;

  @Mock
  File directory2;

  @Mock
  DelegateExecution execution;

  @Mock
  FlowElement element;

  @InjectMocks
  FileDelegate delegate;

  private final Map<String, Object> mockData = new HashMap<>() {{
    put("data", new ArrayList<>() {{
      add("Hello, World!");
    }});
    put("simple", "Hello, World!");
    put("path", "/test/path");
    put("tenandId", "diku");
    put("timestamp", new Date().getTime());
  }};

  @BeforeEach
  void beforeEach() {

    delegate.setInputVariables(inputVariables);
    delegate.setOutputVariable(outputVariable);
    delegate.setPath(path);
    delegate.setLine(line);
    delegate.setOp(op);
    delegate.setTarget(target);
  }

  @Test
  void testFromTaskWorks() {
    assertEquals(FileTask.class, delegate.fromTask());
  }

  @ParameterizedTest
  @MethodSource("provideExecutionValues")
  void testExecute(String inputVariablesValue, String outputVariableValue, String pathValue, String lineValue,
    FileOp fileOp, String targetValue, Class<Exception> exception) throws Exception {

    when(execution.getBpmnModelElementInstance()).thenReturn(element);
    when(element.getName()).thenReturn(delegate.getClass().getSimpleName());

    when(inputVariables.getValue(any(DelegateExecution.class))).thenReturn(inputVariablesValue);

    Set<EmbeddedVariable> inputs = mapper.readValue(inputVariablesValue, new TypeReference<Set<EmbeddedVariable>>() {});

    for (EmbeddedVariable variable : inputs) {
      Object value = mockData.get(variable.getKey());
      switch (variable.getType()) {
        case LOCAL:
          when(execution.getVariableLocal(variable.getKey())).thenReturn(value);
          break;
        case PROCESS:
          when(execution.getVariable(variable.getKey())).thenReturn(value);
          break;
        default:
          break;
      }
    }

    lenient().when(outputVariable.getValue(any(DelegateExecution.class))).thenReturn(outputVariableValue);

    when(path.getValue(any(DelegateExecution.class))).thenReturn(pathValue);
    when(line.getValue(any(DelegateExecution.class))).thenReturn(lineValue);
    when(op.getValue(any(DelegateExecution.class))).thenReturn(fileOp.toString());

    lenient().when(target.getValue(any(DelegateExecution.class))).thenReturn(targetValue);

    if (Objects.nonNull(exception)) {
      assertThrows(exception, () -> delegate.execute(execution));
    } else {
      try (MockedStatic<FileUtility> utilityMock = mockStatic(FileUtility.class)) {
        utilityMock.when(() -> FileUtility.createFile(pathValue)).thenReturn(file1);
        utilityMock.when(() -> FileUtility.createFile(targetValue)).thenReturn(fileTarget);

        switch (fileOp) {
          case LIST, READ, READ_LINE, LINE_COUNT:
            if (StringUtils.isNotEmpty(pathValue)) {
              when(file1.exists()).thenReturn(true);
            }

            if (READ.equals(fileOp)) {
              utilityMock.when(() -> FileUtility.filesReadAllBytes(any())).thenReturn("".getBytes());
            }

            if (LIST.equals(fileOp) && StringUtils.isNotEmpty(pathValue)) {
              final File[] files = {
                file2,
                directory1
              };

              final File[] withDir = {
                directory2
              };

              final File[] empty = {
              };

              when(file1.isDirectory()).thenReturn(true);
              when(file1.listFiles()).thenReturn(files);

              when(file2.isFile()).thenReturn(true);
              when(file2.getAbsolutePath()).thenReturn("");

              when(directory1.isFile()).thenReturn(false);
              when(directory1.isDirectory()).thenReturn(true);
              when(directory1.listFiles()).thenReturn(withDir);

              when(directory2.isFile()).thenReturn(false);
              when(directory2.isDirectory()).thenReturn(true);
              when(directory2.listFiles()).thenReturn(empty);
            }
            break;

          case WRITE:
            break;

          case COPY:
            if (StringUtils.isNotEmpty(pathValue)) {
              when(file1.exists()).thenReturn(true);
              when(fileTarget.exists()).thenReturn(true);
            }
            break;

          case MOVE:
            if (StringUtils.isNotEmpty(pathValue)) {
              if (EXISTS.equals(pathValue)) {
                when(file1.exists()).thenReturn(true);
              } else {
                when(file1.exists()).thenReturn(false);
                when(fileTarget.exists()).thenReturn(true);
              }
            }
            break;

          case DELETE:
            if (EXISTS.equals(pathValue)) {
              when(file1.exists())
                .thenReturn(true)
                .thenReturn(false);

              when(file1.delete()).thenReturn(true);
            } else {
              when(file1.exists()).thenReturn(false);
            }

            break;

          default:
            break;
        }

        delegate.execute(execution);

        switch (fileOp) {
          case LIST, READ, READ_LINE, LINE_COUNT:
            if (StringUtils.isNotEmpty(pathValue)) {
              EmbeddedVariable output = mapper.readValue(outputVariableValue, EmbeddedVariable.class);
              switch (output.getType()) {
                case LOCAL:
                  verify(execution, times(1)).setVariableLocal(eq(output.getKey()), any());
                  break;
                case PROCESS:
                  verify(execution, times(1)).setVariable(eq(output.getKey()), any());
                  break;
                default:
                  break;
              }
            }
            break;

          case WRITE:
            utilityMock.verify(() -> FileUtility.fileUtilsWriteStringToFile(eq(file1), anyString(), any()), times(1));
            break;

          case COPY:
            if (StringUtils.isNotEmpty(pathValue)) {
              assertTrue(file1.exists());
              assertTrue(fileTarget.exists());
            }
            break;

          case MOVE:
            if (StringUtils.isNotEmpty(pathValue)) {
              if (EXISTS.equals(pathValue)) {
                assertTrue(file1.exists());
              } else {
                assertFalse(file1.exists());
                assertTrue(fileTarget.exists());
              }
            }
            break;

          case DELETE:
            assertFalse(file1.exists());
            break;

          default:
            break;
        }
      }
    }
  }

  /**
   * Helper function for parameterized test providing tests with
   *
   * @return
   *   The arguments array stream with the stream columns as:
   *     - inputVariablesValue: Set of EmbeddedVariable as JSON.
   *     - outputVariableValue: EmbeddedVariable as JSON.
   *     - pathValue:           The path of source file.
   *     - lineValue:           The line in source file.
   *     - fileOp:              The file operation, such as LIST.
   *     - targetValue:         The input variable identifier.
   *     - exception:           The exception thrown, if any.
   *
   * @throws IOException
   * @throws JacksonException
   */
  private static Stream<Arguments> provideExecutionValues() throws IOException {

    final String files = "src/test/resources/files";
    final String plainTxt = files + "/plain.txt";
    final String zero = "0";
    final String one = "1";
    final String emptyStr = "";

    final String dataTarget = "data";
    final String simpleTarget = "simple";
    final String tempPlainTxt = files + "/temp/plain.txt";
    final String tempOutput = files + "/temp/output";

    final String data = i("/output/file_task/data.json");
    final String local = i("/output/file_task/local.json");
    final String write = i("/input/file_task/write.json");
    final String writeSimple = i("/input/file_task/write_simple.json");

    return Stream.of(
      Arguments.of(JSON_ARRAY,  local,       files,        zero, LIST,       emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  local,       emptyStr,     zero, LIST,       emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  local,       NOT_DIR,      zero, LIST,       emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  data,        plainTxt,     zero, READ,       emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  data,        emptyStr,     zero, READ,       emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  data,        plainTxt,     zero, LINE_COUNT, emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  data,        plainTxt,     one,  READ_LINE,  emptyStr,     NULL_STR),
      Arguments.of(write,       JSON_OBJECT, tempOutput,   zero, WRITE,      dataTarget,   NULL_STR),
      Arguments.of(writeSimple, JSON_OBJECT, tempOutput,   zero, WRITE,      simpleTarget, NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, emptyStr,     zero, COPY,       tempPlainTxt, NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, emptyStr,     zero, MOVE,       tempPlainTxt, NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, plainTxt,     zero, COPY,       tempPlainTxt, NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, plainTxt,     zero, DELETE,     emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, EXISTS,       zero, DELETE,     emptyStr,     NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, tempPlainTxt, zero, MOVE,       plainTxt,     NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, EXISTS,       zero, MOVE,       plainTxt,     NULL_STR),
      Arguments.of(JSON_ARRAY,  JSON_OBJECT, tempOutput,   zero, DELETE,     emptyStr,     NULL_STR)
    );
  }

}
