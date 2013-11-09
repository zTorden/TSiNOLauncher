package amd.tsino.launcher.auth;

public class Credentials implements Cloneable {
    private String selectedProfile;
    private String password;
    private boolean remember = true;

    @Override
    public Credentials clone() throws CloneNotSupportedException {
        return (Credentials) super.clone();
    }

    public String getUser() {
        return selectedProfile;
    }

    public void setUser(String selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}
