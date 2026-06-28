package dev.arubik.craftengine.fluid.behavior;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.core.world.CEWorld;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.Utils;

public class PipeBehavior extends ConnectedBlockBehavior implements EntityBlock, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    /** Temporary pressure debug logging. */
    public static final boolean PIPE_DBG = false;
    private static long lastPipeDbg = 0;

    protected static final int CAPACITY = 1000; // mb
    protected static final int TRANSFER_PER_TICK = 100; // mb/tick

    protected final BlockDefinition block;

    // Round-robin caching: guardar última dirección exitosa por posición
    private final java.util.Map<Long, Integer> lastSuccessfulDirection = new java.util.concurrent.ConcurrentHashMap<>();

    public PipeBehavior(BlockDefinition block) {
        // NOTE: cml:iron_pump is intentionally NOT in this blanket connect set. The pump is now a
        // full machine with a directional FLUID IO config (in=DOWN/out=UP, facing-relative), so the
        // pipe must connect to it ONLY through the face-aware carrierConnectsHere() path — which
        // honors the pump's actual IN/OUT world faces — not via an all-sides custom-block match.
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(java.util.Arrays.asList("cml:copper_valve", "cml:copper_tank")), true);
        this.block = block;
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
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

    /** Controller carrying this pipe's sync ticking, backed by persistent data. */
    public static class Controller extends PersistentBlockEntity {
        private final PipeBehavior behavior;

        public Controller(BlockEntity blockEntity, PipeBehavior behavior) {
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
            self.behavior.tickPipe(world, cePos);
        }
    }

    private void tickPipe(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        // OLD per-block pipe transport DELETED (roadmap Phase 4). The hydraulic engine (FluidEngine) is the
        // single fluid transport system; pipes are driven as graph nodes. Pipe BEs no longer push/pull.
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    protected void withBE(Level level, BlockPos pos, Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity p = getBE(level, pos);
        if (p != null)
            consumer.accept(p);
    }

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.getStored(level, pos);
    }


    @Override
    public long getCapacity(Level level, BlockPos pos) {
        // Pipes can buffer up to 1000 mB (high throughput, as originally designed). The conservative
        // solver drains them toward equilibrium (≈0 when a tank below has room). Full 0-residual at rest
        // would come from edge-contraction (roadmap Phase 1b).
        return 1000L;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction direction) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.insertFluid(level, pos, stack, CAPACITY, 0, direction);
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained,
            net.minecraft.core.Direction direction) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.extractFluid(level, pos, max, drained, direction);
    }

    protected boolean isCompatibleBehavior(Object behavior) {
        return behavior instanceof FluidCarrier;
    }

    @Override
    protected Class<?> carrierClass() {
        return FluidCarrier.class;
    }

    @Override
    protected dev.arubik.craftengine.multiblock.IOConfiguration.IOType carrierIOType() {
        return dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
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

    private boolean isConnected(Direction dir, BlockPos self, Level level) {
        return isConnectedTo(dir, self, level);
    }

    // Acciones de transferencia compatibles con bombas/válvulas/carriers
    public enum TransferAction {
        PUMP, PUSH, HOMOGENIZE
    }

    // tryTransfer (old per-block pipe push/pull) DELETED — engine is the sole transport.

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            return new PipeBehavior(block);
        }
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
            player.getBukkitEntity().sendActionBar(TankBlockBehavior.fluidInfo(stored, CAPACITY, pos.getY()));
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
