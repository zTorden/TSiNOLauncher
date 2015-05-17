package amd.tsino.launcher.version;

import amd.tsino.launcher.download.Downloadable;
import net.minecraft.launcher.Launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LauncherVersion {
    private final List<Downloadable> artifacts = new ArrayList<>();
    private final ResourceFiles resourceFiles = new ResourceFiles();
    private final VersionFiles versionFiles = new VersionFiles();
    private final ModFiles modFiles = new ModFiles();
    private final CustomFiles customFiles = new CustomFiles();

    void addArtifacts(ArtifactList artifactList) throws IOException {
        artifactList.downloadList();
        Collection<? extends Downloadable> list = artifactList.getArtifacts();
        if (list == null || list.size() == 0) {
            logNoFiles(artifactList);
            return;
        }
        artifacts.addAll(list);
    }

    public void updateArtifactLists() throws IOException {
        artifacts.clear();
        addArtifacts(modFiles);
        addArtifacts(versionFiles);
        addArtifacts(resourceFiles);
        addArtifacts(customFiles);
    }

    public void downloadArtifacts() {
        for (Downloadable d : artifacts) {
            Launcher.getInstance().getDownloads().addDownload(d);
        }
    }

    private void logNoFiles(Object obj) {
        String message = obj.getClass().getSimpleName() + " skipped as there are no files to download";
        Launcher.getInstance().getLog().log(message);
    }

    public void extractFiles() {
        customFiles.extractFiles();
        modFiles.cleanModFolders();
        versionFiles.extractNatives();
        resourceFiles.extractResources();
    }

    public VersionFiles getVersionFiles() {
        return versionFiles;
    }
}
