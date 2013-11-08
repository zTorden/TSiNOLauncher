package amd.tsino.launcher.auth;

@SuppressWarnings("serial")
public class InvalidCredentialsException extends AuthenticationException {

	public InvalidCredentialsException() {
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCredentialsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
