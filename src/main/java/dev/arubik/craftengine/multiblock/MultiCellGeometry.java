package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * Pure geometry for an arbitrary-shaped AUTO-PLACING multi-cell block: place one master block and
 * every other cell declared in {@code cells} gets placed/removed alongside it. Generalizes {@link
 * HorizontalDoubleGeometry}'s fixed 2-cell horizontal case to any list of offsets — a 4-tall
 * vertical tower is just {@code cells = [(0,1,0), (0,2,0), (0,3,0)]}; the horizontal-double
 * equivalent is {@code cells = [(1,0,0)]} rotated the same way {@link HorizontalDoubleGeometry}
 * rotates its RIGHT offset.
 *
 * <p>Offsets are authored SCHEMA-relative as if the block faced NORTH, then rotated by the block's
 * actual horizontal facing via {@link MultiBlockGeometry#rotate} — the exact same convention the
 * "assembled" multiblock system already uses (see {@link MultiBlockBehavior#rotate}), so both
 * multi-cell systems in this codebase agree on what "facing" does to an offset. {@code y} (vertical)
 * is never rotated, matching the intuition that a tower stacks straight up regardless of which way
 * it faces.
 *
 * <p>Cell index convention: {@code 0} = the master (implicit, not itself listed in {@code cells});
 * index {@code i} (1-based) = {@code cells.get(i - 1)}. No block-state property can economically
 * represent this for an unbounded N with an enum the way {@link HorizontalDoubleGeometry}'s
 * left/right {@code half} does — callers store the index as a small integer state property instead
 * (see {@link MultiCellBlockBehavior}).
 */
public final class MultiCellGeometry {

    private MultiCellGeometry() {
    }

    /** A schema-relative offset, authored as if the structure faced NORTH. */
    public record Offset(int x, int y, int z) {

        /** This offset rotated into world space for the given actual facing. */
        public BlockPos rotated(Direction facing) {
            int[] r = MultiBlockGeometry.rotate(x, y, z, facingIndex(facing));
            return new BlockPos(r[0], r[1], r[2]);
        }
    }

    /** Facing -> rotation index. SAME convention as {@link MultiBlockGeometry} (0 = NORTH identity). */
    public static int facingIndex(Direction f) {
        if (f == null)
            return 0;
        return switch (f) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0; // NORTH, UP, DOWN
        };
    }

    /** World position of cell {@code index} (0 = master), given the master's own position/facing. */
    public static BlockPos cellPos(BlockPos masterPos, Direction facing, List<Offset> cells, int index) {
        if (index == 0)
            return masterPos;
        Offset o = cells.get(index - 1);
        return add(masterPos, o.rotated(facing));
    }

    private static BlockPos add(BlockPos a, BlockPos b) {
        return new BlockPos(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }

    /** Every cell's world position, master first, then secondaries in schema order. */
    public static List<BlockPos> allCellPositions(BlockPos masterPos, Direction facing, List<Offset> cells) {
        List<BlockPos> out = new ArrayList<>(cells.size() + 1);
        out.add(masterPos);
        for (Offset o : cells)
            out.add(add(masterPos, o.rotated(facing)));
        return out;
    }

    /**
     * Resolve the master's world position from ANY cell, given that cell's own position/index. Used
     * when a non-master cell is broken or right-clicked and needs to find (and forward to) the
     * master.
     */
    public static BlockPos masterPos(BlockPos thisPos, Direction facing, List<Offset> cells, int cellIndex) {
        if (cellIndex == 0)
            return thisPos;
        BlockPos rotated = cells.get(cellIndex - 1).rotated(facing);
        return new BlockPos(thisPos.x() - rotated.x(), thisPos.y() - rotated.y(), thisPos.z() - rotated.z());
    }

    /**
     * Parses a {@code cells:} list out of ANY block behavior's own config args — deliberately not
     * tied to {@code MachineDefinition} or any other machine-specific type, so a non-machine block
     * behavior can opt into the exact same auto-placement by reading its own {@code
     * ConfigSection.get("cells")} with this one helper. Shape (mirrors what the old
     * MachineDefinition-level "structure" block used, just relocated):
     * <pre>{@code
     * behavior:
     *   type: polyfills:data_machine
     *   machine: polyfills:energy_windmill
     *   cells:
     *     - { right: 0, up: 1, forward: 0 }
     *     - { right: 0, up: 2, forward: 0 }
     *     - { right: 0, up: 3, forward: 0 }
     * }</pre>
     * {@code right}/{@code forward} become this record's {@code x}/{@code z} (rotated by facing);
     * {@code up} becomes {@code y} (never rotated). Returns an empty list (not null) for a behavior
     * that declares no {@code cells} — the normal single-block case.
     */
    public static List<Offset> parseCells(Object rawCells) {
        List<Offset> out = new ArrayList<>();
        if (!(rawCells instanceof List<?> list))
            return out;
        for (Object o : list) {
            if (!(o instanceof java.util.Map<?, ?> m))
                continue;
            int right = intOf(m.get("right"));
            int up = intOf(m.get("up"));
            int forward = intOf(m.get("forward"));
            out.add(new Offset(right, up, forward));
        }
        return out;
    }

    private static int intOf(Object v) {
        if (v == null)
            return 0;
        if (v instanceof Number n)
            return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Throwable t) {
            return 0;
        }
    }
}
