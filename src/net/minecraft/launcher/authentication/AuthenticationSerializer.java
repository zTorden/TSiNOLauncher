package net.minecraft.launcher.authentication;

import java.lang.reflect.Type;
import java.util.Map;

import net.minecraft.launcher.authentication.yggdrasil.YggdrasilAuthenticationService;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AuthenticationSerializer implements
		JsonDeserializer<AuthenticationService>,
		JsonSerializer<AuthenticationService> {
	public AuthenticationService deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		AuthenticationService result = new YggdrasilAuthenticationService();
		if (json == null)
			return result;
		Map<String, String> map = (Map<String, String>) context.deserialize(json, Map.class);
		result.loadFromStorage(map);
		return result;
	}

	public JsonElement serialize(AuthenticationService src, Type typeOfSrc,
			JsonSerializationContext context) {
		Map<?, ?> map = src.saveForStorage();
		if ((map == null) || (map.isEmpty()))
			return null;

		return context.serialize(map);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.AuthenticationSerializer JD-Core
 * Version: 0.6.2
 */