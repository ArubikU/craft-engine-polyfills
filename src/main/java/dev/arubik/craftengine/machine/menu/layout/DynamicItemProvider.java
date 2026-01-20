package dev.arubik.craftengine.machine.menu.layout;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface DynamicItemProvider {
    ItemStack provide(AbstractMachineBlockEntity machine, int tick);
}
