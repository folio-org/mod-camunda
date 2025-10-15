package org.folio.rest.camunda.resolver;

import static org.camunda.bpm.engine.impl.scripting.engine.ScriptingEngines.JAVASCRIPT_SCRIPTING_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScriptEngineResolverTest {

  @InjectMocks
  private ScriptEngineResolver scriptEngineResolver;

  @Test
  void testJsScriptEngineResolverIsDefined() {
    assertNotNull(scriptEngineResolver.getJavaScriptScriptEngine(JAVASCRIPT_SCRIPTING_LANGUAGE));
  }

}
