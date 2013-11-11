package amd.tsino.launcher.download;

import amd.tsino.launcher.LauncherConstants;
import net.minecraft.launcher.Launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    private final Object lock = new Object();
    private final ExecutorService executor;
    private final JobListener jobListener = new JobListener();
    private final ArrayList<UpdateListener> listeners = new ArrayList<>();
    private ArrayList<DownloadJob> failedJobs = new ArrayList<>();
    private int total;
    private int finished;

    public DownloadManager() {
        executor = Executors.newFixedThreadPool(LauncherConstants.DOWNLOAD_THREADS);
    }

    public void addDownload(DownloadJob job) {
        synchronized (lock) {
            total++;
            fireUpdatedEvent();
        }
        job.addJobListener(jobListener);
        executor.execute(job);
    }

    public void addDownload(Downloadable downloadable) {
        addDownload(new DownloadJob(downloadable));
    }

    public void addUpdateListener(UpdateListener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    public void removeUpdateListener(UpdateListener listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    public void fireUpdatedEvent() {
        synchronized (lock) {
            lock.notifyAll();
            for (UpdateListener listener : listeners) {
                listener.updated(this);
            }
        }
    }

    public void reset() {
        synchronized (lock) {
            waitFinish();
            finished = 0;
            total = 0;
            failedJobs = new ArrayList<>();
            fireUpdatedEvent();
        }
    }

    public void waitFinish() {
        while (true) {
            synchronized (lock) {
                if (getFinished() + getFailed() == total) {
                    break;
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Launcher.getInstance().getLog().error(e);
                }
            }
        }
    }

    public int getTotal() {
        synchronized (lock) {
            return total;
        }
    }

    public int getFinished() {
        synchronized (lock) {
            return finished;
        }
    }

    public int getFailed() {
        synchronized (lock) {
            return failedJobs.size();
        }
    }

    public List<DownloadJob> getFailedJobs() {
        synchronized (lock) {
            return Collections.unmodifiableList(failedJobs);
        }
    }

    private class JobListener implements amd.tsino.launcher.download.JobListener {
        @Override
        public void jobStarted(DownloadJob job) {
        }

        @Override
        public void jobFinished(DownloadJob job) {
            synchronized (lock) {
                finished++;
                fireUpdatedEvent();
            }
        }

        @Override
        public void jobFailed(DownloadJob job) {
            synchronized (lock) {
                failedJobs.add(job);
                fireUpdatedEvent();
            }
        }
    }
}
