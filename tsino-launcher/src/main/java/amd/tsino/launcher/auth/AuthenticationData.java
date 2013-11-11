package amd.tsino.launcher.auth;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.launcher.Launcher;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationData {
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final byte[] SALT = new byte[]{2, 3, 5, 7, 11, 13, 17, 19};
    private static final int PBE_ITERATIONS = 7;
    private Credentials credentials;

    public AuthenticationData() {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(getFile()),
                    LauncherConstants.DEFAULT_CHARSET);
            final Gson gson = new Gson();
            Credentials crd = gson.fromJson(reader, Credentials.class);
            reader.close();
            credentials = decrypt(crd);
            return;
        } catch (FileNotFoundException e) {
            Launcher.getInstance().getLog().log("No authentication data found.");
        } catch (Exception e) {
            Launcher.getInstance().getLog().error(e);
        }
        credentials = new Credentials();
    }

    private static Cipher getCipher(int mode, String password) throws Exception {
        PBEParameterSpec paramSpec = new PBEParameterSpec(SALT, PBE_ITERATIONS);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(
                keySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key, paramSpec);
        return cipher;
    }

    private static Credentials decrypt(Credentials crd) {
        if (crd.getPassword() != null) {
            try {
                byte[] data = Base64.decodeBase64(crd.getPassword());
                Cipher cipher = getCipher(Cipher.DECRYPT_MODE, crd.getUser());
                data = cipher.doFinal(data);
                return new Credentials(crd.getUser(), new String(data, LauncherConstants.DEFAULT_CHARSET), crd.isRemember());
            } catch (Exception ex) {
                Launcher.getInstance().getLog().error(ex);
            }
        }
        return new Credentials(crd.getUser(), null, crd.isRemember());
    }

    private static Credentials encrypt(Credentials crd) {
        if (crd.isRemember()) {
            try {
                Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, crd.getUser());
                byte[] data = crd.getPassword().getBytes(LauncherConstants.DEFAULT_CHARSET);
                data = cipher.doFinal(data);
                return new Credentials(crd.getUser(), Base64.encodeBase64String(data), crd.isRemember());
            } catch (Exception ex) {
                Launcher.getInstance().getLog().error(ex);
            }
        }
        return new Credentials(crd.getUser(), null, crd.isRemember());
    }

    private static File getFile() {
        return LauncherUtils.getFile(LauncherConstants.AUTH_JSON);
    }

    public void save() throws IOException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new OutputStreamWriter(new FileOutputStream(getFile()), LauncherConstants.DEFAULT_CHARSET);
        Credentials crd = encrypt(credentials);
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
        Map<String, Object> query = new HashMap<>();
        query.put("user", credentials.getUser());
        query.put("password", credentials.getPassword());
        query.put("version", LauncherConstants.VERSION_NUMERIC);

        String result;
        try {
            result = LauncherUtils.performPost(LauncherConstants.AUTH_URL, query, Launcher
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
