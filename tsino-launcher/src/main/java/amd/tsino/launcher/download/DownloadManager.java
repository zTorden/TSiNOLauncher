package amd.tsino.launcher.download;

import amd.tsino.launcher.LauncherConstants;
import net.minecraft.launcher.Launcher;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private final Object thisLock = new Object();
    private ExecutorService executor;
    private JobListener jobListener = new JobListener();
    private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
    private int total;
    private int finished;
    private int failed;

    public DownloadManager() {
        executor = Executors.newFixedThreadPool(LauncherConstants.DOWNLOAD_THREADS);
    }

    public void addDownload(Downloader downloader) {
        synchronized (thisLock) {
            total++;
            fireUpdatedEvent();
        }
        DownloadJob job = new DownloadJob(downloader);
        job.addJobListener(jobListener);
        executor.execute(job);
    }

    public void shutdown() {
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Launcher.getInstance().getLog().error(e);
        }
    }

    public void addUpdateListener(UpdateListener listener) {
        synchronized (thisLock) {
            listeners.add(listener);
        }
    }

    public void removeUpdateListener(UpdateListener listener) {
        synchronized (thisLock) {
            listeners.remove(listener);
        }
    }

    public void fireUpdatedEvent() {
        synchronized (thisLock) {
            for (UpdateListener listener : listeners) {
                listener.updated(this);
            }
        }
    }

    public void reset() {
        shutdown();
        synchronized (thisLock) {
            failed = 0;
            finished = 0;
            total = 0;
            fireUpdatedEvent();
        }
    }

    public int getTotal() {
        synchronized (thisLock) {
            return total;
        }
    }

    public int getFinished() {
        synchronized (thisLock) {
            return finished;
        }
    }

    public int getFailed() {
        synchronized (thisLock) {
            return failed;
        }
    }

    private class JobListener implements amd.tsino.launcher.download.JobListener {
        @Override
        public void jobStarted(DownloadJob job) {
        }

        @Override
        public void jobFinished(DownloadJob job) {
            synchronized (thisLock) {
                finished++;
                fireUpdatedEvent();
            }
        }

        @Override
        public void jobFailed(DownloadJob job) {
            synchronized (thisLock) {
                failed++;
                fireUpdatedEvent();
            }
        }
    }
}
