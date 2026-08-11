package dev.arubik.craftengine.contraption.physics;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Connects real-world changes to the phys bodies that care about them.
 *
 * <h2>Waking</h2>
 * A settled body sleeps, and the solver skips a sleeping body entirely — it bakes no terrain and
 * generates no contacts. Without this listener a body has no way to learn that the ground it fell
 * asleep on was mined out, so it hangs in the air indefinitely. Sleeping is only a sound
 * optimisation if the world can wake it, and these are the events through which the world changes
 * underneath one.
 *
 * <h2>Explosions</h2>
 * A phys contraption is not a real entity, so vanilla's {@code Explosion} never sees it and a blast
 * passed straight through as though it were not there. Both explosion events are handled so a body
 * gets thrown by TNT, creepers, beds, and anything else that detonates.
 *
 * <p>Everything here runs at {@link EventPriority#MONITOR} and ignores cancelled events: this only
 * ever reacts to changes that are actually happening, and it must never be the reason one is
 * cancelled.
 */
public final class PhysicsWorldListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        wake(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Placing matters as much as breaking: a block shoved under a sleeping body should hold it
        // up, and a sleeping body would otherwise stay embedded in it until something else woke it.
        wake(event.getBlock());
    }

    /**
     * Flowing liquids change what a body rests on without any break/place event ever firing — a lava
     * or water flow washing out the support under a sleeping contraption is exactly the case this
     * covers.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        wake(event.getToBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        blast(event.getLocation(), event.getYield(), event.getEntity().getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        blast(event.getBlock().getLocation(), event.getYield(), event.getBlock().getWorld());
    }

    private static void wake(Block block) {
        org.bukkit.World bukkitWorld = block.getWorld();
        net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle();
        PhysicsWorld.wakeNear(level.dimension(),
                block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
    }

    /**
     * {@code yield} is the fraction of blocks dropped, not the blast power, and Bukkit exposes no
     * power on either event. Vanilla TNT's power of 4 is the right stand-in: it is what the
     * overwhelming majority of explosions a contraption meets actually are, and erring toward it
     * keeps the impulse in a sane range rather than scaling off an unrelated quantity.
     */
    private static final double ASSUMED_EXPLOSION_POWER = 4.0;

    private static void blast(org.bukkit.Location at, float yield, org.bukkit.World world) {
        if (at == null || world == null) {
            return;
        }
        net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
        PhysicsWorld.applyExplosion(level.dimension(), at.getX(), at.getY(), at.getZ(), ASSUMED_EXPLOSION_POWER);
        PhysicsWorld.wakeNear(level.dimension(), at.getX(), at.getY(), at.getZ());
    }
}
