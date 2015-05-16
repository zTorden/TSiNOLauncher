package amd.tsino.launcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Date;
import net.minecraft.launcher.Launcher;

public class LauncherLog {
    private final ByteArrayOutputStream os;
    private final PrintStream ps;
    private final PrintStream logFile;
    private String bootstrapLog = "";

    public LauncherLog() {
        os = new ByteArrayOutputStream(2048);
        try {
            ps = new PrintStream(os, false, LauncherConstants.DEFAULT_CHARSET.name());
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        
        PrintStream log=null;
        try {
            log = new PrintStream (LauncherUtils.getLauncherFile("launcher.log"));
        } catch (FileNotFoundException e) {
	    System.out.println("Could not create launcher.log");
	} finally {
	    logFile=log;
	}
    }

    public void log(String format, Object... args) {
        format += "%n";
        System.out.printf(format, args);
        if(logFile!=null){
          logFile.printf("[%1$tD %1$tT] ", new Date());
	  logFile.printf(format, args);
	}
        ps.printf(format, args);
    }

    void error(String format, Object... args) {
        log("[ERROR] " + format, args);
    }

    public void error(Throwable t) {
        error("%s", t);
        t.printStackTrace();
        t.printStackTrace(ps);
        if(logFile!=null)t.printStackTrace(logFile);
    }

    public String getDump() {
        ps.flush();
        return bootstrapLog + new String(os.toByteArray(), LauncherConstants.DEFAULT_CHARSET);
    }

    public void setBootstrapLog(String bootstrapLog) {
        this.bootstrapLog = bootstrapLog;
    }

    public PrintStream getPS() {
        return ps;
    }
}
