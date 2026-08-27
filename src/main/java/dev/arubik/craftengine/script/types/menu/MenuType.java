package dev.arubik.craftengine.script.types.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import dev.arubik.craftengine.menu.ScriptMenu;
import dev.arubik.craftengine.menu.ScriptMenuRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

/**
 * {@code Menu}/{@code MenuBuilder} — the script-facing API for a standalone, one-shot GUI that
 * isn't backed by any block or item (see {@code dev.arubik.craftengine.menu.ScriptMenu}). {@code
 * Menu.create(size, title)} returns a chainable {@code MenuBuilder}; {@code .set_item(...)} adds
 * icons and (optionally) a click script/data slot by slot; {@code .open(player)} shows it and
 * starts tracking clicks via {@link ScriptMenuRegistry}. Kept deliberately thin next to the heavy
 * {@code MachineLayout}/{@code MachineMenu} system — no pages, no storage slots, just a fixed grid
 * of buttons for a script to pop open ad hoc (e.g. {@code /tpa list}).
 */
public final class MenuType {

    /** Namespace singleton bound as the bare {@code Menu} identifier — see {@code ItemType.NAMESPACE}
     *  for the same pattern applied to {@code Item.skull(...)}. */
    public static final Object INSTANCE = new Object();

    private MenuType() {}

    public static void register() {
        PolyTypeRegistry.define("Menu")
            // Menu.create(size, title) — size is rounded up to the next multiple of 9 and clamped
            // to a legal chest inventory (9..54). A future nicety could accept an InventoryType
            // name instead of a bare size; skipped for now since a chest grid covers the main case.
            // NOT migrated to methodTyped: `title` is an optional trailing argument with a default
            // ("Menu") that only applies when present alongside a REQUIRED size arg — a typed
            // handler's onMissingArgs fallback would trigger on args.size()<2 too, which changes
            // behavior for a caller passing just size (currently valid: title defaults to "Menu").
            // Left untyped.
            .method("create", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                int size = normalizeSize((int) args.get(0).asNum());
                String title = args.size() >= 2 ? args.get(1).asStr() : "Menu";
                Inventory inv = Bukkit.createInventory(null, size, toDisplayComponent(title));
                return ScriptValue.ofObj("MenuBuilder", new ScriptMenu(inv));
            });

        PolyTypeRegistry.define("MenuBuilder")
            // set_item(slot, item, click_script?, click_data?) — click_script is an optional
            // "file.pf:function" ref (omit for a purely decorative item); click_data is an optional
            // make_map(...) value handed back as MenuClick.get(...) when that slot is clicked.
            // NOT migrated to methodTyped: click_script/click_data are optional trailing arguments
            // (args.size() checks decide whether to apply them) — not expressible with a typed
            // handler's fixed-arity decoded arguments. Left untyped.
            .method("set_item", (obj, args) -> {
                ScriptMenu menu = menu(obj);
                if (args.size() < 2 || !(args.get(1) instanceof ScriptValue.Item itemVal) || itemVal.stack() == null) {
                    return ScriptValue.ofObj("MenuBuilder", menu);
                }
                int slot = (int) args.get(0).asNum();
                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(itemVal.stack());
                String clickScript = args.size() >= 3 ? args.get(2).asStr() : null;
                Map<String, ScriptValue> data = args.size() >= 4 ? dataMap(args, 3) : null;
                menu.setItem(slot, bukkitItem, clickScript, data);
                return ScriptValue.ofObj("MenuBuilder", menu);
            })
            // NOT migrated to methodTyped: on a missing arg this still returns the live
            // ScriptValue.ofObj("MenuBuilder", menu) wrapping THIS call's own instance — an
            // instance-dependent fallback a typed handler's onMissingArgs (a fixed value chosen at
            // registration time) cannot express. Left untyped.
            .method("set_close_script", (obj, args) -> {
                ScriptMenu menu = menu(obj);
                if (!args.isEmpty()) menu.setCloseScript(args.get(0).asStr());
                return ScriptValue.ofObj("MenuBuilder", menu);
            })
            // open(player) — shows the inventory THEN tracks it, so a failed openInventory (closed
            // world, invalid state, whatever) never leaves a tracked-but-never-shown menu behind.
            // Argument decoded as TypeCodecs.RAW (not a plain asStr()/asNum()/asBool() coercion) —
            // extractBukkitPlayer(ScriptValue) does more than a cast (unwraps a ScriptValue.Obj,
            // checks its instance is an NMS Entity, then narrows to its Bukkit Player), so per the
            // migration rules it's kept as an explicit in-body call rather than a codec.
            .methodTyped1("open", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (ScriptMenu menu, ScriptValue playerArg) -> {
                    Player bukkitPlayer = extractBukkitPlayer(playerArg);
                    if (bukkitPlayer == null) return false;
                    try {
                        bukkitPlayer.openInventory(menu.getInventory());
                        ScriptMenuRegistry.track(menu.getInventory(), menu);
                        return true;
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
    }

    private static int normalizeSize(int requested) {
        int clamped = Math.max(1, Math.min(54, requested));
        int rows = (clamped + 8) / 9;
        return Math.max(1, rows) * 9;
    }

    /** Same MiniMessage-if-tagged / legacy-ampersand-otherwise heuristic {@code PlayerType
     *  #send_message} already uses — kept consistent rather than inventing a second convention. */
    private static Component toDisplayComponent(String text) {
        if (text == null) return Component.empty();
        try {
            if (text.contains("<") && text.contains(">")) {
                return MiniMessage.miniMessage().deserialize(text);
            }
            return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        } catch (Throwable ignored) {
            return Component.text(text);
        }
    }

    /** Pulls a real Bukkit {@link Player} out of a wrapped NMS {@code Player}/{@code Entity}
     *  script value — same idiom {@code Item.skull(...)} uses (see {@code ItemType}). */
    private static Player extractBukkitPlayer(ScriptValue v) {
        if (!(v instanceof ScriptValue.Obj o) || !(o.instance() instanceof net.minecraft.world.entity.Entity nmsEntity)) {
            return null;
        }
        org.bukkit.entity.Entity bukkit = nmsEntity.getBukkitEntity();
        return bukkit instanceof Player p ? p : null;
    }

    /** Copied from {@code TaskManagerType#dataMap} — same "optional trailing make_map(...) value"
     *  extraction, adjusted for this method's own arg position. */
    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> dataMap(List<ScriptValue> args, int index) {
        if (args.size() <= index) return Map.of();
        ScriptValue v = args.get(index);
        if (v instanceof ScriptValue.Obj o && "Map".equals(o.typeName()) && o.instance() instanceof Map<?, ?> m) {
            return new LinkedHashMap<>((Map<String, ScriptValue>) m);
        }
        return Map.of();
    }

    private static ScriptMenu menu(Object obj) {
        return (ScriptMenu) obj;
    }
}
