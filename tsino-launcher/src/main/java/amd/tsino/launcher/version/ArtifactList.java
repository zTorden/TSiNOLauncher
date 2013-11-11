package amd.tsino.launcher.version;

import amd.tsino.launcher.download.Downloadable;

import java.util.List;

public interface ArtifactList {
    public abstract List<? extends Downloadable> getArtifacts();
}
