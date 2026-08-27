package dev.arubik.craftengine.menu;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.ItemType;
import dev.arubik.craftengine.script.types.world.ServerType;

/**
 * Click/drag/close handling for the standalone {@link ScriptMenu} system — the lightweight sibling
 * of {@code MachineMenuListener}/{@code ItemMenuListener} for a one-shot GUI a script pops open for
 * a player that isn't backed by any block or item. Every slot in a script menu is a fixed
 * display/button, never real storage, so a click is always cancelled outright; the only thing a
 * click can do is fire whatever {@code "file.pf:function"} script was attached to that slot.
 */
public final class ScriptMenuListener implements Listener {

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ScriptMenuListener(), plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ScriptMenu menu = ScriptMenuRegistry.get(event.getInventory());
        if (menu == null) return;
        event.setCancelled(true);
        try {
            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) return; // player's own inventory half
            Runnable javaAction = menu.javaAction(rawSlot);
            if (javaAction != null) { javaAction.run(); return; }
            String clickScript = menu.clickScript(rawSlot);
            if (clickScript == null) return;
            if (!(event.getWhoClicked() instanceof Player player)) return;

            String clickType = event.getClick() != null
                    ? event.getClick().name().toLowerCase(Locale.ROOT) : "left";
            Map<String, ScriptValue> data = menu.clickData(rawSlot);

            ScriptContext.Builder b = ScriptContext.builder();
            bindCommonNamespaces(b);
            bindExtra(b, menu);
            b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle());
            b.typed("MenuClick", new MenuClickInvocation(rawSlot, clickType, data));

            ScriptCall call = ScriptCall.parse(clickScript);
            if (call != null) call.execute(b.build());
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Menu] click script threw", t);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        ScriptMenu menu = ScriptMenuRegistry.get(event.getInventory());
        if (menu == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        ScriptMenu menu = ScriptMenuRegistry.get(inv);
        if (menu == null) return;
        try {
            String closeScript = menu.closeScript();
            if (closeScript != null && !closeScript.isBlank()
                    && event.getPlayer() instanceof Player player) {
                ScriptContext.Builder b = ScriptContext.builder();
                bindCommonNamespaces(b);
                bindExtra(b, menu);
                b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle());
                ScriptCall call = ScriptCall.parse(closeScript);
                if (call != null) call.execute(b.build());
            }
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Menu] close script threw", t);
        } finally {
            ScriptMenuRegistry.untrack(inv);
        }
    }

    /** Same namespace singletons every OTHER script-firing entry point this session binds (Cmd
     *  execution, the generic events bridge, TaskManager) — a click/close script is just as likely
     *  to want to register a countdown, schedule a follow-up, build an item, or open ANOTHER menu
     *  as any of those, so it gets the same baseline instead of a hand-picked subset. */
    private static void bindCommonNamespaces(ScriptContext.Builder b) {
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
    }

    /** Applies whatever extra class-instance bindings the menu itself carries (see {@code
     *  ScriptMenu#bindExtra}) — e.g. {@code CmdRegistry}'s declarative pages attach {@code Cmd}
     *  so a page button's click script can call {@code Cmd.open_page(...)}. */
    private static void bindExtra(ScriptContext.Builder b, ScriptMenu menu) {
        for (var entry : menu.extraBindings().entrySet()) {
            b.typed(entry.getKey(), entry.getValue());
        }
    }
}
