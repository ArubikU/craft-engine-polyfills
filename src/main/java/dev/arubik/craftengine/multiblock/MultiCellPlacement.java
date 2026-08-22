package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * The all-or-nothing auto-placement / cascading-breakage ALGORITHM shared by every "place one
 * block, the rest auto-places" consumer in this codebase — {@link MultiCellBlockBehavior}
 * (standalone, non-machine structures like the workbench) and {@code CelledDataMachineBehavior}
 * (data-driven machines like the energy windmill).
 *
 * <p>Deliberately NOT unifying how a cell's role gets STORED: {@link MultiCellBlockBehavior} marks
 * cells with a numeric {@code cell_index} block-state property because {@code
 * MultiblockMembershipRegistry} (the contraption system) needs that exact index to resolve a
 * structure's master when a workbench gets picked up mid-build; {@code CelledDataMachineBehavior}
 * only ever needs core-vs-part (via {@link MultiBlockRole}, reusing the assembled-multiblock
 * system's existing part-forwarding machinery for IO/menu redirection). Those are two genuinely
 * different, both-correct representations — this class shares the geometry/rollback ALGORITHM
 * that used to be duplicated line-for-line between the two behaviors, via caller-supplied
 * predicates/state-producers, without forcing either caller to change its own state model.
 */
public final class MultiCellPlacement {

    private MultiCellPlacement() {
    }

    /**
     * Places every secondary cell around {@code core} (already in the world), all-or-nothing.
     * Idempotent: if every secondary is already correctly present (per {@code isOurCell}), does
     * nothing and returns {@code true} — {@code onPlace} fires more than once for the same core
     * because each secondary's own placement re-triggers a neighbour-update pass on it.
     *
     * @param coreState   the core's OWN state, handed to {@code cellStateOf} to derive each
     *                    secondary's state (so a secondary can inherit facing etc. from the core)
     * @param isOurCell   given the (already-in-world) state at a computed position and the
     *                    1-based index expected there, true if that state is correctly THIS
     *                    structure's secondary for that exact index — used only for the
     *                    idempotency check (a role-only caller ignores the index argument)
     * @param cellStateOf given the core's state and the 1-based cell index, the state to stamp on
     *                    a newly-placed secondary (a numeric-index caller varies the result per
     *                    index; a role-based caller ignores the index and always returns "part")
     * @param onCellPlaced optional hook invoked with (block, 1-based cell index) after each
     *                    secondary is placed, e.g. to link its controller back to the core — may
     *                    be {@code null}
     * @return {@code true} if the structure exists afterward (placed or already there); {@code
     *         false} if placement failed and {@code core} itself was removed by this method
     */
    public static boolean autoPlaceParts(World world, org.bukkit.block.Block core, BlockPos corePos,
            Direction facing, List<MultiCellGeometry.Offset> cells, ImmutableBlockState coreState,
            java.util.function.BiPredicate<ImmutableBlockState, Integer> isOurCell,
            java.util.function.BiFunction<ImmutableBlockState, Integer, ImmutableBlockState> cellStateOf,
            java.util.function.BiConsumer<org.bukkit.block.Block, Integer> onCellPlaced) {
        boolean allThere = true;
        for (int i = 1; i <= cells.size() && allThere; i++) {
            BlockPos p = MultiCellGeometry.cellPos(corePos, facing, cells, i);
            org.bukkit.block.Block b = world.getBlockAt(p.x(), p.y(), p.z());
            ImmutableBlockState s = CraftEngineBlocks.getCustomBlockState(b);
            allThere = s != null && isOurCell.test(s, i);
        }
        if (allThere)
            return true;

        List<org.bukkit.block.Block> placed = new ArrayList<>();
        for (int i = 1; i <= cells.size(); i++) {
            BlockPos p = MultiCellGeometry.cellPos(corePos, facing, cells, i);
            org.bukkit.block.Block target = world.getBlockAt(p.x(), p.y(), p.z());
            if (!isReplaceable(target)) {
                // All-or-nothing: never leave a half-built structure — roll back everything this
                // attempt placed, plus the core itself, so the player just gets their item back.
                for (org.bukkit.block.Block b : placed)
                    CraftEngineBlocks.remove(b, false);
                CraftEngineBlocks.remove(core, false);
                return false;
            }
            ImmutableBlockState cellState = cellStateOf.apply(coreState, i);
            Location loc = new Location(world, p.x(), p.y(), p.z());
            CraftEngineBlocks.place(loc, cellState, UpdateFlags.UPDATE_ALL, false);
            placed.add(target);
            if (onCellPlaced != null)
                onCellPlaced.accept(target, i);
        }
        return true;
    }

    /**
     * Removes every OTHER cell of the structure anchored at {@code corePos} — called from the
     * cell that already left the world (its position is {@code removedPos}, so it's skipped).
     * Callers must guard against recursion themselves (removing a cell here would otherwise
     * re-trigger this same removal logic for that cell too).
     */
    public static void breakStructure(World world, BlockPos corePos, Direction facing,
            List<MultiCellGeometry.Offset> cells, BlockPos removedPos) {
        for (int i = 0; i <= cells.size(); i++) {
            BlockPos p = MultiCellGeometry.cellPos(corePos, facing, cells, i);
            if (p.x() == removedPos.x() && p.y() == removedPos.y() && p.z() == removedPos.z())
                continue; // this is the cell that already broke
            org.bukkit.block.Block b = world.getBlockAt(p.x(), p.y(), p.z());
            if (CraftEngineBlocks.getCustomBlockState(b) != null)
                CraftEngineBlocks.remove(b, false);
        }
    }

    public static boolean isReplaceable(org.bukkit.block.Block block) {
        if (block == null)
            return false;
        org.bukkit.Material m = block.getType();
        return m.isAir() || m == org.bukkit.Material.WATER || m == org.bukkit.Material.LAVA
                || m == org.bukkit.Material.SHORT_GRASS || m == org.bukkit.Material.TALL_GRASS
                || m == org.bukkit.Material.SNOW;
    }
}
