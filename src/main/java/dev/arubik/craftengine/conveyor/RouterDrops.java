package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/** Shared teardown helper: drop a router's buffered items + wipe its persisted slots. */
final class RouterDrops {

    private RouterDrops() {
    }

    static void dropBuffer(CEWorld world, BlockPos pos, AbstractRouterBlockEntity r) {
        try {
            org.bukkit.World bw = (org.bukkit.World) world.world().platformWorld();
            for (int i = 0; i < r.getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack s = r.getItem(i);
                if (s == null || s.isEmpty())
                    continue;
                org.bukkit.inventory.ItemStack bukkit =
                        org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(s);
                if (bw != null && bukkit != null && !bukkit.getType().isAir())
                    bw.dropItem(new org.bukkit.Location(bw, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5), bukkit);
                r.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
            }
            r.clear();
        } catch (Throwable ignored) {
        }
    }
}
