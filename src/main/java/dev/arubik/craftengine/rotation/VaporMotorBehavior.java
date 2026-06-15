package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.util.Utils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * EntityBlock behavior for the vapor motor. Backs each block instance with a
 * {@link VaporMotorBlockEntity}. Reuses {@link MachineBlockBehavior} for the
 * connectable-faces / IO / facing plumbing.
 */
public class VaporMotorBehavior extends MachineBlockBehavior {

    public static final Key POLYFILL_VAPOR_MOTOR = Key.of("polyfills:vapor_motor");

    public static final Factory FACTORY = new Factory();

    private final int vaporCapacity;
    private final int vaporPerTick;
    private final float maxRpm;
    private final float stressCapacity;
    private final float vaporPerStress;

    public VaporMotorBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            int vaporCapacity, int vaporPerTick, float maxRpm, float stressCapacity, float vaporPerStress) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.vaporCapacity = vaporCapacity;
        this.vaporPerTick = vaporPerTick;
        this.maxRpm = maxRpm;
        this.stressCapacity = stressCapacity;
        this.vaporPerStress = vaporPerStress;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new VaporMotorBlockEntity(blockEntity, vaporCapacity, vaporPerTick, maxRpm,
                stressCapacity, vaporPerStress);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);
            int capacity = Utils.getAsInt(arguments.getOrDefault("vapor-capacity",
                    VaporMotorBlockEntity.DEFAULT_VAPOR_CAPACITY), "vapor-capacity");
            int perTick = Utils.getAsInt(arguments.getOrDefault("vapor-per-tick",
                    VaporMotorBlockEntity.DEFAULT_VAPOR_PER_TICK), "vapor-per-tick");
            float maxRpm = Utils.getAsFloat(arguments.getOrDefault("max-rpm",
                    VaporMotorBlockEntity.DEFAULT_MAX_RPM), "max-rpm");
            float stressCap = Utils.getAsFloat(arguments.getOrDefault("stress-capacity",
                    VaporMotorBlockEntity.DEFAULT_STRESS_CAPACITY), "stress-capacity");
            float vaporPerStress = Utils.getAsFloat(arguments.getOrDefault("vapor-per-stress",
                    VaporMotorBlockEntity.DEFAULT_VAPOR_PER_STRESS), "vapor-per-stress");
            return new VaporMotorBehavior(block, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.getIOConfiguration(null, null), capacity, perTick, maxRpm, stressCap, vaporPerStress);
        }
    }
}
