package amd.tsino.launcher.download;

public interface DownloadJobListener {
	public abstract void jobStarted(DownloadJob job);

	public abstract void jobFinished(DownloadJob job);

	public abstract void jobFailed(DownloadJob job);
}
