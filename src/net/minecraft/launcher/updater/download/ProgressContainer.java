package net.minecraft.launcher.updater.download;

public class ProgressContainer {
	private long total;
	private long current;
	private DownloadJob job;

	public void addProgress(long amount) {
		setCurrent(getCurrent() + amount);
	}

	public long getCurrent() {
		return this.current;
	}

	public DownloadJob getJob() {
		return this.job;
	}

	public float getProgress() {
		if (this.total == 0L)
			return 0.0F;
		return (float) this.current / (float) this.total;
	}

	public long getTotal() {
		return this.total;
	}

	public void setCurrent(long current) {
		this.current = current;
		if (current > this.total)
			this.total = current;
		if (this.job != null)
			this.job.updateProgress();
	}

	public void setJob(DownloadJob job) {
		this.job = job;
		if (job != null)
			job.updateProgress();
	}

	public void setTotal(long total) {
		this.total = total;
		if (this.job != null)
			this.job.updateProgress();
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.download.ProgressContainer JD-Core Version:
 * 0.6.2
 */