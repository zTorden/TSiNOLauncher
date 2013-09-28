package net.minecraft.launcher.ui.popups.login;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AuthErrorForm extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6384686603820013233L;
	private final LogInPopup popup;
	private final JLabel errorLabel = new JLabel();

	public AuthErrorForm(LogInPopup popup) {
		this.popup = popup;

		createInterface();
		clear();
	}

	public void clear() {
		setVisible(false);
	}

	protected void createInterface() {
		setBorder(new EmptyBorder(0, 0, 15, 0));
		this.errorLabel.setFont(this.errorLabel.getFont().deriveFont(1));
		add(this.errorLabel);
	}

	public void displayError(final String[] lines) {
		if (SwingUtilities.isEventDispatchThread()) {
			String error = "";
			for (String line : lines) {
				error = error + "<p>" + line + "</p>";
			}
			this.errorLabel.setText("<html><div style='text-align: center;'>"
					+ error + " </div></html>");
			setVisible(true);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					AuthErrorForm.this.displayError(lines);
				}
			});
		}
	}

	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);
		this.popup.repack();
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.popups.login.AuthErrorForm JD-Core Version: 0.6.2
 */