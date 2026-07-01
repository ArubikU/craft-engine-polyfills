package dev.arubik.craftengine.gas.behavior;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.gas.GasCarrierImpl;
import dev.arubik.craftengine.util.TypedKey;
import dev.arubik.craftengine.util.Utils;
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
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;

public class GasTankBehavior extends ConnectableBlockBehavior implements EntityBlock, GasCarrier {

    public static final Factory FACTORY = new Factory();

    public final EnumProperty<GasType> gasTypeProperty;
    public final IntegerProperty levelProperty;

    public final Set<GasType> acceptedGases = Set.of(GasType.values());
    public final int MAX_CAPACITY = 16000; // Large gas capacity

    public GasTankBehavior(BlockDefinition block,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            EnumProperty<GasType> gasTypeProperty,
            IntegerProperty levelProperty) {
        // Gas connects on all 6 faces (no gravity/direction) so the engine network forms regardless of
        // how pipes/pumps are placed around the tank (was UP/DOWN only).
        super(block,
                java.util.Arrays.asList(Direction.values()), horizontalDirectionProperty, verticalDirectionProperty);
        this.gasTypeProperty = gasTypeProperty;
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
            EnumProperty<GasType> f = (EnumProperty<GasType>) block.getProperty("gastype");
            IntegerProperty level = (IntegerProperty) block.getProperty("level");
            return new GasTankBehavior(block, h, v, f, level);
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
            String gasName = stored.isEmpty() ? "gas.minecraft.empty"
                    : "gas.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + gasName + "> " +
                    "<gray>" + stored.getAmount() + "/" + MAX_CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }

        // Add gas/item interaction if needed (like buckets for fluids, but gas usually
        // uses cylinders/cells)
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    public boolean canAccept(GasStack gas) {
        if (gas == null || gas.isEmpty())
            return false;
        return acceptedGases.contains(gas.getType());
    }

    public GasStack getStoredGas(Level level, net.minecraft.core.BlockPos pos) {
        PersistentBlockEntity be = getBlockEntity(level, pos);
        if (be == null)
            return GasStack.EMPTY;
        return be.getOrDefault(GasKeys.GAS, GasStack.EMPTY);
    }

    public int insertGas(Level level, net.minecraft.core.BlockPos pos, GasStack stack) {
        return insertGasInternal(level, pos, stack, 0);
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        GasType stores = state.get(gasTypeProperty);
        if (stores != null && stores != GasType.EMPTY) {
            int level = state.get(levelProperty);
            executeBlockEntity((Level) context.getLevel().minecraftWorld(),
                    (BlockPos) LocationUtils.toBlockPos(context.getClickedPos()), be -> {
                        be.set(GasKeys.GAS, new GasStack(stores,
                                (int) Math.floor((level / (double) levelProperty.max) * MAX_CAPACITY), 0));
                    });
        }
        return state;
    }

