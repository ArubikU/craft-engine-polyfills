package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A snapshot of the real world's solid collision boxes near one body, baked once per game tick.
 *
 * <h2>Why bake</h2>
 * The solver runs {@link XpbdSolver#SUB_STEPS} substeps per tick and regenerates contacts on each,
 * so the world would otherwise be queried tens of thousands of times per tick per body. Baking makes
 * the substep loop pure arithmetic against a map.
 *
 * <p>It also makes the solve <b>thread-safe by construction</b>: a bake is a plain value with no
 * reference back to the level, so the substep loop touches no Bukkit/NMS state and can be moved off
 * the main thread without a data race. The previous engine's off-thread attempt failed for a
 * different reason — it computed a clamp against the PREVIOUS tick's origin and applied it at the
 * CURRENT one — which a value snapshot plus a same-tick apply avoids entirely.
 *
 * <h2>Real shapes</h2>
 * Boxes come from {@link BlockState#getCollisionShape}, so world slabs, stairs and fences are
 * collided with as their true geometry rather than as full cubes, matching {@link CollisionShape} on
 * the contraption side. Non-colliding blocks bake to nothing and are simply absent.
 */
public final class WorldBlockCache {

    /**
     * One cell's fluid, for buoyancy.
     *
     * <p>Fluids have no collision shape, so the solid bake below skips them entirely — which is why a
     * phys contraption used to fall through an ocean as though it were air. They are recorded
     * separately rather than as colliders because a fluid does not stop a body; it pushes up on the
     * part of it that is submerged.
     *
     * @param buoyancy how hard this fluid pushes back, compared against a block's floatability —
     *        1 for water, 2 for lava. See {@link FloatabilityTable}
     * @param topY world Y of the fluid's surface in this cell, so a body at the waterline is only
     *        partially buoyant instead of popping to full lift the instant its centre dips in
     * @param flow the fluid's own velocity here, in blocks per tick — vanilla's flow direction scaled
     *        by {@link #FLOW_SPEED}. Still water is {@link Vec3#ZERO}
     */
    public record Fluid(double buoyancy, double topY, boolean lava, Vec3 flow) {
    }

    /**
     * How fast flowing fluid actually moves, in blocks per tick.
     *
     * <p>Vanilla's {@code getFlow} returns a direction, not a speed. Because drag pulls a body toward
     * the fluid's velocity rather than toward zero, this doubles as the terminal drift speed: a raft
     * dropped in a river accelerates until it is moving with the current at roughly this rate, and
     * then stops accelerating on its own. Sized to be a touch brisker than vanilla's own entity push
     * so a heavy contraption still visibly travels.
     */
    public static final double FLOW_SPEED = 0.14;

    /**
     * How hard water pushes back, against a block's {@code floatability}. A cell floats when its
     * floatability is under this and sinks when it is over — see {@link FloatabilityTable}, which owns
     * that number now. Water is the unit: 1.
     */
    public static final double WATER_BUOYANCY = 1.0;

    /**
     * Lava pushes twice as hard as water, so it floats everything water does AND everything with a
     * floatability up to 2 — stone rafts ride on lava, netherite does not. Not coded as a case: it is
     * what {@code (fluidBuoyancy - floatability)} says.
     */
    public static final double LAVA_BUOYANCY = 2.0;

    private final Map<Long, List<AABB>> boxes;
    private final Map<Long, Fluid> fluids;

    private WorldBlockCache(Map<Long, List<AABB>> boxes, Map<Long, Fluid> fluids) {
        this.boxes = boxes;
        this.fluids = fluids;
    }

    /** An empty world — every query misses. Used for pure-JVM tests and unloaded regions. */
    public static final WorldBlockCache EMPTY = new WorldBlockCache(Map.of(), Map.of());

    /**
     * Bakes every solid collision box intersecting {@code region}.
     *
     * <p>{@code region} should be the body's bounding box inflated by at least its per-tick motion
     * plus {@link XpbdSolver#SPECULATIVE_DISTANCE}, so a body cannot move into a block that was never
     * baked. Unloaded chunks are skipped rather than force-loaded — a body over unloaded terrain
     * finds no boxes and falls, which is preferable to a physics tick triggering chunk generation.
     */
    public static WorldBlockCache bake(ServerLevel level, AABB region) {
        if (level == null) {
            return EMPTY;
        }
        Map<Long, List<AABB>> map = new HashMap<>();
        Map<Long, Fluid> fluidMap = new HashMap<>();
        int minX = (int) Math.floor(region.minX), maxX = (int) Math.floor(region.maxX);
        int minY = (int) Math.floor(region.minY), maxY = (int) Math.floor(region.maxY);
        int minZ = (int) Math.floor(region.minZ), maxZ = (int) Math.floor(region.maxZ);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!level.hasChunkAt(x, z)) {
                    continue; // never force-load from the physics path
                }
                for (int y = minY; y <= maxY; y++) {
                    cursor.set(x, y, z);
                    BlockState state = level.getBlockState(cursor);
                    if (state.isAir()) {
                        continue;
                    }
                    // Fluids first, and independently of the collision bake below: a fluid's collision
                    // shape is empty, so it would otherwise be skipped and a body would fall through an
                    // ocean as though it were air. A waterlogged stair is both — a collider AND a fluid.
                    try {
                        net.minecraft.world.level.material.FluidState fluid = state.getFluidState();
                        if (!fluid.isEmpty()) {
                            boolean lava = fluid.is(net.minecraft.tags.FluidTags.LAVA);
                            double height = fluid.getHeight(level, cursor);
                            // Vanilla's own flow field: the direction water actually pushes here,
                            // already accounting for slope, falling water and neighbouring sources.
                            // Zero for a still source block, which is why standing water does not drift.
                            net.minecraft.world.phys.Vec3 flow = fluid.getFlow(level, cursor);
                            fluidMap.put(BlockPos.asLong(x, y, z),
                                    new Fluid(lava ? LAVA_BUOYANCY : WATER_BUOYANCY, y + height, lava,
                                            flow.lengthSqr() < 1.0E-12 ? net.minecraft.world.phys.Vec3.ZERO
                                                    : flow.normalize().scale(FLOW_SPEED)));
                        }
                    } catch (Exception ignored) {
                        // A block whose fluid state needs context we cannot give it simply contributes
                        // no buoyancy; that is a body that sinks, not a crash.
                    }
                    VoxelShape shape;
                    try {
                        shape = state.getCollisionShape(level, cursor);
                    } catch (Exception e) {
                        continue;
                    }
                    if (shape.isEmpty()) {
                        continue;
                    }
                    List<AABB> cell = new ArrayList<>();
                    for (AABB box : shape.toAabbs()) {
                        cell.add(box.move(x, y, z));
                    }
                    if (!cell.isEmpty()) {
                        map.put(BlockPos.asLong(x, y, z), cell);
                    }
                }
            }
        }
        return map.isEmpty() && fluidMap.isEmpty() ? EMPTY : new WorldBlockCache(map, fluidMap);
    }

    /**
     * Builds a cache of full-cube solid blocks filling {@code region}'s block coordinates — a synthetic
     * slab of terrain. Lets the solver be exercised against known ground without a live world.
     */
    public static WorldBlockCache solidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Map<Long, List<AABB>> map = new HashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    map.put(BlockPos.asLong(x, y, z), List.of(new AABB(x, y, z, x + 1, y + 1, z + 1)));
                }
            }
        }
        return map.isEmpty() ? EMPTY : new WorldBlockCache(map, Map.of());
    }

    /**
     * Builds a cache of fluid filling {@code region}'s block coordinates, with its surface at
     * {@code surfaceY} — a synthetic body of water. Lets buoyancy be exercised without a live world.
     */
    public static WorldBlockCache fluidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
            double surfaceY, boolean lava) {
        return fluidRegion(minX, minY, minZ, maxX, maxY, maxZ, surfaceY, lava, Vec3.ZERO);
    }

    /**
     * As {@link #fluidRegion(int, int, int, int, int, int, double, boolean)} but with a current: every
     * cell carries {@code flow} as its fluid velocity, in blocks per tick.
     */
    public static WorldBlockCache fluidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
            double surfaceY, boolean lava, Vec3 flow) {
        Map<Long, Fluid> map = new HashMap<>();
        Fluid fluid = new Fluid(lava ? LAVA_BUOYANCY : WATER_BUOYANCY, surfaceY, lava, flow);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    map.put(BlockPos.asLong(x, y, z), fluid);
                }
            }
        }
        return map.isEmpty() ? EMPTY : new WorldBlockCache(Map.of(), map);
    }

    /** The solid boxes of the block cell at these coordinates, or an empty list. */
    public List<AABB> at(int x, int y, int z) {
        List<AABB> cell = boxes.get(BlockPos.asLong(x, y, z));
        return cell == null ? List.of() : cell;
    }

    /** The fluid filling the cell at these coordinates, or {@code null} if it is not a fluid. */
    public Fluid fluidAt(int x, int y, int z) {
        return fluids.get(BlockPos.asLong(x, y, z));
    }

    /** True when nothing here is a fluid — lets the buoyancy pass skip a dry body outright. */
    public boolean hasNoFluid() {
        return fluids.isEmpty();
    }

    /** True when there is no SOLID geometry. A fluid-only bake is not empty for buoyancy purposes. */
    public boolean isEmpty() {
        return boxes.isEmpty();
    }
}
