package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Base for the conveyor-network "router" blocks (merger / splitter). Each holds a
 * tiny internal item buffer ({@code slots} stacks). Belts deliver into the buffer
 * via {@link ConveyorReceiver#receiveConveyorItem}; every tick {@link #route} tries
 * to push buffered stacks back onto downstream belts/receivers. Directionality (which
 * sides are inputs vs. outputs) is enforced per subclass.
 *
 * <p>Buffered items are invisible (no display entity) — these blocks are pass-through
 * routers, not transport surfaces.</p>
 */
public abstract class AbstractRouterBlockEntity extends PersistentWorldlyBlockEntity implements ConveyorReceiver {

    public static final String PROP_FACING = "facing";

    protected Direction defaultFacing = Direction.NORTH;
    protected final int slots;

    protected AbstractRouterBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, Math.max(1, slots));
        if (defaultFacing != null)
            this.defaultFacing = defaultFacing;
        this.slots = Math.max(1, slots);
    }

    // ---------------- live facing read ----------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static String enumName(ImmutableBlockState state, String name) {
        if (state == null)
            return null;
        Property p = state.getProperty(name);
        if (p == null)
            return null;
        Object v = state.get(p);
        if (v == null)
            return null;
        try {
            return Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    /** Output direction (the {@code facing} property), or the default. */
    public Direction facing() {
        String n = enumName(blockEntity().blockState(), PROP_FACING);
        if (n != null) {
            try {
                return Direction.valueOf(n.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return defaultFacing;
    }

    // ---------------- buffer helpers ----------------

    protected boolean slotEmpty(int i) {
        net.minecraft.world.item.ItemStack s = getItem(i);
        return s == null || s.isEmpty();
    }

    protected int firstEmptySlot() {
        for (int i = 0; i < slots; i++)
            if (slotEmpty(i))
                return i;
        return -1;
    }

    protected org.bukkit.inventory.ItemStack bukkitSlot(int i) {
        if (slotEmpty(i))
            return null;
        return CraftItemStack.asBukkitCopy(getItem(i));
    }

    protected void clearSlot(int i) {
        setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
    }

    // Container#setChanged is abstract here; the buffer is persisted via the parent's
    // saveCustomData (KEY_INVENTORY) on chunk save, so this is a no-op.
    @Override
    public void setChanged() {
    }

    // ---------------- ConveyorReceiver ----------------

    @Override
    public boolean isFull() {
        return firstEmptySlot() < 0;
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        if (stack == null || stack.getType().isAir())
            return false;
        if (!acceptsFrom(sourceFacing))
            return false;
        int i = firstEmptySlot();
        if (i < 0)
            return false;
        setItem(i, CraftItemStack.asNMSCopy(stack));
        return true;
    }

    /**
     * Whether an item arriving from a belt whose travel direction is
     * {@code sourceFacing} is allowed in (enforces input-side directionality).
     */
    protected abstract boolean acceptsFrom(Direction sourceFacing);

    /** Push buffered stacks toward the output side(s). */
    protected abstract void route(CEWorld world, BlockPos pos);

    // ---------------- ticking ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<AbstractRouterBlockEntity>) AbstractRouterBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, AbstractRouterBlockEntity self) {
        if (self.isEmpty())
            return;
        self.route(world, pos);
    }
}
