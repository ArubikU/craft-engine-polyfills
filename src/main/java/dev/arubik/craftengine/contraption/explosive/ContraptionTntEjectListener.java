package dev.arubik.craftengine.contraption.explosive;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.ContraptionEntity;
import dev.arubik.craftengine.contraption.ContraptionState;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;

/**
 * <b>Lit TNT leaves the contraption.</b> The instant a captured TNT cell is primed — by redstone, fire,
 * flint and steel, a flaming arrow, or another explosion — the block is removed from the contraption and
 * a real {@link PrimedTnt} appears in the REAL world at that cell's exact live world position, carrying
 * the contraption's velocity at that point. Applies to <i>every</i> contraption, physics-driven or not.
 *
 * <h2>Why the entity-spawn seam and not a block hook</h2>
 * A {@link dev.arubik.craftengine.contraption.level.ContraptionLevel} is a genuine {@code ServerLevel},
 * so vanilla TNT mechanics run inside it unmodified — which is the whole problem: a lit TNT would prime,
 * tick its fuse, and detonate inside a hidden dimension nobody can see. Every ignition path in
 * {@code TntBlock} (verified against this project's own mapped server jar: {@code onPlace},
 * {@code neighborChanged}, {@code useItemOn}, {@code onProjectileHit}, {@code playerWillDestroy},
 * {@code wasExploded}) funnels into one private {@code TntBlock#prime}, whose entire effect is
 * {@code new PrimedTnt(level, x + 0.5, y, z + 0.5, igniter)} followed by
 * {@code level.addFreshEntity(tnt)}. That single {@code addFreshEntity} is therefore the one choke point
 * that catches all of them, and it is exactly what fires Bukkit's cancellable {@link EntitySpawnEvent}
 * ({@code ServerLevel#addEntity} -&gt; {@code CraftEventFactory#doEntityAddEventCalling}). Hooking each
 * block path individually — or scanning for stray {@code PrimedTnt} entities on a timer — would be both
 * broader and later: this way the TNT never spends a single tick inside the contraption, so its fuse
 * cannot drift and no future ignition path can slip past.
 *
 * <p><b>The cell.</b> Every vanilla ignition path already deletes the TNT block itself, in the caller
 * around {@code prime}. The removal here is belt-and-braces so the user-facing rule ("a lit TNT leaves
 * the contraption") holds even for a path that primes without clearing the cell; removing an already-air
 * cell is a no-op, and {@code ContraptionLevel#refreshLocalPositions} picks the change up on the next
 * render pass.
 */
public final class ContraptionTntEjectListener implements Listener {

    /**
     * HIGHEST rather than MONITOR: the spawn is cancelled here, which a MONITOR handler may not do. Runs
     * after ordinary plugins so one that legitimately wants to veto a TNT prime still wins — its cancel
     * means no {@code PrimedTnt} at all, contraption or otherwise.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity handle = ((CraftEntity) event.getEntity()).getHandle();
        if (!(handle instanceof PrimedTnt primed)) {
            return;
        }
        Level level = handle.level();
        ContraptionBoundary boundary = ContraptionBoundary.of(level).orElse(null);
        if (boundary == null) {
            return; // ordinary world — vanilla TNT, untouched
        }
        if (!(boundary.realLevel() instanceof ServerLevel realLevel)) {
            return; // no real world to eject into; leave the TNT be rather than deleting it outright
        }

        Vec3 local = primed.position();
        Vec3 realPos = boundary.realWorldPositionOf(local);
        ContraptionState state = ContraptionWorlds.owning(level).map(ContraptionEntity::state).orElse(null);
        Vec3 inherited = ContraptionExplosives.velocityAt(state, realPos);

        event.setCancelled(true);
        clearCell(level, local);

        PrimedTnt ejected = new PrimedTnt(realLevel, realPos.x, realPos.y, realPos.z, null);
        // Copied rather than re-derived: the fuse may already have been shortened by a random-fuse prime,
        // the block state carries the TNT's own rendered variant, and the owner reference is a UUID that
        // resolves against the REAL level — where the igniting player actually stands — rather than the
        // hidden dimension the original was constructed in.
        ejected.owner = primed.owner;
        ejected.setFuse(primed.getFuse());
        ejected.setBlockState(primed.getBlockState());
        ejected.explosionPower = primed.explosionPower;
        ejected.isIncendiary = primed.isIncendiary;
        // The fresh entity's own random pop is kept and the structure's motion added on top, so TNT
        // dropped from a diving contraption keeps diving and TNT thrown from a spinning one is flung.
        ejected.setDeltaMovement(ejected.getDeltaMovement().add(inherited));
        realLevel.addFreshEntity(ejected);
    }

    /** Removes the TNT cell the prime came from, if it is somehow still standing. See the class javadoc. */
    private static void clearCell(Level level, Vec3 local) {
        BlockPos cell = BlockPos.containing(local);
        if (level.getBlockState(cell).getBlock() instanceof TntBlock) {
            level.removeBlock(cell, false);
        }
    }
}
