package org.allaymc.scriptpluginext.python;

import org.allaymc.server.extension.Extension;
import org.allaymc.server.plugin.AllayPluginManager;

/**
 * @author daoge_cmd
 */
public class PythonPluginExtension extends Extension {
    @Override
    public void main(String[] args) {
        AllayPluginManager.registerLoaderFactory(new PyPluginLoader.PyPluginLoaderFactory());
    }
}
