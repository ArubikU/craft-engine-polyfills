package dev.arubik.craftengine.cron;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import dev.arubik.craftengine.CraftEnginePolyfills;

/**
 * Holds every loaded {@link CronDefinition} plus the pre-parsed {@link CronExpression} for each
 * cron-driven job — parsing once at load time rather than re-parsing the expression string on
 * every tick. Mirrors {@code CmdRegistry}'s {@code volatile} static field pattern.
 */
public final class CronRegistry {

    /** A cron-driven definition paired with its already-parsed expression. */
    public record ParsedCron(CronDefinition definition, CronExpression expression) {}

    private static volatile List<CronDefinition> DEFINITIONS = List.of();
    private static volatile List<ParsedCron> PARSED_CRONS = List.of();

    private CronRegistry() {}

    /**
     * Replaces the loaded set. Cron-expression jobs that fail to parse are logged and skipped
     * (their own bad syntax doesn't stop the rest of the cron subsystem from loading).
     */
    public static void setDefinitions(List<CronDefinition> defs) {
        DEFINITIONS = List.copyOf(defs);
        List<ParsedCron> parsed = new ArrayList<>();
        for (CronDefinition def : defs) {
            if (def.cron() == null) continue;
            try {
                parsed.add(new ParsedCron(def, CronExpression.parse(def.cron())));
            } catch (IllegalArgumentException e) {
                CraftEnginePolyfills.instance().getLogger()
                        .log(Level.WARNING, "[Cron] job '" + def.id() + "' has an invalid cron expression, skipping: "
                                + e.getMessage());
            }
        }
        PARSED_CRONS = List.copyOf(parsed);
    }

    public static List<CronDefinition> all() {
        return DEFINITIONS;
    }

    /** Every enabled, successfully-parsed cron-expression job, for {@link CronScheduler}. */
    public static List<ParsedCron> parsedCrons() {
        return PARSED_CRONS;
    }
}
