/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class CreativePhysWandListener
implements Listener {
    public static final Key CREATIVE_PHYS_WAND = Key.of((String)"cml", (String)"creative_phys_wand");
    private static final double SCALE_STEP = 0.1;
    private static final double ABSOLUTE_MIN_GRAB_DISTANCE = 0.2;
    private static final double MAX_GRAB_DISTANCE = 48.0;
    private static final double GRAB_RADIUS_CLEARANCE = 0.5;
    private final Map<UUID, UUID> grabbed = new HashMap<UUID, UUID>();
    private final Map<UUID, Double> grabDistance = new HashMap<UUID, Double>();
    private final Map<UUID, Integer> grabCooldown = new HashMap<UUID, Integer>();
    private static final int GRAB_COOLDOWN_TICKS = 5;
    private static volatile CreativePhysWandListener instance;

    private static double minGrabDistance(ContraptionState state) {
        double scale = state.scale();
        PhysBody body = PhysicsWorld.bodyOf(state.id());
        double radius = body != null && !body.shape.isEmpty() ? body.shape.boundingRadius() * scale : scale;
        return Math.max(0.2, radius * 0.5);
    }

    private static double distanceStep(ContraptionState state) {
        return Math.max(0.05, 0.25 * state.scale());
    }

    public CreativePhysWandListener() {
        instance = this;
    }

    public static boolean wouldHandlePacketInteract(Player player) {
        CreativePhysWandListener self = instance;
        if (self == null || player == null || !CreativePhysWandListener.wieldingWand(player)) {
            return false;
        }
        return self.grabbed.containsKey(player.getUniqueId()) || CreativePhysWandListener.aimedContraption(player) != null;
    }

    public static void handlePacketInteract(Player player) {
        CreativePhysWandListener self = instance;
        if (self == null || player == null || !CreativePhysWandListener.wieldingWand(player)) {
            return;
        }
        self.toggleGrab(player);
    }

    public void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tickDrag, 1L, 1L);
    }

    private static boolean isWand(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        Key id = CraftEngineItems.getCustomItemId((ItemStack)stack);
        return CREATIVE_PHYS_WAND.equals(id);
    }

    private static boolean wieldingWand(Player player) {
        return player.getGameMode() == GameMode.CREATIVE && CreativePhysWandListener.isWand(player.getInventory().getItemInMainHand());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event) {
        boolean left;
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (!CreativePhysWandListener.wieldingWand(player)) {
            return;
        }
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean bl = left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) {
            return;
        }
        event.setCancelled(true);
        if (right) {
            this.toggleGrab(player);
        } else {
            this.adjustScale(player, player.isSneaking() ? 0.1 : -0.1);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        this.handleEntityInteract(event);
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        this.handleEntityInteract((PlayerInteractEntityEvent)event);
    }

    private void handleEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (!CreativePhysWandListener.wieldingWand(player)) {
            return;
        }
        if (!this.grabbed.containsKey(player.getUniqueId()) && CreativePhysWandListener.aimedContraption(player) == null) {
            return;
        }
        event.setCancelled(true);
        this.toggleGrab(player);
    }

    private void toggleGrab(Player player) {
        UUID id = player.getUniqueId();
        int cooldown = this.grabCooldown.getOrDefault(id, 0);
        if (cooldown > 0) {
            return;
        }
        this.grabCooldown.put(id, 5);
        if (this.grabbed.containsKey(id)) {
            CreativePhysWandListener.setKinematic(this.grabbed.remove(id), false);
            this.grabDistance.remove(id);
            player.sendActionBar((Component)Component.text((String)"Released contraption.", (TextColor)NamedTextColor.GRAY));
            return;
        }
        ContraptionState target = CreativePhysWandListener.aimedContraption(player);
        if (target == null) {
            player.sendActionBar((Component)Component.text((String)"No contraption in sight to grab.", (TextColor)NamedTextColor.YELLOW));
            return;
        }
        Location eye = player.getEyeLocation();
        double dist = eye.toVector().distance(new Vector(target.x(), target.y(), target.z()));
        dist = CreativePhysWandListener.clamp(dist, CreativePhysWandListener.minGrabDistance(target), 48.0);
        this.grabbed.put(id, target.id());
        this.grabDistance.put(id, dist);
        CreativePhysWandListener.setKinematic(target.id(), true);
        Bukkit.getLogger().info("[Wand] GRABBED id=" + String.valueOf(target.id()) + " pos=(" + String.format("%.1f,%.1f,%.1f", target.x(), target.y(), target.z()) + ") held=" + PhysicsWorld.isHeld(target.id()));
        player.sendActionBar((Component)Component.text((String)"Grabbed \u2014 drag with your crosshair. Right-click to drop, scroll to resize, sneak+scroll to reach.", (TextColor)NamedTextColor.AQUA));
    }

    private static void setKinematic(UUID contraptionId, boolean kinematic) {
        if (contraptionId == null) {
            return;
        }
        PhysicsWorld.setHeld(contraptionId, kinematic);
        PhysBody body = PhysicsWorld.bodyOf(contraptionId);
        if (body == null) {
            return;
        }
        body.kinematic = kinematic;
        if (!kinematic) {
            body.body.linearVelocity.zero();
            body.body.angularVelocity.zero();
            body.wakeUp();
        }
    }

    private void adjustScale(Player player, double delta) {
        ContraptionEntity entity = this.grabbedEntity(player.getUniqueId());
        if (entity == null) {
            ContraptionState aimed = CreativePhysWandListener.aimedContraption(player);
            ContraptionEntity contraptionEntity = entity = aimed == null ? null : ContraptionManager.get(aimed.id());
        }
        if (entity == null) {
            player.sendActionBar((Component)Component.text((String)"No contraption to resize.", (TextColor)NamedTextColor.YELLOW));
            return;
        }
        double next = entity.state().scale() + delta;
        entity.setScale(next);
        player.sendActionBar((Component)Component.text((String)String.format("Scale: %.2f", entity.state().scale()), (TextColor)NamedTextColor.LIGHT_PURPLE));
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (!this.grabbed.containsKey(id) || !CreativePhysWandListener.wieldingWand(player)) {
            return;
        }
        event.setCancelled(true);
        int delta = event.getNewSlot() - event.getPreviousSlot();
        if (delta > 4) {
            delta -= 9;
        } else if (delta < -4) {
            delta += 9;
        }
        if (player.isSneaking()) {
            ContraptionState held = this.grabbedState(player);
            double min = held == null ? 0.2 : CreativePhysWandListener.minGrabDistance(held);
            double step = held == null ? 0.25 : CreativePhysWandListener.distanceStep(held);
            double dist = this.grabDistance.getOrDefault(id, min) + (double)delta * step;
            dist = CreativePhysWandListener.clamp(dist, min, 48.0);
            this.grabDistance.put(id, dist);
            player.sendActionBar((Component)Component.text((String)String.format("Grab reach: %.2f blocks", dist), (TextColor)NamedTextColor.AQUA));
        } else {
            this.adjustScale(player, (double)delta * 0.1);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        if (!CreativePhysWandListener.isWand(next)) {
            this.release(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.release(event.getPlayer().getUniqueId());
    }

    private void tickDrag() {
        this.grabCooldown.entrySet().removeIf(e -> {
            int remaining = (Integer)e.getValue() - 1;
            if (remaining <= 0) {
                return true;
            }
            e.setValue(remaining);
            return false;
        });
        Iterator<Map.Entry<UUID, UUID>> it = this.grabbed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, UUID> e2 = it.next();
            UUID playerId = e2.getKey();
            UUID contraptionId = e2.getValue();
            Player player = Bukkit.getPlayer((UUID)playerId);
            if (player == null || !CreativePhysWandListener.wieldingWand(player)) {
                CreativePhysWandListener.setKinematic(contraptionId, false);
                it.remove();
                this.grabDistance.remove(playerId);
                continue;
            }
            ContraptionEntity entity = ContraptionManager.get(contraptionId);
            if (entity == null) {
                it.remove();
                this.grabDistance.remove(playerId);
                player.sendActionBar((Component)Component.text((String)"The grabbed contraption is gone.", (TextColor)NamedTextColor.GRAY));
                continue;
            }
            Location eye = player.getEyeLocation();
            Vector look = eye.getDirection();
            double dist = this.grabDistance.getOrDefault(playerId, CreativePhysWandListener.minGrabDistance(entity.state()));
            double tx = eye.getX() + look.getX() * dist;
            double ty = eye.getY() + look.getY() * dist;
            double tz = eye.getZ() + look.getZ() * dist;
            if (!player.getWorld().getUID().equals(entity.state().worldId())) {
                entity.teleport(player.getWorld(), tx, ty, tz, entity.state().yawRadians());
                continue;
            }
            double bx = entity.state().x();
            double by = entity.state().y();
            double bz = entity.state().z();
            double nx = bx + (tx - bx) * 0.35;
            double ny = by + (ty - by) * 0.35;
            double nz = bz + (tz - bz) * 0.35;
            int cx = Mth.floor((double)nx) >> 4;
            int cz2 = Mth.floor((double)nz) >> 4;
            if (!player.getWorld().isChunkLoaded(cx, cz2)) {
                player.getWorld().loadChunk(cx, cz2, false);
            }
            entity.state().setPosition(nx, ny, nz);
            PhysicsWorld.syncBodyPosition(contraptionId, nx, ny, nz);
            Bukkit.getLogger().info("[Wand] tick pos=(" + String.format("%.1f,%.1f,%.1f", nx, ny, nz) + ") held=" + PhysicsWorld.isHeld(contraptionId) + " suspended=" + entity.renderSuspended());
        }
    }

    private ContraptionState grabbedState(Player player) {
        UUID contraptionId = this.grabbed.get(player.getUniqueId());
        if (contraptionId == null) {
            return null;
        }
        ContraptionEntity entity = ContraptionManager.get(contraptionId);
        return entity == null ? null : entity.state();
    }

    private static ContraptionState aimedContraption(Player player) {
        ServerPlayer sp = ((CraftPlayer)player).getHandle();
        ContraptionInteractionListener.Hit hit = ContraptionInteractionListener.raycast(sp);
        return hit == null ? null : hit.state();
    }

    private ContraptionEntity grabbedEntity(UUID playerId) {
        UUID id = this.grabbed.get(playerId);
        return id == null ? null : ContraptionManager.get(id);
    }

    private void release(UUID playerId) {
        this.grabbed.remove(playerId);
        this.grabDistance.remove(playerId);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}

