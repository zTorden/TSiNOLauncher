package net.minecraft.launcher.profile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.launcher.updater.VersionFilter;
import net.minecraft.launcher.versions.ReleaseType;

public class Profile {
	public static class Resolution {
		private int width;
		private int height;

		public Resolution() {
		}

		public Resolution(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public Resolution(Resolution resolution) {
			this(resolution.getWidth(), resolution.getHeight());
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}
	}

	public static final String DEFAULT_JRE_ARGUMENTS_64BIT = "-Xmx1G -Dfml.ignoreInvalidMinecraftCertificates=true";
	public static final String DEFAULT_JRE_ARGUMENTS_32BIT = "-Xmx512M -Dfml.ignoreInvalidMinecraftCertificates=true";
	public static final Resolution DEFAULT_RESOLUTION = new Resolution(854, 480);
	public static final LauncherVisibilityRule DEFAULT_LAUNCHER_VISIBILITY = LauncherVisibilityRule.CLOSE_LAUNCHER;
	public static final Set<ReleaseType> DEFAULT_RELEASE_TYPES = new HashSet<ReleaseType>(
			Arrays.asList(new ReleaseType[] { ReleaseType.RELEASE }));
	private String name;
	private File gameDir;
	private String lastVersionId;
	private String javaDir;
	private String javaArgs;
	private Resolution resolution;
	private Set<ReleaseType> allowedReleaseTypes;
	private String playerUUID;
	private Boolean useHopperCrashService;

	private LauncherVisibilityRule launcherVisibilityOnGameClose;

	@Deprecated
	private Map<String, String> authentication;

	public Profile() {
	}

	public Profile(Profile copy) {
		this.name = copy.name;
		this.gameDir = copy.gameDir;
		this.playerUUID = copy.playerUUID;
		this.lastVersionId = copy.lastVersionId;
		this.javaDir = copy.javaDir;
		this.javaArgs = copy.javaArgs;
		this.resolution = (copy.resolution == null ? null : new Resolution(
				copy.resolution));
		this.allowedReleaseTypes = (copy.allowedReleaseTypes == null ? null
				: new HashSet<ReleaseType>(copy.allowedReleaseTypes));
		this.useHopperCrashService = copy.useHopperCrashService;
		this.launcherVisibilityOnGameClose = copy.launcherVisibilityOnGameClose;
	}

	public Profile(String name) {
		this.name = name;
	}

	public Set<ReleaseType> getAllowedReleaseTypes() {
		return this.allowedReleaseTypes;
	}

	@Deprecated
	public Map<String, String> getAuthentication() {
		return this.authentication;
	}

	public File getGameDir() {
		return this.gameDir;
	}

	public String getJavaArgs() {
		return this.javaArgs;
	}

	public String getJavaPath() {
		return this.javaDir;
	}

	public String getLastVersionId() {
		return this.lastVersionId;
	}

	public LauncherVisibilityRule getLauncherVisibilityOnGameClose() {
		return this.launcherVisibilityOnGameClose;
	}

	public String getName() {
		return this.name;
	}

	public String getPlayerUUID() {
		return this.playerUUID;
	}

	public Resolution getResolution() {
		return this.resolution;
	}

	public boolean getUseHopperCrashService() {
		return this.useHopperCrashService == null;
	}

	public VersionFilter getVersionFilter() {
		VersionFilter filter = new VersionFilter().setMaxCount(2147483647);

		if (this.allowedReleaseTypes == null)
			filter.onlyForTypes(DEFAULT_RELEASE_TYPES
					.toArray(new ReleaseType[DEFAULT_RELEASE_TYPES.size()]));
		else {
			filter.onlyForTypes(this.allowedReleaseTypes
					.toArray(new ReleaseType[this.allowedReleaseTypes.size()]));
		}

		return filter;
	}

	public void setAllowedReleaseTypes(Set<ReleaseType> allowedReleaseTypes) {
		this.allowedReleaseTypes = allowedReleaseTypes;
	}

	@Deprecated
	public void setAuthentication(Map<String, String> authentication) {
		this.authentication = authentication;
	}

	public void setGameDir(File gameDir) {
		this.gameDir = gameDir;
	}

	public void setJavaArgs(String javaArgs) {
		this.javaArgs = javaArgs;
	}

	public void setJavaDir(String javaDir) {
		this.javaDir = javaDir;
	}

	public void setLastVersionId(String lastVersionId) {
		this.lastVersionId = lastVersionId;
	}

	public void setLauncherVisibilityOnGameClose(
			LauncherVisibilityRule launcherVisibilityOnGameClose) {
		this.launcherVisibilityOnGameClose = launcherVisibilityOnGameClose;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	public void setUseHopperCrashService(boolean useHopperCrashService) {
		this.useHopperCrashService = (useHopperCrashService ? null : Boolean
				.valueOf(false));
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.profile.Profile JD-Core Version: 0.6.2
 */