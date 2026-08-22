package dev.arubik.craftengine.item.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import dev.arubik.craftengine.item.ItemDefinition;
import dev.arubik.craftengine.item.ItemStateData;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.bar.BarDefinition;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.item.ItemStack;

/**
 * The item-behavior menu: opens ONE page of a {@link ItemDefinition}'s {@code pages()} built from
 * the exact same {@link MachineDefinition.PageDef} schema machines/multiblocks use — buttons,
 * bars, paging, ghost slots, and free {@link MenuSlotType#STORAGE} slots — but backed by the
 * item stack's own NBT (see {@link ItemStateData}) instead of a block entity.
 *
 * <p>Replaces the old plain-{@code minecraft:container} backpack: that only ever supported a flat
 * grid of storage, not "ALL the page functions" pages give machines.
 */
public final class ItemMenu implements InventoryHolder {

    private final ItemDefinition definition;
    private final int pageIndex;
    private final MachineDefinition.PageDef page;
    private final UUID playerId;
    private final int slot;      // originating inventory slot, or -1 if offhand
    private final boolean offhand;
    private final Inventory inventory;

    /** The current, possibly-script-mutated held stack (NMS) — read back on close. */
    private ItemStack workingStack;

    private final int[] storageSlots;
    private final Map<Integer, MachineDefinition.ButtonSpec> buttons = new HashMap<>();
    private final Map<Integer, MachineDefinition.PageDef.GhostSlotSpec> ghostSlots = new HashMap<>();
    private final Map<Integer, MachineDefinition.PageDef.StaticSlot> layoutSlots = new HashMap<>();

    private ItemMenu(ItemDefinition definition, int pageIndex, UUID playerId, int slot, boolean offhand,
            ItemStack workingStack) {
        this.definition = definition;
        this.pageIndex = pageIndex;
        this.page = definition.pages().get(pageIndex);
        this.playerId = playerId;
        this.slot = slot;
        this.offhand = offhand;
        this.workingStack = workingStack;
        this.storageSlots = page.storageSlots();

        for (MachineDefinition.ButtonSpec b : page.buttons()) buttons.put(b.slot(), b);
        for (MachineDefinition.PageDef.GhostSlotSpec g : page.ghostSlots())
            for (int s : g.slots()) ghostSlots.put(s, g);
        for (MachineDefinition.PageDef.StaticSlot s : page.layout()) layoutSlots.put(s.slot(), s);

        // "buttons"/"layout": "file.pf:func" — generated ONCE at menu-open time instead of (or
        // alongside) the static arrays above; see MachineDefinition.PageDef#buttonsGenerator/
        // #layoutGenerator. Merges into the SAME maps the static entries use, so render()/the
        // listener's slot classification need no changes to see them.
        if (page.buttonsGenerator() != null) {
            for (MachineDefinition.ButtonSpec b
                    : dev.arubik.craftengine.machine.menu.GeneratedPageContent.buttons(page.buttonsGenerator(), baseContext()))
                buttons.put(b.slot(), b);
        }
        if (page.layoutGenerator() != null) {
            for (MachineDefinition.PageDef.StaticSlot s
                    : dev.arubik.craftengine.machine.menu.GeneratedPageContent.layout(page.layoutGenerator(), baseContext()))
                layoutSlots.put(s.slot(), s);
        }

        String titleStr = page.title() != null ? page.title() : definition.title();
        Component title = MiniMessage.miniMessage().deserialize(titleStr)
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
        boolean chest = page.isChestType();
        this.inventory = chest
                ? Bukkit.createInventory(this, page.resolvedSize(), title)
                : Bukkit.createInventory(this, page.inventoryType(), title);
        render();
    }

    public static void open(Player player, ItemStack heldNms, ItemDefinition definition) {
        openPage(player, heldNms, definition, 0);
    }

