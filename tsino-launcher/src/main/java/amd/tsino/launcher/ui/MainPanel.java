package amd.tsino.launcher.ui;

import amd.tsino.launcher.download.DownloadManager;
import amd.tsino.launcher.download.UpdateListener;
import amd.tsino.launcher.style.MainPanelStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

@SuppressWarnings("serial")
class MainPanel extends ImagePanel {
	private ImagePanel left;
    private AuthPanel auth;
    private ServerPanel server;
    private ProgressBar progress;

    public MainPanel(MainPanelStyle style) throws IOException {
        super(style);
        setLayout(null);
        setOpaque(true);

        left = new ImagePanel(style.left);
        auth = new AuthPanel(style.auth);
        server = new ServerPanel(style.server);
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
                if(Launcher.getInstance().authenticate())
                	((CardLayout)left.getLayout()).show(left, "server");
            	else
	                auth.enableAuth(true);
            }
        });
        
        ServerButton[] servers=server.getServerButtons();
        for(int i=0;i<servers.length;i++)
        	servers[i].addActionListener(new ActionListener(){
	            @Override
	            public void actionPerformed(ActionEvent actionEvent) {
	                Launcher.getInstance().getSettings().setServer(((ServerButton)actionEvent.getSource()).getServer());
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
                    SettingsPanel settingsPanel = new SettingsPanel();
                    int result = JOptionPane.showOptionDialog(MainPanel.this, settingsPanel, "Настройки", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.OK_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        Launcher.getInstance().getSettings().setJavaArgs(settingsPanel.getJavaArgs());
                        Launcher.getInstance().getSettings().setShowOnClose(settingsPanel.getShowOnClose());
                        for(String mod:Launcher.getInstance().getSettings().getDisabledMods().keySet())
                        	Launcher.getInstance().getSettings().setModDisabled(mod,settingsPanel.isModDisabled(mod));
                    }
                }
            }
        });
        
        left.setLayout(new CardLayout());
        left.add(auth,"auth");
        left.add(server,"server");
        add(new ImagePanel(style.header));
        add(left);
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
