package net.minecraft.launcher.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.versions.CompleteVersion;
import net.minecraft.launcher.versions.ReleaseType;
import net.minecraft.launcher.versions.Version;

import com.google.gson.JsonSyntaxException;

public class LocalVersionList extends FileBasedVersionList {
	private final File baseDirectory;
	private final File baseVersionsDir;

	public LocalVersionList(File baseDirectory) {
		if ((baseDirectory == null) || (!baseDirectory.isDirectory()))
			throw new IllegalArgumentException(
					"Base directory is not a folder!");

		this.baseDirectory = baseDirectory;
		this.baseVersionsDir = new File(this.baseDirectory, "versions");
		if (!this.baseVersionsDir.isDirectory())
			this.baseVersionsDir.mkdirs();
	}

	protected InputStream getFileInputStream(String path)
			throws FileNotFoundException {
		return new FileInputStream(new File(this.baseDirectory, path));
	}

	public void refreshVersions() throws IOException {
		clearCache();

		File[] files = this.baseVersionsDir.listFiles();
		if (files == null)
			return;

		for (File directory : files) {
			String id = directory.getName();
			File jsonFile = new File(directory, id + ".json");

			if ((directory.isDirectory()) && (jsonFile.exists())) {
				try {
					String path = "versions/" + id + "/" + id + ".json";
					CompleteVersion version = (CompleteVersion) this.gson
							.fromJson(getContent(path), CompleteVersion.class);

					if (version.getId().equals(id))
						addVersion(version);
					else if (Launcher.getInstance() != null)
						Launcher.getInstance().println(
								"Ignoring: " + path + "; it contains id: '"
										+ version.getId() + "' expected '" + id
										+ "'");
				} catch (RuntimeException ex) {
					if (Launcher.getInstance() != null)
						Launcher.getInstance().println(
								"Couldn't load local version "
										+ jsonFile.getAbsolutePath(), ex);
					else {
						throw new JsonSyntaxException("Loading file: "
								+ jsonFile.toString(), ex);
					}
				}
			}
		}

		for (Version version : getVersions()) {
			ReleaseType type = version.getType();

			if ((getLatestVersion(type) == null)
					|| (getLatestVersion(type).getUpdatedTime().before(version
							.getUpdatedTime())))
				setLatestVersion(version);
		}
	}

	public void saveVersionList() throws IOException {
		String text = serializeVersionList();
		PrintWriter writer = new PrintWriter(new File(this.baseVersionsDir,
				"versions.json"));
		writer.print(text);
		writer.close();
	}

	public void saveVersion(CompleteVersion version) throws IOException {
		String text = serializeVersion(version);
		File target = new File(this.baseVersionsDir, version.getId() + "/"
				+ version.getId() + ".json");
		if (target.getParentFile() != null)
			target.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(target);
		writer.print(text);
		writer.close();
	}

	public File getBaseDirectory() {
		return this.baseDirectory;
	}

	public boolean hasAllFiles(CompleteVersion version, OperatingSystem os) {
		Set<String> files = version.getRequiredFiles(os);

		for (String file : files) {
			if (!new File(this.baseDirectory, file).isFile()) {
				return false;
			}
		}

		return true;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.LocalVersionList JD-Core Version: 0.6.2
 */