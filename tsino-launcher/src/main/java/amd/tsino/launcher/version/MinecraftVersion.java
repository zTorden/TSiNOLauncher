package amd.tsino.launcher.version;

import java.util.ArrayList;
import java.util.List;

class MinecraftVersion {
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

    public String getMainClass() {
        return mainClass;
    }

    public String getID() {
        return id;
    }
}
