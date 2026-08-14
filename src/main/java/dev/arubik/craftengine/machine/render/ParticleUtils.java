package dev.arubik.craftengine.machine.render;

import org.bukkit.Particle;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Bedrock-inspired particle emitter utilities for the machine renderer system.
 *
 * <p>Bukkit's {@code World#spawnParticle} API allows a single spawn point with a
 * symmetric random-spread radius. Bedrock's particle engine supports geometric
 * <em>emitter shapes</em> (sphere, disc, box, cylinder…) and explicit
 * <em>direction modes</em> (outward, inward, tangent, custom axis…). This class
 * provides equivalent server-side equivalents that compute per-particle world
 * positions and velocity vectors before delegating to vanilla spawning.</p>
 *
 * <h2>Coordinate convention</h2>
 * <p>{@code (cx, cy, cz)} is the emitter centre in world space. {@code (rx, ry, rz)}
 * are per-axis half-extents / radii (meaning varies by shape — see {@link Shape}).</p>
 *
 * <h2>Typical usage from a machine renderer</h2>
 * <pre>{@code
 * ParticleUtils.emit(
 *     world, Particle.FLAME,
 *     x + 0.5, y + 1.2, z + 0.5,  // emitter centre
 *     0.3, 0.1, 0.3,               // half-extents (rx, ry, rz)
 *     ParticleUtils.Shape.DISC,
 *     ParticleUtils.Direction.OUTWARD,
 *     5, 0.05,
 *     0, 0, 0                       // custom dir (unused here)
 * );
 * }</pre>
 */
public final class ParticleUtils {

    private ParticleUtils() {}

    // =========================================================================
    // Emitter shape
    // =========================================================================

    /**
     * Geometric region from which particles are spawned.
     *
     * <table>
     *   <tr><th>POINT</th><td>Single point — identical to vanilla.</td></tr>
     *   <tr><th>SPHERE</th><td>Uniform volume inside a sphere of radius {@code rx}.</td></tr>
     *   <tr><th>SPHERE_SURFACE</th><td>Uniform points on a sphere surface of radius {@code rx}.</td></tr>
     *   <tr><th>HEMISPHERE</th><td>Upper hemisphere (+Y) surface, radius {@code rx}.</td></tr>
     *   <tr><th>DISC</th><td>Uniform disc in the XZ plane, radius {@code rx}.</td></tr>
     *   <tr><th>DISC_EDGE</th><td>Perimeter of an XZ disc, radius {@code rx}.</td></tr>
     *   <tr><th>BOX</th><td>Uniform volume inside an AABB of half-extents {@code (rx,ry,rz)}.</td></tr>
     *   <tr><th>BOX_SURFACE</th><td>Uniform points on an AABB surface, half-extents {@code (rx,ry,rz)}.</td></tr>
     *   <tr><th>CYLINDER</th><td>Uniform volume inside a cylinder, radius {@code rx}, half-height {@code ry}.</td></tr>
     *   <tr><th>CYLINDER_SURFACE</th><td>Curved lateral surface of a cylinder, radius {@code rx}, half-height {@code ry}.</td></tr>
     * </table>
     */
    public enum Shape {
        POINT,
        SPHERE,
        SPHERE_SURFACE,
        HEMISPHERE,
        DISC,
        DISC_EDGE,
        BOX,
        BOX_SURFACE,
        CYLINDER,
        CYLINDER_SURFACE;

        /**
         * Case-insensitive lookup with a fallback default.
         *
         * @param name     JSON string value (e.g. {@code "sphere_surface"}).
         * @param fallback value returned when {@code name} is null or unrecognised.
         */
        public static Shape fromName(String name, Shape fallback) {
            if (name == null) return fallback;
            try {
                return Shape.valueOf(name.toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return fallback;
            }
        }
    }

    // =========================================================================
    // Direction mode
    // =========================================================================

    /**
     * Determines the velocity direction assigned to each spawned particle.
     *
     * <table>
     *   <tr><th>RANDOM</th><td>Vanilla random spread using Bukkit's offset parameters.</td></tr>
     *   <tr><th>OUTWARD</th><td>Away from the emitter centre along the spawn-point offset.</td></tr>
     *   <tr><th>INWARD</th><td>Toward the emitter centre.</td></tr>
     *   <tr><th>UP</th><td>Fixed +Y axis.</td></tr>
     *   <tr><th>DOWN</th><td>Fixed −Y axis.</td></tr>
     *   <tr><th>TANGENT</th><td>
     *       Tangent to the emitter surface in the XZ plane (perpendicular to radial
     *       direction). Gives a swirl effect on DISC / CYLINDER shapes.
     *   </td></tr>
     *   <tr><th>CUSTOM</th><td>Explicit {@code (dvx,dvy,dvz)} vector (normalised to {@code speed}).</td></tr>
     * </table>
     */
    public enum Direction {
        RANDOM,
        OUTWARD,
        INWARD,
        UP,
        DOWN,
        TANGENT,
        CUSTOM;

