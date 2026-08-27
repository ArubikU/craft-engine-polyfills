package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.server.TabCompleteEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires whenever a command sender (player or console) requests tab-completion for a command or
 * chat buffer. A script could use {@link #setCompletions} to inject custom suggestions into a
 * plugin command's argument list — e.g. offering the names of this plugin's own machine types where
 * vanilla tab-complete has no idea they exist.
 */
public final class TabCompleteEventWrapper extends ScriptEvent {
    private final TabCompleteEvent raw;

    public TabCompleteEventWrapper(TabCompleteEvent raw) {
        super("TabCompleteEvent");
        this.raw = raw;
    }

    public TabCompleteEvent raw() { return raw; }

    /** The full text typed so far (command name plus whatever argument is being completed). */
    public String buffer() { return raw.getBuffer(); }

    public ScriptValue completions() {
        List<ScriptValue> out = new ArrayList<>();
        for (String s : raw.getCompletions()) out.add(ScriptValue.of(s));
        return new ScriptValue.Array(out);
    }

    /** Replaces the full suggestion list — every arg (or every element of a single array arg) is
     *  treated as one suggestion string, same "flat or array" idiom as {@code event.set_drops(...)}
     *  elsewhere in this package. */
    public void setCompletions(List<ScriptValue> args) {
        List<String> completions = new ArrayList<>();
        for (ScriptValue v : args) {
            if (v instanceof ScriptValue.Array arr) {
                for (ScriptValue elem : arr.elements()) completions.add(elem.asStr());
            } else {
                completions.add(v.asStr());
            }
        }
        raw.setCompletions(completions);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
