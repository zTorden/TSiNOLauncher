package amd.tsino.launcher.version;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.Downloadable;

public class ResourceFiles implements ArtifactList {
    private final Downloadable resourcesZip = new DownloadJob(LauncherUtils.getFile(LauncherConstants.RESOURCES_ZIP), LauncherConstants.RESOURCES_URL);

    @Override
    public void downloadList() throws IOException {
    }

    @Override
    public List<Downloadable> getArtifacts() {
	return Arrays.asList(resourcesZip);
    }
    
    public void extractResources(){
        try {
            LauncherUtils.unzipWithoutReplace(resourcesZip.getFile(), LauncherUtils.getFile(LauncherConstants.RESOURCES_BASE), null);
        } catch (IOException e) {
            Launcher.getInstance().getLog().error(e);
        }
    }
} 