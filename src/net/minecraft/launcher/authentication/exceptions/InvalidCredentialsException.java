package net.minecraft.launcher.authentication.exceptions;

public class InvalidCredentialsException extends AuthenticationException {
	public InvalidCredentialsException() {
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.exceptions.InvalidCredentialsException
 * JD-Core Version: 0.6.2
 */