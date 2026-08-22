package dev.arubik.craftengine.machine.menu.layout;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.inventory.InventoryType;

public class MachineLayout {
    private final InventoryType inventoryType;
    private final String titlePattern;
    private final int size;

    private final Map<Integer, MenuSlotType> slotTypes = new HashMap<>(); // Standard Map
    private final Map<Integer, Integer> machineSlots = new HashMap<>();
    private final Map<Integer, DynamicItemProvider> dynamicProviders = new HashMap<>();
    private final Map<Integer, java.util.function.BiConsumer<dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity, org.bukkit.entity.Player>> buttonActions = new HashMap<>();
    private DynamicTitleProvider titleProvider;
    private net.kyori.adventure.text.Component titleComponent;
    private dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec storageFilter =
            dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec.none();

    /** What a {@link MenuSlotType#STORAGE} slot on this layout will accept — see {@link StorageFilters}. */
    public void setStorageFilter(dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec filter) {
        this.storageFilter = filter == null
                ? dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec.none() : filter;
    }

    public dev.arubik.craftengine.machine.MachineDefinition.PageDef.StorageFilterSpec getStorageFilter() {
        return storageFilter;
    }

    /** A translatable/Adventure title (client-i18n); when set it overrides the String title. */
    public void setTitleComponent(net.kyori.adventure.text.Component title) {
        this.titleComponent = title;
    }

    public net.kyori.adventure.text.Component getTitleComponent() {
        return titleComponent;
    }

    public MachineLayout(InventoryType inventoryType, int size, String titlePattern) {
        this.inventoryType = inventoryType;
        this.size = size;
        this.titlePattern = titlePattern;
    }

    public void setDynamicTitle(DynamicTitleProvider provider) {
        this.titleProvider = provider;
    }

    public DynamicTitleProvider getTitleProvider() {
        return titleProvider;
    }

    public void setSlotType(int slot, MenuSlotType type) {
        slotTypes.put(slot, type);
    }

    public MachineLayout addSlot(int slot, MenuSlotType type) {
        setSlotType(slot, type);
        machineSlots.put(slot, slot);
        return this;
    }

    public MachineLayout addSlot(int slot, MenuSlotType type, int machineSlot) {
        setSlotType(slot, type);
        machineSlots.put(slot, machineSlot);
        return this;
    }

    public void setDynamicProvider(int slot, DynamicItemProvider provider) {
        slotTypes.put(slot, MenuSlotType.DYNAMIC);
        dynamicProviders.put(slot, provider);
    }

    public void addButton(int slot, DynamicItemProvider iconProvider,
            java.util.function.BiConsumer<dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity, org.bukkit.entity.Player> action) {
        slotTypes.put(slot, MenuSlotType.BUTTON);
        dynamicProviders.put(slot, iconProvider);
        buttonActions.put(slot, action);
    }

    /** Click-type-aware button: the action receives the {@link org.bukkit.event.inventory.ClickType}. */
    public interface ClickButtonAction {
        void accept(dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine,
                org.bukkit.entity.Player player, org.bukkit.event.inventory.ClickType click);
    }

    private final Map<Integer, ClickButtonAction> clickButtonActions = new HashMap<>();

    public MachineLayout addClickButton(int slot, DynamicItemProvider iconProvider, ClickButtonAction action) {
        slotTypes.put(slot, MenuSlotType.BUTTON);
        dynamicProviders.put(slot, iconProvider);
        clickButtonActions.put(slot, action);
        return this;
    }

    public ClickButtonAction getClickButtonAction(int slot) {
        return clickButtonActions.get(slot);
    }

    /** A GHOST slot's click callback — receives whatever ItemStack is on the player's CURSOR at
     * click time (may be empty/air) and the click type, never the slot's own displayed item (there
     * is no real item in the slot to receive — see {@link MenuSlotType#GHOST}). Left-click with an
     * item in hand is the natural "set" gesture; right-click is the natural "clear" gesture
     * regardless of what's in hand — left-clicking with an EMPTY hand is deliberately left for the
     * script to treat as a no-op if it wants (browsing an already-set slot shouldn't wipe it out). */
    public interface GhostSlotAction {
        void accept(dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine,
                org.bukkit.entity.Player player, org.bukkit.inventory.ItemStack cursor,
                org.bukkit.event.inventory.ClickType click);
    }

    private final Map<Integer, GhostSlotAction> ghostSlotActions = new HashMap<>();

    public MachineLayout addGhostSlot(int slot, DynamicItemProvider iconProvider, GhostSlotAction action) {
        slotTypes.put(slot, MenuSlotType.GHOST);
        dynamicProviders.put(slot, iconProvider);
        ghostSlotActions.put(slot, action);
        return this;
    }

    public GhostSlotAction getGhostSlotAction(int slot) {
        return ghostSlotActions.get(slot);
    }

    /**
     * Slots the player may not move items in or out of, whatever their {@link MenuSlotType}.
     *
     * <p>Locking is deliberately independent of the slot type: the point is to pin a decorative
     * or placeholder item into a real INPUT/OUTPUT/FUEL/UPGRADE slot to block it — a filter slot
     * a recipe has not unlocked yet, an upgrade bay above the machine's tier, and so on. Marking
     * such a slot BACKGROUND instead would take it out of the machine's own IO bookkeeping.
     */
    private final java.util.Set<Integer> locked = new java.util.HashSet<>();

    /** Pin this slot: its contents become immovable for the player. */
    public MachineLayout setLocked(int slot, boolean lock) {
        if (lock) locked.add(slot); else locked.remove(slot);
        return this;
    }

    public boolean isLocked(int slot) {
        return locked.contains(slot);
    }

    /** Every currently locked slot, ascending. */
    public int[] getLockedSlots() {
        return locked.stream().mapToInt(Integer::intValue).sorted().toArray();
    }

    public MenuSlotType getSlotType(int slot) {
        return slotTypes.getOrDefault(slot, MenuSlotType.BACKGROUND);
    }

    public int getMachineSlot(int slot) {
        return machineSlots.getOrDefault(slot, slot);
    }

    public int[] getSlotsOfType(MenuSlotType type) {
        return slotTypes.entrySet().stream()
                .filter(e -> e.getValue() == type)
                .mapToInt(Map.Entry::getKey)
                .sorted()
                .toArray();
    }

    public DynamicItemProvider getProvider(int slot) {
        return dynamicProviders.get(slot);
    }

    public java.util.function.BiConsumer<dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity, org.bukkit.entity.Player> getButtonAction(
            int slot) {
        return buttonActions.get(slot);
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public int getSize() {
        return size;
    }

    public String getTitlePattern() {
        return titlePattern;
    }

    public void fillBackground(org.bukkit.inventory.ItemStack item) {

        // check if item is null
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        // fill all slots with item
        for (int i = 0; i < size; i++) {
            // check if is not defined
            if (!slotTypes.containsKey(i)) {
                slotTypes.put(i, MenuSlotType.BACKGROUND);
                dynamicProviders.put(i, (player, inventory) -> item);
            }
        }
    }
}
