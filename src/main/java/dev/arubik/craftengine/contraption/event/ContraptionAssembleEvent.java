package dev.arubik.craftengine.contraption.event;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.contraption.BearingType;
import net.minecraft.core.BlockPos;

/**
 * Fired on the main thread just BEFORE a contraption assembles — after the block set to be
 * captured has been resolved (glue scan + multiblock expansion), but before any real block is
 * read or removed from the world (see {@code ContraptionAssembler#assemble}/
 * {@code #assemblePiston} and {@code MinecartBearing#assemble} for the exact call sites).
 *
 * <p>{@linkplain Cancellable Cancelling} this event aborts the assembly entirely: no blocks are
 * captured, no {@code ContraptionEntity} is created, and the triggering assemble method returns
 * {@code null} exactly as it would for an empty structure. When no listener cancels, behavior is
 * identical to before this event existed.
 *
 * <p>The counterpart {@link ContraptionAssembledEvent} fires afterward, only on success.
 */
public class ContraptionAssembleEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final World world;
    private final BlockPos bearing;
    private final BearingType type;
    private final Set<BlockPos> capturedPositions;
    private final Player cause;
    private boolean cancelled;

    /**
     * @param world             the real Bukkit world the bearing lives in
     * @param bearing           the bearing block's world position (local-space origin of the contraption)
     * @param type              which bearing kind is assembling (LINEAR / ROTATIONAL / MINECART)
     * @param capturedPositions the block positions about to be captured — wrapped unmodifiable, never null
     * @param cause             the player who triggered the assembly, or {@code null} if unknown
     *                          (e.g. a redstone-driven piston or a persistence rehydrate)
     */
    public ContraptionAssembleEvent(World world, BlockPos bearing, BearingType type,
            Set<BlockPos> capturedPositions, @Nullable Player cause) {
        this.world = world;
        this.bearing = bearing;
        this.type = type;
        this.capturedPositions = Collections.unmodifiableSet(capturedPositions);
        this.cause = cause;
    }

    /** The real Bukkit world the bearing lives in. */
    public World getWorld() {
        return world;
    }

    /** The bearing block's world position (NMS {@link BlockPos}), the contraption's local-space origin. */
    public BlockPos getBearingBlockPos() {
        return bearing;
    }

    /** Bukkit-friendly view of {@link #getBearingBlockPos()} in {@link #getWorld()}. */
    public Location getBearingLocation() {
        return new Location(world, bearing.getX(), bearing.getY(), bearing.getZ());
    }

    /** Which bearing kind is assembling. */
    public BearingType getType() {
        return type;
    }

    /** The (unmodifiable) set of block positions about to be captured out of the real world. */
    public Set<BlockPos> getCapturedPositions() {
        return capturedPositions;
    }

    /** The player who triggered the assembly, or {@code null} if it was not player-initiated. */
    @Nullable
    public Player getCause() {
        return cause;
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
