package dev.arubik.craftengine.fluid.behavior;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.CEWorld;

public class PipeBehavior extends ConnectedBlockBehavior implements EntityBlockBehavior, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    protected static final int CAPACITY = 1000; // mb
    protected static final int TRANSFER_PER_TICK = 100; // mb/tick

    protected final CustomBlock block;

    public PipeBehavior(CustomBlock block) {
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(java.util.Arrays.asList("cml:iron_pump", "cml:copper_valve", "cml:copper_tank")), true);
        this.block = block;
    }

    @Override
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
    public <T extends BlockEntity> BlockEntityTicker<T> createBlockEntityTicker(CEWorld world,
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
                    if (tryTransfer(level, mcPos, Direction.DOWN, TransferAction.PUSH))
                        return;
                    for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST,
                            Direction.WEST }) {
                        if (tryTransfer(level, mcPos, dir, TransferAction.PUSH))
                            return;
                    }
                }
                for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST,
                        Direction.WEST }) {
                    tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE);
                }
            } else {
                if (tryTransfer(level, mcPos, Direction.UP, TransferAction.PUSH))
                    return;
                for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST,
                        Direction.WEST }) {
                    tryTransfer(level, mcPos, dir, TransferAction.HOMOGENIZE);
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
        PersistentBlockEntity p = getBE(level, pos);
        if (p == null)
            return new FluidStack(FluidType.EMPTY, 0, 0);
        return p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack) {
        if (stack == null || stack.isEmpty())
            return 0;
        final int[] accepted = { 0 };
        withBE(level, pos, p -> {
            FluidStack stored = p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
            if (stored.isEmpty()) {
                int move = Math.min(CAPACITY, stack.getAmount());
                p.set(FluidKeys.FLUID, new FluidStack(stack.getType(), move, stack.getPressure()));
                accepted[0] = move;
            } else if (stored.getType() == stack.getType()) {
                int space = CAPACITY - stored.getAmount();
                if (space > 0) {
                    int move = Math.min(space, stack.getAmount());
                    stored.addAmount(move);
                    int pressure = Math.max(stored.getPressure(), stack.getPressure());
                    p.set(FluidKeys.FLUID, new FluidStack(stored.getType(), stored.getAmount(), pressure));
                    accepted[0] = move;
                }
            }
        });
        return accepted[0];
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained) {
        final int[] moved = { 0 };
        withBE(level, pos, p -> {
            FluidStack stored = p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
            if (stored.isEmpty())
                return;
            int toMove = Math.min(max, stored.getAmount());
            FluidStack out = new FluidStack(stored.getType(), toMove, stored.getPressure());
            stored.removeAmount(toMove);
            if (stored.isEmpty())
                p.remove(FluidKeys.FLUID);
            else
                p.set(FluidKeys.FLUID, stored);
            moved[0] = toMove;
            drained.accept(out);
        });
        return moved[0];
    }

    protected boolean isCompatibleBehavior(Object behavior) {
        return behavior instanceof FluidCarrier;
    }

    @Override
    public FluidAccessMode getAccessMode() {
        return FluidAccessMode.ANYONE_CAN_TAKE;
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

        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        FluidCarrier targetCarrier = customOpt
                .map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null)
                .orElse(null);

        FluidStack stored = getStored(level, from);

        switch (action) {
            case PUMP: {
                // Intentar recoger de fluidos del mundo en el target
                FluidStack collected = FluidType.collectAt(targetPos, level, TRANSFER_PER_TICK,stored.getType());
                if (!collected.isEmpty()) {
                    int accepted = insertFluid(level, from, collected);
                    if (accepted > 0)
                        return true;
                }
                // Intentar extraer de un carrier si permite extracción general
                if (targetCarrier != null && targetCarrier.getAccessMode() == FluidAccessMode.ANYONE_CAN_TAKE) {
                    FluidStack theirStored = targetCarrier.getStored(level, targetPos);
                    if (!theirStored.isEmpty()) {
                        int move = Math.min(TRANSFER_PER_TICK, theirStored.getAmount());
                        final FluidStack[] extracted = { null };
                        int actually = FluidType.extractFromCarrier(targetCarrier, level, targetPos, move,
                                f -> extracted[0] = f);
                        if (actually > 0 && extracted[0] != null) {
                            // Decaimiento de presión por salto
                            FluidStack toInsert = new FluidStack(extracted[0].getType(), actually,
                                    Math.max(0, extracted[0].getPressure() - 1));
                            int accepted = insertFluid(level, from, toInsert);
                            if (accepted < actually) {
                                // devolver resto
                                FluidStack remainder = new FluidStack(extracted[0].getType(), actually - accepted,
                                        extracted[0].getPressure());
                                FluidType.depositToCarrier(targetCarrier, level, targetPos, remainder);
                            }
                            return accepted > 0;
                        }
                    }
                }
                return false;
            }
            case PUSH: {
                if (stored.isEmpty())
                    return false;

                int move = Math.min(TRANSFER_PER_TICK, stored.getAmount());
                if (move <= 0)
                    return false;

                // Restricción para ascender: requiere presión
                if (dir == Direction.UP && stored.getPressure() <= 0)
                    return false;

                if (targetCarrier != null) {
                    int appliedPressure = Math.max(0, stored.getPressure() - 1); // decaimiento de presión por distancia
                                                                                 // 1
                    FluidStack toSend = new FluidStack(stored.getType(), move, appliedPressure);
                    int accepted = FluidType.depositToCarrier(targetCarrier, level, targetPos, toSend);
                    if (accepted > 0) {
                        FluidType.extractFromCarrier(this, level, from, accepted, f -> {
                        });
                        return true;
                    }
                    return false;
                }
                // Sin carrier: intento de colocación/derrame físico
                if (stored.getAmount() >= stored.getType().mbPerFullBlock()) {
                    FluidStack temp = new FluidStack(stored.getType(), stored.getAmount(),
                            Math.max(0, stored.getPressure() - 1));
                    if (FluidType.place(temp, targetPos, level)) {
                        int placed = stored.getAmount() - temp.getAmount();
                        if (placed > 0) {
                            extractFluid(level, from, placed, f -> {
                            });
                            return true;
                        }
                    }
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
                if (diff == 0)
                    return false; // ya equilibrado

                int move = Math.min(TRANSFER_PER_TICK, Math.abs(diff) / 2 + (Math.abs(diff) % 2)); // tender al
                                                                                                   // equilibrio

                if (diff > 0) {
                    // Nosotros tenemos más: empujar al vecino
                    int available = Math.min(move, amountA);
                    if (available <= 0)
                        return false;
                    int appliedPressure = Math.max(0, ((a.isEmpty() ? 0 : a.getPressure()) + (b.isEmpty() ? 0 : b.getPressure())) / 2 );
                    FluidStack toSend = new FluidStack(type, available, appliedPressure);
                    int accepted = FluidType.depositToCarrier(targetCarrier, level, targetPos, toSend);
                    if (accepted > 0) {
                        FluidType.extractFromCarrier(this, level, from, accepted, f -> {
                        });
                        return true;
                    }
                } else {
                    // Vecino tiene más: intentar tirar de él si su modo lo permite
                    if (targetCarrier.getAccessMode() == FluidAccessMode.ANYONE_CAN_TAKE) {
                        int need = Math.min(move, amountB);
                        final FluidStack[] extracted = { null };
                        int actually = FluidType.extractFromCarrier(targetCarrier, level, targetPos, need,
                                f -> extracted[0] = f);
                        if (actually > 0 && extracted[0] != null) {
                            FluidStack toInsert = new FluidStack(type, actually,
                                    Math.max(0, a.getPressure()));
                            int accepted = insertFluid(level, from, toInsert);
                            if (accepted < actually) {
                                FluidStack remainder = new FluidStack(type, actually - accepted,
                                        extracted[0].getPressure());
                                FluidType.depositToCarrier(targetCarrier, level, targetPos, remainder);
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

    public static class Factory implements BlockBehaviorFactory {
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
