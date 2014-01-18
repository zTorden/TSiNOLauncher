package amd.tsino.launcher.auth;

import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import net.minecraft.launcher.Launcher;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationData {
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final byte[] SALT = new byte[]{2, 3, 5, 7, 11, 13, 17, 19};
    private static final int PBE_ITERATIONS = 7;

    private static Cipher getCipher(int mode, String password) throws Exception {
        PBEParameterSpec paramSpec = new PBEParameterSpec(SALT, PBE_ITERATIONS);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(
                keySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key, paramSpec);
        return cipher;
    }

    public static String decrypt(String password, String user) {
        if (password != null) {
            try {
                byte[] data = Base64.decodeBase64(password);
                Cipher cipher = getCipher(Cipher.DECRYPT_MODE, user);
                data = cipher.doFinal(data);
                return new String(data, LauncherConstants.DEFAULT_CHARSET);
            } catch (Exception ex) {
                Launcher.getInstance().getLog().error(ex);
            }
        }
        return null;
    }

    public static String encrypt(String password, String user) {
        if (password != null) {
            try {
                Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, user);
                byte[] data = password.getBytes(LauncherConstants.DEFAULT_CHARSET);
                data = cipher.doFinal(data);
                return Base64.encodeBase64String(data);
            } catch (Exception ex) {
                Launcher.getInstance().getLog().error(ex);
            }
        }
        return null;
    }

    public static String requestSessionID() throws AuthenticationException {
        Credentials crd = Launcher.getInstance().getSettings().getCredentials();
        Map<String, Object> query = new HashMap<>();
        query.put("user", crd.getUser());
        query.put("password", crd.getPassword());
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
