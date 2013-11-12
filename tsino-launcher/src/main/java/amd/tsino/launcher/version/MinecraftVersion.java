package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;

import java.util.ArrayList;
import java.util.List;

public class MinecraftVersion {
    private final String id;
    private final String mainClass;
    private final String minecraftArguments;
    private final List<Library> libraries;

    public MinecraftVersion() {
        id = null;
        mainClass = null;
        minecraftArguments = "";
        libraries = new ArrayList<>();
    }

    public String getMinecraftArguments() {
        return minecraftArguments;
    }

    public List<Library> getLibraries() {
        List<Library> libs = new ArrayList<>();
        for (Library lib : libraries) {
            if (lib.isAllowed()) {
                libs.add(lib);
            }
        }
        return libs;
    }

    public String getVersionJar() {
        return LauncherConstants.VERSIONS_BASE + id + "/" + id + ".jar";
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getID() {
        return id;
    }
}
