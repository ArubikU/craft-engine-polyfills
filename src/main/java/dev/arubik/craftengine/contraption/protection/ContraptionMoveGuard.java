package dev.arubik.craftengine.contraption.protection;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

/**
 * The per-tick "may this contraption MOVE into this region?" gate (roadmap item #7,
 * {@code .migration/ROADMAP-claims-and-phys.md} §1 "Move into a region" +
 * {@code ContraptionEngine.stepKinematics} enforcement row). Consulted by
 * {@link dev.arubik.craftengine.contraption.ContraptionEngine} right before it writes a new
 * position: on DENY the engine vetoes the translation this tick (reusing the existing
 * stall/zero-velocity gate).
 *
 * <p><b>Phase 1 behaviour</b>: with only {@link AllowAllProtection} registered this always
 * returns {@code true}, so movement is unchanged. It is a real coarse gate (whole-contraption
 * footprint AABB, not cell-by-cell) so a later land-claim adapter's {@code canUseRegion} is
 * queried against its native region index exactly once per boundary crossing.
 *
 * <p><b>Hot-path discipline</b> (the design's single biggest risk — per-tick region checks):
 * <ul>
 *   <li><b>Owner-gated.</b> An <em>unowned</em> contraption (redstone/euler pistons, anything
 *   assembled without a triggering player) has no UUID to attribute the query to and is NOT
 *   region-gated in Phase 1 — a documented limitation, kept so nothing autonomous regresses.</li>
 *   <li><b>Boundary-crossing throttle.</b> The provider is only re-queried when the target
 *   position's snapped bearing CELL changes from the last check (cached on
 *   {@link ContraptionState}); an in-cell sub-block move reuses the cached verdict, so a large
 *   contraption at 20 TPS is never per-tick-querying a claim plugin while drifting within one
 *   block.</li>
 * </ul>
 */
public final class ContraptionMoveGuard {

    private ContraptionMoveGuard() {
    }

    /**
     * Whether the contraption may translate to continuous world position {@code (x,y,z)} this
     * tick. Fail-open at every ambiguity (no owner, no world, no level, any exception) so the
     * gate can only ever restrict a genuinely-claimed move, never break normal movement.
     */
    public static boolean allowsMoveTo(ContraptionState state, double x, double y, double z) {
        UUID owner = state.owner();
        if (owner == null) {
            return true; // unowned → not region-gated in Phase 1 (see class javadoc)
        }
        ContraptionLevel level = state.level();
        if (level == null) {
            return true; // null-level unit-test facade — nothing to gate
        }
        World world;
        try {
            net.minecraft.server.MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
            net.minecraft.server.level.ServerLevel serverLevel = server.getLevel(state.worldId());
            world = serverLevel != null ? serverLevel.getWorld() : null;
        } catch (Throwable t) {
            return true; // no live server (pure-JVM test) — behave as before
        }
        if (world == null) {
            return true; // target world not loaded — don't block, matches engine's own safeGetWorld
        }
        // Boundary-crossing throttle: only re-query the provider when the snapped bearing cell
        // changes. Same cell as last check → reuse the cached verdict (see field javadoc on state).
        long key = BlockPos.asLong(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        if (key == state.lastRegionCheckKey()) {
            return state.lastRegionAllowed();
        }
        BoundingBox box = footprint(state, level, x, y, z);
        boolean allowed = box == null || ContraptionProtectionRegistry.canUseRegion(owner, world, box);
        state.setLastRegionCheck(key, allowed);
        return allowed;
    }

    /**
     * Coarse whole-contraption footprint AABB at target transform, in real-world coordinates.
     * Yaw is intentionally ignored (an axis-aligned bound over the yaw-0 local cells, expanded
     * to the target bearing) — a coarse over-approximation is exactly what a cheap region gate
     * wants, and refining to the rotated hull is a later-phase concern. {@code null} for an
     * empty structure (nothing to gate).
     */
    private static BoundingBox footprint(ContraptionState state, ContraptionLevel level, double x, double y, double z) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        boolean any = false;
        for (BlockPos local : level.localPositions()) {
            double cx = x + local.getX();
            double cy = y + local.getY();
            double cz = z + local.getZ();
            minX = Math.min(minX, cx);
            minY = Math.min(minY, cy);
            minZ = Math.min(minZ, cz);
            maxX = Math.max(maxX, cx + 1);
            maxY = Math.max(maxY, cy + 1);
            maxZ = Math.max(maxZ, cz + 1);
            any = true;
        }
        return any ? new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ) : null;
    }
}
