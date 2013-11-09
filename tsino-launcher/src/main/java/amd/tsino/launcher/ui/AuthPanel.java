package amd.tsino.launcher.ui;

import amd.tsino.launcher.auth.Credentials;
import amd.tsino.launcher.style.AuthPanelStyle;
import net.minecraft.launcher.Launcher;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@SuppressWarnings("serial")
public class AuthPanel extends ImagePanel {
    private TextField login;
    private TextField password;
    private ImagePanel error;
    private Button enter;
    private Button register;
    private CheckButton check;

    public AuthPanel(AuthPanelStyle style) throws IOException {
        super(style);
        setLayout(null);

        login = new TextField(style.login, false);
        password = new TextField(style.password, true);
        enter = new Button(style.enter);
        register = new Button(style.register);
        check = new CheckButton(style.check);
        error = new ImagePanel(style.error);
        error.setVisible(false);

        add(login);
        add(password);
        add(enter);
        add(register);
        add(check);
        add(error);
        add(new BrowserFrame(style.browser));

        DocumentListener dl = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                error.setVisible(false);
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                error.setVisible(false);
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                error.setVisible(false);
            }
        };

        login.getDocument().addDocumentListener(dl);
        password.getDocument().addDocumentListener(dl);

        setCredentials(Launcher.getInstance().getAuth().getCredentials());

        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Launcher.getInstance().launch();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public Credentials getCredentials() {
        Credentials crd = new Credentials();
        crd.setUser(login.getText());
        crd.setPassword(password.getText());
        crd.setRemember(check.isSelected());
        return crd;
    }

    public void setCredentials(Credentials crd) {
        login.setText(crd.getUser());
        password.setText(crd.getPassword());
        check.setSelected(crd.isRemember());
    }

    public Button getEnter() {
        return enter;
    }

    public void showLoginError() {
        error.setVisible(true);
    }
}
