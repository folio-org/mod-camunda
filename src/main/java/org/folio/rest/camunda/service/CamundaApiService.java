package org.folio.rest.camunda.service;

import org.folio.rest.camunda.config.SpringConfig;
import org.folio.rest.camunda.exception.RequestMissingWorkflowException;
import org.folio.rest.camunda.exception.ScriptTaskDeserializeCodeFailure;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
import org.folio.rest.camunda.listener.ScriptListener;
import org.folio.rest.camunda.utility.LoggerStream;
import org.folio.rest.workflow.model.Workflow;
import org.operaton.bpm.engine.AuthorizationException;
import org.operaton.bpm.engine.ParseException;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.ProcessEngineConfiguration;
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

  private final BpmnModelFactory bpmnModelFactory;

  private final SpringConfig springConfig;

  /**
   * Initializer.
   *
   * @param bpmnModelFactory The model factory.
   */
  public CamundaApiService(BpmnModelFactory bpmnModelFactory, SpringConfig springConfig) {

    this.bpmnModelFactory = bpmnModelFactory;
    this.springConfig = springConfig;
  }

  public Workflow deployWorkflow(Workflow workflow, String tenant)
      throws WorkflowAlreadyActiveException, ScriptTaskDeserializeCodeFailure {

    if (workflow == null) {
      throw new RequestMissingWorkflowException("deploy");
    }

    if (Boolean.TRUE.equals(workflow.getActive())) {
      throw new WorkflowAlreadyActiveException(workflow.getId());
    }

    logger.info("Deploying BPMN Model for Workflow '{}' with ID '{}'.", workflow.getName(), workflow.getId());

    BpmnModelInstance modelInstance = bpmnModelFactory.fromWorkflow(workflow);

    Bpmn.validateModel(modelInstance);
    
    // EXISTENCE CHECK HERE.

    ProcessEngine processEngine = getProcessEngine(tenant);
    RepositoryService repositoryService = processEngine.getRepositoryService();

    processEngine.getProcessEngineConfiguration()
      .setClassLoader(ScriptListener.class.getClassLoader())
      .setJobExecutorDeploymentAware(true);

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

  public Workflow undeployWorkflow(Workflow workflow, String tenant) {

    if (workflow == null) {
      throw new RequestMissingWorkflowException("undeploy");
    }

    if (Boolean.FALSE.equals(workflow.getActive())) {
      return workflow;
    }
    
    // PROCESS HERE.

    ProcessEngine processEngine = getProcessEngine(tenant);
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.deleteDeployment(workflow.getDeploymentId(), true);

    workflow.setActive(false);
    workflow.setDeploymentId(null);

    return workflow;
  }

  /**
   * A programatic initializer for process engines to be associated with each provided tenant.
   * 
   * If an engine is already present, the existing engine will be fetched.
   * 
   * If no engine is present, the database schema will checked, updated if not present, then
   * the new engine will be initialized.
   * 
   * @param tenant The tenant to be associated with the engine.
   */
  private ProcessEngine getProcessEngine(String tenant) {

    ProcessEngine processEngine = ProcessEngines.getProcessEngine(tenant);

    if (processEngine == null) {
      processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
        .setJdbcUrl(springConfig.getSpringDatasourceConfig().getUrl())
        .setJdbcUsername(springConfig.getSpringDatasourceConfig().getUsername())
        .setJdbcPassword(springConfig.getSpringDatasourceConfig().getPassword())
        .setJobExecutorActivate(true)
        .buildProcessEngine();
    }

    return processEngine;
  }

}
