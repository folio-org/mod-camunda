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

import org.folio.rest.workflow.enums.VariableType;
import org.folio.rest.workflow.model.EmbeddedVariable;
import org.folio.spring.test.helper.MapperHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.Expression;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class AbstractWorkflowOutputDelegateTest {

  @Mock
  private DelegateExecution delegateExecution;

  @Mock
  private Expression outputVariable;

  private JsonMapper mapper;

  @Spy
  private Impl abstractWorkflowOutputDelegate;

  @BeforeEach
  void beforeEach() {
    mapper = Mockito.spy(MapperHelper.build());
  }

  @Test
  void testGetOutputVariableWorks() throws JacksonException {
    final EmbeddedVariable embeddedVariable = new EmbeddedVariable();
    embeddedVariable.setKey(KEY);
    embeddedVariable.setAsJson(true);
    embeddedVariable.setAsTransient(true);
    embeddedVariable.setSpin(true);
    embeddedVariable.setType(VariableType.LOCAL);

    final String embeddedValue = mapper.writeValueAsString(embeddedVariable);

    when(outputVariable.getValue(any())).thenReturn(embeddedValue);
    setField(abstractWorkflowOutputDelegate, "outputVariable", outputVariable);
    setField(abstractWorkflowOutputDelegate, "mapper", mapper);

    final EmbeddedVariable responseVariable = abstractWorkflowOutputDelegate.getOutputVariable(delegateExecution);

    assertEquals(embeddedVariable.getKey(), responseVariable.getKey());
    assertEquals(embeddedVariable.getAsJson(), responseVariable.getAsJson());
    assertEquals(embeddedVariable.getAsTransient(), responseVariable.getAsTransient());
    assertEquals(Boolean.TRUE.equals(embeddedVariable.getSpin()), Boolean.TRUE.equals(responseVariable.getSpin()));
    assertEquals(embeddedVariable.getType(), responseVariable.getType());
  }

  @Test
  void testHasOutputVariableReturnsTrue() {
    when(outputVariable.getValue(any())).thenReturn(JSON_OBJECT);
    setField(abstractWorkflowOutputDelegate, "outputVariable", outputVariable);

    assertTrue(abstractWorkflowOutputDelegate.hasOutputVariable(delegateExecution));
  }

  @Test
  void testHasOutputVariableReturnsFalse() {
    when(outputVariable.getValue(any())).thenReturn(null);
    setField(abstractWorkflowOutputDelegate, "outputVariable", outputVariable);

    assertFalse(abstractWorkflowOutputDelegate.hasOutputVariable(delegateExecution));

    setField(abstractWorkflowOutputDelegate, "outputVariable", null);

    assertFalse(abstractWorkflowOutputDelegate.hasOutputVariable(delegateExecution));
  }

  @Test
  void testSetOutputVariableWorks() {
    setField(abstractWorkflowOutputDelegate, "outputVariable", null);

    abstractWorkflowOutputDelegate.setOutputVariable(outputVariable);
    assertEquals(outputVariable, getField(abstractWorkflowOutputDelegate, "outputVariable"));
  }

  private static class Impl extends AbstractWorkflowOutputDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      // This is a test and should not do anything.
    }

    @Override
    public Class<?> fromTask() {
      return null;
    }
  }

}
