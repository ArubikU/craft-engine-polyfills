package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * {@link PolyClass} wrapping an NMS {@link net.minecraft.server.level.ServerPlayer}.
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code name}         — player login name</li>
 *   <li>{@code display_name} — display name (plain text)</li>
 *   <li>{@code health}       — current health (float)</li>
 *   <li>{@code max_health}   — max health from attribute</li>
 *   <li>{@code food}         — food level (0–20)</li>
 *   <li>{@code level}        — XP level</li>
 *   <li>{@code exp}          — XP progress within current level (0.0–1.0)</li>
 *   <li>{@code game_mode}    — "survival" | "creative" | "adventure" | "spectator"</li>
 *   <li>{@code is_sneaking}  — shift-key held</li>
 *   <li>{@code is_sprinting} — sprinting flag</li>
 *   <li>{@code is_flying}    — flying ability active</li>
 *   <li>{@code is_op}        — has op-level 4 permissions</li>
 *   <li>{@code x}, {@code y}, {@code z} — world position</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code has_item("minecraft:stone")}   — true if vanilla material is anywhere in the inventory</li>
 *   <li>{@code item_count("minecraft:stone")} — total count of matching items in inventory</li>
 *   <li>{@code has_permission("perm")}        — permission check (delegates to Paper CraftPlayer when available)</li>
 * </ul>
 */
public final class PlayerClass implements PolyClass {

    private final net.minecraft.server.level.ServerPlayer player;

    public PlayerClass(net.minecraft.server.level.ServerPlayer player) {
        this.player = player;
    }

    @Override
    public PolyValue get(String property) {
        if (player == null) return PolyValue.NULL;
        return switch (property) {
            case "name"         -> PolyValue.of(player.getScoreboardName());
            case "display_name" -> {
                try { yield PolyValue.of(player.getDisplayName().getString()); }
                catch (Throwable ignored) { yield PolyValue.of(player.getScoreboardName()); }
            }
            case "health"       -> PolyValue.of(player.getHealth());
            case "max_health"   -> {
                try {
                    var inst = player.getAttribute(
                            net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
                    yield PolyValue.of(inst != null ? inst.getValue() : 20.0);
                } catch (Throwable ignored) { yield PolyValue.of(20.0); }
            }
            case "food"         -> PolyValue.of(player.getFoodData().foodLevel);
            case "level"        -> PolyValue.of(player.experienceLevel);
            case "exp"          -> PolyValue.of(player.experienceProgress);
            case "game_mode"    -> {
                try { yield PolyValue.of(player.gameMode.getGameModeForPlayer().getName()); }
                catch (Throwable ignored) { yield PolyValue.of("survival"); }
            }
            case "is_sneaking"  -> PolyValue.of(player.isShiftKeyDown());
            case "is_sprinting" -> PolyValue.of(player.isSprinting());
            case "is_flying"    -> PolyValue.of(player.getAbilities().flying);
            case "is_op"        -> PolyValue.of(player.getBukkitEntity().isOp());
            case "x"            -> PolyValue.of(player.getX());
            case "y"            -> PolyValue.of(player.getY());
            case "z"            -> PolyValue.of(player.getZ());
            case "location"     -> new PolyValue.Obj(LocationClass.forEntity(player));
            case "world"        -> {
                if (player.level() instanceof net.minecraft.server.level.ServerLevel sl)
                    yield new PolyValue.Obj(WorldClass.forLevel(sl));
                yield PolyValue.NULL;
            }
            // Use NMS inventory directly — no Bukkit entity bridge
            case "inventory"    -> new PolyValue.Obj(new InventoryClass(
                    new org.bukkit.craftbukkit.inventory.CraftInventory(player.getInventory())));
            case "main_hand"    -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getMainHandItem()));
            case "off_hand"     -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getOffhandItem()));
            case "helmet"       -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)));
            case "chestplate"   -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)));
            case "leggings"     -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)));
            case "boots"        -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)));
            default             -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (player == null) return PolyValue.NULL;
        return switch (method) {

            // has_item("minecraft:stone") → bool
            case "has_item" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String id = args.get(0).asStr();
                yield PolyValue.of(countInInventory(id) > 0);
            }

            // item_count("minecraft:stone") → number
            case "item_count" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                yield PolyValue.of(countInInventory(args.get(0).asStr()));
            }

            // has_permission("some.perm") → bool
            case "has_permission" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String perm = args.get(0).asStr();
                // Paper exposes the full permission engine via getBukkitEntity()
                try { yield PolyValue.of(player.getBukkitEntity().hasPermission(perm)); }
                catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }

            // main_hand() → item in main hand
            case "main_hand" -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getMainHandItem()));

            // off_hand() → item in off hand
            case "off_hand" -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getOffhandItem()));

            // helmet() → item in helmet slot
            case "helmet" -> PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)));

            default -> get(method);
        };
    }

    /** Total amount of the given material key across all inventory slots. */
    private int countInInventory(String id) {
        // Use Bukkit inventory — simpler, no private-field access
        try {
            org.bukkit.Material mat = org.bukkit.Material.matchMaterial(
                    id.contains(":") ? id : "minecraft:" + id);
            if (mat == null || mat.isAir()) return 0;
            int total = 0;
            org.bukkit.entity.Player bp = player.getBukkitEntity();
            for (org.bukkit.inventory.ItemStack stack : bp.getInventory().getContents()) {
                if (stack != null && stack.getType() == mat) total += stack.getAmount();
            }
            // Armour + offhand
            for (org.bukkit.inventory.ItemStack stack : bp.getInventory().getArmorContents()) {
                if (stack != null && stack.getType() == mat) total += stack.getAmount();
            }
            org.bukkit.inventory.ItemStack offhand = bp.getInventory().getItemInOffHand();
            if (!offhand.getType().isAir() && offhand.getType() == mat) total += offhand.getAmount();
            return total;
        } catch (Throwable ignored) { return 0; }
    }

    /** The underlying NMS player. */
    public net.minecraft.server.level.ServerPlayer player() { return player; }
}
