package amd.tsino.launcher.auth;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

    AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    AuthenticationException(Throwable cause) {
        super(cause);
    }

    AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    AuthenticationException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
