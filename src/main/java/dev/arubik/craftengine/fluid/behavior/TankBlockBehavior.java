package dev.arubik.craftengine.fluid.behavior;

import java.util.List;
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
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;

public class TankBlockBehavior extends ConnectableBlockBehavior implements EntityBlock, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    public static final int TRANSFER_PER_TICK = 100;

    public final EnumProperty<FluidType> fluidTypeProperty;
    public final IntegerProperty levelProperty; // puede ser null si no se registra realmente

    public final Set<FluidType> acceptedFluids = Set.of(FluidType.values());
    public final int MAX_CAPACITY = 5000; // 5 cubos (1000mb cada uno)

    public TankBlockBehavior(BlockDefinition block,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
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
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            EnumProperty<net.momirealms.craftengine.core.util.Direction> h = (EnumProperty<net.momirealms.craftengine.core.util.Direction>) args
                    .get("horizontal");
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
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.getStored(level, pos);
    }

    public int insertFluid(Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
        return insertFluidInternal(level, pos, stack, 0);
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        FluidType stores = state.get(fluidTypeProperty);
        if (stores != null && stores != FluidType.EMPTY) {
            int level = state.get(levelProperty);
            dev.arubik.craftengine.util.CustomBlockData.from((Level) context.getLevel().minecraftWorld(),
                    (BlockPos) LocationUtils.toBlockPos(context.getClickedPos()))
                    .set(FluidKeys.FLUID, new FluidStack(stores,
                            (int) Math.floor((level / (double) levelProperty.max) * MAX_CAPACITY), 0));
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
            return state.owner().value().id().equals(this.block().id());
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
        // 1) Drain from the TOP of the column first (the level drops from above, like real gravity):
        // recurse UP before taking from this tank.
        if (isTank(level, pos.above()) && isStraight(level, pos) && isStraight(level, pos.above())) {
            movedTotal += extractChain(level, pos.above(), max, targetType, agg, depth + 1);
        }
        int remaining = max - movedTotal;
        // 2) Then take from THIS tank if more is still needed.
        if (remaining > 0) {
            FluidStack stored = getStored(level, pos);
            if (!stored.isEmpty() && stored.getType() == targetType) {
                int mv = Math.min(remaining, stored.getAmount());
                final FluidStack[] extractedStack = { null };
                int actuallyExtracted = dev.arubik.craftengine.fluid.FluidCarrierImpl.extractFluid(level, pos, mv,
                        f -> extractedStack[0] = f);
                if (actuallyExtracted > 0) {
                    movedTotal += actuallyExtracted;
                    agg.add(actuallyExtracted, extractedStack[0].getPressure());
                    updateShapeState(level, pos);
                }
            }
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

        {
            Optional<ImmutableBlockState> state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos));
            if (state.isPresent()) {
                ImmutableBlockState cur = state.get();
                // Resolve the properties from the CURRENT state's block, NOT the cached fields: a
                // `/craftengine reload all` re-registers the block with NEW property instances, so the
                // cached levelProperty/fluidTypeProperty become stale and `state.with(staleProp,...)`
                // throws "Property level not found in cml:copper_tank". Looking them up per-call fixes it.
                IntegerProperty lvlProp = (IntegerProperty)(Object) cur.getProperty("level");
                @SuppressWarnings("unchecked")
                EnumProperty<FluidType> ftProp = (EnumProperty<FluidType>) (Object) cur.getProperty("fluidtype");
                if (lvlProp == null)
                    return;
                int lev = (int) Math.ceil((stored.getAmount() / (double) MAX_CAPACITY) * lvlProp.max);
                lev = Math.max(stored.isEmpty() ? 0 : 1, Math.min(lev, lvlProp.max));

                // Skip the (expensive) setBlock + redundant PDC write when the VISIBLE state is unchanged
                // — the fluid amount/persistence was already updated by the insert/extract that called us,
                // so a small change that doesn't move the level bucket needs no block update.
                Integer curLev = cur.get(lvlProp);
                FluidType curFt = ftProp != null ? cur.get(ftProp) : null;
                if (curLev != null && curLev == lev && (ftProp == null || curFt == stored.getType()))
                    return;

                ImmutableBlockState newState = cur.with(lvlProp, lev);
                if (ftProp != null)
                    newState = newState.with(ftProp, stored.getType());

                ((net.minecraft.world.level.LevelWriter) level).setBlock(pos,
                        (net.minecraft.world.level.block.state.BlockState) newState.customBlockState().minecraftState(),
                        3);

                // Force data persistence to Chunk PDC after modification
                if (!stored.isEmpty()) {
                    dev.arubik.craftengine.util.CustomBlockData.from(level, pos).set(FluidKeys.FLUID, stored);
                }
            }
        }
    }

    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
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

    /** Controller carrying this tank's sync ticking + experience-drop on removal. */
    public static class Controller extends PersistentBlockEntity {
        private final TankBlockBehavior behavior;

        public Controller(BlockEntity blockEntity, TankBlockBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        public void onRemove() {
            super.onRemove();
            BlockEntity be = blockEntity();
            FluidStack stored = dev.arubik.craftengine.fluid.FluidCarrierImpl.getStored(
                    (Level) ((BukkitWorld) be.world().world()).minecraftWorld(),
                    Utils.fromPos(be.pos()));

            if (stored != null && stored.getType() == FluidType.EXPERIENCE) {
                Level level = (Level) ((BukkitWorld) be.world().world()).minecraftWorld();

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
        }

        @Override
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
                CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper((BlockEntityTicker<Controller>) Controller::tick);
        }

        public static void tick(CEWorld world,
                net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, Controller self) {
            self.behavior.tickTank(world, cePos);
        }
    }

    public PersistentBlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    public void executeBlockEntity(Level world, net.minecraft.core.BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity be = getBlockEntity(world, pos);
        if (be != null)
            consumer.accept(be);
    }

    private void tickTank(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        Level level = (Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        BlockPos mcPos = BlockPos.of(cePos.asLong());

        // 1. Try to PUMP from UP
        tryTransfer(level, mcPos, Direction.UP, true);

        // 2. Try to PUSH to DOWN
        tryTransfer(level, mcPos, Direction.DOWN, false);
    }

    private void tryTransfer(Level level, BlockPos pos, Direction direction, boolean isPump) {
        if (!canConnectTo(level, pos, direction))
            return;

        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);
        var customOpt = BlockStateUtils.getOptionalCustomBlockState(targetState);

        // Only interact with PIPES
        if (customOpt.isEmpty() || !(customOpt.get().behavior() instanceof PipeBehavior)) {
            return;
        }

        FluidCarrier targetCarrier = (FluidCarrier) customOpt.get().behavior();

        FluidStack myStored = getStored(level, pos);

        if (isPump) {
            // "Extraer naturalmente de una pipe de arriba"
            // We want to pull FROM the target (UP)

            // If we are full, we can't pull
            if (myStored.getAmount() >= MAX_CAPACITY)
                return;

            // Start simple: try to extract from them
            int space = MAX_CAPACITY - myStored.getAmount();
            int limit = Math.min(space, TRANSFER_PER_TICK);

            // If we have fluid, we only accept SAME fluid
            if (!myStored.isEmpty()) {
                FluidStack theirStored = targetCarrier.getStored(level, targetPos);
                if (!theirStored.isEmpty() && theirStored.getType() != myStored.getType()) {
                    return;
                }
            }

            final FluidStack[] extracted = { null };
            // Direction.DOWN is the side of the UPPER block that faces US
            int actually = FluidType.extractFromCarrier(targetCarrier, level, targetPos, limit, f -> extracted[0] = f,
                    Direction.DOWN);

            if (actually > 0 && extracted[0] != null) {
                int accepted = insertFluid(level, pos, extracted[0]);
                // If we couldn't insert all despite saying we had space (race condition?),
                // refund??
                // insertFluid handles logic, but let's assume it works because we checked
                // space.
                // But strictly speaking, if 'accepted' < 'actually', we should refund.
                if (accepted < actually) {
                    FluidStack remainder = new FluidStack(extracted[0].getType(), actually - accepted,
                            extracted[0].getPressure());
                    FluidType.depositToCarrier(targetCarrier, level, targetPos, remainder, Direction.DOWN);
                }
            }

        } else {
            // "Expulsar naturalmente a otras pipes de abajo"
            // We want to push TO the target (DOWN)

            if (myStored.isEmpty())
                return;

            int limit = Math.min(myStored.getAmount(), TRANSFER_PER_TICK);
            FluidStack toPush = new FluidStack(myStored.getType(), limit, myStored.getPressure());

            // Direction.UP is the side of the LOWER block that faces US
            int accepted = targetCarrier.insertFluid(level, targetPos, toPush, Direction.UP);

            if (accepted > 0) {
                // Remove from us
                final int[] removed = { 0 };
                extractFluid(level, pos, accepted, f -> removed[0] = f.getAmount());
            }
        }
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
        if (ibs == null || !ibs.owner().value().id().equals(this.block().id()))
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
