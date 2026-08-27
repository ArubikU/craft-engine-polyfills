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
            .property("rel_x",        obj -> ScriptValue.of(ref(obj).relX()))
            .property("rel_y",        obj -> ScriptValue.of(ref(obj).relY()))
            .property("rel_z",        obj -> ScriptValue.of(ref(obj).relZ()))
            .property("is_master",    obj -> ScriptValue.of(ref(obj).isMaster()))
            .property("is_core_part", obj -> ScriptValue.of(ref(obj).isMaster()))
            .property("is_part",      obj -> ScriptValue.of(!ref(obj).isMaster()))
            .property("part_count",   obj -> ScriptValue.of(ref(obj).partCount()))
            .property("formed",       obj -> ScriptValue.of(ref(obj).formed()))
            .property("part_id",      obj -> ScriptValue.of(Math.abs(ref(obj).relX() * 100 + ref(obj).relY() * 10 + ref(obj).relZ())))
            .property("core_block",   obj -> {
                MultiBlockRef r = ref(obj);
                return (r.level() != null && r.corePos() != null) ? BlockType.wrap(r.level(), r.corePos()) : ScriptValue.NULL;
            })
            .methodTyped3("is_at", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MultiBlockRef r, Double x, Double y, Double z) ->
                    (int) (double) x == r.relX() && (int) (double) y == r.relY() && (int) (double) z == r.relZ())
            // side(side_name) — NOT migrated to methodTyped1: a missing arg still runs the handler
            // with a default of "" ("default-if-missing" shape) rather than short-circuiting, which
            // methodTypedN's onMissingArgs can't express (it skips the handler body entirely). Left
            // untyped.
            .method("side", (obj, args) -> {
                String side = args.isEmpty() ? "" : args.get(0).asStr().toLowerCase();
                MultiBlockRef r = ref(obj);
                return ScriptValue.of(switch (side) {
                    case "left"   -> r.relX() < 0;
                    case "right"  -> r.relX() > 0;
                    case "front"  -> r.relZ() < 0;
                    case "back"   -> r.relZ() > 0;
                    case "top"    -> r.relY() > 0;
                    case "bottom" -> r.relY() < 0;
                    case "center" -> r.relX() == 0 && r.relZ() == 0;
                    default       -> false;
                });
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

    private static MultiBlockRef ref(Object obj) { return (MultiBlockRef) obj; }
}
