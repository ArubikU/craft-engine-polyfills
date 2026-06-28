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
        if (dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED)
            return; // hydraulic engine owns transport when enabled
        {
            Level level = (Level) world.world().minecraftWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos mcPos = BlockPos.of(cePos.asLong());
            FluidStack stored = getStored(level, mcPos);
            if (PIPE_DBG && !stored.isEmpty() && System.currentTimeMillis() - lastPipeDbg > 1000) {
                lastPipeDbg = System.currentTimeMillis();
                dev.arubik.craftengine.CraftEnginePolyfills.log("[PipeDBG] @" + mcPos.getX() + "," + mcPos.getY()
                        + "," + mcPos.getZ() + " " + stored.getType() + " amt=" + stored.getAmount()
                        + " pressure=" + stored.getPressure());
            }
            if (stored.getPressure() <= 0) {
                tryTransfer(level, mcPos, Direction.UP, TransferAction.PUMP);
                stored = getStored(level, mcPos);
                if (!stored.isEmpty()) {
                    // Round-robin: empezar desde última dirección exitosa
                    Direction[] pushDirs = { Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST,
                            Direction.WEST };
                    int startIdx = lastSuccessfulDirection.getOrDefault(mcPos.asLong(), 0);

                    for (int i = 0; i < pushDirs.length; i++) {
                        Direction dir = pushDirs[(startIdx + i) % pushDirs.length];
                        if (tryTransfer(level, mcPos, dir, TransferAction.PUSH)) {
                            lastSuccessfulDirection.put(mcPos.asLong(), (startIdx + i) % pushDirs.length);
                            return;
                        }
                    }
                }
                // Homogenize HORIZONTALLY only. Vertical equalize would PULL fluid back UP out of the
                // pipe below, fighting gravity (the bug: "homogeniza pero no baja"). Downward flow is the
                // PUSH DOWN above (gravity); upward flow needs pressure (PUSH UP in the pressure branch).
                Direction[] homogDirs = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
                int startIdx = lastSuccessfulDirection.getOrDefault(mcPos.asLong(), 0) % homogDirs.length;

                for (int i = 0; i < homogDirs.length; i++) {
                    Direction dir = homogDirs[(startIdx + i) % homogDirs.length];
                    if (tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE)) {
                        lastSuccessfulDirection.put(mcPos.asLong(), (startIdx + i) % homogDirs.length);
                    }
                }
            } else {
                if (tryTransfer(level, mcPos, Direction.UP, TransferAction.PUSH))
                    return;

                // Homogenize over ALL faces (vertical too) so a pressurized column fills evenly.
                // NOTE: no more PUSH DOWN here — it was draining the upper pipes back down.
                Direction[] homogDirs = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST,
                        Direction.UP, Direction.DOWN };
                int startIdx = lastSuccessfulDirection.getOrDefault(mcPos.asLong(), 0) % homogDirs.length;

                for (int i = 0; i < homogDirs.length; i++) {
                    Direction dir = homogDirs[(startIdx + i) % homogDirs.length];
                    if (tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE)) {
                        lastSuccessfulDirection.put(mcPos.asLong(), (startIdx + i) % homogDirs.length);
                    }
                }
            }
        }
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

    private boolean tryTransfer(Level level, BlockPos from, Direction dir, TransferAction action) {
        if (!isConnected(dir, from, level))
            return false;

        // Pipe is a conduit, it essentially has "Open" IO, so we skip checking our own
        // IOConfiguration
        // per user request.

        BlockPos targetPos = offset(from, dir);
        BlockState targetState = level.getBlockState(targetPos);

        // Check neighbor IO Configuration if available. Unwrap Composite/Dual wrappers so machines
        // and multiblocks (whose behavior is wrapped by the engine) are still seen as FluidCarriers.
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        FluidCarrier targetCarrier = customOpt
                .map(cs -> {
                    BlockBehavior b = cs.behavior();
                    if (b instanceof FluidCarrier fc)
                        return fc;
                    return b == null ? null : b.getFirst(FluidCarrier.class);
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
                    if (!targetConfig.acceptsInput(IOConfiguration.IOType.FLUID, targetLocalDir))
                        return false;
                } else if (action == TransferAction.PUMP) {
                    // Pumping FROM target, so target must provide OUTPUT to us
                    if (!targetConfig.providesOutput(IOConfiguration.IOType.FLUID, targetLocalDir))
                        return false;
                }
            }
        }

        FluidStack stored = getStored(level, from);
        int transferRate = TRANSFER_PER_TICK; // Default rate for pipes

        switch (action) {
            case PUMP: {
                int inputRate = transferRate;

                // Intentar recoger de fluidos del mundo en el target
                FluidStack collected = FluidType.collectAt(targetPos, level, inputRate, stored.getType());
                if (!collected.isEmpty()) {
                    int accepted = insertFluid(level, from, collected, null); // Internal fill? Pipe fills itself via
                                                                              // pump
                    if (accepted > 0)
                        return true;
                }
                // Intentar extraer de un carrier si permite extracción general
                if (targetCarrier != null && targetCarrier
                        .getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                    FluidStack theirStored = targetCarrier.getStored(level, targetPos);
                    if (!theirStored.isEmpty()) {
                        // Only pull what THIS pipe can actually hold — never extract into a full or
                        // type-incompatible pipe, or the extracted fluid is lost (the source can't
                        // always take the remainder back). Fixes water vanishing from a tank tower.
                        FluidStack pipeStored = getStored(level, from);
                        int free;
                        if (pipeStored.isEmpty())
                            free = CAPACITY;
                        else if (pipeStored.getType() == theirStored.getType())
                            free = CAPACITY - pipeStored.getAmount();
                        else
                            free = 0;
                        if (free <= 0)
                            return false;
                        int move = Math.min(Math.min(inputRate, theirStored.getAmount()), free);
                        final FluidStack[] extracted = { null };
                        int actually = FluidType.extractFromCarrier(targetCarrier, level, targetPos, move,
                                f -> extracted[0] = f, fromTarget); // Extract from target, passing side
                        if (actually > 0 && extracted[0] != null) {
                            // Decaimiento de presión por salto
                            FluidStack toInsert = new FluidStack(extracted[0].getType(), actually,
                                    Math.max(0, extracted[0].getPressure() - 1));
                            int accepted = insertFluid(level, from, toInsert, null);
                            if (accepted < actually) {
                                // devolver resto
                                FluidStack remainder = new FluidStack(extracted[0].getType(), actually - accepted,
                                        extracted[0].getPressure());
                                FluidType.depositToCarrier(targetCarrier, level, targetPos, remainder, fromTarget);
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
                if (stored.isEmpty())
                    return false;

                int move = Math.min(transferRate, stored.getAmount());
                if (move <= 0)
                    return false;

                // Restricción para ascender: requiere presión
                if (dir == Direction.UP && stored.getPressure() <= 0)
                    return false;

                // Loop detection: verificar historial de transferencias
                PersistentBlockEntity pbe = getBE(level, from);
                if (pbe != null) {
                    String history = pbe.getOrDefault(FluidKeys.TRANSFER_HISTORY, "");
                    if (FluidTransferHelper.wouldCreateLoop(history, targetPos)) {
                        // Loop detectado! Aplicar penalización de cooldown
                        pbe.set(FluidKeys.FLUID_IO_COOLDOWN, 20); // 1 segundo de cooldown
                        return false;
                    }
                }

                int pressure = stored.getPressure();
                // Pressure is only spent CLIMBING (pushing UP). Moving DOWN or horizontally keeps the
                // full pressure, so fluid travels along/through pipes without bleeding pressure.
                int sentPressure = (dir == Direction.UP) ? Math.max(0, pressure - 1) : pressure;
                FluidStack toTransfer = new FluidStack(stored.getType(), move, sentPressure);
                if (PIPE_DBG)
                    dev.arubik.craftengine.CraftEnginePolyfills.log("[PipeDBG] PUSH " + dir + " from "
                            + from.getX() + "," + from.getY() + "," + from.getZ() + " pressureIn=" + pressure
                            + " sent=" + sentPressure + " (targetIsPipe=" + (targetCarrier instanceof PipeBehavior) + ")");

                int accepted = targetCarrier.insertFluid(level, targetPos, toTransfer, fromTarget);

                if (accepted > 0) {
                    // Remove what we sent from OUR store (CustomBlockData — same store insert/extract
                    // use; the old withBE path wrote the BE tag, a different store -> desync).
                    extractFluid(level, from, accepted, null, null);

                    // Actualizar historial después de transferencia exitosa
                    if (pbe != null) {
                        String history = pbe.getOrDefault(FluidKeys.TRANSFER_HISTORY, "");
                        String newHistory = FluidTransferHelper.updateHistory(history, targetPos);
                        pbe.set(FluidKeys.TRANSFER_HISTORY, newHistory);
                    }
                    return true;
                }
                return false;
            }
            case HOMOGENIZE: {
                // Vertical homogenize allowed: DOWN freely; UP only while pressurized so fluid
                // climbs the column within its pressure reach (and doesn't fall straight back).
                if (dir == Direction.UP && stored.getPressure() <= 0)
                    return false;
                if (targetCarrier == null)
                    return false;
                // Only equalize pipe<->pipe; never homogenize against a machine/tank (would drain it).
                if (!(targetCarrier instanceof PipeBehavior))
                    return false;

                FluidStack a = stored; // este
                FluidStack b = targetCarrier.getStored(level, targetPos); // vecino

                // Determinar tipo de fluido a equilibrar
                FluidType type;
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
                    // Homogenize moves fluid between same-level pipes; it must NOT drop pressure
                    // (averaging collapsed pressure around corners). Carry the higher pressure.
                    int appliedPressure = Math.max(a.isEmpty() ? 0 : a.getPressure(),
                            b.isEmpty() ? 0 : b.getPressure());
                    FluidStack toSend = new FluidStack(type, available, appliedPressure);
                    int accepted = FluidType.depositToCarrier(targetCarrier, level, targetPos, toSend, fromTarget);
                    if (accepted > 0) {
                        FluidType.extractFromCarrier(this, level, from, accepted, f -> {
                        }, null); // Internal extract?
                        return true;
                    }
                } else {
                    // Vecino tiene más: intentar tirar de él si su modo lo permite
                    if (targetCarrier
                            .getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                        int need = Math.min(move, amountB);
                        final FluidStack[] extracted = { null };
                        int actually = FluidType.extractFromCarrier(targetCarrier, level, targetPos, need,
                                f -> extracted[0] = f, fromTarget);
                        if (actually > 0 && extracted[0] != null) {
                            FluidStack toInsert = new FluidStack(type, actually,
                                    Math.max(0, a.getPressure()));
                            int accepted = insertFluid(level, from, toInsert, null);
                            if (accepted < actually) {
                                FluidStack remainder = new FluidStack(type, actually - accepted,
                                        extracted[0].getPressure());
                                FluidType.depositToCarrier(targetCarrier, level, targetPos, remainder, fromTarget);
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
            String fluidName = stored.isEmpty() ? "fluid.minecraft.empty"
                    : "fluid.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + fluidName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray> " + stored.getPressure() + "p");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
