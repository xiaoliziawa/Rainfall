package com.lirxowo.rainfall.internal.rule.parse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class JsonSchemaValidator {

    public static void validate(JsonElement element, Type type, String path) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        Class<?> rawType = TypeToken.get(type).getRawType();
        if (rawType.isArray()) {
            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array at " + path);
            }
            for (int index = 0; index < element.getAsJsonArray().size(); index++) {
                validate(element.getAsJsonArray().get(index), rawType.getComponentType(), path + '[' + index + ']');
            }
            return;
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array at " + path);
            }
            Type itemType = type instanceof ParameterizedType parameterized ? parameterized.getActualTypeArguments()[0] : Object.class;
            for (int index = 0; index < element.getAsJsonArray().size(); index++) {
                validate(element.getAsJsonArray().get(index), itemType, path + '[' + index + ']');
            }
            return;
        }
        if (rawType.isPrimitive() || rawType.isEnum() || rawType == String.class || Number.class.isAssignableFrom(rawType)
                || Map.class.isAssignableFrom(rawType) || rawType == Object.class) {
            return;
        }
        if (!element.isJsonObject()) {
            throw new JsonParseException("Expected object at " + path);
        }
        JsonObject object = element.getAsJsonObject();
        Map<String, Field> fields = Arrays.stream(rawType.getFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .collect(Collectors.toMap(Field::getName, field -> field));
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            Field field = fields.get(entry.getKey());
            if (field == null) {
                throw new JsonParseException("Unknown property " + path + '.' + entry.getKey());
            }
            validate(entry.getValue(), field.getGenericType(), path + '.' + entry.getKey());
        }
    }

    private JsonSchemaValidator() {
    }
}
