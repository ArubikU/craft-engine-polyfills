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
    public static Chain create(World world, BlockPos a, BlockPos b, ChainMaterial mat, int blocks) {
        Chain chain = new Chain(UUID.randomUUID(), world.getUID(), a, b, mat, blocks);
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
            removeEndpointBlock(world, level, chain.a);
            removeEndpointBlock(world, level, chain.b);
            if (dropItems) {
                dropChainItems(world, chain);
            }
        } finally {
            BREAKING.remove(chain.id);
        }
    }

    /** Sets an endpoint cell to air — but only if it is still one of this project's chain blocks. */
    private static void removeEndpointBlock(World world, Level level, BlockPos pos) {
        if (ChaineryBlockBehavior.getAt(level, pos) == null) {
            return; // already gone (the mined endpoint, or an unloaded chunk)
        }
        world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).setType(Material.AIR, false);
    }

    private static void dropChainItems(World world, Chain chain) {
        try {
            var def = CraftEngineItems.byId(Key.of(chain.material.linkItem()));
            if (def == null || chain.blocks <= 0) {
                return;
            }
            ItemStack stack = def.buildBukkitItem();
            stack.setAmount(Math.min(chain.blocks, stack.getMaxStackSize()));
            int remaining = chain.blocks;
            Location mid = new Location(world,
                    (chain.a.getX() + chain.b.getX()) / 2.0 + 0.5,
                    (chain.a.getY() + chain.b.getY()) / 2.0 + 0.5,
                    (chain.a.getZ() + chain.b.getZ()) / 2.0 + 0.5);
            while (remaining > 0) {
                ItemStack drop = def.buildBukkitItem();
                drop.setAmount(Math.min(remaining, drop.getMaxStackSize()));
                world.dropItemNaturally(mid, drop);
                remaining -= drop.getAmount();
            }
        } catch (Throwable ignored) {
        }
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
    private static final int ROPE_ITERATIONS = 16;

    /**
     * Once per tick: resolve every chain's two endpoints to their LIVE world positions — a captured endpoint
     * follows its contraption — then (a) render the span onto them and (b) if the chain is stretched past its
     * span, pull the tethered contraption(s) back, snapping the chain if the pull exceeds its max tension.
     */
    public static void tickAll() {
        // Index every chain endpoint that has been captured into a contraption: chainId -> role -> live pos+owner.
        java.util.Map<java.util.UUID, Live[]> captured = indexCapturedEndpoints();

        for (Chain chain : ChainRegistry.all()) {
            try {
                World world = Bukkit.getWorld(chain.worldId);
                if (world == null) {
                    continue;
                }
                Live[] ends = captured.get(chain.id);
                Live a = resolveEnd(world, chain, chain.a, ends, 0);
                Live b = resolveEnd(world, chain, chain.b, ends, 1);
                if (a == null || b == null) {
                    continue; // an endpoint is in an unloaded chunk / not resolvable this tick
                }
                if (applyRope(chain, a, b)) {
                    continue; // the chain snapped this tick — it's already gone
                }
                // Real rope physics: step the verlet chain between the live anchors against the world's terrain
                // (main thread here, so block reads are safe), then draw the links from its particles — a slack
                // chain sags and rests on the floor, a taut one straightens.
                ChainRope.Terrain terrain = terrainFor(world);
                chain.rope.step(a.pos(), b.pos(), chain.blocks, ROPE_ITERATIONS, ROPE_GRAVITY, ROPE_DAMPING, terrain);
                ChainRenderer.syncRope(chain, world, chain.rope);
            } catch (Throwable ignored) {
                // one bad chain shouldn't stall the rest
            }
        }
    }

    /** A solid-block probe over a live Bukkit world (main thread only). Unloaded chunks read as non-solid. */
    private static ChainRope.Terrain terrainFor(World world) {
        return (x, y, z) -> {
            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                return false;
            }
            return world.getBlockAt(x, y, z).getType().isSolid();
        };
    }

    /** Resolves one endpoint: prefer its captured (moving) position; else the static block centre if loaded. */
    private static Live resolveEnd(World world, Chain chain, BlockPos pos, Live[] captured, int role) {
        if (captured != null && captured[role] != null) {
            return captured[role];
        }
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return null;
        }
        return new Live(new org.joml.Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), null, null);
    }

    /** Scans every registered contraption for chain-endpoint block entities it has captured. */
    private static java.util.Map<java.util.UUID, Live[]> indexCapturedEndpoints() {
        java.util.Map<java.util.UUID, Live[]> map = new java.util.HashMap<>();
        for (dev.arubik.craftengine.contraption.ContraptionEntity entity :
                dev.arubik.craftengine.contraption.ContraptionManager.all()) {
            try {
                dev.arubik.craftengine.contraption.level.ContraptionLevel level = entity.state().level();
                java.util.UUID cid = entity.state().id();
                for (BlockPos local : level.localPositions()) {
                    net.momirealms.craftengine.core.block.entity.BlockEntity be =
                            dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded((Level) level, local);
                    if (be == null || !(be.controller instanceof ChainBlockEntity cbe)) {
                        continue;
                    }
                    java.util.UUID chainId = cbe.getChainId();
                    if (chainId == null) {
                        continue;
                    }
                    net.minecraft.world.phys.Vec3 w = level.realWorldPositionOf(local);
                    Live live = new Live(new org.joml.Vector3d(w.x, w.y, w.z), cid, entity.state());
                    map.computeIfAbsent(chainId, k -> new Live[2])[cbe.getRole() == 0 ? 0 : 1] = live;
                }
            } catch (Throwable ignored) {
                // a single misbehaving contraption shouldn't abort the whole index
            }
        }
        return map;
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
            breakChain(chain, true);
            return true;
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
