package net.minecraft.launcher.authentication.yggdrasil;

public class Response {
	private String error;
	private String errorMessage;
	private String cause;

	public String getError() {
		return this.error;
	}

	public String getCause() {
		return this.cause;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.Response JD-Core Version:
 * 0.6.2
 */