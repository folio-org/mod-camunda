package org.folio.rest.camunda.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.folio.rest.camunda.resolver.ScriptEngineResolver;
import org.folio.rest.camunda.service.TenantProvisioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

@ExtendWith(MockitoExtension.class)
class CamundaEngineConfigTest {

  @Mock
  TenantProvisioningService tenantProvisioningService;

  @Spy
  ProcessEngineConfigurationImpl processEngineConfiguration;

  CamundaEngineConfig camundaEngineConfig;

  @BeforeEach
  void beforeEach() {
    camundaEngineConfig = new CamundaEngineConfig(tenantProvisioningService);
  }

  @Test
  void preInitSetsScriptEngineTest() {
    setField(processEngineConfiguration, "scriptEngineResolver", null);

    camundaEngineConfig.preInit(processEngineConfiguration);

    verify(processEngineConfiguration, times(1)).setScriptEngineResolver(any(ScriptEngineResolver.class));

    assertNotNull(getField(processEngineConfiguration, "scriptEngineResolver"));
  }

}
