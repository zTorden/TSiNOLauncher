package amd.tsino.launcher.ui;

import amd.tsino.launcher.style.ProgressBarStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("serial")
public class ProgressBar extends JProgressBar {
    private final BufferedImage background;
    private final BufferedImage start;
    private final BufferedImage end;
    private final BufferedImage body;
    private final Dimension size;

    public ProgressBar(ProgressBarStyle style)
            throws IOException {
        this.background = Launcher.getInstance().getStyle().getImage(style.background);
        this.start = Launcher.getInstance().getStyle().getImage(style.start);
        this.end = Launcher.getInstance().getStyle().getImage(style.end);
        this.body = Launcher.getInstance().getStyle().getImage(style.body);
        size = new Dimension(background.getWidth(), background.getHeight());
        setBounds(style.x, style.y, size.width, size.height);
        setBorder(BorderFactory.createEmptyBorder(style.padding.top,
                style.padding.left, style.padding.bottom, style.padding.right));
        setOpaque(false);
        setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                g.drawImage(background, 0, 0, null);

                Insets b = progressBar.getInsets(); // area for border
                int barRectWidth = progressBar.getWidth() - (b.right + b.left);
                int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
                if (barRectWidth <= 0 || barRectHeight <= 0) return;

                // amount of progress to draw
                int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
                int length = amountFull - getMinLength();
                if (length < 0) return;

                g.drawImage(start, b.left, b.top, null);
                if (length > 0) {
                    g.drawImage(body, b.left + start.getWidth(), b.top, length, body.getHeight(), null);
                }
                g.drawImage(end, b.left + start.getWidth() + length, b.top, null);
            }

            @Override
            protected void paintIndeterminate(Graphics g, JComponent c) {
                g.drawImage(background, 0, 0, null);

                // Paint the bouncing box.
                boxRect = getBox(boxRect);
                if (boxRect != null) {
                    g.drawImage(start, boxRect.x, boxRect.y, null);
                    int length = boxRect.width - getMinLength();
                    if (length > 0) {
                        g.drawImage(body, boxRect.x + start.getWidth(), boxRect.y, length, body.getHeight(), null);
                    }
                    g.drawImage(end, boxRect.x + boxRect.width - end.getWidth(), boxRect.y, null);
                }
            }
        });
    }

    private int getMinLength() {
        return start.getWidth() + end.getWidth();
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }
}
