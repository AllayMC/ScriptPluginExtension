package org.allaymc.scriptpluginext;

import org.allaymc.api.command.SimpleCommand;
import org.allaymc.api.command.tree.CommandContext;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.plugin.PluginContainer;
import org.allaymc.api.server.Server;

/**
 * @author daoge_cmd
 */
public class ScriptPluginExtensionCommand extends SimpleCommand {
    public ScriptPluginExtensionCommand() {
        super("se", "The main command of the script plugin extension.");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("resetctx")
                .str("plugin")
                .optional()
                .exec(context -> {
                    String pluginName = context.getResult(1);
                    if (pluginName.isBlank()) {
                        // Reset ctx of all script plugins
                        Server.getInstance().getPluginManager().getEnabledPlugins().forEach((name, container) -> tryResetContextOf(context, name, container));
                    } else {
                        var container = Server.getInstance().getPluginManager().getEnabledPlugin(pluginName);
                        if (container == null) {
                            context.addError("Plugin not found: " + pluginName);
                            return context.fail();
                        }

                        tryResetContextOf(context, pluginName, container);
                    }
                    context.addOutput("Done");
                    return context.success();
                });
    }

    protected void tryResetContextOf(CommandContext context, String name, PluginContainer container) {
        var plugin = container.plugin();
        if (plugin instanceof ScriptPlugin sp && sp.canResetContext()) {
            context.addOutput("Resetting context of " + name);
            try {
                sp.resetContext();
            } catch (Throwable t) {
                context.addError("Failed to reset context of " + name, t);
            }
        }
    }
}
