package net.minecraft.launcher.versions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.updater.download.Downloadable;

public class CompleteModList implements Version {
	private String id;
	private Date time;
	private ReleaseType type;
	private List<Mod> mods;

	public CompleteModList() {
	}

	@Override
	public String getId() {
		return this.id;
	}

	public Collection<Mod> getMods() {
		return this.mods;
	}

	public Set<Downloadable> getRequiredDownloadables(OperatingSystem os,
			Proxy proxy, File targetDirectory, boolean ignoreLocalFiles)
			throws MalformedURLException {
		Set<Downloadable> neededFiles = new HashSet<Downloadable>();

		for (Mod mod : getMods()) {
			String file = null;

			file = mod.getArtifactPath();

			if (file != null) {
				URL url = new URL(mod.getDownloadUrl()
						+ mod.getArtifactFilename());
				File local = new File(targetDirectory, file);

				if ((!local.isFile()) || (!mod.hasCustomUrl())) {
					neededFiles.add(new Downloadable(proxy, url, local,
							ignoreLocalFiles));
				}
			}
		}

		return neededFiles;
	}

	public Set<String> getRequiredFiles(String directory) {
		Set<String> neededFiles = new HashSet<String>();
		for (Mod mod : getMods()) {
			if (mod.getArtifactBaseDir().equals(directory)) {
				neededFiles.add(mod.getArtifactFilename());
			}
		}
		return neededFiles;
	}

	@Override
	public ReleaseType getType() {
		return this.type;
	}

	@Override
	public Date getUpdatedTime() {
		return this.time;
	}

	@Override
	public void setType(ReleaseType type) {
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		this.type = type;
	}

	@Override
	public void setUpdatedTime(Date time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");
		this.time = time;
	}

	@Override
	public String toString() {
		return "CompleteModList{id='" + this.id + '\'' + ", time=" + this.time
				+ ", type=" + this.type + ", libraries=" + this.mods + '}';
	}
}
