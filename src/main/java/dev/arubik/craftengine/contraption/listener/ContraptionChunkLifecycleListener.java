/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore;
import dev.arubik.craftengine.contraption.player.CePlayers;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class ContraptionChunkLifecycleListener
implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        ServerLevel nmsLevel = ((CraftWorld)event.getWorld()).getHandle();
        block0: for (Entity bukkitEntity : event.getChunk().getEntities()) {
            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
            for (Key type : ContraptionTypeRegistry.getRegisteredTypes()) {
                ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
                if (typeImpl == null || !typeImpl.isBearingEntity(nmsEntity)) continue;
                typeImpl.onBearingEntityLoad(nmsEntity, (Level)nmsLevel);
                continue block0;
            }
        }
        BlockAnchoredContraptionStore.rehydrateChunk(event.getWorld(), event.getChunk().getX(), event.getChunk().getZ());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();
        UUID worldId = event.getWorld().getUID();
        for (Map.Entry<UUID, BearingHammerListener.AnchorKey> e : BearingHammerListener.assembledAnchorsSnapshot().entrySet()) {
            UUID contraptionId = e.getKey();
            BearingHammerListener.AnchorKey anchor = e.getValue();
            if (!anchor.worldId().equals(worldId) || anchor.pos().getX() >> 4 != chunkX || anchor.pos().getZ() >> 4 != chunkZ) continue;
            ContraptionEntity entity = ContraptionManager.get(contraptionId);
            if (entity == null) {
                BearingHammerListener.forgetAssembled(contraptionId);
                continue;
            }
            ServerLevel realLevel = ((CraftWorld)event.getWorld()).getHandle();
            Key type = BlockAnchoredContraptionStore.typeToPersist(entity.state(), (Level)realLevel, anchor.pos());
            double rpm = BearingBlockBehavior.rpmAt((Level)realLevel, anchor.pos());
            double su = BearingBlockBehavior.suPerBlockAt((Level)realLevel, anchor.pos());
            BlockAnchoredContraptionStore.save(entity.state(), anchor.pos(), type, rpm, su);
            List<Player> viewers = CePlayers.resolve(event.getWorld().getPlayers());
            entity.despawn(viewers);
            ContraptionManager.remove(contraptionId);
            ContraptionLevel level = entity.state().level();
            if (level != null) {
                level.dispose();
            }
            BearingHammerListener.forgetAssembled(contraptionId);
        }
        for (Entity bukkitEntity : event.getChunk().getEntities()) {
            ContraptionEntity live;
            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
            ContraptionType owningType = null;
            for (Key type : ContraptionTypeRegistry.getRegisteredTypes()) {
                ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
                if (typeImpl == null || !typeImpl.isBearingEntity(nmsEntity)) continue;
                owningType = typeImpl;
                break;
            }
            if (owningType == null) continue;
            UUID contraptionId = owningType.getContraptionId(nmsEntity);
            ContraptionEntity contraptionEntity = live = contraptionId == null ? null : ContraptionManager.get(contraptionId);
            if (live == null) continue;
            owningType.onBearingEntityUnload(nmsEntity, live.state());
            live.despawn(CePlayers.resolve(event.getWorld().getPlayers()));
            ContraptionManager.remove(contraptionId);
            ContraptionLevel level = live.state().level();
            if (level == null) continue;
            level.dispose();
        }
    }
}

