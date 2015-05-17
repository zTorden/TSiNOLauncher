package amd.tsino.launcher;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import amd.tsino.launcher.ServerInfo;

public class LauncherConstants {
    // versions
    public static final String LAUNCHER_VERSION = "1.2.0 (Torden 2015) (amd 2014)";
    public static final String LAUNCHER_TITLE = "TSiNO Minecraft Launcher " + LAUNCHER_VERSION;
    public static final int VERSION_NUMERIC = 18;
    // download config
    public static final int DOWNLOAD_THREADS = 13;
    public static final int DOWNLOAD_RETRIES = 3;
    public static final int DOWNLOAD_TIMEOUT = 17000;
    // jvm args
    public static final String JVM_ARGS_32BIT = "-Xmx1G";
    public static final String JVM_ARGS_64BIT = "-Xmx2G";
    public static final Map<String,String> JVM_ARGS_VERSION; 
    static {
      JVM_ARGS_VERSION= new HashMap<String,String>();
      JVM_ARGS_VERSION.put("1.7","-XX:MaxPermSize=128M");
    }
    //Server info
    static {
    	ServerInfo.addServer("Server 1", "http://tsino.unet.by/minecraft/klient/", ".tsino_minecraft");
        ServerInfo.addServer("Server 2", "http://tsino.unet.by/minecraft/klient/client2/", ".tsino_minecraft2");
    }
    
    
    // files
    public static final String FS = System.getProperty("file.separator");
	public static final String LAUNCHER_DIRECTORY = ".tsino_launcher";
    public static final String AUTH_JSON = "launcher_profiles.json";
    public static final String STYLE_ZIP = "style.zip";
    public static final String CONFIG_ZIP = "config.zip";
    public static final String RESOURCES_ZIP = "assets.zip"; 
    public static final String RCPACK_ZIP = "rcpack.zip";
    public static final String RESOURCES_BASE = "assets/";
    public static final String RESOURCES_XML = RESOURCES_BASE + "assets.xml";
    public static final String MODS_BASE = "mods/";
    public static final String MODS_JSON = MODS_BASE + "mods.json";
    public static final String VERSIONS_BASE = "versions/";
    public static final String VERSIONS_JSON = VERSIONS_BASE + "versions.json";
    public static final String LIBRARIES_BASE = "libraries/";
    public static final String COREMODS_BASE = "coremods/";
    public static final String CONFIG_BASE = "config/";
    public static final String RCPACKS_BASE = "resourcepacks/";
    // urls
    public static final String LAUNCHERBASE_URL = "http://tsino.unet.by/minecraft/klient/";
    //"https://s3.amazonaws.com/Minecraft.Resources/";
    public static final URL STYLE_URL = LauncherUtils.getURL(LAUNCHERBASE_URL + STYLE_ZIP);
    //public static final URL CONFIG_URL = LauncherUtils.getURL(BASE_URL + CONFIG_BASE + CONFIG_ZIP);
    //public static final URL RESOURCES_URL = LauncherUtils.getURL(BASE_URL + RESOURCES_BASE + RESOURCES_ZIP);
    //public static final URL RCPACK_URL = LauncherUtils.getURL(BASE_URL + RCPACK_ZIP);
    public static final URL AUTH_URL = LauncherUtils.getURL("http://tsino.unet.by/minecraft/auth.php");
    public static final URL REGISTER_URL = LauncherUtils.getURL("http://tsino.unet.by/forum/register.php");
    // update buttons
    public static final String[] UPDATE_BUTTONS = new String[]{"tsino.unet.by"};
    public static final URL[] UPDATE_URLS = new URL[]{
            LauncherUtils.getURL("http://tsino.unet.by/resources/categories/minecraft.1/")
    };
    // utf-8 charset
    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
}
