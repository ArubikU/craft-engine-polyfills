package dev.arubik.craftengine.fluid.behavior;

import java.util.List;
import java.util.Optional;
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
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;



public class TankBlockBehavior extends ConnectableBlockBehavior implements EntityBlock, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    public static final int TRANSFER_PER_TICK = 100;

    public final Property<String> fluidTypeProperty;
    public final IntegerProperty levelProperty; // puede ser null si no se registra realmente

    public final int MAX_CAPACITY = 5000; // 5 cubos (1000mb cada uno)

    public TankBlockBehavior(BlockDefinition block,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            Property<String> fluidTypeProperty,
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
            Property<String> f = (Property<String>) block.getProperty("fluidtype");
            IntegerProperty level = (IntegerProperty) block.getProperty("level");
            return new TankBlockBehavior(block, h, v, f, level);
        }
    }

    /** Shared shift-click readout for tanks AND pipes: i18n fluid name + amount/cap + fill% + lift (the
     * hydraulic level in blocks, clamped >= 0 so deep negative-Y never shows). No more "pressure". */
    public static Component fluidInfo(FluidStack stored, int cap, int y) {
        int amt = (stored == null || stored.isEmpty()) ? 0 : stored.getAmount();
        String typeKey = (stored == null || stored.isEmpty()) ? "polyfill.liquid.empty"
                : "polyfill.liquid." + stored.getType().name().toLowerCase();
        double fill = cap > 0 ? amt / (double) cap : 0;
        return MiniMessage.miniMessage().deserialize(
                "<lang:" + typeKey + "> <gray>" + amt + "/" + cap + " mB</gray> "
                        + "<dark_gray>·</dark_gray> <yellow>" + (int) Math.round(fill * 100) + "%</yellow> "
                        + "<dark_gray>·</dark_gray> <aqua>lift " + String.format("%.2f", Math.max(0.0, fill))
                        + "</aqua>");
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
            player.getBukkitEntity().sendActionBar(fluidInfo(stored, MAX_CAPACITY, pos.getY()));
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
                // Bucket-empty-into-tank sound (2026-07-02 session — "al rellenar un tanque no
                // suena el sonido de rellenar"): this whole fill flow is a hand-rolled
                // reimplementation of vanilla bucket behavior, not a call into vanilla's own
                // BucketItem/LiquidBlockUtils (which is what normally plays this sound), so it
                // never played anything on its own. ContraptionLevel already correctly redirects
                // playSound (via its playSeededSound override) to the real world at the bearing's
                // live transform, so this "just works" for a tank living inside a contraption too.
                playFillOrEmptySound(level, pos, inputFluid.getType(), true);
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
                        playFillOrEmptySound(level, pos, drained[0].getType(), false);
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
        FluidType type = fluid.getType();
        return type != null && !type.isEmpty() && FluidType.REGISTRY.contains(type.id());
    }

    /**
     * Vanilla bucket-fill/empty sound, picked by fluid type — {@code filling} true = pouring
     * INTO the tank (bucket-empty sound, matches vanilla's own "emptying a bucket" naming),
     * false = draining OUT (bucket-fill sound, "filling a bucket FROM the tank"). See the
     * {@code useWithoutItem} call sites for why this needs to be triggered manually.
     */
    static void playFillOrEmptySound(Level level, net.minecraft.core.BlockPos pos, FluidType type,
            boolean filling) {
        // Both sound ids are per-fluid data now; fall back to the plain bucket sounds
        // if a data file names a sound this server does not have.
        net.minecraft.sounds.SoundEvent fallback = filling ? net.minecraft.sounds.SoundEvents.BUCKET_EMPTY
                : net.minecraft.sounds.SoundEvents.BUCKET_FILL;
        net.minecraft.sounds.SoundEvent sound = fallback;
        if (type != null) {
            String id = filling ? type.fillSound() : type.drainSound();
            if (id != null && !id.isBlank()) {
                var resolved = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT
                        .getOptional(net.minecraft.resources.Identifier.parse(id));
                if (resolved.isPresent())
                    sound = resolved.get();
            }
        }
        level.playSound(null, pos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public FluidStack getStored(Level level, net.minecraft.core.BlockPos pos) {
        return dev.arubik.craftengine.fluid.FluidCarrierImpl.getStored(level, pos);
    }

    @Override
    public long getCapacity(Level level, net.minecraft.core.BlockPos pos) {
        return MAX_CAPACITY;
    }

    @Override
    public void onStoreChanged(Level level, net.minecraft.core.BlockPos pos) {
        updateShapeState(level, pos); // refresh fluidtype/level blockstate after an engine write
    }

    public int insertFluid(Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
        return insertFluidInternal(level, pos, stack, 0);
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        FluidType stores = FluidType.byTankVariant(state.get(fluidTypeProperty));
        if (stores != null && stores != FluidType.EMPTY) {
            int level = state.get(levelProperty);
            executeBlockEntity((Level) context.getLevel().minecraftWorld(),
                    (BlockPos) LocationUtils.toBlockPos(context.getClickedPos()),
                    be -> be.set(FluidKeys.FLUID, new FluidStack(stores,
                            (int) Math.floor((level / (double) levelProperty.max) * MAX_CAPACITY), 0)));
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

        // With the hydraulic engine driving transport, DON'T cascade on insert — just fill this tank and
        // let the solver distribute. The old down-cascade + per-block ticks double-counted fluid.
        if (!dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED
                && isTank(level, pos.below()) && isStraight(level, pos) && isStraight(level, pos.below())) {
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
                Property<String> ftProp = (Property<String>) (Object) cur.getProperty("fluidtype");
                if (lvlProp == null)
                    return;
                int lev = (int) Math.ceil((stored.getAmount() / (double) MAX_CAPACITY) * lvlProp.max);
                lev = Math.max(stored.isEmpty() ? 0 : 1, Math.min(lev, lvlProp.max));

                // Skip the (expensive) setBlock + redundant PDC write when the VISIBLE state is unchanged
                // — the fluid amount/persistence was already updated by the insert/extract that called us,
                // so a small change that doesn't move the level bucket needs no block update.
                Integer curLev = cur.get(lvlProp);
                String curFt = ftProp != null ? cur.get(ftProp) : null;
                String wantFt = stored.getType() == null ? "empty" : stored.getType().tankVariant();
                if (curLev != null && curLev == lev && (ftProp == null || wantFt.equals(curFt)))
                    return;

                ImmutableBlockState newState = cur.with(lvlProp, lev);
                if (ftProp != null)
                    newState = newState.with(ftProp, wantFt);

                ((net.minecraft.world.level.LevelWriter) level).setBlock(pos,
                        (net.minecraft.world.level.block.state.BlockState) newState.customBlockState().minecraftState(),
                        3);
                // The fluid amount/type was already persisted to the block entity's own store by the
                // insert/extract that called us — no separate re-write needed.
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
        return PersistentBlockEntity.getIfLoaded(world, pos);
    }

    public void executeBlockEntity(Level world, net.minecraft.core.BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity.executeAt(world, pos, consumer);
    }

    private void tickTank(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        Level level = (Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        // Register this tank's network so the hydraulic engine drives it (BFS expands to all pipes).
        try {
            dev.arubik.craftengine.fluid.graph.FluidEngine
                    .registerSeed((BlockPos) Utils.fromPos(cePos));
        } catch (Throwable ignored) {
        }
        // OLD per-block tank transport DELETED (roadmap Phase 4). The hydraulic engine (FluidEngine) is the
        // single fluid transport system; this tick only registers the network (above) for the engine.
    }

    // tryTransfer (old per-block tank push/pull) DELETED — engine is the sole transport.

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
