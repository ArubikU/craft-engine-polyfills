package dev.arubik.craftengine.contraption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;

/**
 * <b>Destroys live contraptions outright — the admin panic button behind {@code /cep contraption killall}.</b>
 *
 * <p><b>This is a DISCARD, not a disassemble. The captured blocks are destroyed, not returned to the
 * world.</b> That is the deliberate difference from {@link ContraptionAssembler#disassemble} (and
 * {@link MinecartBearing#disassembleInPlace} / {@link GhastContraptionType#disassembleInPlace}), which exist
 * precisely to land a contraption's structure back into the world as real blocks and should be preferred
 * whenever the goal is to remove a contraption without losing what a player built. This path exists for the
 * case those cannot serve: contraptions are eating the tick budget and have to stop existing NOW, including
 * ones whose restore would itself be expensive, land in unloaded terrain, or fail. Callers are expected to
 * warn first.
 *
 * <p><b>Persistence is deleted too, by design.</b> A "killall" whose victims come back on the next chunk
 * load or server restart would be worse than useless — it would look like it worked and quietly leave the
 * lag in place. Every resurrection source is therefore cut for each victim:
 * <ul>
 *   <li><b>Block-anchored</b> (LINEAR/ROTATIONAL): the on-disk record
 *       ({@code BlockAnchoredContraptionStore#delete}) and the in-memory assembled-anchor bookkeeping
 *       ({@code BearingHammerListener#forgetAssembled}) — the two things
 *       {@code ContraptionChunkLifecycleListener} rehydrates from.</li>
 *   <li><b>Minecart-anchored</b>: the anchor cart is REMOVED. It carries the structure in its own PDC and
 *       was itself spawned by {@link MinecartBearing#assemble} (it is part of the contraption, not
 *       pre-existing scenery), so removing it is both necessary — a surviving cart re-hydrates the whole
 *       contraption the next time its chunk loads — and correct.</li>
 *   <li><b>Ghast-anchored</b>: the ghast is a real, pre-existing mob and is left ALIVE; only its bearing
 *       tags are stripped ({@link GhastContraptionType#forget}), which is what stops the rehydrate.</li>
 * </ul>
 *
 * <p>Main-thread only.
 */
public final class ContraptionKill {

    private ContraptionKill() {
    }

    /**
     * Destroys every live contraption in every world. Returns how many were killed.
     *
     * <p>Iterates a SNAPSHOT of {@link ContraptionManager#all()} — that method exposes the registry's live
     * value collection, and {@link #kill} removes from it.
     */
    public static int killAll() {
        int killed = 0;
        for (ContraptionEntity entity : new ArrayList<>(ContraptionManager.all())) {
            if (kill(entity)) {
                killed++;
            }
        }
        return killed;
    }

    /**
     * Destroys one contraption. Returns whether it was actually torn down (false only if it had already
     * gone away). Best-effort per step: no single failing teardown step is allowed to leave the rest of a
     * kill half-done, since the whole point is that the contraption stops costing anything.
     */
    public static boolean kill(ContraptionEntity entity) {
        ContraptionState state = entity.state();
        UUID id = state.id();
        World world = null;
        try {
            net.minecraft.server.MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
            net.minecraft.server.level.ServerLevel level = server.getLevel(state.worldId());
            world = level != null ? level.getWorld() : null;
        } catch (Throwable ignored) {
            // no live server (unit-test path) — nothing to despawn to
        }

        // Despawn to the world's REAL players, never an empty list: every swarm's despawnAll iterates the
        // viewers it is HANDED and then clears its own records, so an empty list sends zero despawn packets
        // while forgetting the entities existed — stranding the fake blocks on every client that could see
        // them, permanently and with no live contraption left to ever clean them up.
        if (world != null) {
            try {
                entity.despawn(CePlayers.resolve(world.getPlayers()));
            } catch (Throwable t) {
                warn(id, "despawn", t);
            }
        }

        boolean existed = ContraptionManager.remove(id) != null;

        // Drop the rigid body, or the physics driver keeps solving a contraption that no longer exists.
        try {
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.remove(id);
        } catch (Throwable t) {
            warn(id, "physics-remove", t);
        }

        cutResurrectionSources(state, id);

        // Last: the mini-dimension itself. This is what actually stops the captured block entities ticking
        // and releases the chunk tickets holding it in memory (see ContraptionLevel#dispose) — the RAM half
        // of what a killall is for. Done after the despawn above, which needs the level to still resolve.
        ContraptionLevel level = state.level();
        if (level != null) {
            try {
                level.dispose();
            } catch (Throwable t) {
                warn(id, "level-dispose", t);
            }
        }
        return existed;
    }

    /** Cuts every path that would rebuild this contraption after it is gone — see the class javadoc. */
    private static void cutResurrectionSources(ContraptionState state, UUID id) {
        try {
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.delete(id);
        } catch (Throwable t) {
            warn(id, "store-delete", t);
        }
        try {
            BearingHammerListener.forgetAssembled(id);
        } catch (Throwable t) {
            warn(id, "forget-assembled", t);
        }
        for (MovementBehavior b : behaviorsOf(state)) {
            try {
                if (b instanceof MinecartFollowBehavior follow) {
                    Entity cart = Bukkit.getEntity(follow.entityId());
                    if (cart != null) {
                        cart.remove();
                    }
                } else if (b instanceof GhastFollowBehavior follow) {
                    Entity ghast = Bukkit.getEntity(follow.entityId());
                    if (ghast != null) {
                        GhastContraptionType.forget(ghast);
                    }
                }
            } catch (Throwable t) {
                warn(id, "anchor-cleanup", t);
            }
        }
    }

    /** Null-tolerant behavior list (the pure-JVM unit-test path can hold a state with none). */
    private static List<MovementBehavior> behaviorsOf(ContraptionState state) {
        List<MovementBehavior> behaviors = state.behaviors();
        return behaviors == null ? List.of() : behaviors;
    }

    private static void warn(UUID id, String step, Throwable t) {
        try {
            Bukkit.getLogger().warning("[Contraption] killall: " + step + " failed for " + id + ": " + t);
        } catch (Throwable ignored) {
        }
    }
}
