package com.example.prif233.Utils;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter
        implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT,
                                 com.google.gson.JsonDeserializationContext context)
            throws JsonParseException {
        return LocalTime.parse(json.getAsString(), FORMATTER);
    }

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc,
                                 com.google.gson.JsonSerializationContext context) {
        return new JsonPrimitive(FORMATTER.format(src));
    }
}