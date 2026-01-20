package dev.arubik.craftengine.util;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import net.minecraft.world.item.ItemStack;

public class BridgeUtils {
    public static org.bukkit.inventory.ItemStack toBukkit(ItemStack nms) {
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static ItemStack toNms(org.bukkit.inventory.ItemStack bukkit) {
        return CraftItemStack.asNMSCopy(bukkit);
    }
}
