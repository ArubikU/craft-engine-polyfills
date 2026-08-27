package dev.arubik.craftengine.events;

/**
 * One data-driven Bukkit/Paper event hook loaded from {@code plugins/CraftEnginePolyfill/events/
 * *.json} — the generic bridge that lets a server owner fire a {@code .pf} script off ANY Bukkit
 * event by naming its class, with no Java code specific to that one event. See {@link
 * GenericEventBridge} for how this gets wired into Bukkit's plugin manager.
 *
 * @param eventClass a fully-qualified Bukkit/Paper event class name, e.g.
 *                   {@code "org.bukkit.event.player.PlayerJoinEvent"}
 * @param priority   a {@link org.bukkit.event.EventPriority} enum name, uppercase, default
 *                   {@code "NORMAL"}
 * @param script     a {@code ScriptCall}-parseable ref, e.g. {@code "my_script.pf:on_join"}
 */
public record EventDefinition(
        String id,
        String eventClass,
        String priority,
        boolean ignoreCancelled,
        String script
) {}
