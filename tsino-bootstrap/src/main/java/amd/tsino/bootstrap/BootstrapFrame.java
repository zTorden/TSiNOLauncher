package amd.tsino.bootstrap;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

public class BootstrapFrame extends JFrame {
	private static final long serialVersionUID = -2936775481148125746L;
	private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
	private final JTextArea textArea;
	private final JScrollPane scrollPane;

	public BootstrapFrame() {
		super("TSiNO Minecraft Launcher");

		setSize(854, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setFont(MONOSPACED);
		((DefaultCaret) textArea.getCaret())
				.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(null);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		add(scrollPane);
		setLocationRelativeTo(null);
	}

	public void printf(String format, Object... args) {
		Document document = this.textArea.getDocument();
		final JScrollBar scrollBar = this.scrollPane.getVerticalScrollBar();

		boolean shouldScroll = scrollBar.getValue()
				+ scrollBar.getSize().getHeight() + MONOSPACED.getSize() * 2 > scrollBar
				.getMaximum();
		try {
			document.insertString(document.getLength(),
					String.format(format, args), null);
		} catch (BadLocationException ignored) {
		}
		if (shouldScroll)
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollBar.setValue(2147483647);
				}
			});
	}
}
