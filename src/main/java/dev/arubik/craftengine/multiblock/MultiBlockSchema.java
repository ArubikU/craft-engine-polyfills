package dev.arubik.craftengine.multiblock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockSchema {

    /**
     * Test for one cell of a structure.
     *
     * <p>
     * Takes the level and position rather than just a {@link BlockState} so a part
     * can be described by vanilla's {@code BlockPredicate}, which matches on state
     * properties and block-entity NBT and therefore needs the world. The old
     * {@code Predicate<BlockState>} form still works through
     * {@link #addPart(int, int, int, Predicate)} — that is what the Java-defined
     * schemas use.
     */
    @FunctionalInterface
    public interface PartMatcher {
        boolean test(Level level, BlockPos pos);
    }

    private final Map<BlockPos, PartMatcher> parts = new HashMap<>();
    private final BlockPos coreOffset;

    public MultiBlockSchema(BlockPos coreOffset) {
        this.coreOffset = coreOffset;
    }

    /** Adds a cell tested against its block state alone. */
    public void addPart(int x, int y, int z, Predicate<BlockState> predicate) {
        addPart(x, y, z, (level, pos) -> predicate.test(level.getBlockState(pos)));
    }

    /** Adds a cell tested against the world, e.g. a vanilla {@code BlockPredicate}. */
    public void addPart(int x, int y, int z, PartMatcher matcher) {
        // A cell sitting on the core is the core itself, not a part of the shell.
        if (x == coreOffset.getX() && y == coreOffset.getY() && z == coreOffset.getZ()) {
            return;
        }
        parts.put(new BlockPos(x, y, z), matcher);
    }

    public Map<BlockPos, PartMatcher> getParts() {
        return parts;
    }

    public BlockPos getCoreOffset() {
        return coreOffset;
    }
}
