package org.allaymc.scriptpluginext.js;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.scriptpluginext.ScriptPluginDescriptor;
import org.allaymc.scriptpluginext.ScriptPluginI18nLoader;
import org.allaymc.api.i18n.I18n;
import org.allaymc.api.plugin.PluginContainer;
import org.allaymc.api.plugin.PluginDescriptor;
import org.allaymc.api.plugin.PluginException;
import org.allaymc.api.plugin.PluginLoader;
import org.allaymc.api.utils.JSONUtils;
import org.allaymc.server.i18n.AllayI18n;
import org.allaymc.server.plugin.DefaultPluginSource;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author daoge_cmd
 */
@Slf4j
public class JSPluginLoader implements PluginLoader {

    @Getter
    protected Path pluginPath;
    protected PluginDescriptor descriptor;

    @SneakyThrows
    public JSPluginLoader(Path pluginPath) {
        this.pluginPath = pluginPath;
    }

    @SneakyThrows
    @Override
    public PluginDescriptor loadDescriptor() {
        descriptor = JSONUtils.from(Files.newBufferedReader(pluginPath.resolve("plugin.json")), ScriptPluginDescriptor.class);
        PluginDescriptor.checkDescriptorValid(descriptor);
        return descriptor;
    }

    @SneakyThrows
    @Override
    public PluginContainer loadPlugin() {
        // Read entrance js file
        var entrancePath = pluginPath.resolve(descriptor.getEntrance());
        if (!Files.exists(entrancePath)) throw new PluginException("Entrance js file not found: " + entrancePath);

        // Load plugin's lang files
        ((AllayI18n) I18n.get()).applyI18nLoader(new ScriptPluginI18nLoader(pluginPath));

        return PluginContainer.createPluginContainer(
                new JSPlugin(),
                descriptor, this,
                DefaultPluginSource.getOrCreateDataFolder(descriptor.getName())
        );
    }

    public static class JsPluginLoaderFactory implements PluginLoader.Factory {

        @Override
        public boolean canLoad(Path pluginPath) {
            return pluginPath.getFileName().toString().endsWith(".js") && Files.isDirectory(pluginPath);
        }

        @Override
        public PluginLoader create(Path pluginPath) {
            return new JSPluginLoader(pluginPath);
        }
    }
}
