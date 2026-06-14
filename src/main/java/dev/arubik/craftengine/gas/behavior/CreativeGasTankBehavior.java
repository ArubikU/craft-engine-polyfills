package dev.arubik.craftengine.gas.behavior;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * CreativeGasTankBehavior: an INFINITE STEAM source for testing gas machinery
 * (e.g. powering the vapor motor).
 *
 * <p>It always reports a full STEAM tank, never depletes on extract, and silently
 * swallows anything inserted (creative void + source). A per-tick block-entity
 * ticker actively pushes STEAM into each of the 6 neighbouring gas carriers via
 * {@link GasTransferHelper#transfer(Level, BlockPos, BlockPos, int, int)} so that
 * downstream consumers never starve.</p>
 *
 * <p>No gas-type / level block properties are required. It reuses the
 * {@link ConnectableBlockBehavior} machinery only for placement/connection; the
 * direction properties are optional and resolved defensively (may be null).</p>
 */
public class CreativeGasTankBehavior extends ConnectableBlockBehavior implements EntityBlock, GasCarrier {

    public static final Key FACTORY_KEY = Key.of("polyfills:creative_gas_tank");
    public static final Factory FACTORY = new Factory();

    /** Large amount so the source is effectively infinite and never starves a motor. */
    public static final int LARGE_AMOUNT = 100000; // mb

    /** Gas type this creative tank emits. */
    public static final GasType GAS = GasType.STEAM;

    public CreativeGasTankBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(Direction.UP, Direction.DOWN),
                horizontalDirectionProperty, verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;
            try {
                Object h = arguments.getOrDefault("horizontal", null);
                if (h instanceof String hs) {
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(hs);
                }
            } catch (ClassCastException ignored) {
            }
            try {
                vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                        .getProperty("vertical");
            } catch (ClassCastException ignored) {
            }
            return new CreativeGasTankBehavior(block, hProp, vProp);
        }
    }

    // ---------------- GasCarrier: infinite STEAM source ----------------

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        // Always full of STEAM.
        return new GasStack(GAS, LARGE_AMOUNT, 0);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, Direction side) {
        // Creative void: accept (and discard) anything.
        if (stack == null || stack.isEmpty())
            return 0;
        return stack.getAmount();
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained,
            Direction side) {
        if (max <= 0)
            return 0;
        // Never depletes: always hand out STEAM up to `max`.
        if (drained != null)
            drained.accept(new GasStack(GAS, max, 0));
        return max;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    // ---------------- EntityBlock + ticker: push into neighbours ----------------

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new Controller(blockEntity, this);
    }

    /** Ticking controller that pushes STEAM into the 6 neighbours every tick. */
    public static class Controller extends BlockEntityController {
        private final CreativeGasTankBehavior behavior;

        public Controller(BlockEntity blockEntity, CreativeGasTankBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
                CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper((BlockEntityTicker<Controller>) Controller::tick);
        }

        public static void tick(CEWorld world,
                net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, Controller self) {
            self.behavior.pushToNeighbours(world, cePos);
        }
    }

    protected void pushToNeighbours(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        Level level = (Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        BlockPos pos = BlockPos.of(cePos.asLong());
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = GasTransferHelper.offset(pos, dir);
            // Resolves a GasCarrier at the neighbour; no-op if there isn't one.
            GasTransferHelper.transfer(level, pos, neighbour, LARGE_AMOUNT, 0);
        }
    }
}
