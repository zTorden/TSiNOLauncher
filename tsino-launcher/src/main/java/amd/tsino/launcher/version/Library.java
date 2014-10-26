package amd.tsino.launcher.version;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.Downloadable;
import net.minecraft.launcher.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Library implements Downloadable {
    private final String name;
    private final List<Rule> rules;
    private final Map<String, String> natives;
    private final ExtractRule extract;
    private String url;

    public Library() {
        name = null;
        rules = null;
        natives = null;
        extract = null;
    }

    public Library(String name, List<Rule> rules, Map<String, String> natives, ExtractRule extract, String url) {
        this.name = name;
        this.rules = rules;
        this.natives = natives;
        this.extract = extract;
        this.url = url;
    }

    public void extractFiles(File dir) throws IOException {
        LauncherUtils.unzip(getFile(), dir, extract.getExclude());
    }

    private String getFileName() {
        StringBuilder sb = new StringBuilder(LauncherConstants.LIBRARIES_BASE);
        String[] parts = name.split(":");
        String[] path = parts[0].split(Pattern.quote("."));
        for (String p : path) {
            sb.append(p);
            sb.append("/");
        }
        sb.append(parts[1]);
        sb.append("/");
        sb.append(parts[2]);
        sb.append("/");
        sb.append(parts[1]);
        sb.append("-");
        sb.append(parts[2]);
        if (isNative()) {
            sb.append("-");
            sb.append(natives.get(LauncherUtils.getOperatingSystem()).replace("${arch}",System.getProperty("sun.arch.data.model")));
        }
        sb.append(".jar");
        return sb.toString();
    }

    public boolean isNative() {
        return natives != null;
    }

    @Override
    public URL getURL() {
        if (url == null) {
            url = LauncherConstants.BASE_URL;
        }
        if (url.charAt(url.length() - 1) != '/') {
            url += '/';
        }
        try {
            return new URL(url + getFileName());
        } catch (MalformedURLException e) {
            Launcher.getInstance().getLog().error(e);
        }
        return null;
    }

    @Override
    public File getFile() {
        return LauncherUtils.getFile(getFileName());
    }

    public boolean isAllowed() {
        if (rules == null) return true;
        boolean allowed = false;
        for (Rule rule : rules) {
            if (rule.isApplicable()) {
                allowed = rule.isAllow();
            }
        }
        return allowed;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Library && name.equals(((Library) obj).name);
    }

    @Override
    public String toString() {
        return String.format("Library {name = %s}", name);
    }

    public static class Rule {
        private final String action;
        private final OS os;

        public Rule() {
            action = null;
            os = null;
        }

        public boolean isAllow() {
            return "allow".equals(action);
        }

        public boolean isApplicable() {
            if (os != null) {
                if (os.getName() != null) {
                    if (!LauncherUtils.getOperatingSystem().equals(os.name)) {
                        return false;
                    }
                    if (os.getVersion() != null) {
                        try {
                            Pattern pattern = Pattern.compile(os.version);
                            Matcher matcher = pattern.matcher(System
                                    .getProperty("os.version"));
                            if (!matcher.matches())
                                return false;
                        } catch (Exception t) {
                            Launcher.getInstance().getLog().error(t);
                        }
                    }
                }
            }
            return true;
        }

        public static class OS {
            private final String name;
            private final String version;

            public OS() {
                name = null;
                version = null;
            }

            public String getName() {
                return name;
            }

            public String getVersion() {
                return version;
            }
        }
    }

    public static class ExtractRule {
        private final List<String> exclude;

        public ExtractRule() {
            exclude = null;
        }

        public List<String> getExclude() {
            return exclude;
        }
    }
}