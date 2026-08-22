package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Generalized "place one block, the rest auto-places" multi-cell behavior for STANDALONE (non
 * data-machine) blocks that need their own controller/menu hooks — e.g. {@code WorkbenchBehavior}.
 * A data-driven machine wanting a multi-cell structure does NOT use this class — see {@code
 * DataMachineBehavior}, which reads the very same {@code cells} config (via {@link
 * MultiCellGeometry#parseCells}) directly into the shared machine block behavior so no per-machine
 * Java is needed. This
 * class exists for the rarer case of a bespoke Java behavior (its own menu, its own controller
 * type) that still wants the auto-placement/breakage mechanics for free.
 *
 * <p>A block declares its shape as {@code cells} in its own behavior config — a list of {@code
 * {x,y,z}} offsets, schema-relative as if the structure faced NORTH (see {@link
 * MultiCellGeometry}). The placed block is always cell 0 (the master); cell {@code i} (1-based) is
 * {@code cells.get(i - 1)}. The cell index is stored as a small integer-valued string property
 * ({@code cell_index} by default) whose declared range in the CONSUMING block's own YAML must
 * cover {@code 0..cells.size()} — this class stays N-agnostic, reading its own {@code cells}
 * config rather than hardcoding any count.
 *
 * <h2>Rules implemented</h2>
 * <ul>
 *   <li><b>Placement</b>: the placed block is stamped {@code cell_index=0} (master), facing from
 *       the player. {@link #onPlace} then places every secondary cell in schema order; if ANY
 *       target cell isn't replaceable, the WHOLE attempt is rolled back — no partial structures.</li>
 *   <li><b>Break</b>: removing ANY cell removes every other cell of the same structure, guarded
 *       against recursion.</li>
 *   <li><b>Right-click</b>: resolves the master from whichever cell was clicked and calls {@link
 *       #onMasterUse}; non-master cells never act themselves.</li>
 *   <li><b>EntityBlock</b>: only the master hosts a real controller via {@link
 *       #createMasterController}; every other cell gets a no-op {@link TransientController}.</li>
 * </ul>
 */
public class MultiCellBlockBehavior extends NmsBlockBehavior implements EntityBlock {

    public static final String DEFAULT_FACING_PROPERTY = "facing";
    public static final String DEFAULT_CELL_INDEX_PROPERTY = "cell_index";

    protected final String facingProperty;
    protected final String cellIndexProperty;
    /** Secondary cells only (master is implicit index 0) — see class javadoc. */
    protected final List<MultiCellGeometry.Offset> cells;

    /** Re-entrancy guard so removing the rest of the structure doesn't re-trigger break handling. */
    private boolean removingStructure = false;

    public MultiCellBlockBehavior(BlockDefinition block, String facingProperty, String cellIndexProperty,
            List<MultiCellGeometry.Offset> cells) {
        super(block);
        this.facingProperty = facingProperty != null ? facingProperty : DEFAULT_FACING_PROPERTY;
        this.cellIndexProperty = cellIndexProperty != null ? cellIndexProperty : DEFAULT_CELL_INDEX_PROPERTY;
        this.cells = cells != null ? List.copyOf(cells) : List.of();
    }

    /** Total cell count, master included. */
    protected final int cellCount() {
        return cells.size() + 1;
    }

    // ---------------- property helpers (graceful fallback) ----------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String enumName(ImmutableBlockState state, String name) {
        if (state == null) return null;
        Property p = state.getProperty(name);
        if (p == null) return null;
        Object v = state.get(p);
        if (v == null) return null;
        try {
            return Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withValue(ImmutableBlockState state, String prop, String valueName) {
        if (state == null) return null;
        Property p = state.getProperty(prop);
        if (p == null) return state;
        try {
            Object value = p.valueByName(valueName.toLowerCase());
            if (value == null) value = p.valueByName(valueName);
            if (value == null) return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }

    // ---------------- public read-only accessors (contraption multiblock-membership expansion) ----------------

    /** Public wrapper over {@link #isMultiCellBlock} — pure read, no side effects, safe to expose. */
    public final boolean isMultiCellBlockPublic(ImmutableBlockState state) {
        return isMultiCellBlock(state);
    }

    /** Public wrapper over {@link #facingOf}. */
    public final Direction facingOfPublic(ImmutableBlockState state) {
        return facingOf(state);
    }

    /** Public wrapper over {@link #cellIndexOf}. */
    public final int cellIndexOfPublic(ImmutableBlockState state) {
        return cellIndexOf(state);
    }

    /** This structure's secondary-cell offsets (master/index-0 is implicit, not included). */
    public final List<MultiCellGeometry.Offset> cellsPublic() {
        return cells;
    }

    /** True iff this block actually defines both required properties. */
    protected final boolean isMultiCellBlock(ImmutableBlockState state) {
        return state != null
                && state.getProperty(facingProperty) != null
                && state.getProperty(cellIndexProperty) != null
                && !cells.isEmpty();
    }

    protected final Direction facingOf(ImmutableBlockState state) {
        String n = enumName(state, facingProperty);
        if (n != null) {
            try {
                return Direction.valueOf(n.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return Direction.NORTH;
    }

    /** Read {@code cell_index}; defaults to 0 (master) if unreadable. */
    protected final int cellIndexOf(ImmutableBlockState state) {
        String n = enumName(state, cellIndexProperty);
        if (n != null) {
            try {
                return Integer.parseInt(n.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    // ---------------- placement ----------------

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        if (!isMultiCellBlock(state)) {
            return state;
        }
        Direction facing = horizontalFacing(context);
        ImmutableBlockState result = withValue(state, facingProperty, facing.name());
        result = withValue(result, cellIndexProperty, "0");
        return result;
    }

    private Direction horizontalFacing(BlockPlaceContext context) {
        try {
            for (Direction d : context.getNearestLookingDirections()) {
                if (d.axis().isHorizontal()) {
                    return d.opposite();
                }
            }
        } catch (Throwable ignored) {
        }
        return Direction.NORTH;
    }

    @Override
    public void onPlace(Object thisBlock, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos nmsPos, net.minecraft.world.level.block.state.BlockState nmsState) {
        try {
            World world = level.getWorld();
            if (world == null) return;
            org.bukkit.block.Block placed = world.getBlockAt(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(placed);
            if (!isMultiCellBlock(state)) return;
            // Only the master (index 0) spawns the rest of the structure — the secondaries this
            // very routine places must not recurse and try to spawn a structure of their own.
            if (cellIndexOf(state) != 0) return;

            Direction facing = facingOf(state);
            BlockPos masterPos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());

            // The actual place/idempotency/rollback ALGORITHM is shared with CelledDataMachineBehavior
            // via MultiCellPlacement — only how a cell's role gets STAMPED differs (this class needs
            // a numeric cell_index, not just core/part, because MultiblockMembershipRegistry's
            // contraption pickup math resolves the master from that exact index; see MultiCellPlacement's
            // class javadoc for why that's not unified away).
            MultiCellPlacement.autoPlaceParts(world, placed, masterPos, facing, cells, state,
                    (s, index) -> isMultiCellBlock(s) && cellIndexOf(s) == index,
                    (coreState, index) -> withValue(coreState, cellIndexProperty, String.valueOf(index)),
                    null);
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("MultiCellBlockBehavior onPlace failed", t);
        }
    }

    // ---------------- break ----------------

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos nmsPos, net.minecraft.world.level.block.state.BlockState nmsState,
            Boolean movedByPiston) {
        if (removingStructure) return; // guard: removing the rest must not recurse
        try {
            World world = level.getWorld();
            if (world == null) return;
            ImmutableBlockState state = null;
            try {
                org.bukkit.block.data.BlockData data =
                        net.momirealms.craftengine.bukkit.util.BlockStateUtils.fromBlockData(nmsState);
                if (data != null) state = CraftEngineBlocks.getCustomBlockState(data);
            } catch (Throwable ignored) {
            }
            if (!isMultiCellBlock(state)) return;

            Direction facing = facingOf(state);
            int myIndex = cellIndexOf(state);
            BlockPos thisPos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            BlockPos masterPos = MultiCellGeometry.masterPos(thisPos, facing, cells, myIndex);

            removingStructure = true;
            try {
                MultiCellPlacement.breakStructure(world, masterPos, facing, cells, thisPos);
            } finally {
                removingStructure = false;
            }
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("MultiCellBlockBehavior break failed", t);
        }
    }

    // ---------------- right-click: forward to master ----------------

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        if (!isMultiCellBlock(state)) {
            return onMasterUse(context, state, context.getClickedPos());
        }
        try {
            Direction facing = facingOf(state);
            int myIndex = cellIndexOf(state);
            BlockPos clicked = context.getClickedPos();
            BlockPos masterPos = MultiCellGeometry.masterPos(clicked, facing, cells, myIndex);

            ImmutableBlockState masterState = state;
            if (myIndex != 0) {
                Object pw = context.getLevel().platformWorld();
                if (pw instanceof World world) {
                    org.bukkit.block.Block masterBlock = world.getBlockAt(masterPos.x(), masterPos.y(), masterPos.z());
                    ImmutableBlockState resolved = CraftEngineBlocks.getCustomBlockState(masterBlock);
                    if (resolved != null) masterState = resolved;
                }
            }
            return onMasterUse(context, masterState, masterPos);
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("MultiCellBlockBehavior use failed", t);
            return InteractionResult.PASS;
        }
    }

    /** Hook invoked on the resolved MASTER cell for a right-click on any cell. Default: no-op. */
    protected InteractionResult onMasterUse(UseOnContext context, ImmutableBlockState masterState, BlockPos masterPos) {
        return InteractionResult.PASS;
    }

    // ---------------- EntityBlock: master hosts controller ----------------

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public final int controllerId() {
        return controllerId;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        ImmutableBlockState state = blockEntity.blockState();
        if (isMultiCellBlock(state) && cellIndexOf(state) != 0) {
            return new TransientController(blockEntity);
        }
        return createMasterController(blockEntity);
    }

    /** Build the controller hosted by the MASTER cell. Default is a no-op {@link TransientController}. */
    protected BlockEntityController createMasterController(BlockEntity blockEntity) {
        return new TransientController(blockEntity);
    }

    /** Trivial no-op controller used by every non-master cell (and the default master). */
    public static final class TransientController extends BlockEntityController {
        public TransientController(BlockEntity blockEntity) {
            super(blockEntity);
        }
    }

    // ---------------- util ----------------

    protected static boolean isReplaceable(org.bukkit.block.Block block) {
        if (block == null) return false;
        org.bukkit.Material m = block.getType();
        return m.isAir() || m == org.bukkit.Material.WATER || m == org.bukkit.Material.LAVA
                || m == org.bukkit.Material.SHORT_GRASS || m == org.bukkit.Material.TALL_GRASS
                || m == org.bukkit.Material.SNOW;
    }

    // ---------------- factory ----------------

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String facingProp = (String) arguments.getOrDefault("facing_property", DEFAULT_FACING_PROPERTY);
            String cellIndexProp = (String) arguments.getOrDefault("cell_index_property", DEFAULT_CELL_INDEX_PROPERTY);
            List<MultiCellGeometry.Offset> cells = parseCells(arguments.get("cells"));
            return new MultiCellBlockBehavior(block, facingProp, cellIndexProp, cells);
        }

        /** Exposed so subclass factories can parse the same {@code cells:} config shape. */
        @SuppressWarnings("unchecked")
        public static List<MultiCellGeometry.Offset> parseCells(Object raw) {
            List<MultiCellGeometry.Offset> out = new ArrayList<>();
            if (!(raw instanceof List<?> list)) return out;
            for (Object o : list) {
                if (!(o instanceof java.util.Map<?, ?> m)) continue;
                int x = intOf(m.get("x")), y = intOf(m.get("y")), z = intOf(m.get("z"));
                out.add(new MultiCellGeometry.Offset(x, y, z));
            }
            return out;
        }

        private static int intOf(Object v) {
            return v instanceof Number n ? n.intValue() : 0;
        }
    }
}
