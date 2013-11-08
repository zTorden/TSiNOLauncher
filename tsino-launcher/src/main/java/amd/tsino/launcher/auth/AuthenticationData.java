package amd.tsino.launcher.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import net.minecraft.launcher.Launcher;

import org.apache.commons.codec.binary.Base64;

import amd.tsino.launcher.LaucherUtils;
import amd.tsino.launcher.LauncherConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuthenticationData {
	private static final String AUTH_JSON = "launcher_profiles.json";
	private static final Charset AUTH_CHARSET = Charset.forName("utf-8");
	private static final String ALGORITHM = "PBEWithMD5AndDES";
	private static final int SALT_SEED = 0x23571113;
	private static final int PBE_ITERATIONS = 7;
	private Credentials credentials;

	public AuthenticationData() throws IOException {
		Reader reader = new InputStreamReader(new FileInputStream(getFile()),
				AUTH_CHARSET);
		final Gson gson = new Gson();
		Credentials crd = gson.fromJson(reader, Credentials.class);
		if (crd.password != null) {
			decrypt(crd);
		}
		reader.close();
		credentials = crd.clone();
	}

	private static Cipher getCipher(int mode, String password) throws Exception {
		byte[] salt = new byte[8];
		new Random(SALT_SEED).nextBytes(salt);
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, PBE_ITERATIONS);
		KeySpec keySpec = new PBEKeySpec(password.toCharArray());
		SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(
				keySpec);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(mode, key, paramSpec);
		return cipher;
	}

	private static void decrypt(Credentials crd) {
		try {
			byte[] data = Base64.decodeBase64(crd.password);
			Cipher cipher = getCipher(Cipher.DECRYPT_MODE, crd.selectedProfile);
			data = cipher.doFinal(data);
			crd.password = new String(data, AUTH_CHARSET);
		} catch (Exception ex) {
			crd.password = null;
			Launcher.getInstance().getLog().error(ex);
		}
	}

	private static void encrypt(Credentials crd) {
		try {
			Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, crd.selectedProfile);
			byte[] data = crd.password.getBytes(AUTH_CHARSET);
			data = cipher.doFinal(data);
			crd.password = Base64.encodeBase64String(data);
		} catch (Exception ex) {
			crd.password = null;
			Launcher.getInstance().getLog().error(ex);
		}
	}

	private static File getFile() {
		return new File(Launcher.getInstance().getWorkDir(), AUTH_JSON);
	}

	public void save() throws IOException {
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer = new OutputStreamWriter(new FileOutputStream(getFile()));
		Credentials crd = credentials.clone();
		if (crd.remember) {
			encrypt(crd);
		} else {
			crd.password = null;
		}
		writer.write(gson.toJson(crd));
		writer.close();
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String requestSessionID() throws AuthenticationException {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("user", credentials.selectedProfile);
		query.put("password", credentials.password);
		query.put("version", LauncherConstants.VERSION_NUMERIC);

		String result;
		try {
			URL url = new URL(LauncherConstants.AUTH_URL);
			result = LaucherUtils.performPost(url, query, Launcher
					.getInstance().getProxy());
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

		String[] values = result.split(":");
		return values[3].trim();
	}
}
