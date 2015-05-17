package amd.tsino.launcher.style;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.Downloader;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LauncherStyle {
    private static final String STYLE_JSON = "style.json";
    private static final Charset STYLE_CHARSET = Charset.forName("utf-8");
    private final File styleZip;
    private MainPanelStyle mainPanelStyle;

    public LauncherStyle() throws IOException {
        styleZip = LauncherUtils.getLauncherFile(LauncherConstants.STYLE_ZIP);
        new DownloadJob(styleZip, LauncherConstants.STYLE_URL).run();
        Downloader.saveDatabase();
        Reader reader = new InputStreamReader(getFile(STYLE_JSON),
                STYLE_CHARSET);
        final Gson gson = new Gson();
        mainPanelStyle = gson.fromJson(reader, MainPanelStyle.class);
        reader.close();
    }

    private InputStream getFile(String name) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(styleZip));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            if (ze.getName().equals(name)) {
                return zis;
            }
            ze = zis.getNextEntry();
        }
        InputStream stream = getClass().getResourceAsStream("/" + name);
        if (stream == null) {
            throw new FileNotFoundException(name);
        }
        return stream;
    }

    public BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(getFile(name));
    }

    public MainPanelStyle getMainPanelStyle() {
        return mainPanelStyle;
    }
}
