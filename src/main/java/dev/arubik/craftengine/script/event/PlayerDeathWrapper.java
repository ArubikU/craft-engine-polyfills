package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a player dies — everything a death-screen/loot mod would want to touch in one place.
 * A script could, for example, run a "soul bound" enchant: strip a specific item back out of
 * {@link #drops()} before it hits the ground and flag {@link #setKeepInventory} so the rest of the
 * inventory never leaves the player at all.
 */
public final class PlayerDeathWrapper extends ScriptEvent {
    private final PlayerDeathEvent raw;

    public PlayerDeathWrapper(PlayerDeathEvent raw) {
        super("PlayerDeathEvent");
        this.raw = raw;
    }

    public PlayerDeathEvent raw() { return raw; }

    public String deathMessage() { return ScriptEventUtil.componentToString(raw.deathMessage()); }

    public void setDeathMessage(String text) {
        raw.deathMessage(text == null || text.isEmpty() ? null : ScriptEventUtil.parseComponent(text));
    }

    public List<net.minecraft.world.item.ItemStack> drops() {
        List<net.minecraft.world.item.ItemStack> out = new ArrayList<>();
        for (org.bukkit.inventory.ItemStack s : raw.getDrops()) out.add(CraftItemStack.asNMSCopy(s));
        return out;
    }

    public void setDrops(List<net.minecraft.world.item.ItemStack> drops) {
        raw.getDrops().clear();
        for (net.minecraft.world.item.ItemStack s : drops) {
            if (s != null && !s.isEmpty()) raw.getDrops().add(CraftItemStack.asBukkitCopy(s));
        }
    }

    public boolean keepInventory() { return raw.getKeepInventory(); }
    public void setKeepInventory(boolean keep) { raw.setKeepInventory(keep); }

    public int exp() { return raw.getDroppedExp(); }
    public void setExp(int exp) { raw.setDroppedExp(exp); }

    // PlayerDeathEvent isn't Cancellable (you can't "cancel" a death) — no proxy needed.
}
