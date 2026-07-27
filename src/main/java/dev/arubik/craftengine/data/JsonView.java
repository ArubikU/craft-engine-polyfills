package dev.arubik.craftengine.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.momirealms.craftengine.core.util.Key;

/**
 * A thin, path-aware reader over a Gson {@link JsonObject}.
 *
 * <p>
 * Every loader in this plugin used to hand-roll {@code has(x) ? get(x).getAsInt()
 * : default} chains, which meant a typo in a data file surfaced as a
 * {@code NullPointerException} with no indication of which file or field was at
 * fault. {@code JsonView} carries a breadcrumb path ({@code "crusher.json >
 * outputs[1] > id"}) into every error instead.
 *
 * <p>
 * Naming convention: {@code xxx(field)} is required and throws if absent;
 * {@code xxx(field, fallback)} is optional.
 */
public final class JsonView {

    /** Failure to read a data file, carrying the field path that broke. */
    public static class MalformedDataException extends RuntimeException {
        public MalformedDataException(String message) {
            super(message);
        }
    }

    private final JsonObject json;
    private final String path;

    private JsonView(JsonObject json, String path) {
        this.json = json;
        this.path = path;
    }

    public static JsonView of(JsonObject json, String path) {
        if (json == null)
            throw new MalformedDataException(path + ": expected a JSON object, found nothing");
        return new JsonView(json, path);
    }

    /** The breadcrumb path of this object, for error messages. */
    public String path() {
        return path;
    }

    /** The backing object, for the rare case a caller needs raw Gson access. */
    public JsonObject raw() {
        return json;
    }

    public boolean has(String field) {
        return json.has(field) && !json.get(field).isJsonNull();
    }

    /** Field names actually present, useful for "unknown key" validation. */
    public List<String> fields() {
        return new ArrayList<>(json.keySet());
    }

    // --------------------------------------------------------------- scalars

    public String string(String field) {
        return primitive(field).getAsString();
    }

    public String string(String field, String fallback) {
        return has(field) ? string(field) : fallback;
    }

    public int integer(String field) {
        return asInt(primitive(field), field);
    }

    public int integer(String field, int fallback) {
        return has(field) ? integer(field) : fallback;
    }

    public long longValue(String field, long fallback) {
        return has(field) ? primitive(field).getAsLong() : fallback;
    }

    public float floating(String field) {
        return primitive(field).getAsFloat();
    }

    public float floating(String field, float fallback) {
        return has(field) ? floating(field) : fallback;
    }

    public double decimal(String field, double fallback) {
        return has(field) ? primitive(field).getAsDouble() : fallback;
    }

    public boolean bool(String field, boolean fallback) {
        return has(field) ? primitive(field).getAsBoolean() : fallback;
    }

    /** Reads an int and rejects values outside {@code [min, max]}. */
    public int rangedInt(String field, int fallback, int min, int max) {
        int value = integer(field, fallback);
        if (value < min || value > max)
            throw new MalformedDataException(
                    child(field) + ": expected a value in [" + min + ", " + max + "], found " + value);
        return value;
    }

    /** Reads a double and rejects values outside {@code [min, max]}. */
    public double rangedDouble(String field, double fallback, double min, double max) {
        double value = decimal(field, fallback);
        if (value < min || value > max)
            throw new MalformedDataException(
                    child(field) + ": expected a value in [" + min + ", " + max + "], found " + value);
        return value;
    }

    // ------------------------------------------------------------------ keys

    /**
     * Reads a namespaced id. A bare {@code "iron_ingot"} is resolved against
     * {@code defaultNamespace}.
     */
    public Key key(String field, String defaultNamespace) {
        return parseKey(string(field), defaultNamespace, child(field));
    }

    public Key key(String field, String defaultNamespace, Key fallback) {
        return has(field) ? key(field, defaultNamespace) : fallback;
    }

    /** Shared id parser so every loader agrees on what a bare id means. */
    public static Key parseKey(String raw, String defaultNamespace, String where) {
        if (raw == null || raw.isBlank())
            throw new MalformedDataException(where + ": expected a namespaced id, found an empty string");
        String value = raw.trim();
        int colon = value.indexOf(':');
        if (colon < 0)
            return Key.of(defaultNamespace, value);
        if (colon == 0 || colon == value.length() - 1)
            throw new MalformedDataException(where + ": malformed namespaced id '" + raw + "'");
        return Key.of(value.substring(0, colon), value.substring(colon + 1));
    }

    // ----------------------------------------------------------------- enums

    /**
     * Reads an enum constant case-insensitively, listing the valid values when the
     * lookup fails.
     */
    public <E extends Enum<E>> E enumValue(String field, Class<E> type, E fallback) {
        if (!has(field))
            return fallback;
        String raw = string(field);
        for (E constant : type.getEnumConstants())
            if (constant.name().equalsIgnoreCase(raw))
                return constant;
        throw new MalformedDataException(child(field) + ": unknown value '" + raw + "'; expected one of "
                + java.util.Arrays.stream(type.getEnumConstants()).map(e -> e.name().toLowerCase(Locale.ROOT)).toList());
    }

