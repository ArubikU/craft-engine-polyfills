package dev.arubik.craftengine.gas.behavior;

import java.util.Map;
import java.util.concurrent.Callable;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.persistence.PersistentDataType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasTransferHelper;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.LocationUtils;

/**
 * GasValveBehavior: controla apertura/cierre y hereda boost de la bomba.
 */
public class GasValveBehavior extends GasPumpBehavior {

    public static final Factory FACTORY = new Factory();
    private static final Key OPEN_KEY = Key.of("gas:valve_open");

    public GasValveBehavior(CustomBlock block,
            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, horizontalDirectionProperty, verticalDirectionProperty);
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    public boolean isOpen(Level level, BlockPos pos) {
        PersistentBlockEntity pbe = getBE(level, pos);
        if (pbe == null)
            return false;
        Boolean open = pbe.get(OPEN_KEY, PersistentDataType.BOOLEAN);
        return open != null && open;
    }

    public void setOpen(Level level, BlockPos pos, boolean open) {
        PersistentBlockEntity pbe = getBE(level, pos);
        if (pbe != null) {
            pbe.set(OPEN_KEY, PersistentDataType.BOOLEAN, open);
        }
    }

    @Override
    public <T extends BlockEntity> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<T> createAsyncBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType())
            return null;
        return (lvl, cePos, ceState, be) -> {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().serverWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos pos = BlockPos.of(cePos.asLong());
            if (!isOpen(level, pos))
                return;
            PersistentBlockEntity pbe = getBE(level, pos);
            if (pbe != null) {
                GasStack s = pbe.getOrDefault(GasKeys.GAS, GasStack.EMPTY);
                if (!s.isEmpty()) {
                    pbe.set(GasKeys.GAS, new GasStack(s.getType(), s.getAmount(), s.getPressure() + 2));
                }
            }

            // Gas Physics:
            // 1. PUMP (succionar)
            if (tryDirectional(level, pos, Direction.DOWN, PumpAction.PUMP))
                return;

            for (var d : new Direction[] {
                    Direction.NORTH,
                    Direction.SOUTH,
                    Direction.EAST,
                    Direction.WEST }) {
                if (tryDirectional(level, pos, d, PumpAction.PUMP))
                    return;
            }

            // 2. PUSH (empujar)
            GasStack stored = getStoredGas(level, pos);
            if (!stored.isEmpty()) {
                // Try pushing UP (natural rise)
                if (tryDirectional(level, pos, Direction.UP, PumpAction.PUSH))
                    return;

                // HORIZONTAL
                for (var d : new Direction[] {
                        Direction.NORTH,
                        Direction.SOUTH,
                        Direction.EAST,
                        Direction.WEST }) {
                    if (tryDirectional(level, pos, d, PumpAction.PUSH))
                        return;
                }

                // DOWN (requires pressure)
                if (stored.getPressure() > 0) {
                    tryDirectional(level, pos, Direction.DOWN, PumpAction.PUSH);
                }
            }
        };
    }

    public void toggle(Level level, BlockPos pos) {
        setOpen(level, pos, !isOpen(level, pos));
    }

    // Redstone: reaccionar a neighborChanged
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object levelObj = args[1];
        Object posObj = args[2];
        net.minecraft.world.level.Level nmsLevel = (net.minecraft.world.level.Level) levelObj;

        net.minecraft.core.BlockPos mcPos = (net.minecraft.core.BlockPos) posObj;
        // Señal presente?
        boolean powered = FastNMS.INSTANCE.method$SignalGetter$hasNeighborSignal(levelObj, posObj);
        Level level = nmsLevel;
        boolean currentlyOpen = isOpen(level, mcPos);
        // Regla: señal de redstone cierra (seguridad) y ausencia de señal abre.
        boolean targetOpen = !powered;
        if (targetOpen != currentlyOpen) {
            setOpen(level, mcPos, targetOpen);
        }
    }

    // Factory
    public static class Factory implements BlockBehaviorFactory<GasValveBehavior> {
        @Override
        public GasValveBehavior create(CustomBlock block, Map<String, Object> arguments) {

            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;

            Object horizontalProp = arguments.get("horizontal-direction-property");
            if (horizontalProp instanceof String) {
                horizontalDirectionProperty = (String) horizontalProp;
            }

            Object verticalProp = arguments.get("vertical-direction-property");
            if (verticalProp instanceof String) {
                verticalDirectionProperty = (String) verticalProp;
            }

            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> hProp = null;
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block
                            .getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                }
            }
            return new GasValveBehavior(block, hProp, vProp);
        }
    }
}
