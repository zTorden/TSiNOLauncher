package net.minecraft.launcher.events;

import net.minecraft.launcher.authentication.AuthenticationService;

public abstract interface AuthenticationChangedListener {
	public abstract void onAuthenticationChanged(
			AuthenticationService paramAuthenticationService);

	public abstract boolean shouldReceiveEventsInUIThread();
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.events.AuthenticationChangedListener JD-Core Version:
 * 0.6.2
 */