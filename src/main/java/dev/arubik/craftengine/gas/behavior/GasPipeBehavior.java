package dev.arubik.craftengine.gas.behavior;

import java.util.HashSet;
import java.util.Map;
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
import dev.arubik.craftengine.util.Utils;

public class GasPipeBehavior extends ConnectedBlockBehavior implements EntityBlockBehavior, GasCarrier {

    public static final Factory FACTORY = new Factory();

    protected static final int CAPACITY = 1000; // mb
    protected static final int TRANSFER_PER_TICK = 100; // mb/tick

    protected final CustomBlock block;

    // Round-robin caching: guardar última dirección exitosa por posición
    private final java.util.Map<Long, Integer> lastSuccessfulDirection = new java.util.concurrent.ConcurrentHashMap<>();

    public GasPipeBehavior(CustomBlock block) {
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(java.util.Arrays.asList("cml:gas_pump", "cml:gas_valve", "cml:gas_tank")), true);
        this.block = block;
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
    public <T extends BlockEntity> BlockEntityTicker<T> createAsyncBlockEntityTicker(CEWorld world,
            ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType())
            return null;
        return (lvl, cePos, ceState, be) -> {
            Level level = (Level) world.world().serverWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos mcPos = BlockPos.of(cePos.asLong());
            GasStack stored = getStoredGas(level, mcPos);
            if (stored.getPressure() <= 0) {
                // Gas sube naturalmente: intentar tomar de abajo (passive rise into pipe)
                tryTransfer(level, mcPos, Direction.DOWN, TransferAction.PUMP);
                stored = getStoredGas(level, mcPos);
                if (!stored.isEmpty()) {
                    // Round-robin: empezar desde última dirección exitosa
                    // Prioridad a subir (UP) sin presión
                    Direction[] pushDirs = { Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST,
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
                // Con presión: permitir bajar
                if (tryTransfer(level, mcPos, Direction.DOWN, TransferAction.PUSH))
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

                stored = getStoredGas(level, mcPos);
                if (!stored.isEmpty()) {
                    // Intentar subir incluso con presión (ya cubierto arriba, pero por si acaso)
                    tryTransfer(level, mcPos, Direction.UP, TransferAction.PUSH);
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
    public GasStack getStoredGas(Level level, BlockPos pos) {
        PersistentBlockEntity be = getBE(level, pos);
        if (be == null)
            return GasStack.EMPTY;
        return be.getOrDefault(GasKeys.GAS, GasStack.EMPTY);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction direction) {
        return GasCarrierImpl.insertGas(level, pos, stack, CAPACITY, GasKeys.GAS);
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

        // Check neighbor IO Configuration if available
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        GasCarrier targetCarrier = customOpt
                .map(cs -> cs.behavior() instanceof GasCarrier fc ? fc : null)
                .orElse(null);

        net.minecraft.core.Direction fromTarget = Utils.oppositeDirection(dir);

        if (customOpt.isPresent()) {
            BlockBehavior behavior = customOpt.get().behavior();
            if (behavior instanceof dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior connectable) {
                IOConfiguration targetConfig = connectable.getIOConfiguration(level, targetPos);

                if (action == TransferAction.PUSH) {
                    // Pushing TO target, so target must accept INPUT from us
                    if (!targetConfig.acceptsInput(IOConfiguration.IOType.GAS, fromTarget))
                        return false;
                } else if (action == TransferAction.PUMP) {
                    // Pumping FROM target, so target must provide OUTPUT to us
                    if (!targetConfig.providesOutput(IOConfiguration.IOType.GAS, fromTarget))
                        return false;
                }
            }
        }

        GasStack stored = getStoredGas(level, from);
        int transferRate = TRANSFER_PER_TICK; // Default rate for pipes

        switch (action) {
            case PUMP: {
                int inputRate = transferRate;

                // Intentar extraer de un carrier si permite extracción general
                if (targetCarrier != null && targetCarrier
                        .getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                    GasStack theirStored = targetCarrier.getStoredGas(level, targetPos);
                    if (!theirStored.isEmpty()) {
                        int move = Math.min(inputRate, theirStored.getAmount());
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
                if (stored.isEmpty())
                    return false;

                int move = Math.min(transferRate, stored.getAmount());
                if (move <= 0)
                    return false;

                // Restricción para descender: requiere presión (gases suben)
                if (dir == Direction.DOWN && stored.getPressure() <= 0)
                    return false;

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
                    stored.shrink(accepted);
                    if (stored.isEmpty())
                        withBE(level, from, p -> p.remove(GasKeys.GAS));
                    else
                        withBE(level, from, p -> p.set(GasKeys.GAS, stored));

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
                // Solo aplicar horizontalmente
                if (dir == Direction.UP || dir == Direction.DOWN)
                    return false;
                if (targetCarrier == null)
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
        public BlockBehavior create(CustomBlock block, Map<String, Object> args) {
            return new GasPipeBehavior(block);
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

        GasStack stored = getStoredGas(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            String gasName = stored.isEmpty() ? "gas.minecraft.empty"
                    : "gas.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + gasName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray> " + stored.getPressure() + "p");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
