package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Location type: world + xyz. Handles contraption sub-levels transparently. */
public final class LocationType {

    public record LocationRef(ServerLevel level, double x, double y, double z) {}

    private LocationType() {}

    public static void register() {
        PolyTypeRegistry.define("Location")
            // ref(obj) is a plain `(LocationRef) obj` cast, so LocationRef is the typed instance
            // parameter directly.
            .propertyTyped("x",       TypeCodecs.DOUBLE, (LocationRef r) -> r.x())
            .propertyTyped("y",       TypeCodecs.DOUBLE, (LocationRef r) -> r.y())
            .propertyTyped("z",       TypeCodecs.DOUBLE, (LocationRef r) -> r.z())
            .propertyTyped("block_x", TypeCodecs.DOUBLE, (LocationRef r) -> Math.floor(r.x()))
            .propertyTyped("block_y", TypeCodecs.DOUBLE, (LocationRef r) -> Math.floor(r.y()))
            .propertyTyped("block_z", TypeCodecs.DOUBLE, (LocationRef r) -> Math.floor(r.z()))
            // polyType("World", ServerLevel) — identical to WorldType.wrap (ofObj for non-null,
            // NULL for null), so the explicit null branch collapses into the codec.
            .propertyTyped("world",   TypeCodecs.polyType("World", ServerLevel.class), (LocationRef r) -> r.level())
            // STRING with a null return — ScriptValue.of(String) maps null to NULL, exactly the
            // old ScriptValue.NULL branches.
            .propertyTyped("biome",   TypeCodecs.STRING, (LocationRef r) -> {
                if (r.level() == null) return null;
                try {
                    var biome = r.level().getBiome(new BlockPos((int) r.x(), (int) r.y(), (int) r.z()));
                    return biome.unwrapKey().map(Object::toString).orElse("unknown");
                } catch (Throwable ignored) { return null; }
            })
            // Typed via null sentinels on all three slots: no codec decodes a PRESENT argument to
            // Java null (asNum() is primitive-backed, boxed only on return), and arguments are
            // positional, so `zArg == null` is exactly the old `args.size() < 3`. The group fallback
            // is preserved by branching on that ONE check — a short call ignores whatever it passed
            // and falls back on all three of this Location's own rounded coords, computed per call
            // off the live instance inside the body.
            .methodTypedOpt3("block", TypeCodecs.DOUBLE, null, TypeCodecs.DOUBLE, null,
                TypeCodecs.DOUBLE, null, TypeCodecs.RAW,
                (LocationRef r, Double xArg, Double yArg, Double zArg) -> {
                if (r.level() == null) return ScriptValue.NULL;
                boolean given = zArg != null;
                int bx = given ? (int)(double) xArg : (int)Math.floor(r.x());
                int by = given ? (int)(double) yArg : (int)Math.floor(r.y());
                int bz = given ? (int)(double) zArg : (int)Math.floor(r.z());
                return BlockType.wrap(r.level(), new BlockPos(bx, by, bz));
            })
            // Return codec is TypeCodecs.RAW (identity passthrough) rather than DOUBLE: the
            // original returns ScriptValue.NULL on missing args and ScriptValue.of(number)
            // otherwise — RAW keeps both paths byte-for-byte identical (onMissingArgs =
            // ScriptValue.NULL, body still calls ScriptValue.of(...) itself).
            .methodTyped3("distance", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (LocationRef r, Double xArg, Double yArg, Double zArg) -> {
                    double dx = r.x() - xArg, dy = r.y() - yArg, dz = r.z() - zArg;
                    return ScriptValue.of(Math.sqrt(dx*dx + dy*dy + dz*dz));
                })
            .methodTyped3("distance_sq", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (LocationRef r, Double xArg, Double yArg, Double zArg) -> {
                    double dx = r.x() - xArg, dy = r.y() - yArg, dz = r.z() - zArg;
                    return ScriptValue.of(dx*dx + dy*dy + dz*dz);
                });
    }

    /** Handles contraption sub-levels: projects to real-world coordinates. */
    public static ScriptValue wrap(ServerLevel level, double x, double y, double z) {
        if (level == null) return ScriptValue.NULL;
        if (level instanceof ContraptionLevel cl) {
            Vec3 real = cl.realWorldPositionOf(new Vec3(x, y, z));
            if (cl.realLevel() instanceof ServerLevel rsl)
                return ScriptValue.ofObj("Location", new LocationRef(rsl, real.x, real.y, real.z));
            return ScriptValue.ofObj("Location", new LocationRef(level, real.x, real.y, real.z));
        }
        return ScriptValue.ofObj("Location", new LocationRef(level, x, y, z));
    }

    public static ScriptValue wrapEntity(Entity entity) {
        if (entity.level() instanceof ServerLevel sl)
            return wrap(sl, entity.getX(), entity.getY(), entity.getZ());
        return ScriptValue.NULL;
    }

    // ref(Object) — the old `(LocationRef) obj` cast helper — was removed: every property is now
    // propertyTyped with a LocationRef instance parameter (every method was already methodTypedN),
    // so the typed registration's own generic cast does the identical conversion.
}
