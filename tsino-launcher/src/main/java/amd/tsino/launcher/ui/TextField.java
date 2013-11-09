package amd.tsino.launcher.ui;

import amd.tsino.launcher.style.TextFieldStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("serial")
public class TextField extends JPasswordField {
    private BufferedImage normal;
    private BufferedImage hover;
    private Dimension size;

    public TextField(TextFieldStyle style, boolean password) throws IOException {
        this.normal = Launcher.getInstance().getStyle().getImage(style.normal);
        this.hover = Launcher.getInstance().getStyle().getImage(style.hover);
        size = new Dimension(normal.getWidth(), normal.getHeight());
        setBounds(style.x, style.y, size.width, size.height);
        setBorder(BorderFactory.createEmptyBorder(style.padding.top,
                style.padding.left, style.padding.bottom, style.padding.right));
        setFont(new Font(style.font, 0, style.size));
        setForeground(Color.decode(style.color));
        setToolTipText(style.tooltip);
        setOpaque(false);

        if (!password) {
            setEchoChar('\0');
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent arg0) {
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isEnabled() && (isFocusOwner() || getMousePosition() != null)) {
            g.drawImage(hover, 0, 0, null);
            super.paintComponent(g);
        } else {
            g.drawImage(normal, 0, 0, null);
            super.paintComponent(g);
        }
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        setCaretPosition(getDocument().getLength());
    }
}
