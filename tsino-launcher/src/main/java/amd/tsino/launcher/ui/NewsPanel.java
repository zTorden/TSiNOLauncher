package amd.tsino.launcher.ui;

import java.io.IOException;

import amd.tsino.launcher.style.NewsPanelStyle;

@SuppressWarnings("serial")
public class NewsPanel extends ImagePanel {
	public NewsPanel(NewsPanelStyle style) throws IOException {
		super(style);
		setLayout(null);
		setFocusable(false);
		add(new BrowserFrame(style.browser));
	}
}
