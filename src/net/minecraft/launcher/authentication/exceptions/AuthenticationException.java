package net.minecraft.launcher.authentication.exceptions;

public class AuthenticationException extends Exception {
	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.exceptions.AuthenticationException
 * JD-Core Version: 0.6.2
 */