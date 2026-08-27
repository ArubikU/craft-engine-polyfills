package dev.arubik.craftengine.script.event;

import org.bukkit.event.server.PluginDisableEvent;

/**
 * Fires right before a plugin finishes disabling (server shutdown, {@code /reload}, or a manual
 * {@code /plugman unload}). A script could use this to detect a soft-dependency going away and
 * gracefully fall back — e.g. disabling a BetterModel-only renderer feature instead of erroring the
 * next time it's used. Not {@code Cancellable} — a plugin can't be vetoed out of disabling.
 */
public final class PluginDisableEventWrapper extends ScriptEvent {
    private final PluginDisableEvent raw;

    public PluginDisableEventWrapper(PluginDisableEvent raw) {
        super("PluginDisableEvent");
        this.raw = raw;
    }

    public PluginDisableEvent raw() { return raw; }

    public String pluginName() { return raw.getPlugin().getName(); }
}
