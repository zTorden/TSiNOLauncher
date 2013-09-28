package net.minecraft.launcher.authentication.exceptions;

public class UpdateLauncherException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public UpdateLauncherException() {
	}

	public UpdateLauncherException(String message) {
		super(message);
	}

	public UpdateLauncherException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateLauncherException(Throwable cause) {
		super(cause);
	}
}
