package net.minecraft.launcher.updater.download;

public abstract interface DownloadListener {
	public abstract void onDownloadJobFinished(DownloadJob paramDownloadJob);

	public abstract void onDownloadJobProgressChanged(
			DownloadJob paramDownloadJob);
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.download.DownloadListener JD-Core Version:
 * 0.6.2
 */