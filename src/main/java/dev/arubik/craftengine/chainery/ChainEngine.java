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
        ChainRenderer.rebuild(chain, world);
        return chain;
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
            var def = CraftEngineItems.byId(Key.of(chain.material.blockId()));
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

    // ---- per-tick render (physics hooks land here in phase 2) ----

    /** Repositions every live chain's render onto its current endpoint positions. */
    public static void tickAll() {
        for (Chain chain : ChainRegistry.all()) {
            try {
                World world = Bukkit.getWorld(chain.worldId);
                if (world == null || !endpointsLoaded(world, chain)) {
                    continue;
                }
                org.joml.Vector3d a = endpointWorld(chain.a);
                org.joml.Vector3d b = endpointWorld(chain.b);
                ChainRenderer.update(chain, world, a, b);
            } catch (Throwable ignored) {
                // one bad chain shouldn't stall the rest
            }
        }
    }

    private static boolean endpointsLoaded(World world, Chain chain) {
        return world.isChunkLoaded(chain.a.getX() >> 4, chain.a.getZ() >> 4)
                && world.isChunkLoaded(chain.b.getX() >> 4, chain.b.getZ() >> 4);
    }

    /**
     * Current world position of an endpoint. v1: the block's centre. Phase 2 will resolve a moving endpoint
     * (a chain block captured into a phys contraption) to its live rigid-body transform via PhysicsWorld.
     */
    private static org.joml.Vector3d endpointWorld(BlockPos pos) {
        return new org.joml.Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
}
