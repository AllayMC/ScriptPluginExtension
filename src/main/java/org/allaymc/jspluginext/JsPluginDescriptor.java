package org.allaymc.jspluginext;

import lombok.Getter;
import org.allaymc.server.plugin.SimplePluginDescriptor;

/**
 * @author daoge_cmd
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
public class JsPluginDescriptor extends SimplePluginDescriptor {
    private int debugPort = -1;
}
