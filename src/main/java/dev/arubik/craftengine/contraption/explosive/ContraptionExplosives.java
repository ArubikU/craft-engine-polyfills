package dev.arubik.craftengine.contraption.explosive;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    /**
     * Blast resistance a cell must clear, per point of blast power, to SURVIVE the detonation. At the
     * detonator's {@code BASE_POWER} of 4 this cutoff is 200 and at {@code MAX_POWER} of 10 it is 500 — so
     * obsidian, netherite, ancient debris, reinforced deepslate (all {@code 1200}) always ride it out, an
     * ender chest ({@code 600}) survives a small payload but not a big one, and stone ({@code 6}) or wood
     * never do. Bigger payload, more energy, fewer survivors — matching "explote con energía similar a la
     * cantidad de TNT" (2026-07-17).
     */
    public static final double SURVIVE_RESISTANCE_PER_POWER = 50.0;

    /** Outward kick (blocks/tick) added to each surviving cell, away from the blast, so debris scatters. */
    public static final double DEBRIS_SCATTER = 0.3;

    /**
     * Turns a detonating contraption's BLAST-RESISTANT cells into real falling-block debris (2026-07-17 —
     * "los no rompibles como obsi ... se quedan flotando"). Everything else — air, the TNT itself, and every
     * cell too weak to survive {@code power} — is left for the blast to consume. Each survivor is spawned as a
     * vanilla {@link FallingBlockEntity} at its real-world position, inheriting the contraption's velocity at
     * that point (so a survivor flung off a fast/spinning body keeps flying) plus a small outward kick from
     * the blast centre, then tumbles and lands as a real block. Only spawned where the real cell is free —
     * a survivor whose landing spot is already solid world is dropped rather than overwriting it.
     *
     * @return how many survivors were spawned
     */
    public static int spawnBlastSurvivors(ContraptionState state, ServerLevel realLevel, Vec3 blastCenter,
            float power) {
        ContraptionLevel level = state == null ? null : state.level();
        if (level == null || realLevel == null) {
            return 0;
        }
        double cutoff = power * SURVIVE_RESISTANCE_PER_POWER;
        // Placing then converting each survivor uses vanilla's own quiet-ish fall() path; we only ever touch
        // cells the real world has left empty (guarded below), so no neighbour cascade into standing terrain.
        int quietFlags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;
        int spawned = 0;
        for (BlockPos local : level.localPositions()) {
            BlockState bs = level.getBlockState(local);
            if (bs.isAir() || bs.getBlock() instanceof TntBlock) {
                continue; // air, or the TNT that just went up — nothing to leave behind
            }
            if (bs.getBlock().getExplosionResistance() < cutoff) {
                continue; // breakable — the blast eats it, exactly like a real explosion
            }
            Vec3 world = level.realWorldPositionOf(local);
            BlockPos worldPos = BlockPos.containing(world.x, world.y, world.z);
            BlockState existing = realLevel.getBlockState(worldPos);
            if (!existing.isAir() && !existing.canBeReplaced()) {
                continue; // its landing spot is real solid world — don't overwrite it
            }
            realLevel.setBlock(worldPos, bs, quietFlags);
            FallingBlockEntity debris = FallingBlockEntity.fall(realLevel, worldPos, bs);
            Vec3 inherited = velocityAt(state, world);
            Vec3 outward = world.subtract(blastCenter);
            double d = outward.length();
            Vec3 scatter = d > 1.0E-6 ? outward.scale(DEBRIS_SCATTER / d) : Vec3.ZERO;
            debris.setDeltaMovement(inherited.add(scatter));
            spawned++;
        }
        return spawned;
    }
}
