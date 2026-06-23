package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Pure (NMS-free, side-effect-free) geometry for multiblock detection, so the "click any block of the
 * structure → find the core" logic is unit-testable without a running server. {@link MultiBlockBehavior}
 * delegates its {@code rotate} + candidate-core enumeration here.
 *
 * <p>Facing index: {@code 0 = NORTH (identity), 1 = EAST, 2 = SOUTH, 3 = WEST}. Vertical/unknown facings
 * use 0. The rotation matches the legacy {@code MultiBlockBehavior.rotate} switch exactly.</p>
 */
public final class MultiBlockGeometry {

    private MultiBlockGeometry() {
    }

    /** Rotate a schema offset (x,y,z) by a horizontal facing index. Returns a fresh {x,y,z}. */
    public static int[] rotate(int x, int y, int z, int facing) {
        return switch (facing) {
            case 2 -> new int[] { -x, y, -z }; // SOUTH
            case 3 -> new int[] { z, y, -x };  // WEST  (NORTH 0,0,-1 -> WEST -1,0,0)
            case 1 -> new int[] { -z, y, x };  // EAST  (NORTH 0,0,-1 -> EAST  1,0,0)
            default -> new int[] { x, y, z };  // NORTH / identity
        };
    }

    /**
     * Every distinct core position implied by the clicked block being SOME cell of the schema: for each
     * schema cell {@code C}, the core would be at {@code clicked - rotate(C - coreOffset, facing)}.
     * Order: {@code coreOffset} first (the "clicked block IS the core" legacy case), then the parts.
     *
     * @param cells      schema cells RELATIVE to the schema origin (must include {@code coreOffset})
     * @param coreOffset the core's cell within the schema
     * @param clicked    the clicked world position
     * @param facing     horizontal facing index (0..3)
     */
    public static List<int[]> candidateCores(Collection<int[]> cells, int[] coreOffset, int[] clicked, int facing) {
        // De-dup by packed coordinates so identical candidates aren't validated twice.
        LinkedHashSet<Long> seen = new LinkedHashSet<>();
        List<int[]> out = new ArrayList<>();
        for (int[] cell : cells) {
            int[] off = rotate(cell[0] - coreOffset[0], cell[1] - coreOffset[1], cell[2] - coreOffset[2], facing);
            int cx = clicked[0] - off[0], cy = clicked[1] - off[1], cz = clicked[2] - off[2];
            long key = pack(cx, cy, cz);
            if (seen.add(key))
                out.add(new int[] { cx, cy, cz });
        }
        return out;
    }

    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
