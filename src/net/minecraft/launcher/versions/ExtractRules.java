package net.minecraft.launcher.versions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractRules {
	private List<String> exclude = new ArrayList<String>();

	public ExtractRules() {
	}

	public ExtractRules(String[] exclude) {
		if (exclude != null)
			Collections.addAll(this.exclude, exclude);
	}

	public List<String> getExcludes() {
		return this.exclude;
	}

	public boolean shouldExtract(String path) {
		if (this.exclude != null) {
			for (String rule : this.exclude) {
				if (path.startsWith(rule))
					return false;
			}
		}

		return true;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.versions.ExtractRules JD-Core Version: 0.6.2
 */