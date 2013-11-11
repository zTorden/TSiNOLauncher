package amd.tsino.launcher.download;

import amd.tsino.launcher.LauncherConstants;
import net.minecraft.launcher.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadJob implements Runnable, Downloadable {
    private final Object lock = new Object();
    private final ArrayList<JobListener> listeners = new ArrayList<>();
    private final File file;
    private final URL url;

    public DownloadJob(File file, URL url) {
        this.file = file;
        this.url = url;
    }

    public DownloadJob(Downloadable downloadable) {
        this.file = downloadable.getFile();
        this.url = downloadable.getURL();
    }

    private void fireStartedEvent() {
        synchronized (lock) {
            for (JobListener listener : listeners) {
                listener.jobStarted(this);
            }
        }
    }

    private void fireFinishedEvent() {
        synchronized (lock) {
            for (JobListener listener : listeners) {
                listener.jobFinished(this);
            }
        }
    }

    private void fireFailedEvent() {
        synchronized (lock) {
            for (JobListener listener : listeners) {
                listener.jobFailed(this);
            }
        }
    }

    public void addJobListener(JobListener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    public void removeJobListener(JobListener listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void run() {
        synchronized (lock) {
            try {
                fireStartedEvent();
                Downloader.download(this, LauncherConstants.DOWNLOAD_RETRIES);
                fireFinishedEvent();
            } catch (IOException e) {
                Launcher.getInstance().getLog().error(e);
                fireFailedEvent();
            }
        }
    }
}
