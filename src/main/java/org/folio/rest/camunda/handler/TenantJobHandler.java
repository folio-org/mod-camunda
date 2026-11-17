package org.folio.rest.camunda.handler;

import org.operaton.bpm.engine.impl.interceptor.CommandContext;
import org.operaton.bpm.engine.impl.jobexecutor.JobHandler;
import org.operaton.bpm.engine.impl.jobexecutor.JobHandlerConfiguration;
import org.operaton.bpm.engine.impl.persistence.entity.ExecutionEntity;

/**
 * Provide a Job handler that injects the tenant into the ThreadLocalStorage.
 *
 * This is necessary when running multiple tenants under a single schema.
 * This is less necessary when running a schema per tenant.
 *
 * However, injecting the tenant for the current running guarantees that the ThreadLocalStorage is correctly set.
 */
public class TenantJobHandler implements JobHandler {

  public static final String JOB_TYPE = "TenantJobHandler";

  @Override
  public String getType() {
    return JOB_TYPE;
  }

  @Override
  public void execute(JobHandlerConfiguration configuration, ExecutionEntity execution, CommandContext commandContext,
    String tenantId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public JobHandlerConfiguration newConfiguration(String canonicalString) {
    // TODO Auto-generated method stub
    return null;
  }

}
