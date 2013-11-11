package amd.tsino.launcher;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class LauncherLog {
    private final ByteArrayOutputStream os;
    private final PrintWriter pw;
    private String bootstrapLog = "";

    public LauncherLog() {
        os = new ByteArrayOutputStream(2048);
        pw = new PrintWriter(new OutputStreamWriter(os, LauncherConstants.DEFAULT_CHARSET));
    }

    public void log(String format, Object... args) {
        format += "%n";
        System.out.printf(format, args);
        pw.printf(format, args);
    }

    void error(String format, Object... args) {
        log("[ERROR] " + format, args);
    }

    public void error(Throwable t) {
        error("%s", t);
        t.printStackTrace();
        t.printStackTrace(pw);
    }

    public String getDump() {
        pw.flush();
        return bootstrapLog + new String(os.toByteArray(), LauncherConstants.DEFAULT_CHARSET);
    }

    public void setBootstrapLog(String bootstrapLog) {
        this.bootstrapLog = bootstrapLog;
    }
}
