package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MachineMenu implements InventoryHolder {

    private final AbstractMachineBlockEntity machine;
    private final MachineLayout layout;
    private final Inventory inventory;
    private int tickCount = 0;

    public MachineMenu(AbstractMachineBlockEntity machine, MachineLayout layout) {
        this.machine = machine;
        this.layout = layout;

        String title = layout.getTitlePattern();
        if (layout.getTitleProvider() != null) {
            String dynamic = layout.getTitleProvider().provide(machine);
            if (dynamic != null) {
                title = dynamic;
            }
        }
        // Placeholder replacement logic would go here: e.g.
        // title.replace("{dynamic:gas}", ...)

        if (layout.getInventoryType() == org.bukkit.event.inventory.InventoryType.CHEST) {
            this.inventory = Bukkit.createInventory(this, layout.getSize(), title);
        } else {
            this.inventory = Bukkit.createInventory(this, layout.getInventoryType(), title);
        }

        updateDynamicSlots();
    }

    public void tick() {
        tickCount++;
        if (tickCount % 5 == 0) {
            updateDynamicSlots();

            // Dynamic Title Update
            if (layout.getTitleProvider() != null) {
                String dynamic = layout.getTitleProvider().provide(machine);
                if (dynamic != null && !dynamic.isEmpty()) {
                    for (org.bukkit.entity.HumanEntity viewer : inventory.getViewers()) {
                        // Paper 1.20+ API for updating title
                        // If not available, might throw Error on old versions, but this target is
                        // likely new
                        try {
                            viewer.getOpenInventory().setTitle(dynamic);
                        } catch (Throwable t) {
                            // Ignored if API missing
                        }
                    }
                }
            }
        }
    }

    public void syncFromMachine() {
        for (int i = 0; i < inventory.getSize(); i++) {
            MenuSlotType type = layout.getSlotType(i);
            if (type == MenuSlotType.INPUT || type == MenuSlotType.OUTPUT || type == MenuSlotType.BURNING) {
                net.minecraft.world.item.ItemStack nms = machine.getItem(i);
                inventory.setItem(i, dev.arubik.craftengine.util.BridgeUtils.toBukkit(nms));
            }
        }
    }

    private void updateDynamicSlots() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (layout.getSlotType(i) == MenuSlotType.DYNAMIC) {
                var provider = layout.getProvider(i);
                if (provider != null) {
                    inventory.setItem(i, provider.provide(machine, tickCount));
                }
            }
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public AbstractMachineBlockEntity getMachine() {
        return machine;
    }

    public MachineLayout getLayout() {
        return layout;
    }
}
