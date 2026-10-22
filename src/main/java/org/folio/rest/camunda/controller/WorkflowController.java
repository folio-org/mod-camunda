package org.folio.rest.camunda.controller;

import java.util.List;

import org.folio.rest.camunda.exception.ScriptTaskDeserializeCodeFailure;
import org.folio.rest.camunda.exception.WorkflowAlreadyActiveException;
import org.folio.rest.camunda.service.CamundaApiService;
import org.folio.rest.workflow.model.Workflow;
import org.folio.spring.tenant.annotation.TenantHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow-engine/workflows")
public class WorkflowController {

  private static final Logger LOG = LoggerFactory.getLogger(WorkflowController.class);

  private CamundaApiService camundaApiService;

  public WorkflowController(CamundaApiService camundaApiService) {
    this.camundaApiService = camundaApiService;
  }

  @PostMapping(value = "/activate", produces = { MediaType.APPLICATION_JSON_VALUE })
  public String activateWorkflow(@RequestBody Workflow workflow, @TenantHeader String tenant)
      throws WorkflowAlreadyActiveException, ScriptTaskDeserializeCodeFailure {
    LOG.debug("Activating Workflow: {}", workflow == null ? null : workflow.getId());
    return camundaApiService.deployWorkflow(workflow, tenant);
  }

  @PostMapping(value = "/deactivate", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<Object> deactivateWorkflows(@RequestBody List<String> ids, @TenantHeader String tenant) {
    LOG.debug("Deactivating Workflows: {}", ids);
    camundaApiService.undeployWorkflows(ids, tenant);

    // Ensure that a HTTP 204 is returned on success.
    return ResponseEntity.noContent().build();
  }
}
