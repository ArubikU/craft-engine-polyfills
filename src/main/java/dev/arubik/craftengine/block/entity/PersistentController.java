package dev.arubik.craftengine.block.entity;

import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Controller-model alias for {@link PersistentBlockEntity} (craft-engine 26.6.2).
 *
 * <p>The block-entity subsystem was re-based onto
 * {@link net.momirealms.craftengine.core.block.entity.BlockEntityController}
 * (the engine's {@code BlockEntity} is now {@code final}). The multiblock
 * subsystem refers to the persistent controller as {@code PersistentController};
 * this thin subclass provides that name while inheriting the full PDC-backed
 * persistence API from {@link PersistentBlockEntity}.</p>
 *
 * <p>It additionally exposes {@code pos()} and {@code world()} accessors that
 * delegate to the engine-owned {@link BlockEntity}, mirroring the members that
 * the old (pre-26.6.2) {@code BlockEntity} base class used to provide.</p>
 */
public class PersistentController extends PersistentBlockEntity {

    public PersistentController(BlockEntity blockEntity) {
        super(blockEntity);
    }

    /** CE position of the backing engine block entity. */
    public net.momirealms.craftengine.core.world.BlockPos pos() {
        return blockEntity().pos();
    }

    /** CE world of the backing engine block entity. */
    public net.momirealms.craftengine.core.world.CEWorld world() {
        return blockEntity().world();
    }
}
