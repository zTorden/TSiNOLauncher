package amd.tsino.launcher.ui;

import amd.tsino.launcher.download.DownloadManager;
import amd.tsino.launcher.download.UpdateListener;
import amd.tsino.launcher.style.MainPanelStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

@SuppressWarnings("serial")
class MainPanel extends ImagePanel {
    private AuthPanel auth;
    private ProgressBar progress;

    public MainPanel(MainPanelStyle style) throws IOException {
        super(style);
        setLayout(null);
        setOpaque(true);

        auth = new AuthPanel(style.auth);
        progress = new ProgressBar(style.progress);
        progress.setVisible(false);
        Launcher.getInstance().getDownloads().addUpdateListener(new UpdateListener() {
            @Override
            public void updated(DownloadManager manager) {
                progress.setIndeterminate(manager.getTotal() < 1);
                progress.setMaximum(manager.getTotal());
                progress.setValue(manager.getFinished() + manager.getFailed());
            }
        });

        auth.getEnter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                auth.enableAuth(false);
                Launcher.getInstance().getSettings().setCredentials(auth.getCredentials());
                progress.setVisible(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Launcher.getInstance().launch();
                        progress.setVisible(false);
                        auth.enableAuth(true);
                    }
                }).start();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() < 16 && e.getY() < 16) {
                    String args = JOptionPane.showInputDialog("Java Arguments:",
                            Launcher.getInstance().getSettings().getJavaArgs());
                    Launcher.getInstance().getSettings().setJavaArgs(args);
                }
            }
        });

        add(new ImagePanel(style.header));
        add(auth);
        add(new NewsPanel(style.news));
        add(progress);
    }

    public AuthPanel getAuth() {
        return auth;
    }

    public ProgressBar getProgress() {
        return progress;
    }
}
