package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public final class MultiBlockType {

    /**
     * Instance for a multiblock context. Includes core level+pos+facing so
     * scripts can resolve world-space block positions from schema-relative coords.
     */
    public record MultiBlockRef(
        int relX, int relY, int relZ,
        boolean isMaster, int partCount, boolean formed,
        ServerLevel level, BlockPos corePos, String coreFacing
    ) {}

    private MultiBlockType() {}

    public static void register() {
        PolyTypeRegistry.define("MultiBlock")
            .propertyTyped("rel_x",        TypeCodecs.DOUBLE, (MultiBlockRef r) -> (double) r.relX())
            .propertyTyped("rel_y",        TypeCodecs.DOUBLE, (MultiBlockRef r) -> (double) r.relY())
            .propertyTyped("rel_z",        TypeCodecs.DOUBLE, (MultiBlockRef r) -> (double) r.relZ())
            .propertyTyped("is_master",    TypeCodecs.BOOL,   (MultiBlockRef r) -> r.isMaster())
            .propertyTyped("is_core_part", TypeCodecs.BOOL,   (MultiBlockRef r) -> r.isMaster())
            .propertyTyped("is_part",      TypeCodecs.BOOL,   (MultiBlockRef r) -> !r.isMaster())
            .propertyTyped("part_count",   TypeCodecs.DOUBLE, (MultiBlockRef r) -> (double) r.partCount())
            .propertyTyped("formed",       TypeCodecs.BOOL,   (MultiBlockRef r) -> r.formed())
            .propertyTyped("part_id",      TypeCodecs.DOUBLE,
                (MultiBlockRef r) -> (double) Math.abs(r.relX() * 100 + r.relY() * 10 + r.relZ()))
            // BlockType.wrap's own null guard is the null check already in front of it here, so
            // constructing the BlockRef directly (null when either half is missing, which the codec
            // encodes back to NULL) is exactly the old result.
            .propertyTyped("core_block", TypeCodecs.polyType("Block", BlockType.BlockRef.class),
                (MultiBlockRef r) -> (r.level() != null && r.corePos() != null)
                        ? new BlockType.BlockRef(r.level(), r.corePos()) : null)
            .methodTyped3("is_at", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MultiBlockRef r, Double x, Double y, Double z) ->
                    (int) (double) x == r.relX() && (int) (double) y == r.relY() && (int) (double) z == r.relZ())
            // side(side_name) — a missing arg still runs the handler with a "" default rather than
            // short-circuiting, which is exactly methodTypedOpt1's shape (STRING's decode is the
            // same asStr() the old body did; "" lower-cases to "" and falls through to `default`,
            // as before).
            .methodTypedOpt1("side", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (MultiBlockRef r, String sideArg) -> switch (sideArg.toLowerCase()) {
                    case "left"   -> r.relX() < 0;
                    case "right"  -> r.relX() > 0;
                    case "front"  -> r.relZ() < 0;
                    case "back"   -> r.relZ() > 0;
                    case "top"    -> r.relY() > 0;
                    case "bottom" -> r.relY() < 0;
                    case "center" -> r.relX() == 0 && r.relZ() == 0;
                    default       -> false;
                })
            // get_part_block(schemaRelX, schemaRelY, schemaRelZ)
            // Returns the world-space block at the given schema-relative offset,
            // rotated according to the core's facing direction.
            // Schema is defined with NORTH as identity (relX=left, relZ=front).
            .methodTyped3("get_part_block", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (MultiBlockRef r, Double sxD, Double syD, Double szD) -> {
                    if (r.level() == null || r.corePos() == null) return ScriptValue.NULL;
                    int sx = (int) (double) sxD, sy = (int) (double) syD, sz = (int) (double) szD;
                    BlockPos schemaOffset = new BlockPos(sx, sy, sz);
                    Direction facing = Direction.byName(r.coreFacing() != null ? r.coreFacing() : "north");
                    BlockPos rotated = rotateOffset(schemaOffset, facing);
                    return BlockType.wrap(r.level(), r.corePos().offset(rotated));
                })
            // get_side_block(side) — "left","right","front","back","top","bottom"
            // Returns the block adjacent to the core in the given facing-relative direction
            .methodTyped1("get_side_block", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MultiBlockRef r, String sideArg) -> {
                    if (r.level() == null || r.corePos() == null) return ScriptValue.NULL;
                    String side = sideArg.toLowerCase();
                    Direction facing = Direction.byName(r.coreFacing() != null ? r.coreFacing() : "north");
                    if (facing == null) facing = Direction.NORTH;
                    Direction worldDir = switch (side) {
                        case "front"  -> facing;
                        case "back"   -> facing.getOpposite();
                        case "left"   -> facing.getCounterClockWise(Direction.Axis.Y);
                        case "right"  -> facing.getClockWise(Direction.Axis.Y);
                        case "top"    -> Direction.UP;
                        case "bottom" -> Direction.DOWN;
                        default       -> Direction.byName(side);
                    };
                    if (worldDir == null) return ScriptValue.NULL;
                    return BlockType.wrap(r.level(), r.corePos().relative(worldDir));
                });
    }

    /** Rotate a schema offset by the core's facing. NORTH = identity. Mirrors MultiBlockBehavior. */
    public static BlockPos rotateOffset(BlockPos pos, Direction facing) {
        if (facing == null || facing == Direction.NORTH || facing.getAxis().isVertical()) return pos;
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return switch (facing) {
            case SOUTH -> new BlockPos(-x, y, -z);
            case WEST  -> new BlockPos(z, y, -x);
            case EAST  -> new BlockPos(-z, y, x);
            default    -> pos;
        };
    }

    public static ScriptValue wrap(int relX, int relY, int relZ, boolean isMaster, int partCount, boolean formed) {
        return ScriptValue.ofObj("MultiBlock", new MultiBlockRef(relX, relY, relZ, isMaster, partCount, formed, null, null, null));
    }

    public static ScriptValue wrap(int relX, int relY, int relZ, boolean isMaster, int partCount, boolean formed,
                                   ServerLevel level, BlockPos corePos, String coreFacing) {
        return ScriptValue.ofObj("MultiBlock", new MultiBlockRef(relX, relY, relZ, isMaster, partCount, formed, level, corePos, coreFacing));
    }
}
