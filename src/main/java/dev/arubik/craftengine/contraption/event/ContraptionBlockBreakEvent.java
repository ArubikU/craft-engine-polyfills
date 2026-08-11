package dev.arubik.craftengine.contraption.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fired on the main thread just BEFORE a contraption-borne miner breaks a REAL-world block — see
 * {@code MinerBehavior#tick}, which accumulates mining damage against the block at its fixed
 * bearing-relative offset and, once damage exceeds the block's vanilla hardness, breaks it via
 * {@code ContraptionAccessor#breakBlockAndCollect} (collecting drops into the miner's own virtual
 * inventory rather than spawning real dropped-item entities). Unlike the interact/place events,
 * the {@link #getWorldPos() position} here is a genuine REAL-world block position, since the miner
 * reaches out of the contraption to cut the actual world.
 *
 * <p>{@linkplain Cancellable Cancelling} this event skips the break: the real block survives, no
 * drops are collected, and the miner's accumulated damage resets so it re-accumulates from scratch
 * on its next cutting cycle (this both honours the veto and avoids the event re-firing every tick
 * while a listener keeps refusing the break). When no listener cancels, behavior is byte-identical
 * to before this event existed.
 *
 * <p>The owning {@link ContraptionEntity} is resolved from the miner's movement context at the fire
 * site (its contraption id via {@code ContraptionManager}); if the facade can't be resolved the
 * break proceeds unguarded, exactly as before.
 */
public class ContraptionBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ContraptionEntity entity;
    private final World world;
    private final BlockPos worldPos;
    private final BlockState state;
    private boolean cancelled;

    /**
     * @param entity   the contraption whose captured miner is breaking the block
     * @param world    the real world the block lives in
     * @param worldPos the REAL-world position of the block about to break
     * @param state    the block state about to break (read from the world immediately before the break)
     */
    public ContraptionBlockBreakEvent(ContraptionEntity entity, World world, BlockPos worldPos, BlockState state) {
        this.entity = entity;
        this.world = world;
        this.worldPos = worldPos;
        this.state = state;
    }

    /** The contraption whose captured miner is breaking the block. */
    public ContraptionEntity getEntity() {
        return entity;
    }

    /** The real world the block lives in. */
    public World getWorld() {
        return world;
    }

    /** The REAL-world position (NMS {@link BlockPos}) of the block about to break. */
    public BlockPos getWorldPos() {
        return worldPos;
    }

    /** Bukkit-friendly view of {@link #getWorldPos()} in {@link #getWorld()}. */
    public Location getLocation() {
        return new Location(world, worldPos.getX(), worldPos.getY(), worldPos.getZ());
    }

    /** The block state (NMS {@link BlockState}) about to break. */
    public BlockState getState() {
        return state;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
