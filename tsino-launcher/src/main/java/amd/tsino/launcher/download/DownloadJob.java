package amd.tsino.launcher.download;

import amd.tsino.launcher.LauncherConstants;
import net.minecraft.launcher.Launcher;

import java.io.IOException;
import java.util.ArrayList;

public class DownloadJob implements Runnable {
    private Downloader downloader;
    private ArrayList<JobListener> listeners = new ArrayList<JobListener>();

    public DownloadJob(Downloader downloader) {
        this.downloader = downloader;
    }

    private void fireStartedEvent() {
        for (JobListener listener : listeners) {
            listener.jobStarted(this);
        }
    }

    private void fireFinishedEvent() {
        for (JobListener listener : listeners) {
            listener.jobFinished(this);
        }
    }

    private void fireFailedEvent() {
        for (JobListener listener : listeners) {
            listener.jobFailed(this);
        }
    }

    public synchronized void addJobListener(JobListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeJobListener(
            JobListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        try {
            fireStartedEvent();
            downloader.download(LauncherConstants.DOWNLOAD_RETRIES);
            fireFinishedEvent();
        } catch (IOException e) {
            Launcher.getInstance().getLog().error(e);
            fireFailedEvent();
        }
    }
}
