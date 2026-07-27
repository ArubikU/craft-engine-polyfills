package dev.arubik.craftengine.gas.behavior;

import java.util.HashSet;
import java.util.function.Consumer;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
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
import dev.arubik.craftengine.util.Utils;

public class GasPipeBehavior extends ConnectedBlockBehavior implements EntityBlock, GasCarrier {

    public static final Factory FACTORY = new Factory();

    /**
     * Legacy default capacity, kept so {@link dev.arubik.craftengine.gas.behavior.GasPumpBehavior}
     * can size itself relative to a pipe. Reads the built-in steel tier; per-instance
     * code should call {@link #capacity()}.
     */
    protected static int defaultCapacity() {
        return dev.arubik.craftengine.pipe.PipeType.STEEL.capacity();
    }

    /** Legacy default throughput; see {@link #defaultCapacity()}. */
    protected static int defaultTransferPerTick() {
        return dev.arubik.craftengine.pipe.PipeType.STEEL.transferPerTick();
    }

    protected final BlockDefinition block;

    /** The data-driven kind this pipe is; held live so retuning takes effect. */
    protected final dev.arubik.craftengine.pipe.PipeType pipeType;

    // Round-robin caching: guardar última dirección exitosa por posición
    private final java.util.Map<Long, Integer> lastSuccessfulDirection = new java.util.concurrent.ConcurrentHashMap<>();

    public GasPipeBehavior(BlockDefinition block) {
        this(block, dev.arubik.craftengine.pipe.PipeType.STEEL);
    }

