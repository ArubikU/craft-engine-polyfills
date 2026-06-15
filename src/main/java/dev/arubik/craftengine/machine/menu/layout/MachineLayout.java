package dev.arubik.craftengine.machine.menu.layout;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.inventory.InventoryType;

public class MachineLayout {
    private final InventoryType inventoryType;
    private final String titlePattern;
    private final int size;

    private final Map<Integer, MenuSlotType> slotTypes = new HashMap<>(); // Standard Map
    private final Map<Integer, DynamicItemProvider> dynamicProviders = new HashMap<>();
    private final Map<Integer, java.util.function.BiConsumer<dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity, org.bukkit.entity.Player>> buttonActions = new HashMap<>();
    private DynamicTitleProvider titleProvider;
    private net.kyori.adventure.text.Component titleComponent;

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

    public MenuSlotType getSlotType(int slot) {
        return slotTypes.getOrDefault(slot, MenuSlotType.BACKGROUND);
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
