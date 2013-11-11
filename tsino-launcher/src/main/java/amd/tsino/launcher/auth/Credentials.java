package amd.tsino.launcher.auth;

public class Credentials {
    private final String selectedProfile;
    private final String password;
    private final boolean remember;

    public Credentials() {
        selectedProfile = "";
        password = "";
        remember = true;
    }

    public Credentials(String user, String password, boolean remember) {
        this.selectedProfile = user;
        this.password = password;
        this.remember = remember;
    }

    public String getUser() {
        return selectedProfile;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRemember() {
        return remember;
    }
}
