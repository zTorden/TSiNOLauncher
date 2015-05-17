package amd.tsino.launcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerInfo{
	private String name;
	private String clientPath;
	private String directory;
	
	private static List<ServerInfo> servers=new ArrayList<>();
	public static void addServer(String name, String clientPath, String directory){
		servers.add(new ServerInfo(name, clientPath, directory));
	}
	
	public static List<ServerInfo> getServers(){
		return Collections.unmodifiableList(servers);
	}
	
	private ServerInfo(String name, String clientPath, String directory){
		this.name=name;
		this.clientPath=clientPath;
		this.directory=directory;
	}
	
	public String getName(){
		return name;
	}
	
	public String getClientPath(){
		return clientPath;
	}
	
	public String getDirectory(){
		return directory;
	}

	public URL getConfigUrl() {
		return LauncherUtils.getURL(clientPath + LauncherConstants.CONFIG_BASE + LauncherConstants.CONFIG_ZIP);
	}

	public URL getRcpackUrl() {
		return  LauncherUtils.getURL(clientPath + LauncherConstants.RCPACK_ZIP);
	}

	public URL getResourcesUrl() {
		return  LauncherUtils.getURL(clientPath + LauncherConstants.RESOURCES_BASE + LauncherConstants.RESOURCES_ZIP);
	}

	public URL getServersDatUrl() {
		return  LauncherUtils.getURL(clientPath + LauncherConstants.SERVERS_DAT);
	}
}
