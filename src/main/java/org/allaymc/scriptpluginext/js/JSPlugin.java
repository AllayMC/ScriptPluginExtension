package org.allaymc.scriptpluginext.js;

import lombok.SneakyThrows;
import org.allaymc.scriptpluginext.ScriptPluginDescriptor;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.plugin.PluginContainer;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

/**
 * @author daoge_cmd
 */
public class JSPlugin extends Plugin {

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
        // ClassCastException won't happen
        var chromeDebugPort = ((ScriptPluginDescriptor) pluginContainer.descriptor()).getDebugPort();
        var cbd = Context.newBuilder("js")
                .allowIO(IOAccess.ALL)
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true)
                .option("js.esm-eval-returns-exports", "true");
        if (chromeDebugPort > 0) {
            pluginLogger.info("Debug mode for javascript plugin {} is enabled. Port: {}", pluginContainer.descriptor().getName(), chromeDebugPort);
            // Debug mode is enabled
            cbd.option("inspect", String.valueOf(chromeDebugPort))
                    .option("inspect.Path", pluginContainer.descriptor().getName())
                    .option("inspect.Suspend", "true")
                    .option("inspect.Internal", "true")
                    .option("inspect.SourcePath", pluginContainer.loader().getPluginPath().toFile().getAbsolutePath());
        }
        context = cbd.build();
        initGlobalMembers();
        var entranceJsFileName = pluginContainer.descriptor().getEntrance();
        var path = pluginContainer.loader().getPluginPath().resolve(entranceJsFileName);
        export = context.eval(
                Source.newBuilder("js", path.toFile())
                        .name(entranceJsFileName)
                        .mimeType("application/javascript+module")
                        .build()
        );
        tryCallJsFunction("onLoad");
    }

    @Override
    public void onEnable() {
        tryCallJsFunction("onEnable");
    }

    @Override
    public void onDisable() {
        tryCallJsFunction("onDisable");
        context.close(true);
    }

    @Override
    public boolean isReloadable() {
        return true;
    }

    @Override
    public void reload() {
        onDisable();
        onLoad();
        onEnable();
    }

    protected void initGlobalMembers() {
        var binding = context.getBindings("js");
        binding.putMember("plugin", this);
        binding.putMember("console", proxyLogger);
    }

    protected void tryCallJsFunction(String functionName) {
        var func = export.getMember(functionName);
        if (func != null && func.canExecute()) {
            func.executeVoid();
        }
    }
}
