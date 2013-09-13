package net.minecraft.launcher.authentication.yggdrasil;

import net.minecraft.launcher.authentication.GameProfile;

public class AuthenticationResponse extends Response {
	private String accessToken;
	private String clientToken;
	private GameProfile selectedProfile;
	private GameProfile[] availableProfiles;

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getClientToken() {
		return this.clientToken;
	}

	public GameProfile[] getAvailableProfiles() {
		return this.availableProfiles;
	}

	public GameProfile getSelectedProfile() {
		return this.selectedProfile;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.AuthenticationResponse
 * JD-Core Version: 0.6.2
 */