package net.minecraft.launcher.events;

import net.minecraft.launcher.updater.VersionManager;

public abstract interface RefreshedVersionsListener {
	public abstract void onVersionsRefreshed(VersionManager paramVersionManager);

	public abstract boolean shouldReceiveEventsInUIThread();
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.events.RefreshedVersionsListener JD-Core Version:
 * 0.6.2
 */