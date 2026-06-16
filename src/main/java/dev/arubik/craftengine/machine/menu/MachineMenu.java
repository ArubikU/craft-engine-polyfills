package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
        // A translatable Component title (client-i18n) takes precedence over the String.
        net.kyori.adventure.text.Component titleComp = layout.getTitleComponent();
        boolean chest = layout.getInventoryType() == org.bukkit.event.inventory.InventoryType.CHEST;
        if (titleComp != null) {
            this.inventory = chest
                    ? Bukkit.createInventory(this, layout.getSize(), titleComp)
                    : Bukkit.createInventory(this, layout.getInventoryType(), titleComp);
        } else if (chest) {
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

    /**
     * While true, {@link #syncFromMachine()} leaves INPUT/FUEL slots alone — set right after a
     * player edits an input slot so the machine's per-tick {@code setChanged()} can't clobber the
     * freshly-placed (not-yet-pushed) item before the scheduled {@link #syncToMachine()} runs.
     */
    private volatile boolean suppressInputPull = false;

    public void markInputDirty() {
        this.suppressInputPull = true;
    }

    // ---- ghost hint for empty FUEL slots (display-only; never stored as a real item) ----
    private static final org.bukkit.NamespacedKey GHOST_KEY =
            new org.bukkit.NamespacedKey("cml", "slot_ghost");

    private static org.bukkit.inventory.ItemStack fuelGhost() {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(org.bukkit.Material.COAL);
        org.bukkit.inventory.meta.ItemMeta m = s.getItemMeta();
        if (m != null) {
            m.displayName(dev.arubik.craftengine.machine.menu.MenuText.noI(
                    dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.fuel",
                            net.kyori.adventure.text.format.NamedTextColor.GRAY)));
            m.getPersistentDataContainer().set(GHOST_KEY, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
            s.setItemMeta(m);
        }
        return s;
    }

    /** True for the display-only placeholder shown in an empty fuel slot (must not be taken/stored). */
    public static boolean isGhost(org.bukkit.inventory.ItemStack stack) {
        if (stack == null || !stack.hasItemMeta())
            return false;
        org.bukkit.inventory.meta.ItemMeta m = stack.getItemMeta();
        return m != null && m.getPersistentDataContainer().has(GHOST_KEY, org.bukkit.persistence.PersistentDataType.BYTE);
    }

    public void syncFromMachine() {
        for (int i = 0; i < inventory.getSize(); i++) {
            MenuSlotType type = layout.getSlotType(i);
            boolean io = type == MenuSlotType.INPUT || type == MenuSlotType.OUTPUT || type == MenuSlotType.FUEL;
            if (!io)
                continue;
            if (suppressInputPull && (type == MenuSlotType.INPUT || type == MenuSlotType.FUEL))
                continue; // don't overwrite a pending player placement
            net.minecraft.world.item.ItemStack nms = machine.getItem(i);
            inventory.setItem(i, dev.arubik.craftengine.util.BridgeUtils.toBukkit(nms));
        }
    }

    /**
     * Pushes item in a specific slot from Bukkit inventory to machine
     */
    public void syncToMachine(int slot) {
        if (slot < 0 || slot >= inventory.getSize())
            return;
        MenuSlotType type = layout.getSlotType(slot);
        if (type == MenuSlotType.INPUT || type == MenuSlotType.OUTPUT || type == MenuSlotType.FUEL) {
            org.bukkit.inventory.ItemStack bukkit = inventory.getItem(slot);
            if (bukkit == null || bukkit.getType() == org.bukkit.Material.AIR || isGhost(bukkit)) {
                machine.setItem(slot, net.minecraft.world.item.ItemStack.EMPTY); // ghost = empty
            } else {
                // Use cast as requested, but fall back to NMS copy if it's a generic itemstack
                if (bukkit instanceof CraftItemStack) {
                    machine.setItem(slot, ((CraftItemStack) bukkit).handle);
                } else {
                    machine.setItem(slot, CraftItemStack.asNMSCopy(bukkit));
                }
            }
        }
    }

    /**
     * Pushes all appropriate slots from Bukkit inventory back to machine
     */
    public void syncToMachine() {
        for (int i = 0; i < inventory.getSize(); i++) {
            syncToMachine(i);
        }
        this.suppressInputPull = false; // player placement captured; resume machine->menu pulls
    }

    private void updateDynamicSlots() {
        for (int i = 0; i < inventory.getSize(); i++) {
            MenuSlotType type = layout.getSlotType(i);
            if (type == MenuSlotType.DYNAMIC || type == MenuSlotType.BUTTON) {
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
