package dev.arubik.craftengine.multiblock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockSchema {
    private final Map<BlockPos, Predicate<BlockState>> parts = new HashMap<>();
    private final BlockPos coreOffset;

    public MultiBlockSchema(BlockPos coreOffset) {
        this.coreOffset = coreOffset;
    }

    public void addPart(int x, int y, int z, Predicate<BlockState> predicate) {

        // if its on the core offset ignore
        if (x == coreOffset.getX() && y == coreOffset.getY() && z == coreOffset.getZ()) {
            return;
        }

        parts.put(new BlockPos(x, y, z), predicate);
    }

    public Map<BlockPos, Predicate<BlockState>> getParts() {
        return parts;
    }

    public BlockPos getCoreOffset() {
        return coreOffset;
    }
}
