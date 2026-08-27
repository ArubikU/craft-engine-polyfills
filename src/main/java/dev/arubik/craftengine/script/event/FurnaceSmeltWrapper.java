package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a furnace finishes cooking an item into its result. A script could use
 * {@link #setResult} to swap the vanilla smelting output for a custom item whenever
 * {@link #source()} matches a specific ingredient.
 */
public final class FurnaceSmeltWrapper extends ScriptEvent {
    private final FurnaceSmeltEvent raw;

    public FurnaceSmeltWrapper(FurnaceSmeltEvent raw) {
        super("FurnaceSmeltEvent");
        this.raw = raw;
    }

    public FurnaceSmeltEvent raw() { return raw; }

    public ScriptValue source() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getSource()));
    }

    public ScriptValue result() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getResult()));
    }

    public void setResult(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setResult(CraftItemStack.asBukkitCopy(i.stack()));
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
