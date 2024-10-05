package org.allaymc.scriptpluginext;

import org.allaymc.scriptpluginext.js.JsPluginLoader;
import org.allaymc.server.extension.Extension;
import org.allaymc.server.plugin.AllayPluginManager;

/**
 * @author daoge_cmd
 */
public class ScriptPluginExtension extends Extension {
    @Override
    public void main(String[] args) {
        AllayPluginManager.registerLoaderFactory(new JsPluginLoader.JsPluginLoaderFactory());
    }
}