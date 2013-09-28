package net.minecraft.launcher.ui.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.LauncherConstants;
import net.minecraft.launcher.authentication.AuthenticationDatabase;
import net.minecraft.launcher.authentication.AuthenticationService;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;
import net.minecraft.launcher.ui.popups.profile.ProfileEditorPopup;

public class ProfileListTab extends JScrollPane implements
		RefreshedProfilesListener {
	private class ProfileTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5971020761573278260L;
		private final List<Profile> profiles = new ArrayList<Profile>();

		private ProfileTableModel() {
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return NUM_COLUMNS;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case COLUMN_AUTHENTICATION:
				return "Username";
			case COLUMN_VERSION:
				return "Version";
			case COLUMN_NAME:
				return "Version name";
			}
			return super.getColumnName(column);
		}

		@Override
		public int getRowCount() {
			return this.profiles.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Profile profile = this.profiles.get(rowIndex);
			AuthenticationDatabase authDatabase = ProfileListTab.this.launcher
					.getProfileManager().getAuthDatabase();
			AuthenticationService auth = authDatabase.getByUUID(profile
					.getPlayerUUID());

			switch (columnIndex) {
			case COLUMN_NAME:
				return profile.getName();
			case COLUMN_AUTHENTICATION:
				if ((auth != null) && (auth.getSelectedProfile() != null)) {
					return auth.getSelectedProfile().getName();
				}
				return "(Not logged in)";
			case COLUMN_VERSION:
				if (profile.getLastVersionId() == null) {
					return "(Latest version)";
				}
				return profile.getLastVersionId();
			}

			return null;
		}

		public void setProfiles(Collection<Profile> profiles) {
			this.profiles.clear();
			this.profiles.addAll(profiles);
			fireTableDataChanged();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4116416784904384279L;
	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_VERSION = 1;
	private static final int COLUMN_AUTHENTICATION = 2;
	private static final int NUM_COLUMNS = 3;
	private final Launcher launcher;
	private final ProfileTableModel dataModel = new ProfileTableModel();
	private final JTable table = new JTable(this.dataModel);
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem addProfileButton = new JMenuItem("Add Profile");
	private final JMenuItem copyProfileButton = new JMenuItem("Copy Profile");

	private final JMenuItem deleteProfileButton = new JMenuItem(
			"Delete Profile");

	public ProfileListTab(Launcher launcher) {
		this.launcher = launcher;

		setViewportView(this.table);
		createInterface();

		launcher.getProfileManager().addRefreshedProfilesListener(this);
	}

	protected void createInterface() {
		this.popupMenu.add(this.addProfileButton);
		this.popupMenu.add(this.copyProfileButton);
		this.popupMenu.add(this.deleteProfileButton);

		this.table.setFillsViewportHeight(true);
		this.table.setSelectionMode(0);

		this.popupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				int[] selection = ProfileListTab.this.table.getSelectedRows();
				boolean hasSelection = (selection != null)
						&& (selection.length > 0);

				ProfileListTab.this.copyProfileButton.setEnabled(hasSelection);
				ProfileListTab.this.deleteProfileButton
						.setEnabled(hasSelection);
			}
		});
		this.addProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile profile = new Profile();
				profile.setName("New Profile");

				while (ProfileListTab.this.launcher.getProfileManager()
						.getProfiles().containsKey(profile.getName())) {
					profile.setName(profile.getName() + "_");
				}

				ProfileEditorPopup.showEditProfileDialog(
						ProfileListTab.this.getLauncher(), profile);
			}
		});
		this.copyProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = ProfileListTab.this.table.getSelectedRow();
				if ((selection < 0)
						|| (selection >= ProfileListTab.this.table
								.getRowCount()))
					return;

				Profile current = dataModel.profiles.get(selection);
				Profile copy = new Profile(current);
				copy.setName("Copy of " + current.getName());

				while (ProfileListTab.this.launcher.getProfileManager()
						.getProfiles().containsKey(copy.getName())) {
					copy.setName(copy.getName() + "_");
				}

				ProfileEditorPopup.showEditProfileDialog(
						ProfileListTab.this.getLauncher(), copy);
			}
		});
		this.deleteProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = ProfileListTab.this.table.getSelectedRow();
				if ((selection < 0)
						|| (selection >= ProfileListTab.this.table
								.getRowCount()))
					return;

				Profile current = dataModel.profiles.get(selection);

				int result = JOptionPane.showOptionDialog(
						ProfileListTab.this.launcher.getFrame(),
						"Are you sure you want to delete this profile?",
						"Profile Confirmation", 0, 2, null,
						LauncherConstants.CONFIRM_PROFILE_DELETION_OPTIONS,
						LauncherConstants.CONFIRM_PROFILE_DELETION_OPTIONS[0]);

				if (result == 0) {
					ProfileListTab.this.launcher.getProfileManager()
							.getProfiles().remove(current.getName());
					try {
						ProfileListTab.this.launcher.getProfileManager()
								.saveProfiles();
						ProfileListTab.this.launcher.getProfileManager()
								.fireRefreshEvent();
					} catch (IOException ex) {
						ProfileListTab.this.launcher.println(
								"Couldn't save profiles whilst deleting '"
										+ current.getName() + "'", ex);
					}
				}
			}
		});
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = ProfileListTab.this.table.getSelectedRow();

					if ((row >= 0) && (row < dataModel.profiles.size()))
						ProfileEditorPopup.showEditProfileDialog(
								ProfileListTab.this.getLauncher(),
								dataModel.profiles.get(row));
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if ((e.isPopupTrigger())
						&& ((e.getComponent() instanceof JTable))) {
					int r = ProfileListTab.this.table.rowAtPoint(e.getPoint());
					if ((r >= 0)
							&& (r < ProfileListTab.this.table.getRowCount()))
						ProfileListTab.this.table.setRowSelectionInterval(r, r);
					else {
						ProfileListTab.this.table.clearSelection();
					}

					ProfileListTab.this.popupMenu.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if ((e.isPopupTrigger())
						&& ((e.getComponent() instanceof JTable))) {
					int r = ProfileListTab.this.table.rowAtPoint(e.getPoint());
					if ((r >= 0)
							&& (r < ProfileListTab.this.table.getRowCount()))
						ProfileListTab.this.table.setRowSelectionInterval(r, r);
					else {
						ProfileListTab.this.table.clearSelection();
					}

					ProfileListTab.this.popupMenu.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}
		});
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	@Override
	public void onProfilesRefreshed(ProfileManager manager) {
		this.dataModel.setProfiles(manager.getProfiles().values());
	}

	@Override
	public boolean shouldReceiveEventsInUIThread() {
		return true;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.tabs.ProfileListTab JD-Core Version: 0.6.2
 */