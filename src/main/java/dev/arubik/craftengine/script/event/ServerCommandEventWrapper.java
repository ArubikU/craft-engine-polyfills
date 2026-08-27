package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * Fires whenever the CONSOLE (or another non-player {@code CommandSender}, e.g. a command block or
 * a plugin issuing a command on the console's behalf) runs a command — player-issued commands go
 * through {@code PlayerCommandPreprocessEvent} instead. A script could use {@link #setCommand} to
 * rewrite a console alias into its real command before it dispatches, or {@link #setCancelled} to
 * block a dangerous console command entirely.
 */
public final class ServerCommandEventWrapper extends ScriptEvent {
    private final ServerCommandEvent raw;

    public ServerCommandEventWrapper(ServerCommandEvent raw) {
        super("ServerCommandEvent");
        this.raw = raw;
    }

    public ServerCommandEvent raw() { return raw; }

    public String command() { return raw.getCommand(); }

    public void setCommand(String command) { raw.setCommand(command == null ? "" : command); }

    public String senderName() { return raw.getSender().getName(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
