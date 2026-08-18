/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Direction
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import net.momirealms.craftengine.core.util.Direction;
import org.bukkit.inventory.ItemStack;

public interface ConveyorDisplayReceiver
extends ConveyorReceiver {
    public boolean adoptConveyorItem(ItemStack var1, float var2, ConveyorItemDisplay var3, boolean var4, Direction var5);
}

