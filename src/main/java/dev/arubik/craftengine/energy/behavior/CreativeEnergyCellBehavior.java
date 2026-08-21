package dev.arubik.craftengine.energy.behavior;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.energy.EnergyCarrier;
import dev.arubik.craftengine.energy.EnergyTransferHelper;
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
 * An INFINITE CraftEnergy source for testing (creative-only, not meant for survival configs) —
 * mirrors {@code CreativeGasTankBehavior}. Always reports a full buffer, never depletes on
 * extract, and swallows anything inserted. Actively pushes into its 6 neighbours every tick so
 * demo setups light up immediately without needing a pre-built cable network.
 */
public class CreativeEnergyCellBehavior extends ConnectableBlockBehavior implements EntityBlock, EnergyCarrier {

    public static final Key FACTORY_KEY = Key.of("polyfills:creative_energy_cell");
    public static final Factory FACTORY = new Factory();

    /** Large amount so the source is effectively infinite. */
    public static final int LARGE_AMOUNT = 1_000_000;

    public CreativeEnergyCellBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(Direction.values()), horizontalDirectionProperty, verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;
            try {
                Object h = arguments.getOrDefault("horizontal", null);
                if (h instanceof String hs)
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(hs);
            } catch (ClassCastException ignored) {
            }
            try {
                vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                        .getProperty("vertical");
            } catch (ClassCastException ignored) {
            }
            return new CreativeEnergyCellBehavior(block, hProp, vProp);
        }
    }

    // ---------------- EnergyCarrier: infinite source ----------------

    @Override
    public int getStoredEnergy(Level level, BlockPos pos) {
        return LARGE_AMOUNT;
    }

    @Override
    public int insertEnergy(Level level, BlockPos pos, int amount, Direction side) {
        return Math.max(0, amount); // creative void: accept (and discard) anything
    }

    @Override
    public int extractEnergy(Level level, BlockPos pos, int max, Direction side) {
        return Math.max(0, max); // never depletes
    }

    @Override
    public long getEnergyCapacity(Level level, BlockPos pos) {
        return LARGE_AMOUNT;
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

    public static class Controller extends BlockEntityController {
        private final CreativeEnergyCellBehavior behavior;

        public Controller(BlockEntity blockEntity, CreativeEnergyCellBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
                CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper((BlockEntityTicker<Controller>) Controller::tick);
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, Controller self) {
            self.behavior.pushToNeighbours(world, cePos);
        }
    }

    protected void pushToNeighbours(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        Level level = (Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        BlockPos pos = BlockPos.of(cePos.asLong());
        for (Direction dir : Direction.values())
            EnergyTransferHelper.transfer(level, pos, EnergyTransferHelper.offset(pos, dir), LARGE_AMOUNT);
    }
}
