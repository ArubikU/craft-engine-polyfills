package dev.arubik.craftengine.gas.behavior;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasCarrierImpl;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * GasPumpBehavior: bloque conectable que añade presión al gas almacenado.
 */
public class GasPumpBehavior extends ConnectableBlockBehavior
        implements GasCarrier, net.momirealms.craftengine.core.block.behavior.EntityBlock {
    public static final Factory FACTORY = new Factory();
    private static final int PRESSURE_BOOST = 8;

    // Sized relative to a plain steel pipe, which is itself data-driven now.
    protected static final int CAPACITY = GasPipeBehavior.defaultCapacity();
    protected static final int TRANSFER_PER_TICK = GasPipeBehavior.defaultTransferPerTick() * 10;

    public GasPumpBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        // Gas has no direction — connect on all 6 faces so the engine can push the pumped gas into a pipe
        // on ANY side (was UP/DOWN only, which left side-placed pipes unconnected).
        super(block, java.util.Arrays.asList(net.minecraft.core.Direction.values()),
                horizontalDirectionProperty,
                verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;

            Object horizontalProp = arguments.getOrDefault("horizontal-direction-property",
                    arguments.getOrDefault("horizontal_facing",
                            arguments.getOrDefault("facing", arguments.get("horizontal"))));
            if (horizontalProp instanceof String) {
                horizontalDirectionProperty = (String) horizontalProp;
            }

            Object verticalProp = arguments.getOrDefault("vertical-direction-property",
                    arguments.getOrDefault("vertical_facing",
                            arguments.getOrDefault("direction", arguments.get("vertical"))));
            if (verticalProp instanceof String) {
                verticalDirectionProperty = (String) verticalProp;
            }

            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(horizontalDirectionProperty);
                    hProp = tmp;
                } catch (ClassCastException ignored) {
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp2 = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                    vProp = tmp2;
                } catch (ClassCastException ignored) {
                }
            }
            return new GasPumpBehavior(block, hProp, vProp);
        }
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new Controller(blockEntity, this);
    }

    /** Controller carrying this gas pump's sync ticking, backed by persistent data. */
    public static class Controller extends PersistentBlockEntity {
        private final GasPumpBehavior behavior;

        public Controller(BlockEntity blockEntity, GasPumpBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
                CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper((BlockEntityTicker<Controller>) Controller::tick);
        }

        public static void tick(CEWorld world,
                net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, Controller self) {
            self.behavior.tickPump(world, cePos);
        }
    }

    protected void tickPump(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        // OLD per-block gas pump transport DELETED (roadmap Phase 5). The hydraulic engine (GasEngine) is
        // the sole gas transport; this block just registers its network so the engine equalizes it.
        net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        dev.arubik.craftengine.fluid.graph.GasEngine.registerSeed(BlockPos.of(cePos.asLong()));
    }

    // Reuse logic structure but adapted for Gas

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    public GasStack getStoredGas(Level level, BlockPos pos) {
        return GasCarrierImpl.getStoredGas(level, pos, GasKeys.GAS); // Need to expose/copy impl
    }

    public int insertGas(Level level, BlockPos pos, GasStack stack, Direction side) {
        // Delegate to Impl using generic keys or specific method
        return GasCarrierImpl.insertGas(level, pos, stack, CAPACITY, GasKeys.GAS);
    }

    public int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained,
            Direction side) {
        return GasCarrierImpl.extractGas(level, pos, max, drained, GasKeys.GAS);
    }

    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.PUMP_VALVE_CAN_TAKE;
    }

    private BlockPos offset(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case UP -> pos.above();
            case DOWN -> pos.below();
        };
    }

    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {
        // Similar display logic
        Level level = (Level) context.getLevel().minecraftWorld();
        BlockPos pos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
        BukkitServerPlayer bplayer = (BukkitServerPlayer) context.getPlayer();
        Player player = (Player) bplayer.serverPlayer();
        InteractionHand hand = context.getHand().equals(
                net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;

        if (level.isClientSide())
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        GasStack stored = getStoredGas(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            String gasName = stored.isEmpty() ? "gas.minecraft.empty"
                    : "gas.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + gasName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
