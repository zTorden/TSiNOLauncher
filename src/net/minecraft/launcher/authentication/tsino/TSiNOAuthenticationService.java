package net.minecraft.launcher.authentication.tsino;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.launcher.Http;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.LauncherConstants;
import net.minecraft.launcher.authentication.BaseAuthenticationService;
import net.minecraft.launcher.authentication.GameProfile;
import net.minecraft.launcher.authentication.exceptions.AuthenticationException;
import net.minecraft.launcher.authentication.exceptions.InvalidCredentialsException;
import net.minecraft.launcher.authentication.exceptions.UpdateLauncherException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class TSiNOAuthenticationService extends BaseAuthenticationService {
	private static final String BASE_URL = "http://tsino.unet.by/minecraft/auth.php";
	private static final String STORAGE_KEY_PASSWORD = "password";
	private GameProfile[] profiles;
	private String accessToken;
	private boolean isOnline;

	@Override
	public boolean canLogIn() {
		return (!canPlayOnline()) && (StringUtils.isNotBlank(getUsername()))
				&& ((StringUtils.isNotBlank(getPassword())));
	}

	@Override
	public boolean canPlayOnline() {
		return (isLoggedIn()) && (getSelectedProfile() != null)
				&& (this.isOnline);
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	@Override
	public GameProfile[] getAvailableProfiles() {
		return this.profiles;
	}

	public String getClientToken() {
		return Launcher.getInstance().getClientToken().toString();
	}

	@Override
	public String getSessionToken() {
		if ((isLoggedIn()) && (getSelectedProfile() != null)
				&& (canPlayOnline())) {
			return getAccessToken();
		}
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		return StringUtils.isNotBlank(this.accessToken);
	}

	@Override
	public void loadFromStorage(Map<String, String> credentials) {
		super.loadFromStorage(credentials);
		setPassword(credentials.get(STORAGE_KEY_PASSWORD));
	}

	@Override
	public void logIn() throws AuthenticationException {
		if (StringUtils.isBlank(getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}

		if (StringUtils.isNotBlank(getPassword()))
			logInWithPassword();
		else
			throw new InvalidCredentialsException("Invalid password");
	}

	protected void logInWithPassword() throws AuthenticationException {
		setSelectedProfile(null);

		if (StringUtils.isBlank(getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}
		if (StringUtils.isBlank(getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		Launcher.getInstance().println("Logging in with username & password");

		String[] values = makeRequest().split(":");

		this.setUsername(values[2].trim());
		this.accessToken = values[3].trim();
		this.isOnline = true;

		this.profiles = new GameProfile[1];
		this.profiles[0] = new GameProfile(getUsername().toLowerCase(),
				getUsername());
		setSelectedProfile(profiles[0]);

		fireAuthenticationChangedEvent();
	}

	@Override
	public void logOut() {
		super.logOut();

		this.accessToken = null;
		this.profiles = null;
		this.isOnline = false;
	}

	private String makeRequest() throws AuthenticationException {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("user", getUsername());
		query.put("password", getPassword());
		query.put("version", LauncherConstants.VERSION_NUMERIC);

		String result;
		try {
			URL url = new URL(BASE_URL);
			result = Http.performPost(url, query, Launcher.getInstance()
					.getProxy());
		} catch (Exception ex) {
			throw new AuthenticationException(ex.toString());
		}

		if (!result.contains(":")) {
			if (result.trim().equals("Bad login")) {
				throw new InvalidCredentialsException(
						"Неправильный логин или пароль!");
			} else if (result.trim().equals("Old version")) {
				throw new UpdateLauncherException("Нужно обновить лаунчер!");
			}
			throw new AuthenticationException(result);
		}
		return result;
	}

	@Override
	public Map<String, String> saveForStorage() {
		Map<String, String> result = super.saveForStorage();
		if (!shouldRememberMe())
			return result;

		if (StringUtils.isNotBlank(getPassword())) {
			result.put(STORAGE_KEY_PASSWORD, getPassword());
		}

		return result;
	}

	@Override
	public void selectGameProfile(GameProfile profile)
			throws AuthenticationException {
		if (!isLoggedIn()) {
			throw new AuthenticationException(
					"Cannot change game profile whilst not logged in");
		}
		if (getSelectedProfile() != null) {
			throw new AuthenticationException(
					"Cannot change game profile. You must log out and back in.");
		}
		if ((profile == null) || (!ArrayUtils.contains(this.profiles, profile))) {
			throw new IllegalArgumentException("Invalid profile '" + profile
					+ "'");
		}

		String[] values = makeRequest().split(":");

		this.accessToken = values[3].trim();
		setSelectedProfile(profiles[0]);

		fireAuthenticationChangedEvent();
	}

	@Override
	public String toString() {
		return "TSiNOAuthenticationService{profiles="
				+ Arrays.toString(this.profiles) + ", selectedProfile="
				+ getSelectedProfile() + ", sessionToken='" + getSessionToken()
				+ '\'' + ", username='" + getUsername() + '\''
				+ ", isLoggedIn=" + isLoggedIn() + ", canPlayOnline="
				+ canPlayOnline() + ", accessToken='" + this.accessToken + '\''
				+ ", clientToken='" + getClientToken() + '\'' + '}';
	}
}
