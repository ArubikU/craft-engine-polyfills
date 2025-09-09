package dev.arubik.craftengine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.momirealms.craftengine.core.util.HorizontalDirection;

public class Utils {
    public static Direction fromDirection(net.momirealms.craftengine.core.util.Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }
    public static Direction fromDirection(HorizontalDirection direction) {
        return switch (direction) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
        };
    }
    public static BlockHitResult toBlockHitResult(net.momirealms.craftengine.core.world.Vec3d hitVec, net.momirealms.craftengine.core.util.Direction direction, net.momirealms.craftengine.core.world.Vec3i blockPos) {
        return new BlockHitResult(
                new net.minecraft.world.phys.Vec3(hitVec.x(), hitVec.y(), hitVec.z()),
                fromDirection(direction),
                new net.minecraft.core.BlockPos(blockPos.x(), blockPos.y(), blockPos.z()),
                false
        );
    }
    public static BlockPos getRelativeBlockPos(net.momirealms.craftengine.core.util.Direction direction,
            BlockPos pos, Level level) {
        return switch (direction) {
            case NORTH -> pos.offset(0, 0, -1);
            case EAST -> pos.offset(1, 0, 0);
            case SOUTH -> pos.offset(0, 0, 1);
            case WEST -> pos.offset(-1, 0, 0);
            case UP -> pos.offset(0, 1, 0);
            case DOWN -> pos.offset(0, -1, 0);
        };
    }
}
