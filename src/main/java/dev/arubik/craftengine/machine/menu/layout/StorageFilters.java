package dev.arubik.craftengine.machine.menu.layout;

import dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

/**
 * Resolves a {@link StorageFilterSpec} against a candidate item — shared by every
 * {@link MenuSlotType#STORAGE} consumer (machines, multiblocks/cells via
 * {@code MachineMenuListener}, and items via {@code dev.arubik.craftengine.item.menu.ItemMenuListener}),
 * so "can this go in a free storage slot" is answered the same way everywhere.
 */
public final class StorageFilters {
    private StorageFilters() {}

    /**
     * Whether {@code candidate} may be placed into a STORAGE slot governed by {@code filter}.
     * {@code extraCtx}, if given, is copied into the script's context before it runs (e.g. the
     * owning item/player for an item-behavior backpack) so the filter script can see more than
     * just the candidate.
     */
    public static boolean allows(StorageFilterSpec filter, net.minecraft.world.item.ItemStack candidate,
            ScriptContext extraCtx) {
        if (filter == null || filter.isEmpty()) return true;
        if (candidate == null || candidate.isEmpty()) return true;

        String id = itemId(candidate);
        if (!filter.deny().isEmpty() && filter.deny().stream().anyMatch(d -> matches(d, id))) return false;
        if (!filter.allow().isEmpty() && filter.allow().stream().noneMatch(a -> matches(a, id))) return false;

        String script = filter.script();
        if (script != null && !script.isBlank()) {
            try {
                ScriptCall call = ScriptCall.parse(script);
                if (call != null) {
                    ScriptContext.Builder b = ScriptContext.builder();
                    if (extraCtx != null) b.copyFrom(extraCtx);
                    ScriptContext ctx = b.item("item", candidate).build();
                    ScriptValue result = call.evaluate(ctx);
                    if (result != ScriptValue.NULL) return result.asBool();
                }
            } catch (Throwable ignored) {
                // A broken filter script fails open rather than bricking every storage slot on the page.
            }
        }
        return true;
    }

    /** Overload for Bukkit-side callers (menu click handlers work in Bukkit ItemStacks). */
    public static boolean allows(StorageFilterSpec filter, org.bukkit.inventory.ItemStack candidate,
            ScriptContext extraCtx) {
        if (candidate == null || candidate.getType().isAir()) return true;
        return allows(filter, org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(candidate), extraCtx);
    }

    /** How many of {@code candidate} a STORAGE slot governed by {@code filter} will ever hold at
     *  once — {@link StorageFilterSpec#maxAmount()} if set (capped further by the item's own max
     *  stack size, never raised above it), otherwise just the item's own max stack size (plain
     *  chest behaviour). */
    public static int effectiveMaxStack(StorageFilterSpec filter, org.bukkit.inventory.ItemStack candidate) {
        int itemMax = candidate.getMaxStackSize();
        if (filter == null || !filter.hasMaxAmount()) return itemMax;
        return Math.min(itemMax, filter.maxAmount());
    }

    /** A {@code Player}-bound context to pass as {@code extraCtx} so a filter script can message
     *  the clicker (e.g. explain a denial) — every STORAGE click handler has a player on hand. */
    public static ScriptContext playerContext(org.bukkit.entity.Player player) {
        if (player == null) return null;
        try {
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            return ScriptContext.builder().player(sp).build();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean matches(String pattern, String id) {
        if (pattern == null) return false;
        return pattern.equalsIgnoreCase(id) || ("minecraft:" + pattern).equalsIgnoreCase(id);
    }

    private static String itemId(net.minecraft.world.item.ItemStack nms) {
        try {
            org.bukkit.inventory.ItemStack bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            net.momirealms.craftengine.core.util.Key ce =
                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
            if (ce != null) return ce.toString();
        } catch (Throwable ignored) {
        }
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(nms.getItem()).toString();
    }
}
