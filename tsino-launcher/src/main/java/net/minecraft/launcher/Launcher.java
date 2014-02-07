package net.minecraft.launcher;

import amd.tsino.launcher.GameLauncher;
import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherLog;
import amd.tsino.launcher.LauncherSettings;
import amd.tsino.launcher.auth.AuthenticationData;
import amd.tsino.launcher.auth.AuthenticationException;
import amd.tsino.launcher.auth.InvalidCredentialsException;
import amd.tsino.launcher.auth.UpdateLauncherException;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.DownloadManager;
import amd.tsino.launcher.download.Downloader;
import amd.tsino.launcher.style.LauncherStyle;
import amd.tsino.launcher.ui.LauncherFrame;
import amd.tsino.launcher.version.LauncherVersion;

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
    private LauncherStyle style;
    private DownloadManager downloads;
    private LauncherSettings settings;

    public Launcher(JFrame frame, File workDir, Proxy proxy,
                    Integer version) throws IOException {
        if (instance != null) {
            throw new RuntimeException("Should be only one instance");
        }
        instance = this;
        this.workDir = workDir;
        this.proxy = proxy;
        log = new LauncherLog();
        log.log("Launcher version %s started...",
                LauncherConstants.LAUNCHER_VERSION);
        style = new LauncherStyle();
        settings = new LauncherSettings();
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
        String sessionID = "null";
        try {
            sessionID = AuthenticationData.requestSessionID();
            settings.save();
        } catch (InvalidCredentialsException e) {
            frame.showLoginError();
            return;
        } catch (UpdateLauncherException e) {
            log.error(e);
            frame.showOutdatedNotice();
            return;
        } catch (IOException | AuthenticationException e) {
            log.error(e);
            if (frame.showOfflineNotice() != 0) {
                return;
            }
        }

        LauncherVersion v = new LauncherVersion();
        try {
            downloads.reset();
            log.log("Starting downloads...");
            v.updateArtifactLists();
            v.downloadArtifacts();
            downloads.waitFinish();
            Downloader.saveDatabase();

            if (downloads.getFailed() > 0) {
                StringBuilder line = new StringBuilder();
                line.append('[');
                for (DownloadJob job : downloads.getFailedJobs()) {
                    line.append(job.getFile().getName());
                    line.append(',');
                }
                if (line.length() > 0) line.deleteCharAt(line.length() - 1);
                line.append(']');
                throw new Exception("Download failed: " + line);
            }
        } catch (Exception e) {
            log.error(e);
            if (frame.showDownloadFailedNotice() != 0) {
                return;
            }
        }

        log.log("Extracting files...");
        v.extractFiles();

        frame.hide();

        log.log("Starting game...");
        try {
            GameLauncher.launchGame(v.getVersionFiles(), sessionID);
        } catch (Exception e) {
            log.error(e);
            frame.showLaunchFailedNotice();
        }

        if (settings.getShowOnClose()) {
            frame.show();
        } else {
            System.exit(0);
        }
    }

    public LauncherSettings getSettings() {
        return settings;
    }

    public DownloadManager getDownloads() {
        return downloads;
    }
}
