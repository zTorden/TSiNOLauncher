package net.minecraft.launcher.authentication.exceptions;

public class UserMigratedException extends InvalidCredentialsException {
	public UserMigratedException() {
	}

	public UserMigratedException(String message) {
		super(message);
	}

	public UserMigratedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserMigratedException(Throwable cause) {
		super(cause);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.exceptions.UserMigratedException
 * JD-Core Version: 0.6.2
 */