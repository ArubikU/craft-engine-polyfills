package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;

/**
 * Block behavior backing a {@link ConveyorBlockEntity}. Wires the controller
 * (EntityBlock composition model) and per-tick ticking, mirroring the machine
 * examples.
 *
 * <p>Registration: expose {@link #FACTORY} under {@link #POLYFILL_CONVEYOR}; the
 * main thread wires it in {@code block/BlockBehaviors.java}.</p>
 */
public class ConveyorBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior implements EntityBlock {

    public static final Key POLYFILL_CONVEYOR = Key.of("polyfills:conveyor");
    public static final Factory FACTORY = new Factory();

    private final Direction facing;
    private int controllerId;

    public ConveyorBehavior(BlockDefinition block, Direction facing) {
        super(block);
        this.facing = facing == null ? Direction.NORTH : facing;
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new ConveyorBlockEntity(blockEntity, facing);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Direction facing = Direction.NORTH;
            Object f = arguments.get("facing");
            if (f != null) {
                try {
                    facing = Direction.valueOf(f.toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            return new ConveyorBehavior(block, facing);
        }
    }
}
