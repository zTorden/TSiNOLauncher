package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.ServerInfo;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.Downloadable;
import net.minecraft.launcher.Launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CustomFiles implements ArtifactList {
	private final ServerInfo server=Launcher.getInstance().getSettings().getServer();
    private final Downloadable configZip = new DownloadJob(LauncherUtils.getClientFile(LauncherConstants.CONFIG_ZIP), server.getConfigUrl());
    private final Downloadable rcpackZip = new DownloadJob(LauncherUtils.getClientFile(LauncherConstants.RCPACK_ZIP), server.getRcpackUrl());

    @Override
    public void downloadList() throws IOException {
    }

    @Override
    public List<Downloadable> getArtifacts() {
        return Arrays.asList(configZip, rcpackZip);
    }

    public void extractFiles() {
        try {
            LauncherUtils.unzipWithoutReplace(configZip.getFile(), LauncherUtils.getClientFile(LauncherConstants.CONFIG_BASE), null);
        } catch (IOException e) {
            Launcher.getInstance().getLog().error(e);
        }
        try {
            LauncherUtils.unzipWithoutReplace(rcpackZip.getFile(), LauncherUtils.getClientFile(LauncherConstants.RCPACKS_BASE), null);
        } catch (IOException e) {
            Launcher.getInstance().getLog().error(e);
        }
        try {
            String name = "servers.dat";
            File file = LauncherUtils.getClientFile(name);
            if (!file.exists()) {
                try (InputStream in = getClass().getResourceAsStream("/" + name)) {
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        LauncherUtils.copyData(in, out);
                    }
                }
            }
        } catch (Exception e) {
            Launcher.getInstance().getLog().error(e);
        }
    }
}