        /**
         * Case-insensitive lookup with a fallback default.
         *
         * @param name     JSON string value (e.g. {@code "outward"}).
         * @param fallback value returned when {@code name} is null or unrecognised.
         */
        public static Direction fromName(String name, Direction fallback) {
            if (name == null) return fallback;
            try {
                return Direction.valueOf(name.toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return fallback;
            }
        }
    }

    // =========================================================================
    // Main emit entry point
    // =========================================================================

    /**
     * Emits {@code count} particles using the given shape and direction mode.
     *
     * <p>Each particle is assigned an individual world position (sampled from
     * {@code shape}) and a velocity vector (derived from {@code direction}).
     * For {@link Direction#RANDOM} the vanilla spread path is used so the
     * Bukkit RNG handles scattering; every other mode uses count=0 with an
     * explicit velocity so the particle travels in a predictable direction.</p>
     *
     * @param world     target Bukkit world
     * @param particle  Bukkit particle type
     * @param cx        emitter centre X
     * @param cy        emitter centre Y
     * @param cz        emitter centre Z
     * @param rx        X half-extent / radius (see {@link Shape} per-shape semantics)
     * @param ry        Y half-extent / half-height
     * @param rz        Z half-extent (unused for symmetric shapes)
     * @param shape     emitter shape
     * @param direction direction mode
     * @param count     number of particles to emit this call
     * @param speed     particle speed (magnitude of velocity vector)
     * @param dvx       custom direction X (only used when {@code direction == CUSTOM})
     * @param dvy       custom direction Y
     * @param dvz       custom direction Z
     */
    public static void emit(
            World world, Particle particle,
            double cx, double cy, double cz,
            double rx, double ry, double rz,
            Shape shape, Direction direction,
            int count, double speed,
            double dvx, double dvy, double dvz) {

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if (shape == Shape.POINT && direction == Direction.RANDOM) {
            // Fast path: vanilla semantics, no per-particle loop needed
            world.spawnParticle(particle, cx, cy, cz, count,
                    rx, ry, rz, speed);
            return;
        }

        for (int k = 0; k < count; k++) {
            double[] pos = sampleShape(rng, shape, rx, ry, rz);
            double lx = pos[0], ly = pos[1], lz = pos[2];
            double wx = cx + lx, wy = cy + ly, wz = cz + lz;
            double[] vel = computeDirection(rng, direction, lx, ly, lz, dvx, dvy, dvz, speed);
            if (direction == Direction.RANDOM) {
                // Use Bukkit spread (rx/ry/rz) with its random — vel is [0,0,0]
                world.spawnParticle(particle, wx, wy, wz, 0, rx, ry, rz, speed);
            } else {
                world.spawnParticle(particle, wx, wy, wz, 0,
                        vel[0], vel[1], vel[2], speed);
            }
        }
    }

    /**
     * Convenience overload matching vanilla behaviour exactly (POINT + RANDOM).
     * Delegates to Bukkit's native multi-count path, which is cheaper than looping.
     */
    public static void emitRandom(World world, Particle particle,
            double cx, double cy, double cz,
            int count, double sx, double sy, double sz, double speed) {
        world.spawnParticle(particle, cx, cy, cz, count, sx, sy, sz, speed);
    }

    // =========================================================================
    // Shape samplers
    // =========================================================================

