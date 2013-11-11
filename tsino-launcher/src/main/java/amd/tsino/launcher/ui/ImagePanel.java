package amd.tsino.launcher.ui;

import amd.tsino.launcher.style.ImagePanelStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("serial")
class ImagePanel extends JPanel {
    private BufferedImage background;
    private Dimension size;

    ImagePanel(ImagePanelStyle style) throws IOException {
        this.background = Launcher.getInstance().getStyle()
                .getImage(style.background);
        size = new Dimension(background.getWidth(), background.getHeight());
        setBounds(style.x, style.y, size.width, size.height);
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, null);
    }

    @Override
    protected void paintBorder(Graphics g) {
    }
}
