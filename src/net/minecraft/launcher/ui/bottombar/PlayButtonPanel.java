package net.minecraft.launcher.ui.bottombar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.authentication.AuthenticationService;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.events.RefreshedVersionsListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;
import net.minecraft.launcher.updater.VersionManager;

public class PlayButtonPanel extends JPanel implements
		RefreshedProfilesListener, RefreshedVersionsListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6719881689813565300L;
	private final Launcher launcher;
	private final JButton playButton = new JButton("Старт");

	public PlayButtonPanel(Launcher launcher) {
		this.launcher = launcher;

		launcher.getProfileManager().addRefreshedProfilesListener(this);
		checkState();
		createInterface();

		this.playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayButtonPanel.this.getLauncher().getVersionManager()
						.getExecutorService().submit(new Runnable() {
							@Override
							public void run() {
								PlayButtonPanel.this.getLauncher()
										.getGameLauncher().playGame();
							}
						});
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

		if ((auth == null)
				|| (!auth.isLoggedIn())
				|| (this.launcher.getVersionManager().getVersions(
						profile.getVersionFilter()).isEmpty())) {
			this.playButton.setEnabled(false);
			this.playButton.setText("Старт");
		} else if (auth.getSelectedProfile() == null) {
			this.playButton.setEnabled(true);
			this.playButton.setText("Старт");
		} else if (auth.canPlayOnline()) {
			this.playButton.setEnabled(true);
			this.playButton.setText("Старт");
		} else {
			this.playButton.setEnabled(true);
			this.playButton.setText("Старт");
		}

		if (this.launcher.getGameLauncher().isWorking())
			this.playButton.setEnabled(false);
	}

	protected void createInterface() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = 1;
		constraints.weightx = 1.0D;
		constraints.weighty = 1.0D;

		constraints.gridy = 0;
		constraints.gridx = 0;
		add(this.playButton, constraints);

		this.playButton.setFont(this.playButton.getFont().deriveFont(1,
				this.playButton.getFont().getSize() + 2));
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
 * net.minecraft.launcher.ui.bottombar.PlayButtonPanel JD-Core Version: 0.6.2
 */