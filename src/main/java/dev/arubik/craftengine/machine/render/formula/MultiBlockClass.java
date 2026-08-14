package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * PolyClass for multiblock structure context.
 * Exposed as "MultiBlock" in PolyContext when a machine is part of a multiblock.
 *
 * <p>Properties: {@code rel_x}, {@code rel_y}, {@code rel_z},
 * {@code is_master}, {@code part_count}, {@code formed}.</p>
 *
 * <p>Methods:
 * <ul>
 *   <li>{@code is_at(rx, ry, rz)} → bool: true if this block is at the given relative coords</li>
 *   <li>{@code side("left"|"right"|"front"|"back"|"top"|"bottom"|"center")} → bool</li>
 * </ul>
 * </p>
 */
public final class MultiBlockClass implements PolyClass {

    /** Relative position of this block within the multiblock structure. */
    private final int relX, relY, relZ;
    /** True when this is the controller/master block. */
    private final boolean isMaster;
    /** Total number of blocks in the structure. */
    private final int partCount;
    /** True when the multiblock is fully formed. */
    private final boolean formed;

    public MultiBlockClass(int relX, int relY, int relZ,
                           boolean isMaster, int partCount, boolean formed) {
        this.relX      = relX;
        this.relY      = relY;
        this.relZ      = relZ;
        this.isMaster  = isMaster;
        this.partCount = partCount;
        this.formed    = formed;
    }

    // ---- PolyClass ---------------------------------------------------------

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "rel_x"          -> PolyValue.of(relX);
            case "rel_y"          -> PolyValue.of(relY);
            case "rel_z"          -> PolyValue.of(relZ);
            case "is_master", "is_core_part" -> PolyValue.of(isMaster);
            case "is_part"        -> PolyValue.of(!isMaster);  // true for non-master blocks
            case "part_count"     -> PolyValue.of(partCount);
            case "formed"         -> PolyValue.of(formed);
            // Absolute part index as a single value (useful for per-part effects)
            case "part_id"        -> PolyValue.of(Math.abs(relX * 100 + relY * 10 + relZ));
            default               -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {
            // MultiBlock.is_at(rx, ry, rz) → bool
            case "is_at" -> {
                if (args.size() < 3) yield PolyValue.of(false);
                yield PolyValue.of(
                        (int) args.get(0).asNum() == relX &&
                        (int) args.get(1).asNum() == relY &&
                        (int) args.get(2).asNum() == relZ);
            }
            // MultiBlock.side("left"|"right"|"front"|"back"|"top"|"bottom"|"center") → bool
            case "side" -> {
                String side = args.isEmpty() ? "" : args.get(0).asStr().toLowerCase();
                yield PolyValue.of(isOnSide(side));
            }
            default -> PolyValue.NULL;
        };
    }

    // ---- Private helpers ---------------------------------------------------

    private boolean isOnSide(String side) {
        return switch (side) {
            case "left"   -> relX < 0;
            case "right"  -> relX > 0;
            case "front"  -> relZ < 0;
            case "back"   -> relZ > 0;
            case "top"    -> relY > 0;
            case "bottom" -> relY < 0;
            case "center" -> relX == 0 && relZ == 0;
            default       -> false;
        };
    }
}
