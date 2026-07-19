package dev.arubik.craftengine.chainery;

import org.joml.Vector3d;

/**
 * A verlet particle rope — the real physics of a chain/rope span (CHAINERY phase 3, "al ser físicas si no se
 * estiran al max tendrán físicas o sea caerán al suelo"). {@code segments + 1} point masses linked by
 * unit-length distance constraints, its two ends pinned to the anchor positions each step. Under gravity a
 * slack rope hangs in a catenary and rests on the floor; pulled taut its segments straighten. Pure math with
 * an injected {@link Terrain} probe, so it is unit-testable and can be stepped wherever the anchors are known
 * (the chain's contraption-pull tension is handled separately by {@link ChainPhysics} against PhysicsWorld).
 */
public final class ChainRope {

    /** Solid-block probe (block coords). Injected so the sim never reads the world itself — thread-agnostic. */
    public interface Terrain {
        boolean solid(int x, int y, int z);

        Terrain EMPTY = (x, y, z) -> false;
    }

    /**
     * Collision sub-division: this many verlet particles PER BLOCK of chain. A segment can only tunnel through
     * an obstacle thinner than its length, so finer segments (1/SUBDIV block) stop the rope from crossing a
     * whole block when no full-block particle happens to land inside it (the "casi cualquier traspaso" fix).
     * Render still draws one link per block by striding SUBDIV — see {@link #renderStride()}.
     */
    public static final int SUBDIV = 2;

    private double seg = 1.0; // current segment length (1/SUBDIV block)

    private Vector3d[] pos;
    private Vector3d[] prev;
    private int particles;

    /** Particles-per-block stride the renderer samples so it draws one link per block, not one per sub-segment. */
    public int renderStride() {
        return SUBDIV;
    }

    /** Rebuilds to {@code segments} blocks of chain, sub-divided SUBDIV× for collision (first step / length change). */
    private void reset(Vector3d a, Vector3d b, int segments) {
        particles = Math.max(2, segments * SUBDIV + 1);
        seg = 1.0 / SUBDIV;
        pos = new Vector3d[particles];
        prev = new Vector3d[particles];
        for (int i = 0; i < particles; i++) {
            double t = (double) i / (particles - 1);
            pos[i] = new Vector3d(a).lerp(b, t);
            prev[i] = new Vector3d(pos[i]);
        }
    }

    /**
     * Advances one tick: verlet-integrate the interior under gravity, pin the ends to {@code a}/{@code b},
     * satisfy the unit-length segment constraints ({@code iterations} passes — more = stiffer/iron), and keep
     * every particle out of solid terrain so a slack rope lands on the floor.
     */
    public void step(Vector3d a, Vector3d b, int segments, int iterations, double gravity, double damping,
            Terrain terrain) {
        if (pos == null || particles != segments * SUBDIV + 1) {
            reset(a, b, segments);
        }
        // Verlet integrate the interior particles (ends are pinned, so skip them).
        for (int i = 1; i < particles - 1; i++) {
            double vx = (pos[i].x - prev[i].x) * damping;
            double vy = (pos[i].y - prev[i].y) * damping;
            double vz = (pos[i].z - prev[i].z) * damping;
            prev[i].set(pos[i]);
            pos[i].add(vx, vy + gravity, vz);
        }
        pos[0].set(a);
        pos[particles - 1].set(b);

        for (int iter = 0; iter < iterations; iter++) {
            for (int s = 0; s < particles - 1; s++) {
                satisfy(s, s + 1);
            }
            // Re-pin the ends after each relaxation pass (they are infinite-mass anchors).
            pos[0].set(a);
            pos[particles - 1].set(b);
            for (int i = 1; i < particles - 1; i++) {
                collide(pos[i], terrain);
            }
        }
    }

    /** Pulls two linked particles back to the unit segment length (both move half — ends get re-pinned after). */
    private void satisfy(int i, int j) {
        double dx = pos[j].x - pos[i].x;
        double dy = pos[j].y - pos[i].y;
        double dz = pos[j].z - pos[i].z;
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d < 1.0e-9) {
            return;
        }
        double diff = (d - seg) / d * 0.5;
        double cx = dx * diff, cy = dy * diff, cz = dz * diff;
        pos[i].add(cx, cy, cz);
        pos[j].sub(cx, cy, cz);
    }

    /**
     * Pushes a particle out of a solid block along the shallowest OPEN face, so a rope rests on floors, drapes
     * over corners/edges, and hangs against walls (the "tensión en esquinas" — a link caught on a block corner
     * is shoved to the nearest air, and the segment constraints then bend the rope around it). Prefers pushing
     * UP (resting on top) when the block above is open, since that is the common floor case.
     */
    private static void collide(Vector3d p, Terrain terrain) {
        int bx = (int) Math.floor(p.x);
        int by = (int) Math.floor(p.y);
        int bz = (int) Math.floor(p.z);
        if (!terrain.solid(bx, by, bz)) {
            return;
        }
        // Penetration depth to each of the block's six faces, only where the neighbour that way is open.
        double up = terrain.solid(bx, by + 1, bz) ? Double.MAX_VALUE : (by + 1.0) - p.y;
        double down = terrain.solid(bx, by - 1, bz) ? Double.MAX_VALUE : p.y - by;
        double east = terrain.solid(bx + 1, by, bz) ? Double.MAX_VALUE : (bx + 1.0) - p.x;
        double west = terrain.solid(bx - 1, by, bz) ? Double.MAX_VALUE : p.x - bx;
        double south = terrain.solid(bx, by, bz + 1) ? Double.MAX_VALUE : (bz + 1.0) - p.z;
        double north = terrain.solid(bx, by, bz - 1) ? Double.MAX_VALUE : p.z - bz;
        // Bias UP slightly so a link on a flat floor rests on top rather than squirting out a side.
        double best = up * 0.999;
        int face = 0; // 0=up,1=down,2=east,3=west,4=south,5=north
        if (down < best) { best = down; face = 1; }
        if (east < best) { best = east; face = 2; }
        if (west < best) { best = west; face = 3; }
        if (south < best) { best = south; face = 4; }
        if (north < best) { best = north; face = 5; }
        switch (face) {
            case 1 -> p.y = by - 1.0e-3;
            case 2 -> p.x = bx + 1.0;
            case 3 -> p.x = bx - 1.0e-3;
            case 4 -> p.z = bz + 1.0;
            case 5 -> p.z = bz - 1.0e-3;
            default -> p.y = by + 1.0; // rest on top
        }
    }

    /** Number of particles (segments + 1); 0 until first stepped. */
    public int particleCount() {
        return pos == null ? 0 : particles;
    }

    /** World position of particle {@code i} (0..particleCount-1). Returned instance is live — copy if kept. */
    public Vector3d particle(int i) {
        return pos[i];
    }
}
