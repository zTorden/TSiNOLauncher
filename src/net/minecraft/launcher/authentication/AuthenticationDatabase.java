package net.minecraft.launcher.authentication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launcher.authentication.yggdrasil.YggdrasilAuthenticationService;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class AuthenticationDatabase {
	public static final String DEMO_UUID_PREFIX = "demo-";
	private final Map<String, AuthenticationService> authById;

	public AuthenticationDatabase() {
		this(new HashMap<String, AuthenticationService>());
	}

	public AuthenticationDatabase(Map<String, AuthenticationService> authById) {
		this.authById = authById;
	}

	public AuthenticationService getByName(String name) {
		if (name == null)
			return null;

		for (Map.Entry<String, AuthenticationService> entry : this.authById
				.entrySet()) {
			GameProfile profile = ((AuthenticationService) entry.getValue())
					.getSelectedProfile();

			if ((profile != null) && (profile.getName().equals(name)))
				return (AuthenticationService) entry.getValue();
			if ((profile == null)
					&& (getUserFromDemoUUID((String) entry.getKey())
							.equals(name))) {
				return (AuthenticationService) entry.getValue();
			}
		}

		return null;
	}

	public AuthenticationService getByUUID(String uuid) {
		return (AuthenticationService) this.authById.get(uuid);
	}

	public Collection<String> getKnownNames() {
		List<String> names = new ArrayList<String>();

		for (Map.Entry<String, AuthenticationService> entry : this.authById
				.entrySet()) {
			GameProfile profile = ((AuthenticationService) entry.getValue())
					.getSelectedProfile();

			if (profile != null)
				names.add(profile.getName());
			else {
				names.add(getUserFromDemoUUID((String) entry.getKey()));
			}
		}

		return names;
	}

	public void register(String uuid, AuthenticationService authentication) {
		this.authById.put(uuid, authentication);
	}

	public Set<String> getknownUUIDs() {
		return this.authById.keySet();
	}

	public void removeUUID(String uuid) {
		this.authById.remove(uuid);
	}

	public static String getUserFromDemoUUID(String uuid) {
		if ((uuid.startsWith("demo-")) && (uuid.length() > "demo-".length())) {
			return "Demo User " + uuid.substring("demo-".length());
		}
		return "Demo User";
	}

	public static class Serializer implements
			JsonDeserializer<AuthenticationDatabase>,
			JsonSerializer<AuthenticationDatabase> {
		@SuppressWarnings("unchecked")
		public AuthenticationDatabase deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			TypeToken<Map<String, Map<String, String>>> token = new TypeToken<Map<String, Map<String, String>>>() {

			};
			Map<String, AuthenticationService> services = new HashMap<String, AuthenticationService>();
			Map<String, Map<String, String>> credentials = (Map<String, Map<String, String>>) context
					.deserialize(json, token.getType());

			for (Map.Entry<String, Map<String, String>> entry : credentials
					.entrySet()) {
				AuthenticationService service = new YggdrasilAuthenticationService();
				service.loadFromStorage((Map<String, String>) entry.getValue());
				services.put(entry.getKey(), service);
			}

			return new AuthenticationDatabase(services);
		}

		public JsonElement serialize(AuthenticationDatabase src,
				Type typeOfSrc, JsonSerializationContext context) {
			Map<String, AuthenticationService> services = src.authById;
			Map<String, Map<String, String>> credentials = new HashMap<String, Map<String, String>>();

			for (Map.Entry<String, AuthenticationService> entry : services
					.entrySet()) {
				credentials.put(entry.getKey(), ((AuthenticationService) entry
						.getValue()).saveForStorage());
			}

			return context.serialize(credentials);
		}

	}

}
