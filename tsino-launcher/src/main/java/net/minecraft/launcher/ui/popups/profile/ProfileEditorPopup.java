package net.minecraft.launcher.ui.popups.profile;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;

public class ProfileEditorPopup extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8971703740777032255L;

	public static void showEditProfileDialog(Launcher launcher, Profile profile) {
		JDialog dialog = new JDialog(launcher.getFrame(), "Profile Editor",
				true);
		ProfileEditorPopup editor = new ProfileEditorPopup(launcher, profile);
		dialog.add(editor);
		dialog.pack();
		dialog.setLocationRelativeTo(launcher.getFrame());
		dialog.setVisible(true);
	}

	private final Launcher launcher;
	private final Profile originalProfile;
	private final Profile profile;
	private final JButton saveButton = new JButton("Save Profile");
	private final JButton cancelButton = new JButton("Cancel");
	private final ProfileInfoPanel profileInfoPanel;
	private final ProfileVersionPanel profileVersionPanel;

	private final ProfileJavaPanel javaInfoPanel;

	public ProfileEditorPopup(Launcher launcher, Profile profile) {
		super(true);

		this.launcher = launcher;
		this.originalProfile = profile;
		this.profile = new Profile(profile);
		this.profileInfoPanel = new ProfileInfoPanel(this);
		this.profileVersionPanel = new ProfileVersionPanel(this);
		this.javaInfoPanel = new ProfileJavaPanel(this);

		this.saveButton.addActionListener(this);
		this.cancelButton.addActionListener(this);

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 5));
		createInterface();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.saveButton) {
			try {
				ProfileManager manager = this.launcher.getProfileManager();
				Map<String, Profile> profiles = manager.getProfiles();

				if (!this.originalProfile.getName().equals(
						this.profile.getName())) {
					profiles.remove(this.originalProfile.getName());

					while (profiles.containsKey(this.profile.getName())) {
						this.profile.setName(this.profile.getName() + "_");
					}
				}

				profiles.put(this.profile.getName(), this.profile);

				manager.saveProfiles();
				manager.fireRefreshEvent();
			} catch (IOException ex) {
				this.launcher.println("Couldn't save profiles whilst editing "
						+ this.profile.getName(), ex);
			}
		}

		Window window = (Window) getTopLevelAncestor();
		window.dispatchEvent(new WindowEvent(window, 201));
	}

	protected void createInterface() {
		JPanel standardPanels = new JPanel(true);
		standardPanels.setLayout(new BoxLayout(standardPanels, 1));
		standardPanels.add(this.profileInfoPanel);
		standardPanels.add(this.profileVersionPanel);
		standardPanels.add(this.javaInfoPanel);

		add(standardPanels, "Center");

		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout(new BoxLayout(buttonPannel, 0));
		buttonPannel.add(this.cancelButton);
		buttonPannel.add(Box.createGlue());
		buttonPannel.add(this.saveButton);
		add(buttonPannel, "South");
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public Profile getProfile() {
		return this.profile;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.popups.profile.ProfileEditorPopup JD-Core Version:
 * 0.6.2
 */