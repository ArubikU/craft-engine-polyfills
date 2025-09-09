package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;
import java.util.HashSet;
import java.util.function.Consumer;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.BlockBehavior;
import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.CEWorld;

public class PipeBehavior extends ConnectedBlockBehavior implements EntityBlockBehavior, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    protected static final int CAPACITY = 1000; // mb
    protected static final int TRANSFER_PER_TICK = 100; // mb/tick

    protected final CustomBlock block;

    public PipeBehavior(CustomBlock block) {
        super(block, new java.util.ArrayList<>(), new HashSet<>(),
                new HashSet<>(java.util.Arrays.asList("cml:iron_pump","cml:copper_valve")), true);
        this.block = block;
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state) {
        return new PersistentBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> createBlockEntityTicker(CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType()) return null;
        return (lvl, cePos, ceState, be) -> {
            // Obtener Level nativo desde CEWorld (proporcionado por el usuario)
            Level level = (Level) world.world().serverWorld();
            if (level == null || level.isClientSide()) return;
            BlockPos mcPos = BlockPos.of(cePos.asLong());
            FluidStack stored = getStored(level, mcPos);
            if (stored.isEmpty()) return;
            if (tryTransfer(level, mcPos, Direction.DOWN, stored)) return;
            for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                if (tryTransfer(level, mcPos, dir, stored)) return;
            }
            if (stored.getPressure() > 0) {
                tryTransfer(level, mcPos, Direction.UP, stored);
            }
        };
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p) return p;
        return null;
    }

    protected void withBE(Level level, BlockPos pos, Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity p = getBE(level, pos);
        if (p != null) consumer.accept(p);
    }

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        PersistentBlockEntity p = getBE(level, pos);
        if (p == null) return new FluidStack(FluidType.EMPTY, 0, 0);
        return p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        final int[] accepted = {0};
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
        final int[] moved = {0};
        withBE(level, pos, p -> {
            FluidStack stored = p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
            if (stored.isEmpty()) return;
            int toMove = Math.min(max, stored.getAmount());
            FluidStack out = new FluidStack(stored.getType(), toMove, stored.getPressure());
            stored.removeAmount(toMove);
            if (stored.isEmpty()) p.remove(FluidKeys.FLUID); else p.set(FluidKeys.FLUID, stored);
            moved[0] = toMove;
            drained.accept(out);
        });
        return moved[0];
    }

    protected boolean isCompatibleBehavior(Object behavior) { return behavior instanceof FluidCarrier; }

    @Override
    public FluidAccessMode getAccessMode() { return FluidAccessMode.ANYONE_CAN_TAKE; }

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

    @SuppressWarnings("unused")
    private boolean tryTransfer(Level level, BlockPos from, Direction dir, FluidStack snapshot) {
        if (snapshot.isEmpty()) return false;
        BlockPos targetPos = offset(from, dir);
        BlockState targetState = level.getBlockState(targetPos);
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);
        FluidCarrier targetCarrier = customOpt
            .map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null)
            .orElse(null);

        int move = Math.min(TRANSFER_PER_TICK, snapshot.getAmount());
        if (move <= 0) return false;

        if (targetCarrier != null) {
            FluidStack toSend = new FluidStack(snapshot.getType(), move, snapshot.getPressure());
            int accepted = FluidType.depositToCarrier(targetCarrier, level, targetPos, toSend);
            if (accepted > 0) {
                FluidType.extractFromCarrier(this, level, from, accepted, f -> {}); // usa helper estático
                return true;
            }
            return false;
        }
        // Sin carrier: intento de colocación física (solo agua/lava/slime) usando API centralizada
        if (snapshot.getAmount() >= snapshot.getType().mbPerFullBlock()) {
            FluidStack temp = new FluidStack(snapshot.getType(), snapshot.getAmount(), snapshot.getPressure());
            if (FluidType.place(temp, targetPos, level)) {
                int placed = snapshot.getAmount() - temp.getAmount();
                if (placed > 0) {
                    extractFluid(level, from, placed, f -> {});
                    return true;
                }
            }
        }
        return false;
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> args) { return new PipeBehavior(block); }
    }
}
