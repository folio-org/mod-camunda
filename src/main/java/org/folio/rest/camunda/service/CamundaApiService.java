package org.folio.rest.camunda.service;

import java.util.List;

import org.folio.rest.camunda.exception.RequestMissingWorkflowException;
import org.folio.rest.camunda.exception.ScriptTaskDeserializeCodeFailure;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
import org.folio.rest.camunda.listener.ScriptListener;
import org.folio.rest.camunda.utility.LoggerStream;
import org.folio.rest.workflow.model.Workflow;
import org.operaton.bpm.engine.AuthorizationException;
import org.operaton.bpm.engine.ParseException;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.ProcessEngines;
import org.operaton.bpm.engine.RepositoryService;
import org.operaton.bpm.engine.exception.NotFoundException;
import org.operaton.bpm.engine.exception.NotValidException;
import org.operaton.bpm.engine.repository.Deployment;
import org.operaton.bpm.engine.repository.ProcessDefinition;
import org.operaton.bpm.model.bpmn.Bpmn;
import org.operaton.bpm.model.bpmn.BpmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

@Service
public class CamundaApiService {

  private static final Logger logger = LoggerFactory.getLogger(CamundaApiService.class);

  private static final LoggerStream debugStream = new LoggerStream(logger, Level.DEBUG);

  private BpmnModelFactory bpmnModelFactory;

  /**
   * Initializer.
   *
   * @param bpmnModelFactory The model factory.
   */
  public CamundaApiService(BpmnModelFactory bpmnModelFactory) {

    this.bpmnModelFactory = bpmnModelFactory;
  }

  public String deployWorkflow(Workflow workflow, String tenant)
      throws WorkflowAlreadyActiveException, ScriptTaskDeserializeCodeFailure {

    if (workflow == null || tenant == null || !tenant.equals(workflow.getTenantId())) {
      throw new RequestMissingWorkflowException("deploy");
    }

    if (Boolean.TRUE.equals(workflow.getActive())) {
      throw new WorkflowAlreadyActiveException(workflow.getId());
    }

    logger.info("Deploying BPMN Model for Workflow '{}' with ID '{}'.", workflow.getName(), workflow.getId());

    BpmnModelInstance modelInstance = bpmnModelFactory.fromWorkflow(workflow);

    Bpmn.validateModel(modelInstance);

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();

    processEngine.getProcessEngineConfiguration()
      .setClassLoader(ScriptListener.class.getClassLoader())
      .setJobExecutorDeploymentAware(true);

    try {
      Deployment deployment = repositoryService.createDeployment().name(workflow.getName())
        .addModelInstance(workflow.getName().replace(" ", "") + ".bpmn", modelInstance)
        .source("mod-workflow")
        .tenantId(tenant).deploy();

      return deployment.getId();
    } catch (NotFoundException | NotValidException | ParseException | AuthorizationException e) {
      Bpmn.writeModelToStream(debugStream, modelInstance);

      throw e;
    }
  }

  public void undeployWorkflows(List<String> ids, String tenant) {

    if (ids == null) {
      throw new RequestMissingWorkflowException("undeploy");
    }

    if (ids.isEmpty()) {
      return;
    }

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();

    for (String id : ids) {

      final List<ProcessDefinition> activeProcesses = repositoryService
        .createProcessDefinitionQuery()
        .deploymentId(id)
        .tenantIdIn(tenant)
        .active()
        .list();

      activeProcesses.forEach(process -> {
        repositoryService.deleteDeployment(process.getId(), true);
      });
    }
    
  }

}
