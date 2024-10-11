package org.allaymc.scriptpluginext.python;

import lombok.SneakyThrows;
import org.allaymc.scriptpluginext.ScriptPluginDescriptor;
import org.allaymc.api.plugin.Plugin;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

/**
 * @author daoge_cmd
 */
public class PyPlugin extends Plugin {

    protected Context pyContext;
    protected Value pyExport;

    @SneakyThrows
    @Override
    public void onLoad() {
        // TODO: https://github.com/oracle/graalpython/blob/master/docs/user/Embedding-Build-Tools.md#deployment
        // ClassCastException won't happen
        var chromeDebugPort = ((ScriptPluginDescriptor) pluginContainer.descriptor()).getDebugPort();
        var cbd = Context.newBuilder("python")
                .allowIO(IOAccess.ALL)
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true);
        if (chromeDebugPort > 0) {
            pluginLogger.info("Debug mode for python plugin {} is enabled. Port: {}", pluginContainer.descriptor().getName(), chromeDebugPort);
            // Debug mode is enabled
            cbd.option("inspect", String.valueOf(chromeDebugPort))
                    .option("inspect.Path", pluginContainer.descriptor().getName())
                    .option("inspect.Suspend", "true")
                    .option("inspect.Internal", "true")
                    .option("inspect.SourcePath", pluginContainer.loader().getPluginPath().toFile().getAbsolutePath());
        }
        pyContext = cbd.build();
        initGlobalMembers();
        var entrancePyFileName = pluginContainer.descriptor().getEntrance();
        var path = pluginContainer.loader().getPluginPath().resolve(entrancePyFileName);
        pyExport = pyContext.eval(
                Source.newBuilder("python", path.toFile())
                        .name(entrancePyFileName)
                        .build()
        );
        tryCallPyFunction("onLoad");
    }

    @Override
    public void onEnable() {
        tryCallPyFunction("onEnable");
    }

    @Override
    public void onDisable() {
        tryCallPyFunction("onDisable");
        pyContext.close(true);
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
        var binding = pyContext.getBindings("python");
        binding.putMember("plugin", this);
    }

    protected void tryCallPyFunction(String functionName) {
        var func = pyExport.getMember(functionName);
        if (func != null && func.canExecute()) {
            func.executeVoid();
        }
    }
}
