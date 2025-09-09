package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;
import java.util.concurrent.Callable; // para compat legacy tick

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.persistence.PersistentDataType;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.FluidKeys;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.LocationUtils;

/**
 * ValveBehavior: controla apertura/cierre y hereda boost de la bomba.
 */
public class ValveBehavior extends PumpBehavior {

    public static final Factory FACTORY = new Factory();
    private static final Key OPEN_KEY = Key.of("fluid:valve_open");

    public ValveBehavior(CustomBlock block, net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
                         net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> verticalDirectionProperty) {
        super(block, horizontalDirectionProperty, verticalDirectionProperty);
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p) return p;
        return null;
    }

    public boolean isOpen(Level level, BlockPos pos) {
        PersistentBlockEntity pbe = getBE(level, pos);
        if (pbe == null) return false;
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
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        if (level.isClientSide()) return;
        if (!isOpen(level, pos)) return;
        super.tick(thisBlock, args, superMethod);
    }

    @Override
    public <T extends BlockEntity> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<T> createBlockEntityTicker(net.momirealms.craftengine.core.world.CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType()) return null;
        return (lvl, cePos, ceState, be) -> {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().serverWorld();
            if (level == null || level.isClientSide()) return;
            BlockPos pos = BlockPos.of(cePos.asLong());
            if (!isOpen(level, pos)) return; // cerrada => no flujo
            // Reutiliza el ticker de Pump llamando manualmente a la lógica: simulamos un tick parcial
            // Simplificamos replicando boost + transferencia a través de métodos heredados
            // (Podríamos refactorizar a un método protegido común si se repite mucho)
            PersistentBlockEntity pbe = getBE(level, pos);
            if (pbe != null) {
                FluidStack s = pbe.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY,0,0));
                if (!s.isEmpty()) {
                    pbe.set(FluidKeys.FLUID, new FluidStack(s.getType(), s.getAmount(), s.getPressure()+2));
                }
            }
            FluidStack stored = getStored(level, pos);
            if (stored.isEmpty()) return;
            // Orden igual a bomba. (En una versión futura podríamos filtrar una sola dirección si quisieras que la válvula bloquee sólo una cara específica.)
            if (tryDirectional(level, pos, net.momirealms.craftengine.core.util.Direction.DOWN, stored)) return;
            for (var d : new net.momirealms.craftengine.core.util.Direction[]{net.momirealms.craftengine.core.util.Direction.NORTH, net.momirealms.craftengine.core.util.Direction.SOUTH, net.momirealms.craftengine.core.util.Direction.EAST, net.momirealms.craftengine.core.util.Direction.WEST}) {
                if (tryDirectional(level, pos, d, stored)) return;
            }
            if (stored.getPressure() > 0) {
                tryDirectional(level, pos, net.momirealms.craftengine.core.util.Direction.UP, stored);
            }
        };
    }

    // Añadir helper de toggle (MVP sencillo)
    public void toggle(Level level, BlockPos pos) {
        setOpen(level, pos, !isOpen(level, pos));
    }

    // Redstone: reaccionar a neighborChanged (similar enfoque al ventilador)
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    // stateObj no es necesario para esta lógica
        Object levelObj = args[1];
        Object posObj = args[2];
        net.minecraft.world.level.Level nmsLevel = (net.minecraft.world.level.Level) levelObj;
    // No necesitamos instancia completa de BukkitWorld aquí, sólo señal
    net.momirealms.craftengine.core.world.BlockPos cePos = LocationUtils.fromBlockPos(posObj);
    net.minecraft.core.BlockPos mcPos = net.minecraft.core.BlockPos.of(cePos.asLong());
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
    public static class Factory implements BlockBehaviorFactory {
        @Override
        public ValveBehavior create(CustomBlock block, Map<String, Object> arguments) {

            // Leer las propiedades de dirección desde los argumentos
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
            net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block.getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            
            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<Direction>) block.getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            return new ValveBehavior(block,hProp,vProp);
        }
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
}
