package net.minecraft.launcher.updater;

import java.io.File;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class FileTypeAdapter extends TypeAdapter<File> {
	@Override
	public File read(JsonReader in) throws IOException {
		if (in.hasNext()) {
			String name = in.nextString();
			return name != null ? new File(name) : null;
		}

		return null;
	}

	@Override
	public void write(JsonWriter out, File value) throws IOException {
		if (value == null)
			out.nullValue();
		else
			out.value(value.getAbsolutePath());
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.FileTypeAdapter JD-Core Version: 0.6.2
 */