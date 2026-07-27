package dev.arubik.craftengine.machine.menu.bar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code bars/*.json} into {@link BarDefinition#REGISTRY}.
 *
 * <p>
 * The body is the same shape the block config already used for a bar, so an
 * existing gauge moves here by copy-paste:
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:fluid",
 *   "model": "column",
 *   "family": "water",
 *   "name": "lang:polyfill.ui.fluid",
 *   "generate": {                    // build levels from the fluid registry
 *     "source": "fluid",
 *     "item": "cml:%family%_%suffix%_%level%",
 *     "lore": ["%value%/%max% mB", "%percent%%"],
 *     "parts": [ { "suffix": "bottom", "levels": 16 },
 *                { "suffix": "midbot", "levels": 18 } ]
 *   }
 * }
 * }</pre>
 *
 * <p>
 * A definition never declares slots: where a gauge sits belongs to the machine
 * showing it, so {@code machines/*.json} supplies that.
 */
public final class BarLoader {

    private BarLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        // PHASE_DEFINITIONS: a generated bar reads the fluid/gas registries, which are
        // populated in PHASE_TYPES.
        Registries.addLoader("bars", Registries.PHASE_DEFINITIONS, BarLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("bars", BarLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + BarDefinition.REGISTRY.size() + " bar definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName));

        // Reuse the existing bar parser rather than duplicating it: convert the JSON to
        // the plain map/list form it already understands and let it do the work,
        // including the registry-driven `generate` block. A placeholder slot is added
        // because that parser skips a bar with none — the real slots come from the
        // machine, which is why a definition does not declare any.
        Object body = toPlain(view.raw());
        if (body instanceof Map<?, ?> raw) {
            Map<String, Object> withSlot = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet())
                withSlot.put(String.valueOf(e.getKey()), e.getValue());
            withSlot.put("slots", List.of(0L));
            body = withSlot;
        }
        List<MachineBar> parsed = MachineBars.parse(Map.of(id.value(), body));
        if (parsed.isEmpty())
            throw view.error("declares no states, segments or generate block");

        MachineBar bar = parsed.get(0);
        BarDefinition.REGISTRY.register(id, new BarDefinition(id, bar.model, bar.family, bar.name,
                bar.states, bar.segments));
    }

    /** Gson tree -> plain maps/lists/numbers, which is what the bar parser reads. */
    private static Object toPlain(JsonElement element) {
        if (element == null || element.isJsonNull())
            return null;
        if (element.isJsonObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> e : ((JsonObject) element).entrySet())
                map.put(e.getKey(), toPlain(e.getValue()));
            return map;
        }
        if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonElement e : (JsonArray) element)
                list.add(toPlain(e));
            return list;
        }
        var primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean())
            return primitive.getAsBoolean();
        if (primitive.isNumber())
            return primitive.getAsDouble() == Math.rint(primitive.getAsDouble())
                    ? (Object) primitive.getAsLong()
                    : (Object) primitive.getAsDouble();
        return primitive.getAsString();
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
