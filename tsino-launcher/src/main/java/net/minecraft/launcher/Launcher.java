package net.minecraft.launcher;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherLog;
import amd.tsino.launcher.auth.AuthenticationData;
import amd.tsino.launcher.auth.AuthenticationException;
import amd.tsino.launcher.auth.InvalidCredentialsException;
import amd.tsino.launcher.auth.UpdateLauncherException;
import amd.tsino.launcher.download.DownloadManager;
import amd.tsino.launcher.style.LauncherStyle;
import amd.tsino.launcher.ui.LauncherFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;

public class Launcher {
    private static Launcher instance;
    private LauncherFrame frame;
    private File workDir;
    private Proxy proxy;
    private LauncherLog log;
    private AuthenticationData auth;
    private LauncherStyle style;
    private DownloadManager downloads;

    public Launcher(JFrame frame, File workDir, Proxy proxy,
                    Integer version) throws IOException {
        instance = this;
        this.workDir = workDir;
        this.proxy = proxy;
        log = new LauncherLog();
        log.log("Launcher version %s started...",
                LauncherConstants.LAUNCHER_VERSION);
        style = new LauncherStyle();
        auth = new AuthenticationData();
        downloads = new DownloadManager();
        this.frame = new LauncherFrame(frame);
    }

    public static Launcher getInstance() {
        return instance;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public LauncherLog getLog() {
        return log;
    }

    public File getWorkDir() {
        return workDir;
    }

    public LauncherStyle getStyle() {
        return style;
    }

    public void launch() {
        auth.setCredentials(frame.getMainPanel().getAuth().getCredentials());
        String sessionID = null;
        try {
            sessionID = auth.requestSessionID();
            auth.save();
        } catch (InvalidCredentialsException e) {
            frame.getMainPanel().getAuth().showLoginError();
            return;
        } catch (UpdateLauncherException e) {
            log.error(e);
            frame.showOutdatedNotice();
            return;
        } catch (AuthenticationException e) {
            log.error(e);
            if (!frame.showOfflineNotice()) {
                return;
            }
        } catch (IOException e) {
            log.error(e);
            return;
        }

        log.log("Starting downloads...");
    }

    public AuthenticationData getAuth() {
        return auth;
    }

    public DownloadManager getDownloads() {
        return downloads;
    }
}
