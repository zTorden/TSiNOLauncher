package amd.tsino.launcher.ui;

import java.io.IOException;
import java.util.Iterator;

import amd.tsino.launcher.ServerInfo;
import amd.tsino.launcher.style.ServerPanelStyle;

@SuppressWarnings("serial")
public class ServerPanel extends ImagePanel {

    private ServerButton[] serverButtons = new ServerButton[2];

    public ServerPanel(ServerPanelStyle style) throws IOException {
        super(style);
        setLayout(null);

        Iterator<ServerInfo>servers=ServerInfo.getServers().iterator();
        add(serverButtons[0]=new ServerButton(style.server0,servers.next()));
        add(serverButtons[1]=new ServerButton(style.server1,servers.next()));
    }
    
    public ServerButton[] getServerButtons(){
    	return serverButtons;
    }
}