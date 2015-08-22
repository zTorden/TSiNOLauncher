package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.Downloadable;
import com.google.gson.Gson;
import net.minecraft.launcher.Launcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VersionFiles implements ArtifactList {
    private static final String NATIVES_SUFFIX = "-natives-";
    private final Gson gson = new Gson();
    private MinecraftVersion version;
    private File nativesDir = null;

    private String getLatestVersionName() throws IOException {
        URL url = LauncherUtils.getURL(Launcher.getInstance().getSettings().getServer().getClientPath() + LauncherConstants.VERSIONS_JSON);
        File file = LauncherUtils.getClientFile(LauncherConstants.VERSIONS_JSON);
        new DownloadJob(file, url).run();
        Reader reader = new InputStreamReader(new FileInputStream(file), LauncherConstants.DEFAULT_CHARSET);
        Versions versions = gson.fromJson(reader, Versions.class);
        reader.close();
        return versions.getLatest().getRelease();
    }

    private void downloadVersionInfo() throws IOException {
        String versionName = getLatestVersionName();
        final String fileName = LauncherConstants.VERSIONS_BASE + versionName + "/" + versionName + ".json";
        URL url = new URL(Launcher.getInstance().getSettings().getServer().getClientPath() + fileName);
        File file = LauncherUtils.getClientFile(fileName);
        new DownloadJob(file, url).run();
        Reader reader = new InputStreamReader(new FileInputStream(file), LauncherConstants.DEFAULT_CHARSET);
        version = gson.fromJson(reader, MinecraftVersion.class);
        reader.close();
        if (!versionName.equals(version.getID())) {
            throw new IOException("Version id mismatch");
        }
    }

    @Override
    public void downloadList() throws IOException {
        downloadVersionInfo();
    }

    @Override
    public List<Downloadable> getArtifacts() {
        ArrayList<Downloadable> list = new ArrayList<>();
        for (Library lib : version.getLibraries()) {
            list.add(lib);
        }
        try {
            final String name = version.getVersionJar();
            URL url = new URL(Launcher.getInstance().getSettings().getServer().getClientPath() + name);
            list.add(new DownloadJob(LauncherUtils.getClientFile(name), url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void extractNatives() {
        cleanOldNatives();
        unpackNatives();
    }

    private void unpackNatives() {
    	Launcher.getInstance().getLog().log("Unpacking new natives...");    	
        nativesDir = LauncherUtils.getClientFile(LauncherConstants.VERSIONS_BASE + "/" + version.getID() + "/" + version.getID() + NATIVES_SUFFIX + System.currentTimeMillis() + "/");
        for (Library lib : version.getLibraries()) {
            if (lib.isNative()) {
                try {
                    lib.extractFiles(nativesDir);
                } catch (IOException e) {
                    Launcher.getInstance().getLog().error(e);
                }
            }
        }
    }

    private void cleanOldNatives() {
    	Launcher.getInstance().getLog().log("Looking for old natives to clean up...");
    	File root = LauncherUtils.getClientFile(LauncherConstants.VERSIONS_BASE);

        for (File version : root
                .listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
            for (File folder : version.listFiles((FileFilter) FileFilterUtils.prefixFileFilter(version.getName() + NATIVES_SUFFIX))) {
                Launcher.getInstance().getLog().log("Deleting %s", folder.toString());
                FileUtils.deleteQuietly(folder);
            }
        }
    }

    public MinecraftVersion getVersion() {
        return version;
    }

    public File getNativesDir() {
        return nativesDir;
    }

    public static class Versions {
        private final LatestVersion latest;

        public Versions() {
            latest = null;
        }

        public LatestVersion getLatest() {
            return latest;
        }

        public static class LatestVersion {
            private final String release;

            public LatestVersion() {
                release = null;
            }

            public String getRelease() {
                return release;
            }
        }
    }
}
