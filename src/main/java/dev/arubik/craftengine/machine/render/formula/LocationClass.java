package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * {@link PolyClass} representing a world position.
 *
 * <p>The static factory {@link #forLevel(net.minecraft.server.level.ServerLevel, double, double, double)}
 * handles contraption sub-levels transparently: if the supplied level is a
 * {@link dev.arubik.craftengine.contraption.core.ContraptionLevel}, the local coordinates
 * are projected to real-world coordinates via
 * {@link dev.arubik.craftengine.contraption.core.ContraptionLevel#realWorldPositionOf(net.minecraft.world.phys.Vec3)}
 * and the real parent level is used for all world queries (biome, world name, etc.).
 * Callers never need to think about whether they are inside a contraption.</p>
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code x}, {@code y}, {@code z}           — real-world floating-point coordinates</li>
 *   <li>{@code block_x}, {@code block_y}, {@code block_z} — floored integer block coordinates</li>
 *   <li>{@code world}                             — dimension key path (e.g. "overworld")</li>
 *   <li>{@code biome}                             — namespaced biome at this block (e.g. "minecraft:plains")</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code distance(x, y, z)}    — Euclidean distance to the given point</li>
 *   <li>{@code distance_sq(x, y, z)} — squared Euclidean distance (cheaper, avoids sqrt)</li>
 * </ul>
 */
public final class LocationClass implements PolyClass {

    /** Always the real (non-contraption) level. */
    private final net.minecraft.server.level.ServerLevel level;
    /** Always real-world coordinates (contraption-local coords are projected in the factory). */
    private final double x, y, z;

    private LocationClass(net.minecraft.server.level.ServerLevel level, double x, double y, double z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Preferred factory.
     *
     * <p>If {@code level} is a contraption sub-level the coordinates are projected to real-world
     * space and the real parent level is stored instead, so all subsequent queries ({@code biome},
     * {@code world}, etc.) reflect the actual game world.</p>
     *
     * @param level the level the coordinates belong to (may be a contraption sub-level)
     * @param lx    X coordinate in that level's space
     * @param ly    Y coordinate
     * @param lz    Z coordinate
     */
    public static LocationClass forLevel(net.minecraft.server.level.ServerLevel level,
                                         double lx, double ly, double lz) {
        if (level instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
            net.minecraft.world.phys.Vec3 real =
                    cl.realWorldPositionOf(new net.minecraft.world.phys.Vec3(lx, ly, lz));
            net.minecraft.world.level.Level rl = cl.realLevel();
            if (rl instanceof net.minecraft.server.level.ServerLevel rsl) {
                return new LocationClass(rsl, real.x, real.y, real.z);
            }
            // realLevel is not a ServerLevel (shouldn't happen) — keep original level, use projected pos
            return new LocationClass(level, real.x, real.y, real.z);
        }
        return new LocationClass(level, lx, ly, lz);
    }

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "x"       -> PolyValue.of(x);
            case "y"       -> PolyValue.of(y);
            case "z"       -> PolyValue.of(z);
            case "block_x" -> PolyValue.of(Math.floor(x));
            case "block_y" -> PolyValue.of(Math.floor(y));
            case "block_z" -> PolyValue.of(Math.floor(z));
            case "world"   -> level != null
                    ? new PolyValue.Obj(WorldClass.forLevel(level))
                    : PolyValue.NULL;
            case "biome"   -> {
                if (level == null) yield PolyValue.NULL;
                try {
                    net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome =
                            level.getBiome(new net.minecraft.core.BlockPos(
                                    (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)));
                    yield PolyValue.of(biome.unwrapKey()
                            .map(k -> k.toString()).orElse("unknown"));
                } catch (Throwable ignored) { yield PolyValue.NULL; }
            }
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {
            // distance(x, y, z) → Euclidean distance
            case "distance" -> {
                if (args.size() < 3) yield PolyValue.NULL;
                double dx = x - args.get(0).asNum();
                double dy = y - args.get(1).asNum();
                double dz = z - args.get(2).asNum();
                yield PolyValue.of(Math.sqrt(dx * dx + dy * dy + dz * dz));
            }
            // block() / block(x, y, z) → BlockClass at this (or given) position
            case "block" -> {
                if (level == null) yield PolyValue.NULL;
                int bx, by, bz;
                if (args.size() >= 3) {
                    bx = (int) args.get(0).asNum();
                    by = (int) args.get(1).asNum();
                    bz = (int) args.get(2).asNum();
                } else {
                    bx = (int) Math.floor(x);
                    by = (int) Math.floor(y);
                    bz = (int) Math.floor(z);
                }
                yield new PolyValue.Obj(BlockClass.of(level, new net.minecraft.core.BlockPos(bx, by, bz)));
            }
            // distance_sq(x, y, z) → squared distance (no sqrt)
            case "distance_sq" -> {
                if (args.size() < 3) yield PolyValue.NULL;
                double dx = x - args.get(0).asNum();
                double dy = y - args.get(1).asNum();
                double dz = z - args.get(2).asNum();
                yield PolyValue.of(dx * dx + dy * dy + dz * dz);
            }
            default -> get(method);
        };
    }

    /**
     * Factory for creating a LocationClass from an entity's current position and level.
     * Handles contraption sub-levels transparently.
     */
    public static LocationClass forEntity(net.minecraft.world.entity.Entity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            return forLevel(sl, entity.getX(), entity.getY(), entity.getZ());
        }
        return new LocationClass(null, entity.getX(), entity.getY(), entity.getZ());
    }

    // ---- accessors for code that needs the raw values ----

    public net.minecraft.server.level.ServerLevel level() { return level; }
    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
}
