package amd.tsino.launcher.ui;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import net.minecraft.launcher.Launcher;

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
}
