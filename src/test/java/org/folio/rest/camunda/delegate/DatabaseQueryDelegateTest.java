package org.folio.rest.camunda.delegate;

import static org.folio.spring.test.mock.MockMvcConstant.JSON_OBJECT;
import static org.folio.spring.test.mock.MockMvcConstant.KEY;
import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.folio.rest.camunda.service.DatabaseConnectionService;
import org.folio.rest.workflow.model.DatabaseQueryTask;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseQueryDelegateTest {

  private static final String QUERY = "query";

  @Mock
  private DatabaseConnectionService connectionService;

  @Mock
  private Connection connection;

  @Mock
  private Statement statement;

  @Mock
  private FlowElement flowElementBpmn;

  @Mock
  private DelegateExecution delegateExecution;

  private Expression designationExpression;

  private Expression includeHeaderExpression;

  private Expression inputVariablesExpression;

  private Expression outputPathExpression;

  private Expression queryExpression;

  private Expression resultTypeExpression;

  private ObjectMapper objectMapper;

  @InjectMocks
  private DatabaseQueryDelegate databaseQueryDelegate;

  @BeforeEach
  void beforeEach() {
    designationExpression = mock(Expression.class);
    includeHeaderExpression = mock(Expression.class);
    inputVariablesExpression = mock(Expression.class);
    queryExpression = mock(Expression.class);
    resultTypeExpression = mock(Expression.class);

    objectMapper = new ObjectMapper();
  }

  @Test
  void testExecuteWorksWithUpdateCount() throws Exception {
    setupExecuteMocking();

    when(connectionService.getConnection(anyString())).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);
    when(statement.execute(anyString())).thenReturn(true);
    when(statement.getUpdateCount()).thenReturn(1);

    databaseQueryDelegate.execute(delegateExecution);
  }

  @Test
  void testExecuteThrowsException() {
    assertThrows(Exception.class, () -> {
      setupExecuteMocking();

      doThrow(SQLException.class).when(connectionService).getConnection(anyString());

      databaseQueryDelegate.execute(delegateExecution);
    });
  }

  @Test
  void testSetIncludeHeaderWorks() {
    setField(databaseQueryDelegate, "includeHeader", null);

    databaseQueryDelegate.setIncludeHeader(includeHeaderExpression);
    assertEquals(includeHeaderExpression, getField(databaseQueryDelegate, "includeHeader"));
  }

  @Test
  void testSetOutputPathWorks() {
    setField(databaseQueryDelegate, "outputPath", null);

    databaseQueryDelegate.setOutputPath(outputPathExpression);
    assertEquals(outputPathExpression, getField(databaseQueryDelegate, "outputPath"));
  }

  @Test
  void testSetQueryWorks() {
    setField(databaseQueryDelegate, "query", null);

    databaseQueryDelegate.setQuery(queryExpression);
    assertEquals(queryExpression, getField(databaseQueryDelegate, "query"));
  }

  @Test
  void testSetResultTypeWorks() {
    setField(databaseQueryDelegate, "resultType", null);

    databaseQueryDelegate.setResultType(resultTypeExpression);
    assertEquals(resultTypeExpression, getField(databaseQueryDelegate, "resultType"));
  }

  @Test
  void testFromTaskWorks() {
    assertEquals(DatabaseQueryTask.class, databaseQueryDelegate.fromTask());
  }

  /**
   * Provide common mocking behavior for the execute() method.
   * @throws JsonProcessingException 
   */
  private void setupExecuteMocking() throws JsonProcessingException {
    final Set<EmbeddedVariable> inputs = new HashSet<>(List.of(new EmbeddedVariable()));

    when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElementBpmn);
    when(flowElementBpmn.getName()).thenReturn(KEY);
    when(designationExpression.getValue(any())).thenReturn(VALUE);
    when(queryExpression.getValue(any())).thenReturn(QUERY);
    when(includeHeaderExpression.getValue(any())).thenReturn(JSON_OBJECT);
    when(inputVariablesExpression.getValue(any())).thenReturn(objectMapper.writeValueAsString(inputs));

    setField(databaseQueryDelegate, "designation", designationExpression);
    setField(databaseQueryDelegate, "connectionService", connectionService);
    setField(databaseQueryDelegate, "includeHeader", includeHeaderExpression);
    setField(databaseQueryDelegate, "outputPath", outputPathExpression);
    setField(databaseQueryDelegate, "query", queryExpression);
    setField(databaseQueryDelegate, "resultType", resultTypeExpression);
    setField(databaseQueryDelegate, "inputVariables", inputVariablesExpression);
    setField(databaseQueryDelegate, "objectMapper", objectMapper);
  }
}
