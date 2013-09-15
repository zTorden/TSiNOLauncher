package net.minecraft.launcher.ui.tabs;

import java.awt.Component;

import javax.swing.JTabbedPane;

import net.minecraft.launcher.Launcher;

public class LauncherTabPanel extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1850360775666476168L;
	private final Launcher launcher;
	private final WebsiteTab blog;
	private final ConsoleTab console;
	private final ProfileListTab profiles;
	private final VersionListTab versions;

	public LauncherTabPanel(Launcher launcher) {
		super(1);

		this.launcher = launcher;
		this.blog = new WebsiteTab(launcher);
		this.console = new ConsoleTab(launcher);
		this.profiles = new ProfileListTab(launcher);
		this.versions = new VersionListTab(launcher);

		createInterface();
	}

	protected void createInterface() {
		addTab("Новости", this.blog);
		addTab("Консоль", this.console);
	}

	public WebsiteTab getBlog() {
		return this.blog;
	}

	public ConsoleTab getConsole() {
		return this.console;
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	protected void removeTab(Component tab) {
		for (int i = 0; i < getTabCount(); i++)
			if (getTabComponentAt(i) == tab) {
				removeTabAt(i);
				break;
			}
	}

	public void showAll() {
		addTab("Profile Editor", this.profiles);
		addTab("Local Version Editor (NYI)", this.versions);
	}

	public void showConsole() {
		setSelectedComponent(this.console);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.tabs.LauncherTabPanel JD-Core Version: 0.6.2
 */