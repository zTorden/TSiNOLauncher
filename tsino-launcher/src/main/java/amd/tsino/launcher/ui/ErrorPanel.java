package amd.tsino.launcher.ui;

import net.minecraft.launcher.Launcher;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;

class ErrorPanel extends JPanel {
    public ErrorPanel(String message) {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFocusable(true);
        area.setFont(new Font("Monospaced", 0, 10));
        area.setText(Launcher.getInstance().getLog().getDump());

        setTextPopupMenu(area);

        JScrollPane pane = new JScrollPane(area);
        pane.setMinimumSize(new Dimension(640, 480));
        pane.setPreferredSize(new Dimension(640, 480));

        setLayout(new BorderLayout());
        add(new JLabel(message), BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
    }

    private static void setTextPopupMenu(final JTextArea area) {
        Action a;
        a = area.getActionMap().get(DefaultEditorKit.cutAction);
        a.putValue(Action.NAME, "Cut");
        JMenuItem cut = new JMenuItem(a);
        cut.setEnabled(false);

        a = area.getActionMap().get(DefaultEditorKit.copyAction);
        a.putValue(Action.NAME, "Copy");
        JMenuItem copy = new JMenuItem(a);

        a = area.getActionMap().get(DefaultEditorKit.pasteAction);
        a.putValue(Action.NAME, "Paste");
        JMenuItem paste = new JMenuItem(a);
        paste.setEnabled(false);

        a = area.getActionMap().get(DefaultEditorKit.selectAllAction);
        a.putValue(Action.NAME, "Select All");
        JMenuItem select = new JMenuItem(a);

        JPopupMenu menu = new JPopupMenu();
        menu.add(cut);
        menu.add(copy);
        menu.add(paste);
        menu.addSeparator();
        menu.add(select);
        area.setComponentPopupMenu(menu);

        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                area.requestFocusInWindow();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }
        });
    }
}
