package amd.tsino.launcher;

import amd.tsino.launcher.auth.AuthenticationData;
import amd.tsino.launcher.auth.Credentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.launcher.Launcher;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LauncherSettings {
    private Settings settings;

    public LauncherSettings() {
        load();
    }

    private static class Settings {
        private String selectedProfile;
        private String password;
        private boolean remember = true;
        private String javaArgs;
        private Map<String,Boolean> disableMods=new HashMap<>();
        {
        	disableMods.put("Optifine", false);
        	disableMods.put("BetterFonts", false);
        }
        private boolean showOnClose;
    }

    public void load() {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(getFile()),
                    LauncherConstants.DEFAULT_CHARSET);
            final Gson gson = new Gson();
            settings = gson.fromJson(reader, LauncherSettings.Settings.class);
            reader.close();
            return;
        } catch (FileNotFoundException e) {
            Launcher.getInstance().getLog().log("Settings file not found.");
        } catch (Exception e) {
            Launcher.getInstance().getLog().error(e);
        }
        settings = new Settings();
    }

    public void save() throws IOException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new OutputStreamWriter(new FileOutputStream(getFile()), LauncherConstants.DEFAULT_CHARSET);
        writer.write(gson.toJson(settings));
        writer.close();
    }

    private static File getFile() {
        return LauncherUtils.getFile(LauncherConstants.AUTH_JSON);
    }

    public Credentials getCredentials() {
        return new Credentials(settings.selectedProfile, AuthenticationData.decrypt(settings.password, settings.selectedProfile), settings.remember);
    }

    public void setCredentials(Credentials crd) {
        settings.selectedProfile = crd.getUser();
        settings.password = AuthenticationData.encrypt(crd.getPassword(), crd.getUser());
        settings.remember = crd.isRemember();
    }

    public static String defaultJavaArgs() {
    	String args=("32".equals(System.getProperty("sun.arch.data.model")))?LauncherConstants.JVM_ARGS_32BIT : LauncherConstants.JVM_ARGS_64BIT;
        String version[]=System.getProperty("java.version").split("\\.");
        String versionParams=LauncherConstants.JVM_ARGS_VERSION.get(System.getProperty("java.version"));
        if(versionParams==null) versionParams=LauncherConstants.JVM_ARGS_VERSION.get(version[0]+"."+version[1]);
        return versionParams==null? args : args+" "+versionParams;
    }

    public String getJavaArgs() {
        if (settings.javaArgs != null) return settings.javaArgs;
        return defaultJavaArgs();
    }

    public void setJavaArgs(String args) {
        if (args == null) return;
        args = args.trim();
        if (defaultJavaArgs().equals(args)) {
            settings.javaArgs = null;
        } else {
            settings.javaArgs = args;
        }
    }

    public Map<String,Boolean> getDisabledMods() {
        return new HashMap<>(settings.disableMods);
    }

    public void setModDisabled(String name,boolean enabled) {
        settings.disableMods.put(name, enabled);
    }

    public boolean getShowOnClose() {
        return settings.showOnClose;
    }

    public void setShowOnClose(boolean showOnClose) {
        settings.showOnClose = showOnClose;
    }
}
