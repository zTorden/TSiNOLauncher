package amd.tsino.launcher;

import java.net.URL;

public class LauncherConstants {
	public static final String LAUNCHER_VERSION = "1.0.0 (amd 2013)";
	public static final int VERSION_NUMERIC = 17;
	public static final String AUTH_URL = "http://tsino.unet.by/minecraft/auth.php";
	public static final String BASE_URL = "http://tsino.unet.by/minecraft/klient/";
	public static final String STYLE_ZIP = "style.zip";
	public static final URL STYLE_URL = LaucherUtils
			.constantURL(LauncherConstants.BASE_URL + STYLE_ZIP);
	public static final int DOWNLOAD_THREADS = 13;
	public static final int DOWNLOAD_RETRIES = 7;
	public static final int DOWNLOAD_TIMEOUT = 11000;
}
