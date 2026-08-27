package dev.arubik.craftengine.script.types.event;

import dev.arubik.craftengine.events.DynamicEventRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .method("register", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of("");
                int timeout = args.size() > 2 ? (int) args.get(2).asNum() : 0;
                String id = DynamicEventRegistry.register(args.get(0).asStr(), args.get(1).asStr(), timeout);
                return ScriptValue.of(id == null ? "" : id);
            })
            .method("unregister", (obj, args) ->
                ScriptValue.of(!args.isEmpty() && DynamicEventRegistry.unregister(args.get(0).asStr())));
    }
}
