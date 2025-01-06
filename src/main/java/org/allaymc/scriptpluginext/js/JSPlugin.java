package org.allaymc.scriptpluginext.js;

import lombok.SneakyThrows;
import org.allaymc.scriptpluginext.ScriptPlugin;
import org.allaymc.scriptpluginext.ScriptPluginDescriptor;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.plugin.PluginContainer;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

/**
 * @author daoge_cmd
 */
public class JSPlugin extends Plugin implements ScriptPlugin {

    protected Context context;
    protected Value export;
    protected JSPluginProxyLogger proxyLogger;

    @Override
    public void setPluginContainer(PluginContainer pluginContainer) {
        super.setPluginContainer(pluginContainer);
        this.proxyLogger = new JSPluginProxyLogger(pluginLogger);
    }

    @SneakyThrows
    @Override
    public void onLoad() {
        var debugPort = ((ScriptPluginDescriptor) pluginContainer.descriptor()).getDebugPort();
        var cbd = Context.newBuilder("js")
                .allowIO(IOAccess.ALL)
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true)
                // Use strict mode by default
                .option("js.strict", "true")
                // The js.esm-eval-returns-exports option (false by default) can be used to expose
                // the ES module namespace exported object to a Polyglot Context. This can be handy
                // when an ES module is used directly from Java
                .option("js.esm-eval-returns-exports", "true")
                // Enable CommonJS experimental support.
                .option("js.commonjs-require", "true")
                // Directory where the NPM modules to be loaded are located.
                .option("js.commonjs-require-cwd", pluginContainer.loader().getPluginPath().toString());
                // TODO: js.commonjs-core-modules-replacements
        if (debugPort > 0) {
            pluginLogger.info("Debug mode for javascript plugin {} is enabled. Port: {}", pluginContainer.descriptor().getName(), debugPort);
            // Debug mode is enabled
            cbd.option("inspect", String.valueOf(debugPort))
                    // The custom path that generates the connection URL
                    .option("inspect.Path", pluginContainer.descriptor().getName())
                    // The list of directories or ZIP/JAR files representing the source path. When the inspected
                    // application contains relative references to source files, their content is loaded from
                    // locations resolved with respect to this source path. It is useful during LLVM debugging,
                    // for instance. The paths are delimited by : on UNIX systems and by ; on MS Windows
                    .option("inspect.SourcePath", pluginContainer.loader().getPluginPath().toFile().getAbsolutePath())
                    // Do not suspend on the first line of the application code
                    .option("inspect.Suspend", "false")
                    // When true, internal sources are inspected as well. Internal sources may provide
                    // language implementation details
                    .option("inspect.Internal", "true");
        }
        context = cbd.build();
        initGlobalMembers();
        var entranceJsFileName = pluginContainer.descriptor().getEntrance();
        var path = pluginContainer.loader().getPluginPath().resolve(entranceJsFileName);
        export = context.eval(
                Source.newBuilder("js", path.toFile())
                        .name(entranceJsFileName)
                        // ECMAScript modules can be loaded in a Context simply by evaluating the module sources. GraalJS loads
                        // ECMAScript modules based on their file extension. Therefore, any ECMAScript module should have file name
                        // extension .mjs. Alternatively, the module Source should have MIME type "application/javascript+module"
                        .mimeType("application/javascript+module")
                        .build()
        );
        tryCallJSFunction("onLoad");
    }

    @Override
    public void onEnable() {
        tryCallJSFunction("onEnable");
    }

    @Override
    public void onDisable() {
        tryCallJSFunction("onDisable");
        context.close(true);
    }

    @Override
    public boolean isReloadable() {
        return tryCallJSFunction("isReloadable", false);
    }

    @Override
    public void reload() {
        tryCallJSFunction("reload");
    }

    @Override
    public boolean canResetContext() {
        return tryCallJSFunction("canResetContext", false);
    }

    @Override
    public void resetContext() {
        onDisable();
        onLoad();
        onEnable();
    }

    protected void initGlobalMembers() {
        var binding = context.getBindings("js");
        binding.putMember("thisPlugin", this);
        // Proxy the original "console" object, so when the js plug-in uses the
        // "console" object, it will output information through log4j
        binding.putMember("console", proxyLogger);
    }

    protected void tryCallJSFunction(String name) {
        var func = export.getMember(name);
        if (func != null && func.canExecute()) {
            func.executeVoid();
        }
    }

    protected <T> T tryCallJSFunction(String name, T defaultValue) {
        var func = export.getMember(name);
        if (func != null && func.canExecute()) {
            try {
                return func.execute().asHostObject();
            } catch (Throwable ignore) {
                return defaultValue;
            }
        }

        return defaultValue;
    }
}
