package net.minecraft.launcher;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

import javax.swing.JFrame;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherLog;
import amd.tsino.launcher.auth.AuthenticationData;
import amd.tsino.launcher.style.LauncherStyle;
import amd.tsino.launcher.ui.LauncherFrame;

public class Launcher {
	private static Launcher instance;
	private LauncherFrame frame;
	private File workDir;
	private Proxy proxy;
	private LauncherLog log;
	private AuthenticationData auth;
	private LauncherStyle style;

	public Launcher(JFrame frame, File workDir, Proxy proxy,
			Integer bootstrapVersion) throws IOException {
		instance = this;
		this.workDir = workDir;
		this.proxy = proxy;
		log = new LauncherLog();
		log.log("Launcher version %s started...",
				LauncherConstants.LAUNCHER_VERSION);
		auth = new AuthenticationData();
		style = new LauncherStyle();
		this.frame = new LauncherFrame(frame);
	}

	public LauncherFrame getFrame() {
		return frame;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public static Launcher getInstance() {
		return instance;
	}

	public LauncherLog getLog() {
		return log;
	}

	public File getWorkDir() {
		return workDir;
	}

	public AuthenticationData getAuth() {
		return auth;
	}

	public LauncherStyle getStyle() {
		return style;
	}
}