    public GasPipeBehavior(BlockDefinition block, dev.arubik.craftengine.pipe.PipeType pipeType) {
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(pipeType.connectsTo()), true);
        this.block = block;
        this.pipeType = pipeType;
        // Gas connects on every face (no gravity/direction). This list drives canConnectTo, which the
        // hydraulic GasEngine uses to build the network — it was empty, so NOTHING connected (every gas
        // node came out isolated). Mirror the fluid PipeBehavior.
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
    }

    /** mB this pipe segment buffers, from its {@link dev.arubik.craftengine.pipe.PipeType}. */
    protected int capacity() {
        return pipeType.capacity();
    }

    /** mB/tick this pipe moves, from its {@link dev.arubik.craftengine.pipe.PipeType}. */
    protected int transferPerTick() {
        return pipeType.transferPerTick();
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

    /** Controller carrying this gas pipe's sync ticking, backed by persistent data. */
    public static class Controller extends PersistentBlockEntity {
        private final GasPipeBehavior behavior;

        public Controller(BlockEntity blockEntity, GasPipeBehavior behavior) {
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
        // OLD per-block gas pipe transport DELETED (roadmap Phase 5). The hydraulic engine (GasEngine) is
        // the single gas transport; this pipe just registers its network so the engine equalizes it.
        Level level = (Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        dev.arubik.craftengine.fluid.graph.GasEngine.registerSeed(BlockPos.of(cePos.asLong()));
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        return PersistentBlockEntity.getIfLoaded(level, pos);
    }

    protected void withBE(Level level, BlockPos pos, Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity.executeAt(level, pos, consumer);
    }

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        // MUST read the same store insertGas/extractGas write to (the block entity's own tag via
        // GasCarrierImpl) — they used to diverge from a separate chunk-PDC store, which silently lost gas.
        return GasCarrierImpl.getStoredGas(level, pos, GasKeys.GAS);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction direction) {
        return GasCarrierImpl.insertGas(level, pos, stack, capacity(), GasKeys.GAS);
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained,
            net.minecraft.core.Direction direction) {
        return GasCarrierImpl.extractGas(level, pos, max, drained, GasKeys.GAS);
    }

    protected boolean isCompatibleBehavior(Object behavior) {
        return behavior instanceof GasCarrier;
    }

    @Override
    protected Class<?> carrierClass() {
        return GasCarrier.class;
    }

    @Override
    protected dev.arubik.craftengine.multiblock.IOConfiguration.IOType carrierIOType() {
        return dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS;
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

    private boolean tryTransfer(Level level, BlockPos from, Direction dir, TransferAction action) {
        if (!isConnected(dir, from, level))
            return false;

        BlockPos targetPos = offset(from, dir);
        BlockState targetState = level.getBlockState(targetPos);

        // Check neighbor IO Configuration if available. Unwrap Composite/Dual wrappers so machines
        // and multiblocks are still seen as GasCarriers.
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        GasCarrier targetCarrier = customOpt
                .map(cs -> {
                    BlockBehavior b = cs.behavior();
                    if (b instanceof GasCarrier fc)
                        return fc;
                    return b == null ? null : b.getFirst(GasCarrier.class);
                })
                .orElse(null);

        net.minecraft.core.Direction fromTarget = Utils.oppositeDirection(dir);

        if (customOpt.isPresent()) {
            BlockBehavior raw = customOpt.get().behavior();
            dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior connectable =
                    raw instanceof dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior c ? c
                            : (raw == null ? null
                                    : raw.getFirst(dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior.class));
            if (connectable != null) {
                IOConfiguration targetConfig = connectable.getIOConfiguration(level, targetPos);
                Direction targetLocalDir = connectable.toLocalDirection(fromTarget, targetState);

                if (action == TransferAction.PUSH) {
                    // Pushing TO target, so target must accept INPUT from us
                    if (!targetConfig.acceptsInput(IOConfiguration.IOType.GAS, targetLocalDir))
                        return false;
                } else if (action == TransferAction.PUMP) {
                    // Pumping FROM target, so target must provide OUTPUT to us
                    if (!targetConfig.providesOutput(IOConfiguration.IOType.GAS, targetLocalDir))
                        return false;
                }
            }
        }

        GasStack stored = getStoredGas(level, from);
        int transferRate = transferPerTick(); // Default rate for pipes

        switch (action) {
            case PUMP: {
                int inputRate = transferRate;

                // Only PUMP (suck) from a real SOURCE (tank/machine), never from another PIPE — pipe
                // <-> pipe must use PUSH/HOMOGENIZE. Otherwise every pipe in a column sucks the one
                // below it, so gas always migrates UP and can never descend.
                if (targetCarrier instanceof GasPipeBehavior)
                    return false;
                // Intentar extraer de un carrier si permite extracción general
                if (targetCarrier != null && targetCarrier
                        .getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                    GasStack theirStored = targetCarrier.getStoredGas(level, targetPos);
                    if (!theirStored.isEmpty()) {
                        // Only pull what THIS pipe can actually hold — never extract into a full or
                        // type-incompatible pipe (the source is output-only and would lose the gas).
                        GasStack pipeStored = getStoredGas(level, from);
                        int free;
                        if (pipeStored.isEmpty())
                            free = capacity();
                        else if (pipeStored.getType() == theirStored.getType())
                            free = capacity() - pipeStored.getAmount();
                        else
                            free = 0;
                        if (free <= 0)
                            return false;
                        int move = Math.min(Math.min(inputRate, theirStored.getAmount()), free);
                        final GasStack[] extracted = { null };
                        int actually = targetCarrier.extractGas(level, targetPos, move, f -> extracted[0] = f,
                                fromTarget);
                        if (actually > 0 && extracted[0] != null) {
                            // Decaimiento de presión por salto
                            GasStack toInsert = new GasStack(extracted[0].getType(), actually,
                                    Math.max(0, extracted[0].getPressure() - 1));
                            int accepted = insertGas(level, from, toInsert, null);
                            if (accepted < actually) {
                                // devolver resto
                                GasStack remainder = new GasStack(extracted[0].getType(), actually - accepted,
                                        extracted[0].getPressure());
                                targetCarrier.insertGas(level, targetPos, remainder, fromTarget);
                            }
                            return accepted > 0;
                        }
                    }
                }
                return false;
            }
            case PUSH: {
                if (targetCarrier == null)
                    return false;
                // PUSH only feeds real CONSUMERS (machines/tanks). Pipe<->pipe is handled by
                // HOMOGENIZE, so don't push into another pipe (avoids oscillation/loops).
                if (targetCarrier instanceof GasPipeBehavior)
                    return false;
                if (stored.isEmpty())
                    return false;

                int move = Math.min(transferRate, stored.getAmount());
                if (move <= 0)
                    return false;
                // (Gas may now flow DOWN even without pressure — it's tried LAST, after up/horizontal,
                // so it still rises preferentially but isn't trapped when the only outlet is below.)

                // Loop detection: verificar historial de transferencias
                PersistentBlockEntity pbe = getBE(level, from);
                if (pbe != null) {
                    // Assuming GasKeys has TRANSFER_HISTORY or similar, or reusing standard method
                    String history = pbe.getOrDefault(GasKeys.TRANSFER_HISTORY, "");
                    if (GasTransferHelper.wouldCreateLoop(history, targetPos)) {
                        // Loop detectado! Aplicar penalización de cooldown
                        pbe.set(GasKeys.GAS_IO_COOLDOWN, 20);
                        return false;
                    }
                }

                int pressure = stored.getPressure();
                GasStack toTransfer = new GasStack(stored.getType(), move,
                        Math.max(0, pressure - 1));

                int accepted = targetCarrier.insertGas(level, targetPos, toTransfer, fromTarget);

                if (accepted > 0) {
                    // Remove what we sent from our own block-entity store (same place insert/extract use).
                    extractGas(level, from, accepted, null, fromTarget);

                    // Actualizar historial después de transferencia exitosa
                    if (pbe != null) {
                        String history = pbe.getOrDefault(GasKeys.TRANSFER_HISTORY, "");
                        String newHistory = GasTransferHelper.updateHistory(history, targetPos);
                        pbe.set(GasKeys.TRANSFER_HISTORY, newHistory);
                    }
                    return true;
                }
                return false;
            }
            case HOMOGENIZE: {
                if (targetCarrier == null)
                    return false;
                // Only equalize pipe<->pipe (no pressure model). Vertical IS allowed now, so gas
                // spreads up/down and around corners instead of getting stuck on the corner pipe.
                if (!(targetCarrier instanceof GasPipeBehavior))
                    return false;

                GasStack a = stored; // este
                GasStack b = targetCarrier.getStoredGas(level, targetPos); // vecino

                // Determinar tipo de fluido a equilibrar
                GasType type;
                if (!a.isEmpty() && !b.isEmpty() && a.getType() != b.getType())
                    return false; // no mezclar tipos distintos
                if (a.isEmpty() && b.isEmpty())
                    return false;
                type = !a.isEmpty() ? a.getType() : b.getType();

                int amountA = a.isEmpty() ? 0 : a.getAmount();
                int amountB = b.isEmpty() ? 0 : b.getAmount();
                int diff = amountA - amountB;

                // Dead zone: evitar micro-transferencias que causan oscilación
                if (Math.abs(diff) < 10)
                    return false;

                if (diff == 0)
                    return false; // ya equilibrado

                int move = Math.min(transferRate, Math.abs(diff) / 2 + (Math.abs(diff) % 2)); // tender al
                                                                                              // equilibrio

                if (diff > 0) {
                    // Nosotros tenemos más: empujar al vecino
                    int available = Math.min(move, amountA);
                    if (available <= 0)
                        return false;
                    int appliedPressure = Math.max(0,
                            ((a.isEmpty() ? 0 : a.getPressure()) + (b.isEmpty() ? 0 : b.getPressure())) / 2);
                    GasStack toSend = new GasStack(type, available, appliedPressure);
                    int accepted = targetCarrier.insertGas(level, targetPos, toSend, fromTarget);
                    if (accepted > 0) {
                        extractGas(level, from, accepted, f -> {
                        }, null);
                        return true;
                    }
                } else {
                    // Vecino tiene más: intentar tirar de él si su modo lo permite
                    if (targetCarrier
                            .getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                        int need = Math.min(move, amountB);
                        final GasStack[] extracted = { null };
                        int actually = targetCarrier.extractGas(level, targetPos, need, f -> extracted[0] = f,
                                fromTarget);
                        if (actually > 0 && extracted[0] != null) {
                            GasStack toInsert = new GasStack(type, actually,
                                    Math.max(0, a.getPressure()));
                            int accepted = insertGas(level, from, toInsert, null);
                            if (accepted < actually) {
                                GasStack remainder = new GasStack(type, actually - accepted,
                                        extracted[0].getPressure());
                                targetCarrier.insertGas(level, targetPos, remainder, fromTarget);
                            }
                            return accepted > 0;
                        }
                    }
                }
                return false;
            }
            default:
                return false;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            // `pipe_type: polyfills:steel` selects the data-driven tier; unset or unknown
            // falls back to steel so existing packs keep working.
            dev.arubik.craftengine.pipe.PipeType type = null;
            Object configured = args == null ? null : args.get("pipe_type");
            if (configured != null)
                type = dev.arubik.craftengine.pipe.PipeType.byName(String.valueOf(configured));
            if (type == null)
                type = dev.arubik.craftengine.pipe.PipeType.byBlockId(block.id());
            if (type == null)
                type = dev.arubik.craftengine.pipe.PipeType.STEEL;
            return new GasPipeBehavior(block, type);
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

        GasStack stored = getStoredGas(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            player.getBukkitEntity().sendActionBar(gasInfo(stored, capacity()));
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    /** Shared shift-click readout for gas pipes/tanks: i18n gas name + amount/cap + fill%. Gas has NO
     * head/lift (it equalizes freely, no gravity) — so no "lift"/pressure field, unlike the fluid readout. */
    public static Component gasInfo(GasStack stored, int cap) {
        int amt = (stored == null || stored.isEmpty()) ? 0 : stored.getAmount();
        String key = (stored == null || stored.isEmpty()) ? "gas.minecraft.empty"
                : "gas.minecraft." + stored.getType().toString().toLowerCase();
        double fill = cap > 0 ? amt / (double) cap : 0;
        return MiniMessage.miniMessage().deserialize(
                "<lang:" + key + "> <gray>" + amt + "/" + cap + " mB</gray> "
                        + "<dark_gray>·</dark_gray> <yellow>" + (int) Math.round(fill * 100) + "%</yellow>");
    }
}
