package net.minecraft.launcher.ui.bottombar;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;
import net.minecraft.launcher.ui.popups.profile.ProfileEditorPopup;

public class ProfileSelectionPanel extends JPanel implements ActionListener,
		ItemListener, RefreshedProfilesListener {
	private static class ProfileListRenderer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4261620060891045879L;

		@Override
		@SuppressWarnings("rawtypes")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if ((value instanceof Profile)) {
				value = ((Profile) value).getName();
			}

			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			return this;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7901872501492963801L;
	private final JComboBox<Profile> profileList = new JComboBox<Profile>();
	private final JButton newProfileButton = new JButton("New Profile");
	private final JButton editProfileButton = new JButton("Edit Profile");
	private final Launcher launcher;

	private boolean skipSelectionUpdate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ProfileSelectionPanel(Launcher launcher) {
		this.launcher = launcher;

		this.profileList.setRenderer(new ProfileListRenderer());
		this.profileList.addItemListener(this);
		((JComboBox) this.profileList).addItem("Загрузка профилей...");

		this.newProfileButton.addActionListener(this);
		this.editProfileButton.addActionListener(this);

		createInterface();

		launcher.getProfileManager().addRefreshedProfilesListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.newProfileButton) {
			Profile profile = new Profile(this.launcher.getProfileManager()
					.getSelectedProfile());
			profile.setName("Copy of " + profile.getName());

			while (this.launcher.getProfileManager().getProfiles()
					.containsKey(profile.getName())) {
				profile.setName(profile.getName() + "_");
			}

			ProfileEditorPopup.showEditProfileDialog(getLauncher(), profile);
			this.launcher.getProfileManager().setSelectedProfile(
					profile.getName());
		} else if (e.getSource() == this.editProfileButton) {
			Profile profile = this.launcher.getProfileManager()
					.getSelectedProfile();
			ProfileEditorPopup.showEditProfileDialog(getLauncher(), profile);
		}
	}

	protected void createInterface() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = 2;
		constraints.weightx = 0.0D;

		constraints.gridy = 0;

		add(new JLabel("Profile: "), constraints);
		constraints.gridx = 1;
		add(this.profileList, constraints);
		constraints.gridx = 0;

		constraints.gridy += 1;

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
		buttonPanel.add(this.newProfileButton);
		buttonPanel.add(this.editProfileButton);

		constraints.gridwidth = 2;
		add(buttonPanel, constraints);
		constraints.gridwidth = 1;

		constraints.gridy += 1;
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != 1)
			return;

		if ((!this.skipSelectionUpdate) && ((e.getItem() instanceof Profile))) {
			Profile profile = (Profile) e.getItem();
			this.launcher.getProfileManager().setSelectedProfile(
					profile.getName());
			try {
				this.launcher.getProfileManager().saveProfiles();
			} catch (IOException e1) {
				this.launcher.println("Couldn't save new selected profile", e1);
			}
			this.launcher.ensureLoggedIn();
		}
	}

	@Override
	public void onProfilesRefreshed(ProfileManager manager) {
		populateProfiles();
	}

	public void populateProfiles() {
		String previous = this.launcher.getProfileManager()
				.getSelectedProfile().getName();
		Profile selected = null;
		Collection<Profile> profiles = this.launcher.getProfileManager()
				.getProfiles().values();
		this.profileList.removeAllItems();

		this.skipSelectionUpdate = true;

		for (Profile profile : profiles) {
			if (previous.equals(profile.getName())) {
				selected = profile;
			}

			this.profileList.addItem(profile);
		}

		if (selected == null) {
			if (profiles.isEmpty()) {
				selected = this.launcher.getProfileManager()
						.getSelectedProfile();
				this.profileList.addItem(selected);
			}

			selected = profiles.iterator().next();
		}

		this.profileList.setSelectedItem(selected);
		this.skipSelectionUpdate = false;
	}

	@Override
	public boolean shouldReceiveEventsInUIThread() {
		return true;
	}

}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.bottombar.ProfileSelectionPanel JD-Core Version:
 * 0.6.2
 */