    // Inserción recursiva: Gases suben, así que intentamos llenar el tanque más
    // ARRIBA primero.
    private int insertGasInternal(Level level, net.minecraft.core.BlockPos pos, GasStack stack, int depth) {
        if (stack == null || stack.isEmpty() || !canAccept(stack))
            return 0;
        if (depth > 256)
            return 0;

        int totalAccepted = 0;

        // Intentar pasar al tanque de ARRIBA si es parte de la misma estructura
        // vertical
        if (isTank(level, pos.above()) && isStraight(level, pos) && isStraight(level, pos.above())) {
            int acceptedUp = insertGasInternal(level, pos.above(), stack, depth + 1);
            if (acceptedUp >= stack.getAmount()) {
                return acceptedUp;
            } else if (acceptedUp > 0) {
                totalAccepted += acceptedUp;
                stack = new GasStack(stack.getType(), stack.getAmount() - acceptedUp, stack.getPressure());
            }
        }

        // 2. Insertar lo que reste en ESTE tanque
        if (!stack.isEmpty()) {
            int acceptedHere = GasCarrierImpl.insertGas(level, pos, stack, MAX_CAPACITY, GasKeys.GAS);
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

    public boolean isTank(Level level, BlockPos pos) {
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state != null) {
            return state.owner().value().id().equals(this.block().id());
        }
        return false;
    }

    public int extractGas(Level level, net.minecraft.core.BlockPos pos, int max,
            java.util.function.Consumer<GasStack> drained) {
        if (max <= 0)
            return 0;

        // Estrategia de extracción:
        // Si el gas sube, los tanques de arriba están llenos y los de abajo vacíos.
        // Si quiero extraer, debería tomar de donde haya gas.
        // Si accedo al bloque de abajo, y está vacío (porque el gas subió), debería
        // buscar gas hacia arriba y traerlo (sucking).

        GasType targetType = GasType.EMPTY;
        int scanDepth = 0;
        net.minecraft.core.BlockPos scan = pos;

        // Escanear hacia arriba buscando gas
        while (scanDepth <= 256) {
            GasStack s = getStoredGas(level, scan);
            if (!s.isEmpty()) {
                targetType = s.getType();
                break;
            }
            if (!isTank(level, scan.above()) || !isStraight(level, scan) || !isStraight(level, scan.above()))
                break;
            scan = scan.above();
            scanDepth++;
        }

        if (targetType == GasType.EMPTY)
            return 0;

        // Extraer en cadena empezando desde donde encontramos el gas (o desde aquí y
        // subiendo recursivamente)
        // Usamos la misma lógica recursiva UP que en Tanks líquidos, ya que simplemente
        // busca y extrae donde haya.
        Aggregator agg = new Aggregator(targetType);
        int moved = extractChain(level, pos, max, targetType, agg, 0);
        if (moved > 0) {
            drained.accept(new GasStack(targetType, moved, agg.maxPressure));
        }
        return moved;
    }

    private static class Aggregator {
        int maxPressure = 0;

        Aggregator(GasType t) {
        }

        void add(int mb, int pressure) {
            if (pressure > maxPressure)
                maxPressure = pressure;
        }
    }

    private int extractChain(Level level, net.minecraft.core.BlockPos pos, int max, GasType targetType,
            Aggregator agg, int depth) {
        if (max <= 0 || depth > 256)
            return 0;
        int movedTotal = 0;
        // 1) Extraer de este tanque si coincide
        final int[] movedHere = { 0 };
        final int[] pressureHere = { 0 };
        executeBlockEntity(level, pos, be -> {
            GasStack stored = be.getOrDefault(GasKeys.GAS, GasStack.EMPTY);
            if (stored.isEmpty() || stored.getType() != targetType)
                return;
            int mv = Math.min(max, stored.getAmount());
            pressureHere[0] = stored.getPressure();
            // Shrink
            GasStack newStack = new GasStack(stored.getType(), stored.getAmount() - mv, stored.getPressure());
            if (newStack.isEmpty())
                be.remove(GasKeys.GAS);
            else
                be.set(GasKeys.GAS, newStack);
            movedHere[0] = mv;
        });

        if (movedHere[0] > 0) {
            movedTotal += movedHere[0];
            agg.add(movedHere[0], pressureHere[0]);
            updateShapeState(level, pos);
        }

        int remaining = max - movedTotal;
        // 2) Si falta, intentar arriba en línea recta (recurse UP)
        if (remaining > 0 && isTank(level, pos.above()) && isStraight(level, pos) && isStraight(level, pos.above())) {
            movedTotal += extractChain(level, pos.above(), remaining, targetType, agg, depth + 1);
        }
        return movedTotal;
    }

    private void updateShapeState(Level level, BlockPos pos) {
        if (level.isClientSide())
            return;
        GasStack stored = getStoredGas(level, pos);
        if (levelProperty != null) {
            int lev = (int) Math.ceil((stored.getAmount() / (double) MAX_CAPACITY) * levelProperty.max);
            lev = Math.max(stored.isEmpty() ? 0 : 1, Math.min(lev, levelProperty.max));
            Optional<ImmutableBlockState> state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos));
            if (state.isPresent()) {
                ImmutableBlockState newState = state.get().with(levelProperty, lev)
                        .with(gasTypeProperty, stored.getType());
                ((net.minecraft.world.level.LevelWriter) level).setBlock(pos,
                        (net.minecraft.world.level.block.state.BlockState) newState.customBlockState().minecraftState(),
                        3);
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
        return new PersistentBlockEntity(blockEntity);
    }

    public PersistentBlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        return PersistentBlockEntity.getIfLoaded(world, pos);
    }

    public void executeBlockEntity(Level world, net.minecraft.core.BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity.executeAt(world, pos, consumer);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side) {
        return insertGas(level, pos, stack);
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained,
            net.minecraft.core.Direction side) {
        return extractGas(level, pos, max, drained);
    }
}
