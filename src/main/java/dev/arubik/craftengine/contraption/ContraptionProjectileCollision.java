package dev.arubik.craftengine.contraption;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * Real-world projectiles vs contraptions (user's simpler design: "que los projectiles sean ejectados y así solo
 * te encargas de calcular si colisionan con un contraption, y al colisionar una flecha se vuelve parte del
 * contraption").
 *
 * <p>Projectiles stay ORDINARY real-world entities — they are never moved into the hidden level. A contraption's
 * captured blocks live in that hidden level, so a real-world arrow flies through empty world air where the ship
 * visually is and would never hit it on its own. This class closes that gap: each tick it sweeps every nearby
 * projectile's path THIS TICK into the contraption's local frame and clips it against the captured cells. On a
 * hit the projectile is STUCK to the contraption — frozen, gravity off, and re-anchored to the hit cell every
 * tick so it rides the ship as it moves/rotates, exactly like an arrow embedded in a block.
 *
 * <p>No sub-level transfer, no coordinate-frame entity teleports — just a raycast and a per-tick position pin.
 */
public final class ContraptionProjectileCollision {

    private ContraptionProjectileCollision() {
    }

    /** A projectile stuck to a contraption: which one, where it rides (LOCAL hit point) and its LOCAL facing at
     *  impact (so its arrow rotates WITH the hull instead of freezing in world space). */
    private record Stuck(UUID contraptionId, Vec3 localPos, Vec3 localDir) {
    }

    /** projectile UUID -> where it's stuck. Session-scoped; a picked-up/despawned arrow is pruned each tick. */
    private static final Map<UUID, Stuck> STUCK = new ConcurrentHashMap<>();

    /** How far (blocks) to back the anchor off the hit face along the flight dir, so the arrow protrudes not buries. */
    private static final double STICK_BACKOFF = 0.35;

    /** Runs the sweep + pin for one contraption. Main thread, with the real level. */
    public static void tick(ContraptionState state, ContraptionLevel level, ServerLevel realLevel) {
        if (state == null || level == null || realLevel == null) {
            return;
        }
        // Prune arrows that were picked up / despawned since last tick (cheap — usually a handful).
        STUCK.keySet().removeIf(id -> Bukkit.getEntity(id) == null);

        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        double pitch = state.pitchRadians();
        double roll = state.rollRadians();
        double scale = state.scale();

        AABB worldBox = worldSearchBox(state, level);
        if (worldBox == null) {
            return;
        }
        List<Projectile> projectiles = realLevel.getEntitiesOfClass(Projectile.class, worldBox);
        for (Projectile proj : projectiles) {
            if (proj == null || proj.isRemoved()) {
                continue;
            }
            UUID id = proj.getUUID();
            Stuck st = STUCK.get(id);
            if (st != null) {
                // Already stuck — if to THIS contraption, re-anchor position AND facing to the moving hull.
                if (st.contraptionId().equals(state.id())) {
                    reanchor(proj, level, st);
                }
                continue;
            }
            // Free projectile: sweep THIS tick's segment (previous -> current) in the contraption's LOCAL frame
            // and clip it against the captured cells. `xo/yo/zo` are the pre-move position, so the segment is the
            // exact path travelled this tick — a fast arrow can't tunnel through a thin wall.
            Vec3 worldStart = new Vec3(proj.xo, proj.yo, proj.zo);
            Vec3 worldEnd = proj.position();
            Vec3 localStart = ContraptionMath.realToLocal(worldStart, bearing, yaw, pitch, roll, scale);
            Vec3 localEnd = ContraptionMath.realToLocal(worldEnd, bearing, yaw, pitch, roll, scale);
            Optional<Vec3> hit = clipAgainstCells(level, localStart, localEnd);
            if (hit.isEmpty()) {
                continue;
            }
            Vec3 localHit = hit.get();
            // The arrow's facing at IMPACT is its flight direction — capture it and rotate it into the hull's LOCAL
            // frame (user: "que las flechas mantengan su rotación de golpe pero esa se transforme en local y luego
            // esa local sea usada para transformaciones"), so #reanchor can turn it back into world space against
            // the CURRENT hull orientation each tick and the arrow keeps pointing the right way as the ship rotates.
            Vec3 worldDir = proj.getDeltaMovement();
            if (worldDir.lengthSqr() < 1.0e-9) {
                worldDir = proj.getViewVector(1.0f);
            }
            Vec3 localDir = worldToLocalDir(worldDir, bearing, yaw, pitch, roll, scale);
            // The clip point is the block SURFACE, but the arrow model extends FORWARD from its position, so
            // pinning there buries the tip inside the block (user: "queda muy metida"). Pull the anchor back along
            // the flight direction so the arrow protrudes from the face like a vanilla stuck arrow.
            Vec3 localPos = localHit.subtract(localDir.scale(STICK_BACKOFF));
            STUCK.put(id, new Stuck(state.id(), localPos, localDir));
            // Pin it: an AbstractArrow is set no-physics so it stops running its own ground/despawn-on-air logic
            // (there's no REAL block under it) AND so its playerTouch pickup path stays reachable — a pickup-able
            // arrow can then be collected right off the hull; a non-pickup one still can't (its own pickup flag
            // decides). #reanchor freezes its motion and rides it every tick.
            if (proj.getBukkitEntity() instanceof org.bukkit.entity.AbstractArrow) {
                proj.noPhysics = true;
            }
            reanchor(proj, level, STUCK.get(id));
        }
    }

    /** Freezes {@code proj} onto its stuck cell + facing, re-derived against the hull's CURRENT pose this tick. */
    private static void reanchor(Projectile proj, ContraptionLevel level, Stuck st) {
        Vec3 w = level.realWorldPositionOf(st.localPos());
        proj.setPos(w.x, w.y, w.z);
        proj.setDeltaMovement(Vec3.ZERO);
        proj.setNoGravity(true);
        // LOCAL facing -> WORLD facing under the hull's live rotation, then point the arrow along it.
        Vec3 worldDir = level.rotateToRealWorld(st.localDir());
        double hd = Math.sqrt(worldDir.x * worldDir.x + worldDir.z * worldDir.z);
        if (worldDir.lengthSqr() > 1.0e-9) {
            float yaw = (float) (Math.atan2(worldDir.x, worldDir.z) * (180.0 / Math.PI));
            float pitch = (float) (Math.atan2(worldDir.y, hd) * (180.0 / Math.PI));
            proj.setYRot(yaw);
            proj.setXRot(pitch);
            proj.yRotO = yaw;
            proj.xRotO = pitch;
        }
    }

    /** Rotates a WORLD direction into the hull's LOCAL frame, using the authoritative realToLocal (a rigid
     *  transform): apply it to {@code bearing + dir} and to {@code bearing}, the difference is the rotated dir. */
    private static Vec3 worldToLocalDir(Vec3 worldDir, Vec3 bearing, double yaw, double pitch, double roll,
            double scale) {
        Vec3 o = ContraptionMath.realToLocal(bearing, bearing, yaw, pitch, roll, scale);
        Vec3 d = ContraptionMath.realToLocal(bearing.add(worldDir), bearing, yaw, pitch, roll, scale);
        Vec3 local = d.subtract(o);
        return local.lengthSqr() < 1.0e-12 ? worldDir : local.normalize();
    }

    /**
     * First point where segment {@code a}->{@code b} (LOCAL frame) enters a solid captured cell, or empty.
     *
     * <p>Voxel DDA (Amanatides-Woo), NOT a scan of every cell (user: "tickear cada celda es mucho"): it walks ONLY
     * the blocks the ray actually crosses this tick — a handful for a fast arrow — and asks the O(1) cell set
     * {@code localPositions().contains(pos)} whether each is captured. Cost is O(blocks crossed), independent of
     * contraption size, versus the old O(cells).
     */
    private static Optional<Vec3> clipAgainstCells(ContraptionLevel level, Vec3 a, Vec3 b) {
        java.util.Set<BlockPos> cells = level.localPositions();
        double dx = b.x - a.x, dy = b.y - a.y, dz = b.z - a.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0e-9) {
            BlockPos p = BlockPos.containing(a.x, a.y, a.z);
            return solidCell(level, cells, p) ? Optional.of(a) : Optional.empty();
        }
        double ux = dx / len, uy = dy / len, uz = dz / len;
        int x = net.minecraft.util.Mth.floor(a.x), y = net.minecraft.util.Mth.floor(a.y), z = net.minecraft.util.Mth.floor(a.z);
        int stepX = ux > 0 ? 1 : ux < 0 ? -1 : 0;
        int stepY = uy > 0 ? 1 : uy < 0 ? -1 : 0;
        int stepZ = uz > 0 ? 1 : uz < 0 ? -1 : 0;
        double tMaxX = axisTMax(a.x, x, ux, stepX);
        double tMaxY = axisTMax(a.y, y, uy, stepY);
        double tMaxZ = axisTMax(a.z, z, uz, stepZ);
        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / ux);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / uy);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / uz);
        double t = 0.0;
        int guard = 0;
        while (t <= len && guard++ < 4096) {
            BlockPos p = new BlockPos(x, y, z);
            if (solidCell(level, cells, p)) {
                // Entry point into this cell's box — the exact stick position.
                AABB box = new AABB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                Optional<Vec3> c = box.clip(a, b);
                return c.isPresent() ? c : Optional.of(new Vec3(a.x + ux * t, a.y + uy * t, a.z + uz * t));
            }
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                x += stepX;
                t = tMaxX;
                tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
                y += stepY;
                t = tMaxY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                t = tMaxZ;
                tMaxZ += tDeltaZ;
            }
        }
        return Optional.empty();
    }

    /** Distance (in `len` units — the ray is unit-length) from {@code start} to the first grid line crossed on this axis. */
    private static double axisTMax(double start, int block, double u, int step) {
        if (step == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double next = step > 0 ? (block + 1) - start : start - block;
        return next / Math.abs(u);
    }

    /** Whether cell {@code p} is a captured, actually-colliding block (a torch/plant is captured but non-solid). */
    private static boolean solidCell(ContraptionLevel level, java.util.Set<BlockPos> cells, BlockPos p) {
        if (!cells.contains(p)) {
            return false;
        }
        BlockState s = level.getBlockState(p);
        return !s.isAir()
                && !s.getCollisionShape(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
    }

    /** A world-axis box around the contraption big enough to catch a projectile a tick from hitting it. */
    private static AABB worldSearchBox(ContraptionState state, ContraptionLevel level) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        boolean any = false;
        for (BlockPos p : level.localPositions()) {
            any = true;
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        if (!any) {
            return null;
        }
        // Local half-extent -> a world radius (rotation-agnostic: the diagonal), scaled, plus a speed margin.
        double halfX = (maxX + 1 - minX) * 0.5, halfY = (maxY + 1 - minY) * 0.5, halfZ = (maxZ + 1 - minZ) * 0.5;
        double radius = Math.sqrt(halfX * halfX + halfY * halfY + halfZ * halfZ) * Math.max(1.0, state.scale()) + 6.0;
        return new AABB(state.x() - radius, state.y() - radius, state.z() - radius,
                state.x() + radius, state.y() + radius, state.z() + radius);
    }
}
