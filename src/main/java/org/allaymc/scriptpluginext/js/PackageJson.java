package org.allaymc.scriptpluginext.js;

import lombok.Getter;
import org.allaymc.api.plugin.PluginDependency;
import org.allaymc.scriptpluginext.ScriptPluginDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author daoge_cmd
 */
public class PackageJson implements ScriptPluginDescriptor {
    // "main" is optional in package.json, and in this case the default value will be "index.js"
    private String main = "index.js";
    @Getter
    private String name;
    @Getter
    private String description = "";
    @Getter
    private String version;
    // TODO: support another format used in "author" and "contributors" which is in
    // json object, see https://dev.nodejs.cn/learn/the-package-json-guide/#author
    private String author;
    private List<String> contributors = Collections.emptyList();
    private String homepage = "";
    // NOTICE: The following fields are introduced by us, they are not part of the specification
    // Compared to "dependencies" or "devDependencies", "allayDependencies" specifies plugins that
    // need to be installed together with the server. These plugins are not installed by npm (or
    // other package managers), but should be installed manually by the user. The dependent plugin
    // is not necessarily written in javascript, it may be in other languages such as java
    private List<PluginDependency> allayDependencies = Collections.emptyList();
    // The debug port, optional. If specified, the plugin will be started in debug mode
    private int allayDebugPort = -1;

    @Override
    public String getEntrance() {
        return main;
    }

    @Override
    public List<String> getAuthors() {
        var authors = new ArrayList<String>();
        authors.add(author);
        authors.addAll(contributors);
        return authors;
    }

    @Override
    public String getWebsite() {
        return homepage;
    }

    @Override
    public List<PluginDependency> getDependencies() {
        return allayDependencies;
    }

    @Override
    public int getDebugPort() {
        return allayDebugPort;
    }
}
