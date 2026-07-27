package org.folio.rest.camunda.service;

import org.folio.rest.camunda.exception.ScriptTaskDeserializeCodeFailure;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
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

  public Workflow deployWorkflow(Workflow workflow, String tenant)
      throws WorkflowAlreadyActiveException, ScriptTaskDeserializeCodeFailure {

    if (Boolean.TRUE.equals(workflow.getActive())) {
      throw new WorkflowAlreadyActiveException(workflow.getId());
    }

    logger.info("Deploying BPMN Model for Workflow '{}' with ID '{}'.", workflow.getName(), workflow.getId());

    BpmnModelInstance modelInstance = bpmnModelFactory.fromWorkflow(workflow);

    Bpmn.validateModel(modelInstance);

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();

    try {
      Deployment deployment = repositoryService.createDeployment().name(workflow.getName())
        .addModelInstance(workflow.getName().replace(" ", "") + ".bpmn", modelInstance)
        .source("mod-workflow")
        .tenantId(tenant).deploy();

      String deploymentId = deployment.getId();

      workflow.setActive(true);
      workflow.setDeploymentId(deploymentId);
    } catch (NotFoundException | NotValidException | ParseException | AuthorizationException e) {
      Bpmn.writeModelToStream(debugStream, modelInstance);

      throw e;
    }

    return workflow;
  }

  public Workflow undeployWorkflow(Workflow workflow) {
    if (Boolean.FALSE.equals(workflow.getActive())) {
      return workflow;
    }

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.deleteDeployment(workflow.getDeploymentId(), true);

    workflow.setActive(false);
    workflow.setDeploymentId(null);

    return workflow;
  }

}
