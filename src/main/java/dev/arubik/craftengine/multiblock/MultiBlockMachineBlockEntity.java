package dev.arubik.craftengine.multiblock;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineController;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Base controller for MultiBlock Machine block entities (ce 26.6.2).
 * Used for CORE blocks in multiblock structures that need full machine
 * capability. Re-based onto BlockEntityController via AbstractMachineController.
 */
public abstract class MultiBlockMachineBlockEntity extends AbstractMachineController {

    protected final MultiBlockSchema schema;

    public MultiBlockMachineBlockEntity(int containerSize, BlockEntity blockEntity, MultiBlockSchema schema) {
        super(containerSize, blockEntity);
        this.schema = schema;
    }

    /**
     * Gets the multiblock schema for this machine.
     */
    public MultiBlockSchema getSchema() {
        return schema;
    }

    /**
     * Get dynamic redstone output based on which part and which side.
     * Override in subclasses to provide custom logic.
     *
     * @param relativePos Position relative to core (BlockPos.ZERO for core itself)
     * @param side        Direction from which redstone is being read
     * @return Signal strength 0-15
     */
    public int getRedstoneOutput(BlockPos relativePos, Direction side) {
        // Default: signal based on processing state
        return isProcessing() ? 15 : 0;
    }

    /**
     * Drive machine ticking through the controller model. Mirrors the legacy
     * behavior-driven sync tick: only CORE machine controllers tick.
     */
    @Override
    public <C extends net.momirealms.craftengine.core.block.entity.BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return net.momirealms.craftengine.core.block.entity.BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<MultiBlockMachineBlockEntity>) MultiBlockMachineBlockEntity::tick);
    }

    public static void tick(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state,
            MultiBlockMachineBlockEntity self) {
        self.tick((Level) world.world.minecraftWorld(),
                dev.arubik.craftengine.util.Utils.fromPos(pos), state);
    }

    /**
     * Multiblock auto-pull: in addition to the CORE's own input faces, pull fluid/gas from a
     * connected carrier on every PART's input face (routed into the core tanks). Driven entirely by
     * each part's IOConfiguration — no hardcoded positions.
     */
    @Override
    protected void pullFromInputFaces(Level level) {
        super.pullFromInputFaces(level);
        if (schema == null)
            return;
        BlockPos core = getMachinePos();
        Direction facing = coreFacing(level);
        dev.arubik.craftengine.multiblock.MultiBlockBehavior beh = coreBehavior();
        for (java.util.Map.Entry<BlockPos, ?> e : schema.getParts().entrySet()) {
            BlockPos rel = e.getKey().subtract(schema.getCoreOffset());
            BlockPos partPos = core.offset(rotate(rel, facing));
            if (partPos.equals(core))
                continue;
            dev.arubik.craftengine.multiblock.IOConfiguration cfg = null;
            var be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, partPos);
            if (be != null && be.controller instanceof MultiBlockPartBlockEntity p)
                cfg = p.getIOConfiguration();
            // Part config not set/persisted -> recompute from the behavior's structure IO provider.
            if (cfg == null && beh != null && beh.getIOProvider() != null)
                cfg = beh.getIOProvider().configurePartIO(rel);
            if (DEBUG_IO)
                System.out.println("[Pull] part rel=" + rel + " pos=" + partPos.toShortString()
                        + " cfg=" + (cfg == null ? "null" : "ok"));
            if (cfg == null)
                continue;
            for (Direction world : Direction.values()) {
                // Parts are placed with default facing, so local == world here.
                BlockPos src = partPos.relative(world);
                Direction sideFromSrc = world.getOpposite();
                if (cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, world))
                    pullFluidInto(level, src, sideFromSrc, cfg.getTargetSlot(
                            dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, world));
                if (cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, world))
                    pullGasInto(level, src, sideFromSrc, cfg.getTargetSlot(
                            dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, world));
            }
        }
    }

    /** The MultiBlockBehavior backing this core (unwrapping the engine's behavior wrapper). */
    private dev.arubik.craftengine.multiblock.MultiBlockBehavior coreBehavior() {
        try {
            var b = blockEntity().blockState().behavior();
            if (b instanceof dev.arubik.craftengine.multiblock.MultiBlockBehavior mb)
                return mb;
            return b == null ? null : b.getFirst(dev.arubik.craftengine.multiblock.MultiBlockBehavior.class);
        } catch (Throwable t) {
            return null;
        }
    }

    private Direction coreFacing(Level level) {
        try {
            var st = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(level.getBlockState(getMachinePos())).orElse(null);
            if (st != null) {
                var prop = st.getProperty("facing");
                if (prop != null) {
                    Object v = st.get(prop);
                    if (v != null)
                        return Direction.valueOf(v.toString().toUpperCase());
                }
            }
        } catch (Throwable ignored) {
        }
        return Direction.NORTH;
    }

    /** Rotate a schema offset by the core's facing (NORTH = identity), matching MultiBlockBehavior. */
    private static BlockPos rotate(BlockPos pos, Direction facing) {
        if (facing == null || facing == Direction.NORTH || facing.getAxis().isVertical())
            return pos;
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return switch (facing) {
            case SOUTH -> new BlockPos(-x, y, -z);
            case WEST -> new BlockPos(z, y, -x);
            case EAST -> new BlockPos(-z, y, x);
            default -> pos;
        };
    }

    /**
     * Abstract methods from AbstractMachineController that must be implemented
     */
    @Override
    public abstract MachineLayout getLayout();

    @Override
    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level level);

    @Override
    protected abstract boolean canFitOutput(Level level, RecipeOutput output);

    @Override
    protected abstract void consumeInputs(Level level, AbstractProcessingRecipe recipe);

    @Override
    protected abstract String getMachineId();
}
