package net.minecraft.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLHandshakeException;

public class Downloader implements Runnable {
	private static final int MAX_RETRIES = 10;
	private final Proxy proxy;
	private final String currentMd5;
	private final File targetFile;
	private final Controller controller;
	private Bootstrap bootstrap;

	public Downloader(Controller controller, Bootstrap bootstrap, Proxy proxy,
			String currentMd5, File targetFile) {
		this.controller = controller;
		this.bootstrap = bootstrap;
		this.proxy = proxy;
		this.currentMd5 = currentMd5;
		this.targetFile = targetFile;
	}

	public void run() {
		int retries = 0;
		while (true) {
			retries++;
			if (retries > MAX_RETRIES)
				break;
			try {
				URL url = new URL(Bootstrap.LAUNCHER_URL);

				HttpURLConnection connection = getConnection(url);

				connection.setUseCaches(false);
				connection.setDefaultUseCaches(false);
				connection.setRequestProperty("Cache-Control",
						"no-store,max-age=0,no-cache");
				connection.setRequestProperty("Expires", "0");
				connection.setRequestProperty("Pragma", "no-cache");
				if (this.currentMd5 != null) {
					connection.setRequestProperty("If-None-Match",
							this.currentMd5.toLowerCase());
				}

				connection.setConnectTimeout(30000);
				connection.setReadTimeout(10000);

				log(new StringBuilder()
						.append("Downloading: " + Bootstrap.LAUNCHER_URL)
						.append(retries > 1 ? String.format(" (try %d/%d)",
								new Object[] { Integer.valueOf(retries),
										Integer.valueOf(10) }) : "").toString());
				long start = System.nanoTime();
				connection.connect();
				long elapsed = System.nanoTime() - start;
				log(new StringBuilder().append("Got reply in: ")
						.append(elapsed / 1000000L).append("ms").toString());

				int code = connection.getResponseCode() / 100;

				if (code == 2) {
					String eTag = connection.getHeaderField("ETag");

					if (eTag == null) {
						eTag = "-";
					} else {
						eTag = eTag.substring(1, eTag.length() - 1);
					}

					this.controller.foundUpdate.set(true);
					this.controller.foundUpdateLatch.countDown();

					InputStream inputStream = connection.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(
							this.targetFile);

					long startDownload = System.nanoTime();
					long bytesRead = 0L;
					byte[] buffer = new byte[65536];
					try {
						int read = inputStream.read(buffer);
						while (read >= 1) {
							bytesRead += read;
							outputStream.write(buffer, 0, read);
							read = inputStream.read(buffer);
						}
					} finally {
						inputStream.close();
						outputStream.close();
					}
					long elapsedDownload = System.nanoTime() - startDownload;

					float elapsedSeconds = (float) (1L + elapsedDownload) / 1.0E+009F;
					float kbRead = (float) bytesRead / 1024.0F;
					long length = connection.getContentLengthLong();
					log(String.format(
							"Downloaded %.1fkb in %ds at %.1fkb/s",
							new Object[] { Float.valueOf(kbRead),
									Integer.valueOf((int) elapsedSeconds),
									Float.valueOf(kbRead / elapsedSeconds) }));

					if (length != bytesRead) {
						log("After downloading, the hash didn't match. Retrying");
					} else {
						this.controller.hasDownloadedLatch.countDown();
						return;
					}
				} else if (code == 4) {
					log("Remote file not found.");
				} else {
					this.controller.foundUpdate.set(false);
					this.controller.foundUpdateLatch.countDown();
					log("No update found.");
					return;
				}
			} catch (Exception e) {
				log(new StringBuilder().append("Exception: ")
						.append(e.toString()).toString());
				suggestHelp(e);
			}
		}

		log("Unable to download remote file. Check your internet connection/proxy settings.");
	}

	public void suggestHelp(Throwable t) {
		if ((t instanceof BindException))
			log("Recognized exception: the likely cause is a broken ipv4/6 stack. Check your TCP/IP settings.");
		else if ((t instanceof SSLHandshakeException))
			log("Recognized exception: the likely cause is a set of broken/missing root-certificates. Check your java install and perhaps reinstall it.");
	}

	public void log(String str) {
		this.bootstrap.println(str);
	}

	public HttpURLConnection getConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection(this.proxy);
	}

	public static class Controller {
		public final CountDownLatch foundUpdateLatch = new CountDownLatch(1);
		public final AtomicBoolean foundUpdate = new AtomicBoolean(false);
		public final CountDownLatch hasDownloadedLatch = new CountDownLatch(1);
	}
}
