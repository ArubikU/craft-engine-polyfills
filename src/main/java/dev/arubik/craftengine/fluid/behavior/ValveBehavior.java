package dev.arubik.craftengine.fluid.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.persistence.PersistentDataType;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import net.momirealms.craftengine.bukkit.util.LocationUtils;

/**
 * ValveBehavior: controla apertura/cierre y hereda boost de la bomba.
 */
public class ValveBehavior extends PumpBehavior {

    public static final Factory FACTORY = new Factory();
    private static final Key OPEN_KEY = Key.of("fluid:valve_open");

    public ValveBehavior(BlockDefinition block,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, horizontalDirectionProperty, verticalDirectionProperty);
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p)
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
    protected void tickPump(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        // OLD valve transport DELETED — the engine drives flow; the valve is a one-way graph edge.
    }

    // Añadir helper de toggle (MVP sencillo)
    public void toggle(Level level, BlockPos pos) {
        setOpen(level, pos, !isOpen(level, pos));
    }

    // Redstone: reaccionar a neighborChanged (similar enfoque al ventilador)
    @Override
    public void neighborChanged(Object thisBlock, Object[] args) {
        // stateObj no es necesario para esta lógica
        Object levelObj = args[1];
        Object posObj = args[2];
        net.minecraft.world.level.Level nmsLevel = (net.minecraft.world.level.Level) levelObj;
        // No necesitamos instancia completa de BukkitWorld aquí, sólo señal
        net.momirealms.craftengine.core.world.BlockPos cePos = LocationUtils.fromBlockPos(posObj);
        net.minecraft.core.BlockPos mcPos = net.minecraft.core.BlockPos.of(cePos.asLong());
        // Señal presente?
        boolean powered = ((net.minecraft.world.level.SignalGetter) levelObj)
                .hasNeighborSignal((net.minecraft.core.BlockPos) posObj);
        Level level = nmsLevel;
        boolean currentlyOpen = isOpen(level, mcPos);
        // Regla: señal de redstone cierra (seguridad) y ausencia de señal abre.
        boolean targetOpen = !powered;
        if (targetOpen != currentlyOpen) {
            setOpen(level, mcPos, targetOpen);
        }
    }

    // Factory
    public static class Factory implements BlockBehaviorFactory<ValveBehavior> {
        @Override
        public ValveBehavior create(BlockDefinition block, ConfigSection arguments) {

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

            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            return new ValveBehavior(block, hProp, vProp);
        }
    }
}