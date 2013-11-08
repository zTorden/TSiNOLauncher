package amd.tsino.launcher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.style.Padding;
import amd.tsino.launcher.style.ProgressBarStyle;

@SuppressWarnings("serial")
public class ProgressBar extends JProgressBar {
	private BufferedImage background;
	private BufferedImage start;
	private BufferedImage end;
	private BufferedImage body;
	private Padding padding;
	private Dimension size;

	public ProgressBar(ProgressBarStyle style)
			throws IOException {
		this.background = Launcher.getInstance().getStyle().getImage(style.background);
		this.start = Launcher.getInstance().getStyle().getImage(style.start);
		this.end = Launcher.getInstance().getStyle().getImage(style.end);
		this.body = Launcher.getInstance().getStyle().getImage(style.body);
		this.padding = style.padding;
		size = new Dimension(background.getWidth(), background.getHeight());
		setBounds(style.x, style.y, size.width, size.height);
		setOpaque(false);
	}

	private int getActiveLength() {
		return getWidth() - (padding.left + padding.right);
	}

	private int getMinLength() {
		return start.getWidth() + end.getWidth();
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
		BoundedRangeModel model = getModel();
		if (model.getValue() == model.getMinimum()) {
			return;
		}
		int bodyLength = ((model.getValue() * getActiveLength()) / (model
				.getMaximum() - model.getMinimum())) - getMinLength();
		if (bodyLength <= 0) {
			g.drawImage(start, padding.left, padding.top, null);
			g.drawImage(end, padding.left + start.getWidth(), padding.top, null);
		} else {
			g.drawImage(start, padding.left, padding.top, null);
			g.drawImage(body, padding.left + start.getWidth(), padding.top,
					bodyLength, body.getHeight(), null);
			g.drawImage(end, padding.left + start.getWidth() + bodyLength,
					padding.top, null);
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
	}
}
