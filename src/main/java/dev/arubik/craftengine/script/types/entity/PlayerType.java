package dev.arubik.craftengine.script.types.entity;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Player extends Entity — inherits all Entity properties and methods,
 * adds player-specific ones.
 */
public final class PlayerType {

    private PlayerType() {}

    public static void register() {
        PolyTypeRegistry.define("Player", "LivingEntity")
            .property("food_level", obj -> ScriptValue.of(player(obj).getFoodData().getFoodLevel()))
            .property("saturation", obj -> ScriptValue.of(player(obj).getFoodData().getSaturationLevel()))
            .property("xp_level", obj -> ScriptValue.of(player(obj).experienceLevel))
            .property("xp_progress", obj -> ScriptValue.of(player(obj).experienceProgress))
            .property("gamemode", obj -> {
                if (player(obj) instanceof ServerPlayer sp) {
                    return ScriptValue.of(sp.gameMode.getGameModeForPlayer().getName());
                }
                return ScriptValue.of("survival");
            })
            .property("is_flying", obj -> ScriptValue.of(player(obj).getAbilities().flying))
            .property("is_creative", obj -> ScriptValue.of(player(obj).getAbilities().instabuild))
            .property("main_hand", obj -> ScriptValue.ofItem(player(obj).getMainHandItem()))
            .property("off_hand", obj -> ScriptValue.ofItem(player(obj).getOffhandItem()))
            .method("send_message", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String text = args.get(0).asStr();
                if (player(obj) instanceof ServerPlayer sp) {
                    try {
                        Component msg;
                        if (text.contains("<") && text.contains(">")) {
                            msg = MiniMessage.miniMessage().deserialize(text);
                        } else {
                            msg = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
                        }
                        sp.getBukkitEntity().sendMessage(msg);
                        return ScriptValue.of(true);
                    } catch (Throwable ignored) {
                        try { sp.getBukkitEntity().sendMessage(text); return ScriptValue.of(true); } catch (Throwable e2) {}
                    }
                }
                return ScriptValue.of(false);
            })
            .method("give_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                ScriptValue itemArg = args.get(0);
                ItemStack toGive = null;
                if (itemArg instanceof ScriptValue.Item i) toGive = i.stack().copy();
                else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toGive = is.copy();
                if (toGive == null || toGive.isEmpty()) return ScriptValue.of(false);
                Player p = player(obj);
                boolean added = p.getInventory().add(toGive);
                if (!added) p.drop(toGive, false);
                return ScriptValue.of(true);
            })
            .method("remove_item", (obj, args) -> {
                // remove_item("main_hand"|"off_hand"|slotN, count)
                if (args.isEmpty()) return ScriptValue.of(false);
                Player p = player(obj);
                String slot = args.get(0).asStr();
                int count = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
                if ("main_hand".equals(slot)) {
                    ItemStack s = p.getMainHandItem();
                    s.shrink(count);
                    return ScriptValue.of(true);
                } else if ("off_hand".equals(slot)) {
                    p.getOffhandItem().shrink(count);
                    return ScriptValue.of(true);
                } else {
                    try {
                        int idx = Integer.parseInt(slot);
                        p.getInventory().getItem(idx).shrink(count);
                        return ScriptValue.of(true);
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.of(false);
            })
            .method("get_inventory_slot", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                int slot = (int) args.get(0).asNum();
                Player p = player(obj);
                if (slot < 0 || slot >= p.getInventory().getContainerSize()) return ScriptValue.NULL;
                ItemStack stack = p.getInventory().getItem(slot);
                return stack.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(stack);
            });
    }

    public static ScriptValue wrap(Player player) {
        if (player == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Player", player);
    }

    private static Player player(Object obj) {
        return (Player) obj;
    }
}
