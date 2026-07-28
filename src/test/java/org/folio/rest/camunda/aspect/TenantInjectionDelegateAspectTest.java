package org.folio.rest.camunda.aspect;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.folio.spring.tenant.exception.NoTenantException;
import org.folio.spring.tenant.properties.TenantProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.operaton.bpm.engine.delegate.DelegateTask;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TenantInjectionDelegateAspectTest {

  private static final String MOCK_TENANT = "diku";

  @Mock
  private DelegateExecution execution;

  @Mock
  private DelegateTask task;

  @Mock
  private TenantProperties tenantProperties;

  @InjectMocks
  private TenantInjectionDelegateAspect tenantInjectionDelegateAspect;

  @Test
  void testBeforeDelegateExecution() {
    when(execution.getTenantId()).thenReturn(MOCK_TENANT);
    tenantInjectionDelegateAspect.beforeDelegateExecution(execution);

    verify(execution).getTenantId();

    when(execution.getTenantId()).thenReturn(null);
    when(tenantProperties.isForceTenant()).thenReturn(true);
    assertThrows(NoTenantException.class, () -> tenantInjectionDelegateAspect.beforeDelegateExecution(execution));
  }

  @Test
  void testBeforeExecutionListenerNotify() {
    when(execution.getTenantId()).thenReturn(MOCK_TENANT);
    tenantInjectionDelegateAspect.beforeExecutionListenerNotify(execution);

    verify(execution).getTenantId();

    when(execution.getTenantId()).thenReturn(null);
    when(tenantProperties.isForceTenant()).thenReturn(true);
    assertThrows(NoTenantException.class, () -> tenantInjectionDelegateAspect.beforeExecutionListenerNotify(execution));
  }

  @Test
  void testBeforeTaskListenerNotify() {
    when(task.getTenantId()).thenReturn(MOCK_TENANT);
    tenantInjectionDelegateAspect.beforeTaskListenerNotify(task);

    verify(task).getTenantId();

    when(task.getTenantId()).thenReturn(null);
    when(tenantProperties.isForceTenant()).thenReturn(true);
    assertThrows(NoTenantException.class, () -> tenantInjectionDelegateAspect.beforeTaskListenerNotify(task));
  }

}
