package net.minecraft.launcher.profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.SwingUtilities;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.authentication.AuthenticationDatabase;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.updater.DateTypeAdapter;
import net.minecraft.launcher.updater.FileTypeAdapter;
import net.minecraft.launcher.updater.LowerCaseEnumTypeAdapterFactory;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProfileManager {
	private static class RawProfileList {
		public Map<String, Profile> profiles = new HashMap<String, Profile>();
		public String selectedProfile;
		public UUID clientToken = UUID.randomUUID();
		public AuthenticationDatabase authenticationDatabase = new AuthenticationDatabase();
	}

	public static final String DEFAULT_PROFILE_NAME = "(Default)";
	private final Launcher launcher;
	private final Gson gson;
	private final Map<String, Profile> profiles = new HashMap<String, Profile>();
	private final File profileFile;
	private final List<RefreshedProfilesListener> refreshedProfilesListeners = Collections
			.synchronizedList(new ArrayList<RefreshedProfilesListener>());
	private String selectedProfile;

	private AuthenticationDatabase authDatabase = new AuthenticationDatabase();

	public ProfileManager(Launcher launcher) {
		this.launcher = launcher;
		this.profileFile = new File(launcher.getWorkingDirectory(),
				"launcher_profiles.json");

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
		builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		builder.registerTypeAdapter(File.class, new FileTypeAdapter());
		builder.registerTypeAdapter(AuthenticationDatabase.class,
				new AuthenticationDatabase.Serializer());
		builder.setPrettyPrinting();
		this.gson = builder.create();
	}

	public void addRefreshedProfilesListener(RefreshedProfilesListener listener) {
		this.refreshedProfilesListeners.add(listener);
	}

	public void fireRefreshEvent() {
		final List<RefreshedProfilesListener> listeners = new ArrayList<RefreshedProfilesListener>(
				this.refreshedProfilesListeners);
		for (Iterator<RefreshedProfilesListener> iterator = listeners
				.iterator(); iterator.hasNext();) {
			RefreshedProfilesListener listener = iterator.next();

			if (!listener.shouldReceiveEventsInUIThread()) {
				listener.onProfilesRefreshed(this);
				iterator.remove();
			}
		}

		if (!listeners.isEmpty())
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (RefreshedProfilesListener listener : listeners)
						listener.onProfilesRefreshed(ProfileManager.this);
				}
			});
	}

	public AuthenticationDatabase getAuthDatabase() {
		return this.authDatabase;
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public Map<String, Profile> getProfiles() {
		return this.profiles;
	}

	public Profile getSelectedProfile() {
		if ((this.selectedProfile == null)
				|| (!this.profiles.containsKey(this.selectedProfile))) {
			if (this.profiles.get("(Default)") != null) {
				this.selectedProfile = "(Default)";
			} else if (this.profiles.size() > 0) {
				this.selectedProfile = this.profiles.values().iterator().next()
						.getName();
			} else {
				this.selectedProfile = "(Default)";
				this.profiles.put("(Default)",
						new Profile(this.selectedProfile));
			}
		}

		return this.profiles.get(this.selectedProfile);
	}

	public boolean loadProfiles() throws IOException {
		this.profiles.clear();
		this.selectedProfile = null;

		if (this.profileFile.isFile()) {
			RawProfileList rawProfileList = this.gson.fromJson(
					FileUtils.readFileToString(this.profileFile),
					RawProfileList.class);

			this.profiles.putAll(rawProfileList.profiles);
			this.selectedProfile = rawProfileList.selectedProfile;
			this.authDatabase = rawProfileList.authenticationDatabase;
			this.launcher.setClientToken(rawProfileList.clientToken);

			fireRefreshEvent();
			return true;
		}
		fireRefreshEvent();
		return false;
	}

	public void saveProfiles() throws IOException {
		RawProfileList rawProfileList = new RawProfileList();
		rawProfileList.profiles = this.profiles;
		rawProfileList.selectedProfile = getSelectedProfile().getName();
		rawProfileList.clientToken = this.launcher.getClientToken();
		rawProfileList.authenticationDatabase = this.authDatabase;

		FileUtils.writeStringToFile(this.profileFile,
				this.gson.toJson(rawProfileList));
	}

	public void setSelectedProfile(String selectedProfile) {
		boolean update = !this.selectedProfile.equals(selectedProfile);
		this.selectedProfile = selectedProfile;

		if (update)
			fireRefreshEvent();
	}

	public void trimAuthDatabase() {
		Set<String> uuids = new HashSet<String>(
				this.authDatabase.getknownUUIDs());

		for (Profile profile : this.profiles.values()) {
			uuids.remove(profile.getPlayerUUID());
		}

		for (String uuid : uuids)
			this.authDatabase.removeUUID(uuid);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.profile.ProfileManager JD-Core Version: 0.6.2
 */