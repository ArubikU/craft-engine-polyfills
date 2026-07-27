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

    /**
     * Legacy default capacity, kept so {@link PumpBehavior} can size itself relative
     * to a pipe. Reads the built-in copper tier; per-instance code should call
     * {@link #capacity()} instead, which honours the pipe's actual kind.
     */
    protected static int defaultCapacity() {
        return dev.arubik.craftengine.pipe.PipeType.COPPER.capacity();
    }

    /** Legacy default throughput; see {@link #defaultCapacity()}. */
    protected static int defaultTransferPerTick() {
        return dev.arubik.craftengine.pipe.PipeType.COPPER.transferPerTick();
    }

    protected final BlockDefinition block;

    /**
     * The data-driven kind this pipe is. Held as a live reference rather than
     * copied fields, so retuning it in {@code pipe_types/*.json} takes effect
     * without reconstructing the behavior.
     */
    protected final dev.arubik.craftengine.pipe.PipeType pipeType;

    // Round-robin caching: guardar última dirección exitosa por posición
    private final java.util.Map<Long, Integer> lastSuccessfulDirection = new java.util.concurrent.ConcurrentHashMap<>();

    public PipeBehavior(BlockDefinition block) {
        this(block, dev.arubik.craftengine.pipe.PipeType.COPPER);
    }

    public PipeBehavior(BlockDefinition block, dev.arubik.craftengine.pipe.PipeType pipeType) {
        // NOTE: cml:iron_pump is intentionally NOT in the connect set. The pump is now a
        // full machine with a directional FLUID IO config (in=DOWN/out=UP, facing-relative), so the
        // pipe must connect to it ONLY through the face-aware carrierConnectsHere() path — which
        // honors the pump's actual IN/OUT world faces — not via an all-sides custom-block match.
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(pipeType.connectsTo()), true);
        this.block = block;
        this.pipeType = pipeType;
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
    }

    /** mB this pipe segment buffers, from its {@link dev.arubik.craftengine.pipe.PipeType}. */
    protected int capacity() {
        return pipeType.capacity();
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
        // Buffer size is per pipe kind now. The conservative solver drains a pipe toward
        // equilibrium (≈0 when a tank below has room); full 0-residual at rest would come
        // from edge-contraction (roadmap Phase 1b).
        return capacity();
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction direction) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.insertFluid(level, pos, stack, capacity(), 0, direction);
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
            // `pipe_type: polyfills:copper` in the block config selects the data-driven
            // tier; unset or unknown falls back to copper so existing packs keep working.
            dev.arubik.craftengine.pipe.PipeType type = null;
            Object configured = args == null ? null : args.get("pipe_type");
            if (configured != null)
                type = dev.arubik.craftengine.pipe.PipeType.byName(String.valueOf(configured));
            if (type == null)
                type = dev.arubik.craftengine.pipe.PipeType.byBlockId(block.id());
            if (type == null)
                type = dev.arubik.craftengine.pipe.PipeType.COPPER;
            return new PipeBehavior(block, type);
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
            player.getBukkitEntity().sendActionBar(TankBlockBehavior.fluidInfo(stored, capacity(), pos.getY()));
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
