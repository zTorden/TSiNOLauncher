package net.minecraft.launcher.authentication.yggdrasil;

public class AuthenticationRequest {
	private Agent agent;
	private String username;
	private String password;
	private String clientToken;

	public AuthenticationRequest(
			YggdrasilAuthenticationService authenticationService,
			String password) {
		this.agent = authenticationService.getAgent();
		this.username = authenticationService.getUsername();
		this.clientToken = authenticationService.getClientToken();
		this.password = password;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.AuthenticationRequest JD-Core
 * Version: 0.6.2
 */