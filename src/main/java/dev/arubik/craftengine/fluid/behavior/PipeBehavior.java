package dev.arubik.craftengine.fluid.behavior;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.core.world.CEWorld;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.Utils;

public class PipeBehavior extends ConnectedBlockBehavior implements EntityBlockBehavior, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    protected static final int CAPACITY = 1000; // mb
    protected static final int TRANSFER_PER_TICK = 100; // mb/tick

    protected final CustomBlock block;

    // Round-robin caching: guardar última dirección exitosa por posición
    private final java.util.Map<Long, Integer> lastSuccessfulDirection = new java.util.concurrent.ConcurrentHashMap<>();

    public PipeBehavior(CustomBlock block) {
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(java.util.Arrays.asList("cml:iron_pump", "cml:copper_valve", "cml:copper_tank")), true);
        this.block = block;
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        return new PersistentBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> createSyncBlockEntityTicker(CEWorld world,
            ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType())
            return null;
        return (lvl, cePos, ceState, be) -> {
            Level level = (Level) world.world().serverWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos mcPos = BlockPos.of(cePos.asLong());
            FluidStack stored = getStored(level, mcPos);
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
                // Round-robin homogenize
                Direction[] horizDirs = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
                int startIdx = lastSuccessfulDirection.getOrDefault(mcPos.asLong(), 0) % horizDirs.length;

                for (int i = 0; i < horizDirs.length; i++) {
                    Direction dir = horizDirs[(startIdx + i) % horizDirs.length];
                    if (tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE)) {
                        lastSuccessfulDirection.put(mcPos.asLong(), (startIdx + i) % horizDirs.length);
                    }
                }
            } else {
                if (tryTransfer(level, mcPos, Direction.UP, TransferAction.PUSH))
                    return;

                // Round-robin homogenize (con presión)
                Direction[] horizDirs = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
                int startIdx = lastSuccessfulDirection.getOrDefault(mcPos.asLong(), 0) % horizDirs.length;

                for (int i = 0; i < horizDirs.length; i++) {
                    Direction dir = horizDirs[(startIdx + i) % horizDirs.length];
                    if (tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE)) {
                        lastSuccessfulDirection.put(mcPos.asLong(), (startIdx + i) % horizDirs.length);
                    }
                }

                stored = getStored(level, mcPos);
                if (!stored.isEmpty()) {
                    tryTransfer(level, mcPos, Direction.DOWN, TransferAction.PUSH);
                }
            }
        };
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p)
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

        // Check neighbor IO Configuration if available
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        FluidCarrier targetCarrier = customOpt
                .map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null)
                .orElse(null);

        net.minecraft.core.Direction fromTarget = Utils.oppositeDirection(dir);

        if (customOpt.isPresent()) {
            BlockBehavior behavior = customOpt.get().behavior();
            if (behavior instanceof dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior connectable) {
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
                        int move = Math.min(inputRate, theirStored.getAmount());
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
                FluidStack toTransfer = new FluidStack(stored.getType(), move,
                        Math.max(0, pressure - 1));

                int accepted = targetCarrier.insertFluid(level, targetPos, toTransfer, fromTarget);

                if (accepted > 0) {
                    stored.removeAmount(accepted);
                    if (stored.isEmpty())
                        withBE(level, from, p -> p.remove(FluidKeys.FLUID));
                    else
                        withBE(level, from, p -> p.set(FluidKeys.FLUID, stored));

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
                // Solo aplicar horizontalmente
                if (dir == Direction.UP || dir == Direction.DOWN)
                    return false;
                if (targetCarrier == null)
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
                    int appliedPressure = Math.max(0,
                            ((a.isEmpty() ? 0 : a.getPressure()) + (b.isEmpty() ? 0 : b.getPressure())) / 2);
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
        public BlockBehavior create(CustomBlock block, Map<String, Object> args) {
            return new PipeBehavior(block);
        }
    }

    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {

        Level level = (Level) context.getLevel().serverWorld();
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
