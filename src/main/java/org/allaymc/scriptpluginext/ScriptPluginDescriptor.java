package org.allaymc.scriptpluginext;

import lombok.Getter;
import org.allaymc.server.plugin.SimplePluginDescriptor;

/**
 * @author daoge_cmd
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
public class ScriptPluginDescriptor extends SimplePluginDescriptor {
    private int debugPort = -1;
}
