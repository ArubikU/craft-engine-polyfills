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
import dev.arubik.craftengine.util.TypedKey;
import dev.arubik.craftengine.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

public class TankBlockBehavior extends ConnectableBlockBehavior implements EntityBlockBehavior, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    public final EnumProperty<FluidType> fluidTypeProperty;
    public final IntegerProperty levelProperty; // puede ser null si no se registra realmente

    public final Set<FluidType> acceptedFluids = Set.of(FluidType.values());
    public final int MAX_CAPACITY = 5000; // 5 cubos (1000mb cada uno)

    public TankBlockBehavior(CustomBlock block,
            EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            EnumProperty<FluidType> fluidTypeProperty,
            IntegerProperty levelProperty) {
        super(block,
                List.of(Direction.UP, Direction.DOWN), horizontalDirectionProperty, verticalDirectionProperty);
        this.fluidTypeProperty = fluidTypeProperty;
        this.levelProperty = levelProperty;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @SuppressWarnings("unchecked")
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> args) {
            EnumProperty<HorizontalDirection> h = (EnumProperty<HorizontalDirection>) args.get("horizontal");
            EnumProperty<net.momirealms.craftengine.core.util.Direction> v = (EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                    .getProperty("vertical");
            EnumProperty<FluidType> f = (EnumProperty<FluidType>) block.getProperty("fluidtype");
            IntegerProperty level = (IntegerProperty) block.getProperty("level");
            return new TankBlockBehavior(block, h, v, f, level);
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
                    "<gray>" + stored.getAmount() + "/" + MAX_CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }

        // Try to collect fluid from the held item
        var collectResult = FluidType.collectFromStack(held);
        FluidStack inputFluid = collectResult.getFirst();
        ItemStack remainingItem = collectResult.getSecond();

        if (!inputFluid.isEmpty()) {
            int accepted = insertFluid(level, pos, inputFluid);
            if (accepted == inputFluid.getAmount()) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                    if (!remainingItem.isEmpty()) {
                        player.addItem(remainingItem);
                    }
                }
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
        }

        // Try to output fluid to the held item
        if (!stored.isEmpty()) {
            var outputResult = FluidType.collectToStack(held, stored, stored.getAmount());
            ItemStack resultItem = outputResult.getFirst();
            FluidStack remainingFluid = outputResult.getSecond();

            if (!resultItem.isEmpty()) {
                int drainedAmount = stored.getAmount() - remainingFluid.getAmount();
                if (drainedAmount > 0) {
                    final FluidStack[] drained = { null };
                    extractFluid(level, pos, drainedAmount, f -> drained[0] = f);
                    if (drained[0] != null && drained[0].getAmount() == drainedAmount) {
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                            player.addItem(resultItem);
                        }
                        return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
                    }
                }
            }
        }

        // Reacción a través de FluidType.reaction: p.ej. LAVA + WATER_BUCKET / WATER +
        // LAVA_BUCKET
        if (held != null && !held.isEmpty()) {
            FluidType wanted = FluidType.EMPTY;
            if (held.getItem() == Items.WATER_BUCKET)
                wanted = FluidType.LAVA;
            else if (held.getItem() == Items.LAVA_BUCKET)
                wanted = FluidType.WATER;
            if (wanted != FluidType.EMPTY) {
                // Calcular disponible hasta 1 cubo sin modificar aún
                int available = availableChain(level, pos, wanted, FluidType.MB_PER_BUCKET);
                if (available > 0) {
                    FluidStack input = new FluidStack(wanted, Math.min(available, FluidType.MB_PER_BUCKET), 0);
                    var reaction = FluidType.reaction(input, held);
                    FluidStack newStack = reaction.getFirst();
                    var outputs = reaction.getSecond();
                    int consumed = input.getAmount() - (newStack == null ? 0 : newStack.getAmount());
                    if (consumed > 0 && !outputs.isEmpty()) {
                        // Consumir exactamente lo indicado por la reacción
                        Aggregator agg = new Aggregator(wanted);
                        int moved = extractChain(level, pos, consumed, wanted, agg, 0);
                        if (moved == consumed) {
                            if (!player.getAbilities().instabuild) {
                                held.shrink(1);
                                for (ItemStack out : outputs) {
                                    if (out != null && !out.isEmpty())
                                        player.addItem(out);
                                }
                            }
                            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
                        }
                    }
                }
            }
        }

        return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
    }

    public boolean canAccept(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty())
            return false;
        return acceptedFluids.contains(fluid.getType());
    }

    public FluidStack getStored(Level level, net.minecraft.core.BlockPos pos) {
        PersistentBlockEntity be = getBlockEntity(level, pos);
        if (be == null)
            return new FluidStack(FluidType.EMPTY, 0, 0);
        return be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
    }

    public int insertFluid(Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
        return insertFluidInternal(level, pos, stack, 0);
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        FluidType stores = state.get(fluidTypeProperty);
        if (stores != null && stores != FluidType.EMPTY) {
            int level = state.get(levelProperty);
            executeBlockEntity((Level) context.getLevel().serverWorld(),
                    (BlockPos) LocationUtils.toBlockPos(context.getClickedPos()), be -> {
                        be.set(FluidKeys.FLUID, new FluidStack(stores,
                                (int) Math.floor((level / (double) levelProperty.max) * MAX_CAPACITY), 0));
                    });
        }
        return state;
    }

    // Inserción recursiva: intenta llenar primero el tanque más abajo (en línea
    // recta) y luego este.
    private int insertFluidInternal(Level level, net.minecraft.core.BlockPos pos, FluidStack stack, int depth) {
        if (stack == null || stack.isEmpty() || !canAccept(stack))
            return 0;
        if (depth > 256)
            return 0; // salvaguarda

        int totalAccepted = 0;

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
        // 2. Insertar lo que reste en este tanque
        if (!stack.isEmpty()) {
            int acceptedHere = dev.arubik.craftengine.fluid.FluidCarrierImpl.insertFluid(level, pos, stack,
                    MAX_CAPACITY, 0);
            if (acceptedHere > 0) {
                totalAccepted += acceptedHere;
                updateShapeState(level, pos);
            }
        }

        return totalAccepted;
    }

    public boolean isStraight(Level level, BlockPos pos) {
        if (verticalDirectionProperty != null) {
            ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos))
                    .orElse(null);
            if (state != null) {
                net.momirealms.craftengine.core.util.Direction dir = state.get(verticalDirectionProperty);
                return dir == net.momirealms.craftengine.core.util.Direction.UP
                        || dir == net.momirealms.craftengine.core.util.Direction.DOWN;
            }
        }
        return true;
    }

    public boolean isSameDirection(Level level, BlockPos pos, Direction dir) {
        if (verticalDirectionProperty != null) {
            ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos))
                    .orElse(null);
            if (state != null) {
                net.momirealms.craftengine.core.util.Direction current = state.get(verticalDirectionProperty);
                return Utils.fromDirection(current) == dir;
            }
        }
        return false;
    }

    public boolean isTank(Level level, BlockPos pos) {
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state != null) {
            return state.owner().value().id().equals(this.customBlock.id());
        }
        return false;
    }

    public int extractFluid(Level level, net.minecraft.core.BlockPos pos, int max,
            java.util.function.Consumer<FluidStack> drained) {
        if (max <= 0)
            return 0;
        // Determinar tipo objetivo buscando el primer tanque no vacío desde este hacia
        // arriba
        FluidType targetType = FluidType.EMPTY;
        int scanDepth = 0;
        net.minecraft.core.BlockPos scan = pos;
        while (scanDepth <= 256) {
            FluidStack s = getStored(level, scan);
            if (!s.isEmpty()) {
                targetType = s.getType();
                break;
            }
            if (!isTank(level, scan.above()) || !isStraight(level, scan) || !isStraight(level, scan.above()))
                break;
            scan = scan.above();
            scanDepth++;
        }
        if (targetType == FluidType.EMPTY)
            return 0;

        // Extraer en cadena respetando el tipo objetivo, de este tanque y luego hacia
        // arriba
        Aggregator agg = new Aggregator(targetType);
        int moved = extractChain(level, pos, max, targetType, agg, 0);
        if (moved > 0) {
            drained.accept(new FluidStack(targetType, moved, agg.maxPressure));
        }
        return moved;
    }

    private static class Aggregator {
        int maxPressure = 0;

        Aggregator(FluidType t) {
        }

        void add(int mb, int pressure) {
            if (pressure > maxPressure)
                maxPressure = pressure;
        }
    }

    private int extractChain(Level level, net.minecraft.core.BlockPos pos, int max, FluidType targetType,
            Aggregator agg, int depth) {
        if (max <= 0 || depth > 256)
            return 0;
        int movedTotal = 0;
        // 1) Extraer de este tanque si coincide el tipo
        final int[] movedHere = { 0 };
        final int[] pressureHere = { 0 };
        executeBlockEntity(level, pos, be -> {
            FluidStack stored = be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY, 0, 0));
            if (stored.isEmpty() || stored.getType() != targetType)
                return;
            int mv = Math.min(max, stored.getAmount());
            pressureHere[0] = stored.getPressure();
            stored.removeAmount(mv);
            if (stored.isEmpty())
                be.remove(FluidKeys.FLUID);
            else
                be.set(FluidKeys.FLUID, stored);
            movedHere[0] = mv;
        });
        if (movedHere[0] > 0) {
            movedTotal += movedHere[0];
            agg.add(movedHere[0], pressureHere[0]);
            updateShapeState(level, pos);
        }
        int remaining = max - movedTotal;
        // 2) Si falta, intentar arriba en línea recta
        if (remaining > 0 && isTank(level, pos.above()) && isStraight(level, pos) && isStraight(level, pos.above())) {
            movedTotal += extractChain(level, pos.above(), remaining, targetType, agg, depth + 1);
        }
        return movedTotal;
    }

    // Cuenta cuánto fluido de 'type' hay disponible hacia arriba (en línea recta)
    // sin modificar estado
    private int availableChain(Level level, net.minecraft.core.BlockPos pos, FluidType type, int max) {
        int total = 0;
        net.minecraft.core.BlockPos p = pos;
        int depth = 0;
        while (depth <= 256 && total < max) {
            FluidStack s = getStored(level, p);
            if (!s.isEmpty() && s.getType() == type) {
                int canTake = Math.min(max - total, s.getAmount());
                total += canTake;
                if (total >= max)
                    break;
            }
            if (!isTank(level, p.above()) || !isStraight(level, p) || !isStraight(level, p.above()))
                break;
            p = p.above();
            depth++;
        }
        return total;
    }

    private void updateShapeState(Level level, BlockPos pos) {
        if (level.isClientSide())
            return;
        FluidStack stored = getStored(level, pos);
        if (levelProperty != null) {
            int lev = (int) Math.ceil((stored.getAmount() / (double) MAX_CAPACITY) * levelProperty.max); // 0..10
            lev = Math.max(stored.isEmpty() ? 0 : 1, Math.min(lev, levelProperty.max));
            Optional<ImmutableBlockState> state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos));
            if (state.isPresent()) {
                ImmutableBlockState newState = state.get().with(levelProperty, lev)
                        .with(fluidTypeProperty, stored.getType());
                FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(),
                        3);

            }
        }
    }

    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos arg0,
            ImmutableBlockState arg1) {
        PersistentBlockEntity be = new PersistentBlockEntity(arg0, arg1);
        be.setPreRemoveHook((CompoundTag container) -> {
            FluidStack stored = TypedKey.getOfCompound(FluidKeys.FLUID, container);
            if (stored != null && stored.getType() == FluidType.EXPERIENCE) {
                Level level = (Level) ((BukkitWorld) be.world().world()).serverWorld();

                int amount = stored.getAmount();
                int orbs = (int) Math.ceil(amount / 7.0);
                while (orbs > 0) {
                    int toSpawn = Math.min(orbs, 10);
                    orbs -= toSpawn;
                    ExperienceOrb orb = new ExperienceOrb(
                            level, be.pos().x() + 0.5, be.pos().y() + 0.5, be.pos().z() + 0.5, toSpawn);
                    level.addFreshEntity(orb);
                }
            }
            return null;
        });
        return be;
    }

    public PersistentBlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be instanceof PersistentBlockEntity)
            return (PersistentBlockEntity) be;
        return null;
    }

    public void executeBlockEntity(Level world, net.minecraft.core.BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity be = getBlockEntity(world, pos);
        if (be != null)
            consumer.accept(be);
    }

    @Override
    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        if (args.length < 1)
            return 0;
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        // check if it is experience tank
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ibs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ibs == null || !ibs.owner().value().id().equals(this.customBlock.id()))
            return 0;

        FluidStack stored = getStored(level, pos);
        if (stored.isEmpty())
            return 0;
        int signal = (int) Math.floor((stored.getAmount() / (double) MAX_CAPACITY) * 15); // 0..15
        return Math.max(1, Math.min(signal, 15));
    }

    @Override
    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side) {
        return insertFluid(level, pos, stack);
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained,
            net.minecraft.core.Direction side) {
        return extractFluid(level, pos, max, drained);
    }
}
