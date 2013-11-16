package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.DownloadJob;
import com.google.gson.Gson;
import net.minecraft.launcher.Launcher;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModFiles implements ArtifactList {
    private ModList modList;


    public void downloadList() throws IOException {
        File modsJson = LauncherUtils.getFile(LauncherConstants.MODS_JSON);
        new DownloadJob(modsJson, LauncherUtils.getURL(LauncherConstants.BASE_URL + LauncherConstants.MODS_JSON)).run();
        Reader reader = new InputStreamReader(new FileInputStream(modsJson), LauncherConstants.DEFAULT_CHARSET);
        final Gson gson = new Gson();
        modList = gson.fromJson(reader, ModList.class);
        reader.close();
    }

    @Override
    public List<Mod> getArtifacts() {
        try {
            downloadList();
            return modList.getList();
        } catch (IOException e) {
            Launcher.getInstance().getLog().error(e);
        }
        return new ArrayList<>();
    }

    public void cleanModFolders() {
        clearCoreModFolder();
        clearModFolder();
    }

    private void clearCoreModFolder() {
        File coreModDir = LauncherUtils.getFile(LauncherConstants.COREMODS_BASE);
        FileUtils.deleteQuietly(coreModDir);
    }

    private void clearModFolder() {
        Set<String> mods = new HashSet<>();
        for (Mod mod : modList.getList()) {
            mods.add(mod.getName());
        }

        File modsDir = LauncherUtils.getFile(LauncherConstants.MODS_BASE);
        if (modsDir.exists()) {
            File[] files = modsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !mods.contains(file.getName())) {
                        Launcher.getInstance().getLog().log(
                                "Removing mod: %s", file.toString());
                        FileUtils.deleteQuietly(file);
                    }
                }
            }
        }
    }

    public static class ModList {
        private final List<Mod> mods;

        public ModList() {
            mods = new ArrayList<>();
        }

        public List<Mod> getList() {
            return mods;
        }
    }
}
