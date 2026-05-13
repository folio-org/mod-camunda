package org.folio.rest.camunda.delegate;

import static org.folio.spring.test.mock.MockMvcConstant.JSON_OBJECT;
import static org.folio.spring.test.mock.MockMvcConstant.KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.folio.rest.workflow.enums.VariableType;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractWorkflowIODelegateTest {

  @Mock
  private DelegateExecution delegateExecution;

  @Mock
  private Expression inputVariables;

  @Mock
  private Expression outputVariable;

  private ObjectMapper objectMapper;

  @Spy
  private Impl abstractWorkflowIODelegate;

  @BeforeEach
  public void beforeEach() {
    objectMapper = Mockito.spy(JsonMapper.builder().build());
  }

  @Test
  void testGetOutputVariableWorks() throws JacksonException {
    final EmbeddedVariable embeddedVariable = new EmbeddedVariable();
    embeddedVariable.setKey(KEY);
    embeddedVariable.setAsJson(true);
    embeddedVariable.setAsTransient(true);
    embeddedVariable.setSpin(true);
    embeddedVariable.setType(VariableType.LOCAL);

    final String embeddedString = objectMapper.writeValueAsString(embeddedVariable);

    when(outputVariable.getValue(any())).thenReturn(embeddedString);
    setField(abstractWorkflowIODelegate, "outputVariable", outputVariable);
    setField(abstractWorkflowIODelegate, "objectMapper", objectMapper);

    final EmbeddedVariable responseVariable = abstractWorkflowIODelegate.getOutputVariable(delegateExecution);

    assertEquals(embeddedVariable.getKey(), responseVariable.getKey());
    assertEquals(embeddedVariable.getAsJson(), responseVariable.getAsJson());
    assertEquals(embeddedVariable.getAsTransient(), responseVariable.getAsTransient());
    assertEquals(Boolean.TRUE.equals(embeddedVariable.getSpin()), Boolean.TRUE.equals(responseVariable.getSpin()));
    assertEquals(embeddedVariable.getType(), responseVariable.getType());
  }

  @Test
  void testHasOutputVariablesReturnsTrue() {
    when(inputVariables.getValue(any())).thenReturn(JSON_OBJECT);
    setField(abstractWorkflowIODelegate, "inputVariables", inputVariables);

    assertTrue(abstractWorkflowIODelegate.hasInputVariables(delegateExecution));
  }

  @Test
  void testHasOutputVariableReturnsTrue() {
    when(outputVariable.getValue(any())).thenReturn(JSON_OBJECT);
    setField(abstractWorkflowIODelegate, "outputVariable", outputVariable);

    assertTrue(abstractWorkflowIODelegate.hasOutputVariable(delegateExecution));
  }

  @Test
  void testHasOutputVariableReturnsFalse() {
    when(outputVariable.getValue(any())).thenReturn(null);
    setField(abstractWorkflowIODelegate, "outputVariable", outputVariable);

    assertFalse(abstractWorkflowIODelegate.hasOutputVariable(delegateExecution));

    setField(abstractWorkflowIODelegate, "outputVariable", null);

    assertFalse(abstractWorkflowIODelegate.hasOutputVariable(delegateExecution));
  }

  @Test
  void testSetOutputVariableWorks() {
    setField(abstractWorkflowIODelegate, "outputVariable", null);

    abstractWorkflowIODelegate.setOutputVariable(outputVariable);
    assertEquals(outputVariable, getField(abstractWorkflowIODelegate, "outputVariable"));
  }

  private static class Impl extends AbstractWorkflowIODelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
    }

    @Override
    public Class<?> fromTask() {
      return null;
    }
  };

}
