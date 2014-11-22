package amd.tsino.launcher;

import net.minecraft.launcher.Launcher;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LauncherUtils {

	public static void openLink(URL link) {
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(
                    null);
            desktopClass.getMethod("browse", new Class[]{URI.class}).invoke(o, link.toURI());
        } catch (Exception e) {
            Launcher.getInstance().getLog().error(e);
        }
    }

    private static String buildQuery(Map<String, Object> query)
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

    private static String performPost(URL url, String parameters, Proxy proxy,
                                      String contentType, boolean returnErrorPage) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection(proxy);
        byte[] paramAsBytes = parameters.getBytes(LauncherConstants.DEFAULT_CHARSET);

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
                    connection.getInputStream(), LauncherConstants.DEFAULT_CHARSET));
        } catch (IOException e) {
            if (returnErrorPage) {
                InputStream stream = connection.getErrorStream();
                if (stream != null)
                    reader = new BufferedReader(new InputStreamReader(stream, LauncherConstants.DEFAULT_CHARSET));
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

    public static String getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return "windows";
        if (osName.contains("mac")) return "osx";
        if (osName.contains("linux") || osName.contains("unix")) return "linux";
        return "unknown";
    }

    public static void createDir(File dir) {
        if (!dir.isDirectory()) {
            final boolean created = dir.mkdirs();
            if (created) {
                Launcher.getInstance().getLog().log("Directory created: %s", dir.getPath());
            }
        }
    }

    public static URL getURL(String input) {
        try {
            return new URL(input);
        } catch (MalformedURLException e) {
            Launcher.getInstance().getLog().error(e);
            throw new Error(e);
        }
    }

    public static File getFile(String name) {
        return new File(Launcher.getInstance().getWorkDir(), name);
    }

    public static void unzip(File zip, File dir, List<String> exclude) throws IOException {
    	unzip(zip, dir, exclude, true);
    }
    
    public static void unzipWithoutReplace(File zip, File dir, List<String> exclude) throws IOException {
    	unzip(zip, dir, exclude, false);
    }
    
    private static void unzip(File zip, File dir, List<String> exclude, boolean replaceExisting) throws IOException {
        LauncherUtils.createDir(dir);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                boolean extract = true;
                if (exclude != null) {
                    for (String prefix : exclude) {
                        if (fileName.startsWith(prefix)) {
                            extract = false;
                            break;
                        }
                    }
                }
                if (extract) {
                    File newFile = new File(dir, fileName);
                    if (ze.isDirectory()) {
                        Launcher.getInstance().getLog().log("Creating directory: %s", newFile.getAbsolutePath());
                        LauncherUtils.createDir(newFile);
                    } else {
                    	if(newFile.exists()&&!replaceExisting)
                            Launcher.getInstance().getLog().log("Skipping unzip: file %s exists.", newFile.getAbsolutePath());
                    	else
                    	{
                            Launcher.getInstance().getLog().log("File unzip: %s", newFile.getAbsolutePath());
	                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
	                            int len;
	                            byte[] buffer = new byte[4096];
	                            while ((len = zis.read(buffer)) > 0) {
	                                fos.write(buffer, 0, len);
	                            }
	                        }
                    	}
                    }
                }
                ze = zis.getNextEntry();
            }
        }
    }

    public static void copyData(InputStream in, OutputStream out) throws IOException {
        int count;
        byte[] buffer = new byte[8192];
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
    }

    public static void safeSleep(long l) {
        while (true) {
            try {
                Thread.sleep(l);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
