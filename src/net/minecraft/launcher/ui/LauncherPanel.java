package net.minecraft.launcher.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.LauncherConstants;
import net.minecraft.launcher.ui.tabs.LauncherTabPanel;

public class LauncherPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CARD_DIRT_BACKGROUND = "loading";
	public static final String CARD_LOGIN = "login";
	public static final String CARD_LAUNCHER = "launcher";
	private final CardLayout cardLayout;
	private final LauncherTabPanel tabPanel;
	private final BottomBarPanel bottomBar;
	private final JProgressBar progressBar;
	private final Launcher launcher;
	private final JPanel loginPanel;

	public LauncherPanel(Launcher launcher) {
		this.launcher = launcher;
		this.cardLayout = new CardLayout();
		setLayout(this.cardLayout);

		this.progressBar = new JProgressBar();
		this.bottomBar = new BottomBarPanel(launcher);
		this.tabPanel = new LauncherTabPanel(launcher);
		this.loginPanel = new TexturedPanel("/dirt.png");
		createInterface();
	}

	protected JPanel createDirtInterface() {
		return new TexturedPanel("/dirt.png");
	}

	protected void createInterface() {
		add(createLauncherInterface(), "launcher");
		add(createDirtInterface(), "loading");
		add(createLoginInterface(), "login");
	}

	protected JPanel createLauncherInterface() {
		JPanel result = new JPanel(new BorderLayout());

		this.tabPanel.getBlog().setPage(LauncherConstants.URL_BLOG);

		JPanel topWrapper = new JPanel();
		topWrapper.setLayout(new BorderLayout());
		topWrapper.add(this.tabPanel, "Center");
		topWrapper.add(this.progressBar, "South");

		this.progressBar.setVisible(false);
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);

		result.add(topWrapper, "Center");
		result.add(this.bottomBar, "South");

		return result;
	}

	protected JPanel createLoginInterface() {
		this.loginPanel.setLayout(new GridBagLayout());
		return this.loginPanel;
	}

	public BottomBarPanel getBottomBar() {
		return this.bottomBar;
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

	public LauncherTabPanel getTabPanel() {
		return this.tabPanel;
	}

	public void setCard(String card, JPanel additional) {
		if (card.equals("login")) {
			this.loginPanel.removeAll();
			this.loginPanel.add(additional);
		}
		this.cardLayout.show(this, card);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.LauncherPanel JD-Core Version: 0.6.2
 */