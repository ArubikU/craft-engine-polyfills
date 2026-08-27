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
            // Migrated to methodTypedOpt3/Opt4 (schedule/repeat/schedule_async): `data` is a
            // genuinely optional trailing make_map(...) argument and the body still runs when it's
            // omitted, which is methodTypedOptN's shape (methodTypedN's onMissingArgs would instead
            // skip the scheduling side effect entirely). The REQUIRED slots take a Java `null`
            // default used purely as an "argument was absent" sentinel, reproducing the original
            // args.size() guards EXACTLY: a decoded STRING is never null (ScriptValue.of(String)
            // maps null to NULL, so asStr() always returns a real string) and a decoded DOUBLE is
            // never null, so null can only mean "not passed".
            .methodTypedOpt3("schedule", TypeCodecs.STRING, (String) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.STRING,
                (Object obj, String ref, Double delay, ScriptValue data) -> {
                    if (ref == null || delay == null) return "";
                    return TaskManagerRegistry.schedule(ref, delay.longValue(), dataMap(data));
                })
            .methodTypedOpt4("repeat", TypeCodecs.STRING, (String) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.STRING,
                (Object obj, String ref, Double delay, Double period, ScriptValue data) -> {
                    if (ref == null || delay == null || period == null) return "";
                    return TaskManagerRegistry.repeat(ref, delay.longValue(), period.longValue(), dataMap(data));
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
            // Migrated to methodTypedOpt3 — same shape as schedule(...) above.
            .methodTypedOpt3("schedule_async", TypeCodecs.STRING, (String) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.STRING,
                (Object obj, String ref, Double delay, ScriptValue data) -> {
                    if (ref == null || delay == null) return "";
                    return TaskManagerRegistry.scheduleAsync(ref, delay.longValue(), dataMap(data));
                })
            .methodTyped1("cancel", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String handle) -> TaskManagerRegistry.cancel(handle));
    }

    /** {@code null} means the optional {@code data} argument wasn't passed at all — same empty-map
     *  result the old {@code dataMap(args, index)} produced for an out-of-range index. */
    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> dataMap(ScriptValue v) {
        if (v == null) return Map.of();
        if (v instanceof ScriptValue.Obj o && "Map".equals(o.typeName()) && o.instance() instanceof Map<?, ?> m) {
            return new LinkedHashMap<>((Map<String, ScriptValue>) m);
        }
        return Map.of();
    }
}
