package amd.tsino.launcher.ui;

import net.minecraft.launcher.Launcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JCheckBox disableBox;
    private JCheckBox showBox;
    private JComboBox<String> argsBox;

    public SettingsPanel() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridLayout(4, 1));
        disableBox = new JCheckBox("Отключить OptiFine");
        disableBox.setSelected(Launcher.getInstance().getSettings().getDisableOptiFine());
        showBox = new JCheckBox("Показывать лаунчер после закрытия клиента");
        showBox.setSelected(Launcher.getInstance().getSettings().getShowOnClose());
        JLabel argsLabel = new JLabel("Аргументы Java:");
        argsBox = new JComboBox<>(new String[]{
                Launcher.getInstance().getSettings().getJavaArgs(),
                "-Xmx512m",
                "-Xmx764m",
                "-Xmx1G",
                "-Xmx2G",
                "-Xmx3G"
        });
        argsBox.setEditable(true);
        add(disableBox);
        add(showBox);
        add(argsLabel);
        add(argsBox);
    }

    public String getJavaArgs() {
        return argsBox.getSelectedItem().toString();
    }

    public boolean getShowOnClose() {
        return showBox.isSelected();
    }

    public boolean getDisableOptiFine() {
        return disableBox.isSelected();
    }
}
