package dev.arubik.craftengine.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * One open (or about-to-open) standalone script menu — a fixed grid of display/button items with
 * no backing block, built incrementally by the {@code Menu}/{@code MenuBuilder} script API (see
 * {@code dev.arubik.craftengine.script.types.menu.MenuType}) before being shown to a player via
 * {@code .open(player)}. Unlike {@code MachineMenu}, this is a plain data holder — no page system,
 * no storage slots, every slot is either decorative or a button with an optional click script.
 */
public final class ScriptMenu {

    private final Inventory inventory;
    private final Map<Integer, String> clickScripts = new HashMap<>();
    private final Map<Integer, Map<String, ScriptValue>> clickData = new HashMap<>();
    private final Map<Integer, Runnable> javaActions = new HashMap<>();
    private final Map<String, Object> extraBindings = new HashMap<>();
    private String closeScript;

    public ScriptMenu(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    /** Sets the item shown at {@code slot} and (optionally) the script/data fired on a click there.
     *  {@code clickScript} null/blank leaves the slot purely decorative; {@code data} null clears
     *  any previously-attached data map for that slot. */
    public void setItem(int slot, ItemStack item, String clickScript, Map<String, ScriptValue> data) {
        inventory.setItem(slot, item);
        if (clickScript != null && !clickScript.isBlank()) {
            clickScripts.put(slot, clickScript);
        } else {
            clickScripts.remove(slot);
        }
        if (data != null) {
            clickData.put(slot, data);
        } else {
            clickData.remove(slot);
        }
    }

    public String clickScript(int slot) {
        return clickScripts.get(slot);
    }

    public Map<String, ScriptValue> clickData(int slot) {
        return clickData.getOrDefault(slot, Map.of());
    }

    /** A Java-side click handler for {@code slot}, checked BEFORE any script click ref — for a
     *  built-in action (like {@code CmdRegistry}'s {@code "page:<name>"} in-menu navigation) that
     *  needs to run regardless of whether a .pf script is even involved for this page. */
    public void setJavaAction(int slot, Runnable action) {
        if (action != null) javaActions.put(slot, action);
        else javaActions.remove(slot);
    }

    public Runnable javaAction(int slot) {
        return javaActions.get(slot);
    }

    /** Extra class-instance bindings a click/close script fired from THIS menu should get on top
     *  of the usual common namespaces — kept fully generic (this package has no dependency on
     *  {@code dev.arubik.craftengine.cmd}) so any future declarative-page-style system can attach
     *  its own without this class needing to know about it. Used by {@code CmdRegistry}'s
     *  declarative {@code pages} to bind {@code Cmd} (so a page button's click script can call
     *  {@code Cmd.open_page(...)} for in-menu navigation/pagination). */
    public void bindExtra(String className, Object instance) {
        if (instance != null) extraBindings.put(className, instance);
        else extraBindings.remove(className);
    }

    public Map<String, Object> extraBindings() {
        return extraBindings;
    }

    public void setCloseScript(String closeScript) {
        this.closeScript = closeScript;
    }

    public String closeScript() {
        return closeScript;
    }
}
