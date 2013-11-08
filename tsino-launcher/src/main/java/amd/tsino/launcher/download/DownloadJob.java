package amd.tsino.launcher.download;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.LauncherConstants;

public class DownloadJob implements Runnable {
	private Downloader downloader;
	private ArrayList<DownloadJobListener> listeners = new ArrayList<DownloadJobListener>();

	public DownloadJob(Downloader downloader) {
		this.downloader = downloader;
	}

	private void fireStartedEvent() {
		for (DownloadJobListener listener : listeners) {
			listener.jobStarted(this);
		}
	}

	private void fireFinishedEvent() {
		for (DownloadJobListener listener : listeners) {
			listener.jobFinished(this);
		}
	}

	private void fireFailedEvent() {
		for (DownloadJobListener listener : listeners) {
			listener.jobFailed(this);
		}
	}

	public synchronized void addDownloadJobListener(DownloadJobListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeDownloadJobListener(
			DownloadJobListener listener) {
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
