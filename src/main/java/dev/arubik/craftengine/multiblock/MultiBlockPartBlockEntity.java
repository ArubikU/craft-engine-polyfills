package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Unified MultiBlock Part/Core Block Entity.
 * Handles both CORE and PART roles with automatic redirection to core.
 * Supports optional machine capability and IO configuration.
 */
public class MultiBlockPartBlockEntity extends PersistentBlockEntity
        implements WorldlyContainer {

    // Persistent data keys
    private static final TypedKey<int[]> KEY_CORE_POS = TypedKey.of("craftengine", "multiblock_core_pos",
            PersistentDataType.INTEGER_ARRAY);
    private static final TypedKey<BlockState> KEY_ORIGINAL_STATE = TypedKey.of("craftengine",
            "multiblock_original_state",
            dev.arubik.craftengine.util.CustomDataType.BLOCK_STATE_TYPE);
    private static final TypedKey<String> KEY_ROLE = TypedKey.of("craftengine", "multiblock_role",
            PersistentDataType.STRING);
    private static final TypedKey<String> KEY_IO_CONFIG = TypedKey.of("craftengine", "multiblock_io_config",
            PersistentDataType.STRING);

    // Local keys that should not be delegated to core
    private static final Set<String> LOCAL_KEYS = new HashSet<>();
    static {
        LOCAL_KEYS.add(KEY_CORE_POS.getKey().toString());
        LOCAL_KEYS.add(KEY_ORIGINAL_STATE.getKey().toString());
        LOCAL_KEYS.add(KEY_ROLE.getKey().toString());
        LOCAL_KEYS.add(KEY_IO_CONFIG.getKey().toString());
    }

    // Runtime state
    private Level nmsLevel;
    private BlockPos core; // Core position if this is a PART
    private IOConfiguration ioConfig;
    private MultiBlockRole role;

    public MultiBlockPartBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        super(pos, state);
        this.role = MultiBlockRole.NONE;
    }

    public MultiBlockRole getRole() {
        return role;
    }

    public void setRole(MultiBlockRole role) {
        this.role = role;
        super.set(KEY_ROLE.getKey(), PersistentDataType.STRING, role.name());
    }

    public boolean isFormed() {
        return role == MultiBlockRole.CORE || (role == MultiBlockRole.PART && getCorePos() != null);
    }

    // ========== Position Management ==========

    private BlockPos getNmsPos() {
        return new BlockPos(pos.x(), pos.y(), pos.z());
    }

    @Override
    public Location getLocation() {
        return new Location((World) world.world.platformWorld(), pos.x(), pos.y(), pos.z());
    }
    // ========== Core Position Management ==========

    @Nullable
    public BlockPos getCorePos() {
        if (role == MultiBlockRole.CORE) {
            return getNmsPos();
        }
        if (core != null) {
            return core;
        }
        int[] corePosArr = super.get(KEY_CORE_POS);
        if (corePosArr != null && corePosArr.length == 3) {
            this.core = new BlockPos(corePosArr[0], corePosArr[1], corePosArr[2]);
            return this.core;
        }
        return null;
    }

    public void setCorePos(BlockPos corePos) {
        this.core = corePos;
        super.set(KEY_CORE_POS.getKey(), PersistentDataType.INTEGER_ARRAY,
                new int[] { corePos.getX(), corePos.getY(), corePos.getZ() });
    }

    @Nullable
    private MultiBlockMachineBlockEntity getCoreBlockEntity() {
        BlockPos corePos = getCorePos();
        if (corePos == null)
            return null;
        BlockEntity be = world
                .getBlockEntityAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(corePos.asLong()));
        if (be instanceof MultiBlockMachineBlockEntity pbe) {
            return pbe;
        }
        return null;
    }

    @Nullable
    private WorldlyContainer getCoreContainer() {
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        if (target != null) {
            return target;
        }
        return null;
    }

    // ========== Original Block State Management ==========

    public void setOriginalBlock(BlockState blockState) {
        super.set(KEY_ORIGINAL_STATE.getKey(), dev.arubik.craftengine.util.CustomDataType.BLOCK_STATE_TYPE,
                blockState);
    }

    @Nullable
    public BlockState getOriginalBlock() {
        return super.get(KEY_ORIGINAL_STATE);
    }

    // ========== IO Configuration ==========

    public void setIOConfiguration(IOConfiguration config) {
        this.ioConfig = config;
        // Optionally serialize to persistent data if needed
    }

    @Nullable
    public IOConfiguration getIOConfiguration() {
        return ioConfig;
    }

    public boolean acceptsInput(IOConfiguration.IOType type, Direction dir) {
        if (ioConfig != null) {
            return ioConfig.acceptsInput(type, dir);
        }
        MultiBlockMachineBlockEntity core = getCoreBlockEntity();
        return core != null && core.getIOConfiguration().acceptsInput(type, dir);
    }

    public boolean providesOutput(IOConfiguration.IOType type, Direction dir) {
        if (ioConfig != null) {
            return ioConfig.providesOutput(type, dir);
        }
        MultiBlockMachineBlockEntity core = getCoreBlockEntity();
        return core != null && core.getIOConfiguration().providesOutput(type, dir);
    }

    // ========== Redstone Support ==========

    /**
     * Get position relative to core (for redstone and IO logic)
     */
    public BlockPos getRelativePos() {
        if (role == MultiBlockRole.CORE) {
            return BlockPos.ZERO;
        }
        if (core != null) {
            BlockPos myPos = getNmsPos();
            return myPos.subtract(core);
        }
        return BlockPos.ZERO;
    }

    /**
     * Get redstone output signal for this part
     * First checks if this part can output redstone in the given direction
     * (IOConfiguration)
     * Then redirects to CORE if this is a PART, passing relative position and side
     */
    public int getRedstoneOutput(net.minecraft.core.Direction side) {
        // First check if this part can output redstone in this direction
        if (!canOutputRedstone(side)) {
            return 0;
        }

        if (role == MultiBlockRole.PART && core != null) {
            MultiBlockMachineBlockEntity target = getCoreBlockEntity();
            if (target != null) {
                return target.getRedstoneOutput(getRelativePos(), side);
            }
        }
        return 0;
    }

    /**
     * Check if this part can output redstone on the given side
     * Uses IOConfiguration to determine directional output capability
     */
    public boolean canOutputRedstone(net.minecraft.core.Direction side) {
        IOConfiguration config = getIOConfiguration();
        return config != null && config.providesOutput(IOConfiguration.IOType.REDSTONE, side);
    }

    private boolean isLocalKey(NamespacedKey key) {
        return LOCAL_KEYS.contains(key.toString());
    }

    @Override
    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        if (isLocalKey(key))
            return super.has(key, type);
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        return target != null ? target.has(key, type) : super.has(key, type);
    }

    @Override
    public boolean has(NamespacedKey key) {
        if (isLocalKey(key))
            return super.has(key);
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        return target != null ? target.has(key) : super.has(key);
    }

    @Override
    public <P, C> @Nullable C get(NamespacedKey key, PersistentDataType<P, C> type) {
        if (isLocalKey(key))
            return super.get(key, type);
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        return target != null ? target.get(key, type) : super.get(key, type);
    }

    @Override
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        if (isLocalKey(key)) {
            super.set(key, type, value);
            return;
        }
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        if (target != null)
            target.set(key, type, value);
        else
            super.set(key, type, value);
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {
        if (isLocalKey(key)) {
            super.remove(key);
            return;
        }
        MultiBlockMachineBlockEntity target = getCoreBlockEntity();
        if (target != null)
            target.remove(key);
        else
            super.remove(key);
    }

    // ========== WorldlyContainer Implementation (Delegation) ==========

    @Override
    public int getContainerSize() {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        WorldlyContainer c = getCoreContainer();
        return c == null || c.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        WorldlyContainer c = getCoreContainer();
        if (c != null)
            c.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        WorldlyContainer c = getCoreContainer();
        return c != null && c.stillValid(player);
    }

    @Override
    public void clearContent() {
        WorldlyContainer c = getCoreContainer();
        if (c != null)
            c.clearContent();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        // If we have IO config, filter slots based on it
        if (ioConfig != null && !ioConfig.acceptsInput(IOConfiguration.IOType.ITEM, side)
                && !ioConfig.providesOutput(IOConfiguration.IOType.ITEM, side)) {
            return new int[0]; // No slots accessible from this side
        }

        WorldlyContainer c = getCoreContainer();
        if (c != null) {
            return c.getSlotsForFace(side);
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        // Check IO config first
        if (ioConfig != null && !ioConfig.acceptsInput(IOConfiguration.IOType.ITEM, dir)) {
            return false;
        }
        WorldlyContainer c = getCoreContainer();
        return c != null && c.canPlaceItemThroughFace(slot, stack, dir);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        // Check IO config first
        if (ioConfig != null && !ioConfig.providesOutput(IOConfiguration.IOType.ITEM, dir)) {
            return false;
        }
        WorldlyContainer c = getCoreContainer();
        return c != null && c.canTakeItemThroughFace(slot, stack, dir);
    }

    @Override
    public int getMaxStackSize() {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.getMaxStackSize() : 64;
    }

    @Override
    public List<ItemStack> getContents() {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.getContents() : new ArrayList<>();
    }

    @Override
    public void onOpen(CraftHumanEntity player) {
        WorldlyContainer c = getCoreContainer();
        if (c != null)
            c.onOpen(player);
    }

    @Override
    public void onClose(CraftHumanEntity player) {
        WorldlyContainer c = getCoreContainer();
        if (c != null)
            c.onClose(player);
    }

    @Override
    public List<HumanEntity> getViewers() {
        WorldlyContainer c = getCoreContainer();
        return c != null ? c.getViewers() : new ArrayList<>();
    }

    @Override
    public void setMaxStackSize(int size) {
        WorldlyContainer c = getCoreContainer();
        if (c != null)
            c.setMaxStackSize(size);
    }

    @Override
    public InventoryHolder getOwner() {
        WorldlyContainer c = getCoreContainer();
        if (c instanceof InventoryHolder h)
            return h;
        return null;
    }

    @Override
    public void setChanged() {
        WorldlyContainer c = getCoreContainer();
        if (c != null) {
            c.setChanged();
        }
    }

    // ========== Redstone Support ==========

    /**
     * Get redstone signal input from a specific direction
     * Checks if this part accepts REDSTONE input from the given direction
     */
    public int getRedstoneInput(Direction dir) {
        IOConfiguration config = getIOConfiguration();
        if (config != null && config.acceptsInput(IOConfiguration.IOType.REDSTONE, dir)) {
            // Get neighbor block position
            BlockPos neighborPos = getNmsPos().relative(dir);
            // Read redstone signal from neighbor
            if (nmsLevel != null && nmsLevel.isLoaded(neighborPos)) {
                return nmsLevel.getSignal(neighborPos, dir);
            }
        }
        return 0;
    }

    /**
     * Check if any configured redstone input has a signal (>0)
     * Returns true if any REDSTONE input direction has signal strength > 0
     */
    public boolean hasRedstoneSignal() {
        IOConfiguration config = getIOConfiguration();
        if (config == null)
            return false;

        for (Direction dir : Direction.values()) {
            if (config.acceptsInput(IOConfiguration.IOType.REDSTONE, dir)) {
                if (getRedstoneInput(dir) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get redstone output signal strength
     * Override this in machine entities to provide dynamic output
     */
    public int getRedstoneOutput() {
        return 0;
    }

    /**
     * Minecraft hook for direct redstone signal
     * Called when checking redstone power from this block
     */
    public int getDirectSignal(Direction dir) {
        if (canOutputRedstone(dir.getOpposite())) {
            return getRedstoneOutput();
        }
        return 0;
    }

}