    /**
     * Returns a local-space offset {@code [lx, ly, lz]} from the emitter centre
     * sampled uniformly from the requested shape.
     *
     * <p>This is exposed as a public method so callers that want fine-grained
     * control (e.g. custom velocity computation per particle) can reuse the
     * sampling logic without going through {@link #emit}.</p>
     *
     * @param rng  caller-provided RNG (typically {@link ThreadLocalRandom#current()})
     * @param shape  shape to sample from
     * @param rx  X half-extent / radius
     * @param ry  Y half-extent / half-height
     * @param rz  Z half-extent
     * @return {@code double[3]} local offset {@code {lx, ly, lz}}
     */
    public static double[] sampleShape(ThreadLocalRandom rng, Shape shape,
            double rx, double ry, double rz) {
        return switch (shape) {
            case POINT -> new double[]{0, 0, 0};

            case SPHERE -> {
                // Rejection sampling inside a unit cube until the point falls inside the sphere
                double x, y, z;
                do {
                    x = rng.nextDouble(-1.0, 1.0);
                    y = rng.nextDouble(-1.0, 1.0);
                    z = rng.nextDouble(-1.0, 1.0);
                } while (x * x + y * y + z * z > 1.0);
                yield new double[]{x * rx, y * rx, z * rx};
            }

            case SPHERE_SURFACE -> {
                // Marsaglia (1972): project 3 independent Gaussians onto the unit sphere
                double u, v, w, len;
                do {
                    u = rng.nextGaussian();
                    v = rng.nextGaussian();
                    w = rng.nextGaussian();
                    len = Math.sqrt(u * u + v * v + w * w);
                } while (len < 1e-9);
                yield new double[]{u / len * rx, v / len * rx, w / len * rx};
            }

            case HEMISPHERE -> {
                // Upper hemisphere only: sample sphere surface then flip Y if negative
                double u, v, w, len;
                do {
                    u = rng.nextGaussian();
                    v = rng.nextGaussian();
                    w = rng.nextGaussian();
                    len = Math.sqrt(u * u + v * v + w * w);
                } while (len < 1e-9);
                yield new double[]{u / len * rx, Math.abs(v / len) * rx, w / len * rx};
            }

            case DISC -> {
                // Uniform disc via sqrt-radius trick (avoids centre clustering)
                double r = rx * Math.sqrt(rng.nextDouble());
                double theta = rng.nextDouble(0, 2 * Math.PI);
                yield new double[]{Math.cos(theta) * r, 0, Math.sin(theta) * r};
            }

            case DISC_EDGE -> {
                double theta = rng.nextDouble(0, 2 * Math.PI);
                yield new double[]{Math.cos(theta) * rx, 0, Math.sin(theta) * rx};
            }

            case BOX -> new double[]{
                    rng.nextDouble(-rx, rx),
                    rng.nextDouble(-ry, ry),
                    rng.nextDouble(-rz, rz)
            };

            case BOX_SURFACE -> {
                // Pick one of 6 faces weighted by face area, then sample a uniform point on it
                double aX = ry * rz, aY = rx * rz, aZ = rx * ry;
                double total = 2.0 * (aX + aY + aZ);
                double pick = rng.nextDouble(total);
                double bx, by, bz;
                if (pick < 2.0 * aX) {
                    bx = (pick < aX) ? -rx : rx;
                    by = rng.nextDouble(-ry, ry);
                    bz = rng.nextDouble(-rz, rz);
                } else if (pick < 2.0 * (aX + aY)) {
                    bx = rng.nextDouble(-rx, rx);
                    by = (pick < 2.0 * aX + aY) ? -ry : ry;
                    bz = rng.nextDouble(-rz, rz);
                } else {
                    bx = rng.nextDouble(-rx, rx);
                    by = rng.nextDouble(-ry, ry);
                    bz = (pick < 2.0 * (aX + aY) + aZ) ? -rz : rz;
                }
                yield new double[]{bx, by, bz};
            }

            case CYLINDER -> {
                double r = rx * Math.sqrt(rng.nextDouble());
                double theta = rng.nextDouble(0, 2 * Math.PI);
                double h = rng.nextDouble(-ry, ry);
                yield new double[]{Math.cos(theta) * r, h, Math.sin(theta) * r};
            }

            case CYLINDER_SURFACE -> {
                double theta = rng.nextDouble(0, 2 * Math.PI);
                double h = rng.nextDouble(-ry, ry);
                yield new double[]{Math.cos(theta) * rx, h, Math.sin(theta) * rx};
            }
        };
    }

    // =========================================================================
    // Direction / velocity computation
    // =========================================================================

    /**
     * Returns a velocity vector {@code [vx, vy, vz]} for a particle spawned at the
     * given local offset from the emitter centre.
     *
     * <p>For {@link Direction#RANDOM} the returned vector is {@code [0,0,0]}; the
     * caller should use Bukkit's offset parameters to produce randomness instead.</p>
     *
     * @param rng       RNG (used for TANGENT on degenerate inputs)
     * @param direction direction mode
     * @param lx        local spawn offset X from emitter centre
     * @param ly        local spawn offset Y
     * @param lz        local spawn offset Z
     * @param dvx       custom direction X (only used for CUSTOM)
     * @param dvy       custom direction Y
     * @param dvz       custom direction Z
     * @param speed     desired speed (magnitude of the returned vector, unless RANDOM)
     * @return {@code double[3]} velocity vector {@code {vx, vy, vz}}
     */
    public static double[] computeDirection(ThreadLocalRandom rng, Direction direction,
            double lx, double ly, double lz,
            double dvx, double dvy, double dvz,
            double speed) {
        return switch (direction) {
            case RANDOM  -> new double[]{0, 0, 0};
            case OUTWARD -> normalize(lx, ly, lz, speed);
            case INWARD  -> normalize(-lx, -ly, -lz, speed);
            case UP      -> new double[]{0, speed, 0};
            case DOWN    -> new double[]{0, -speed, 0};
            case TANGENT -> {
                // XZ-plane tangent: perpendicular to the horizontal radial component.
                // This creates a natural swirl effect for disc/cylinder emitters.
                double radXZ = Math.sqrt(lx * lx + lz * lz);
                if (radXZ < 1e-9) {
                    // Degenerate: point is directly above/below centre — pick random tangent
                    double theta = rng.nextDouble(0, 2 * Math.PI);
                    yield new double[]{Math.cos(theta) * speed, 0, Math.sin(theta) * speed};
                }
                yield new double[]{-lz / radXZ * speed, 0, lx / radXZ * speed};
            }
            case CUSTOM  -> normalize(dvx, dvy, dvz, speed);
        };
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    /**
     * Returns {@code (x,y,z)} scaled so its length equals {@code scale}.
     * Falls back to {@code (0, scale, 0)} when the input vector is near-zero.
     */
    public static double[] normalize(double x, double y, double z, double scale) {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1e-9) return new double[]{0, scale, 0};
        return new double[]{x / len * scale, y / len * scale, z / len * scale};
    }
}
