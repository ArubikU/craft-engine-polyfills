/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.dedicated.DedicatedServer
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Entity
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;

public final class ContraptionKill {
    private ContraptionKill() {
    }

    public static int killAll() {
        int killed = 0;
        for (ContraptionEntity entity : new ArrayList<ContraptionEntity>(ContraptionManager.all())) {
            if (!ContraptionKill.kill(entity)) continue;
            ++killed;
        }
        return killed;
    }

    public static boolean kill(ContraptionEntity entity) {
        ContraptionState state = entity.state();
        ContraptionLevel level = state.level();
        UUID id = state.id();
            try {
                Level realLevel = (Level)level.realLevel();
                if (realLevel != null) {
                    //get bukkit world
                    CraftWorld craftWorld = (CraftWorld)Bukkit.getWorld(realLevel.dimension().identifier().toString());
                    entity.despawn(CePlayers.resolve(craftWorld.getPlayers()));
                }
            }
            catch (Throwable t) {
                ContraptionKill.warn(id, "despawn", t);
            }
        boolean existed = ContraptionManager.remove(id) != null;
        try {
            PhysicsWorld.remove(id);
        }
        catch (Throwable t) {
            ContraptionKill.warn(id, "physics-remove", t);
        }
        ContraptionKill.cutResurrectionSources(state, id);
        if (level != null) {
            try {
                level.dispose();
            }
            catch (Throwable t) {
                ContraptionKill.warn(id, "level-dispose", t);
            }
        }
        return existed;
    }

    private static void cutResurrectionSources(ContraptionState state, UUID id) {
        try {
            BlockAnchoredContraptionStore.delete(id);
        }
        catch (Throwable t) {
            ContraptionKill.warn(id, "store-delete", t);
        }
        try {
            BearingHammerListener.forgetAssembled(id);
        }
        catch (Throwable t) {
            ContraptionKill.warn(id, "forget-assembled", t);
        }
        for (MovementBehavior b : ContraptionKill.behaviorsOf(state)) {
            try {
                GhastFollowBehavior follow;
                Entity ghast;
                if (b instanceof MinecartFollowBehavior) {
                    MinecartFollowBehavior follow2 = (MinecartFollowBehavior)b;
                    Entity cart = Bukkit.getEntity((UUID)follow2.entityId());
                    if (cart == null) continue;
                    cart.remove();
                    continue;
                }
                if (!(b instanceof GhastFollowBehavior) || (ghast = Bukkit.getEntity((UUID)(follow = (GhastFollowBehavior)b).entityId())) == null) continue;
                GhastContraptionType.forget(ghast);
            }
            catch (Throwable t) {
                ContraptionKill.warn(id, "anchor-cleanup", t);
            }
        }
    }

    private static List<MovementBehavior> behaviorsOf(ContraptionState state) {
        List<MovementBehavior> behaviors = state.behaviors();
        return behaviors == null ? List.of() : behaviors;
    }

    private static void warn(UUID id, String step, Throwable t) {
        try {
            Bukkit.getLogger().warning("[Contraption] killall: " + step + " failed for " + String.valueOf(id) + ": " + String.valueOf(t));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

