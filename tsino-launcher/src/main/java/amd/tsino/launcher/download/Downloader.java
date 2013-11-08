package amd.tsino.launcher.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import net.minecraft.launcher.Launcher;
import amd.tsino.bootstrap.EtagDatabase;

public class Downloader {
	private static String getEtag(HttpURLConnection connection) {
		return EtagDatabase.formatEtag(connection.getHeaderField("ETag"));
	}

	private URL url;

	private File file;

	public Downloader(URL url, File file) {
		this.url = url;
		this.file = file;
	}

	private void download() throws IOException {
		String etag = EtagDatabase.getInstance().getEtag(file);

		try {
			HttpURLConnection connection = makeConnection(etag);
			int status = connection.getResponseCode();

			if (status == 304) {
				Launcher.getInstance()
						.getLog()
						.log("Using own copy as it matched etag: %s",
								file.getName());
			} else if (status / 100 == 2) {
				InputStream inputStream = connection.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(file);
				String hash = EtagDatabase.copyAndDigest(inputStream,
						outputStream);
				etag = getEtag(connection);
				EtagDatabase.getInstance().setEtag(file, etag, hash);
				Launcher.getInstance().getLog()
						.log("Downloaded: %s", file.getName());
			} else {
				throw new IOException("Server responded with " + status);
			}
		} catch (IOException ex) {
			throw new IOException(String.format(
					"Couldn't connect to server (%s)", ex.getMessage()), ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Missing Digest.MD5", ex);
		}
	}

	public void download(int retryCount) throws IOException {
		if (file.isDirectory()) {
			throw new IOException("File is a directory: " + file.toString());
		}

		if (file.getParentFile() != null) {
			if (!file.getParentFile().isDirectory()) {
				file.getParentFile().mkdirs();
			}
		}

		for (int i = 0; i < retryCount; i++) {
			try {
				download();
				return;
			} catch (IOException ex) {
				Launcher.getInstance().getLog().error(ex);
			}
		}

		if (file.isFile()) {
			Launcher.getInstance().getLog()
					.log("Assuming our copy is good: %s", file.toString());
		}
	}

	private HttpURLConnection makeConnection(String localEtag)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection(Launcher.getInstance().getProxy());
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(10000);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		connection.setRequestProperty("Cache-Control",
				"no-store,max-age=0,no-cache");
		connection.setRequestProperty("Expires", "0");
		connection.setRequestProperty("Pragma", "no-cache");
		if (localEtag != null) {
			connection.setRequestProperty("If-None-Match", "\"" + localEtag
					+ "\"");
		}
		connection.connect();
		return connection;
	}

	public void saveDatabase() {
		EtagDatabase.getInstance().saveDatabase();
	}
}
