package org.allaymc.scriptpluginext.javascript;

import org.allaymc.server.extension.Extension;
import org.allaymc.server.plugin.AllayPluginManager;

/**
 * @author daoge_cmd
 */
public class JavaScriptPluginExtension extends Extension {
    @Override
    public void main(String[] args) {
        AllayPluginManager.registerLoaderFactory(new JsPluginLoader.JsPluginLoaderFactory());
    }
}
