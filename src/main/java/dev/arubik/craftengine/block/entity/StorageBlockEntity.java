package dev.arubik.craftengine.block.entity;

import dev.arubik.craftengine.util.SoundMap;
import dev.arubik.craftengine.util.TypedKeys;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Generic chest-like storage block entity (backed by {@code storage-block-behavior} config blocks).
 * Persists its inventory + configured max-stack-size on its own CE tag (inherited from
 * {@link dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity}) — no chunk PDC involved.
 */
public class StorageBlockEntity extends PersistentWorldlyBlockEntity {

    private final String title;
    private SoundMap soundMap;
    private org.bukkit.inventory.Inventory bukkitInventory;

    public StorageBlockEntity(BlockEntity blockEntity, int size, String title, SoundMap soundMap) {
        super(blockEntity, size);
        this.title = (title != null) ? title : "Storage";
        this.soundMap = soundMap;
    }

    public void setSoundMap(SoundMap soundMap) {
        this.soundMap = soundMap;
    }

    @Override
    public void setChanged() {
        // Persistence is pulled at chunk-save via saveCustomData/loadCustomData; no dirty flag needed.
    }

    @Override
    public int getMaxStackSize() {
        return getOrDefault(TypedKeys.MAX_STACK_SIZE, maxStackSize);
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
        set(TypedKeys.MAX_STACK_SIZE, size);
    }

    private net.minecraft.network.chat.Component titleComponent() {
        String t = (title == null) ? "Storage" : title;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("^<lang:([^>]+)>$").matcher(t.trim());
        if (m.matches())
            return net.minecraft.network.chat.Component.translatable(m.group(1));
        return net.minecraft.network.chat.Component.literal(t);
    }

    /** Open a vanilla ChestMenu backed DIRECTLY by this NMS container — no separate Bukkit copy. */
    public void open(org.bukkit.entity.Player player) {
        net.minecraft.server.level.ServerPlayer sp = ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
        int rows = Math.max(1, Math.min(getContainerSize() / 9, 6));
        net.minecraft.world.inventory.MenuType<net.minecraft.world.inventory.ChestMenu> type = switch (rows) {
            case 1 -> net.minecraft.world.inventory.MenuType.GENERIC_9x1;
            case 2 -> net.minecraft.world.inventory.MenuType.GENERIC_9x2;
            case 4 -> net.minecraft.world.inventory.MenuType.GENERIC_9x4;
            case 5 -> net.minecraft.world.inventory.MenuType.GENERIC_9x5;
            case 6 -> net.minecraft.world.inventory.MenuType.GENERIC_9x6;
            default -> net.minecraft.world.inventory.MenuType.GENERIC_9x3;
        };
        final int r = rows;
        net.minecraft.world.MenuProvider provider = new net.minecraft.world.SimpleMenuProvider(
                (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(type, id, inv, this, r),
                titleComponent());
        sp.openMenu(provider);
    }

    public int getAnalogOutput() {
        return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(this);
    }

    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        // LIVE wrapper around this NMS Container (not a separate copy) so hopper/crafter events see the
        // same backing array — no desync between a Bukkit-side copy and the real slots.
        if (bukkitInventory == null) {
            bukkitInventory = new org.bukkit.craftbukkit.inventory.CraftInventory(this);
        }
        return bukkitInventory;
    }

    @Override
    public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        super.onOpen(player);
        if (soundMap != null) {
            soundMap.playOpen(player.getLocation());
        }
    }

    @Override
    public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        super.onClose(player);
        if (soundMap != null) {
            soundMap.playClose(player.getLocation());
        }
    }
}
