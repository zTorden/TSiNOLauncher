package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.Downloadable;
import net.minecraft.launcher.Launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Mod implements Downloadable {
    private final String name;
    private String url;

    public Mod() {
        name = null;
        url = null;
    }

    public String getName() {
        return name;
    }

    @Override
    public URL getURL() {
        try {
            if (url == null) {
                url = Launcher.getInstance().getSettings().getServer().getClientPath() + LauncherConstants.MODS_BASE;
            }
            if (url.charAt(url.length() - 1) != '/') {
                url += '/';
            }
            return new URL(url + getName());
        } catch (MalformedURLException e) {
            Launcher.getInstance().getLog().error(e);
        }
        return null;
    }

    @Override
    public File getFile() {
        return LauncherUtils.getClientFile(LauncherConstants.MODS_BASE + getName());
    }
}