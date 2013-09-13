package net.minecraft.launcher.ui.popups.profile;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.minecraft.launcher.events.RefreshedVersionsListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.updater.VersionManager;
import net.minecraft.launcher.updater.VersionSyncInfo;
import net.minecraft.launcher.versions.ReleaseType;
import net.minecraft.launcher.versions.Version;

public class ProfileVersionPanel extends JPanel implements
		RefreshedVersionsListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6289037093403953785L;
	private final ProfileEditorPopup editor;
	private final JComboBox<VersionSyncInfo> versionList = new JComboBox<VersionSyncInfo>();
	private final List<ReleaseTypeCheckBox> customVersionTypes = new ArrayList<ReleaseTypeCheckBox>();

	public ProfileVersionPanel(ProfileEditorPopup editor) {
		this.editor = editor;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Version Selection"));

		createInterface();
		addEventHandlers();

		List<VersionSyncInfo> versions = editor.getLauncher()
				.getVersionManager()
				.getVersions(editor.getProfile().getVersionFilter());

		if (versions.isEmpty())
			editor.getLauncher().getVersionManager()
					.addRefreshedVersionsListener(this);
		else
			populateVersions(versions);
	}

	protected void createInterface() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = 17;

		constraints.gridy = 0;

		for (ReleaseType type : ReleaseType.values()) {
			if (type.getDescription() != null) {
				ReleaseTypeCheckBox checkbox = new ReleaseTypeCheckBox(type);
				checkbox.setSelected(this.editor.getProfile()
						.getVersionFilter().getTypes().contains(type));
				this.customVersionTypes.add(checkbox);

				constraints.fill = 2;
				constraints.weightx = 1.0D;
				constraints.gridwidth = 0;
				add(checkbox, constraints);
				constraints.gridwidth = 1;
				constraints.weightx = 0.0D;
				constraints.fill = 0;

				constraints.gridy += 1;
			}
		}
		add(new JLabel("Use version:"), constraints);
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		add(this.versionList, constraints);
		constraints.weightx = 0.0D;
		constraints.fill = 0;

		constraints.gridy += 1;

		this.versionList.setRenderer(new VersionListRenderer());
	}

	protected void addEventHandlers() {
		this.versionList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				ProfileVersionPanel.this.updateVersionSelection();
			}

		});
		for (final ReleaseTypeCheckBox type : this.customVersionTypes)
			type.addItemListener(new ItemListener() {
				private boolean isUpdating = false;

				public void itemStateChanged(ItemEvent e) {
					if (this.isUpdating)
						return;
					if ((e.getStateChange() == 1)
							&& (type.getType().getPopupWarning() != null)) {
						int result = JOptionPane
								.showConfirmDialog(
										ProfileVersionPanel.this.editor
												.getLauncher().getFrame(),
										type.getType().getPopupWarning()
												+ "\n\nAre you sure you want to continue?");

						this.isUpdating = true;
						if (result == 0) {
							type.setSelected(true);
							ProfileVersionPanel.this
									.updateCustomVersionFilter();
						} else {
							type.setSelected(false);
						}
						this.isUpdating = false;
					} else {
						ProfileVersionPanel.this.updateCustomVersionFilter();
					}
				}

			});
	}

	private void updateCustomVersionFilter() {
		Profile profile = this.editor.getProfile();
		Set<ReleaseType> newTypes = new HashSet<ReleaseType>(
				Profile.DEFAULT_RELEASE_TYPES);

		for (ReleaseTypeCheckBox type : this.customVersionTypes) {
			if (type.isSelected())
				newTypes.add(type.getType());
			else {
				newTypes.remove(type.getType());
			}
		}

		if (newTypes.equals(Profile.DEFAULT_RELEASE_TYPES))
			profile.setAllowedReleaseTypes(null);
		else {
			profile.setAllowedReleaseTypes(newTypes);
		}

		populateVersions(this.editor.getLauncher().getVersionManager()
				.getVersions(this.editor.getProfile().getVersionFilter()));
		this.editor.getLauncher().getVersionManager()
				.removeRefreshedVersionsListener(this);
	}

	private void updateVersionSelection() {
		Object selection = this.versionList.getSelectedItem();

		if ((selection instanceof VersionSyncInfo)) {
			Version version = ((VersionSyncInfo) selection).getLatestVersion();
			this.editor.getProfile().setLastVersionId(version.getId());
		} else {
			this.editor.getProfile().setLastVersionId(null);
		}
	}

	private void populateVersions(List<VersionSyncInfo> versions) {
		String previous = this.editor.getProfile().getLastVersionId();
		VersionSyncInfo selected = null;

		this.versionList.removeAllItems();
		((JComboBox)this.versionList).addItem("Use Latest Version");

		for (VersionSyncInfo version : versions) {
			if (version.getLatestVersion().getId().equals(previous)) {
				selected = version;
			}

			this.versionList.addItem(version);
		}

		if ((selected == null) && (!versions.isEmpty()))
			this.versionList.setSelectedIndex(0);
		else
			this.versionList.setSelectedItem(selected);
	}

	public void onVersionsRefreshed(VersionManager manager) {
		List<VersionSyncInfo> versions = manager.getVersions(this.editor
				.getProfile().getVersionFilter());
		populateVersions(versions);
		this.editor.getLauncher().getVersionManager()
				.removeRefreshedVersionsListener(this);
	}

	public boolean shouldReceiveEventsInUIThread() {
		return true;
	}

	public static class ReleaseTypeCheckBox extends JCheckBox {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1845265106843550912L;
		private final ReleaseType type;

		private ReleaseTypeCheckBox(ReleaseType type) {
			super();
			this.type = type;
		}

		public ReleaseType getType() {
			return this.type;
		}

	}

	private static class VersionListRenderer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7668031930621404340L;

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if ((value instanceof VersionSyncInfo)) {
				VersionSyncInfo syncInfo = (VersionSyncInfo) value;
				Version version = syncInfo.getLatestVersion();

				value = String.format("%s %s", new Object[] {
						version.getType().getName(), version.getId() });
			}

			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			return this;
		}

	}

}
