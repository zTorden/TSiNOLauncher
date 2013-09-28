package net.minecraft.launcher.process;

import java.util.List;

public class JavaProcess {
	private static final int MAX_SYSOUT_LINES = 5;
	private final List<String> commands;
	private final Process process;
	private final LimitedCapacityList<String> sysOutLines = new LimitedCapacityList<String>(
			String.class, MAX_SYSOUT_LINES);
	private JavaProcessRunnable onExit;
	private ProcessMonitorThread monitor = new ProcessMonitorThread(this);

	public JavaProcess(List<String> commands, Process process) {
		this.commands = commands;
		this.process = process;

		this.monitor.start();
	}

	public int getExitCode() {
		try {
			return this.process.exitValue();
		} catch (IllegalThreadStateException ex) {
			ex.fillInStackTrace();
			throw ex;
		}
	}

	public JavaProcessRunnable getExitRunnable() {
		return this.onExit;
	}

	public Process getRawProcess() {
		return this.process;
	}

	public String getStartupCommand() {
		return this.process.toString();
	}

	public List<String> getStartupCommands() {
		return this.commands;
	}

	public LimitedCapacityList<String> getSysOutLines() {
		return this.sysOutLines;
	}

	public boolean isRunning() {
		try {
			this.process.exitValue();
		} catch (IllegalThreadStateException ex) {
			return true;
		}

		return false;
	}

	public void safeSetExitRunnable(JavaProcessRunnable runnable) {
		setExitRunnable(runnable);

		if ((!isRunning()) && (runnable != null))
			runnable.onJavaProcessEnded(this);
	}

	public void setExitRunnable(JavaProcessRunnable runnable) {
		this.onExit = runnable;
	}

	public void stop() {
		this.process.destroy();
	}

	@Override
	public String toString() {
		return "JavaProcess[commands=" + this.commands + ", isRunning="
				+ isRunning() + "]";
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.process.JavaProcess JD-Core Version: 0.6.2
 */