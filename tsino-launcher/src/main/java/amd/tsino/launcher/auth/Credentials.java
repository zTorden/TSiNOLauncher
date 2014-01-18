package amd.tsino.launcher.auth;

public class Credentials {
    private final String user;
    private final String password;
    private final boolean remember;

    public Credentials(String user, String password, boolean remember) {
        if (user != null) this.user = user;
        else this.user = "";
        if (password != null) this.password = password;
        else this.password = "";
        this.remember = remember;
    }

    public String getUser() {

        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRemember() {
        return remember;
    }
}
