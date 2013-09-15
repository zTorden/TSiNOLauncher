package net.minecraft.bootstrap;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Bootstrap extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Font MONOSPACED = new Font("Monospaced", 0, 12);

	public static void main(String[] args) throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");

		OptionParser optionParser = new OptionParser();
		optionParser.allowsUnrecognizedOptions();

		optionParser.accepts("help", "Show help").forHelp();
		optionParser.accepts("force", "Force updating");

		OptionSpec<?> proxyHostOption = optionParser.accepts("proxyHost",
				"Optional").withRequiredArg();
		OptionSpec<Integer> proxyPortOption = optionParser
				.accepts("proxyPort", "Optional").withRequiredArg()
				.defaultsTo("8080", new String[0]).ofType(Integer.class);
		OptionSpec<?> proxyUserOption = optionParser.accepts("proxyUser",
				"Optional").withRequiredArg();
		OptionSpec<?> proxyPassOption = optionParser.accepts("proxyPass",
				"Optional").withRequiredArg();
		OptionSpec<File> workingDirectoryOption = optionParser
				.accepts("workDir", "Optional").withRequiredArg()
				.ofType(File.class)
				.defaultsTo(Util.getWorkingDirectory(), new File[0]);
		OptionSpec<?> nonOptions = optionParser.nonOptions();
		OptionSet optionSet;
		try {
			optionSet = optionParser.parse(args);
		} catch (OptionException e) {
			optionParser.printHelpOn(System.out);
			System.out
					.println("(to pass in arguments to minecraft directly use: '--' followed by your arguments");
			return;
		}

		if (optionSet.has("help")) {
			optionParser.printHelpOn(System.out);
			return;
		}

		String hostName = (String) optionSet.valueOf(proxyHostOption);
		Proxy proxy = Proxy.NO_PROXY;
		if (hostName != null) {
			try {
				proxy = new Proxy(Proxy.Type.SOCKS,
						new InetSocketAddress(hostName, optionSet.valueOf(
								proxyPortOption).intValue()));
			} catch (Exception ignored) {
			}
		}
		String proxyUser = (String) optionSet.valueOf(proxyUserOption);
		String proxyPass = (String) optionSet.valueOf(proxyPassOption);
		PasswordAuthentication passwordAuthentication = null;
		if ((!proxy.equals(Proxy.NO_PROXY)) && (stringHasValue(proxyUser))
				&& (stringHasValue(proxyPass))) {
			passwordAuthentication = new PasswordAuthentication(proxyUser,
					proxyPass.toCharArray());

			final PasswordAuthentication auth = passwordAuthentication;
			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return auth;
				}

			});
		}

		File workingDirectory = optionSet.valueOf(workingDirectoryOption);
		if ((workingDirectory.exists()) && (!workingDirectory.isDirectory()))
			throw new FatalBootstrapError(new StringBuilder()
					.append("Invalid working directory: ")
					.append(workingDirectory).toString());
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) {
			throw new FatalBootstrapError(new StringBuilder()
					.append("Unable to create directory: ")
					.append(workingDirectory).toString());
		}

		List<?> strings = optionSet.valuesOf(nonOptions);
		String[] remainderArgs = strings.toArray(new String[strings.size()]);

		Bootstrap frame = new Bootstrap(workingDirectory, proxy,
				passwordAuthentication, remainderArgs);
		try {
			frame.startLauncher();
		} catch (Throwable t) {
			ByteArrayOutputStream stracktrace = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(stracktrace));
			frame.println(new StringBuilder().append("FATAL ERROR: ")
					.append(stracktrace.toString()).toString());
			frame.println("\nPlease fix the error and restart.");
		}
	}

	private static boolean stringHasValue(String string) {
		return (string != null) && (!string.isEmpty());
	}

	private final File workDir;
	private final Proxy proxy;
	private final JTextArea textArea;
	private final JScrollPane scrollPane;
	private final PasswordAuthentication proxyAuth;

	private final String[] remainderArgs;

	private final StringBuilder outputBuffer = new StringBuilder();

	public Bootstrap(File workDir, Proxy proxy,
			PasswordAuthentication proxyAuth, String[] remainderArgs) {
		super("Minecraft Launcher");
		this.workDir = workDir;
		this.proxy = proxy;
		this.proxyAuth = proxyAuth;
		this.remainderArgs = remainderArgs;
		new File(workDir, "launcher.jar");

		setSize(854, 480);
		setDefaultCloseOperation(3);

		this.textArea = new JTextArea();
		this.textArea.setLineWrap(true);
		this.textArea.setEditable(false);
		this.textArea.setFont(MONOSPACED);
		((DefaultCaret) this.textArea.getCaret()).setUpdatePolicy(1);

		this.scrollPane = new JScrollPane(this.textArea);
		this.scrollPane.setBorder(null);
		this.scrollPane.setVerticalScrollBarPolicy(22);

		add(this.scrollPane);
		setLocationRelativeTo(null);
		setVisible(true);

		println("Bootstrap (v5)");
		println(new StringBuilder()
				.append("Current time is ")
				.append(DateFormat.getDateTimeInstance(2, 2, Locale.US).format(
						new Date())).toString());
		println(new StringBuilder()
				.append("System.getProperty('os.name') == '")
				.append(System.getProperty("os.name")).append("'").toString());
		println(new StringBuilder()
				.append("System.getProperty('os.version') == '")
				.append(System.getProperty("os.version")).append("'")
				.toString());
		println(new StringBuilder()
				.append("System.getProperty('os.arch') == '")
				.append(System.getProperty("os.arch")).append("'").toString());
		println(new StringBuilder()
				.append("System.getProperty('java.version') == '")
				.append(System.getProperty("java.version")).append("'")
				.toString());
		println(new StringBuilder()
				.append("System.getProperty('java.vendor') == '")
				.append(System.getProperty("java.vendor")).append("'")
				.toString());
		println(new StringBuilder()
				.append("System.getProperty('sun.arch.data.model') == '")
				.append(System.getProperty("sun.arch.data.model")).append("'")
				.toString());
		println("");
	}

	private void print(String string) {
		System.out.print(string);

		this.outputBuffer.append(string);

		Document document = this.textArea.getDocument();
		final JScrollBar scrollBar = this.scrollPane.getVerticalScrollBar();

		boolean shouldScroll = scrollBar.getValue()
				+ scrollBar.getSize().getHeight() + MONOSPACED.getSize() * 2 > scrollBar
				.getMaximum();
		try {
			document.insertString(document.getLength(), string, null);
		} catch (BadLocationException ignored) {
		}
		if (shouldScroll)
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollBar.setValue(2147483647);
				}
			});
	}

	private void println(String string) {
		print(new StringBuilder().append(string).append("\n").toString());
	}

	public void startLauncher() {
		println("Starting launcher.");
		try {
			new net.minecraft.launcher.Launcher(this, workDir, proxy,
					proxyAuth, remainderArgs,
					BootstrapConstants.BOOTSTRAP_VERSION_NUMBER);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FatalBootstrapError(new StringBuilder()
					.append("Unable to start: ").append(e).toString());
		}
	}
}