    public <E extends Enum<E>> E enumValue(String field, Class<E> type) {
        E value = enumValue(field, type, null);
        if (value == null)
            throw new MalformedDataException(child(field) + ": required field is missing");
        return value;
    }

    // --------------------------------------------------------------- nesting

    public JsonView object(String field) {
        JsonElement element = require(field);
        if (!element.isJsonObject())
            throw new MalformedDataException(child(field) + ": expected an object, found " + describe(element));
        return new JsonView(element.getAsJsonObject(), child(field));
    }

    public Optional<JsonView> optionalObject(String field) {
        return has(field) ? Optional.of(object(field)) : Optional.empty();
    }

    /**
     * Reads an array of objects. Missing means empty, so optional lists need no
     * {@code has} guard at the call site.
     */
    public List<JsonView> objectList(String field) {
        if (!has(field))
            return List.of();
        JsonElement element = json.get(field);
        if (!element.isJsonArray())
            throw new MalformedDataException(child(field) + ": expected an array, found " + describe(element));
        JsonArray array = element.getAsJsonArray();
        List<JsonView> out = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            JsonElement item = array.get(i);
            String itemPath = path + " > " + field + "[" + i + "]";
            if (!item.isJsonObject())
                throw new MalformedDataException(itemPath + ": expected an object, found " + describe(item));
            out.add(new JsonView(item.getAsJsonObject(), itemPath));
        }
        return out;
    }

    /** Reads an array of strings. Missing means empty. */
    public List<String> stringList(String field) {
        if (!has(field))
            return List.of();
        JsonElement element = json.get(field);
        if (element.isJsonPrimitive())
            return List.of(element.getAsString());
        if (!element.isJsonArray())
            throw new MalformedDataException(child(field) + ": expected an array of strings");
        List<String> out = new ArrayList<>();
        for (JsonElement item : element.getAsJsonArray())
            out.add(item.getAsString());
        return out;
    }

    /** Reads an array of ints. Missing means empty. */
    public List<Integer> intList(String field) {
        if (!has(field))
            return List.of();
        JsonElement element = json.get(field);
        if (element.isJsonPrimitive())
            return List.of(asInt(element.getAsJsonPrimitive(), field));
        if (!element.isJsonArray())
            throw new MalformedDataException(child(field) + ": expected an array of integers");
        List<Integer> out = new ArrayList<>();
        for (JsonElement item : element.getAsJsonArray())
            out.add(asInt(item.getAsJsonPrimitive(), field));
        return out;
    }

    /**
     * Reads a free-form {@code {name: value}} map as views, e.g. the {@code bars}
     * or {@code keys} sections.
     */
    public List<java.util.Map.Entry<String, JsonView>> objectMap(String field) {
        if (!has(field))
            return List.of();
        JsonView section = object(field);
        List<java.util.Map.Entry<String, JsonView>> out = new ArrayList<>();
        for (String name : section.json.keySet())
            out.add(java.util.Map.entry(name, section.object(name)));
        return out;
    }

    /** Reads a free-form {@code {name: "string"}} map, e.g. a recipe key legend. */
    public List<java.util.Map.Entry<String, String>> stringMap(String field) {
        if (!has(field))
            return List.of();
        JsonView section = object(field);
        List<java.util.Map.Entry<String, String>> out = new ArrayList<>();
        for (String name : section.json.keySet())
            out.add(java.util.Map.entry(name, section.string(name)));
        return out;
    }

    // ----------------------------------------------------------------- errors

    /** Fails with this view's path prefixed, for domain checks the reader can't do. */
    public MalformedDataException error(String message) {
        return new MalformedDataException(path + ": " + message);
    }

    private String child(String field) {
        return path + " > " + field;
    }

    private JsonElement require(String field) {
        if (!has(field))
            throw new MalformedDataException(child(field) + ": required field is missing");
        return json.get(field);
    }

    private JsonPrimitive primitive(String field) {
        JsonElement element = require(field);
        if (!element.isJsonPrimitive())
            throw new MalformedDataException(child(field) + ": expected a value, found " + describe(element));
        return element.getAsJsonPrimitive();
    }

    private int asInt(JsonPrimitive primitive, String field) {
        double value = primitive.getAsDouble();
        if (value != Math.rint(value))
            throw new MalformedDataException(child(field) + ": expected an integer, found " + value);
        return (int) value;
    }

    private static String describe(JsonElement element) {
        if (element.isJsonArray())
            return "an array";
        if (element.isJsonObject())
            return "an object";
        if (element.isJsonNull())
            return "null";
        return "'" + element.getAsString() + "'";
    }
}
