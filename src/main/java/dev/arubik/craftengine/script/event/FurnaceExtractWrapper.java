package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;

/**
 * Fires when a player takes the cooked result out of a furnace's output slot (and gets the queued-up
 * smelting XP for it). A script could use {@link #itemType()}/{@link #itemAmount()} to grant a bonus
 * currency payout whenever a player collects a specific smelted good.
 *
 * <p>Unlike most inventory events, {@code FurnaceExtractEvent} does NOT implement {@code Cancellable}
 * — extraction has already happened by the time this fires — so {@link ScriptEvent#cancel()} here
 * only flips the inert base-class flag, same as {@link PlayerItemBreakWrapper}.
 */
public final class FurnaceExtractWrapper extends ScriptEvent {
    private final FurnaceExtractEvent raw;

    public FurnaceExtractWrapper(FurnaceExtractEvent raw) {
        super("FurnaceExtractEvent");
        this.raw = raw;
    }

    public FurnaceExtractEvent raw() { return raw; }

    public ScriptValue player() {
        return PlayerType.wrap(((CraftPlayer) raw.getPlayer()).getHandle());
    }

    /** Lowercase material name of what was extracted, e.g. {@code "cooked_beef"}. */
    public String itemType() { return raw.getItemType().name().toLowerCase(Locale.ROOT); }

    public int itemAmount() { return raw.getItemAmount(); }
}
