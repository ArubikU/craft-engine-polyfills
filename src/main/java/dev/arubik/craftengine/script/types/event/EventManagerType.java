package dev.arubik.craftengine.script.types.event;

import dev.arubik.craftengine.events.DynamicEventRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

/**
 * {@code EventManager} — the script-driven counterpart to the static {@code events/*.json} bridge
 * (see {@code GenericEventBridge}): {@code EventManager.register("PlayerMoveEvent", "file.pf:fn",
 * timeout_ticks)} subscribes to one Bukkit event, fires {@code fn} the FIRST time it happens (or
 * never, if {@code timeout_ticks} elapses first), and cleans itself up either way — see {@link
 * DynamicEventRegistry}'s javadoc for the reference use (a teleport countdown cancelled by
 * movement). {@code register} returns a handle string for {@code EventManager.unregister(...)} to
 * cancel early once it's no longer needed (e.g. the teleport already happened).
 */
public final class EventManagerType {

    public static final Object INSTANCE = new Object();

    private EventManagerType() {}

    public static void register() {
        PolyTypeRegistry.define("EventManager")
            // NOT migrated to methodTyped: `timeout_ticks` is an optional trailing argument with a
            // default (0) that only applies when present alongside the 2 required args — a typed
            // handler forced to arity 3 would treat a legitimate 2-arg call as "missing args" and
            // return the fixed onMissingArgs fallback instead of actually registering.
            // methodTypedOpt3 is wrong too, in the other direction: it would run the body on a 0- or
            // 1-arg call and really subscribe a listener for the defaulted event/handler names,
            // where today such a call returns "" having registered nothing. Left untyped.
            .method("register", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of("");
                int timeout = args.size() > 2 ? (int) args.get(2).asNum() : 0;
                String id = DynamicEventRegistry.register(args.get(0).asStr(), args.get(1).asStr(), timeout);
                return ScriptValue.of(id == null ? "" : id);
            })
            .methodTyped1("unregister", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String handle) -> DynamicEventRegistry.unregister(handle));
    }
}
