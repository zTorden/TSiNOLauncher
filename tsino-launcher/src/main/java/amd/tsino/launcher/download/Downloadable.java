package amd.tsino.launcher.download;

import java.io.File;
import java.net.URL;

public interface Downloadable {
    public abstract URL getURL();

    public abstract File getFile();
}
