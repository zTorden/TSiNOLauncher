package amd.tsino.launcher.version;

import amd.tsino.launcher.download.Downloadable;

import java.io.IOException;
import java.util.Collection;

public interface ArtifactList {
    public abstract void downloadList() throws IOException;

    public abstract Collection<? extends Downloadable> getArtifacts();
}
