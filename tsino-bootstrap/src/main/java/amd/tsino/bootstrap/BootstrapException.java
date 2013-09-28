package amd.tsino.bootstrap;

public class BootstrapException extends Exception {
	private static final long serialVersionUID = 942743360834708419L;

	public BootstrapException(String message) {
		super(message);
	}

	public BootstrapException(String message, Throwable cause) {
		super(message, cause);
	}
}
