package dev.arubik.craftengine.script.types.event;

import dev.arubik.craftengine.events.DynamicEventRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
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
            // Typed with a null sentinel on the LAST required slot (the handler name): STRING
            // decodes a PRESENT argument via the total asStr() (a NULL ScriptValue yields the
            // literal "null"), so it can never yield Java null — `handlerName == null` is exactly
            // the old `args.size() < 2` early return, taken before anything is subscribed.
            // `timeout_ticks` keeps its optional 0 default.
            .methodTypedOpt3("register", TypeCodecs.STRING, null, TypeCodecs.STRING, null,
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.STRING,
                (Object obj, String eventName, String handlerName, Double timeoutArg) -> {
                    if (handlerName == null) return "";
                    String id = DynamicEventRegistry.register(eventName, handlerName, timeoutArg.intValue());
                    return id == null ? "" : id;
                })
            .methodTyped1("unregister", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String handle) -> DynamicEventRegistry.unregister(handle));
    }
}
