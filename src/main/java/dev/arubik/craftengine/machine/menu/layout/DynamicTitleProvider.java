package dev.arubik.craftengine.machine.menu.layout;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;

@FunctionalInterface
public interface DynamicTitleProvider {
    String provide(AbstractMachineBlockEntity machine);
}
