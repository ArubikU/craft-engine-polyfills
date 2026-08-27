package dev.arubik.craftengine.cron;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.types.world.ServerType;

/**
 * Drives every enabled {@link CronDefinition}, ticking once per real second (20 Minecraft ticks).
 * Cron scripts are server-wide (no player context), so each firing gets a plain context with only
 * {@code Server} bound.
 *
 * <p>Two independent firing mechanisms live side by side here, matching the two trigger modes on
 * {@link CronDefinition}:
 * <ul>
 * <li>{@code every_seconds} jobs: a per-job elapsed-second counter that fires and resets once it
 * reaches the job's interval.</li>
 * <li>{@code cron} jobs: re-evaluated only when the wall-clock minute actually changes, so a job
 * fires once per matching minute rather than once per second throughout that whole minute.</li>
 * </ul>
 */
public final class CronScheduler {

    private static final Map<String, Integer> ELAPSED_SECONDS = new HashMap<>();
    private static volatile long lastEpochMinute = -1;

    private CronScheduler() {}

    public static void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, CronScheduler::tick, 20L, 20L);
    }

    private static void tick() {
        tickEverySeconds();
        tickCrons();
    }

    private static void tickEverySeconds() {
        for (CronDefinition def : CronRegistry.all()) {
            if (!def.enabled() || def.everySeconds() <= 0) continue;
            int elapsed = ELAPSED_SECONDS.merge(def.id(), 1, Integer::sum);
            if (elapsed >= def.everySeconds()) {
                ELAPSED_SECONDS.put(def.id(), 0);
                fire(def);
            }
        }
    }

    private static void tickCrons() {
        ZonedDateTime now = ZonedDateTime.now();
        long epochMinute = now.toEpochSecond() / 60L;
        if (epochMinute == lastEpochMinute) return;
        lastEpochMinute = epochMinute;

        for (CronRegistry.ParsedCron parsed : CronRegistry.parsedCrons()) {
            CronDefinition def = parsed.definition();
            if (!def.enabled()) continue;
            if (parsed.expression().matches(now)) {
                fire(def);
            }
        }
    }

    private static void fire(CronDefinition def) {
        try {
            ScriptContext ctx = ScriptContext.builder()
                    .typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons())
                    .build();
            ScriptCall call = ScriptCall.parse(def.script());
            if (call != null) call.execute(ctx);
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .log(Level.WARNING, "[Cron] job '" + def.id() + "' script threw", t);
        }
    }
}
