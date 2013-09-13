package net.minecraft.launcher.authentication.yggdrasil;

import net.minecraft.launcher.authentication.GameProfile;

public class RefreshRequest {
	private String clientToken;
	private String accessToken;
	private GameProfile selectedProfile;

	public RefreshRequest(YggdrasilAuthenticationService authenticationService) {
		this(authenticationService, null);
	}

	public RefreshRequest(YggdrasilAuthenticationService authenticationService,
			GameProfile profile) {
		this.clientToken = authenticationService.getClientToken();
		this.accessToken = authenticationService.getAccessToken();
		this.selectedProfile = profile;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.RefreshRequest JD-Core
 * Version: 0.6.2
 */