package net.minecraft.launcher.authentication.yggdrasil;

public class InvalidateRequest {
	private String accessToken;
	private String clientToken;

	public InvalidateRequest(
			YggdrasilAuthenticationService authenticationService) {
		this.accessToken = authenticationService.getAccessToken();
		this.clientToken = authenticationService.getClientToken();
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.InvalidateRequest JD-Core
 * Version: 0.6.2
 */