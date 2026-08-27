package dev.arubik.craftengine.cron;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;

/**
 * Loads every {@code crons/*.json} into {@link CronRegistry}. Mirrors {@code
 * CmdDefinitionLoader}'s single-pass load exactly.
 */
public final class CronDefinitionLoader {

    private CronDefinitionLoader() {}

    public static void load() {
        List<CronDefinition> defs = new ArrayList<>();
        DataFiles.loadDirectory("crons", (view, name) -> defs.add(parseCron(view, name)));
        CronRegistry.setDefinitions(defs);
    }

    private static CronDefinition parseCron(JsonView view, String name) {
        String id = view.string("id", name);
        String cron = view.has("cron") ? view.string("cron") : null;
        int everySeconds = view.integer("every_seconds", 0);
        String script = view.string("script", null);
        boolean enabled = view.bool("enabled", true);

        if (script == null || script.isBlank())
            throw view.error("job '" + id + "' requires a \"script\" field");

        boolean hasCron = cron != null && !cron.isBlank();
        boolean hasEvery = everySeconds > 0;
        if (hasCron && hasEvery)
            throw view.error("job '" + id + "' must declare only one of \"cron\" or \"every_seconds\", not both");
        if (!hasCron && !hasEvery)
            throw view.error("job '" + id + "' must declare one of \"cron\" or \"every_seconds\"");

        return new CronDefinition(id, hasCron ? cron : null, hasEvery ? everySeconds : 0, script, enabled);
    }
}
