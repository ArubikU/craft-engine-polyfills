package dev.arubik.craftengine.script.types.menu;

import java.util.LinkedHashMap;
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
            // Migrated to methodTypedOpt2: `title` is an optional trailing argument defaulting to
            // "Menu", so a 1-arg call must still build the menu — methodTyped2's onMissingArgs
            // would wrongly short-circuit it, but methodTypedOptN always runs the body. `size`
            // takes a Java `null` default used purely as an "argument was absent" sentinel,
            // reproducing the original `args.isEmpty()` early return EXACTLY (a decoded DOUBLE is
            // never null — asNum() always yields a real double — so null can only mean "not
            // passed").
            .methodTypedOpt2("create", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, "Menu", TypeCodecs.RAW,
                (Object obj, Double sizeArg, String title) -> {
                    if (sizeArg == null) return ScriptValue.NULL;
                    int size = normalizeSize(sizeArg.intValue());
                    Inventory inv = Bukkit.createInventory(null, size, toDisplayComponent(title));
                    return ScriptValue.ofObj("MenuBuilder", new ScriptMenu(inv));
                });

        PolyTypeRegistry.define("MenuBuilder")
            // set_item(slot, item, click_script?, click_data?) — click_script is an optional
            // "file.pf:function" ref (omit for a purely decorative item); click_data is an optional
            // make_map(...) value handed back as MenuClick.get(...) when that slot is clicked.
            // Migrated to methodTypedOpt4: click_script/click_data are optional trailing arguments
            // and the body still runs (and still returns the live builder) when they're omitted,
            // which is methodTypedOptN's shape. Java `null` defaults act as "argument was absent"
            // sentinels — a decoded DOUBLE/STRING is never null and a decoded RAW is never null, so
            // each null reproduces exactly one of the original args.size() branches: slot/item
            // absent -> the unchanged builder, click_script absent -> null (what setItem expects),
            // click_data absent -> null (NOT dataMap's Map.of(), which is what a PRESENT non-map
            // argument yields — that distinction is preserved).
            // `item` stays TypeCodecs.RAW: it needs an instanceof narrowing to ScriptValue.Item.
            .methodTypedOpt4("set_item", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.STRING, (String) null,
                TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.RAW,
                (ScriptMenu menu, Double slotArg, ScriptValue itemArg, String clickScript, ScriptValue dataArg) -> {
                    if (slotArg == null || !(itemArg instanceof ScriptValue.Item itemVal) || itemVal.stack() == null) {
                        return ScriptValue.ofObj("MenuBuilder", menu);
                    }
                    int slot = slotArg.intValue();
                    org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(itemVal.stack());
                    Map<String, ScriptValue> data = dataArg == null ? null : dataMap(dataArg);
                    menu.setItem(slot, bukkitItem, clickScript, data);
                    return ScriptValue.ofObj("MenuBuilder", menu);
                })
            // Migrated to methodTypedOpt1: on a missing arg this still returns the live
            // ScriptValue.ofObj("MenuBuilder", menu) wrapping THIS call's own instance — an
            // instance-dependent fallback methodTyped1's fixed onMissingArgs can't express, but
            // methodTypedOptN always runs the body so the instance is in hand. The null default is
            // the "argument was absent" sentinel (see set_item above).
            .methodTypedOpt1("set_close_script", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                (ScriptMenu menu, String script) -> {
                    if (script != null) menu.setCloseScript(script);
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

    /** Copied from {@code TaskManagerType#dataMap} — same "make_map(...) value" extraction, taking
     *  the already-decoded argument now that set_item is a typed registration. Only called for an
     *  argument that was actually PRESENT (a missing one is null and never reaches here), so this
     *  keeps the old list form's "present but not a Map -> Map.of()" behaviour. */
    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> dataMap(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && "Map".equals(o.typeName()) && o.instance() instanceof Map<?, ?> m) {
            return new LinkedHashMap<>((Map<String, ScriptValue>) m);
        }
        return Map.of();
    }
}
