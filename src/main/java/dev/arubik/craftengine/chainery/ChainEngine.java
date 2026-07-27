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
        // Mirror the physics break: removing a link is refused if the resulting length would be over-stretched
        // past MAX_STRETCH for the current span (so a 5.4-block diagonal keeps a sensible minimum ≈ span/1.8).
        double span = currentSpan(chain);
        double newEffectiveMax = (chain.blocks - 1) * (1.0 + chain.material.stretch());
        if (chain.material.maxTension() > 0.0 && span > newEffectiveMax * (1.0 + ChainPhysics.MAX_STRETCH)) {
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

    /** Removes EVERY chain (admin cleanup / {@code /cep chainery clear}). Returns how many were removed. */
    public static int clearAll() {
        int n = 0;
        for (Chain chain : ChainRegistry.all()) {
            breakChain(chain, false); // no drops — this is a wipe, not a mine
            n++;
        }
        return n;
    }

    /**
     * A contraption just disassembled — re-anchor every chain endpoint it was carrying to wherever that anchor
     * block actually landed (user: "al hacer disassembly ... las chains no se acomodan solas ... se bugean").
     * The captured {@code chain_anchor} blocks are restored into the world at {@code restingPositions} with their
     * {@link ChainBlockEntity} bindings intact; but the chain's static endpoint still points at the ORIGINAL
     * pre-assembly cell, which is wrong once the contraption moved before it was taken apart. For each restored
     * anchor cell we read its bindings and move the matching chain endpoint onto it, dropping the now-stale
     * contraption capture so the endpoint resolves statically at its true new home.
     */
    public static void onContraptionDisassembled(UUID contraptionId, World world,
            java.util.Set<BlockPos> restingPositions, int quarterTurns) {
        if (world == null || restingPositions == null || restingPositions.isEmpty()) {
            return;
        }
        Level level = ((CraftWorld) world).getHandle();
        for (BlockPos pos : restingPositions) {
            ChainBlockEntity be = ChaineryBlockBehavior.getAt(level, pos);
            if (be == null) {
                continue;
            }
            for (ChainBlockEntity.Binding binding : be.bindings()) {
                Chain chain = ChainRegistry.get(binding.chainId());
                if (chain == null) {
                    continue;
                }
                int role = binding.role();
                BlockPos endpoint = role == 0 ? chain.a : chain.b;
                if (endpoint.equals(pos)) {
                    continue; // already where its block landed — nothing stale to fix
                }
                // Follow the block to its real resting cell and forget the capture, so resolveEnd treats it as a
                // plain static anchor here. KEEP the exact attach offset (user: "al restaurar un chain desde un
                // contraption pierde el punto exacto de soporte y se queda en el centro del bloque") — the support
                // block is restored alongside the anchor, so the face point is still valid; only rotate the offset
                // by the disassembly quarter-turns to match the restored orientation. Clear the velocity sample so
                // there's no re-anchor spike.
                ChainRegistry.reanchor(chain, role, pos);
                if (role == 0) {
                    chain.contraptionA = null;
                    chain.localA = null;
                    chain.offsetA = rotateOffsetYaw(chain.offsetA, quarterTurns);
                    chain.lastEndA = null;
                } else {
                    chain.contraptionB = null;
                    chain.localB = null;
                    chain.offsetB = rotateOffsetYaw(chain.offsetB, quarterTurns);
                    chain.lastEndB = null;
                }
                chain.orphanTicks = 0;
            }
        }
    }

    /** Rotates a local-frame attach offset by {@code quarterTurns} 90° steps around Y — matching
     *  {@code ContraptionCapture.rotateLocal}'s {@code (x,z)->(-z,x)} so the restored offset lines up with the
     *  grid-snapped block. Null-safe (a centre attach stays centre). */
    private static org.joml.Vector3d rotateOffsetYaw(org.joml.Vector3d off, int quarterTurns) {
        if (off == null) {
            return null;
        }
        double x = off.x, z = off.z;
        int turns = ((quarterTurns % 4) + 4) % 4;
        for (int i = 0; i < turns; i++) {
            double nx = -z, nz = x;
            x = nx;
            z = nz;
        }
        return new org.joml.Vector3d(x, off.y, z);
    }

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
            removeEndpointBlock(world, level, chain.a, chain.id);
            removeEndpointBlock(world, level, chain.b, chain.id);
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
    private static void removeEndpointBlock(World world, Level level, BlockPos pos, UUID chainId) {
        // Drop this chain's binding from the anchor block entity first, so a shared junction stops listing the
        // now-severed span (and getChainId/scan never resolve a dead id).
        ChainBlockEntity be = ChaineryBlockBehavior.getAt(level, pos);
        if (be != null) {
            be.unbind(chainId);
        }
        if (ChainRegistry.countAt(world.getUID(), pos) > 0) {
            return; // a shared anchor still hosts another chain — keep it
        }
        if (be == null) {
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
                    if (be != null && be.controller instanceof ChainBlockEntity cbe) {
                        boolean wasBound = cbe.bindings().stream().anyMatch(bd -> chainId.equals(bd.chainId()));
                        if (wasBound && cbe.unbind(chainId) == 0) {
                            // Only air the captured cell once NO other chain still anchors here — a shared junction
                            // keeps its anchor for the chains that remain.
                            level.setBlock(local, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), flags);
                            changed = true;
                        }
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
    /** An orphaned chain (anchor gone, not captured) is only removed after this long — grace for a captured
     *  chain's contraption to reload after a restart before we give up on it (~10s). */
    private static final int ORPHAN_GRACE_TICKS = 200;

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

        // Shared per-tick interaction-density budget (user: "aplica reducción cuando hay varias cerca en 1 mismo
        // lugar ... 32 chains entre 2 bloques => ~100 interactions"). Counts how many INTERACTION hitboxes have
        // been spawned in each world cell this tick, across ALL chains, so ChainRenderer can cap the overlap —
        // 32 stacked chains in one block collapse to a handful of clickable boxes instead of ~100.
        java.util.Map<Long, Integer> interactionBudget = new java.util.HashMap<>();

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
                // Orphan handling: a STATIC (non-captured) endpoint whose anchor block is gone. This is TRUE for a
                // captured chain in the window between restart and its contraption reloading, so DON'T delete right
                // away — grace it: skip rendering while orphaned, and only remove after ORPHAN_GRACE_TICKS (the
                // contraption clearly isn't coming back), which is what finally clears a genuinely dead chain.
                if (isOrphan(world, chain.a, a) || isOrphan(world, chain.b, b)) {
                    if (++chain.orphanTicks >= ORPHAN_GRACE_TICKS) {
                        breakChain(chain, false);
                    }
                    continue; // don't render at the stale position while orphaned
                }
                chain.orphanTicks = 0;
                if (applyRope(chain, a, b)) {
                    continue; // the chain snapped this tick — it's already gone
                }
                // Real rope physics: step the verlet chain between the live anchors against the world's terrain
                // AND the contraption cells (main thread here, so block reads are safe), then draw the links from
                // its particles — a slack chain sags, rests on the floor, and drapes over contraption edges
                // (tensión en esquinas), a taut one straightens.
                ChainRope.Terrain terrain = terrainFor(world, scan.occupancyByWorld().get(chain.worldId));
                chain.rope.step(a.pos(), b.pos(), chain.blocks, ROPE_ITERATIONS, ROPE_GRAVITY, ROPE_DAMPING, terrain);
                ChainRenderer.syncRope(chain, world, chain.rope, interactionBudget);
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
        // A contraption capture removes the support blocks under/beside captured anchors as it pulls the structure
        // in, which fires this exact neighbour-change — but that is assembly, NOT a real support loss, so it must
        // not sever (bug: "si armo un contraption que adentro tiene 2 chain, estas se rompen y sus anchor quedan
        // huérfanos"). Same capture flag ChainBlockEntity#onRemove uses to tell assembly from a genuine mine.
        if (dev.arubik.craftengine.contraption.ContraptionCapture.isRemovingForCapture()) {
            return;
        }
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

    /** True if a STATIC endpoint's anchor block is gone (chunk loaded, no chain block there) — an orphan. A
     *  captured endpoint (rides a contraption) is never an orphan. */
    private static boolean isOrphan(World world, BlockPos pos, Live live) {
        if (live.contraptionId() != null) {
            return false; // captured — its anchor is in the contraption, not at pos
        }
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return false; // unknown terrain — don't remove
        }
        Level nms = ((CraftWorld) world).getHandle();
        return ChaineryBlockBehavior.getAt(nms, pos) == null;
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

    /** Resolves one endpoint: this-tick capture (block entity) → persisted capture (contraption id) → static. */
    private static Live resolveEnd(World world, Chain chain, BlockPos pos, Live[] captured, int role) {
        if (captured != null && captured[role] != null) {
            return captured[role]; // the scan found the anchor's block entity this tick
        }
        // Persisted-capture fallback: reconnect to the recorded contraption even if the block-entity link was lost
        // (a restart), without depending on the block entity at all.
        java.util.UUID cid = role == 0 ? chain.contraptionA : chain.contraptionB;
        BlockPos local = role == 0 ? chain.localA : chain.localB;
        if (cid != null && local != null) {
            dev.arubik.craftengine.contraption.ContraptionEntity ent =
                    dev.arubik.craftengine.contraption.ContraptionManager.get(cid);
            if (ent == null) {
                return null; // contraption not loaded (or gone) — wait; orphan grace decides if it's really gone
            }
            dev.arubik.craftengine.contraption.level.ContraptionLevel lvl = ent.state().level();
            if (!lvl.localPositions().contains(local)) {
                // The anchor left this contraption (disassembled / split) — forget the capture, fall through to static.
                if (role == 0) {
                    chain.contraptionA = null;
                    chain.localA = null;
                } else {
                    chain.contraptionB = null;
                    chain.localB = null;
                }
            } else {
                net.minecraft.world.phys.Vec3 w = lvl.realWorldPositionOf(new net.minecraft.world.phys.Vec3(
                        local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
                org.joml.Vector3d p = new org.joml.Vector3d(w.x, w.y, w.z);
                org.joml.Vector3d off = role == 0 ? chain.offsetA : chain.offsetB;
                if (off != null) {
                    net.minecraft.world.phys.Vec3 rd = lvl.rotateToRealWorld(
                            new net.minecraft.world.phys.Vec3(off.x, off.y, off.z));
                    p.add(rd.x, rd.y, rd.z);
                }
                return new Live(p, cid, ent.state());
            }
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
                    // Use the cell CENTRE (the BlockPos overload transforms the integer CORNER, which drifts the
                    // captured connection point by ~half a cell vs the static path's pos+0.5).
                    net.minecraft.world.phys.Vec3 w = level.realWorldPositionOf(new net.minecraft.world.phys.Vec3(
                            local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
                    // Occupancy: EVERY world block this cell fills — its scaled footprint (radius = scale/2), so a
                    // scaled-up contraption blocks the rope across its whole size, not just one centre block.
                    rasterizeCell(cells, w, scale);
                    net.momirealms.craftengine.core.block.entity.BlockEntity be =
                            dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded((Level) level, local);
                    if (be == null || !(be.controller instanceof ChainBlockEntity cbe)) {
                        continue;
                    }
                    // A junction cell hosts up to 4 chains — resolve EVERY chain bound here, not just the last
                    // one (fix: "solo el ultimo chain funciona ... el resto desaparece al armar el contraption").
                    for (ChainBlockEntity.Binding binding : cbe.bindings()) {
                        java.util.UUID chainId = binding.chainId();
                        int role = binding.role();
                        // Attach at the hitbox offset, rotated with the contraption so it tracks the surface as it moves.
                        org.joml.Vector3d p = new org.joml.Vector3d(w.x, w.y, w.z);
                        Chain chain = ChainRegistry.get(chainId);
                        org.joml.Vector3d off = chain == null ? null
                                : (role == 0 ? chain.offsetA : chain.offsetB);
                        if (off != null) {
                            net.minecraft.world.phys.Vec3 rd = level.rotateToRealWorld(
                                    new net.minecraft.world.phys.Vec3(off.x, off.y, off.z));
                            p.add(rd.x, rd.y, rd.z);
                        }
                        Live live = new Live(p, cid, entity.state());
                        endpoints.computeIfAbsent(chainId, k -> new Live[2])[role == 0 ? 0 : 1] = live;
                        // Record where this captured endpoint lives, so it reconnects after a restart even without
                        // the block-entity link (the key fix for a chain joining two contraptions).
                        if (chain != null) {
                            if (role == 0) {
                                chain.contraptionA = cid;
                                chain.localA = local.immutable();
                            } else {
                                chain.contraptionB = cid;
                                chain.localB = local.immutable();
                            }
                        }
                    }
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
        // Damping term: the endpoints' relative velocity ALONG the axis (positive = separating), derived from how
        // far each end moved since last tick. Feeding this into the solver turns the tether from a pure position
        // spring (which pumps energy and oscillates forever — "nunca se redujo la energía") into a damped one that
        // bleeds off relative motion and settles. First coupled tick has no previous sample → 0 (no damping yet).
        double separating = 0.0;
        if (chain.lastEndA != null && chain.lastEndB != null) {
            org.joml.Vector3d unit = new org.joml.Vector3d(axis).div(dist);
            org.joml.Vector3d velA = new org.joml.Vector3d(a.pos()).sub(chain.lastEndA);
            org.joml.Vector3d velB = new org.joml.Vector3d(b.pos()).sub(chain.lastEndB);
            separating = velB.sub(velA).dot(unit); // (vB - vA)·unit — relative rate along A->B
        }
        chain.lastEndA = new org.joml.Vector3d(a.pos());
        chain.lastEndB = new org.joml.Vector3d(b.pos());
        ChainPhysics.RopeResult r = ChainPhysics.resolve(dist, chain.blocks, chain.material.stretch(),
                separating,
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
        // Pull A toward B (+axis) and B toward A (-axis). `maxed` = the chain is over its break stretch, i.e. the
        // other end can't yield — only THEN does a block-anchored bearing stall; below that it keeps moving/pulling.
        boolean maxed = r.broke();
        handleEnd(a, impulse, maxed);
        handleEnd(b, new org.joml.Vector3d(impulse).negate(), maxed);
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
    private static void handleEnd(Live end, org.joml.Vector3d impulse, boolean maxed) {
        if (end.state() == null) {
            return; // a static block anchor — nothing to move
        }
        dev.arubik.craftengine.contraption.BearingType type = end.state().bearingType();
        if (type == dev.arubik.craftengine.contraption.BearingType.PHYS) {
            // A rigid body — impulse at the endpoint (scaled by inverse mass), so it yanks + torques.
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.applyThrust(end.contraptionId(), end.pos(), impulse);
        } else if (type == dev.arubik.craftengine.contraption.BearingType.MINECART
                || type == dev.arubik.craftengine.contraption.BearingType.GHAST) {
            // Entity-anchored (a real minecart/ghast carries it) — push its ANCHOR ENTITY toward the tension so a
            // moving end drags the other. (bearingType is now set at assembly — see MinecartBearing/GhastHarness.)
            pullAnchorEntity(end.state(), impulse);
        } else if (type == dev.arubik.craftengine.contraption.BearingType.LINEAR
                || type == dev.arubik.craftengine.contraption.BearingType.ROTATIONAL) {
            // Block-anchored: can't be dragged; its moving endpoint already pulls the OTHER end (winch). Only STALL
            // it when the chain is MAXED so it doesn't rip through.
            if (maxed) {
                end.state().setStalled(true);
            }
        }
    }

    /** Velocity added to an anchor entity per unit of rope impulse (entities take velocity, not a mass-scaled
     *  impulse) — strong so a tethered minecart/ghast actually follows a fast pull instead of the chain snapping. */
    private static final double ENTITY_PULL_FACTOR = 0.25;

    /** Nudges a MINECART/GHAST contraption's real anchor entity toward the chain tension. */
    private static void pullAnchorEntity(dev.arubik.craftengine.contraption.ContraptionState state,
            org.joml.Vector3d impulse) {
        // GHAST sets anchorEntityId; a MINECART does NOT (its cart id lives on the MinecartFollowBehavior), so
        // fall back to that — otherwise a minecart tether resolved to a null entity and nothing was pulled.
        java.util.UUID id = state.anchorEntityId();
        if (id == null) {
            for (dev.arubik.craftengine.contraption.MovementBehavior b : state.behaviors()) {
                if (b instanceof dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior follow) {
                    id = follow.entityId();
                    break;
                }
            }
        }
        if (id == null) {
            return;
        }
        org.bukkit.entity.Entity e = Bukkit.getEntity(id);
        if (e == null) {
            return;
        }
        e.setVelocity(e.getVelocity().add(new org.bukkit.util.Vector(
                impulse.x * ENTITY_PULL_FACTOR, impulse.y * ENTITY_PULL_FACTOR, impulse.z * ENTITY_PULL_FACTOR)));
    }
}
