package amd.tsino.launcher;

public class LauncherLog {
	public void log(String format, Object... args) {
		format += "\n";
		System.out.printf(format, args);
	}

	public void error(String format, Object... args) {
		log("[ERROR] " + format, args);
	}

	public void error(Throwable t) {
		error("%s", t);
		t.printStackTrace();
	}
}
