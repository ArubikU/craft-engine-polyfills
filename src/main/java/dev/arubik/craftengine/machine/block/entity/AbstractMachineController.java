package dev.arubik.craftengine.machine.block.entity;

import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Controller-model alias for {@link AbstractMachineBlockEntity}
 * (craft-engine 26.6.2).
 *
 * <p>The machine subsystem was re-based onto
 * {@link net.momirealms.craftengine.core.block.entity.BlockEntityController}.
 * The multiblock subsystem refers to the machine controller as
 * {@code AbstractMachineController} and constructs it with the container size
 * first ({@code super(containerSize, blockEntity)}); this thin subclass adapts
 * that argument order onto {@link AbstractMachineBlockEntity}'s
 * {@code (BlockEntity, int)} constructor while inheriting the full machine
 * processing / menu / fluid / gas API.</p>
 */
public abstract class AbstractMachineController extends AbstractMachineBlockEntity {

    public AbstractMachineController(int containerSize, BlockEntity blockEntity) {
        super(blockEntity, containerSize);
    }
}
