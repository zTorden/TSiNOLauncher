package amd.tsino.bootstrap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

public class Util {

	public static URL constantURL(String input) {
		try {
			return new URL(input);
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
	}

	public static File getBaseDir() {
		String path = System.getProperty("user.home", ".");
		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.contains("win")) {
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null) {
				path = applicationData;
			}
		} else if (osName.contains("mac")) {
			return new File(path, "Library/Application Support/");
		}

		return new File(path);
	}

	public static String performPost(URL url, String parameters, Proxy proxy,
			String contentType, boolean returnErrorPage) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection(proxy);
		byte[] paramAsBytes = parameters.getBytes(Charset.forName("UTF-8"));

		connection.setConnectTimeout(15000);
		connection.setReadTimeout(60000);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", new StringBuilder()
				.append(contentType).append("; charset=utf-8").toString());

		connection.setRequestProperty("Content-Length", new StringBuilder()
				.append("").append(paramAsBytes.length).toString());
		connection.setRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		DataOutputStream writer = new DataOutputStream(
				connection.getOutputStream());
		writer.write(paramAsBytes);
		writer.flush();
		writer.close();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		} catch (IOException e) {
			if (returnErrorPage) {
				InputStream stream = connection.getErrorStream();
				if (stream != null)
					reader = new BufferedReader(new InputStreamReader(stream));
				else
					throw e;
			} else {
				throw e;
			}
		}
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		reader.close();
		return response.toString();
	}
}
