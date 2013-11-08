package amd.tsino.launcher.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.minecraft.launcher.Launcher;
import amd.tsino.launcher.auth.AuthenticationData;
import amd.tsino.launcher.auth.Credentials;
import amd.tsino.launcher.style.AuthPanelStyle;

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

		Credentials crd = Launcher.getInstance().getAuth().getCredentials();

		login.setText(crd.selectedProfile);
		password.setText(crd.password);
		check.setSelected(crd.remember);

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

		enter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AuthenticationData auth = Launcher.getInstance().getAuth();
				auth.setCredentials(getCredentials());
				try {
					auth.save();
				} catch (IOException e) {
					Launcher.getInstance().getLog().error(e);
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	public Credentials getCredentials() {
		Credentials crd = new Credentials();
		crd.selectedProfile = login.getText();
		crd.password = password.getText();
		crd.remember = check.isSelected();
		return crd;
	}

	public TextField getLogin() {
		return login;
	}

	public TextField getPassword() {
		return password;
	}

	public ImagePanel getError() {
		return error;
	}

	public Button getEnter() {
		return enter;
	}

	public Button getRegister() {
		return register;
	}

	public CheckButton getCheck() {
		return check;
	}
}
