package amd.tsino.launcher;

import java.net.URL;
import java.nio.charset.Charset;

public class LauncherConstants {
    // versions
    public static final String LAUNCHER_VERSION = "1.0.0 (amd 2013)";
    public static final String LAUNCHER_TITLE = "TSiNO Minecraft Launcher " + LAUNCHER_VERSION;
    public static final int VERSION_NUMERIC = 17;
    // download config
    public static final int DOWNLOAD_THREADS = 13;
    public static final int DOWNLOAD_RETRIES = 3;
    public static final int DOWNLOAD_TIMEOUT = 17000;
    // files
    public static final String AUTH_JSON = "launcher_profiles.json";
    public static final String STYLE_ZIP = "style.zip";
    public static final String CONFIG_ZIP = "config.zip";
    public static final String RCPACK_ZIP = "rcpack.zip";
    public static final String RESOURCES_XML = "assets.xml";
    public static final String RESOURCES_BASE = "assets/";
    public static final String MODS_BASE = "mods/";
    public static final String MODS_JSON = "mods.json";
    public static final String VERSIONS_BASE = "versions/";
    public static final String VERSIONS_JSON = VERSIONS_BASE + "versions.json";
    public static final String LIBRARIES_BASE = "libraries/";
    public static final String COREMODS_BASE = "coremods/";
    public static final String CONFIG_BASE = "config/";
    public static final String RCPACKS_BASE = "resourcepacks/";
    // urls
    public static final String BASE_URL = "http://tsino.unet.by/minecraft/klient/";
    public static final String RESOURCES_URL = "https://s3.amazonaws.com/Minecraft.Resources/";
    public static final URL STYLE_URL = LauncherUtils.getURL(BASE_URL + STYLE_ZIP);
    public static final URL CONFIG_URL = LauncherUtils.getURL(BASE_URL + "config/" + CONFIG_ZIP);
    public static final URL RCPACK_URL = LauncherUtils.getURL(BASE_URL + RCPACK_ZIP);
    public static final URL AUTH_URL = LauncherUtils.getURL("http://tsino.unet.by/minecraft/auth.php");
    public static final URL REGISTER_URL = LauncherUtils.getURL("http://tsino.unet.by/forum/register.php");
    // update buttons
    public static final String[] UPDATE_BUTTONS = new String[]{"tsino.unet.by", "dropbox.com"};
    public static final URL[] UPDATE_URLS = new URL[]{
            LauncherUtils.getURL("http://tsino.unet.by/forum/downloads.php?do=file&id=18"),
            LauncherUtils.getURL("https://www.dropbox.com/sh/9khygz2yc50iv5l/XHwtKRL3K9")
    };
    // utf-8 charset
    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
}
