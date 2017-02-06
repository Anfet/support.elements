package net.anfet.simple.support.library;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.anfet.simple.support.library.utils.Dates;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;

/**
 * Gson поддержки для встраивания нужных классов
 */
public class SupportGson {
	public static final Gson get() {
		return getBuilder().create();
	}

	public static final GsonBuilder getBuilder() {
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(Time.class, new TimeSerializer());
		builder.registerTypeAdapter(Timestamp.class, new TimestampSerializer());
		return builder;
	}

	private static class TimeSerializer implements JsonSerializer<Time>, JsonDeserializer<Time> {

		@Override
		public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return new Time(Dates.HHmmss.parse(json.getAsString()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(Dates.HHmmss.format(src));
		}
	}

	private static class TimestampSerializer implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

		@Override
		public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return new Timestamp(Dates.yyyyMMddHHmmss.parse(json.getAsString()).getTime());
			} catch (ParseException e) {
				return null;
			}
		}

		@Override
		public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(Dates.yyyyMMddHHmmss.format(src));
		}
	}
}
