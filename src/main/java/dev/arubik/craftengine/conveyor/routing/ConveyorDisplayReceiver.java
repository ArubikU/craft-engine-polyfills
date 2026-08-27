package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.belt.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.routing.ConveyorReceiver;
import net.momirealms.craftengine.core.util.Direction;
import org.bukkit.inventory.ItemStack;

public interface ConveyorDisplayReceiver
extends ConveyorReceiver {
    public boolean adoptConveyorItem(ItemStack var1, float var2, ConveyorItemDisplay var3, boolean var4, Direction var5);
}

