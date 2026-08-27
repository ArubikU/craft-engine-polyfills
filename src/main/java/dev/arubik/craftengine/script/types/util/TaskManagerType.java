package dev.arubik.craftengine.script.types.util;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.tasks.TaskManagerRegistry;

/**
 * {@code TaskManager} — plain deferred/repeating script execution. {@code
 * TaskManager.schedule("file.pf:fn", delay_ticks, data?)} runs {@code fn} once after {@code
 * delay_ticks}; {@code TaskManager.repeat("file.pf:fn", delay_ticks, period_ticks, data?)} runs it
 * repeatedly starting after {@code delay_ticks}, every {@code period_ticks} after that. Both
 * return a handle string for {@code TaskManager.cancel(...)}. The optional trailing {@code data}
 * (a {@code make_map(...)} value) is handed back to the fired script as {@code Task.get("key")} —
 * see {@link TaskManagerRegistry}'s javadoc for why that matters (it can carry a LIVE
 * Player/Entity value, not just a name/UUID to re-resolve).
 */
public final class TaskManagerType {

    public static final Object INSTANCE = new Object();

    private TaskManagerType() {}

    public static void register() {
        PolyTypeRegistry.define("TaskManager")
            // NOT migrated to methodTyped: `data` is a genuinely optional trailing make_map(...)
            // argument (dataMap checks args.size()/index itself) — a typed handler only receives
            // its fixed-arity decoded arguments, not the original args list/size, so that
            // conditional read can't be expressed. Left untyped (schedule/repeat/schedule_async).
            .method("schedule", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of("");
                String id = TaskManagerRegistry.schedule(args.get(0).asStr(), (long) args.get(1).asNum(), dataMap(args, 2));
                return ScriptValue.of(id);
            })
            .method("repeat", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of("");
                String id = TaskManagerRegistry.repeat(args.get(0).asStr(), (long) args.get(1).asNum(), (long) args.get(2).asNum(), dataMap(args, 3));
                return ScriptValue.of(id);
            })
            // schedule_async(file.pf:fn, delay_ticks, data?) — SAME signature as schedule(...), but
            // fn runs on a background thread pool (Bukkit's runTaskLaterAsynchronously), not the
            // main server thread. ONLY safe for a function that touches NOTHING backed by Bukkit or
            // this plugin's own shared mutable state — no Player/Server/Menu/Dialog/VirtualUI/Item
            // calls, no Server.get_typed/set_typed (ServerFlags is a plain unsynchronized HashMap),
            // no world/block access. Safe inputs are ONLY the plain strings/numbers/arrays/maps
            // passed in via `data` (Task.get(...) reads a snapshot, not shared state) — do all your
            // heavy pure computation here, then call TaskManager.schedule(...) (the SYNC one) from
            // inside fn to hop back to the main thread and apply the result. See games/gomoku.pf's
            // bot_move/bot_compute_move/bot_apply_move split for the reference pattern this exists
            // for: the bot's minimax-ish search is pure string/array/number work with zero Bukkit
            // touches, so it runs off-thread instead of freezing a server tick.
            .method("schedule_async", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of("");
                String id = TaskManagerRegistry.scheduleAsync(args.get(0).asStr(), (long) args.get(1).asNum(), dataMap(args, 2));
                return ScriptValue.of(id);
            })
            .methodTyped1("cancel", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String handle) -> TaskManagerRegistry.cancel(handle));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> dataMap(java.util.List<ScriptValue> args, int index) {
        if (args.size() <= index) return Map.of();
        ScriptValue v = args.get(index);
        if (v instanceof ScriptValue.Obj o && "Map".equals(o.typeName()) && o.instance() instanceof Map<?, ?> m) {
            return new LinkedHashMap<>((Map<String, ScriptValue>) m);
        }
        return Map.of();
    }
}
