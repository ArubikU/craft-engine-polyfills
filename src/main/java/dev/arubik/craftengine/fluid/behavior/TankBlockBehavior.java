package dev.arubik.craftengine.fluid.behavior;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

public class TankBlockBehavior extends ConnectableBlockBehavior implements EntityBlockBehavior, FluidCarrier {

    public static final Factory FACTORY = new Factory();


    public final EnumProperty<FluidType> fluidTypeProperty;
    public final IntegerProperty levelProperty; // puede ser null si no se registra realmente

    public final Set<FluidType> acceptedFluids = Set.of(FluidType.WATER, FluidType.LAVA);
    public final int MAX_CAPACITY = 5000; // 5 cubos (1000mb cada uno)

    public TankBlockBehavior(CustomBlock block,
            EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            EnumProperty<Direction> verticalDirectionProperty,
            EnumProperty<FluidType> fluidTypeProperty) {
        super(block,
                List.of(Direction.UP, Direction.DOWN), horizontalDirectionProperty, verticalDirectionProperty);
        this.fluidTypeProperty = fluidTypeProperty;
        this.levelProperty = null; // placeholder hasta conocer API correcta
    }

    public static class Factory implements BlockBehaviorFactory {
        @SuppressWarnings("unchecked")
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> args) {
            EnumProperty<HorizontalDirection> h = (EnumProperty<HorizontalDirection>) args.get("horizontal");
            EnumProperty<Direction> v = (EnumProperty<Direction>) args.get("vertical");
            EnumProperty<FluidType> f = (EnumProperty<FluidType>) args.get("fluidType");
            return new TankBlockBehavior(block, h, v, f);
        }
    }

    // --- Interacción jugador (cubos) ---
    public InteractionResult use(Object thisBlock, Object[] args, java.util.concurrent.Callable<Object> superCall) throws Exception {
        try { if (superCall != null) superCall.call(); } catch (Exception ignored) {}
        Level level = (Level) args[0];
        BlockPos pos = (BlockPos) args[1];
        Player player = (Player) args[2];
        InteractionHand hand = (InteractionHand) args[3];
        // BlockHitResult hit = (BlockHitResult) args[4]; // no usado
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        FluidStack stored = getStored(level, pos);

        if (held.getItem() == Items.WATER_BUCKET) {
            int accepted = insertFluid(level, pos, new FluidStack(FluidType.WATER, FluidType.MB_PER_BUCKET, 0));
            if (accepted == FluidType.MB_PER_BUCKET) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                    player.addItem(new ItemStack(Items.BUCKET));
                }
                return InteractionResult.CONSUME;
            }
        } else if (held.getItem() == Items.LAVA_BUCKET) {
            int accepted = insertFluid(level, pos, new FluidStack(FluidType.LAVA, FluidType.MB_PER_BUCKET, 0));
            if (accepted == FluidType.MB_PER_BUCKET) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                    player.addItem(new ItemStack(Items.BUCKET));
                }
                return InteractionResult.CONSUME;
            }
        } else if (held.getItem() == Items.BUCKET && !stored.isEmpty() && stored.getAmount() >= FluidType.MB_PER_BUCKET) {
            FluidType type = stored.getType();
            if (type == FluidType.WATER || type == FluidType.LAVA) {
                final FluidStack[] drained = { null };
                extractFluid(level, pos, FluidType.MB_PER_BUCKET, f -> drained[0] = f);
                if (drained[0] != null && drained[0].getAmount() == FluidType.MB_PER_BUCKET) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        if (type == FluidType.WATER) player.addItem(new ItemStack(Items.WATER_BUCKET));
                        else if (type == FluidType.LAVA) player.addItem(new ItemStack(Items.LAVA_BUCKET));
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public boolean canAccept(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) return false;
        return acceptedFluids.contains(fluid.getType());
    }

    public FluidStack getStored(Level level, net.minecraft.core.BlockPos pos) {
        PersistentBlockEntity be = getBlockEntity(level, pos);
        if (be == null) return new FluidStack(FluidType.EMPTY, 0, 0);
        return be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
    }

    public int insertFluid(Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
        return insertFluidInternal(level, pos, stack, 0);
    }

    // Inserción recursiva: intenta llenar primero el tanque más abajo (en línea recta) y luego este.
    private int insertFluidInternal(Level level, net.minecraft.core.BlockPos pos, FluidStack stack, int depth) {
        if (stack == null || stack.isEmpty() || !canAccept(stack)) return 0;
        if (depth > 256) return 0; // salvaguarda

        int totalAccepted = 0;

        // 1. Intentar pasar el fluido al tanque de abajo si:
        //    - existe
        //    - es un tanque del mismo tipo de bloque
        //    - ambos están en modo "Straight" (orientación vertical adecuada)
        if (isTank(level, pos.below()) && isStraight(level, pos) && isStraight(level, pos.below())) {
            // Enviar todo el stack hacia abajo primero
            int acceptedDown = insertFluidInternal(level, pos.below(), stack, depth + 1);
            if (acceptedDown >= stack.getAmount()) {
                // Todo se insertó abajo, nada que hacer aquí
                return acceptedDown;
            } else if (acceptedDown > 0) {
                totalAccepted += acceptedDown;
                // Calcular remanente para este tanque
                stack = new FluidStack(stack.getType(), stack.getAmount() - acceptedDown, stack.getPressure());
            }
        }

        // 2. Insertar lo que reste en este tanque
        if (!stack.isEmpty()) {
            final int[] acceptedHere = { 0 };
            FluidStack remainingStack = stack; // alias
            executeBlockEntity(level, pos, be -> {
                FluidStack stored = be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
                if (stored.isEmpty()) {
                    int mv = Math.min(MAX_CAPACITY, remainingStack.getAmount());
                    be.set(FluidKeys.FLUID, new FluidStack(remainingStack.getType(), mv, remainingStack.getPressure()));
                    acceptedHere[0] = mv;
                } else if (stored.getType() == remainingStack.getType()) {
                    int space = MAX_CAPACITY - stored.getAmount();
                    if (space > 0) {
                        int mv = Math.min(space, remainingStack.getAmount());
                        stored.addAmount(mv);
                        int pressure = Math.max(stored.getPressure(), remainingStack.getPressure());
                        be.set(FluidKeys.FLUID, new FluidStack(stored.getType(), stored.getAmount(), pressure));
                        acceptedHere[0] = mv;
                    }
                }
            });
            if (acceptedHere[0] > 0) {
                totalAccepted += acceptedHere[0];
                updateShapeState(level, pos);
            }
        }

        return totalAccepted;
    }

    public boolean isStraight(Level level, BlockPos pos) {
        if(verticalDirectionProperty != null) {
            ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
            if(state != null) {
                Direction dir = state.get(verticalDirectionProperty);
                return dir == Direction.UP || dir == Direction.DOWN;
            }
        }
        return true;
    }

    public boolean isSameDirection(Level level, BlockPos pos, Direction dir) {
        if(verticalDirectionProperty != null) {
            ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
            if(state != null) {
                Direction current = state.get(verticalDirectionProperty);
                return current == dir;
            }
        }
        return false;
    }

    public boolean isTank(Level level, BlockPos pos) {
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if(state != null) {
           return  state.owner().value().id().equals(this.customBlock.id());
        }
        return false;
    }

    public int extractFluid(Level level, net.minecraft.core.BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained) {
        final int[] moved = { 0 };
        executeBlockEntity(level, pos, be -> {
            FluidStack stored = be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
            if (stored.isEmpty()) return;
            int mv = Math.min(max, stored.getAmount());
            FluidStack out = new FluidStack(stored.getType(), mv, stored.getPressure());
            stored.removeAmount(mv);
            if (stored.isEmpty()) be.remove(FluidKeys.FLUID); else be.set(FluidKeys.FLUID, stored);
            moved[0] = mv;
            drained.accept(out);
        });
        if (moved[0] > 0) updateShapeState(level, pos);
        return moved[0];
    }

    private void updateShapeState(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        FluidStack stored = getStored(level, pos);
        if(levelProperty!=null) {
            int lev = (int) Math.floor((stored.getAmount() / (double) MAX_CAPACITY) * levelProperty.max); // 0..10
            Optional<ImmutableBlockState> state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos));
            if (state.isPresent()) {
                ImmutableBlockState newState = state.get().with(levelProperty, lev);
                if (!newState.equals(state.get())) {
                    FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(), 3);
                }
            }
        }
    }

    public FluidAccessMode getAccessMode() { return FluidAccessMode.ANYONE_CAN_TAKE; }

    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos arg0, ImmutableBlockState arg1) {
        return new PersistentBlockEntity(arg0, arg1);
    }

    public PersistentBlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be instanceof PersistentBlockEntity) return (PersistentBlockEntity) be;
        return null;
    }

    public void executeBlockEntity(Level world, net.minecraft.core.BlockPos pos, java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity be = getBlockEntity(world, pos);
        if (be != null) consumer.accept(be);
    }
}
