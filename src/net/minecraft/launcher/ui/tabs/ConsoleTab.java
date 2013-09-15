package net.minecraft.launcher.ui.tabs;

import java.awt.Font;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.minecraft.launcher.Launcher;

public class ConsoleTab extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6076752381005959522L;

	private static final Font MONOSPACED = new Font("Monospaced", 0, 12);

	private final JTextPane console = new JTextPane();
	private final Launcher launcher;

	public ConsoleTab(Launcher launcher) {
		this.launcher = launcher;

		this.console.setFont(MONOSPACED);
		this.console.setEditable(false);
		this.console.setMargin(null);

		setViewportView(this.console);
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public void print(final String line) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ConsoleTab.this.print(line);
				}
			});
			return;
		}

		Document document = this.console.getDocument();
		JScrollBar scrollBar = getVerticalScrollBar();
		boolean shouldScroll = false;

		if (getViewport().getView() == this.console) {
			shouldScroll = scrollBar.getValue()
					+ scrollBar.getSize().getHeight() + MONOSPACED.getSize()
					* 4 > scrollBar.getMaximum();
		}
		try {
			document.insertString(document.getLength(), line, null);
		} catch (BadLocationException localBadLocationException) {
		}
		if (shouldScroll)
			scrollBar.setValue(2147483647);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.tabs.ConsoleTab JD-Core Version: 0.6.2
 */