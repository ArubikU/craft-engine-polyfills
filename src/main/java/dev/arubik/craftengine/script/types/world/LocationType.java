package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
            .property("x",       obj -> ScriptValue.of(ref(obj).x()))
            .property("y",       obj -> ScriptValue.of(ref(obj).y()))
            .property("z",       obj -> ScriptValue.of(ref(obj).z()))
            .property("block_x", obj -> ScriptValue.of(Math.floor(ref(obj).x())))
            .property("block_y", obj -> ScriptValue.of(Math.floor(ref(obj).y())))
            .property("block_z", obj -> ScriptValue.of(Math.floor(ref(obj).z())))
            .property("world",   obj -> {
                ServerLevel lvl = ref(obj).level();
                return lvl != null ? WorldType.wrap(lvl) : ScriptValue.NULL;
            })
            .property("biome",   obj -> {
                LocationRef r = ref(obj);
                if (r.level() == null) return ScriptValue.NULL;
                try {
                    var biome = r.level().getBiome(new BlockPos((int) r.x(), (int) r.y(), (int) r.z()));
                    return ScriptValue.of(biome.unwrapKey().map(Object::toString).orElse("unknown"));
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            .method("block",       (obj, args) -> {
                LocationRef r = ref(obj);
                if (r.level() == null) return ScriptValue.NULL;
                int bx = args.size() >= 3 ? (int)args.get(0).asNum() : (int)Math.floor(r.x());
                int by = args.size() >= 3 ? (int)args.get(1).asNum() : (int)Math.floor(r.y());
                int bz = args.size() >= 3 ? (int)args.get(2).asNum() : (int)Math.floor(r.z());
                return BlockType.wrap(r.level(), new BlockPos(bx, by, bz));
            })
            .method("distance",    (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                LocationRef r = ref(obj);
                double dx = r.x() - args.get(0).asNum(), dy = r.y() - args.get(1).asNum(), dz = r.z() - args.get(2).asNum();
                return ScriptValue.of(Math.sqrt(dx*dx + dy*dy + dz*dz));
            })
            .method("distance_sq", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                LocationRef r = ref(obj);
                double dx = r.x() - args.get(0).asNum(), dy = r.y() - args.get(1).asNum(), dz = r.z() - args.get(2).asNum();
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

    private static LocationRef ref(Object obj) { return (LocationRef) obj; }
}
