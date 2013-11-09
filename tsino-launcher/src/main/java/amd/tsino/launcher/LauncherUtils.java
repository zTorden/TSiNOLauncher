package amd.tsino.launcher;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

public class LauncherUtils {
    public static void openLink(URI link) throws Exception {
        Class<?> desktopClass = Class.forName("java.awt.Desktop");
        Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(
                null, new Object());
        desktopClass.getMethod("browse", new Class[]{URI.class}).invoke(o, link);
    }

    public static String buildQuery(Map<String, Object> query)
            throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));

            if (entry.getValue() != null) {
                builder.append('=');
                builder.append(URLEncoder.encode(entry.getValue().toString(),
                        "UTF-8"));
            }
        }

        return builder.toString();
    }

    public static URL constantURL(String input) {
        try {
            return new URL(input);
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    public static String performPost(URL url, String parameters, Proxy proxy,
                                     String contentType, boolean returnErrorPage) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection(proxy);
        byte[] paramAsBytes = parameters.getBytes(Charset.forName("UTF-8"));

        connection.setConnectTimeout(LauncherConstants.DOWNLOAD_TIMEOUT);
        connection.setReadTimeout(LauncherConstants.DOWNLOAD_TIMEOUT);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");

        connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
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

    public static String performPost(URL url, Map<String, Object> query,
                                     Proxy proxy) throws IOException {
        return performPost(url, buildQuery(query), proxy,
                "application/x-www-form-urlencoded", false);
    }
}
