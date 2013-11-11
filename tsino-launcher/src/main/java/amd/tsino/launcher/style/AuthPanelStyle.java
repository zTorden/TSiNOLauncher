package amd.tsino.launcher.style;

public class AuthPanelStyle extends ImagePanelStyle {
    public final TextFieldStyle login;
    public final TextFieldStyle password;
    public final CheckButtonStyle check;
    public final ButtonStyle enter;
    public final ButtonStyle register;
    public final ImagePanelStyle error;
    public final BrowserPanelStyle browser;

    public AuthPanelStyle() {
        this.login = null;
        this.password = null;
        this.check = null;
        this.enter = null;
        this.register = null;
        this.error = null;
        this.browser = null;
    }
}
