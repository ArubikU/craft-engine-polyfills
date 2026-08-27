package dev.arubik.craftengine.script.event;

import org.bukkit.event.server.PluginEnableEvent;

/**
 * Fires right after a plugin finishes enabling (including this plugin's own soft-dependencies —
 * BetterModel, ModelEngine — if they load after this one). A script could use this to detect a
 * soft-dependency becoming available at runtime and re-run a "hook up integration" step that only
 * makes sense once that plugin is present. Not {@code Cancellable} — the plugin has already
 * finished enabling by the time this fires.
 */
public final class PluginEnableEventWrapper extends ScriptEvent {
    private final PluginEnableEvent raw;

    public PluginEnableEventWrapper(PluginEnableEvent raw) {
        super("PluginEnableEvent");
        this.raw = raw;
    }

    public PluginEnableEvent raw() { return raw; }

    public String pluginName() { return raw.getPlugin().getName(); }
}
