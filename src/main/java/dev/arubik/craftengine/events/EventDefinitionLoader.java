package dev.arubik.craftengine.events;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;

/**
 * Loads every {@code events/*.json} into {@link GenericEventBridge}. Mirrors {@code
 * CmdDefinitionLoader}'s shape — one flat pass, no cross-registry ordering dependency.
 */
public final class EventDefinitionLoader {

    private EventDefinitionLoader() {}

    public static void load() {
        List<EventDefinition> defs = new ArrayList<>();
        DataFiles.loadDirectory("events", (view, name) -> defs.add(parseEvent(view, name)));
        GenericEventBridge.setDefinitions(defs);
    }

    private static EventDefinition parseEvent(JsonView view, String name) {
        String id = view.string("id", name);
        if (!view.has("event") || view.string("event").isBlank()) {
            throw view.error("\"event\" is required — the fully-qualified Bukkit/Paper event class to hook");
        }
        String eventClass = view.string("event");
        String priority = view.string("priority", "NORMAL");
        boolean ignoreCancelled = view.bool("ignore_cancelled", false);
        if (!view.has("script") || view.string("script").isBlank()) {
            throw view.error("\"script\" is required — the .pf script ref to call when " + eventClass + " fires");
        }
        String script = view.string("script");
        return new EventDefinition(id, eventClass, priority, ignoreCancelled, script);
    }
}
