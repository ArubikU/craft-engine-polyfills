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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.multiblock.IOConfiguration.IORole;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
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
        implements WorldlyContainer, InventoryHolder {

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
        if (role == MultiBlockRole.NONE) {
            String roleName = super.get(KEY_ROLE);
            if (roleName != null) {
                try {
                    this.role = MultiBlockRole.valueOf(roleName);
                } catch (Exception ignored) {
                }
            }
        }
        return role;
    }

    public void setRole(MultiBlockRole role) {
        this.role = role;
        super.set(KEY_ROLE, role.name());
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
    private BlockEntity getCoreEntity() {
        BlockPos corePos = getCorePos();
        if (corePos == null) {
            System.out.println("[MultiBlockPartBlockEntity] getCoreEntity: corePos is NULL at " + pos);
            return null;
        }
        if (world == null) {
            System.out.println("[MultiBlockPartBlockEntity] getCoreEntity: world is NULL at " + pos);
            return null;
        }
        BlockEntity be = world
                .getBlockEntityAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(corePos.asLong()));
        if (be == null) {
            System.out.println("[MultiBlockPartBlockEntity] getCoreEntity: No BlockEntity at " + corePos + " (Self at "
                    + pos + ")");
        }
        return be;
    }

    @Nullable
    private WorldlyContainer getCoreContainer() {
        BlockEntity core = getCoreEntity();
        if (core == this)
            return null; // Avoid recursion
        if (core instanceof WorldlyContainer container) {
            return container;
        }
        return null;
    }

    // ========== Original Block State Management ==========

    public void setOriginalBlock(BlockState blockState) {
        super.set(KEY_ORIGINAL_STATE, blockState);
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
        Direction localDir = dir;

        ConnectableBlockBehavior connectable = getBlockBehavior(ConnectableBlockBehavior.class);
        if (connectable != null) {
            localDir = connectable.toLocalDirection(dir, (BlockState) blockState.customBlockState().literalObject());
        }

        if (ioConfig != null) {
            return ioConfig.acceptsInput(type, localDir);
        }
        BlockEntity core = getCoreEntity();
        if (core instanceof MultiBlockMachineBlockEntity machine) {
            IOConfiguration coreConfig = machine.getIOConfiguration();
            if (coreConfig != null) {
                return coreConfig.acceptsInput(type, localDir);
            }
            return true; // Fallback to permissive if machine has no config
        } else if (core instanceof MultiBlockPartBlockEntity part) {
            IOConfiguration coreConfig = part.getIOConfiguration();
            if (coreConfig != null) {
                return coreConfig.acceptsInput(type, localDir);
            }
            return true; // Fallback to permissive
        }
        return false;
    }

    public boolean providesOutput(IOConfiguration.IOType type, Direction dir) {
        Direction localDir = dir;
        ConnectableBlockBehavior connectable = getBlockBehavior(ConnectableBlockBehavior.class);
        if (connectable != null) {
            localDir = connectable.toLocalDirection(dir, (BlockState) blockState.customBlockState().literalObject());
        }

        if (ioConfig != null) {
            return ioConfig.providesOutput(type, localDir);
        }
        BlockEntity core = getCoreEntity();
        if (core instanceof MultiBlockMachineBlockEntity machine) {
            IOConfiguration coreConfig = machine.getIOConfiguration();
            if (coreConfig != null) {
                return coreConfig.providesOutput(type, localDir);
            }
            return true; // Fallback to permissive
        } else if (core instanceof MultiBlockPartBlockEntity part) {
            IOConfiguration coreConfig = part.getIOConfiguration();
            if (coreConfig != null) {
                return coreConfig.providesOutput(type, localDir);
            }
            return true; // Fallback to permissive
        }
        return false;
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
            BlockEntity target = getCoreEntity();
            if (target instanceof MultiBlockMachineBlockEntity machine) {
                return machine.getRedstoneOutput(getRelativePos(), side);
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
        BlockEntity target = getCoreEntity();
        if (target == null || target == this)
            return super.has(key, type);
        return (target instanceof MultiBlockMachineBlockEntity machine) ? machine.has(key, type) : super.has(key, type);
    }

    @Override
    public boolean has(NamespacedKey key) {
        if (isLocalKey(key))
            return super.has(key);
        BlockEntity target = getCoreEntity();
        if (target == null || target == this)
            return super.has(key);
        return (target instanceof MultiBlockMachineBlockEntity machine) ? machine.has(key) : super.has(key);
    }

    @Override
    public <P, C> @Nullable C get(NamespacedKey key, PersistentDataType<P, C> type) {
        if (isLocalKey(key))
            return super.get(key, type);
        BlockEntity target = getCoreEntity();
        if (target == null || target == this)
            return super.get(key, type);
        return (target instanceof MultiBlockMachineBlockEntity machine) ? machine.get(key, type) : super.get(key, type);
    }

    @Override
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        if (isLocalKey(key)) {
            super.set(key, type, value);
            return;
        }
        BlockEntity target = getCoreEntity();
        if (target == null || target == this) {
            super.set(key, type, value);
            return;
        }
        if (target instanceof MultiBlockMachineBlockEntity machine)
            machine.set(key, type, value);
        else
            super.set(key, type, value);
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {
        if (isLocalKey(key)) {
            super.remove(key);
            return;
        }
        BlockEntity target = getCoreEntity();
        if (target == null || target == this) {
            super.remove(key);
            return;
        }
        if (target instanceof MultiBlockMachineBlockEntity machine)
            machine.remove(key);
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
        // System.out.println("[MultiBlockPartBlockEntity] getSlotsForFace side=" + side
        // + " at " + pos);
        // If we have IO config, filter slots based on it
        if (ioConfig != null) {
            java.util.Set<Integer> slotSet = new java.util.TreeSet<>();

            // Inputs (including fuel)
            if (acceptsInput(IOConfiguration.IOType.ITEM, side)) {
                for (int s : ioConfig.getSlots(IOType.ITEM, IORole.INPUT)) {
                    slotSet.add(s);
                }
                for (int s : ioConfig.getSlots(IOType.ITEM, IORole.FUEL)) {
                    slotSet.add(s);
                }
            }

            // Outputs
            if (providesOutput(IOConfiguration.IOType.ITEM, side)) {
                for (int s : ioConfig.getSlots(IOType.ITEM, IORole.OUTPUT)) {
                    slotSet.add(s);
                }
            }

            if (!slotSet.isEmpty()) {
                return slotSet.stream().mapToInt(Integer::intValue).toArray();
            }
        }

        WorldlyContainer c = getCoreContainer();
        if (c != null) {
            int[] slots = c.getSlotsForFace(side);
            // System.out.println("[MultiBlockPartBlockEntity] Delegating to core, slots
            // size: " + slots.length);
            return slots;
        }
        // System.out.println("[MultiBlockPartBlockEntity] No core container found!");
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        // Check IO config first
        if (ioConfig != null) {
            if (!acceptsInput(IOConfiguration.IOType.ITEM, dir)) {
                return false;
            }

            int[] inputSlots = ioConfig.getSlots(IOType.ITEM, IORole.INPUT);
            int[] fuelSlots = ioConfig.getSlots(IOType.ITEM, IORole.FUEL);

            // If no slots are explicitly defined but direction is allowed, fallback to core
            if (inputSlots.length == 0 && fuelSlots.length == 0) {
                WorldlyContainer c = getCoreContainer();
                return c != null && c.canPlaceItemThroughFace(slot, stack, dir);
            }

            // Verify if slot is in INPUT or FUEL
            for (int s : inputSlots) {
                if (s == slot)
                    return true;
            }
            for (int s : fuelSlots) {
                if (s == slot)
                    return true;
            }
            return false;
        }
        WorldlyContainer c = getCoreContainer();
        return c != null && c.canPlaceItemThroughFace(slot, stack, dir);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        // Check IO config first
        if (ioConfig != null) {
            if (!providesOutput(IOConfiguration.IOType.ITEM, dir)) {
                return false;
            }

            int[] outputSlots = ioConfig.getSlots(IOType.ITEM, IORole.OUTPUT);

            // If no slots are explicitly defined but direction is allowed, fallback to core
            if (outputSlots.length == 0) {
                WorldlyContainer c = getCoreContainer();
                return c != null && c.canTakeItemThroughFace(slot, stack, dir);
            }

            // Verify if slot is in OUTPUT
            for (int s : outputSlots) {
                if (s == slot)
                    return true;
            }
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
    public @NotNull Inventory getInventory() {
        WorldlyContainer c = getCoreContainer();
        if (c instanceof InventoryHolder h) {
            Inventory inv = h.getInventory();
            if (inv != null)
                return inv;
        }
        // Return a dummy empty inventory instead of null to avoid NPEs in events
        return org.bukkit.Bukkit.createInventory(null, 0);
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
    public @Nullable InventoryHolder getOwner() {
        WorldlyContainer c = getCoreContainer();
        if (c instanceof InventoryHolder h)
            return h;
        return this; // Return self as owner if no core, instead of null
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
        Direction localDir = dir;
        ConnectableBlockBehavior connectable = getBlockBehavior(ConnectableBlockBehavior.class);

        if (connectable != null) {
            localDir = connectable.toLocalDirection(dir, (BlockState) blockState.customBlockState().literalObject());
        }

        if (config != null && config.acceptsInput(IOConfiguration.IOType.REDSTONE, localDir)) {
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
