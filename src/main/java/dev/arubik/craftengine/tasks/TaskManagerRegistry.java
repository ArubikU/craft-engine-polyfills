package dev.arubik.craftengine.tasks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.event.EventManagerType;
import dev.arubik.craftengine.script.types.util.TaskManagerType;
import dev.arubik.craftengine.script.types.util.TaskType;
import dev.arubik.craftengine.script.types.world.ServerType;

/**
 * {@code TaskManager.schedule(script, delay_ticks, data?)} / {@code .repeat(script, delay_ticks,
 * period_ticks, data?)} — plain deferred/repeating {@code .pf} script execution, backed by
 * Bukkit's own scheduler. The optional {@code data} map (built with {@code make_map(...)} on the
 * scripting side) is handed back to the fired script as {@code Task.get("key")} — including LIVE
 * values (a {@code Player}/{@code Entity} object, not just a name/UUID string) that survive the
 * delay intact, so a script doesn't have to round-trip everything through {@code
 * Player.*_flag}/{@code Server.*_flag} and re-resolve it by hand when the task fires.
 */
public final class TaskManagerRegistry {

    private static final Map<String, BukkitTask> TASKS = new ConcurrentHashMap<>();
    private static final AtomicLong SEQ = new AtomicLong();

    private TaskManagerRegistry() {}

    public static String schedule(String scriptRef, long delayTicks, Map<String, ScriptValue> data) {
        Plugin plugin = CraftEnginePolyfills.instance();
        String id = "task_" + SEQ.incrementAndGet();
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            runScript(id, scriptRef, data);
            TASKS.remove(id);
        }, Math.max(0, delayTicks));
        TASKS.put(id, task);
        return id;
    }

    public static String repeat(String scriptRef, long delayTicks, long periodTicks, Map<String, ScriptValue> data) {
        Plugin plugin = CraftEnginePolyfills.instance();
        String id = "task_" + SEQ.incrementAndGet();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> runScript(id, scriptRef, data),
                Math.max(0, delayTicks), Math.max(1, periodTicks));
        TASKS.put(id, task);
        return id;
    }

    /** Same as {@link #schedule} but the fired script runs on a background thread (Bukkit's
     *  {@code runTaskLaterAsynchronously}) instead of blocking a server tick — see {@code
     *  TaskManagerType#register}'s {@code schedule_async} doc for what's safe to do inside it. */
    public static String scheduleAsync(String scriptRef, long delayTicks, Map<String, ScriptValue> data) {
        Plugin plugin = CraftEnginePolyfills.instance();
        String id = "task_" + SEQ.incrementAndGet();
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            runScript(id, scriptRef, data);
            TASKS.remove(id);
        }, Math.max(0, delayTicks));
        TASKS.put(id, task);
        return id;
    }

    public static boolean cancel(String id) {
        BukkitTask task = TASKS.remove(id);
        if (task == null) return false;
        try { task.cancel(); } catch (Throwable ignored) {}
        return true;
    }

    private static void runScript(String id, String scriptRef, Map<String, ScriptValue> data) {
        try {
            ScriptCall call = ScriptCall.parse(scriptRef);
            if (call == null) return;
            ScriptContext.Builder b = ScriptContext.builder();
            b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
            b.typed("Task", new dev.arubik.craftengine.tasks.TaskInvocation(id, data));
            call.execute(b.build());
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[TaskManager] " + scriptRef + " threw", t);
        }
    }
}
