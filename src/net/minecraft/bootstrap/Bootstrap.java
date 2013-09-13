package net.minecraft.bootstrap;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

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
import net.minecraft.hopper.HopperService;
import LZMA.LzmaInputStream;

public class Bootstrap extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
	public static final String LAUNCHER_URL = "http://tmpfs.linuxd.org/launcher/launcher.pack.lzma";
	private final File workDir;
	private final Proxy proxy;
	private final File launcherJar;
	private final File packedLauncherJar;
	private final File packedLauncherJarNew;
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
		this.launcherJar = new File(workDir, "launcher.jar");
		this.packedLauncherJar = new File(workDir, "launcher.pack.lzma");
		this.packedLauncherJarNew = new File(workDir, "launcher.pack.lzma.new");

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

	public void execute(boolean force) {
		if (this.packedLauncherJarNew.isFile()) {
			println("Found cached update");
			renameNew();
		}

		Downloader.Controller controller = new Downloader.Controller();

		if ((force) || (!this.packedLauncherJar.exists())) {
			Downloader downloader = new Downloader(controller, this,
					this.proxy, null, this.packedLauncherJarNew);
			downloader.run();

			if (controller.hasDownloadedLatch.getCount() != 0L) {
				throw new FatalBootstrapError(
						"Unable to download while being forced");
			}

			renameNew();
		} else {
			String md5 = getMd5(this.packedLauncherJar);

			Thread thread = new Thread(new Downloader(controller, this,
					this.proxy, md5, this.packedLauncherJarNew));
			thread.setName("Launcher downloader");
			thread.start();
			try {
				println("Looking for update");
				boolean wasInTime = controller.foundUpdateLatch.await(3L,
						TimeUnit.SECONDS);

				if (controller.foundUpdate.get()) {
					println("Found update in time, waiting to download");
					controller.hasDownloadedLatch.await();
					renameNew();
				} else if (!wasInTime) {
					println("Didn't find an update in time.");
				}
			} catch (InterruptedException e) {
				throw new FatalBootstrapError(new StringBuilder()
						.append("Got interrupted: ").append(e.toString())
						.toString());
			}
		}

		unpack();
		startLauncher(this.launcherJar);
	}

	public void unpack() {
		File lzmaUnpacked = getUnpackedLzmaFile(this.packedLauncherJar);
		InputStream inputHandle = null;
		OutputStream outputHandle = null;

		println(new StringBuilder().append("Reversing LZMA on ")
				.append(this.packedLauncherJar).append(" to ")
				.append(lzmaUnpacked).toString());
		try {
			inputHandle = new LzmaInputStream(new FileInputStream(
					this.packedLauncherJar));
			outputHandle = new FileOutputStream(lzmaUnpacked);

			byte[] buffer = new byte[65536];

			int read = inputHandle.read(buffer);
			while (read >= 1) {
				outputHandle.write(buffer, 0, read);
				read = inputHandle.read(buffer);
			}
		} catch (Exception e) {
			throw new FatalBootstrapError(new StringBuilder()
					.append("Unable to un-lzma: ").append(e).toString());
		} finally {
			closeSilently(inputHandle);
			closeSilently(outputHandle);
		}

		println(new StringBuilder().append("Unpacking ").append(lzmaUnpacked)
				.append(" to ").append(this.launcherJar).toString());

		JarOutputStream jarOutputStream = null;
		try {
			jarOutputStream = new JarOutputStream(new FileOutputStream(
					this.launcherJar));
			Pack200.newUnpacker().unpack(lzmaUnpacked, jarOutputStream);
		} catch (Exception e) {
			throw new FatalBootstrapError(new StringBuilder()
					.append("Unable to un-pack200: ").append(e).toString());
		} finally {
			closeSilently(jarOutputStream);
		}

		println(new StringBuilder().append("Cleaning up ").append(lzmaUnpacked)
				.toString());

		lzmaUnpacked.delete();
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException ignored) {
			}
	}

	private File getUnpackedLzmaFile(File packedLauncherJar) {
		String filePath = packedLauncherJar.getAbsolutePath();
		if (filePath.endsWith(".lzma")) {
			filePath = filePath.substring(0, filePath.length() - 5);
		}
		return new File(filePath);
	}

	public String getMd5(File file) {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(new FileInputStream(file),
					MessageDigest.getInstance("MD5"));
			byte[] buffer = new byte[65536];

			int read = stream.read(buffer);
			while (read >= 1)
				read = stream.read(buffer);
		} catch (Exception ignored) {
			return null;
		} finally {
			closeSilently(stream);
		}

		return String.format("%1$032x", new Object[] { new BigInteger(1, stream
				.getMessageDigest().digest()) });
	}

	public void println(String string) {
		print(new StringBuilder().append(string).append("\n").toString());
	}

	public void print(String string) {
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
				public void run() {
					scrollBar.setValue(2147483647);
				}
			});
	}

	public void startLauncher(File launcherJar) {
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

	public void renameNew() {
		if ((this.packedLauncherJar.exists())
				&& (!this.packedLauncherJar.isFile())
				&& (!this.packedLauncherJar.delete())) {
			throw new FatalBootstrapError(new StringBuilder()
					.append("while renaming, target path: ")
					.append(this.packedLauncherJar.getAbsolutePath())
					.append(" is not a file and we failed to delete it")
					.toString());
		}

		if (this.packedLauncherJarNew.isFile()) {
			println(new StringBuilder().append("Renaming ")
					.append(this.packedLauncherJarNew.getAbsolutePath())
					.append(" to ")
					.append(this.packedLauncherJar.getAbsolutePath())
					.toString());

			if (this.packedLauncherJarNew.renameTo(this.packedLauncherJar)) {
				println("Renamed successfully.");
			} else {
				if ((this.packedLauncherJar.exists())
						&& (!this.packedLauncherJar.canWrite())) {
					throw new FatalBootstrapError(new StringBuilder()
							.append("unable to rename: target")
							.append(this.packedLauncherJar.getAbsolutePath())
							.append(" not writable").toString());
				}

				println("Unable to rename - could be on another filesystem, trying copy & delete.");

				if ((this.packedLauncherJarNew.exists())
						&& (this.packedLauncherJarNew.isFile()))
					try {
						copyFile(this.packedLauncherJarNew,
								this.packedLauncherJar);
						if (this.packedLauncherJarNew.delete())
							println("Copy & delete succeeded.");
						else
							println(new StringBuilder()
									.append("Unable to remove ")
									.append(this.packedLauncherJarNew
											.getAbsolutePath())
									.append(" after copy.").toString());
					} catch (IOException e) {
						throw new FatalBootstrapError(new StringBuilder()
								.append("unable to copy:").append(e).toString());
					}
				else
					println("Nevermind... file vanished?");
			}
		}
	}

	public static void copyFile(File source, File target) throws IOException {
		if (!target.exists()) {
			target.createNewFile();
		}

		FileChannel sourceChannel = null;
		FileChannel targetChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			targetChannel = new FileOutputStream(target).getChannel();
			targetChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
		} finally {
			if (sourceChannel != null) {
				sourceChannel.close();
			}

			if (targetChannel != null)
				targetChannel.close();
		}
	}

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
				proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(
						hostName,
						((Integer) optionSet.valueOf(proxyPortOption))
								.intValue()));
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
				protected PasswordAuthentication getPasswordAuthentication() {
					return auth;
				}

			});
		}

		File workingDirectory = (File) optionSet
				.valueOf(workingDirectoryOption);
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
		String[] remainderArgs = (String[]) strings.toArray(new String[strings
				.size()]);

		boolean force = optionSet.has("force");

		Bootstrap frame = new Bootstrap(workingDirectory, proxy,
				passwordAuthentication, remainderArgs);
		try {
			frame.execute(force);
		} catch (Throwable t) {
			ByteArrayOutputStream stracktrace = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(stracktrace));

			StringBuilder report = new StringBuilder();
			report.append(stracktrace).append("\n\n-- Head --\nStacktrace:\n")
					.append(stracktrace).append("\n\n")
					.append(frame.outputBuffer);
			report.append("\tMinecraft.Bootstrap Version: 5");
			try {
				HopperService.submitReport(proxy, report.toString(),
						"Minecraft.Bootstrap", "5");
			} catch (Throwable ignored) {
			}
			frame.println(new StringBuilder().append("FATAL ERROR: ")
					.append(stracktrace.toString()).toString());
			frame.println("\nPlease fix the error and restart.");
		}
	}

	public static boolean stringHasValue(String string) {
		return (string != null) && (!string.isEmpty());
	}
}
