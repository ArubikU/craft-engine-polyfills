package dev.arubik.craftengine.chainery;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Orchestrates the lifecycle of every {@link Chain} (CHAINERY): creation from the item behavior, ticking the
 * dynamic render, and severing a chain when an endpoint is mined/exploded. The physics coupling (phys/phys
 * rope, bearing torque-cutoff, tension break) plugs into {@link #tickAll()} in a later phase — v1 keeps the
 * span rendered and correctly bound.
 */
public final class ChainEngine {

    private ChainEngine() {
    }

    /** Chains currently mid-sever — guards the recursive block removal (removing the far endpoint fires its
     *  own onRemove, which would re-enter this). */
    private static final Set<UUID> BREAKING = ConcurrentHashMap.newKeySet();

    // ---- creation ----

    /** Creates, registers, binds and renders a chain between two freshly-placed endpoint blocks. */
    public static Chain create(World world, BlockPos a, BlockPos b, org.joml.Vector3d offsetA,
            org.joml.Vector3d offsetB, ChainMaterial mat, int blocks) {
        Chain chain = new Chain(UUID.randomUUID(), world.getUID(), a, b, offsetA, offsetB, mat, blocks,
                new net.minecraft.nbt.CompoundTag());
        ChainRegistry.register(chain);
        Level level = ((CraftWorld) world).getHandle();
        bindEndpoint(level, a, chain.id, 0);
        bindEndpoint(level, b, chain.id, 1);
        return chain; // the rope is stepped + rendered on the next tick (see tickAll)
    }

    private static void bindEndpoint(Level level, BlockPos pos, UUID chainId, int role) {
        ChainBlockEntity be = ChaineryBlockBehavior.getAt(level, pos);
        if (be != null) {
            be.bind(chainId, role);
        }
    }

    // ---- removing a link (with the tension safety math) ----

    public enum RemoveResult {
        REMOVED, WOULD_BREAK, AT_MIN
    }

    /**
     * Removes one link from {@code chain} unless doing so would over-tension it. Shortening the rope past the
     * endpoints' current span stretches it; the resulting pull is {@code pull·STIFFNESS·overshoot}, and the
     * chain snaps once that exceeds {@code maxTension}. So the minimum safe length keeps
     * {@code span − (blocks−1)·(1+stretch)} within {@code maxTension / (pull·STIFFNESS)} — if the next removal
     * would cross that, this refuses and reports {@link RemoveResult#WOULD_BREAK}.
     */
    public static RemoveResult tryRemoveLink(Chain chain) {
        if (chain.blocks <= 1) {
            return RemoveResult.AT_MIN;
        }
        double span = currentSpan(chain);
        double newEffectiveMax = (chain.blocks - 1) * (1.0 + chain.material.stretch());
        double overshoot = span - newEffectiveMax;
        double maxOvershoot = chain.material.maxTension() <= 0.0 ? Double.MAX_VALUE
                : chain.material.maxTension() / Math.max(1.0e-6, chain.material.pull() * ROPE_STIFFNESS);
        if (overshoot > maxOvershoot) {
            return RemoveResult.WOULD_BREAK;
        }
        chain.blocks--;
        return RemoveResult.REMOVED;
    }

    /** Current straight span between the chain's live endpoints (rope ends if stepped, else block distance). */
    private static double currentSpan(Chain chain) {
        int n = chain.rope.particleCount();
        if (n >= 2) {
            return chain.rope.particle(0).distance(chain.rope.particle(n - 1));
        }
        return Math.sqrt(chain.a.distSqr(chain.b));
    }

    // ---- breaking ----

    /** Entry point from {@link ChainBlockEntity#onRemove()} — a real break at one end severs the whole span. */
    public static void onEndpointBroken(UUID chainId) {
        Chain chain = ChainRegistry.get(chainId);
        if (chain != null) {
            breakChain(chain, true);
        }
    }

    /**
     * Severs {@code chain}: drops its items, removes both endpoint blocks and its render, and unregisters it.
     * Re-entrant-safe (removing the far endpoint block triggers another onRemove for the same chain).
     */
    public static void breakChain(Chain chain, boolean dropItems) {
        if (!BREAKING.add(chain.id)) {
            return; // already being severed by the far-endpoint removal
        }
        try {
            ChainRegistry.remove(chain.id);
            World world = Bukkit.getWorld(chain.worldId);
            if (world == null) {
                return;
            }
            ChainRenderer.despawn(chain, world);
            Level level = ((CraftWorld) world).getHandle();
            // Real-world static anchors at their original cells.
            removeEndpointBlock(world, level, chain.a);
            removeEndpointBlock(world, level, chain.b);
            // Anchors that have been CAPTURED into a contraption live in the hologram, not at chain.a/chain.b —
            // remove those from their contraption too, so a tension-snap clears the anchors everywhere (user:
            // "al romper el chain por tensión no se desaparecen los anchors en el contraption y vida real").
            removeCapturedAnchors(chain.id);
            if (dropItems) {
                dropChainItems(world, chain);
            }
        } finally {
            BREAKING.remove(chain.id);
        }
    }

    /** Sets an endpoint cell to air — but only if it's a chain block AND no OTHER chain still anchors there. */
    private static void removeEndpointBlock(World world, Level level, BlockPos pos) {
        if (ChainRegistry.countAt(world.getUID(), pos) > 0) {
            return; // a shared anchor still hosts another chain — keep it
        }
        if (ChaineryBlockBehavior.getAt(level, pos) == null) {
            return; // already gone (the mined endpoint, or an unloaded chunk)
        }
        world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).setType(Material.AIR, false);
    }

    /** Removes any anchor cell bound to {@code chainId} from whatever contraption currently holds it. */
    private static void removeCapturedAnchors(java.util.UUID chainId) {
        int flags = net.minecraft.world.level.block.Block.UPDATE_CLIENTS
                | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE;
        for (dev.arubik.craftengine.contraption.ContraptionEntity entity :
                dev.arubik.craftengine.contraption.ContraptionManager.all()) {
            try {
                dev.arubik.craftengine.contraption.level.ContraptionLevel level = entity.state().level();
                boolean changed = false;
                for (BlockPos local : new java.util.HashSet<>(level.localPositions())) {
                    net.momirealms.craftengine.core.block.entity.BlockEntity be =
                            dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded((Level) level, local);
                    if (be != null && be.controller instanceof ChainBlockEntity cbe
                            && chainId.equals(cbe.getChainId())) {
                        level.setBlock(local, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), flags);
                        changed = true;
                    }
                }
                if (changed) {
                    level.markCellsDirty();
                    level.refreshLocalPositions();
                }
            } catch (Throwable ignored) {
                // one contraption failing shouldn't block the rest of the sever
            }
        }
    }

    private static void dropChainItems(World world, Chain chain) {
        try {
            if (chain.blocks <= 0) {
                return;
            }
            int remaining = chain.blocks;
            Location mid = new Location(world,
                    (chain.a.getX() + chain.b.getX()) / 2.0 + 0.5,
                    (chain.a.getY() + chain.b.getY()) / 2.0 + 0.5,
                    (chain.a.getZ() + chain.b.getZ()) / 2.0 + 0.5);
            while (remaining > 0) {
                ItemStack drop = linkItemStack(chain.material.linkItem());
                if (drop == null) {
                    return;
                }
                drop.setAmount(Math.min(remaining, drop.getMaxStackSize()));
                world.dropItemNaturally(mid, drop);
                remaining -= drop.getAmount();
            }
        } catch (Throwable ignored) {
        }
    }

    /** Builds one link item — a CraftEngine item if {@code id} is one, else a vanilla item by material key.
     *  Public so the renderer builds its display model from the SAME configured id (any CE or vanilla id). */
    public static ItemStack linkItemStack(String id) {
        var def = CraftEngineItems.byId(Key.of(id));
        if (def != null) {
            return def.buildBukkitItem();
        }
        org.bukkit.NamespacedKey mk = org.bukkit.NamespacedKey.fromString(id);
        org.bukkit.Material m = mk == null ? null : org.bukkit.Registry.MATERIAL.get(mk);
        return m == null ? null : new ItemStack(m);
    }

    // ---- per-tick render + rope physics ----

    /** A resolved live endpoint: its world position and, if it rides a contraption, that contraption. */
    private record Live(org.joml.Vector3d pos, java.util.UUID contraptionId,
            dev.arubik.craftengine.contraption.ContraptionState state) {
    }

    /** Spring stiffness of the tether pull (impulse per block of overshoot). Applied via PhysicsWorld#applyThrust,
     *  which scales it by the body's inverse mass — so a heavier contraption is pulled less, as expected. */
    private static final double ROPE_STIFFNESS = 6.0;

    // Verlet rope step params (see ChainRope): gravity/tick, velocity retention, relaxation passes (stiffness).
    private static final double ROPE_GRAVITY = -0.04;
    private static final double ROPE_DAMPING = 0.98;
    private static final int ROPE_ITERATIONS = 32; // more passes for the SUBDIV-finer particle count
    /** Over-tension must persist this many ticks before a chain snaps — the assembly-spike margin. */
    private static final int BREAK_GRACE_TICKS = 30;

    /**
     * Once per tick: resolve every chain's two endpoints to their LIVE world positions — a captured endpoint
     * follows its contraption — then (a) render the span onto them and (b) if the chain is stretched past its
     * span, pull the tethered contraption(s) back, snapping the chain if the pull exceeds its max tension.
     */
    /** One tick's scan of every contraption: captured chain endpoints + the world cells the contraptions fill. */
    private record Scan(java.util.Map<java.util.UUID, Live[]> endpoints,
            java.util.Map<java.util.UUID, java.util.Set<Long>> occupancyByWorld) {
    }

    public static void tickAll() {
        // One pass over every contraption: locate captured chain endpoints AND collect the world cells each
        // contraption currently fills, so the rope can collide with moving contraption geometry too.
        Scan scan = scanContraptions();

        for (Chain chain : ChainRegistry.all()) {
            try {
                World world = Bukkit.getWorld(chain.worldId);
                if (world == null) {
                    continue;
                }
                Live[] ends = scan.endpoints().get(chain.id);
                Live a = resolveEnd(world, chain, chain.a, ends, 0);
                Live b = resolveEnd(world, chain, chain.b, ends, 1);
                if (a == null || b == null) {
                    continue; // an endpoint is in an unloaded chunk / not resolvable this tick
                }
                if (applyRope(chain, a, b)) {
                    continue; // the chain snapped this tick — it's already gone
                }
                // Real rope physics: step the verlet chain between the live anchors against the world's terrain
                // AND the contraption cells (main thread here, so block reads are safe), then draw the links from
                // its particles — a slack chain sags, rests on the floor, and drapes over contraption edges
                // (tensión en esquinas), a taut one straightens.
                ChainRope.Terrain terrain = terrainFor(world, scan.occupancyByWorld().get(chain.worldId));
                chain.rope.step(a.pos(), b.pos(), chain.blocks, ROPE_ITERATIONS, ROPE_GRAVITY, ROPE_DAMPING, terrain);
                ChainRenderer.syncRope(chain, world, chain.rope);
            } catch (Throwable ignored) {
                // one bad chain shouldn't stall the rest
            }
        }
    }

    /**
     * A neighbour of an anchor at {@code pos} changed — sever any chain there whose support block is now gone.
     * Called from {@link ChaineryBlockBehavior#neighborChanged} (event-driven, replaces the per-tick poll).
     */
    public static void onAnchorNeighborChanged(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        org.bukkit.World world = level.getWorld();
        java.util.UUID worldId = world.getUID();
        for (Chain chain : ChainRegistry.chainsAt(worldId, pos)) {
            org.joml.Vector3d off = chain.a.equals(pos) ? chain.offsetA : chain.offsetB;
            if (supportGone(world, pos, off)) {
                breakChain(chain, true);
            }
        }
    }

    /**
     * Whether the block a static anchor is stuck to has been removed. The offset points from the anchor cell
     * toward the support block's surface, so the support is the neighbour in the offset's dominant direction.
     * Returns false when there's no offset (centre attach), or the support chunk isn't loaded (unknown → keep).
     */
    private static boolean supportGone(World world, BlockPos anchor, org.joml.Vector3d off) {
        if (off == null) {
            return false;
        }
        double ax = Math.abs(off.x), ay = Math.abs(off.y), az = Math.abs(off.z);
        if (ax < 0.1 && ay < 0.1 && az < 0.1) {
            return false; // attaches at the centre — no single support block
        }
        int sx = anchor.getX(), sy = anchor.getY(), sz = anchor.getZ();
        if (ay >= ax && ay >= az) {
            sy += off.y > 0 ? 1 : -1;
        } else if (ax >= az) {
            sx += off.x > 0 ? 1 : -1;
        } else {
            sz += off.z > 0 ? 1 : -1;
        }
        if (!world.isChunkLoaded(sx >> 4, sz >> 4)) {
            return false; // don't sever on unknown terrain
        }
        return !world.getBlockAt(sx, sy, sz).getType().isSolid();
    }

    /**
     * A solid-block probe over a live Bukkit world (main thread only), plus the contraption cells occupying it
     * this tick (so the rope collides with moving contraption surfaces). Unloaded chunks read as non-solid.
     */
    private static ChainRope.Terrain terrainFor(World world, java.util.Set<Long> contraptionCells) {
        return (x, y, z) -> {
            if (contraptionCells != null && contraptionCells.contains(net.minecraft.core.BlockPos.asLong(x, y, z))) {
                return true;
            }
            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                return false;
            }
            return world.getBlockAt(x, y, z).getType().isSolid();
        };
    }

    /** Resolves one endpoint: prefer its captured (moving) position; else the static anchor's attach FACE. */
    private static Live resolveEnd(World world, Chain chain, BlockPos pos, Live[] captured, int role) {
        if (captured != null && captured[role] != null) {
            return captured[role];
        }
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return null;
        }
        // Attach at the offset point on the connected block's real hitbox, not the anchor centre.
        org.joml.Vector3d p = new org.joml.Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        org.joml.Vector3d off = role == 0 ? chain.offsetA : chain.offsetB;
        if (off != null) {
            p.add(off);
        }
        return new Live(p, null, null);
    }

    /** Marks every world block a cell at world-centre {@code w} with the given {@code scale} covers. */
    private static void rasterizeCell(java.util.Set<Long> cells, net.minecraft.world.phys.Vec3 w, double scale) {
        double r = 0.5 * Math.max(1.0, scale);
        int lox = (int) Math.floor(w.x - r + 1.0e-6), hix = (int) Math.floor(w.x + r - 1.0e-6);
        int loy = (int) Math.floor(w.y - r + 1.0e-6), hiy = (int) Math.floor(w.y + r - 1.0e-6);
        int loz = (int) Math.floor(w.z - r + 1.0e-6), hiz = (int) Math.floor(w.z + r - 1.0e-6);
        for (int x = lox; x <= hix; x++) {
            for (int y = loy; y <= hiy; y++) {
                for (int z = loz; z <= hiz; z++) {
                    cells.add(net.minecraft.core.BlockPos.asLong(x, y, z));
                }
            }
        }
    }

    /** Scans every contraption once: captured chain endpoints + per-world occupancy of contraption cells. */
    private static Scan scanContraptions() {
        java.util.Map<java.util.UUID, Live[]> endpoints = new java.util.HashMap<>();
        java.util.Map<java.util.UUID, java.util.Set<Long>> occupancy = new java.util.HashMap<>();
        for (dev.arubik.craftengine.contraption.ContraptionEntity entity :
                dev.arubik.craftengine.contraption.ContraptionManager.all()) {
            try {
                dev.arubik.craftengine.contraption.level.ContraptionLevel level = entity.state().level();
                java.util.UUID cid = entity.state().id();
                java.util.UUID worldId = entity.state().worldId();
                double scale = entity.state().scale();
                java.util.Set<Long> cells = occupancy.computeIfAbsent(worldId, w -> new java.util.HashSet<>());
                for (BlockPos local : level.localPositions()) {
                    net.minecraft.world.phys.Vec3 w = level.realWorldPositionOf(local);
                    // Occupancy: EVERY world block this cell fills — its scaled footprint (radius = scale/2), so a
                    // scaled-up contraption blocks the rope across its whole size, not just one centre block.
                    rasterizeCell(cells, w, scale);
                    net.momirealms.craftengine.core.block.entity.BlockEntity be =
                            dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded((Level) level, local);
                    if (be == null || !(be.controller instanceof ChainBlockEntity cbe)) {
                        continue;
                    }
                    java.util.UUID chainId = cbe.getChainId();
                    if (chainId == null) {
                        continue;
                    }
                    // Attach at the hitbox offset, rotated with the contraption so it tracks the surface as it moves.
                    org.joml.Vector3d p = new org.joml.Vector3d(w.x, w.y, w.z);
                    Chain chain = ChainRegistry.get(chainId);
                    org.joml.Vector3d off = chain == null ? null
                            : (cbe.getRole() == 0 ? chain.offsetA : chain.offsetB);
                    if (off != null) {
                        net.minecraft.world.phys.Vec3 rd = level.rotateToRealWorld(
                                new net.minecraft.world.phys.Vec3(off.x, off.y, off.z));
                        p.add(rd.x, rd.y, rd.z);
                    }
                    Live live = new Live(p, cid, entity.state());
                    endpoints.computeIfAbsent(chainId, k -> new Live[2])[cbe.getRole() == 0 ? 0 : 1] = live;
                }
            } catch (Throwable ignored) {
                // a single misbehaving contraption shouldn't abort the whole scan
            }
        }
        return new Scan(endpoints, occupancy);
    }

    /**
     * Applies the rope constraint for one chain and returns true if it SNAPPED (and was removed) this tick.
     * Slack chains and chains between two static blocks do nothing. A stretched chain pulls its contraption
     * endpoint(s) back toward the span with an impulse via {@code PhysicsWorld.applyThrust} (a no-op on a
     * non-phys or static endpoint), and snaps if that pull exceeds {@link ChainMaterial#maxTension()}.
     */
    private static boolean applyRope(Chain chain, Live a, Live b) {
        // Nothing to pull if neither end rides a (phys) contraption.
        if (a.contraptionId() == null && b.contraptionId() == null) {
            return false;
        }
        org.joml.Vector3d axis = new org.joml.Vector3d(b.pos()).sub(a.pos());
        double dist = axis.length();
        if (dist < 1.0e-6) {
            return false;
        }
        ChainPhysics.RopeResult r = ChainPhysics.resolve(dist, chain.blocks, chain.material.stretch(),
                0.0 /* position-only; the solver's own damping supplies the velocity term */,
                1.0 /* unit — PhysicsWorld scales by real inverse mass */,
                chain.material.pull() * ROPE_STIFFNESS, chain.material.maxTension(), 1.0);
        if (r.impulse() <= 0.0) {
            return false;
        }
        if (r.broke()) {
            // MARGIN: don't snap on a one-tick spike (e.g. the instant a side is assembled into a phys body and
            // the solver momentarily over-stretches the taut chain). Only break after the over-tension SUSTAINS.
            if (++chain.overTensionTicks >= BREAK_GRACE_TICKS) {
                breakChain(chain, true);
                return true;
            }
        } else {
            chain.overTensionTicks = 0;
        }
        axis.div(dist); // unit A->B
        org.joml.Vector3d impulse = new org.joml.Vector3d(axis).mul(r.impulse());
        // Pull A toward B (+axis) and B toward A (-axis).
        handleEnd(a, impulse);
        handleEnd(b, new org.joml.Vector3d(impulse).negate());
        return false;
    }

    /**
     * Applies the taut-chain response to one endpoint by contraption type:
     * <ul>
     *   <li>PHYS — a real impulse at the endpoint (via {@code applyThrust}), so the rope yanks the body back
     *       and, off-centre, torques it — the phys/phys rope and the phys/block tether.</li>
     *   <li>LINEAR / ROTATIONAL — these bearings are kinematic (no rigid body, so an impulse is a no-op); the
     *       chain running out instead STALLS the bearing, i.e. "el torque debe ponerse en 0" — the block
     *       entity telling its contraption to stop this tick.</li>
     * </ul>
     */
    private static void handleEnd(Live end, org.joml.Vector3d impulse) {
        if (end.state() == null) {
            return; // a static block anchor — nothing to move
        }
        dev.arubik.craftengine.contraption.BearingType type = end.state().bearingType();
        if (type == dev.arubik.craftengine.contraption.BearingType.PHYS) {
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.applyThrust(end.contraptionId(), end.pos(), impulse);
        } else if (type == dev.arubik.craftengine.contraption.BearingType.LINEAR
                || type == dev.arubik.craftengine.contraption.BearingType.ROTATIONAL) {
            end.state().setStalled(true); // chain maxed -> cut the bearing's torque
        }
    }
}
