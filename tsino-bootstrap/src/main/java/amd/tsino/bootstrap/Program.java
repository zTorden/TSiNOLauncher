package amd.tsino.bootstrap;

public class Program {
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.net.useSystemProxies", "true");

		Bootstrap.getInstance().run();
	}
}
