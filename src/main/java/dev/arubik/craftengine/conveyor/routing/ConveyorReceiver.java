package dev.arubik.craftengine.conveyor.routing;

import net.momirealms.craftengine.core.util.Direction;
import org.bukkit.inventory.ItemStack;

public interface ConveyorReceiver {
    public boolean isFull();

    public boolean receiveConveyorItem(ItemStack var1, Direction var2);

    default public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing, float jitter) {
        return this.receiveConveyorItem(stack, sourceFacing);
    }
}

