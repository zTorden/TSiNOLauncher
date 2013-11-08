package amd.tsino.launcher.auth;

@SuppressWarnings("serial")
public class UpdateLauncherException extends AuthenticationException {

	public UpdateLauncherException() {
	}

	public UpdateLauncherException(String message) {
		super(message);
	}

	public UpdateLauncherException(Throwable cause) {
		super(cause);
	}

	public UpdateLauncherException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateLauncherException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
