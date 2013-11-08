package amd.tsino.launcher.auth;

import net.minecraft.launcher.Launcher;

public class Credentials implements Cloneable {
	public String selectedProfile;
	public String password;
	public boolean remember = true;

	@Override
	public Credentials clone() {
		try {
			return (Credentials) super.clone();
		} catch (CloneNotSupportedException e) {
			Launcher.getInstance().getLog().error(e);
			throw new RuntimeException(e);
		}
	}
}
