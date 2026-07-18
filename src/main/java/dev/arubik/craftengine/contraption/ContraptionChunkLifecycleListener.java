package dev.arubik.craftengine.contraption;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * Replaces the old {@code ContraptionPersistence}/{@code ContraptionChunkListener} design
 * (CONTRAPTIONS.md Phase 6 course-correction): that design used {@code Math.floorDiv} on a
 * contraption's CURRENT (possibly drifting, for a moving contraption) {@code
 * ContraptionState#x()}/{@code z()} to guess which real-world chunk to watch — wrong twice
 * over: a contraption is anchored to a fixed real block or entity, not a floating XZ guess,
 * and a moving contraption's live position drifts away from that fixed anchor entirely.
 *
 * <p>This listener instead keys everything off each bearing's FIXED anchor:
 * <ul>
 *   <li>Block-anchored (LINEAR/ROTATIONAL — detected via
 *   {@link dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior}, assembled-state
 *   bookkeeping in {@link BearingHammerListener#assembledAnchorsSnapshot()}): on unload, tear
 *   down the in-memory {@link ContraptionLevel} for any contraption whose tracked anchor
 *   position falls in the unloading chunk. <b>Known gap, unchanged by the behavior-detection
 *   fix</b>: there is still no real {@code BlockEntity} to persist into, so this loses the
 *   structure — documented in {@link BearingHammerListener}'s javadoc, not silently swept
 *   under the rug. Nothing to do on load (no real save path to rehydrate from).</li>
 *   <li>Entity-anchored (MINECART, real — see {@link MinecartBearing}): on unload, re-dump
 *   the CURRENT structure into the minecart's own PersistentDataContainer (so nothing is
 *   lost — the entity itself persists via ordinary vanilla entity save/load) and tear down
 *   the in-memory level; on load, rehydrate from that same PDC. This half is NOT a
 *   placeholder — it's the real, final persistence path for that bearing type.</li>
 * </ul>
 */
public final class ContraptionChunkLifecycleListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (MinecartBearing.isBearing(entity)) {
                MinecartBearing.rehydrate(entity);
            }
            // GHAST bearings are entity-anchored exactly like minecarts (the harnessed ghast's own
            // vanilla entity save/load persists the PDC the structure rides in), so they get the same
            // treatment — see GhastHarnessBearing's "Persistence".
            if (GhastHarnessBearing.isBearing(entity)) {
                GhastHarnessBearing.rehydrate(entity);
            }
        }
        // Block-anchored (LINEAR/ROTATIONAL) — the disk analog of the minecart's natural
        // entity-driven rehydrate above: rebuild any persisted contraption whose bearing anchor
        // block is in THIS loading chunk and isn't already live. See BlockAnchoredContraptionStore.
        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.rehydrateChunk(
                event.getWorld(), event.getChunk().getX(), event.getChunk().getZ());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();
        UUID worldId = event.getWorld().getUID();

        // Block-anchored bearings (LINEAR/ROTATIONAL) — see BearingHammerListener's javadoc
        // for why this bookkeeping lives there instead of a real BlockEntity.
        for (Map.Entry<UUID, BearingHammerListener.AnchorKey> e
                : BearingHammerListener.assembledAnchorsSnapshot().entrySet()) {
            UUID contraptionId = e.getKey();
            BearingHammerListener.AnchorKey anchor = e.getValue();
            if (!anchor.worldId().equals(worldId))
                continue;
            if ((anchor.pos().getX() >> 4) != chunkX || (anchor.pos().getZ() >> 4) != chunkZ)
                continue;

            ContraptionEntity entity = ContraptionManager.get(contraptionId);
            if (entity == null) {
                BearingHammerListener.forgetAssembled(contraptionId);
                continue;
            }
            // Re-dump the CURRENT live structure/transform before teardown (not just the
            // assemble-time snapshot) — exactly like the minecart branch below calls
            // saveStructure first. Type/rpm/su are re-derived from the bearing block itself (still
            // present in this chunk that's about to unload). See BlockAnchoredContraptionStore#save.
            net.minecraft.world.level.Level realLevel =
                    ((org.bukkit.craftbukkit.CraftWorld) event.getWorld()).getHandle();
            BearingType type = dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore
                    .typeToPersist(entity.state(), realLevel, anchor.pos());
            double rpm = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior
                    .rpmAt(realLevel, anchor.pos());
            double su = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior
                    .suPerBlockAt(realLevel, anchor.pos());
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.save(
                    entity.state(), anchor.pos(), type, rpm, su);
            // Despawn to the world's actual players, never an empty list: every swarm's
            // despawnAll iterates the viewers it is HANDED and then clears its own records, so an
            // empty list sends zero despawn packets while still forgetting the entities existed —
            // leaving the fake blocks stranded on every client that could see them.
            List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                    CePlayers.resolve(event.getWorld().getPlayers());
            entity.despawn(viewers);
            ContraptionManager.remove(contraptionId);
            ContraptionLevel level = entity.state().level();
            if (level != null) {
                level.dispose();
            }
            BearingHammerListener.forgetAssembled(contraptionId);
        }

        // Real entity-anchored bearings (MINECART, GHAST) — save-then-teardown, not lossy.
        for (Entity entity : event.getChunk().getEntities()) {
            boolean minecart = MinecartBearing.isBearing(entity) && MinecartBearing.isAssembled(entity);
            boolean ghast = GhastHarnessBearing.isBearing(entity) && GhastHarnessBearing.isAssembled(entity);
            if (!minecart && !ghast)
                continue;
            UUID contraptionId = minecart ? MinecartBearing.contraptionId(entity)
                    : GhastHarnessBearing.contraptionId(entity);
            ContraptionEntity live = contraptionId == null ? null : ContraptionManager.get(contraptionId);
            if (live == null)
                continue;
            if (minecart) {
                MinecartBearing.saveStructure(entity, live.state());
            } else {
                GhastHarnessBearing.saveStructure(entity, live.state());
            }
            live.despawn(CePlayers.resolve(event.getWorld().getPlayers()));
            ContraptionManager.remove(contraptionId);
            ContraptionLevel level = live.state().level();
            if (level != null) {
                level.dispose();
            }
        }
    }
}
