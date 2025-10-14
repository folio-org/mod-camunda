package org.folio.rest.camunda.resolver;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.camunda.bpm.engine.impl.scripting.engine.DefaultScriptEngineResolver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

/**
 * A custom script engine resolver to allow for setting configuration.
 *
 * This can be used to configure any supported scripting engine.
 *
 * Note: To enable Nashorn compatibility, enable the builders experimental options  allowExperimentalOptions(true).
 *       Then set .option("js.nashorn-compat", "true").
 */
public class ScriptEngineResolver extends DefaultScriptEngineResolver {

  public ScriptEngineResolver(ScriptEngineManager scriptEngineManager) {
    super(scriptEngineManager);
  }

  @Override
  protected ScriptEngine getJavaScriptScriptEngine(String language) {
    Engine engine = Engine.newBuilder()
      .allowExperimentalOptions(true)
      .option("engine.WarnInterpreterOnly", "false")
      .build();

    Context.Builder builder = Context.newBuilder("js")
      // Make sure GraalVM JS can provide access to the host and can lookup classes.
      .allowHostClassLookup(s -> true);

    return GraalJSScriptEngine.create(engine, builder);
  }
}
