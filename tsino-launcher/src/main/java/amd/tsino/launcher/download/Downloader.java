package amd.tsino.launcher.download;

import amd.tsino.bootstrap.EtagDatabase;
import amd.tsino.launcher.LauncherUtils;
import net.minecraft.launcher.Launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class Downloader {

    private Downloader() {
    }

    private static String getEtag(HttpURLConnection connection) {
        return EtagDatabase.formatEtag(connection.getHeaderField("ETag"));
    }

    public static void saveDatabase() {
        EtagDatabase.getInstance().saveDatabase();
    }

    private static void download(File file, URL url) throws IOException {
        String etag = EtagDatabase.getInstance().getEtag(file);

        try {
            HttpURLConnection connection = makeConnection(url, etag);
            int status = connection.getResponseCode();

            if (status == 304) {
                Launcher.getInstance().getLog()
                        .log("Using own copy as it matched etag: %s",
                                file.getName());
            } else if (status / 100 == 2) {
                long contentLength = connection.getContentLengthLong();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                String hash = EtagDatabase.copyAndDigest(inputStream,
                        outputStream);
                long fileLength = file.length();
                if (contentLength > 0 && fileLength != contentLength) {
                    throw new IOException(String.format("File length does not match Content-Length: %d != %d", fileLength, contentLength));
                }
                etag = getEtag(connection);
                EtagDatabase.getInstance().setEtag(file, etag, hash);
                Launcher.getInstance().getLog()
                        .log("Downloaded: %s", file.getName());
            } else {
                throw new IOException("Server responded with " + status);
            }
        } catch (IOException ex) {
            throw new IOException(String.format(
                    "Couldn't connect to server (%s, url: %s)", ex.getMessage(), url.toString()), ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Missing Digest.MD5", ex);
        }
    }

    public static void download(Downloadable downloadable, int retries) throws IOException {
        if (downloadable.getFile().isDirectory()) {
            throw new IOException("File is a directory: " + downloadable.getFile().toString());
        }

        if (downloadable.getFile().getParentFile() != null) {
            LauncherUtils.createDir(downloadable.getFile().getParentFile());
        }

        for (int i = 0; i < retries; i++) {
            try {
                download(downloadable.getFile(), downloadable.getURL());
                return;
            } catch (IOException ex) {
                Launcher.getInstance().getLog().error(ex);
            }
        }

        if (downloadable.getFile().isFile()) {
            Launcher.getInstance().getLog()
                    .log("Assuming our copy is good: %s", downloadable.getFile().toString());
        } else {
            Launcher.getInstance().getLog()
                    .log("Download failed: %s", downloadable.getFile().toString());
            throw new IOException("Download failed");
        }
    }

    private static HttpURLConnection makeConnection(URL url, String localEtag)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Launcher.getInstance().getProxy());
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
}
