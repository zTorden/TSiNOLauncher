package net.minecraft.launcher.profile;

public enum LauncherVisibilityRule {
	HIDE_LAUNCHER("Hide launcher and re-open when game closes"), CLOSE_LAUNCHER(
			"Close launcher when game starts"), DO_NOTHING(
			"Keep the launcher open");

	private final String name;

	private LauncherVisibilityRule(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.profile.LauncherVisibilityRule JD-Core Version: 0.6.2
 */