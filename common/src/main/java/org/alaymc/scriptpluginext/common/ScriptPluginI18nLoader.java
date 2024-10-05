package org.alaymc.scriptpluginext.common;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.i18n.LangCode;
import org.allaymc.api.utils.JSONUtils;
import org.allaymc.server.i18n.I18nLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author daoge_cmd
 */
@Slf4j
@AllArgsConstructor
public class ScriptPluginI18nLoader implements I18nLoader {

    protected Path pluginPath;

    @Override
    public Map<String, String> getLangMap(LangCode langCode) {
        try {
            var str = Files.readString(pluginPath.resolve("lang").resolve(langCode.name() + ".json"));
            TypeToken<HashMap<String, String>> typeToken = new TypeToken<>() {};
            return JSONUtils.fromLenient(str, typeToken);
        } catch (NoSuchFileException e) {
            return Collections.emptyMap();
        } catch (IOException e) {
            log.error("Error while loading plugin language file", e);
            return Collections.emptyMap();
        }
    }
}
