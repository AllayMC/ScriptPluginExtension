package org.allaymc.scriptpluginext.javascript;

import lombok.SneakyThrows;
import org.allaymc.scriptpluginext.common.ScriptPluginDescriptor;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.plugin.PluginContainer;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

/**
 * @author daoge_cmd
 */
public class JsPlugin extends Plugin {

    protected Context jsContext;
    protected Value jsExport;
    protected JsPluginProxyLogger proxyLogger;

    @Override
    public void setPluginContainer(PluginContainer pluginContainer) {
        super.setPluginContainer(pluginContainer);
        proxyLogger = new JsPluginProxyLogger(pluginLogger);
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
        jsContext = cbd.build();
        initGlobalMembers();
        var entranceJsFileName = pluginContainer.descriptor().getEntrance();
        var path = pluginContainer.loader().getPluginPath().resolve(entranceJsFileName);
        jsExport = jsContext.eval(
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
        jsContext.close(true);
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
        var binding = jsContext.getBindings("js");
        binding.putMember("plugin", this);
        binding.putMember("console", proxyLogger);
    }

    protected void tryCallJsFunction(String functionName) {
        var func = jsExport.getMember(functionName);
        if (func != null && func.canExecute()) {
            func.executeVoid();
        }
    }
}
