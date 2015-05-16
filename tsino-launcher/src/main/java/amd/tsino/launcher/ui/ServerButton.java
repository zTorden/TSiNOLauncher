package amd.tsino.launcher.ui;

import java.io.IOException;

import amd.tsino.launcher.ServerInfo;
import amd.tsino.launcher.style.ButtonStyle;

@SuppressWarnings("serial")
public class ServerButton extends Button {
	
	private ServerInfo server;

	public ServerButton(ButtonStyle style, ServerInfo server) throws IOException {
		super(style);
		this.server=server;
		setFocusable(false);
		
	}

	public ServerInfo getServer(){
		return server;
	}
}
