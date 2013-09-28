package net.minecraft.launcher.process;

public class MinecraftProcessArguments {
	public static final String LEGACY = " ${auth_player_name} ${auth_session}";
	public static final String USERNAME_SESSION = "--username ${auth_player_name} --session ${auth_session}";
	public static final String USERNAME_SESSION_VERSION = "--username ${auth_player_name} --session ${auth_session} --version ${profile_name}";
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.process.MinecraftProcessArguments JD-Core Version:
 * 0.6.2
 */