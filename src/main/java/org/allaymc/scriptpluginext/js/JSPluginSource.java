package org.allaymc.scriptpluginext.js;

import lombok.SneakyThrows;
import org.allaymc.api.plugin.PluginSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author daoge_cmd
 */
public class JSPluginSource implements PluginSource {

    public static final Path JS_PLUGIN_FOLDER = Path.of("jsplugins");
    // Because the js plugin itself is also a folder, set the root of the data folder to another directory to avoid conflicts.
    public static final Path JS_PLUGIN_DATA_FOLDER = Path.of("jsplugindata");

    @SneakyThrows
    public JSPluginSource() {
        if (!Files.exists(JS_PLUGIN_FOLDER)) {
            Files.createDirectory(JS_PLUGIN_FOLDER);
        }
        if (!Files.exists(JS_PLUGIN_DATA_FOLDER)) {
            Files.createDirectory(JS_PLUGIN_DATA_FOLDER);
        }
    }

    @SneakyThrows
    public static Path getOrCreateDataFolder(String pluginName) {
        var dataFolder = JS_PLUGIN_DATA_FOLDER.resolve(pluginName);
        if (!Files.exists(dataFolder)) {
            Files.createDirectory(dataFolder);
        }
        return dataFolder;
    }

    @SneakyThrows
    @Override
    public Set<Path> find() {
        try (var stream = Files.list(JS_PLUGIN_FOLDER)) {
            return stream.collect(Collectors.toSet());
        }
    }
}
