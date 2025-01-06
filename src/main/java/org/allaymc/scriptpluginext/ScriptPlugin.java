package org.allaymc.scriptpluginext;

/**
 * @author daoge_cmd
 */
public interface ScriptPlugin {
    boolean canResetContext();

    void resetContext();
}
