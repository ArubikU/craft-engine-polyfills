package dev.arubik.craftengine;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonFormatter {

    private static final Gson GSON = new Gson();

    // ================== PRETTY JSON ==================
    public static String prettyFormating(JsonObject json) {
        StringBuilder sb = new StringBuilder("{");

        for (var entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            sb.append(key).append(": ").append(formatValue(value)).append(", ");
        }

        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // quitar última coma
        }
        sb.append("}");
        return sb.toString();
    }

    private static String formatValue(JsonElement value) {
        if (value.isJsonNull()) {
            return "null";
        }

        if (value.isJsonPrimitive()) {
            var primitive = value.getAsJsonPrimitive();

            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? "true" : "false";
            }
            if (primitive.isNumber()) {
                return formatNumber(primitive.getAsString());
            }
            if (primitive.isString()) {
                String raw = primitive.getAsString();
                if ((raw.startsWith("{") && raw.endsWith("}")) ||
                    (raw.startsWith("[") && raw.endsWith("]"))) {
                    try {
                        JsonElement parsed = JsonParser.parseString(raw);
                        if (parsed.isJsonObject()) {
                            return prettyFormating(parsed.getAsJsonObject());
                        } else if (parsed.isJsonArray()) {
                            return formatValue(parsed);
                        }
                    } catch (Exception ignored) {}
                }
                return "\"" + raw + "\"";
            }
        }

        if (value.isJsonArray()) {
            JsonArray arr = value.getAsJsonArray();
            StringBuilder sb = new StringBuilder("[");
            for (JsonElement el : arr) {
                sb.append(formatValue(el)).append(", ");
            }
            if (arr.size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
            return sb.toString();
        }

        if (value.isJsonObject()) {
            return prettyFormating(value.getAsJsonObject());
        }

        return value.toString();
    }

    private static String formatNumber(String raw) {
        try {
            long l = Long.parseLong(raw);
            if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
                return raw + "L";
            }
            int i = Integer.parseInt(raw);
            if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
                return raw + "b";
            } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
                return raw + "s";
            } else {
                return raw; // int
            }
        } catch (NumberFormatException e) {
            try {
                double d = Double.parseDouble(raw);
                if (raw.contains(".")) {
                    if (raw.endsWith("f") || raw.endsWith("F")) {
                        return raw.endsWith("f") ? raw : raw + "f";
                    }
                    return raw + "d";
                }
                return raw;
            } catch (NumberFormatException ex) {
                return raw;
            }
        }
    }

    // ================== MINI MESSAGE ==================
    public static String toMiniMessage(JsonObject json) {
        StringBuilder styled = new StringBuilder("<gray>{</gray> ");

        for (var entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            styled.append("<yellow>").append(key).append("</yellow>")
                  .append("<gray>: </gray>")
                  .append(formatMiniValue(value))
                  .append("<gray>, </gray>");
        }

        if (styled.toString().endsWith("<gray>, </gray>")) {
            styled.setLength(styled.length() - "<gray>, </gray>".length());
        }

        styled.append(" <gray>}</gray>");
        return styled.toString();
    }

    private static String formatMiniValue(JsonElement value) {
        if (value.isJsonNull()) {
            return "<dark_gray>null</dark_gray>";
        }

        if (value.isJsonPrimitive()) {
            var primitive = value.getAsJsonPrimitive();

            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? "<green>true</green>" : "<red>false</red>";
            }
            if (primitive.isNumber()) {
                String formatted = formatNumber(primitive.getAsString());
                if (formatted.endsWith("b")) return "<blue>" + formatted + "</blue>";
                if (formatted.endsWith("s")) return "<aqua>" + formatted + "</aqua>";
                if (formatted.endsWith("L")) return "<gold>" + formatted + "</gold>";
                if (formatted.endsWith("f")) return "<light_purple>" + formatted + "</light_purple>";
                if (formatted.endsWith("d")) return "<dark_aqua>" + formatted + "</dark_aqua>";
                return "<yellow>" + formatted + "</yellow>";
            }
            if (primitive.isString()) {
                String raw = primitive.getAsString();
                if ((raw.startsWith("{") && raw.endsWith("}")) ||
                    (raw.startsWith("[") && raw.endsWith("]"))) {
                    try {
                        JsonElement parsed = JsonParser.parseString(raw);
                        if (parsed.isJsonObject()) {
                            return toMiniMessage(parsed.getAsJsonObject());
                        } else if (parsed.isJsonArray()) {
                            return formatMiniValue(parsed);
                        }
                    } catch (Exception ignored) {}
                }
                return "<white>\"" + raw + "\"</white>";
            }
        }

        if (value.isJsonArray()) {
            JsonArray arr = value.getAsJsonArray();
            StringBuilder sb = new StringBuilder("<gray>[</gray>");
            for (JsonElement el : arr) {
                sb.append(formatMiniValue(el)).append("<gray>, </gray>");
            }
            if (arr.size() > 0) {
                sb.setLength(sb.length() - "<gray>, </gray>".length());
            }
            sb.append("<gray>]</gray>");
            return sb.toString();
        }

        if (value.isJsonObject()) {
            return toMiniMessage(value.getAsJsonObject());
        }

        return "<dark_gray>" + value.toString() + "</dark_gray>";
    }
}
