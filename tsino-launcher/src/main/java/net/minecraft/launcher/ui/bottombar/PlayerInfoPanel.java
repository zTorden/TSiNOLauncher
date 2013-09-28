package net.minecraft.launcher.ui.bottombar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.authentication.AuthenticationService;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.events.RefreshedVersionsListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;
import net.minecraft.launcher.updater.VersionManager;

public class PlayerInfoPanel extends JPanel implements
		RefreshedProfilesListener, RefreshedVersionsListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7417104029880905633L;
	private final Launcher launcher;
	private final JLabel welcomeText = new JLabel("", 0);
	private final JButton logOutButton = new JButton("Выход");

	public PlayerInfoPanel(final Launcher launcher) {
		this.launcher = launcher;

		launcher.getProfileManager().addRefreshedProfilesListener(this);
		checkState();
		createInterface();

		this.logOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launcher.getProfileManager().getSelectedProfile()
						.setPlayerUUID(null);
				launcher.getProfileManager().trimAuthDatabase();
				launcher.showLoginPrompt();
			}
		});
	}

	public void checkState() {
		Profile profile = this.launcher.getProfileManager().getProfiles()
				.isEmpty() ? null : this.launcher.getProfileManager()
				.getSelectedProfile();
		AuthenticationService auth = profile == null ? null : this.launcher
				.getProfileManager().getAuthDatabase()
				.getByUUID(profile.getPlayerUUID());

		if ((auth == null) || (!auth.isLoggedIn())) {
			this.welcomeText.setText("Добро пожаловать, гость!");
			this.logOutButton.setEnabled(false);
		} else if (auth.getSelectedProfile() == null) {
			this.welcomeText.setText("<html>Добро пожаловать, игрок!</html>");
			this.logOutButton.setEnabled(true);
		} else {
			this.welcomeText.setText("<html>Добро пожаловать, <b>"
					+ auth.getSelectedProfile().getName() + "</b></html>");
			this.logOutButton.setEnabled(true);
		}
	}

	protected void createInterface() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = 2;

		constraints.gridy = 0;

		constraints.weightx = 1.0D;
		constraints.gridwidth = 2;
		add(this.welcomeText, constraints);
		constraints.gridwidth = 1;
		constraints.weightx = 0.0D;

		constraints.gridy += 1;

		constraints.weightx = 0.5D;
		constraints.fill = 0;
		add(this.logOutButton, constraints);
		constraints.weightx = 0.0D;

		constraints.gridy += 1;
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	@Override
	public void onProfilesRefreshed(ProfileManager manager) {
		checkState();
	}

	@Override
	public void onVersionsRefreshed(VersionManager manager) {
		checkState();
	}

	@Override
	public boolean shouldReceiveEventsInUIThread() {
		return true;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.bottombar.PlayerInfoPanel JD-Core Version: 0.6.2
 */