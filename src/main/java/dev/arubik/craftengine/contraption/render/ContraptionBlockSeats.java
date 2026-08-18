/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.block.behavior.SeatBlockBehavior
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.seat.SeatConfig
 *  net.momirealms.craftengine.core.util.Direction
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.render.ContraptionSeatMath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.SeatBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.seat.SeatConfig;
import net.momirealms.craftengine.core.util.Direction;

public final class ContraptionBlockSeats {
    private ContraptionBlockSeats() {
    }

    public static List<BlockSeat> scan(ContraptionLevel level) {
        ArrayList<BlockSeat> out = new ArrayList<BlockSeat>();
        if (level == null) {
            return out;
        }
        for (BlockPos local : level.localPositions()) {
            try {
                SeatBlockBehavior seat = ContraptionBlockSeats.seatBehaviorAt(level, local);
                if (seat == null || seat.seats == null || seat.seats.length == 0) continue;
                ImmutableBlockState state = ContraptionBlockSeats.customStateAt(level, local);
                float facingYaw = ContraptionBlockSeats.facingYawDegrees(seat, state);
                Vec3 source = new Vec3((double)local.getX() + 0.5, (double)local.getY(), (double)local.getZ() + 0.5);
                for (SeatConfig cfg : seat.seats) {
                    if (cfg == null || cfg.position() == null) continue;
                    Vec3 offset = ContraptionSeatMath.seatOffset(cfg.position(), facingYaw);
                    out.add(new BlockSeat(source.add(offset), ContraptionSeatMath.seatYawDegrees(cfg, facingYaw), cfg.limitPlayerRotation()));
                }
            }
            catch (Throwable throwable) {
            }
        }
        return out;
    }

    private static SeatBlockBehavior seatBehaviorAt(ContraptionLevel level, BlockPos local) {
        ImmutableBlockState state = ContraptionBlockSeats.customStateAt(level, local);
        if (state == null) {
            return null;
        }
        BlockBehavior behavior = state.behavior();
        return behavior == null ? null : (SeatBlockBehavior)behavior.getFirst(SeatBlockBehavior.class);
    }

    private static ImmutableBlockState customStateAt(ContraptionLevel level, BlockPos local) {
        Optional custom = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(local));
        return (ImmutableBlockState) custom.orElse(null);
    }

    static float facingYawDegrees(SeatBlockBehavior seat, ImmutableBlockState state) {
        Property property = seat.directionProperty;
        if (property == null || state == null) {
            return 0.0f;
        }
        Direction direction = (Direction)state.get(property);
        if (direction == null) {
            return 0.0f;
        }
        return switch (direction) {
            case Direction.SOUTH -> 180.0f;
            case Direction.WEST -> 270.0f;
            case Direction.EAST -> 90.0f;
            default -> 0.0f;
        };
    }

    public record BlockSeat(Vec3 local, float yawOffsetDegrees, boolean limitPlayerRotation) {
    }
}

