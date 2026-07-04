package dev.arubik.craftengine.multiblock;

import org.bukkit.Location;
import org.bukkit.World;

import dev.arubik.craftengine.multiblock.HorizontalDoubleGeometry.Half;
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
 * Reusable HORIZONTAL 2-wide double-block behavior. CraftEngine only ships a
 * VERTICAL {@code DoubleHighBlockBehavior}; this is the sideways analogue.
 *
 * <p>A horizontal double block occupies two horizontally-adjacent cells linked by
 * pure position math (no extra block-state beyond {@code facing} + {@code half}):
 * <ul>
 *   <li>The placed block becomes the {@code LEFT} (master) half.</li>
 *   <li>A {@code RIGHT} half is placed at {@code master.relative(facing.clockWise())}.</li>
 *   <li>The partner is always reachable from either half via {@link HorizontalDoubleGeometry}.</li>
 * </ul>
 * See {@link HorizontalDoubleGeometry} for the exact direction convention.
 *
 * <h2>How to make any 2-wide block</h2>
 * <ol>
 *   <li>Give the block a {@code 4-direction} facing property and a string/enum
 *       {@code half} property with values {@code left} and {@code right}.</li>
 *   <li>Register a behavior whose factory returns a {@code HorizontalDoubleBlockBehavior}
 *       (or a subclass overriding {@link #onMasterUse}). Optionally set the config
 *       keys {@code facing_property} / {@code half_property} to rename the properties.</li>
 *   <li>Provide two models (left / right halves). Map them with
 *       {@code "half=left"} / {@code "half=right"} variants.</li>
 * </ol>
 * If either property is missing the behavior degrades gracefully to a normal
 * single block (no partner placed, no master forwarding).
 *
 * <h2>Rules implemented</h2>
 * <ul>
 *   <li><b>Placement</b> ({@link #updateStateForPlacement}): stamps the placed
 *       block as {@code half=left}, facing from the player. The actual RIGHT-half
 *       placement happens in {@link #onPlace} (the master must already exist).</li>
 *   <li><b>Placement</b> ({@link #onPlace}): if the RIGHT cell is not replaceable,
 *       removes the just-placed master (abort) rather than leaving a half-structure;
 *       otherwise places the RIGHT half.</li>
 *   <li><b>Break</b> ({@link #affectNeighborsAfterRemoval}): removes the partner
 *       half too, guarded against recursion.</li>
 *   <li><b>Right-click</b> ({@link #useWithoutItem}): resolves the master and calls
 *       {@link #onMasterUse}; the RIGHT half forwards rather than acting itself.</li>
 *   <li><b>EntityBlock</b>: only the master hosts a real controller; the RIGHT half
 *       gets a no-op {@link TransientController}.</li>
 * </ul>
 */
public class HorizontalDoubleBlockBehavior extends NmsBlockBehavior implements EntityBlock {

    /** Default config / property names. */
    public static final String DEFAULT_FACING_PROPERTY = "facing";
    public static final String DEFAULT_HALF_PROPERTY = "half";

    protected final String facingProperty;
    protected final String halfProperty;

    /** Re-entrancy guard so partner removal does not re-trigger break handling. */
    private boolean removingPartner = false;

    public HorizontalDoubleBlockBehavior(BlockDefinition block, String facingProperty, String halfProperty) {
        super(block);
        this.facingProperty = facingProperty != null ? facingProperty : DEFAULT_FACING_PROPERTY;
        this.halfProperty = halfProperty != null ? halfProperty : DEFAULT_HALF_PROPERTY;
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

    /** True iff this block actually defines both required properties. */
    protected final boolean isDoubleBlock(ImmutableBlockState state) {
        return state != null
                && state.getProperty(facingProperty) != null
                && state.getProperty(halfProperty) != null;
    }

    /** Read {@code facing}; defaults to NORTH if unreadable. */
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

    /** Read {@code half}; defaults to LEFT. */
    protected final Half halfOf(ImmutableBlockState state) {
        return Half.fromName(enumName(state, halfProperty));
    }

    // ---------------- public read-only accessors (contraption multiblock-membership expansion) ----------------

    /** Public wrapper over {@link #isDoubleBlock} — pure read, no side effects, safe to expose. */
    public final boolean isDoubleBlockPublic(ImmutableBlockState state) {
        return isDoubleBlock(state);
    }

    /** Public wrapper over {@link #facingOf}. */
    public final Direction facingOfPublic(ImmutableBlockState state) {
        return facingOf(state);
    }

    /** Public wrapper over {@link #halfOf}. */
    public final Half halfOfPublic(ImmutableBlockState state) {
        return halfOf(state);
    }

    // ---------------- placement ----------------

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        if (!isDoubleBlock(state)) {
            return state;
        }
        // The placed block is always the LEFT (master). Stamp facing from the
        // player's looking direction (horizontal) and half=left. The RIGHT half is
        // placed in onPlace once this master block actually exists in the world.
        Direction facing = horizontalFacing(context);
        ImmutableBlockState result = withValue(state, facingProperty, facing.name());
        result = withValue(result, halfProperty, "left");
        return result;
    }

    /** Nearest horizontal looking direction (front of the structure faces the player). */
    private Direction horizontalFacing(BlockPlaceContext context) {
        try {
            for (Direction d : context.getNearestLookingDirections()) {
                if (d.axis().isHorizontal()) {
                    // Block "facing" faces the player, i.e. opposite the look direction.
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
            if (!isDoubleBlock(state)) return;
            // Only the LEFT (master) spawns the partner. (RIGHT halves are created
            // by this very routine, so they must not recurse and place a third block.)
            if (halfOf(state) != Half.LEFT) return;

            Direction facing = facingOf(state);
            BlockPos masterPos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            BlockPos rightPos = HorizontalDoubleGeometry.rightCell(masterPos, facing);

            org.bukkit.block.Block rightBlock = world.getBlockAt(rightPos.x(), rightPos.y(), rightPos.z());

            // IDEMPOTENT: onPlace fires more than once for the master (the partner's
            // UPDATE_ALL re-triggers it). If the partner RIGHT half is already there,
            // do nothing — otherwise the 2nd pass would see it as "occupied" (its
            // auto_state host is a solid vanilla block) and wrongly abort the master.
            ImmutableBlockState rightExisting = CraftEngineBlocks.getCustomBlockState(rightBlock);
            if (rightExisting != null && isDoubleBlock(rightExisting) && halfOf(rightExisting) == Half.RIGHT) {
                return;
            }

            if (!isReplaceable(rightBlock)) {
                // Both halves must fit or nothing places: abort by removing the master
                // so the player never gets a half structure. (Creative gives the item
                // back on break; survival drops it via the loot table.)
                CraftEngineBlocks.remove(placed, false);
                return;
            }

            ImmutableBlockState rightState = withValue(state, halfProperty, "right");
            Location loc = new Location(world, rightPos.x(), rightPos.y(), rightPos.z());
            CraftEngineBlocks.place(loc, rightState, UpdateFlags.UPDATE_ALL, false);
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("HorizontalDoubleBlockBehavior onPlace failed", t);
        }
    }

    // ---------------- break ----------------

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos nmsPos, net.minecraft.world.level.block.state.BlockState nmsState,
            Boolean movedByPiston) {
        if (removingPartner) return; // guard: partner removal must not recurse
        try {
            World world = level.getWorld();
            if (world == null) return;
            // The block at our own pos is already gone; reconstruct half/facing from
            // the OLD NMS state (converted to BukkitData -> custom state).
            ImmutableBlockState state = null;
            try {
                org.bukkit.block.data.BlockData data =
                        net.momirealms.craftengine.bukkit.util.BlockStateUtils.fromBlockData(nmsState);
                if (data != null) state = CraftEngineBlocks.getCustomBlockState(data);
            } catch (Throwable ignored) {
            }
            if (!isDoubleBlock(state)) return;

            Direction facing = facingOf(state);
            Half half = halfOf(state);
            BlockPos thisPos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            BlockPos partner = HorizontalDoubleGeometry.partnerPos(thisPos, facing, half);

            org.bukkit.block.Block partnerBlock = world.getBlockAt(partner.x(), partner.y(), partner.z());
            ImmutableBlockState partnerState = CraftEngineBlocks.getCustomBlockState(partnerBlock);
            // Only remove if it really is the matching other half of THIS structure.
            if (partnerState == null || !isDoubleBlock(partnerState)) return;
            if (halfOf(partnerState) != half.other()) return;

            removingPartner = true;
            try {
                CraftEngineBlocks.remove(partnerBlock, false);
            } finally {
                removingPartner = false;
            }
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("HorizontalDoubleBlockBehavior break failed", t);
        }
    }

    // ---------------- right-click: forward to master ----------------

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        if (!isDoubleBlock(state)) {
            return onMasterUse(context, state, context.getClickedPos());
        }
        try {
            Direction facing = facingOf(state);
            Half half = halfOf(state);
            BlockPos clicked = context.getClickedPos();
            BlockPos masterPos = HorizontalDoubleGeometry.masterPos(clicked, facing, half);

            ImmutableBlockState masterState = state;
            if (half == Half.RIGHT) {
                Object pw = context.getLevel().platformWorld();
                if (pw instanceof World world) {
                    org.bukkit.block.Block masterBlock = world.getBlockAt(masterPos.x(), masterPos.y(), masterPos.z());
                    ImmutableBlockState resolved = CraftEngineBlocks.getCustomBlockState(masterBlock);
                    if (resolved != null) masterState = resolved;
                }
            }
            return onMasterUse(context, masterState, masterPos);
        } catch (Throwable t) {
            CraftEngine.instance().logger().warn("HorizontalDoubleBlockBehavior use failed", t);
            return InteractionResult.PASS;
        }
    }

    /**
     * Hook invoked on the resolved MASTER half for a right-click on either half.
     * Default: do nothing. Subclasses (e.g. a workbench) override to open a menu.
     *
     * @param context    the original use context (player / hand / world)
     * @param masterState the master half's block state
     * @param masterPos  the master half's position
     */
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
        // Only the LEFT (master) hosts a real controller; the RIGHT half is a marker.
        ImmutableBlockState state = blockEntity.blockState();
        if (isDoubleBlock(state) && halfOf(state) == Half.RIGHT) {
            return new TransientController(blockEntity);
        }
        return createMasterController(blockEntity);
    }

    /**
     * Build the controller hosted by the MASTER half. Default is a no-op
     * {@link TransientController}; subclasses override for a stateful controller.
     */
    protected BlockEntityController createMasterController(BlockEntity blockEntity) {
        return new TransientController(blockEntity);
    }

    /** Trivial no-op controller used by the RIGHT half (and the default master). */
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
            String halfProp = (String) arguments.getOrDefault("half_property", DEFAULT_HALF_PROPERTY);
            return new HorizontalDoubleBlockBehavior(block, facingProp, halfProp);
        }
    }
}
