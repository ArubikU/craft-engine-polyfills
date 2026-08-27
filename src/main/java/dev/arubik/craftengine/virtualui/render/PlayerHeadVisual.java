package dev.arubik.craftengine.virtualui.render;

import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.Objects;
import java.util.UUID;

/** Renders a player-head item carrying {@code targetPlayer}'s skin — backs
 *  {@code PlayerRenderWidget} (the closest achievable "player render" from pure packet displays,
 *  see {@code Widget.PlayerRenderWidget}'s doc). */
public final class PlayerHeadVisual extends AbstractItemVisual {

    private final UUID targetPlayer;

    public PlayerHeadVisual(UUID targetPlayer, float scale, Quaternionf rotation) {
        super(scale, rotation);
        this.targetPlayer = targetPlayer;
    }

    @Override
    protected ItemStack resolveItemStack() {
        if (targetPlayer == null) return null;
        try {
            org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta)
                    org.bukkit.Bukkit.getItemFactory().getItemMeta(org.bukkit.Material.PLAYER_HEAD);
            if (meta == null) return null;
            meta.setOwningPlayer(org.bukkit.Bukkit.getOfflinePlayer(targetPlayer));
            org.bukkit.inventory.ItemStack bukkitStack = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
            bukkitStack.setItemMeta(meta);
            return org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkitStack);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    protected boolean sameItemAs(AbstractItemVisual other) {
        return other instanceof PlayerHeadVisual o && Objects.equals(targetPlayer, o.targetPlayer);
    }
}
