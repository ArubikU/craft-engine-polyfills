package dev.arubik.craftengine.fluid.behavior;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.minecraft.core.Direction;

/**
 * PumpBehavior: bloque conectable que añade presión al fluido almacenado.
 * Reimplementa mínima lógica de transferencia usando composición con la
 * interfaz FluidCarrier.
 */
public class PumpBehavior extends ConnectableBlockBehavior
        implements FluidCarrier, net.momirealms.craftengine.core.block.behavior.EntityBlock {
    public static final Factory FACTORY = new Factory();
    /** Pressure the pump stamps onto pumped fluid — config `pressure` (default 8). Higher = lifts farther. */
    private final int pressureBoost;

    protected static final int CAPACITY = PipeBehavior.CAPACITY; // uniformidad
    protected static final int TRANSFER_PER_TICK = PipeBehavior.TRANSFER_PER_TICK * 10;

    public PumpBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        this(block, horizontalDirectionProperty, verticalDirectionProperty, 8);
    }

    public PumpBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            int pressureBoost) {
        super(block, java.util.List.of(net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN),
                horizontalDirectionProperty,
                verticalDirectionProperty);
        this.pressureBoost = pressureBoost;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public net.momirealms.craftengine.core.block.behavior.BlockBehavior create(BlockDefinition block,
                ConfigSection arguments) {

            // Leer las propiedades de dirección desde los argumentos
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;

            // Aceptar claves alternativas comunes
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
                    // Property type mismatch, keep as null
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp2 = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                    vProp = tmp2;
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            Object p = arguments.getOrDefault("pressure", arguments.get("pressure-boost"));
            int pressureBoost = p instanceof Number ? ((Number) p).intValue() : 8;
            return new PumpBehavior(block, hProp, vProp, pressureBoost);
        }
    }

    // --- Block Entity infra ---
    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new Controller(blockEntity, this);
    }

    /** Controller carrying this pump's sync ticking, backed by persistent data. */
    public static class Controller extends PersistentBlockEntity {
        private final PumpBehavior behavior;

        public Controller(BlockEntity blockEntity, PumpBehavior behavior) {
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
        // OLD pump transport DELETED — the hydraulic engine (FluidEngine) is the sole fluid transport.
    }



    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    private void withBE(Level level, BlockPos pos, java.util.function.Consumer<PersistentBlockEntity> c) {
        PersistentBlockEntity p = getBE(level, pos);
        if (p != null)
            c.accept(p);
    }

    public FluidStack getStored(Level level, BlockPos pos) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.getStored(level, pos);
    }

    public int insertFluid(Level level, BlockPos pos, FluidStack stack) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.insertFluid(level, pos, stack, CAPACITY, pressureBoost);
    }

    public int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.extractFluid(level, pos, max, drained);
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

        FluidStack stored = getStored(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            String fluidName = stored.isEmpty() ? "fluid.minecraft.empty"
                    : "fluid.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + fluidName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side) {
        return insertFluid(level, pos, stack);
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained,
            net.minecraft.core.Direction side) {
        return extractFluid(level, pos, max, drained);
    }
}
