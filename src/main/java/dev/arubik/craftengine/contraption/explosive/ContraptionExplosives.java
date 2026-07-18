package dev.arubik.craftengine.contraption.explosive;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.ContraptionState;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.contraption.physics.RigidBody;

/**
 * Shared primitives for the two ways a contraption's TNT reaches the real world:
 * {@link ContraptionTntEjectListener} (a cell was lit) and {@link ContraptionImpactDetonator} (the whole
 * body crashed). Both need to know how much TNT is aboard and how fast a given point of the structure is
 * actually moving, and neither answer belongs to either of them alone.
 */
public final class ContraptionExplosives {

    private ContraptionExplosives() {
    }

    /** How many captured cells are TNT. Zero for a null level, or one carrying none. */
    public static int countTnt(ContraptionLevel level) {
        if (level == null) {
            return 0;
        }
        int count = 0;
        for (BlockPos local : level.localPositions()) {
            if (level.getBlockState(local).getBlock() instanceof TntBlock) {
                count++;
            }
        }
        return count;
    }

    /**
     * The real-world velocity of the material point of {@code state}'s structure currently sitting at
     * {@code worldPos}, in blocks per tick — what anything leaving the contraption must inherit so it
     * keeps flying with the body instead of being dropped dead in its wake.
     *
     * <p>For a PhysContraption this is the rigid-body answer {@code v + ω × r}: a point out on the rim of
     * a spinning body moves considerably faster than its center of mass, and TNT thrown off a rotating
     * contraption should be flung, not dribbled. For every other contraption there is no rigid body to
     * ask, so it falls back to the bearing's per-tick translation
     * ({@link ContraptionState#lastDeltaX()}) — which has no rotational term, and correctly reports zero
     * for a stationary or stalled contraption.
     */
    public static Vec3 velocityAt(ContraptionState state, Vec3 worldPos) {
        if (state == null) {
            return Vec3.ZERO;
        }
        PhysBody phys = PhysicsWorld.bodyOf(state.id());
        if (phys != null) {
            RigidBody body = phys.body;
            Vector3d r = new Vector3d(worldPos.x - body.position.x, worldPos.y - body.position.y,
                    worldPos.z - body.position.z);
            Vector3d v = body.velocityAt(r, new Vector3d());
            return new Vec3(v.x, v.y, v.z);
        }
        return new Vec3(state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ());
    }
}
