package amd.tsino.launcher.ui;

import net.minecraft.launcher.Launcher;

import javax.swing.*;
import java.awt.*;

public class ErrorPanel extends JPanel {
    public ErrorPanel(String message) {
        JTextArea area = new JTextArea();
        area.setText(Launcher.getInstance().getLog().getDump());

        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(300, 300));

        setLayout(new BorderLayout());
        add(new JLabel(message), BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
    }
}
