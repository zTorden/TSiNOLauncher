package net.minecraft.launcher.ui.bottombar;

import java.awt.GridBagConstraints;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import net.minecraft.launcher.Http;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.updater.LowerCaseEnumTypeAdapterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class StatusPanelForm extends SidebarGridForm {
	private static final String SERVER_SESSION = "session.minecraft.net";
	private static final String SERVER_LOGIN = "login.minecraft.net";
	private final Launcher launcher;
	private final JLabel sessionStatus = new JLabel("???");
	private final JLabel loginStatus = new JLabel("???");
	private final Gson gson = new GsonBuilder().registerTypeAdapterFactory(
			new LowerCaseEnumTypeAdapterFactory()).create();

	public StatusPanelForm(Launcher launcher) {
		this.launcher = launcher;

		createInterface();
		refreshStatuses();
	}

	protected void populateGrid(GridBagConstraints constraints) {
		add(new JLabel("Multiplayer:", 2), constraints, 0, 0, 0, 1, 17);
		add(this.sessionStatus, constraints, 1, 0, 1, 1);

		add(new JLabel("Login:", 2), constraints, 0, 1, 0, 1, 17);
		add(this.loginStatus, constraints, 1, 1, 1, 1);
	}

	public JLabel getSessionStatus() {
		return this.sessionStatus;
	}

	public JLabel getLoginStatus() {
		return this.loginStatus;
	}

	public void refreshStatuses() {
		this.launcher.getVersionManager().getExecutorService()
				.submit(new Runnable() {
					public void run() {
						try {
							TypeToken<List<Map<String, String>>> token = new TypeToken<List<Map<String, String>>>() {
							};
							@SuppressWarnings("unchecked")
							List<Map<String, String>> statuses = (List<Map<String, String>>) StatusPanelForm.this.gson
									.fromJson(Http.performGet(new URL(
											"http://status.mojang.com/check"),
											StatusPanelForm.this.launcher
													.getProxy()), token
											.getType());

							for (Map<String, String> serverStatusInformation : statuses)
								if (serverStatusInformation
										.containsKey("login.minecraft.net"))
									StatusPanelForm.this.loginStatus.setText(serverStatusInformation
											.get("login.minecraft.net"));
								else if (serverStatusInformation
										.containsKey("session.minecraft.net"))
									StatusPanelForm.this.sessionStatus.setText(serverStatusInformation
											.get("session.minecraft.net"));
						} catch (Exception e) {
							Launcher.getInstance().println(
									"Couldn't get server status", e);
						}
					}
				});
	}

	public static enum ServerStatus {
		GREEN("Online, no problems detected."), YELLOW(
				"May be experiencing issues."), RED(
				"Offline, experiencing problems.");

		private final String title;

		private ServerStatus(String title) {
			this.title = title;
		}
	}
}
