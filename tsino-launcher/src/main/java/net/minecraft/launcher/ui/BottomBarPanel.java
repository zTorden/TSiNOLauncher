package net.minecraft.launcher.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.ui.bottombar.PlayButtonPanel;
import net.minecraft.launcher.ui.bottombar.PlayerInfoPanel;
import net.minecraft.launcher.ui.bottombar.ProfileSelectionPanel;

public class BottomBarPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4418011928212144485L;
	private final Launcher launcher;
	private final ProfileSelectionPanel profileSelectionPanel;
	private final PlayerInfoPanel playerInfoPanel;
	private final PlayButtonPanel playButtonPanel;

	public BottomBarPanel(Launcher launcher) {
		this.launcher = launcher;

		int border = 7;
		setBorder(new EmptyBorder(border, border, border, border));

		this.profileSelectionPanel = new ProfileSelectionPanel(launcher);
		this.playerInfoPanel = new PlayerInfoPanel(launcher);
		this.playButtonPanel = new PlayButtonPanel(launcher);

		createInterface();
	}

	protected void createInterface() {
		setLayout(new GridLayout(1, 3));

		final JPanel optionsPanel = new JPanel();
		final JButton options = new JButton("Параметры");
		optionsPanel.add(options);
		options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove(0);
				launcher.getLauncherPanel().getTabPanel().showAll();
				add(wrapSidePanel(profileSelectionPanel, 17), 0);
			}
		});
		add(wrapSidePanel(optionsPanel, 17));
		add(this.playButtonPanel);
		add(wrapSidePanel(this.playerInfoPanel, 13));
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public PlayButtonPanel getPlayButtonPanel() {
		return this.playButtonPanel;
	}

	public PlayerInfoPanel getPlayerInfoPanel() {
		return this.playerInfoPanel;
	}

	public ProfileSelectionPanel getProfileSelectionPanel() {
		return this.profileSelectionPanel;
	}

	protected JPanel wrapSidePanel(JPanel target, int side) {
		JPanel wrapper = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = side;
		constraints.weightx = 1.0D;
		constraints.weighty = 1.0D;

		wrapper.add(target, constraints);

		return wrapper;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.BottomBarPanel JD-Core Version: 0.6.2
 */