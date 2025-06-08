package com.grabdriver.myapplication.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    
    private final SimpleDateFormat[] dateFormats = {
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    };

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return new JsonPrimitive(dateFormats[0].format(src));
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String dateStr = json.getAsString();
        
        // Try each date format until one works
        for (SimpleDateFormat format : dateFormats) {
            try {
                synchronized (format) { // SimpleDateFormat is not thread-safe
                    return format.parse(dateStr);
                }
            } catch (ParseException e) {
                // Continue to next format
            }
        }
        
        // If no format worked, throw exception
        throw new JsonParseException("Unable to parse date: " + dateStr);
    }
} 