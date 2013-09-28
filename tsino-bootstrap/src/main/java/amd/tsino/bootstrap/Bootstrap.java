package amd.tsino.bootstrap;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;

public class Bootstrap implements Runnable {
	public static final int BOOTSTRAP_VERSION = 5;
	public static final URL LAUNCHER_URL = Util
			.constantURL("http://tsino.unet.by/minecraft/klient/launcher.jar");
	public static final String LAUNCHER_FILE_NAME = "launcher.jar";
	private static Bootstrap instance = null;
	private Proxy proxy = Proxy.NO_PROXY;
	private File workingDir = Util.getDefaultWorkingDir();
	private BootstrapFrame frame = new BootstrapFrame();

	public static Bootstrap getInstance() {
		if (instance == null) {
			instance = new Bootstrap();
		}
		return instance;
	}

	private Bootstrap() {
	}

	public void error(String format, Object... args) {
		log("[ERROR] " + format, args);
	}

	public void error(Throwable t) {
		error("%s", t);
		t.printStackTrace();
	}

	public Proxy getProxy() {
		return proxy;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	private void launch() throws Exception {
		proxy = ProxySelector.getDefault().select(LAUNCHER_URL.toURI()).get(0);

		if ((workingDir.exists()) && (!workingDir.isDirectory())) {
			error("Invalid working directory: %s", workingDir.toString());
			throw new BootstrapException("Invalid working directory");
		}

		if ((!workingDir.exists()) && (!workingDir.mkdirs())) {
			error("Unable to create directory: %s", workingDir.toString());
			throw new BootstrapException("Unable to create directory");
		}

		printSystemInfo();
		File launcherFile = new File(workingDir, LAUNCHER_FILE_NAME);
		new Downloader(LAUNCHER_URL, launcherFile).download(3);
		EtagDatabase.getInstance().saveDatabase();
		startLauncher(launcherFile);
	}

	public void log(String format, Object... args) {
		format += "\n";
		System.out.printf(format, args);
		frame.printf(format, args);
	}

	private void printSystemInfo() {
		log("Bootstrap (v%s) starting...", BOOTSTRAP_VERSION);
		log("Current time is %s",
				DateFormat.getDateTimeInstance(2, 2, Locale.US)
						.format(new Date()).toString());
		log("System.getProperty('os.name') == '%s'",
				System.getProperty("os.name"));
		log("System.getProperty('os.version') == '%s'",
				System.getProperty("os.version"));
		log("System.getProperty('os.arch') == '%s'",
				System.getProperty("os.arch"));
		log("System.getProperty('java.version') == '%s'",
				System.getProperty("java.version"));
		log("System.getProperty('java.vendor') == '%s'",
				System.getProperty("java.vendor"));
		log("System.getProperty('sun.arch.data.model') == '%s'",
				System.getProperty("sun.arch.data.model"));
		log("");
	}

	@Override
	public void run() {
		frame.setVisible(true);

		try {
			launch();
		} catch (Throwable t) {
			error(t);
			log("\n\nPlease, restart the launcher!");
		}
	}

	@SuppressWarnings("resource")
	public void startLauncher(File launcherJar) throws BootstrapException {
		log("Starting launcher...");
		try {
			Class<?> aClass = new URLClassLoader(new URL[] { launcherJar
					.toURI().toURL() })
					.loadClass("net.minecraft.launcher.Launcher");
			Constructor<?> constructor = aClass.getConstructor(new Class[] {
					JFrame.class, File.class, Proxy.class, Integer.class });
			constructor.newInstance(new Object[] { this.frame, this.workingDir,
					this.proxy, BOOTSTRAP_VERSION });
		} catch (Exception e) {
			throw new BootstrapException("Unable to start: "
					+ launcherJar.getName(), e);
		}
	}
}
