package amd.tsino.launcher.version;

import net.minecraft.launcher.Launcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ModList {
    private List<Mod> mods;

    public static ModList download() {
        return new ModList();
    }

    public static class Mod {
        private String name;
        private String url;
        private boolean coremod;

        public String getName() {
            return name;
        }

        public URL getUrl() {
            try {
                return new URL(url + name);
            } catch (MalformedURLException e) {
                Launcher.getInstance().getLog().error(e);
            }
            return null;
        }

        public boolean isCoremod() {
            return coremod;
        }
    }
}
