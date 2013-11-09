package amd.tsino.launcher.ui;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LauncherFrame {
    private JFrame frame;
    private MainPanel mainPanel;

    public LauncherFrame(JFrame frame) throws IOException {
        this.frame = frame;

        mainPanel = new MainPanel(Launcher.getInstance().getStyle()
                .getMainPanelStyle());
        mainPanel.setVisible(true);

        frame.setVisible(false);
        frame.getContentPane().removeAll();
        frame.setBackground(Color.DARK_GRAY);
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(mainPanel.getAuth().getEnter());
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void showOutdatedNotice() {
        String error = "Извините, у Вас старая версия лаунчера.\n"
                + "Пожалуйста, скачайте новый лаунчер.";

        int result = JOptionPane.showOptionDialog(frame, error,
                "Outdated launcher", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                LauncherConstants.UPDATE_BUTTONS,
                LauncherConstants.UPDATE_BUTTONS[1]);

        if (result < LauncherConstants.UPDATE_URLS.length - 1) {
            try {
                LauncherUtils.openLink(LauncherConstants.UPDATE_URLS[result].toURI());
            } catch (Exception e) {
                Launcher.getInstance().getLog().error(e);
            }
        }
    }

    public boolean showOfflineNotice() {
        String message = "Извините, не удалось подключиться к серверу.\n" +
                "Проверьте Ваше интернет-соединение.\n\n" +
                "Попытаться запустить игру в оффлайн режиме?";

        int result = JOptionPane.showConfirmDialog(frame, message,
                "Offline", JOptionPane.YES_NO_OPTION);
        return result == 0;
    }
}
