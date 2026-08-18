/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.EmptyBlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.AbstractArrow
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;

public final class ContraptionProjectileCollision {
    private static final Map<UUID, Stuck> STUCK = new ConcurrentHashMap<UUID, Stuck>();
    private static final double STICK_BACKOFF = 0.35;

    private ContraptionProjectileCollision() {
    }

    public static void tick(ContraptionState state, ContraptionLevel level, ServerLevel realLevel) {
        if (state == null || level == null || realLevel == null) {
            return;
        }
        STUCK.keySet().removeIf(id -> Bukkit.getEntity((UUID)id) == null);
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        double pitch = state.pitchRadians();
        double roll = state.rollRadians();
        double scale = state.scale();
        AABB worldBox = ContraptionProjectileCollision.worldSearchBox(state, level);
        if (worldBox == null) {
            return;
        }
        List<Projectile> projectiles = realLevel.getEntitiesOfClass(Projectile.class, worldBox);
        for (Projectile proj : projectiles) {
            Vec3 localEnd;
            if (proj == null || proj.isRemoved()) continue;
            UUID id2 = proj.getUUID();
            Stuck st = STUCK.get(id2);
            if (st != null) {
                if (!st.contraptionId().equals(state.id())) continue;
                ContraptionProjectileCollision.reanchor(proj, level, st);
                continue;
            }
            Vec3 worldStart = new Vec3(proj.xo, proj.yo, proj.zo);
            Vec3 worldEnd = proj.position();
            Vec3 localStart = ContraptionMath.realToLocal(worldStart, bearing, yaw, pitch, roll, scale);
            Optional<Vec3> hit = ContraptionProjectileCollision.clipAgainstCells(level, localStart, localEnd = ContraptionMath.realToLocal(worldEnd, bearing, yaw, pitch, roll, scale));
            if (hit.isEmpty()) continue;
            Vec3 localHit = hit.get();
            Vec3 worldDir = proj.getDeltaMovement();
            if (worldDir.lengthSqr() < 1.0E-9) {
                worldDir = proj.getViewVector(1.0f);
            }
            Vec3 localDir = ContraptionProjectileCollision.worldToLocalDir(worldDir, bearing, yaw, pitch, roll, scale);
            Vec3 localPos = localHit.subtract(localDir.scale(0.35));
            STUCK.put(id2, new Stuck(state.id(), localPos, localDir));
            if (proj.getBukkitEntity() instanceof AbstractArrow) {
                proj.noPhysics = true;
            }
            ContraptionProjectileCollision.reanchor(proj, level, STUCK.get(id2));
        }
    }

    private static void reanchor(Projectile proj, ContraptionLevel level, Stuck st) {
        Vec3 w = level.realWorldPositionOf(st.localPos());
        proj.setPos(w.x, w.y, w.z);
        proj.setDeltaMovement(Vec3.ZERO);
        proj.setNoGravity(true);
        Vec3 worldDir = level.rotateToRealWorld(st.localDir());
        double hd = Math.sqrt(worldDir.x * worldDir.x + worldDir.z * worldDir.z);
        if (worldDir.lengthSqr() > 1.0E-9) {
            float yaw = (float)(Math.atan2(worldDir.x, worldDir.z) * 57.29577951308232);
            float pitch = (float)(Math.atan2(worldDir.y, hd) * 57.29577951308232);
            proj.setYRot(yaw);
            proj.setXRot(pitch);
            proj.yRotO = yaw;
            proj.xRotO = pitch;
        }
    }

    private static Vec3 worldToLocalDir(Vec3 worldDir, Vec3 bearing, double yaw, double pitch, double roll, double scale) {
        Vec3 o = ContraptionMath.realToLocal(bearing, bearing, yaw, pitch, roll, scale);
        Vec3 d = ContraptionMath.realToLocal(bearing.add(worldDir), bearing, yaw, pitch, roll, scale);
        Vec3 local = d.subtract(o);
        return local.lengthSqr() < 1.0E-12 ? worldDir : local.normalize();
    }

    private static Optional<Vec3> clipAgainstCells(ContraptionLevel level, Vec3 a, Vec3 b) {
        Set<BlockPos> cells = level.localPositions();
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dz = b.z - a.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0E-9) {
            BlockPos p = BlockPos.containing((double)a.x, (double)a.y, (double)a.z);
            return ContraptionProjectileCollision.solidCell(level, cells, p) ? Optional.of(a) : Optional.empty();
        }
        double ux = dx / len;
        double uy = dy / len;
        double uz = dz / len;
        int x = Mth.floor((double)a.x);
        int y = Mth.floor((double)a.y);
        int z = Mth.floor((double)a.z);
        int stepX = ux > 0.0 ? 1 : (ux < 0.0 ? -1 : 0);
        int stepY = uy > 0.0 ? 1 : (uy < 0.0 ? -1 : 0);
        int stepZ = uz > 0.0 ? 1 : (uz < 0.0 ? -1 : 0);
        double tMaxX = ContraptionProjectileCollision.axisTMax(a.x, x, ux, stepX);
        double tMaxY = ContraptionProjectileCollision.axisTMax(a.y, y, uy, stepY);
        double tMaxZ = ContraptionProjectileCollision.axisTMax(a.z, z, uz, stepZ);
        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / ux);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / uy);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / uz);
        double t = 0.0;
        int guard = 0;
        while (t <= len && guard++ < 4096) {
            BlockPos p = new BlockPos(x, y, z);
            if (ContraptionProjectileCollision.solidCell(level, cells, p)) {
                AABB box = new AABB((double)x, (double)y, (double)z, (double)x + 1.0, (double)y + 1.0, (double)z + 1.0);
                Optional<Vec3> c = box.clip(a, b);
                return c.isPresent() ? c : Optional.of(new Vec3(a.x + ux * t, a.y + uy * t, a.z + uz * t));
            }
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                x += stepX;
                t = tMaxX;
                tMaxX += tDeltaX;
                continue;
            }
            if (tMaxY <= tMaxZ) {
                y += stepY;
                t = tMaxY;
                tMaxY += tDeltaY;
                continue;
            }
            z += stepZ;
            t = tMaxZ;
            tMaxZ += tDeltaZ;
        }
        return Optional.empty();
    }

    private static double axisTMax(double start, int block, double u, int step) {
        if (step == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double next = step > 0 ? (double)(block + 1) - start : start - (double)block;
        return next / Math.abs(u);
    }

    private static boolean solidCell(ContraptionLevel level, Set<BlockPos> cells, BlockPos p) {
        if (!cells.contains(p)) {
            return false;
        }
        BlockState s = level.getBlockState(p);
        return !s.isAir() && !s.getCollisionShape((BlockGetter)EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
    }

    private static AABB worldSearchBox(ContraptionState state, ContraptionLevel level) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
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
        double halfX = (double)(maxX + 1 - minX) * 0.5;
        double halfY = (double)(maxY + 1 - minY) * 0.5;
        double halfZ = (double)(maxZ + 1 - minZ) * 0.5;
        double radius = Math.sqrt(halfX * halfX + halfY * halfY + halfZ * halfZ) * Math.max(1.0, state.scale()) + 6.0;
        return new AABB(state.x() - radius, state.y() - radius, state.z() - radius, state.x() + radius, state.y() + radius, state.z() + radius);
    }

    private record Stuck(UUID contraptionId, Vec3 localPos, Vec3 localDir) {
    }
}

