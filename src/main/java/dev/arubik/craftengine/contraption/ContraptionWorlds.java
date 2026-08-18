/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ContraptionWorlds {
    private static final Map<ContraptionBoundary, ContraptionEntity> BACK_INDEX = new WeakHashMap<ContraptionBoundary, ContraptionEntity>();
    private static final double GRID_POS_EPS = 0.05;
    private static final double GRID_YAW_EPS = 0.02;

    private ContraptionWorlds() {
    }

    public static void index(ContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level != null) {
            BACK_INDEX.put(level, entity);
        }
    }

    public static void unindex(ContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level != null) {
            BACK_INDEX.remove(level, entity);
        }
    }

    public static Optional<ContraptionEntity> owning(ContraptionLevel level) {
        return level == null ? Optional.empty() : Optional.ofNullable(BACK_INDEX.get(level));
    }

    public static Optional<ContraptionEntity> owning(Level level) {
        return ContraptionBoundary.of(level).flatMap(b -> Optional.ofNullable(BACK_INDEX.get(b)));
    }

    public static Optional<ContraptionEntity> entityOf(ContraptionLevel level) {
        return ContraptionWorlds.owning(level);
    }

    public static Optional<ContraptionEntity> entityOf(Level level) {
        return ContraptionWorlds.owning(level);
    }

    public static Entity resolveEntity(Level level, UUID id) {
        ServerLevel serverLevel;
        Entity direct;
        if (level instanceof ServerLevel && (direct = (serverLevel = (ServerLevel)level).getEntity(id)) != null) {
            return direct;
        }
        Level real = ContraptionBoundary.of(level).map(ContraptionBoundary::realLevel).orElse(null);
        if (real instanceof ServerLevel) {
            ServerLevel realServerLevel = (ServerLevel)real;
            return realServerLevel.getEntity(id);
        }
        return null;
    }

    public static <T extends Entity> List<T> unionEntities(Level level, Class<T> clazz, AABB box, Predicate<? super T> predicate) {
        return level.getEntities(EntityTypeTest.forClass(clazz), box, predicate);
    }

    public static boolean isGridAligned(ContraptionLevel level) {
        double snapped;
        ContraptionState state = ContraptionWorlds.owning(level).map(ContraptionEntity::state).orElse(null);
        if (state == null) {
            return false;
        }
        if (!(ContraptionWorlds.nearInteger(state.x()) && ContraptionWorlds.nearInteger(state.y()) && ContraptionWorlds.nearInteger(state.z()))) {
            return false;
        }
        double yaw = state.yawRadians();
        return Math.abs(ContraptionWorlds.wrapRadians(yaw - (snapped = ContraptionMath.snapYawToCardinal(yaw)))) <= 0.02;
    }

    public static Optional<BlockPos> realBlockNeighbor(ContraptionLevel level, BlockPos localPos, Direction localFace) {
        if (level == null || localPos == null || localFace == null || !ContraptionWorlds.isGridAligned(level)) {
            return Optional.empty();
        }
        if (!(level.realLevel() instanceof ServerLevel)) {
            return Optional.empty();
        }
        BlockPos realCell = ContraptionMath.gridSnap(level.realWorldPositionOf(localPos));
        int[] step = ContraptionWorlds.rotatedStep(level, localFace);
        return Optional.of(realCell.offset(step[0], step[1], step[2]));
    }

    public static Optional<Direction> realDirectionOf(ContraptionLevel level, Direction localFace) {
        if (level == null || localFace == null || !ContraptionWorlds.isGridAligned(level)) {
            return Optional.empty();
        }
        int[] step = ContraptionWorlds.rotatedStep(level, localFace);
        for (Direction d : Direction.values()) {
            if (d.getStepX() != step[0] || d.getStepY() != step[1] || d.getStepZ() != step[2]) continue;
            return Optional.of(d);
        }
        return Optional.empty();
    }

    private static int[] rotatedStep(ContraptionLevel level, Direction localFace) {
        double yaw = ContraptionMath.snapYawToCardinal(level.realYawRadians());
        Vec3 rotated = ContraptionMath.rotateYaw(new Vec3((double)localFace.getStepX(), (double)localFace.getStepY(), (double)localFace.getStepZ()), yaw);
        return new int[]{(int)Math.round(rotated.x), (int)Math.round(rotated.y), (int)Math.round(rotated.z)};
    }

    private static boolean nearInteger(double v) {
        return Math.abs(v - Math.rint(v)) <= 0.05;
    }

    private static double wrapRadians(double radians) {
        double twoPi = Math.PI * 2;
        double wrapped = radians % twoPi;
        if (wrapped < -Math.PI) {
            wrapped += twoPi;
        } else if (wrapped > Math.PI) {
            wrapped -= twoPi;
        }
        return wrapped;
    }
}

