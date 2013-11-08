package amd.tsino.launcher.download;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amd.tsino.launcher.LauncherConstants;

public class DownloadManager {
	private ExecutorService executor;
	private JobListener jobListener = new JobListener();
	private Object thisLock = new Object();
	private int total;
	private int finished;
	private int failed;

	public DownloadManager() {
		executor = Executors
				.newFixedThreadPool(LauncherConstants.DOWNLOAD_THREADS);
	}

	public void addDownload(Downloader downloader) {
		synchronized (thisLock) {
			total++;
		}
		DownloadJob job = new DownloadJob(downloader);
		job.addDownloadJobListener(jobListener);
		executor.execute(job);
	}

	public void shutdown() {
		executor.shutdown();
	}

	private class JobListener implements DownloadJobListener {
		@Override
		public void jobStarted(DownloadJob job) {
		}

		@Override
		public void jobFinished(DownloadJob job) {
			synchronized (thisLock) {
				finished++;
			}
		}

		@Override
		public void jobFailed(DownloadJob job) {
			synchronized (thisLock) {
				failed++;
			}
		}
	}
}
