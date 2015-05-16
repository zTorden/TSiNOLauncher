package amd.tsino.launcher.ui;

import net.minecraft.launcher.Launcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("serial")
public class SettingsPanel extends JPanel {
    private Map<String,JCheckBox> modBoxes=new HashMap<>(); 
    private JCheckBox showBox;
    private JComboBox<String> argsBox;

    public SettingsPanel() {
        Map<String,Boolean>optionalMods=Launcher.getInstance().getSettings().getDisabledMods();
        for(String mod:optionalMods.keySet()){
        	JCheckBox modBox = new JCheckBox("Отключить "+mod);
        	modBox.setSelected(optionalMods.get(mod));
        	modBoxes.put(mod,modBox);
        }
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

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridLayout(3+modBoxes.size(), 1));
        argsBox.setEditable(true);
        for(JCheckBox modCheckBox:modBoxes.values())
        	add(modCheckBox);
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

    public boolean isModDisabled(String mod) {
        return modBoxes.get(mod).isSelected();
    }
}
