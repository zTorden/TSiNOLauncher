package net.minecraft.launcher.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.launcher.OperatingSystem;

public class Rule {
	public static enum Action {
		ALLOW, DISALLOW;
	}

	public class OSRestriction {
		private OperatingSystem name;
		private String version;

		public OSRestriction() {
		}

		public boolean isCurrentOperatingSystem() {
			if ((this.name != null)
					&& (this.name != OperatingSystem.getCurrentPlatform()))
				return false;

			if (this.version != null)
				try {
					Pattern pattern = Pattern.compile(this.version);
					Matcher matcher = pattern.matcher(System
							.getProperty("os.version"));
					if (!matcher.matches())
						return false;
				} catch (Throwable localThrowable) {
				}
			return true;
		}

		@Override
		public String toString() {
			return "OSRestriction{name=" + this.name + ", version='"
					+ this.version + '\'' + '}';
		}
	}

	private Action action = Action.ALLOW;

	private OSRestriction os;

	public Action getAppliedAction() {
		if ((this.os != null) && (!this.os.isCurrentOperatingSystem()))
			return null;

		return this.action;
	}

	@Override
	public String toString() {
		return "Rule{action=" + this.action + ", os=" + this.os + '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.versions.Rule JD-Core Version: 0.6.2
 */