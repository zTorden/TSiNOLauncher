package net.minecraft.launcher.updater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.bootstrap.EtagDatabase;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.LauncherConstants;
import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.events.RefreshedVersionsListener;
import net.minecraft.launcher.updater.download.DownloadJob;
import net.minecraft.launcher.updater.download.Downloadable;
import net.minecraft.launcher.versions.CompleteModList;
import net.minecraft.launcher.versions.CompleteVersion;
import net.minecraft.launcher.versions.ReleaseType;
import net.minecraft.launcher.versions.Version;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VersionManager {
	private final VersionList localVersionList;
	private final VersionList remoteVersionList;
	private final ThreadPoolExecutor executorService = new ExceptionalThreadPoolExecutor(
			8);
	private final List<RefreshedVersionsListener> refreshedVersionsListeners = Collections
			.synchronizedList(new ArrayList<RefreshedVersionsListener>());
	private final Object refreshLock = new Object();
	private boolean isRefreshing;

	public VersionManager(VersionList localVersionList,
			VersionList remoteVersionList) {
		this.localVersionList = localVersionList;
		this.remoteVersionList = remoteVersionList;
	}

	public void addRefreshedVersionsListener(RefreshedVersionsListener listener) {
		this.refreshedVersionsListeners.add(listener);
	}

	private void cleanDirectory(String directory, Set<String> files) {
		File baseDirectory = ((LocalVersionList) this.localVersionList)
				.getBaseDirectory();
		File coremods_dir = new File(baseDirectory, directory);
		if (coremods_dir.exists()) {
			for (File file : coremods_dir.listFiles()) {
				if (file.isFile() && !files.contains(file.getName())) {
					Launcher.getInstance().println(
							"Removing mod: " + file.toString());
					FileUtils.deleteQuietly(file);
				}
			}
		}
	}

	public DownloadJob downloadMods(DownloadJob job) throws IOException {

		CompleteModList version = remoteVersionList.getCompleteModList();
		File baseDirectory = ((LocalVersionList) this.localVersionList)
				.getBaseDirectory();
		Proxy proxy = ((RemoteVersionList) this.remoteVersionList).getProxy();

		job.addDownloadables(version.getRequiredDownloadables(
				OperatingSystem.getCurrentPlatform(), proxy, baseDirectory,
				false));

		Set<String> req_mods = version.getRequiredFiles("mods");
		cleanDirectory("mods", req_mods);

		Set<String> req_coremods = version.getRequiredFiles("coremods");
		cleanDirectory("coremods", req_coremods);

		return job;
	}

	public DownloadJob downloadConfigs(DownloadJob job) throws IOException {
		File baseDirectory = ((LocalVersionList) this.localVersionList)
				.getBaseDirectory();

		job.addDownloadables(getConfigFiles(
				((RemoteVersionList) this.remoteVersionList).getProxy(),
				baseDirectory));

		return job;
	}

	public DownloadJob downloadResources(DownloadJob job) throws IOException {
		File baseDirectory = ((LocalVersionList) this.localVersionList)
				.getBaseDirectory();

		job.addDownloadables(getResourceFiles(
				((RemoteVersionList) this.remoteVersionList).getProxy(),
				baseDirectory));

		return job;
	}

	public DownloadJob downloadVersion(VersionSyncInfo syncInfo, DownloadJob job)
			throws IOException {
		if (!(this.localVersionList instanceof LocalVersionList))
			throw new IllegalArgumentException(
					"Cannot download if local repo isn't a LocalVersionList");
		if (!(this.remoteVersionList instanceof RemoteVersionList))
			throw new IllegalArgumentException(
					"Cannot download if local repo isn't a RemoteVersionList");
		CompleteVersion version = getLatestCompleteVersion(syncInfo);
		File baseDirectory = ((LocalVersionList) this.localVersionList)
				.getBaseDirectory();
		Proxy proxy = ((RemoteVersionList) this.remoteVersionList).getProxy();

		job.addDownloadables(version.getRequiredDownloadables(
				OperatingSystem.getCurrentPlatform(), proxy, baseDirectory,
				false));

		String jarFile = "versions/" + version.getId() + "/" + version.getId()
				+ ".jar";
		job.addDownloadables(new Downloadable[] { new Downloadable(proxy,
				new URL(LauncherConstants.URL_DOWNLOAD_BASE + jarFile),
				new File(baseDirectory, jarFile), false) });

		return job;
	}

	public ThreadPoolExecutor getExecutorService() {
		return this.executorService;
	}

	public List<VersionSyncInfo> getInstalledVersions() {
		List<VersionSyncInfo> result = new ArrayList<VersionSyncInfo>();

		for (Version version : this.localVersionList.getVersions()) {
			if ((version.getType() != null)
					&& (version.getUpdatedTime() != null)) {
				VersionSyncInfo syncInfo = getVersionSyncInfo(version,
						this.remoteVersionList.getVersion(version.getId()));
				result.add(syncInfo);
			}
		}
		return result;
	}

	public CompleteVersion getLatestCompleteVersion(VersionSyncInfo syncInfo)
			throws IOException {
		if (syncInfo.getLatestSource() == VersionSyncInfo.VersionSource.REMOTE) {
			CompleteVersion result = null;
			IOException exception = null;
			try {
				result = this.remoteVersionList.getCompleteVersion(syncInfo
						.getLatestVersion());
			} catch (IOException e) {
				exception = e;
				try {
					result = this.localVersionList.getCompleteVersion(syncInfo
							.getLatestVersion());
				} catch (IOException localIOException1) {
				}
			}
			if (result != null) {
				return result;
			}
			throw exception;
		}

		return this.localVersionList.getCompleteVersion(syncInfo
				.getLatestVersion());
	}

	public VersionList getLocalVersionList() {
		return this.localVersionList;
	}

	public VersionList getRemoteVersionList() {
		return this.remoteVersionList;
	}

	private Set<Downloadable> getResourceFiles(Proxy proxy, File baseDirectory) {
		Set<Downloadable> result = new HashSet<Downloadable>();
		try {
			URL resourceUrl = new URL(LauncherConstants.URL_RESOURCE_BASE);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(resourceUrl.openConnection(proxy)
					.getInputStream());
			NodeList nodeLst = doc.getElementsByTagName("Contents");

			long start = System.nanoTime();
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node node = nodeLst.item(i);

				if (node.getNodeType() == 1) {
					Element element = (Element) node;
					String key = element.getElementsByTagName("Key").item(0)
							.getChildNodes().item(0).getNodeValue();
					String etag = element.getElementsByTagName("ETag") != null ? element
							.getElementsByTagName("ETag").item(0)
							.getChildNodes().item(0).getNodeValue()
							: "-";
					long size = Long.parseLong(element
							.getElementsByTagName("Size").item(0)
							.getChildNodes().item(0).getNodeValue());

					if (size > 0L) {
						File file = new File(baseDirectory, "assets/" + key);
						if (etag.length() > 1) {
							etag = EtagDatabase.formatEtag(etag);
							if ((file.isFile()) && (file.length() == size)) {
								String localEtag = EtagDatabase.getInstance()
										.getEtag(file);
								if (localEtag.equals(etag))
									continue;
							}
						}
						Downloadable downloadable = new Downloadable(proxy,
								new URL(LauncherConstants.URL_RESOURCE_BASE
										+ key), file, false);
						downloadable.setExpectedSize(size);
						result.add(downloadable);
					}
				}
			}
			long end = System.nanoTime();
			long delta = end - start;
			Launcher.getInstance().println(
					"Delta time to compare resources: " + delta / 1000000L
							+ " ms ");
		} catch (Exception ex) {
			Launcher.getInstance().println("Couldn't download resources", ex);
		}

		return result;
	}

	private Set<Downloadable> getConfigFiles(Proxy proxy, File baseDirectory) {
		Set<Downloadable> result = new HashSet<Downloadable>();
		try {
			URL remoteFile = new URL(LauncherConstants.URL_CONFIG_ZIP);
			File localFile = new File(baseDirectory, "config.zip");

			Downloadable downloadable = new Downloadable(proxy, remoteFile,
					localFile, isRefreshing);
			result.add(downloadable);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<VersionSyncInfo> getVersions() {
		return getVersions(null);
	}

	public List<VersionSyncInfo> getVersions(VersionFilter filter) {
		synchronized (this.refreshLock) {
			if (this.isRefreshing)
				return new ArrayList<VersionSyncInfo>();
		}

		List<VersionSyncInfo> result = new ArrayList<VersionSyncInfo>();
		Map<String, VersionSyncInfo> lookup = new HashMap<String, VersionSyncInfo>();
		Map<ReleaseType, Integer> counts = new EnumMap<ReleaseType, Integer>(
				ReleaseType.class);

		for (ReleaseType type : ReleaseType.values()) {
			counts.put(type, Integer.valueOf(0));
		}

		for (Version version : this.localVersionList.getVersions()) {
			if ((version.getType() != null)
					&& (version.getUpdatedTime() != null)
					&& ((filter == null) || ((filter.getTypes()
							.contains(version.getType())) && (counts.get(
							version.getType()).intValue() < filter
							.getMaxCount())))) {
				VersionSyncInfo syncInfo = getVersionSyncInfo(version,
						this.remoteVersionList.getVersion(version.getId()));
				lookup.put(version.getId(), syncInfo);
				result.add(syncInfo);
			}
		}
		for (Version version : this.remoteVersionList.getVersions()) {
			if ((version.getType() != null)
					&& (version.getUpdatedTime() != null)
					&& (!lookup.containsKey(version.getId()))
					&& ((filter == null) || ((filter.getTypes()
							.contains(version.getType())) && (counts.get(
							version.getType()).intValue() < filter
							.getMaxCount())))) {
				VersionSyncInfo syncInfo = getVersionSyncInfo(
						this.localVersionList.getVersion(version.getId()),
						version);
				lookup.put(version.getId(), syncInfo);
				result.add(syncInfo);

				if (filter != null)
					counts.put(version.getType(), Integer.valueOf(counts.get(
							version.getType()).intValue() + 1));
			}
		}
		if (result.isEmpty()) {
			for (Version version : this.localVersionList.getVersions()) {
				if ((version.getType() != null)
						&& (version.getUpdatedTime() != null)) {
					VersionSyncInfo syncInfo = getVersionSyncInfo(version,
							this.remoteVersionList.getVersion(version.getId()));
					lookup.put(version.getId(), syncInfo);
					result.add(syncInfo);
				}
			}
		}

		Collections.sort(result, new Comparator<VersionSyncInfo>() {
			@Override
			public int compare(VersionSyncInfo a, VersionSyncInfo b) {
				Version aVer = a.getLatestVersion();
				Version bVer = b.getLatestVersion();

				if ((aVer.getReleaseTime() != null)
						&& (bVer.getReleaseTime() != null)) {
					return bVer.getReleaseTime().compareTo(
							aVer.getReleaseTime());
				}
				return bVer.getUpdatedTime().compareTo(aVer.getUpdatedTime());
			}
		});
		return result;
	}

	public VersionSyncInfo getVersionSyncInfo(String name) {
		return getVersionSyncInfo(this.localVersionList.getVersion(name),
				this.remoteVersionList.getVersion(name));
	}

	public VersionSyncInfo getVersionSyncInfo(Version version) {
		return getVersionSyncInfo(version.getId());
	}

	public VersionSyncInfo getVersionSyncInfo(Version localVersion,
			Version remoteVersion) {
		boolean installed = localVersion != null;
		boolean upToDate = installed;

		if ((installed) && (remoteVersion != null)) {
			upToDate = !remoteVersion.getUpdatedTime().after(
					localVersion.getUpdatedTime());
		}
		if ((localVersion instanceof CompleteVersion)) {
			upToDate &= this.localVersionList.hasAllFiles(
					(CompleteVersion) localVersion,
					OperatingSystem.getCurrentPlatform());
		}

		return new VersionSyncInfo(localVersion, remoteVersion, installed,
				upToDate);
	}

	public void refreshVersions() throws IOException {
		synchronized (this.refreshLock) {
			this.isRefreshing = true;
		}
		try {
			Launcher.getInstance().println("Refreshing local version list...");
			this.localVersionList.refreshVersions();
			Launcher.getInstance().println("Refreshing remote version list...");
			this.remoteVersionList.refreshVersions();
		} catch (IOException ex) {
			synchronized (this.refreshLock) {
				this.isRefreshing = false;
			}
			throw ex;
		}

		Launcher.getInstance().println("Refresh complete.");

		synchronized (this.refreshLock) {
			this.isRefreshing = false;
		}

		final List<RefreshedVersionsListener> listeners = new ArrayList<RefreshedVersionsListener>(
				this.refreshedVersionsListeners);
		for (Iterator<RefreshedVersionsListener> iterator = listeners
				.iterator(); iterator.hasNext();) {
			RefreshedVersionsListener listener = iterator.next();

			if (!listener.shouldReceiveEventsInUIThread()) {
				listener.onVersionsRefreshed(this);
				iterator.remove();
			}
		}

		if (!listeners.isEmpty())
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (RefreshedVersionsListener listener : listeners)
						listener.onVersionsRefreshed(VersionManager.this);
				}
			});
	}

	public void removeRefreshedVersionsListener(
			RefreshedVersionsListener listener) {
		this.refreshedVersionsListeners.remove(listener);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.VersionManager JD-Core Version: 0.6.2
 */