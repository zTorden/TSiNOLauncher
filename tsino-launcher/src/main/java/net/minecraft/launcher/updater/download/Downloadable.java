package net.minecraft.launcher.updater.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import amd.tsino.bootstrap.EtagDatabase;

public class Downloadable {
	public static String getEtag(HttpURLConnection connection) {
		return EtagDatabase.formatEtag(connection.getHeaderField("ETag"));
	}

	private final URL url;
	private final File target;
	private final boolean forceDownload;
	private final Proxy proxy;
	private final ProgressContainer monitor;
	private int numAttempts;

	private long expectedSize;

	public Downloadable(Proxy proxy, URL remoteFile, File localFile,
			boolean forceDownload) {
		this.proxy = proxy;
		this.url = remoteFile;
		this.target = localFile;
		this.forceDownload = forceDownload;
		this.monitor = new ProgressContainer();
	}

	public String download() throws IOException {
		String localEtag = null;
		this.numAttempts += 1;

		if ((this.target.getParentFile() != null)
				&& (!this.target.getParentFile().isDirectory())) {
			this.target.getParentFile().mkdirs();
		}
		if ((!this.forceDownload) && (this.target.isFile())) {
			localEtag = EtagDatabase.getInstance().getEtag(this.target);
		}

		if ((this.target.isFile()) && (!this.target.canWrite())) {
			throw new RuntimeException("Do not have write permissions for "
					+ this.target + " - aborting!");
		}
		try {
			HttpURLConnection connection = makeConnection(localEtag);
			int status = connection.getResponseCode();

			if (status == 304)
				return "Used own copy as it matched etag";
			if (status / 100 == 2) {
				if (this.expectedSize == 0L)
					this.monitor.setTotal(connection.getContentLength());
				else {
					this.monitor.setTotal(this.expectedSize);
				}

				InputStream inputStream = new MonitoringInputStream(
						connection.getInputStream(), this.monitor);
				FileOutputStream outputStream = new FileOutputStream(
						this.target);
				String hash = EtagDatabase.copyAndDigest(inputStream,
						outputStream);
				String etag = getEtag(connection);

				if (etag.equals("-")) {
					return "Didn't have etag so assuming our copy is good";
				}
				EtagDatabase.getInstance().setEtag(target, etag, hash);
				return "Downloaded succsessfully";
			}
			if (this.target.isFile()) {
				return "Couldn't connect to server (responded with " + status
						+ ") but have local file, assuming it's good";
			}
			throw new RuntimeException("Server responded with " + status);
		} catch (IOException e) {
			if (this.target.isFile()) {
				return "Couldn't connect to server ("
						+ e.getClass().getSimpleName() + ": '" + e.getMessage()
						+ "') but have local file, assuming it's good";
			}
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Missing Digest.MD5", e);
		}
	}

	public long getExpectedSize() {
		return this.expectedSize;
	}

	public ProgressContainer getMonitor() {
		return this.monitor;
	}

	public int getNumAttempts() {
		return this.numAttempts;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public File getTarget() {
		return this.target;
	}

	public URL getUrl() {
		return this.url;
	}

	protected HttpURLConnection makeConnection(String localEtag)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) this.url
				.openConnection(this.proxy);
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(60000);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		connection.setRequestProperty("Cache-Control",
				"no-store,max-age=0,no-cache");
		connection.setRequestProperty("Expires", "0");
		connection.setRequestProperty("Pragma", "no-cache");
		if (localEtag != null)
			connection.setRequestProperty("If-None-Match", "\"" + localEtag
					+ "\"");

		connection.connect();

		return connection;
	}

	public void setExpectedSize(long expectedSize) {
		this.expectedSize = expectedSize;
	}

	public boolean shouldIgnoreLocal() {
		return this.forceDownload;
	}

}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.download.Downloadable JD-Core Version: 0.6.2
 */