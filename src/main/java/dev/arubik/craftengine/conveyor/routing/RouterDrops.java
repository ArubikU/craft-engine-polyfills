package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.routing.AbstractRouterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

final class RouterDrops {
    private RouterDrops() {
    }

    static void dropBuffer(CEWorld world, BlockPos pos, AbstractRouterBlockEntity r) {
        try {
            World bw = (World)world.world().platformWorld();
            for (int i = 0; i < r.getContainerSize(); ++i) {
                ItemStack s = r.getItem(i);
                if (s == null || s.isEmpty()) continue;
                org.bukkit.inventory.ItemStack bukkit = CraftItemStack.asBukkitCopy((ItemStack)s);
                if (bw != null && bukkit != null && !bukkit.getType().isAir()) {
                    bw.dropItem(new Location(bw, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5), bukkit);
                }
                r.setItem(i, ItemStack.EMPTY);
            }
            r.clear();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

