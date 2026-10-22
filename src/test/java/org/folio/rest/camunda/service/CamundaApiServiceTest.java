package org.folio.rest.camunda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.folio.rest.camunda.exception.RequestMissingWorkflowException;
import org.folio.rest.camunda.exception.ScriptTaskDeserializeCodeFailure;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
import org.folio.rest.workflow.model.Workflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.ProcessEngineConfiguration;
import org.operaton.bpm.engine.ProcessEngines;
import org.operaton.bpm.engine.RepositoryService;
import org.operaton.bpm.engine.exception.NotValidException;
import org.operaton.bpm.engine.repository.Deployment;
import org.operaton.bpm.engine.repository.DeploymentBuilder;
import org.operaton.bpm.engine.repository.ProcessDefinition;
import org.operaton.bpm.engine.repository.ProcessDefinitionQuery;
import org.operaton.bpm.model.bpmn.Bpmn;
import org.operaton.bpm.model.bpmn.BpmnModelInstance;

@ExtendWith(MockitoExtension.class)
class CamundaApiServiceTest {

  private static final String TENANT = "testTenant";

  private static final String DEPLOYMENT_ID = "deploymentId";

  @Mock
  private BpmnModelFactory bpmnModelFactory;

  @InjectMocks
  private CamundaApiService camundaApiService;

  @Mock
  private BpmnModelInstance modelInstance;

  @Mock
  private ProcessEngine processEngine;

  @Mock
  ProcessEngineConfiguration processEngineConfiguration;

  @Mock
  private RepositoryService repositoryService;

  @Mock
  private DeploymentBuilder deploymentBuilder;

  @Mock
  private Deployment deployment;

  @Mock
  private ProcessDefinitionQuery processDefinitionQuery;

  @Mock
  private ProcessDefinition processDefinition;

  private Workflow workflow;

  private List<ProcessDefinition> processDefinitionList;

  @BeforeEach
  void setUp() throws ScriptTaskDeserializeCodeFailure {
    workflow = new Workflow();
    workflow.setActive(false);
    workflow.setName("Test Workflow");
    workflow.setTenantId(TENANT);

    processDefinitionList = List.of(processDefinition);

    lenient().when(bpmnModelFactory.fromWorkflow(workflow)).thenReturn(modelInstance);
    lenient().when(processEngine.getRepositoryService()).thenReturn(repositoryService);
    lenient().when(repositoryService.createDeployment()).thenReturn(deploymentBuilder);
    lenient().when(repositoryService.createProcessDefinitionQuery()).thenReturn(processDefinitionQuery);
    lenient().when(deploymentBuilder.name(anyString())).thenReturn(deploymentBuilder);
    lenient().when(deploymentBuilder.addModelInstance(anyString(), any(BpmnModelInstance.class)))
      .thenReturn(deploymentBuilder);
    lenient().when(deploymentBuilder.source(anyString())).thenReturn(deploymentBuilder);
    lenient().when(deploymentBuilder.tenantId(anyString())).thenReturn(deploymentBuilder);
    lenient().when(deploymentBuilder.deploy()).thenReturn(deployment);
    lenient().when(deployment.getId()).thenReturn(DEPLOYMENT_ID);
    lenient().when(processEngine.getProcessEngineConfiguration()).thenReturn(processEngineConfiguration);
    lenient().when(processEngineConfiguration.setClassLoader(any())).thenReturn(processEngineConfiguration);
    lenient().when(processDefinitionQuery.deploymentId(any())).thenReturn(processDefinitionQuery);
    lenient().when(processDefinitionQuery.tenantIdIn(any())).thenReturn(processDefinitionQuery);
    lenient().when(processDefinitionQuery.active()).thenReturn(processDefinitionQuery);
    lenient().when(processDefinitionQuery.list()).thenReturn(processDefinitionList);
    lenient().when(processDefinition.getId()).thenReturn(DEPLOYMENT_ID);
  }

  @Test
  void testDeployWorkflow() throws ScriptTaskDeserializeCodeFailure, WorkflowAlreadyActiveException {
    try (
        MockedStatic<ProcessEngines> utilityProcessEngines = mockStatic(ProcessEngines.class);
        MockedStatic<Bpmn> utilityBpmn = mockStatic(Bpmn.class);
      ) {
      utilityProcessEngines.when(ProcessEngines::getDefaultProcessEngine).thenReturn(processEngine);
      utilityBpmn.when(() -> Bpmn.validateModel(modelInstance)).thenAnswer(answer -> null);

      String result = camundaApiService.deployWorkflow(workflow, TENANT);

      assertNotNull(result);
      assertEquals(DEPLOYMENT_ID, result);

      workflow.getDeploymentIds().add(result);
      workflow.setActive(true);

      verify(bpmnModelFactory).fromWorkflow(workflow);
      verify(repositoryService).createDeployment();
    }
  }

  @Test
  void testDeployWorkflowException() {
    try (
        MockedStatic<ProcessEngines> utilityProcessEngines = mockStatic(ProcessEngines.class);
        MockedStatic<Bpmn> utilityBpmn = mockStatic(Bpmn.class);
    ) {
      utilityProcessEngines.when(ProcessEngines::getDefaultProcessEngine).thenReturn(processEngine);
      utilityBpmn.when(() -> Bpmn.validateModel(modelInstance)).thenAnswer(answer -> null);

      when(deploymentBuilder.deploy()).thenThrow(new NotValidException());

      assertThrows(NotValidException.class, () -> camundaApiService.deployWorkflow(workflow, TENANT));
    }
  }

  @Test
  void testDeployWorkflowMissingWOrkflowException() {
    assertThrows(RequestMissingWorkflowException.class, () -> camundaApiService.deployWorkflow(null, TENANT));
  }

  @Test
  void testUndeployWorkflowMissingWOrkflowException() {
    assertThrows(RequestMissingWorkflowException.class, () -> camundaApiService.undeployWorkflows(null, TENANT));
  }

  @Test
  void testDeployWorkflowAlreadyActive() throws WorkflowAlreadyActiveException, ScriptTaskDeserializeCodeFailure {
    testDeployWorkflow();

    assertThrows(WorkflowAlreadyActiveException.class, () -> camundaApiService.deployWorkflow(workflow, TENANT));
  }

  @Test
  void testUndeployWorkflow() throws ScriptTaskDeserializeCodeFailure, WorkflowAlreadyActiveException {
    testDeployWorkflow();

    try (MockedStatic<ProcessEngines> utilityProcessEngines = mockStatic(ProcessEngines.class)) {
      utilityProcessEngines.when(ProcessEngines::getDefaultProcessEngine).thenReturn(processEngine);

      camundaApiService.undeployWorkflows(workflow.getDeploymentIds(), TENANT);

      verify(repositoryService).deleteDeployment(DEPLOYMENT_ID, true);
    }
  }

  @Test
  void testUndeployWorkflowNotActive() {
    try (MockedStatic<ProcessEngines> utilityProcessEngines = mockStatic(ProcessEngines.class)) {
      utilityProcessEngines.when(ProcessEngines::getDefaultProcessEngine).thenReturn(processEngine);

      camundaApiService.undeployWorkflows(workflow.getDeploymentIds(), TENANT);

      verify(repositoryService, never()).deleteDeployment(anyString(), anyBoolean());
    }
  }
}
