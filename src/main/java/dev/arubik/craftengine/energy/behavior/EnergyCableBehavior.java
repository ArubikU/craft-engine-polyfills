package dev.arubik.craftengine.energy.behavior;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.energy.EnergyCarrier;
import dev.arubik.craftengine.energy.EnergyCarrierImpl;
import dev.arubik.craftengine.fluid.graph.EnergyEngine;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.pipe.PipeType;
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
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * A CraftEnergy cable segment: a data-driven {@link PipeType} tier (resource {@code energy}),
 * mirroring {@link dev.arubik.craftengine.gas.behavior.GasPipeBehavior} exactly. Transport is
 * entirely the {@link EnergyEngine} network solve — this behavior just registers the seed each
 * tick and exposes the shared {@link dev.arubik.craftengine.energy.EnergyKeys#ENERGY} buffer as
 * an {@link EnergyCarrier} node.
 */
public class EnergyCableBehavior extends ConnectedBlockBehavior implements EntityBlock, EnergyCarrier {

    public static final Factory FACTORY = new Factory();

    protected final BlockDefinition block;
    protected final PipeType pipeType;

    public EnergyCableBehavior(BlockDefinition block, PipeType pipeType) {
        super(block, new java.util.ArrayList<>(), new java.util.HashSet<>(),
                new java.util.HashSet<>(pipeType.connectsTo()), true);
        this.block = block;
        this.pipeType = pipeType;
        // Energy has no gravity/direction bias — connects on every face.
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
    }

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

    public static class Controller extends PersistentBlockEntity {
        private final EnergyCableBehavior behavior;

        public Controller(BlockEntity blockEntity, EnergyCableBehavior behavior) {
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
            Level level = (Level) world.world().minecraftWorld();
            if (level == null || level.isClientSide())
                return;
            EnergyEngine.registerSeed(BlockPos.of(cePos.asLong()));
        }
    }

    // ---------------- EnergyCarrier: plain conduit buffer ----------------

    @Override
    public int getStoredEnergy(Level level, BlockPos pos) {
        return EnergyCarrierImpl.getStoredEnergy(level, pos);
    }

    @Override
    public int insertEnergy(Level level, BlockPos pos, int amount, Direction side) {
        return EnergyCarrierImpl.insertEnergy(level, pos, amount, capacity());
    }

    @Override
    public int extractEnergy(Level level, BlockPos pos, int max, Direction side) {
        return EnergyCarrierImpl.extractEnergy(level, pos, max);
    }

    @Override
    public long getEnergyCapacity(Level level, BlockPos pos) {
        return capacity();
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    @Override
    protected Class<?> carrierClass() {
        return EnergyCarrier.class;
    }

    @Override
    protected IOConfiguration.IOType carrierIOType() {
        return IOConfiguration.IOType.ENERGY;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            PipeType type = null;
            Object configured = args == null ? null : args.get("pipe_type");
            if (configured != null)
                type = PipeType.byName(String.valueOf(configured));
            if (type == null)
                type = PipeType.byBlockId(block.id());
            if (type == null)
                throw new IllegalStateException(
                        "energy_cable_block '" + block.id() + "' has no matching pipe_types/*.json entry"
                                + " (set behavior.pipe_type or bind a pipe_types block id to it)");
            return new EnergyCableBehavior(block, type);
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
        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            int stored = getStoredEnergy(level, pos);
            player.getBukkitEntity().sendActionBar(energyInfo(stored, capacity()));
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    public static Component energyInfo(int stored, int cap) {
        double fill = cap > 0 ? stored / (double) cap : 0;
        return MiniMessage.miniMessage().deserialize(
                "<yellow>⚡</yellow> <gray>" + stored + "/" + cap + " CE</gray> "
                        + "<dark_gray>·</dark_gray> <yellow>" + (int) Math.round(fill * 100) + "%</yellow>");
    }
}
