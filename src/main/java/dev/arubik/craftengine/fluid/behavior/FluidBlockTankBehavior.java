package dev.arubik.craftengine.fluid.behavior;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidCarrierImpl;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Create-style multiblock fluid tank with a UNIFIED store. A connected group of {@code fluid_block_tank}
 * blocks behaves as ONE tank: a controller (the group's min-corner) holds the combined fluid, and EVERY
 * member routes its store/insert/extract to that controller — so a pipe on any face of any member accesses
 * the whole tank, even if that block is locally "empty".
 *
 * <p>The group identity (controller + member count + this block's vertical index) is cached on the block
 * entity and recomputed only on place/break/neighbour change — NOT per {@code getStored}, which the engine
 * calls every tick. The hydraulic engine collapses the group to a single graph node (see
 * {@link dev.arubik.craftengine.fluid.graph.FluidGraphBuilder}).</p>
 */
public class FluidBlockTankBehavior extends ConnectableBlockBehavior implements EntityBlock, FluidCarrier {

    public static final Factory FACTORY = new Factory();

    /** Per-block capacity (mB). Group capacity = member count × this. */
    public static final int CAP_PER_BLOCK = 8000;
    private static final int MAX_GROUP = 4096;

    public FluidBlockTankBehavior(BlockDefinition block,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, java.util.Arrays.asList(Direction.values()), horizontalDirectionProperty,
                verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            EnumProperty<net.momirealms.craftengine.core.util.Direction> h =
                    (EnumProperty<net.momirealms.craftengine.core.util.Direction>) block.getProperty("horizontal");
            EnumProperty<net.momirealms.craftengine.core.util.Direction> v =
                    (EnumProperty<net.momirealms.craftengine.core.util.Direction>) block.getProperty("vertical");
            return new FluidBlockTankBehavior(block, h, v);
        }
    }

    // ---------------- group resolution (cached on the BE; BFS only on change) ----------------

    /** Result of a group scan: controller (min-corner) + member count + Y bounds + footprint width. */
    public static final class Group {
        public final BlockPos controller;
        public final int count;
        public final int minX, minY, minZ;
        public final int height;
        public final int width; // footprint side = max(dx, dz) + 1 (Create tanks are w×w × height)

        Group(BlockPos controller, int count, int minX, int minY, int minZ, int height, int width) {
            this.controller = controller;
            this.count = count;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.height = height;
            this.width = width;
        }
    }

    public boolean isTank(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        return s != null && !s.isEmpty() && s.behavior().getFirst(FluidBlockTankBehavior.class) != null;
    }

    private static final int MAX_WIDTH = 3;

    /**
     * Resolve the queried block's group like Create's {@code ConnectivityHandler.formMulti}: only COMPLETE
     * rectangular prisms (square footprint w≤3 × height, every cell a tank, single fluid) merge — formed
     * greedily largest-first, so an incomplete layer stays separate instead of flood-merging a blob.
     * Returns the group that ends up containing {@code start} (its controller = the prism's min-corner).
     */
    public Group scanGroup(Level level, BlockPos start) {
        if (!isTank(level, start))
            return new Group(start.immutable(), 1, start.getX(), start.getY(), start.getZ(), 1, 1);
        // 1) connected component (bounded flood).
        java.util.List<BlockPos> comp = new java.util.ArrayList<>();
        HashSet<Long> inComp = new HashSet<>();
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        q.add(start.immutable());
        inComp.add(start.asLong());
        while (!q.isEmpty() && comp.size() < MAX_GROUP) {
            BlockPos p = q.poll();
            comp.add(p);
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (!inComp.contains(np.asLong()) && isTank(level, np)) {
                    inComp.add(np.asLong());
                    q.add(np.immutable());
                }
            }
        }
        // 2) candidate prisms: each block as a min-corner, tallest filled w×w column per width.
        java.util.List<long[]> prisms = new java.util.ArrayList<>(); // [amount, ctrlPos, w, h]
        for (BlockPos c : comp)
            for (int w = 1; w <= MAX_WIDTH; w++) {
                int h = maxFilledHeight(level, c, w, inComp);
                if (h > 0)
                    prisms.add(new long[] { (long) w * w * h, c.asLong(), w, h });
            }
        // 3) greedy: claim the largest prism whose cells are all still free.
        prisms.sort((a, b) -> Long.compare(b[0], a[0]));
        HashSet<Long> assigned = new HashSet<>();
        java.util.Map<Long, long[]> owner = new java.util.HashMap<>(); // cell -> [ctrlPos, w, h]
        for (long[] pr : prisms) {
            BlockPos c = BlockPos.of(pr[1]);
            int w = (int) pr[2], h = (int) pr[3];
            boolean free = true;
            for (int dy = 0; dy < h && free; dy++)
                for (int dx = 0; dx < w && free; dx++)
                    for (int dz = 0; dz < w && free; dz++)
                        if (assigned.contains(c.offset(dx, dy, dz).asLong()))
                            free = false;
            if (!free)
                continue;
            for (int dy = 0; dy < h; dy++)
                for (int dx = 0; dx < w; dx++)
                    for (int dz = 0; dz < w; dz++) {
                        long cell = c.offset(dx, dy, dz).asLong();
                        assigned.add(cell);
                        owner.put(cell, new long[] { c.asLong(), w, h });
                    }
        }
        // 4) the queried block's resolved group.
        long[] a = owner.get(start.asLong());
        if (a == null)
            return new Group(start.immutable(), 1, start.getX(), start.getY(), start.getZ(), 1, 1);
        BlockPos ctrl = BlockPos.of(a[0]);
        int w = (int) a[1], h = (int) a[2];
        return new Group(ctrl, w * w * h, ctrl.getX(), ctrl.getY(), ctrl.getZ(), h, w);
    }

    /** Tallest run of fully-filled w×w layers up from min-corner {@code c} (all tanks; single fluid type). */
    private int maxFilledHeight(Level level, BlockPos c, int w, HashSet<Long> inComp) {
        FluidType locked = null;
        int h = 0;
        for (int dy = 0; dy < MAX_GROUP; dy++) {
            for (int dx = 0; dx < w; dx++)
                for (int dz = 0; dz < w; dz++) {
                    BlockPos cell = c.offset(dx, dy, dz);
                    if (!inComp.contains(cell.asLong()))
                        return h; // incomplete layer -> stop growing
                    FluidStack s = FluidCarrierImpl.getStored(level, cell);
                    if (s != null && !s.isEmpty()) {
                        if (locked == null)
                            locked = s.getType();
                        else if (locked != s.getType())
                            return h; // different fluids -> do not merge
                    }
                }
            h++;
        }
        return h;
    }

    /** Cached controller pos for this member (BE cache; falls back to a fresh scan). */
    public BlockPos controllerOf(Level level, BlockPos pos) {
        Controller be = controllerBE(level, pos);
        if (be != null && be.ctrl != 0L)
            return BlockPos.of(be.ctrl);
        return scanGroup(level, pos).controller;
    }

    private Controller controllerBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        return be != null && be.controller instanceof Controller c ? c : null;
    }

    /** Whether this member's group shows the window (hammer-toggled). Default true. */
    public boolean isWindowed(Level level, BlockPos pos) {
        Controller be = controllerBE(level, pos);
        return be == null || be.windowed;
    }

    /**
     * Hammer toggle: flip the whole group's window on/off, persist it on every member, refresh the render.
     * @return the group member count (the hammer durability cost).
     */
    public int toggleWindowed(Level level, BlockPos pos) {
        Group g = scanGroup(level, pos);
        Controller cbe = controllerBE(level, g.controller);
        boolean next = !(cbe == null || cbe.windowed);
        // set the flag on every member BE so it survives controller changes
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        HashSet<Long> seen = new HashSet<>();
        q.add(pos.immutable());
        seen.add(pos.asLong());
        int count = 0;
        while (!q.isEmpty()) {
            BlockPos p = q.poll();
            if (!isTank(level, p))
                continue;
            count++;
            Controller be = controllerBE(level, p);
            if (be != null)
                be.windowed = next;
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (seen.add(np.asLong()) && isTank(level, np))
                    q.add(np.immutable());
            }
        }
        refreshGroupRender(level, g.controller);
        return Math.max(1, count);
    }

    // ---------------- FluidCarrier (everything routes to the controller's store) ----------------

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        return FluidCarrierImpl.getStored(level, controllerOf(level, pos));
    }

    @Override
    public long getCapacity(Level level, BlockPos pos) {
        Controller be = controllerBE(level, pos);
        int count = be != null && be.count > 0 ? be.count : scanGroup(level, pos).count;
        return (long) count * CAP_PER_BLOCK;
    }

    @Override
    public void setStoredRaw(Level level, BlockPos pos, FluidStack stack) {
        BlockPos ctrl = controllerOf(level, pos);
        if (stack == null || stack.isEmpty())
            dev.arubik.craftengine.util.CustomBlockData.from(level, ctrl).remove(FluidKeys.FLUID);
        else
            dev.arubik.craftengine.util.CustomBlockData.from(level, ctrl).set(FluidKeys.FLUID, stack);
    }

    @Override
    public void onStoreChanged(Level level, BlockPos pos) {
        refreshGroupRender(level, controllerOf(level, pos));
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, Direction side) {
        BlockPos ctrl = controllerOf(level, pos);
        int n = FluidCarrierImpl.insertFluid(level, ctrl, stack, (int) getCapacity(level, pos), 0, side);
        if (n > 0)
            refreshGroupRender(level, ctrl);
        return n;
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained,
            Direction side) {
        BlockPos ctrl = controllerOf(level, pos);
        int n = FluidCarrierImpl.extractFluid(level, ctrl, max, drained, side);
        if (n > 0)
            refreshGroupRender(level, ctrl);
        return n;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    // ---------------- blockstate: frame (bottom/top) + window fluid plane (fluidtype/level) ----------------

    /** Recompute every member's blockstate (bottom/top + the fluid plane mapped from the group fill). */
    public void refreshGroupRender(Level level, BlockPos anyMember) {
        if (level.isClientSide())
            return;
        Group g = scanGroup(level, anyMember);
        FluidStack stored = FluidCarrierImpl.getStored(level, g.controller);
        int cap = g.count * CAP_PER_BLOCK;
        double fill = (stored == null || stored.isEmpty() || cap <= 0) ? 0.0
                : Math.min(1.0, stored.getAmount() / (double) cap);
        double surface = fill * g.height; // in block rows from the bottom
        FluidType type = (stored == null || stored.isEmpty()) ? FluidType.EMPTY : stored.getType();
        boolean win = isWindowed(level, g.controller);

        // Server-side fluid box (Create's renderFluidBox equivalent) at the controller.
        try {
            FluidTankRender.update(level, g.controller, g.width, g.height, type, fill);
        } catch (Throwable ignored) {
        }

        // Re-scan to visit every member (scanGroup only tracked the controller); cheap, bounded by MAX_GROUP.
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        HashSet<Long> seen = new HashSet<>();
        q.add(anyMember.immutable());
        seen.add(anyMember.asLong());
        while (!q.isEmpty()) {
            BlockPos p = q.poll();
            if (!isTank(level, p))
                continue;
            int k = p.getY() - g.minY; // vertical index, 0 = bottom row
            double memberFill = Math.max(0.0, Math.min(1.0, surface - k)); // 0..1 within this row
            dev.arubik.craftengine.property.TankShape shape = win
                    ? windowShape(g.width, p.getX() - g.minX, p.getZ() - g.minZ)
                    : dev.arubik.craftengine.property.TankShape.PLAIN;
            applyMemberState(level, p, !isTank(level, p.below()), !isTank(level, p.above()), type, memberFill, shape);
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (seen.add(np.asLong()) && isTank(level, np))
                    q.add(np.immutable());
            }
        }
    }

    /** Window shape for a member at footprint offset (xOff, zOff), Create's exact rule (setWindows). */
    private static dev.arubik.craftengine.property.TankShape windowShape(int width, int xOff, int zOff) {
        if (width == 1)
            return dev.arubik.craftengine.property.TankShape.WINDOW;
        if (width == 2)
            return xOff == 0
                    ? (zOff == 0 ? dev.arubik.craftengine.property.TankShape.WINDOW_NW
                            : dev.arubik.craftengine.property.TankShape.WINDOW_SW)
                    : (zOff == 0 ? dev.arubik.craftengine.property.TankShape.WINDOW_NE
                            : dev.arubik.craftengine.property.TankShape.WINDOW_SE);
        if (width == 3 && Math.abs(xOff - zOff) == 1)
            return dev.arubik.craftengine.property.TankShape.WINDOW;
        return dev.arubik.craftengine.property.TankShape.PLAIN;
    }

    @SuppressWarnings("unchecked")
    private void applyMemberState(Level level, BlockPos pos, boolean bottom, boolean top, FluidType type,
            double memberFill, dev.arubik.craftengine.property.TankShape shape) {
        Optional<ImmutableBlockState> opt = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos));
        if (opt.isEmpty())
            return;
        ImmutableBlockState cur = opt.get();
        ImmutableBlockState ns = cur;
        // bottom / top frame (Property<Boolean>, same pattern as the fan's powered state).
        net.momirealms.craftengine.core.block.property.Property<Boolean> bp =
                (net.momirealms.craftengine.core.block.property.Property<Boolean>) (Object) cur.getProperty("bottom");
        if (bp != null)
            ns = ns.with(bp, bottom);
        net.momirealms.craftengine.core.block.property.Property<Boolean> tp =
                (net.momirealms.craftengine.core.block.property.Property<Boolean>) (Object) cur.getProperty("top");
        if (tp != null)
            ns = ns.with(tp, top);
        // shape (custom EnumProperty<TankShape>) — Create's window/corner/plain tiling.
        EnumProperty<dev.arubik.craftengine.property.TankShape> shapeProp =
                (EnumProperty<dev.arubik.craftengine.property.TankShape>) (Object) cur.getProperty("shape");
        if (shapeProp != null)
            ns = ns.with(shapeProp, shape);
        // Fluid plane: level (0..max) + fluidtype — same convention as the personal tank's window fill.
        IntegerProperty lvlProp = (IntegerProperty) (Object) cur.getProperty("level");
        if (lvlProp != null) {
            int lev = type == FluidType.EMPTY ? 0
                    : Math.max(1, Math.min(lvlProp.max, (int) Math.round(memberFill * lvlProp.max)));
            ns = ns.with(lvlProp, lev);
        }
        EnumProperty<FluidType> ftProp = (EnumProperty<FluidType>) (Object) cur.getProperty("fluidtype");
        if (ftProp != null)
            ns = ns.with(ftProp, type);
        if (ns != cur) {
            try {
                ((net.minecraft.world.level.LevelWriter) level).setBlock(pos,
                        (BlockState) ns.customBlockState().minecraftState(), 3);
            } catch (Throwable ignored) {
            }
        }
    }

    // ---------------- shift-click readout (group total) ----------------

    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {
        Level level = (Level) context.getLevel().minecraftWorld();
        if (level.isClientSide())
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS;
        BlockPos pos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
        Player player = (Player) ((BukkitServerPlayer) context.getPlayer()).serverPlayer();
        InteractionHand hand = context.getHand().equals(
                net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;
        ItemStack held = player.getItemInHand(hand);
        if ((held == null || held.isEmpty()) && player.isShiftKeyDown()) {
            player.getBukkitEntity().sendActionBar(
                    TankBlockBehavior.fluidInfo(getStored(level, pos), (int) getCapacity(level, pos), pos.getY()));
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    // ---------------- block entity (group cache + engine seed) ----------------

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new Controller(blockEntity, this);
    }

    /** Per-member block entity: caches the group (controller + count) so getStored is O(1), recomputed on
     * place/break/neighbour change. The CONTROLLER member persists the fluid + registers the engine seed. */
    public static class Controller extends PersistentBlockEntity {
        private final FluidBlockTankBehavior behavior;
        long ctrl;   // cached controller pos (asLong); 0 = unresolved
        int count;   // cached member count
        boolean windowed = true; // hammer-toggled: false hides the window (all shapes PLAIN)
        private int recomputeCd;

        public Controller(BlockEntity blockEntity, FluidBlockTankBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        private void recompute(Level level, BlockPos pos) {
            Group g = behavior.scanGroup(level, pos);
            this.ctrl = g.controller.asLong();
            this.count = g.count;
        }

        @Override
        public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            super.saveCustomData(tag);
            tag.putBoolean("win", windowed);
            // Only the controller block carries the unified fluid (others route to it).
            try {
                Level level = (Level) ((BukkitWorld) blockEntity().world().world()).minecraftWorld();
                BlockPos pos = (BlockPos) Utils.fromPos(blockEntity().pos());
                if (ctrl == 0L || ctrl == pos.asLong()) {
                    FluidStack f = FluidCarrierImpl.getStored(level, pos);
                    if (f != null && !f.isEmpty()) {
                        tag.putString("t", f.getType().name());
                        tag.putInt("a", f.getAmount());
                        tag.putInt("p", f.getPressure());
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        @Override
        public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            super.loadCustomData(tag);
            if (tag.containsKey("win"))
                windowed = tag.getBoolean("win");
            try {
                String tn = tag.getString("t");
                if (tn != null && !tn.isEmpty()) {
                    Level level = (Level) ((BukkitWorld) blockEntity().world().world()).minecraftWorld();
                    BlockPos pos = (BlockPos) Utils.fromPos(blockEntity().pos());
                    dev.arubik.craftengine.util.CustomBlockData.from(level, pos).set(FluidKeys.FLUID,
                            new FluidStack(FluidType.valueOf(tn), tag.getInt("a"), tag.getInt("p")));
                }
            } catch (Throwable ignored) {
            }
        }

        @Override
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
                CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper((BlockEntityTicker<Controller>) Controller::tick);
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, Controller self) {
            Level level = (Level) world.world().minecraftWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos pos = (BlockPos) Utils.fromPos(cePos);
            // Refresh the group cache periodically (cheap cadence) + on first tick. When the group changes
            // the controller re-renders all members' blockstates (bottom/top/shape) + the fluid box.
            if (self.ctrl == 0L || self.recomputeCd-- <= 0) {
                long prevCtrl = self.ctrl;
                int prevCount = self.count;
                self.recompute(level, pos);
                self.recomputeCd = 20;
                if (self.ctrl == pos.asLong() && (self.ctrl != prevCtrl || self.count != prevCount)) {
                    try {
                        self.behavior.refreshGroupRender(level, pos);
                    } catch (Throwable ignored) {
                    }
                }
            }
            // Only the controller registers the (single) engine seed for the whole group.
            if (self.ctrl == pos.asLong()) {
                try {
                    dev.arubik.craftengine.fluid.graph.FluidEngine.registerSeed(pos);
                } catch (Throwable ignored) {
                }
            }
        }
    }
}
