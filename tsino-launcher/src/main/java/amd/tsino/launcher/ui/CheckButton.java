package amd.tsino.launcher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.style.CheckButtonStyle;

@SuppressWarnings("serial")
public class CheckButton extends JCheckBox {
	private BufferedImage true_normal;
	private BufferedImage true_hover;
	private BufferedImage true_click;
	private BufferedImage false_normal;
	private BufferedImage false_hover;
	private BufferedImage false_click;
	private Dimension size;

	public CheckButton(CheckButtonStyle style) throws IOException {
		this.true_normal = Launcher.getInstance().getStyle()
				.getImage(style.true_normal);
		this.true_hover = Launcher.getInstance().getStyle()
				.getImage(style.true_hover);
		this.true_click = Launcher.getInstance().getStyle()
				.getImage(style.true_click);
		this.false_normal = Launcher.getInstance().getStyle()
				.getImage(style.false_normal);
		this.false_hover = Launcher.getInstance().getStyle()
				.getImage(style.false_hover);
		this.false_click = Launcher.getInstance().getStyle()
				.getImage(style.false_click);

		size = new Dimension(true_normal.getWidth(), true_normal.getHeight());
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
			if (model.isSelected()) {
				g.drawImage(true_click, 0, 0, null);
			} else {
				g.drawImage(false_click, 0, 0, null);
			}
		} else if (model.isRollover() || (isFocusOwner() && isFocusPainted())) {
			if (model.isSelected()) {
				g.drawImage(true_hover, 0, 0, null);
			} else {
				g.drawImage(false_hover, 0, 0, null);
			}
		} else {
			if (model.isSelected()) {
				g.drawImage(true_normal, 0, 0, null);
			} else {
				g.drawImage(false_normal, 0, 0, null);
			}
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
	}
}
