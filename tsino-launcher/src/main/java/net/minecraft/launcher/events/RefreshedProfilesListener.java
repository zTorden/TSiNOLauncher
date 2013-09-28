package net.minecraft.launcher.events;

import net.minecraft.launcher.profile.ProfileManager;

public abstract interface RefreshedProfilesListener {
	public abstract void onProfilesRefreshed(ProfileManager paramProfileManager);

	public abstract boolean shouldReceiveEventsInUIThread();
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.events.RefreshedProfilesListener JD-Core Version:
 * 0.6.2
 */