package dev.arubik.craftengine.contraption.event;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.core.BlockPos;

/**
 * Fired on the main thread just AFTER a contraption has disassembled — the real blocks have
 * been grid-snapped and restored (or ejected) back into the world and the contraption has been
 * dropped from {@code ContraptionManager}; see {@code ContraptionAssembler#disassemble} and
 * {@code MinecartBearing#disassemble}.
 *
 * <p>Not cancellable — the contraption no longer exists as a live entity by the time this
 * fires (its mini-dimension is being disposed). The {@link ContraptionEntity} facade is
 * intentionally NOT exposed here since its backing level is gone; instead the id plus the
 * cheaply-available final resting footprint (the snapped bearing origin and the set of world
 * positions the blocks landed on) are provided.
 */
public class ContraptionDisassembledEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID contraptionId;
    private final World world;
    private final BlockPos snappedBearing;
    private final Set<BlockPos> restingPositions;

    /**
     * @param contraptionId    the id of the now-gone contraption
     * @param world            the world its blocks were restored into
     * @param snappedBearing   the grid-snapped world position of the bearing origin after disassembly
     * @param restingPositions the world positions the restored blocks landed on — wrapped unmodifiable, never null
     */
    public ContraptionDisassembledEvent(UUID contraptionId, World world, BlockPos snappedBearing,
            Set<BlockPos> restingPositions) {
        this.contraptionId = contraptionId;
        this.world = world;
        this.snappedBearing = snappedBearing;
        this.restingPositions = Collections.unmodifiableSet(restingPositions);
    }

    /** The id of the contraption that just disassembled. */
    public UUID getContraptionId() {
        return contraptionId;
    }

    /** The world the blocks were restored into. */
    public World getWorld() {
        return world;
    }

    /** The grid-snapped world position of the bearing origin after disassembly. */
    public BlockPos getSnappedBearing() {
        return snappedBearing;
    }

    /** Bukkit-friendly view of {@link #getSnappedBearing()} in {@link #getWorld()}. */
    public Location getSnappedBearingLocation() {
        return new Location(world, snappedBearing.getX(), snappedBearing.getY(), snappedBearing.getZ());
    }

    /** The (unmodifiable) set of world positions the restored blocks landed on. */
    public Set<BlockPos> getRestingPositions() {
        return restingPositions;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
