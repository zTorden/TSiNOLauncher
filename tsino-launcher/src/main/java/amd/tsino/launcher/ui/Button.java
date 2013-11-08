package amd.tsino.launcher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ButtonModel;
import javax.swing.JButton;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.style.ButtonStyle;

@SuppressWarnings("serial")
public class Button extends JButton {
	private BufferedImage normal;
	private BufferedImage hover;
	private BufferedImage click;
	private Dimension size;

	public Button(ButtonStyle style) throws IOException {
		this.normal = Launcher.getInstance().getStyle().getImage(style.normal);
		this.hover = Launcher.getInstance().getStyle().getImage(style.hover);
		this.click = Launcher.getInstance().getStyle().getImage(style.click);
		size = new Dimension(normal.getWidth(), normal.getHeight());
		setBounds(style.x, style.y, size.width, size.height);
		setToolTipText(style.tooltip);
		setOpaque(false);
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	protected void paintComponent(Graphics g) {
		ButtonModel model = getModel();
		if (model.isPressed()) {
			g.drawImage(click, 0, 0, null);
		} else if (model.isRollover() || (isFocusOwner() && isFocusPainted())) {
			g.drawImage(hover, 0, 0, null);
		} else {
			g.drawImage(normal, 0, 0, null);
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
	}
}
