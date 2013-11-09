package amd.tsino.launcher.version;

public class LauncherVersion {
    ModList mods;

    public void downloadMods() {
        mods = ModList.download();
    }
}
