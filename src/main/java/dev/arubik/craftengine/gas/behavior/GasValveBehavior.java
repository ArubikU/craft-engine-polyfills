package dev.arubik.craftengine.gas.behavior;

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
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasTransferHelper;
import net.momirealms.craftengine.bukkit.util.LocationUtils;

/**
 * GasValveBehavior: controla apertura/cierre y hereda boost de la bomba.
 */
public class GasValveBehavior extends GasPumpBehavior {

    public static final Factory FACTORY = new Factory();
    private static final Key OPEN_KEY = Key.of("gas:valve_open");

    public GasValveBehavior(BlockDefinition block,
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
        Boolean open = pbe.get(OPEN_KEY, NbtType.BOOLEAN);
        return open != null && open;
    }

    public void setOpen(Level level, BlockPos pos, boolean open) {
        PersistentBlockEntity pbe = getBE(level, pos);
        if (pbe != null) {
            pbe.set(OPEN_KEY, NbtType.BOOLEAN, open);
        }
    }

    @Override
    protected void tickPump(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos) {
        // OLD gas valve transport DELETED (roadmap Phase 5). The engine drives gas flow; this valve is a
        // graph edge (closed = no edge). It just registers its network so the engine equalizes it.
        net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().minecraftWorld();
        if (level == null || level.isClientSide())
            return;
        dev.arubik.craftengine.fluid.graph.GasEngine.registerSeed(BlockPos.of(cePos.asLong()));
    }

    public void toggle(Level level, BlockPos pos) {
        setOpen(level, pos, !isOpen(level, pos));
    }

    // Redstone: reaccionar a neighborChanged
    @Override
    public void neighborChanged(Object thisBlock, Object[] args) {
        Object levelObj = args[1];
        Object posObj = args[2];
        net.minecraft.world.level.Level nmsLevel = (net.minecraft.world.level.Level) levelObj;

        net.minecraft.core.BlockPos mcPos = (net.minecraft.core.BlockPos) posObj;
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
    public static class Factory implements BlockBehaviorFactory<GasValveBehavior> {
        @Override
        public GasValveBehavior create(BlockDefinition block, ConfigSection arguments) {

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
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                }
            }
            return new GasValveBehavior(block, hProp, vProp);
        }
    }
}
