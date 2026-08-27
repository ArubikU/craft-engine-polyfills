package dev.arubik.craftengine.events;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;

/**
 * {@code EventManager.register(event, script, timeout_ticks)} — a SCRIPT-driven counterpart to
 * the static {@code events/*.json} bridge ({@link GenericEventBridge}): instead of a fixed
 * definition loaded once at startup, a running script can subscribe to one Bukkit event, fire a
 * callback the FIRST time it happens, and have the subscription clean itself up automatically —
 * the exact shape a "cancel this if the player moves within N ticks" countdown needs (see
 * {@code tpa.pf}'s teleport warmup for the reference use).
 *
 * <p>Every registration is one-shot: it fires at most once, then unregisters itself, whether it
 * fired naturally or {@code timeout_ticks} elapsed first (a script is free to re-register from
 * inside its own callback if it actually wants to keep listening). Registering many long-lived
 * handlers this way for something that should run for every player forever is the wrong tool —
 * that's exactly what {@code events/*.json} + {@link GenericEventBridge} is for.
 */
public final class DynamicEventRegistry {

    private record Handle(Listener listener, BukkitTask timeoutTask) {}

    private static final Map<String, Handle> HANDLERS = new ConcurrentHashMap<>();
    private static final AtomicLong SEQ = new AtomicLong();

    private DynamicEventRegistry() {}

    /** @return a handle id to pass to {@link #unregister(String)}, or {@code null} if the event
     *  class couldn't be resolved/registered. */
    public static String register(String eventNameOrAlias, String scriptRef, int timeoutTicks) {
        Plugin plugin = CraftEnginePolyfills.instance();
        try {
            Class<?> raw = Class.forName(resolveAlias(eventNameOrAlias));
            if (!Event.class.isAssignableFrom(raw)) {
                plugin.getLogger().warning("[EventManager] '" + eventNameOrAlias + "' is not a Bukkit Event");
                return null;
            }
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) raw;
            String id = "dyn_" + SEQ.incrementAndGet();
            Listener listener = new Listener() {};
            Bukkit.getPluginManager().registerEvent(
                eventClass, listener, EventPriority.NORMAL,
                (l, event) -> {
                    try {
                        ScriptCall call = ScriptCall.parse(scriptRef);
                        if (call != null) call.execute(GenericEventBridge.buildContext(event));
                    } catch (Throwable t) {
                        plugin.getLogger().log(Level.WARNING, "[EventManager] " + scriptRef + " threw", t);
                    } finally {
                        unregister(id);
                    }
                },
                plugin, false
            );
            BukkitTask timeoutTask = timeoutTicks > 0
                ? Bukkit.getScheduler().runTaskLater(plugin, () -> unregister(id), timeoutTicks)
                : null;
            HANDLERS.put(id, new Handle(listener, timeoutTask));
            return id;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("[EventManager] unknown event '" + eventNameOrAlias + "'");
            return null;
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "[EventManager] failed to register '" + eventNameOrAlias + "'", t);
            return null;
        }
    }

    /** Cancels a registration before it fires (or before its timeout) — also called internally
     *  once a handler fires, so calling this again with the same (now-stale) id is a harmless
     *  no-op. */
    public static boolean unregister(String id) {
        Handle h = HANDLERS.remove(id);
        if (h == null) return false;
        HandlerList.unregisterAll(h.listener());
        if (h.timeoutTask() != null) h.timeoutTask().cancel();
        return true;
    }

    /** A handful of short names for the events a script is realistically going to want without
     *  typing the fully-qualified class every time; anything containing a "." is assumed to
     *  already be fully-qualified and passed through untouched (any Bukkit/Paper event, including
     *  ones from other plugins, works this way — the short list below is convenience only). */
    private static String resolveAlias(String name) {
        if (name.indexOf('.') >= 0) return name;
        return switch (name) {
            case "PlayerMoveEvent" -> "org.bukkit.event.player.PlayerMoveEvent";
            case "PlayerJoinEvent" -> "org.bukkit.event.player.PlayerJoinEvent";
            case "PlayerQuitEvent" -> "org.bukkit.event.player.PlayerQuitEvent";
            case "PlayerDeathEvent" -> "org.bukkit.event.entity.PlayerDeathEvent";
            case "PlayerTeleportEvent" -> "org.bukkit.event.player.PlayerTeleportEvent";
            case "EntityDamageEvent" -> "org.bukkit.event.entity.EntityDamageEvent";
            case "PlayerInteractEvent" -> "org.bukkit.event.player.PlayerInteractEvent";
            default -> name.toUpperCase(Locale.ROOT).startsWith("PLAYER")
                ? "org.bukkit.event.player." + name
                : "org.bukkit.event." + name;
        };
    }
}