    static void openPage(Player player, ItemStack heldNms, ItemDefinition definition, int pageIndex) {
        if (definition.pages().isEmpty()) return;
        int idx = Math.max(0, Math.min(pageIndex, definition.pages().size() - 1));

        // Reading which hand the item lives in is a genuine Bukkit-API edge (PlayerInventory only
        // exposes Bukkit ItemStacks), so the NMS->Bukkit conversion happens right here and nowhere
        // else — heldNms itself stays NMS all the way into the ItemMenu constructor.
        org.bukkit.inventory.ItemStack offhandBukkit = player.getInventory().getItemInOffHand();
        boolean offhand = offhandBukkit.getType() != Material.AIR
                && ItemStack.isSameItemSameComponents(CraftItemStack.asNMSCopy(offhandBukkit), heldNms)
                && !ItemStack.isSameItemSameComponents(
                        CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()), heldNms);
        int slot = offhand ? -1 : player.getInventory().getHeldItemSlot();

        ItemMenu menu = new ItemMenu(definition, idx, player.getUniqueId(), slot, offhand, heldNms);
        player.openInventory(menu.inventory);
    }

    // ------------------------------------------------------------------- render

    /** Re-draws every slot from current state — called on open and whenever a button/ghost script
     *  might have changed something (tank levels feeding a bar, etc). */
    void render() {
        int size = inventory.getSize();
        ItemStack[] storageContents = storageSlots.length > 0
                ? ItemStateData.pageStorage(workingStack, pageIndex, storageSlots.length) : new ItemStack[0];

        for (int i = 0; i < size; i++) {
            MenuSlotType type = slotType(i);
            org.bukkit.inventory.ItemStack rendered = switch (type) {
                case STORAGE -> {
                    int pos = storagePosition(i);
                    yield pos >= 0 && pos < storageContents.length ? storageContents[pos].asBukkitCopy() : null;
                }
                case BUTTON -> renderButton(buttons.get(i));
                case GHOST -> renderGhost(i, ghostSlots.get(i));
                case BACKGROUND -> renderBackground(i);
                default -> null;
            };
            inventory.setItem(i, rendered);
        }

        for (MachineDefinition.BarRef ref : page.bars()) {
            BarDefinition barDef = BarDefinition.REGISTRY.get(ref.bar());
            if (barDef == null) continue;
            MachineBar bar = barDef.toBar(ref.slots());
            double[] vm = barStat(ref.source());
            int n = ref.slots().length;
            for (int k = 0; k < n; k++)
                inventory.setItem(ref.slots()[k], MachineBars.renderSegment(bar, k, n, vm[0], vm[1], null, Map.of()));
        }
    }

    private MenuSlotType slotType(int i) {
        if (contains(storageSlots, i)) return MenuSlotType.STORAGE;
        if (buttons.containsKey(i)) return MenuSlotType.BUTTON;
        if (ghostSlots.containsKey(i)) return MenuSlotType.GHOST;
        if (isBarSlot(i)) return MenuSlotType.DYNAMIC; // rendered separately below, never player-editable
        return MenuSlotType.BACKGROUND;
    }

    private boolean isBarSlot(int i) {
        for (MachineDefinition.BarRef ref : page.bars())
            if (contains(ref.slots(), i)) return true;
        return false;
    }

    private static boolean contains(int[] arr, int v) {
        for (int a : arr) if (a == v) return true;
        return false;
    }

    private int storagePosition(int slotIdx) {
        for (int i = 0; i < storageSlots.length; i++) if (storageSlots[i] == slotIdx) return i;
        return -1;
    }

    private org.bukkit.inventory.ItemStack renderButton(MachineDefinition.ButtonSpec spec) {
        if (spec == null) return null;
        return new MachineDefinition.ItemSpec(spec.icon(), spec.name(), spec.lore(), Map.of()).build();
    }

    private org.bukkit.inventory.ItemStack renderBackground(int slotIdx) {
        MachineDefinition.PageDef.StaticSlot s = layoutSlots.get(slotIdx);
        if (s == null) return MenuText.emptyFiller();
        return new MachineDefinition.ItemSpec(s.item(), s.name(), s.lore(), Map.of()).build();
    }

    private org.bukkit.inventory.ItemStack renderGhost(int slotIdx, MachineDefinition.PageDef.GhostSlotSpec spec) {
        if (spec == null) return null;
        try {
            ScriptCall call = ScriptCall.parse(spec.getRef());
            if (call != null) {
                ScriptContext ctx = buildBaseContext().vars().isEmpty() ? null : buildBaseContext();
                ScriptContext slotCtx = ScriptContext.builder().copyFrom(baseContext())
                        .val("slot", ScriptValue.of(slotIdx)).build();
                ScriptValue result = call.evaluate(slotCtx);
                if (result instanceof ScriptValue.Item itemVal && itemVal.stack() != null && !itemVal.stack().isEmpty())
                    return itemVal.stack().asBukkitCopy();
                if (result instanceof ScriptValue.Str s && !s.value().isBlank())
                    return MenuText.iconItem(net.momirealms.craftengine.core.util.Key.of(s.value()),
                            Material.PAPER, Component.empty(), new Component[0]);
            }
        } catch (Throwable ignored) {
        }
        return MenuText.iconItem(net.momirealms.craftengine.core.util.Key.of(spec.emptyIcon()),
                Material.AIR, Component.empty(), new Component[0]);
    }

    /** {@code "tank:<name>"} or a {@code "file.pf:function"} ref returning {@code [value, max]}
     *  (or a bare 0..1 fraction) — mirrors {@code DataMachineBlockEntity#barStat}'s convention. */
    private double[] barStat(String source) {
        if (source != null && source.startsWith("tank:")) {
            String name = source.substring("tank:".length());
            ItemDefinition.TankSpec tank = definition.tank(name);
            int amount = ItemStateData.tankAmount(workingStack, name);
            return new double[]{amount, tank != null ? tank.capacity() : Math.max(1, amount)};
        }
        if (source != null && source.contains(".pf:")) {
            try {
                ScriptCall call = ScriptCall.parse(source);
                if (call != null) {
                    ScriptValue result = call.evaluate(baseContext());
                    if (result instanceof ScriptValue.Array arr && arr.elements().size() >= 2)
                        return new double[]{arr.elements().get(0).asNum(), arr.elements().get(1).asNum()};
                    if (result != ScriptValue.NULL) return new double[]{result.asNum() * 100.0, 100.0};
                }
            } catch (Throwable ignored) {
            }
        }
        return new double[]{0, 100};
    }

    private ScriptContext baseContext() {
        ScriptContext.Builder b = ScriptContext.builder().item("item", workingStack);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            org.bukkit.entity.Player bukkitPlayer = player;
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) bukkitPlayer).getHandle();
            b.player(sp);
        }
        return b.build();
    }

    private ScriptContext buildBaseContext() { return baseContext(); }

    // ------------------------------------------------------------------- access for the listener

    ItemDefinition definition() { return definition; }
    int pageIndex() { return pageIndex; }
    MachineDefinition.PageDef page() { return page; }
    UUID playerId() { return playerId; }
    int originatingSlot() { return slot; }
    boolean offhand() { return offhand; }
    int[] storageSlots() { return storageSlots; }
    Map<Integer, MachineDefinition.ButtonSpec> buttons() { return buttons; }
    Map<Integer, MachineDefinition.PageDef.GhostSlotSpec> ghostSlotsBySlot() { return ghostSlots; }
    ItemStack workingStack() { return workingStack; }
    void setWorkingStack(ItemStack s) { this.workingStack = s; }

    /** Persists the CURRENT page's storage-slot contents from the live inventory into NBT on
     *  {@link #workingStack}. Does not touch other pages' storage. */
    void saveStorageToWorkingStack() {
        if (storageSlots.length == 0) return;
        ItemStack[] snapshot = new ItemStack[storageSlots.length];
        for (int i = 0; i < storageSlots.length; i++) {
            org.bukkit.inventory.ItemStack bukkit = inventory.getItem(storageSlots[i]);
            snapshot[i] = (bukkit == null || bukkit.getType() == Material.AIR)
                    ? ItemStack.EMPTY : CraftItemStack.asNMSCopy(bukkit);
        }
        this.workingStack = ItemStateData.setPageStorage(workingStack, pageIndex, snapshot);
    }

    @Override
    public Inventory getInventory() { return inventory; }
}
