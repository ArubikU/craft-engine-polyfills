package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.world.StructureGrowEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a tree/mushroom/vine structure is about to grow (naturally aging up from a sapling,
 * or bonemealed) — before the generated blocks in {@link #blocks()} are actually placed. A script
 * could use this to protect a claimed region: filter {@link #blocks()} down to blocks outside the
 * claim and cancel the growth entirely if any would land inside it.
 */
public final class StructureGrowWrapper extends ScriptEvent {
    private final StructureGrowEvent raw;

    public StructureGrowWrapper(StructureGrowEvent raw) {
        super("StructureGrowEvent");
        this.raw = raw;
    }

    public StructureGrowEvent raw() { return raw; }

    public ScriptValue location() { return ScriptEventUtil.wrapLocation(raw.getLocation()); }

    /** The player who bonemealed the growth, or {@code NULL} for a purely natural grow-tick with no
     *  player involved. */
    public ScriptValue player() {
        Player player = raw.getPlayer();
        if (player == null) return ScriptValue.NULL;
        return PlayerType.wrap(((CraftPlayer) player).getHandle());
    }

    /** The blocks about to be placed as part of the grown structure. {@code getBlocks()} returns
     *  immutable {@code BlockState} snapshots (their eventual type/data, not yet placed) rather than
     *  live {@code Block}s, so each is re-wrapped as the live block at that position — a script
     *  reading it before growth completes still sees whatever was there before (e.g. air). */
    public List<ScriptValue> blocks() {
        List<ScriptValue> out = new ArrayList<>();
        for (BlockState state : raw.getBlocks()) {
            ServerLevel level = ((CraftWorld) state.getWorld()).getHandle();
            out.add(BlockType.wrap(level, new BlockPos(state.getX(), state.getY(), state.getZ())));
        }
        return out;
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
