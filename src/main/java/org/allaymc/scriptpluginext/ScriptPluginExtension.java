package org.allaymc.scriptpluginext;

import org.allaymc.api.registry.Registries;
import org.allaymc.scriptpluginext.js.JSPluginLoader;
import org.allaymc.scriptpluginext.js.JSPluginSource;
import org.allaymc.server.extension.Extension;
import org.allaymc.server.plugin.AllayPluginManager;

/**
 * @author daoge_cmd
 */
public class ScriptPluginExtension extends Extension {
    @Override
    public void main(String[] args) {
        AllayPluginManager.registerSource(new JSPluginSource());
        AllayPluginManager.registerLoaderFactory(new JSPluginLoader.JsPluginLoaderFactory());
    }

    @Override
    public void afterServerStarted() {
        Registries.COMMANDS.register(new ScriptPluginExtensionCommand());
    }
}