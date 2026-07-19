package dev.arubik.craftengine.chainery;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;

/**
 * Block behavior for a CHAINERY endpoint block: it carries a {@link ChainBlockEntity} so each of the two
 * blocks a chain drops at its ends persists the chain's id and severs the span when mined/exploded (but not
 * when captured). The block itself is otherwise an ordinary decorative block — its MODEL is what the chain's
 * links are rendered from. Registered under {@code polyfills:chainery_block} in {@code BlockBehaviors}.
 */
public class ChaineryBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    public static final Factory FACTORY = new Factory();

    public ChaineryBlockBehavior(BlockDefinition customBlock) {
        super(customBlock);
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new ChainBlockEntity(blockEntity);
    }

    /** Loaded {@link ChainBlockEntity} at {@code pos}, or null. */
    public static ChainBlockEntity getAt(Level level, BlockPos pos) {
        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
        return (be != null && be.controller instanceof ChainBlockEntity c) ? c : null;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return (BlockBehavior) new ChaineryBlockBehavior(block);
        }
    }
}
