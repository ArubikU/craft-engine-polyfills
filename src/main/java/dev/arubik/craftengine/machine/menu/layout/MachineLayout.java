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
}
