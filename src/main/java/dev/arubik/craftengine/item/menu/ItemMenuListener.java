package dev.arubik.craftengine.item.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import net.minecraft.world.item.ItemStack;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.menu.layout.StorageFilters;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

/**
 * Click/drag/close handling for {@link ItemMenu} — the item-behavior twin of
 * {@code MachineMenuListener}, generalized since there is no block entity underneath: STORAGE
 * behaves like a free chest slot (gated by the page's {@code storage_filter}), BUTTON dispatches
 * built-ins ({@code next_page}/{@code prev_page}/{@code close}) or a {@code "file.pf:function"}
 * script ref, GHOST dispatches its {@code set} script exactly like a machine's, and BACKGROUND/
 * bar slots are immovable.
 */
public final class ItemMenuListener implements Listener {

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ItemMenuListener(), plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemMenu menu)) return;

        // ANY action that could move/replace the item sitting in the EXACT inventory slot this
        // menu's own item occupies (it stays there the whole time the menu is open — see
        // ItemMenu#openPage/onClose) must be denied — not just dropping it. onClose unconditionally
        // does player.getInventory().setItem(originatingSlot, updated): if the backing item was
        // moved OUT of that slot by anything else while the menu was open (dropped, but ALSO
        // hotbar-swapped, shift-clicked via a bulk action, etc.) and something else now occupies
        // that slot — or nothing does — onClose's write creates a FRESH re-serialized copy there
        // while the original stack the player relocated is still sitting wherever they moved it:
        // a real duplication (worse still if that "somewhere else" was one of THIS menu's own
        // storage slots — the saved-back copy then has itself nested as one of its own contents).
        // Was previously only checked for drop actions; that's what actually let a plain hotbar
        // number-key press (HOTBAR_SWAP) on this exact slot duplicate the item.
        if (!menu.offhand() && event.getClickedInventory() == event.getWhoClicked().getInventory()
                && event.getSlot() == menu.originatingSlot()) {
            event.setCancelled(true);
            if (isDropAction(event.getAction()) && event.getWhoClicked() instanceof Player player) {
                runDropBlockedHook(menu, player);
            }
            return;
        }
        // HOTBAR_SWAP can ALSO target the anchor slot while a DIFFERENT slot (one of this menu's
        // own, e.g.) is the one actually clicked — Bukkit swaps the clicked slot's contents with
        // whatever is in the hotbar slot named by getHotbarButton(), regardless of which inventory
        // the click landed in. That would yank the real backing item out of its anchor slot into
        // the menu itself (nesting it into its own storage) the same way the direct-click case above
        // does, just via the opposite slot. menu.originatingSlot() is a PlayerInventory index (0-35,
        // set from wherever the item was held), matching getHotbarButton()'s 0-8 range directly when
        // the item was held from the hotbar — the only case this swap could ever reach it.
        if (!menu.offhand() && event.getAction() == InventoryAction.HOTBAR_SWAP
                && event.getHotbarButton() == menu.originatingSlot()) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            // Shift-click FROM the player's own inventory into this menu.
            if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
            event.setCancelled(true);
            org.bukkit.inventory.ItemStack moving = event.getCurrentItem();
            if (moving == null || moving.getType().isAir()) return;
            int[] storage = menu.storageSlots();
            if (storage.length == 0) return;
            if (!StorageFilters.allows(menu.page().storageFilter(), moving,
                    StorageFilters.playerContext(event.getWhoClicked() instanceof Player p ? p : null))) return;
            org.bukkit.inventory.ItemStack leftover = mergeInto(event.getInventory(), storage,
                    moving.clone(), menu.page().storageFilter());
            event.setCurrentItem(leftover);
            return;
        }

        MenuSlotType type = classify(menu, slot);
        switch (type) {
            case STORAGE -> handleStorageClick(event, menu);
            case BUTTON -> {
                event.setCancelled(true);
                if (!(event.getWhoClicked() instanceof Player player)) return;
                handleButton(menu, menu.buttons().get(slot), player, slot, event.getClick());
            }
            case GHOST -> {
                event.setCancelled(true);
                if (!(event.getWhoClicked() instanceof Player player)) return;
                handleGhost(menu, slot, menu.ghostSlotsBySlot().get(slot), player, event.getCursor(), event.getClick());
            }
            default -> event.setCancelled(true); // BACKGROUND / bar slots
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemMenu menu)) return;
        for (int slot : event.getRawSlots()) {
            if (slot >= event.getInventory().getSize()) continue;
            MenuSlotType type = classify(menu, slot);
            if (type != MenuSlotType.STORAGE) {
                event.setCancelled(true);
                return;
            }
        }
        org.bukkit.inventory.ItemStack cursor = event.getOldCursor();
        if (cursor != null && !cursor.getType().isAir()
                && !StorageFilters.allows(menu.page().storageFilter(), cursor,
                        StorageFilters.playerContext(event.getWhoClicked() instanceof Player p ? p : null))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemMenu menu)) return;
        if (!(event.getPlayer() instanceof Player player) || !player.getUniqueId().equals(menu.playerId())) return;

        menu.saveStorageToWorkingStack();

        org.bukkit.inventory.ItemStack updated = menu.workingStack().asBukkitCopy();
        if (menu.offhand()) {
            player.getInventory().setItemInOffHand(updated);
        } else {
            player.getInventory().setItem(menu.originatingSlot(), updated);
        }
    }

    // ------------------------------------------------------------------- helpers

    /** Enforces both the identity filter AND the page's {@code storage_filter.max_amount} cap on
     *  a direct click into a STORAGE slot — capping (splitting the excess back onto the cursor)
     *  rather than flatly denying, so a capped slot still behaves like a slot, just a smaller one. */
    private void handleStorageClick(InventoryClickEvent event, ItemMenu menu) {
        if (!isPlaceAction(event.getAction())) return;
        org.bukkit.inventory.ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType().isAir()) return;

        MachineDefinition.PageDef.StorageFilterSpec filter = menu.page().storageFilter();
        Player clicker = event.getWhoClicked() instanceof Player p ? p : null;
        if (!StorageFilters.allows(filter, cursor, StorageFilters.playerContext(clicker))) {
            event.setCancelled(true);
            return;
        }
        if (!filter.hasMaxAmount() || clicker == null) return;

        int maxStack = StorageFilters.effectiveMaxStack(filter, cursor);
        org.bukkit.inventory.ItemStack current = event.getCurrentItem();
        boolean stackable = current != null && !current.getType().isAir() && current.isSimilar(cursor);
        int currentAmount = stackable ? current.getAmount() : 0;
        int placing = event.getAction() == InventoryAction.PLACE_ONE ? 1 : cursor.getAmount();
        if (currentAmount + placing <= maxStack) return; // within cap — let vanilla handle it

        event.setCancelled(true);
        int space = Math.max(0, maxStack - currentAmount);
        if (space <= 0) return;
        int move = Math.min(space, placing);
        org.bukkit.inventory.ItemStack placed = stackable ? current.clone() : cursor.clone();
        placed.setAmount(currentAmount + move);
        event.getInventory().setItem(event.getRawSlot(), placed);
        org.bukkit.inventory.ItemStack remaining = cursor.clone();
        remaining.setAmount(cursor.getAmount() - move);
        clicker.setItemOnCursor(remaining.getAmount() > 0 ? remaining : null);
    }

    private MenuSlotType classify(ItemMenu menu, int slot) {
        for (int s : menu.storageSlots()) if (s == slot) return MenuSlotType.STORAGE;
        if (menu.buttons().containsKey(slot)) return MenuSlotType.BUTTON;
        if (menu.ghostSlotsBySlot().containsKey(slot)) return MenuSlotType.GHOST;
        for (MachineDefinition.BarRef ref : menu.page().bars())
            for (int s : ref.slots()) if (s == slot) return MenuSlotType.DYNAMIC;
        return MenuSlotType.BACKGROUND;
    }

    private void handleButton(ItemMenu menu, MachineDefinition.ButtonSpec spec, Player player,
            int slot, org.bukkit.event.inventory.ClickType click) {
        if (spec == null) return;
        String action = spec.action();
        if (action == null || action.isBlank() || action.equals("none")) return;

        switch (action) {
            case "close" -> player.closeInventory();
            case "next_page" -> switchPage(menu, player, menu.pageIndex() + 1);
            case "prev_page" -> switchPage(menu, player, menu.pageIndex() - 1);
            default -> {
                if (!action.contains(".pf:")) return;
                menu.saveStorageToWorkingStack();
                String clickName = click != null ? click.name().toLowerCase(java.util.Locale.ROOT) : "left";
                ItemStack result = runHook(action, menu.workingStack(), player, slot, clickName);
                menu.setWorkingStack(result);
                menu.render();
            }
        }
    }

    private void switchPage(ItemMenu menu, Player player, int newIndex) {
        if (newIndex < 0 || newIndex >= menu.definition().pages().size()) return;
        menu.saveStorageToWorkingStack();
        ItemMenu.openPage(player, menu.workingStack(), menu.definition(), newIndex);
    }

    private void handleGhost(ItemMenu menu, int slot, MachineDefinition.PageDef.GhostSlotSpec spec,
            Player player, org.bukkit.inventory.ItemStack cursor, org.bukkit.event.inventory.ClickType click) {
        if (spec == null || spec.setRef() == null) return;
        boolean cursorEmpty = cursor == null || cursor.getType().isAir();
        try {
            ScriptCall call = ScriptCall.parse(spec.setRef());
            if (call == null) return;
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            String clickTypeName = click != null ? click.name().toLowerCase(java.util.Locale.ROOT) : "left";
            String clickedIdStr = cursorEmpty ? "" : itemStackId(cursor);
            ScriptContext.Builder b = ScriptContext.builder()
                    .item("item", menu.workingStack())
                    .player(sp)
                    .val("slot", ScriptValue.of(slot))
                    .str("clicked_id", clickedIdStr)
                    .str("click_type", clickTypeName)
                    .event(new dev.arubik.craftengine.script.event.GhostSlotEvent(slot, clickedIdStr, clickTypeName));
            b.val("clicked_item", cursorEmpty ? ScriptValue.NULL
                    : ScriptValue.ofItem(CraftItemStack.asNMSCopy(cursor)));
            bindCommonNamespaces(b);
            call.execute(b.build());
            menu.render();
        } catch (Throwable ignored) {
        }
    }

    /** Runs a {@code "file.pf:function"} ref with {@code item}/{@code player}/{@code event}
     *  ({@link dev.arubik.craftengine.script.event.ButtonEvent}) bound and applies back whatever
     *  the script leaves in {@code item} — same convention as {@code DataItemBehavior#runHook}. */
    private ItemStack runHook(String ref, ItemStack stack, Player player, int slot, String clickType) {
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) return stack;
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            ScriptContext.Builder b = ScriptContext.builder().item("item", stack).player(sp)
                    .event(new dev.arubik.craftengine.script.event.ButtonEvent(slot, clickType));
            bindCommonNamespaces(b);
            ScriptContext ctx = b.build();
            ScriptContext result = call.execute(ctx);
            ScriptValue iv = result.getVar("item");
            if (iv instanceof ScriptValue.Item itemVal && itemVal.stack() != null) return itemVal.stack();
        } catch (Throwable ignored) {
        }
        return stack;
    }

    /** Same namespace singletons every OTHER script-firing entry point in this codebase binds (Cmd
     *  execution, the generic events bridge, TaskManager, machine scripts) — an item-menu button/
     *  ghost-slot/drop-blocked script is just as likely to want to open ANOTHER menu, show a
     *  dialog, schedule a task, or register a temporary event listener as any of those. */
    private static void bindCommonNamespaces(ScriptContext.Builder b) {
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
    }

    private static String itemStackId(org.bukkit.inventory.ItemStack stack) {
        try {
            var ceId = net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(stack);
            if (ceId != null) return ceId.toString();
        } catch (Throwable ignored) {
        }
        return stack.getType().getKey().toString();
    }

    private static boolean isDropAction(InventoryAction action) {
        return action == InventoryAction.DROP_ONE_SLOT || action == InventoryAction.DROP_ALL_SLOT;
    }

    /** Runs the item's declared {@code on_drop_blocked} script (if any) after a drop of the menu's
     *  own backing item was denied — purely a notification hook (a sound, a message); the deny
     *  itself already happened and isn't something the script can veto or approve, structural
     *  invariants aren't policy. Mirrors {@code DataItemBehavior#runHook}'s event-shape convention. */
    private void runDropBlockedHook(ItemMenu menu, Player player) {
        String ref = menu.definition().script("on_drop_blocked");
        if (ref == null) return;
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) return;
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            ScriptContext.Builder b = ScriptContext.builder().item("item", menu.workingStack()).player(sp)
                    .event(new dev.arubik.craftengine.script.event.ItemActionEvent("on_drop_blocked"));
            bindCommonNamespaces(b);
            ScriptContext ctx = b.build();
            call.execute(ctx);
        } catch (Throwable ignored) {}
    }

    private static boolean isPlaceAction(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR
                || action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD;
    }

    private static org.bukkit.inventory.ItemStack mergeInto(org.bukkit.inventory.Inventory inv, int[] slots,
            org.bukkit.inventory.ItemStack stack, MachineDefinition.PageDef.StorageFilterSpec filter) {
        int maxStack = StorageFilters.effectiveMaxStack(filter, stack);
        org.bukkit.inventory.ItemStack cur;
        for (int s : slots) {
            int space;
            if (stack.getAmount() <= 0) return null;
            cur = inv.getItem(s);
            if (cur == null || cur.getType().isAir() || !cur.isSimilar(stack)
                    || (space = maxStack - cur.getAmount()) <= 0) continue;
            int move = Math.min(space, stack.getAmount());
            cur.setAmount(cur.getAmount() + move);
            inv.setItem(s, cur);
            stack.setAmount(stack.getAmount() - move);
        }
        for (int s : slots) {
            if (stack.getAmount() <= 0) return null;
            cur = inv.getItem(s);
            if (cur != null && !cur.getType().isAir()) continue;
            int move = Math.min(maxStack, stack.getAmount());
            org.bukkit.inventory.ItemStack placed = stack.clone();
            placed.setAmount(move);
            inv.setItem(s, placed);
            stack.setAmount(stack.getAmount() - move);
        }
        return stack.getAmount() > 0 ? stack : null;
    }
}
