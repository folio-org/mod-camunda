package org.folio.rest.camunda.listener;

import static org.folio.spring.test.mock.MockMvcConstant.VALUE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.folio.rest.camunda.provider.LoggerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.ProcessEngineServices;
import org.operaton.bpm.engine.RepositoryService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.repository.ProcessDefinition;
import org.operaton.bpm.engine.repository.ProcessDefinitionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class LogListenerTest {

  private static final Logger logger = spy(LoggerFactory.getLogger(LogListener.class));

  @Mock
  LoggerProvider provider;

  @Mock
  DelegateExecution execution;

  @Mock
  ProcessEngineServices processEngineServices;

  @Mock
  RepositoryService repositoryService;

  @Mock
  ProcessDefinitionQuery processDefinitionQuery;

  @Mock
  ProcessDefinition processDefinition;

  private LogListener listener;

  @BeforeEach
  void beforeEach() {

    when(provider.forClass(LogListener.class)).thenReturn(logger);

    listener = new LogListener(provider);
  }

  @Test
  void notifyEnvDefaultsTest() throws Exception {

    when(execution.getCurrentActivityId()).thenReturn(VALUE);
    when(execution.getCurrentActivityName()).thenReturn(VALUE);
    when(execution.getProcessDefinitionId()).thenReturn(VALUE);
    when(execution.getProcessEngineServices()).thenReturn(processEngineServices);

    when(processEngineServices.getRepositoryService()).thenReturn(repositoryService);
    when(repositoryService.createProcessDefinitionQuery()).thenReturn(processDefinitionQuery);
    when(processDefinitionQuery.processDefinitionId(anyString())).thenReturn(processDefinitionQuery);
    when(processDefinitionQuery.singleResult()).thenReturn(processDefinition);
    when(processDefinition.getName()).thenReturn(VALUE);

    listener.notify(execution);

    verify(logger).info(anyString(), anyString(), anyString(), anyString(), anyString());
  }

}
