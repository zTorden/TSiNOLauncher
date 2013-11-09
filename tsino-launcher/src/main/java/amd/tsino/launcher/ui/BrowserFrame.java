package amd.tsino.launcher.ui;

import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.style.BrowserPanelStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.IOException;
import java.net.URL;

@SuppressWarnings("serial")
public class BrowserFrame extends JTextPane {
    public BrowserFrame(BrowserPanelStyle style) throws IOException {
        setEditable(false);
        setFocusable(false);
        setBorder(null);
        setOpaque(false);
        setContentType("text/html");
        setBounds(style.x, style.y, style.w, style.h);

        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent he) {
                if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        LauncherUtils.openLink(he.getURL().toURI());
                    } catch (Exception e) {
                        Launcher.getInstance().getLog().error(e);
                    }
                }
            }
        });

        final URL url = new URL(style.url);
        Thread thread = new Thread("Update website tab") {
            @Override
            public void run() {
                try {
                    setPage(url);
                } catch (IOException e) {
                    Launcher.getInstance().getLog().error(e);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
