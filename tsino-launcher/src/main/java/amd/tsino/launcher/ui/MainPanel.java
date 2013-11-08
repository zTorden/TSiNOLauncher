package amd.tsino.launcher.ui;

import java.io.IOException;

import amd.tsino.launcher.style.MainPanelStyle;

@SuppressWarnings("serial")
public class MainPanel extends ImagePanel {
	private AuthPanel auth;
	private ProgressBar progress;

	public MainPanel(MainPanelStyle style) throws IOException {
		super(style);
		setLayout(null);
		setOpaque(true);

		auth = new AuthPanel(style.auth);
		progress = new ProgressBar(style.progress);
		progress.setVisible(false);

		add(new ImagePanel(style.header));
		add(auth);
		add(new NewsPanel(style.news));
		add(progress);
	}

	public AuthPanel getAuth() {
		return auth;
	}

	public ProgressBar getProgress() {
		return progress;
	}
}
