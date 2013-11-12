package amd.tsino.launcher.version;

import amd.tsino.launcher.download.Downloadable;

import java.io.IOException;
import java.util.List;

public interface ArtifactList {
    public abstract void downloadList() throws IOException;

    public abstract List<? extends Downloadable> getArtifacts();
}
