package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires for every right/left click a player makes against the world (blocks and air alike) — the
 * vanilla counterpart to this plugin's own {@link InteractEvent} used for machines/items. A script
 * could use {@link #clickedBlock()} to implement a "right-click a sign to open a shop menu" hook
 * without needing a dedicated block-type registration for every sign in the world.
 */
public final class PlayerInteractWrapper extends ScriptEvent {
    private final PlayerInteractEvent raw;

    public PlayerInteractWrapper(PlayerInteractEvent raw) {
        super("PlayerInteractEvent");
        this.raw = raw;
    }

    public PlayerInteractEvent raw() { return raw; }

    /** e.g. {@code "right_click_block"}, {@code "left_click_air"}, ... */
    public String action() { return raw.getAction().name().toLowerCase(Locale.ROOT); }

    /** {@code "hand"}/{@code "off_hand"}, or {@code null} if the API didn't report one. */
    public String hand() {
        EquipmentSlot slot = raw.getHand();
        return slot == null ? null : slot.name().toLowerCase(Locale.ROOT);
    }

    /** The clicked block, or {@code NULL} if this click didn't hit one (e.g. a left/right click on
     *  air). */
    public ScriptValue clickedBlock() {
        Block block = raw.getClickedBlock();
        if (block == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The item in the interacting hand, or {@code NULL} if empty. */
    public ScriptValue item() {
        org.bukkit.inventory.ItemStack stack = raw.getItem();
        if (stack == null || stack.getType().isAir()) return ScriptValue.NULL;
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(stack));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
