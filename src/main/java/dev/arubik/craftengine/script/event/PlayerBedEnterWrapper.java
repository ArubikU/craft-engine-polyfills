package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerBedEnterEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player tries to get into a bed. A script could use {@link #bedEnterResult()} to
 * show a custom message explaining WHY the bed didn't work (too far from spawn, monsters nearby,
 * daytime, ...) instead of vanilla's generic action-bar text.
 */
public final class PlayerBedEnterWrapper extends ScriptEvent {
    private final PlayerBedEnterEvent raw;

    public PlayerBedEnterWrapper(PlayerBedEnterEvent raw) {
        super("PlayerBedEnterEvent");
        this.raw = raw;
    }

    public PlayerBedEnterEvent raw() { return raw; }

    public ScriptValue bed() {
        Block block = raw.getBed();
        if (block == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** Why vanilla will let/deny this attempt — e.g. {@code "ok"}, {@code "not_possible_here"},
     *  {@code "not_possible_now"}, {@code "too_far_away"}, {@code "not_safe"}, {@code "other_problem"}. */
    public String bedEnterResult() { return raw.getBedEnterResult().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
