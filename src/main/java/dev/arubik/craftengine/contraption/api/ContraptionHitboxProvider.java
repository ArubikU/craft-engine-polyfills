package dev.arubik.craftengine.contraption.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * A block behavior that provides custom hitboxes for contraption collision.
 *
 * <p>
 * Vanilla blocks use their block-state bounding box for collision; furniture blocks
 * (armor stands, item frames, hanging signs) and blocks with complex shapes may need
 * custom hitboxes. The contraption collision system checks for this interface and
 * aggregates the returned AABBs into the contraption's shulker hitbox swarm.
 *
 * <p>
 * <b>Use cases</b>:
 * <ul>
 *   <li>Furniture that spawns entities (armor stands, item frames) — return entity hitboxes</li>
 *   <li>Blocks with non-cubic collision shapes (slabs, stairs, fences) — return precise AABBs</li>
 *   <li>Blocks with dynamic hitboxes (rotating shafts, moving pistons) — compute per-tick</li>
 * </ul>
 *
 * <p>
 * <b>Implementation note</b>: Hitboxes are in block-local coordinates (0,0,0 to 1,1,1).
 * The contraption system transforms them to world space using the contraption's position
 * and rotation.
 */
public interface ContraptionHitboxProvider {

    /**
     * Returns custom hitboxes for this block when inside a contraption.
     *
     * <p>
     * Coordinates are block-local: (0, 0, 0) is the block's min corner, (1, 1, 1) is max.
     * The contraption collision system transforms these to world space. For a full-block
     * hitbox, return {@code List.of(new AABB(0, 0, 0, 1, 1, 1))}. For multiple hitboxes
     * (e.g., a fence with post + arm), return all AABBs.
     *
     * <p>
     * Called once when the block is captured into the contraption, then cached. If hitboxes
     * change dynamically (e.g., a piston extending), the contraption must recompute collision
     * shapes (future enhancement — not implemented yet).
     *
     * @param level the contraption's private level (not the real world)
     * @param pos   the block's position in that level
     * @return list of AABBs in block-local coordinates, never null or empty
     */
    List<AABB> getContraptionHitboxes(Level level, BlockPos pos);

    /**
     * Whether these hitboxes change dynamically and need recomputation each tick.
     *
     * <p>
     * Most blocks have static hitboxes (a slab is always 0-0.5 height). Dynamic blocks
     * (pistons, rotating shafts, animated furniture) return true, and the contraption
     * collision system recomputes their AABBs every tick.
     *
     * <p>
     * <b>Performance warning</b>: Dynamic hitboxes are expensive. Only return true if
     * the shape genuinely changes (not just the block's internal state).
     *
     * @return true if hitboxes change and need per-tick recomputation
     */
    default boolean hasDynamicHitboxes() {
        return false;
    }

    /**
     * Whether this block should be included in contraption collision at all.
     *
     * <p>
     * Some blocks (air, water, decorative plants) have no collision. Return false to
     * skip this block entirely when building the contraption's hitbox swarm. Default
     * is true (all blocks with this interface are assumed collidable).
     *
     * @return true if this block contributes hitboxes to the contraption
     */
    default boolean hasContraptionCollision() {
        return true;
    }
}
