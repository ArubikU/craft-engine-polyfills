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

    /**
     * Membership test for multiblock group-formation ({@link #scanGroup}/{@link #solveComponent}).
     * MUST match the neighbor's exact registered block id ({@code s.owner().value().id()}), not
     * just "does this neighbor's behavior chain also include {@code FluidBlockTankBehavior}"
     * (2026-07-02 session — "si se pone un copper tank sobre un multiblock tank, saca el liquido
     * del tanque aunque sean liquidos diferentes"). {@code copper_tank} and {@code fluid_block_tank}
     * are DIFFERENT registered blocks that both happen to share this same Java behavior class —
     * the previous class-only check treated any two blocks using this class as ONE unified
     * multiblock group the instant they touched, silently merging/sharing their fluid storage
     * (and losing/overwriting whichever type didn't "win" the shared pool) even though they're
     * meant to be entirely separate tanks. Same convention {@code TankBlockBehavior.isTank}
     * already uses (exact block-id match) for its own same-type vertical-stacking check.
     */
    public boolean isTank(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (s == null || s.isEmpty() || s.behavior().getFirst(FluidBlockTankBehavior.class) == null) {
            return false;
        }
        return s.owner() != null && s.owner().value().id().equals(this.block().id());
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
        return groupOf(start, solveComponent(level, start));
    }

    /** Build a Group for {@code cell} from a solved owner map (controller min-corner = cell's group). */
    private static Group groupOf(BlockPos cell, java.util.Map<Long, long[]> owner) {
        long[] a = owner.get(cell.asLong());
        if (a == null)
            return new Group(cell.immutable(), 1, cell.getX(), cell.getY(), cell.getZ(), 1, 1);
        BlockPos ctrl = BlockPos.of(a[0]);
        int w = (int) a[1], h = (int) a[2];
        return new Group(ctrl, w * w * h, ctrl.getX(), ctrl.getY(), ctrl.getZ(), h, w);
    }

    /**
     * Solve the WHOLE connected component once (Create's formMulti greedy): returns cell→[ctrlPos, w, h]
     * for every member. One solve assigns all members, so a topology change recomputes the component in a
     * single pass instead of per-block.
     */
    public java.util.Map<Long, long[]> solveComponent(Level level, BlockPos start) {
        java.util.List<BlockPos> comp = new java.util.ArrayList<>();
        HashSet<Long> inComp = new HashSet<>();
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        q.add(start.immutable());
        inComp.add(start.asLong());
        while (!q.isEmpty() && comp.size() < MAX_GROUP) {
            BlockPos p = q.poll();
            if (!isTank(level, p))
                continue;
            comp.add(p);
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (!inComp.contains(np.asLong()) && isTank(level, np)) {
                    inComp.add(np.asLong());
                    q.add(np.immutable());
                }
            }
        }
        // candidate prisms: each block as a min-corner, tallest filled w×w column per width.
        java.util.List<long[]> prisms = new java.util.ArrayList<>(); // [amount, ctrlPos, w, h]
        for (BlockPos c : comp)
            for (int w = 1; w <= MAX_WIDTH; w++) {
                int h = maxFilledHeight(level, c, w, inComp);
                if (h > 0)
                    prisms.add(new long[] { (long) w * w * h, c.asLong(), w, h });
            }
        // greedy: claim the largest prism whose cells are all still free. The tie-break MUST be deterministic
        // (amount desc, then wider, then min controller pos) so every member solves the SAME partition —
        // otherwise different blocks pick different overlapping prisms and the multi renders inconsistently
        // (e.g. a leftover pair drawn as half of a 2×2).
        prisms.sort((a, b) -> {
            int c = Long.compare(b[0], a[0]);
            if (c != 0)
                return c;
            c = Long.compare(b[2], a[2]); // prefer the wider footprint
            if (c != 0)
                return c;
            return Long.compare(a[1], b[1]); // then the lowest controller position
        });
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
        return owner;
    }

    /** Local tank-topology signature (6-neighbour mask) — a change means a tank was placed/removed nearby. */
    public int neighborSig(Level level, BlockPos pos) {
        int m = 0, i = 0;
        for (Direction d : Direction.values()) {
            if (isTank(level, pos.relative(d)))
                m |= (1 << i);
            i++;
        }
        return m;
    }

    /**
     * Recompute the WHOLE component after a tank was placed/removed: one solve assigns every member's cached
     * controller/count/signature, then each distinct group consolidates its fluid + refreshes its render.
     * This is the ONLY place the heavy formation runs — driven by topology change, never per tick.
     */
    public void recomputeArea(Level level, BlockPos start) {
        if (level.isClientSide())
            return;
        java.util.Map<Long, long[]> owner = solveComponent(level, start);
        HashSet<Long> controllers = new HashSet<>();
        for (java.util.Map.Entry<Long, long[]> e : owner.entrySet()) {
            BlockPos cell = BlockPos.of(e.getKey());
            long[] a = e.getValue();
            Controller be = controllerBE(level, cell);
            if (be != null) {
                be.ctrl = a[0];
                be.count = (int) (a[1] * a[1] * a[2]);
                be.neighborSig = neighborSig(level, cell);
            }
            controllers.add(a[0]);
        }
        // Clear fluid boxes left at cells that are no longer controllers (group split/reshape) — otherwise a
        // demoted controller leaves a phantom fluid box floating. remove() is a no-op when there's no box.
        for (Long cell : owner.keySet())
            if (!controllers.contains(cell)) {
                FluidTankRender.remove(level, BlockPos.of(cell));
                FluidShellRender.remove(level, BlockPos.of(cell));
            }
        for (long c : controllers) {
            consolidateFromOwner(level, owner, c); // per-group fluid sum (reuse the solved map)
            refreshGroupFromOwner(level, owner, c);
        }
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
        PersistentBlockEntity.executeAt(level, ctrl, be -> {
            if (stack == null || stack.isEmpty())
                be.remove(FluidKeys.FLUID);
            else
                be.set(FluidKeys.FLUID, stack);
        });
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

    /**
     * Consolidate every member's stored fluid into the controller (clearing the others). Needed because the
     * unified store lives at the controller pos — when the controller MOVES (group reshaped, or two filled
     * groups merge) the fluid would otherwise be stranded at an old controller and read as empty. All fluid
     * in a valid group shares one type (merge rejects mixed), so summing is safe; clamp to group capacity.
     */
    public void consolidateFluid(Level level, BlockPos anyMember) {
        if (level.isClientSide())
            return;
        consolidateFromOwner(level, solveComponent(level, anyMember),
                scanGroup(level, anyMember).controller.asLong());
    }

    /**
     * Sum the fluid of EXACTLY ONE group (the cells the owner map assigns to {@code ctrl}) into its
     * controller, clamped to THAT group's capacity, clearing the other members. Operating per-group (not
     * over the whole connected component) is critical: a flood-then-clamp summed adjacent SEPARATE tanks and
     * clamped to one small group's cap, destroying fluid (40k tank + a 1×1 next to it collapsed to 8k).
     */
    private void consolidateFromOwner(Level level, java.util.Map<Long, long[]> owner, long ctrl) {
        long[] ca = owner.get(ctrl);
        if (ca == null)
            return;
        int cap = (int) (ca[1] * ca[1] * ca[2]) * CAP_PER_BLOCK;
        BlockPos ctrlPos = BlockPos.of(ctrl);
        int total = 0;
        FluidType type = null;
        for (java.util.Map.Entry<Long, long[]> e : owner.entrySet()) {
            if (e.getValue()[0] != ctrl)
                continue; // only THIS group's cells
            BlockPos m = BlockPos.of(e.getKey());
            FluidStack s = FluidCarrierImpl.getStored(level, m);
            if (s != null && !s.isEmpty() && (type == null || type == s.getType())) {
                type = s.getType();
                total += s.getAmount();
            }
            if (!m.equals(ctrlPos))
                PersistentBlockEntity.executeAt(level, m, be -> be.remove(FluidKeys.FLUID));
        }
        final FluidType finalType = type;
        final int finalTotal = total;
        PersistentBlockEntity.executeAt(level, ctrlPos, be -> {
            if (finalType != null && finalTotal > 0)
                be.set(FluidKeys.FLUID, new FluidStack(finalType, Math.min(finalTotal, cap), 0));
            else
                be.remove(FluidKeys.FLUID);
        });
    }

    /** Recompute every member's blockstate (bottom/top + the fluid plane mapped from the group fill). */
    public void refreshGroupRender(Level level, BlockPos anyMember) {
        if (level.isClientSide())
            return;
        java.util.Map<Long, long[]> owner = solveComponent(level, anyMember);
        long[] me = owner.get(anyMember.asLong());
        if (me == null)
            return;
        long ctrl = me[0]; // resolve to THIS member's group controller (not necessarily anyMember)
        refreshGroupFromOwner(level, owner, ctrl);
    }

    /**
     * Render exactly ONE group (the cells the owner map assigns to {@code ctrl}). bottom/top/shape come from
     * SAME-GROUP membership, so the model reflects the real multiblock: a face is capped where THIS group
     * ends — even if a different tank/group sits against it — and windows tile only within this footprint.
     */
    private void refreshGroupFromOwner(Level level, java.util.Map<Long, long[]> owner, long ctrl) {
        long[] ca = owner.get(ctrl);
        if (ca == null)
            return;
        BlockPos ctrlPos = BlockPos.of(ctrl);
        int width = (int) ca[1], height = (int) ca[2];
        int minX = ctrlPos.getX(), minY = ctrlPos.getY(), minZ = ctrlPos.getZ();
        int cap = width * width * height * CAP_PER_BLOCK;
        FluidStack stored = FluidCarrierImpl.getStored(level, ctrlPos);
        double fill = (stored == null || stored.isEmpty() || cap <= 0) ? 0.0
                : Math.min(1.0, stored.getAmount() / (double) cap);
        double surface = fill * height;
        FluidType type = (stored == null || stored.isEmpty()) ? FluidType.EMPTY : stored.getType();
        boolean win = isWindowed(level, ctrlPos);

        try {
            // No window -> no fluid render at all (the group is opaque/closed).
            if (win)
                FluidTankRender.update(level, ctrlPos, width, height, type, fill);
            else
                FluidTankRender.remove(level, ctrlPos);
        } catch (Throwable ignored) {
        }

        // Shell DISABLED: the block's 24 frame models render the tank again (display-entity shell looked
        // wrong). Clear any shell quads previously spawned for this group.
        try {
            FluidShellRender.remove(level, ctrlPos);
        } catch (Throwable ignored) {
        }

        for (java.util.Map.Entry<Long, long[]> e : owner.entrySet()) {
            if (e.getValue()[0] != ctrl)
                continue; // only THIS group's cells
            BlockPos p = BlockPos.of(e.getKey());
            int k = p.getY() - minY;
            double memberFill = Math.max(0.0, Math.min(1.0, surface - k));
            boolean bottom = !sameGroup(owner, p.below(), ctrl);
            boolean top = !sameGroup(owner, p.above(), ctrl);
            // facing = which HORIZONTAL sides are exterior (neighbour not in this group) — the model culls
            // the interior sides so the multiblock is hollow/see-through. (north=-z, east=+x, south=+z, west=-x)
            dev.arubik.craftengine.property.TankFacing facing = win
                    ? dev.arubik.craftengine.property.TankFacing.of(
                            !sameGroup(owner, p.north(), ctrl), !sameGroup(owner, p.east(), ctrl),
                            !sameGroup(owner, p.south(), ctrl), !sameGroup(owner, p.west(), ctrl), width)
                    : dev.arubik.craftengine.property.TankFacing.SOLID; // hammer window off -> opaque tank
            applyMemberState(level, p, bottom, top, type, memberFill, facing);
        }
    }

    private static boolean sameGroup(java.util.Map<Long, long[]> owner, BlockPos pos, long ctrl) {
        long[] a = owner.get(pos.asLong());
        return a != null && a[0] == ctrl;
    }

    @SuppressWarnings("unchecked")
    private void applyMemberState(Level level, BlockPos pos, boolean bottom, boolean top, FluidType type,
            double memberFill, dev.arubik.craftengine.property.TankFacing facing) {
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
        // facing (custom EnumProperty<TankFacing>) — which sides are exterior; model culls interior faces.
        EnumProperty<dev.arubik.craftengine.property.TankFacing> faceProp =
                (EnumProperty<dev.arubik.craftengine.property.TankFacing>) (Object) cur.getProperty("facing");
        if (faceProp != null)
            ns = ns.with(faceProp, facing);
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

        // Bucket/bottle fill-into-tank — this behavior previously only handled the shift-click
        // readout above and never actually accepted a held container's fluid at all (unlike the
        // single-block TankBlockBehavior, which already had this). Mirrors that same pattern,
        // routed through insertFluid/extractFluid so it correctly reaches the GROUP controller
        // for a multiblock tank instead of a single member's own (irrelevant) local store.
        if (held != null && !held.isEmpty()) {
            var collectResult = FluidType.collectFromStack(held);
            FluidStack inputFluid = collectResult.getFirst();
            ItemStack emptiedContainer = collectResult.getSecond();
            if (!inputFluid.isEmpty()) {
                int accepted = insertFluid(level, pos, inputFluid, null);
                if (accepted == inputFluid.getAmount()) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        if (!emptiedContainer.isEmpty()) {
                            player.addItem(emptiedContainer);
                        }
                    }
                    // See TankBlockBehavior#playFillOrEmptySound's javadoc — same missing-sound
                    // gap, same fix (this is a hand-rolled fill flow, not vanilla BucketItem).
                    TankBlockBehavior.playFillOrEmptySound(level, pos, inputFluid.getType(), true);
                    return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
                }
            }

            FluidStack stored = getStored(level, pos);
            if (!stored.isEmpty()) {
                var outputResult = FluidType.collectToStack(held, stored, stored.getAmount());
                ItemStack resultItem = outputResult.getFirst();
                FluidStack remainingFluid = outputResult.getSecond();
                if (!resultItem.isEmpty()) {
                    int drainedAmount = stored.getAmount() - remainingFluid.getAmount();
                    if (drainedAmount > 0) {
                        final FluidStack[] drained = { null };
                        extractFluid(level, pos, drainedAmount, f -> drained[0] = f, null);
                        if (drained[0] != null && drained[0].getAmount() == drainedAmount) {
                            if (!player.getAbilities().instabuild) {
                                held.shrink(1);
                                player.addItem(resultItem);
                            }
                            TankBlockBehavior.playFillOrEmptySound(level, pos, drained[0].getType(), false);
                            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
                        }
                    }
                }
            }
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
        int neighborSig = -1;    // last seen local tank-topology mask; mismatch => place/break nearby

        public Controller(BlockEntity blockEntity, FluidBlockTankBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        public void onRemove() {
            super.onRemove();
            // Clear this block's fluid displays (if it was a controller) + re-solve the remaining tanks so
            // the survivors re-render with their new group (event-driven sig change also catches it, but the
            // broken controller's displays must go NOW or they linger as floating fluid boxes).
            try {
                Level level = (Level) ((BukkitWorld) blockEntity().world().world()).minecraftWorld();
                BlockPos pos = (BlockPos) Utils.fromPos(blockEntity().pos());
                FluidTankRender.remove(level, pos);
                FluidShellRender.remove(level, pos);
                for (Direction d : Direction.values()) {
                    BlockPos np = pos.relative(d);
                    if (behavior.isTank(level, np)) {
                        behavior.recomputeArea(level, np);
                        break;
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        private static final dev.arubik.craftengine.util.TypedKey<Boolean> KEY_WINDOWED =
                dev.arubik.craftengine.util.TypedKey.of("craftengine", "tank_windowed",
                        dev.arubik.craftengine.util.NbtType.BOOLEAN);

        @Override
        public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            set(KEY_WINDOWED, windowed);
            super.saveCustomData(tag);
            // The unified fluid (FluidKeys.FLUID) is already in this controller's own container and
            // gets written by super.saveCustomData(tag) above — no separate mirror needed.
        }

        @Override
        public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            super.loadCustomData(tag);
            windowed = getOrDefault(KEY_WINDOWED, windowed);
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
            // EVENT-DRIVEN recompute: the heavy formation runs ONLY when the local tank topology changes
            // (a tank placed/removed nearby), detected by a cheap 6-neighbour signature — never periodically.
            // recomputeArea solves the whole component once, updating every member's cache (incl. signatures).
            int sig = self.behavior.neighborSig(level, pos);
            if (self.ctrl == 0L || sig != self.neighborSig) {
                self.neighborSig = sig;
                try {
                    self.behavior.recomputeArea(level, pos);
                } catch (Throwable ignored) {
                }
            }
            // Only the controller registers the (single) engine seed for the whole group, and broadcasts the
            // group's packet fluid displays to whoever currently tracks its chunk (handles join / chunk load /
            // render-distance changes — the displays are virtual, so they must be re-sent per tracker).
            if (self.ctrl == pos.asLong()) {
                try {
                    dev.arubik.craftengine.fluid.graph.FluidEngine.registerSeed(pos);
                } catch (Throwable ignored) {
                }
                try {
                    FluidTankRender.flush(level, pos);
                } catch (Throwable ignored) {
                }
            }
        }
    }
}
