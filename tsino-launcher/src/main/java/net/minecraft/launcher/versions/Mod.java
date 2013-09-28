package net.minecraft.launcher.versions;

import net.minecraft.launcher.LauncherConstants;

public class Mod {
	private String name;
	private String url;
	private boolean coremod = false;

	public Mod() {
	}

	public Mod(String name) {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException(
					"Mod name cannot be null or empty");
		this.name = name;
	}

	public String getArtifactBaseDir() {
		if (this.name == null)
			throw new IllegalStateException(
					"Cannot get artifact dir of empty/blank artifact");
		if (coremod) {
			return "coremods";
		}
		return "mods";
	}

	public String getArtifactFilename() {
		if (this.name == null)
			throw new IllegalStateException(
					"Cannot get artifact filename of empty/blank artifact");
		return name;
	}

	public String getArtifactPath() {
		if (this.name == null)
			throw new IllegalStateException(
					"Cannot get artifact path of empty/blank artifact");
		return String.format("%s/%s", new Object[] { getArtifactBaseDir(),
				getArtifactFilename() });
	}

	public String getDownloadUrl() {
		if (this.url != null)
			return this.url;
		return LauncherConstants.URL_MODS_BASE;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasCustomUrl() {
		return this.url != null;
	}

	@Override
	public String toString() {
		return "Mod{name='" + this.name + '\'' + '}';
	}
}
