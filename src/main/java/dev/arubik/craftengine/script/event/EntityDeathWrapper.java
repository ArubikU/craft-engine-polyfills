package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityDeathEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when any non-player living entity dies — the mob-drop counterpart of {@link
 * PlayerDeathWrapper}. A script could use this to run a custom loot table: clear {@link #drops()}
 * entirely via {@link #setDrops} and roll its own list based on {@link #entity()}'s type instead of
 * whatever vanilla/CraftEngine would have dropped.
 */
public final class EntityDeathWrapper extends ScriptEvent {
    private final EntityDeathEvent raw;

    public EntityDeathWrapper(EntityDeathEvent raw) {
        super("EntityDeathEvent");
        this.raw = raw;
    }

    public EntityDeathEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
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

    public int droppedExp() { return raw.getDroppedExp(); }
    public void setDroppedExp(int exp) { raw.setDroppedExp(exp); }

    // EntityDeathEvent isn't Cancellable (same as PlayerDeathEvent) — no proxy needed.
}
