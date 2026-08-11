package dev.arubik.craftengine.contraption.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
import org.bukkit.util.Vector;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerPlayer;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * <b>Creative Phys Wand (roadmap item #9 — the "creative phys wand" entry point).</b> A
 * creative-mode-only tool that lets a builder GRAB a whole live contraption and drag it around by
 * the crosshair (and resize it live), reusing the two building blocks the rest of the contraption
 * stack already exposes: {@link ContraptionEntity#teleport(org.bukkit.World, double, double, double, double)}
 * (roadmap item #1's atomic whole-structure move) to follow the aim every tick, and
 * {@link ContraptionEntity#setScale(double)} (the clamped {@code [0.1, 10]} live-resize setter) to
 * scale it up/down.
 *
 * <p><b>Item identity.</b> The wand is the CraftEngine custom item {@code cml:creative_phys_wand},
 * detected exactly like every other tool in this package (the hammers via {@code HammerItems},
 * {@link GlueWandListener}'s {@code cml:slime_glue}): {@link CraftEngineItems#getCustomItemId(ItemStack)}
 * on the held stack, compared against {@link #CREATIVE_PHYS_WAND}. No block/item behavior needed — a
 * plain model + item name declares it (see the class-footer YAML in the accompanying summary).
 *
 * <p><b>Which contraption is aimed at.</b> Rather than re-derive a footprint raycast, this REUSES
 * {@link ContraptionInteractionListener#raycast(ServerPlayer)} — the exact yaw-correct eye-ray-vs-
 * cell-AABB raycast the interaction system already runs (the one that logs
 * {@code "[Contraption] raycast: N contraption(s) checked"}) — and reads {@code hit.state().id()} off
 * the resolved {@link ContraptionInteractionListener.Hit}. Nearest hit across every live contraption
 * wins, so aiming at any visible cell of a contraption grabs that contraption.
 *
 * <p><b>Controls</b> (all creative-only — the wand no-ops for a non-{@link GameMode#CREATIVE}
 * player, and is entirely inert for anyone not holding it):
 * <ul>
 *   <li><b>Right-click</b> (air or block) aiming at a contraption → <b>GRAB</b> it (start dragging;
 *       the grab distance is captured as the current eye→bearing distance, clamped to
 *       a scale-derived minimum (see {@link #minGrabDistance})..{@link #MAX_GRAB_DISTANCE}). Right-click again → <b>RELEASE</b>
 *       (drop it wherever it currently is). A right-click while not aiming at any contraption (and
 *       not already grabbing) just reports "no contraption in sight".</li>
 *   <li><b>Left-click</b> → <b>scale DOWN</b> by {@link #SCALE_STEP}; <b>Sneak + left-click</b> →
 *       <b>scale UP</b>. Applies to the grabbed contraption if one is held, otherwise to whichever
 *       contraption the player is aiming at (so you can resize without grabbing). Clamped by
 *       {@link ContraptionState#setScale} to {@code [0.1, 10]}.</li>
 *   <li><b>Sneak + scroll</b> (mouse wheel) while grabbing → change the <b>grab distance</b> (push
 *       the held contraption farther / pull it closer along your look vector) by a scale-derived step (see {@link #distanceStep})
 *       per notch; the hotbar slot change is cancelled so the wand stays selected. A NON-sneak scroll
 *       is left alone, so scrolling off the wand slot naturally auto-releases (see below).</li>
 * </ul>
 *
 * <p><b>Per-tick drag.</b> {@link #start} registers a 1-tick repeating scheduler task (mirroring
 * {@link GlueWandListener#start}/{@code ConveyorWandListener#start}), whose {@link #tickDrag} walks
 * {@link #grabbed} and, for each still-valid (player, contraption) pair, teleports the contraption so
 * its bearing pivot sits {@code grabDistance} blocks in front of the player's eye along their look
 * direction — i.e. it follows the crosshair at a fixed reach — keeping the contraption's CURRENT yaw
 * (a drag translates, it doesn't spin). The move is a single {@link ContraptionEntity#teleport}
 * call, which is cross-world-safe, so dragging a contraption from one world into another Just Works.
 *
 * <p><b>Auto-release safety.</b> Every tick {@link #tickDrag} drops the grab (silently, no dangling
 * state) if the player logged off, left creative mode, stopped holding the wand, or the contraption
 * disassembled / despawned out from under them ({@code ContraptionManager.get(id) == null}).
 * {@link #onQuit} also proactively forgets a quitting player, and {@link #onItemHeld} forgets anyone
 * whose newly-selected hotbar slot isn't the wand.
 */
public final class CreativePhysWandListener implements Listener {

    /** The wand's CraftEngine custom-item id — see {@link CraftEngineItems#getCustomItemId(ItemStack)}. */
    public static final Key CREATIVE_PHYS_WAND = Key.of("cml", "creative_phys_wand");

    /** Per-notch live-resize increment applied by left-click / sneak+left-click (clamped by {@link ContraptionState#setScale}). */
    private static final double SCALE_STEP = 0.1;

    /**
     * Hard floor on grab distance, regardless of how small the contraption is. Not zero: at zero the
     * body would sit inside the eye, where the look vector pivots around it and the drag becomes
     * uncontrollable.
     */
    private static final double ABSOLUTE_MIN_GRAB_DISTANCE = 0.2;

    /** Farthest a grabbed contraption's bearing may be pushed from the eye. */
    private static final double MAX_GRAB_DISTANCE = 48.0;

    /**
     * How close a contraption may be pulled, as a fraction of its own radius — so the limit is
     * "until it is about to touch your face", not a fixed number of blocks.
     */
    private static final double GRAB_RADIUS_CLEARANCE = 0.5;

    /**
     * The closest this particular contraption may be held, derived from its actual size rather than a
     * constant.
     *
     * <p>A fixed floor is wrong in both directions: it held a scale-0.1 trinket a comically useless
     * two blocks away, while letting a scale-10 structure be pulled until it swallowed the camera.
     * Scaling the limit by the body's own bounding radius means a small contraption can legitimately
     * be held at well under one block, which is what {@code scale} is for.
     *
     * <p>The radius comes from the phys body's collision shape when there is one; a non-phys
     * contraption has no such shape, so its {@code scale} stands in directly.
     */
    private static double minGrabDistance(ContraptionState state) {
        double scale = state.scale();
        var body = dev.arubik.craftengine.contraption.physics.PhysicsWorld.bodyOf(state.id());
        double radius = body != null && !body.shape.isEmpty() ? body.shape.boundingRadius() * scale : scale;
        return Math.max(ABSOLUTE_MIN_GRAB_DISTANCE, radius * GRAB_RADIUS_CLEARANCE);
    }

    /**
     * Per-notch grab-distance increment. Proportional to scale so a tiny contraption is nudged in
     * sub-block steps instead of leaping past its whole usable range on one scroll notch.
     */
    private static double distanceStep(ContraptionState state) {
        return Math.max(0.05, 0.25 * state.scale());
    }

    /** player id -> currently-grabbed contraption id (the "grabbed contraption" tracker). */
    private final Map<UUID, UUID> grabbed = new HashMap<>();

    /** player id -> current grab distance (blocks in front of the eye the bearing is held at). */
    private final Map<UUID, Double> grabDistance = new HashMap<>();

    /**
     * The live registered instance, for the PACKET bridge (2026-07-04 fix — see
     * {@link #wouldHandlePacketInteract}). {@code ContraptionInteractPacketDebug} is a static
     * packetevents listener with no reference to this Bukkit listener, so it needs a way in.
     */
    private static volatile CreativePhysWandListener instance;

    public CreativePhysWandListener() {
        instance = this;
    }

    /**
     * <b>The packet bridge's OFF-THREAD "should the wand eat this click?" check (2026-07-04 fix).</b>
     *
     * <p>Root cause it fixes: a contraption is a swarm of PACKET-ONLY fake {@code INTERACTION} entities
     * that the SERVER never spawned, so when the client right-clicks one it sends an
     * {@code INTERACT_ENTITY} packet for an entity id vanilla can't resolve — vanilla drops it and
     * {@link PlayerInteractEntityEvent} NEVER FIRES (see {@code ContraptionInteractPacketDebug}'s class
     * javadoc, which live-confirmed exactly this). That made this listener's own
     * {@code onInteractEntity}/{@code onInteractAtEntity} handlers dead code — the wand never grabbed
     * anything, which is precisely the reported bug. The ONLY thing that sees these clicks is the
     * packetevents listener, so the grab has to be dispatched from there.
     *
     * <p>Called on packetevents' Netty decode thread, so it must stay READ-ONLY: it reads the player's
     * game mode + main-hand item and (via {@link #aimedContraption}) the contraption raycast, which is
     * immutable position data only — the exact same off-thread-safe reads
     * {@code ContraptionInteractPacketDebug} already performs to decide packet cancellation
     * synchronously. Returns true iff the wand should consume this click, in which case the caller
     * cancels the packet and hops {@link #handlePacketInteract} onto the main thread.
     */
    public static boolean wouldHandlePacketInteract(Player player) {
        CreativePhysWandListener self = instance;
        if (self == null || player == null || !wieldingWand(player)) {
            return false;
        }
        // Already grabbing → a click anywhere drops it. Otherwise only consume when actually aiming at
        // a contraption, so a wand-holding creative player can still interact with everything else.
        return self.grabbed.containsKey(player.getUniqueId()) || aimedContraption(player) != null;
    }

    /** MAIN-THREAD half of {@link #wouldHandlePacketInteract} — performs the actual grab/release. */
    public static void handlePacketInteract(Player player) {
        CreativePhysWandListener self = instance;
        if (self == null || player == null || !wieldingWand(player)) {
            return;
        }
        self.toggleGrab(player);
    }

    /**
     * Start the per-tick drag task (see {@link #tickDrag}), mirroring the other wand listeners'
     * {@code start(plugin)} convention. Called once from {@code CraftEnginePolyfills#onEnable} right
     * after this listener is registered.
     */
    public void start(org.bukkit.plugin.Plugin plugin) {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::tickDrag, 1L, 1L);
    }

    // ------------------------------------------------------------------ held-item identity

    /** True iff {@code stack} is the {@code cml:creative_phys_wand} custom item. */
    private static boolean isWand(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        Key id = CraftEngineItems.getCustomItemId(stack);
        return CREATIVE_PHYS_WAND.equals(id);
    }

    /** True iff {@code player} is in creative mode AND currently holding the wand in their main hand. */
    private static boolean wieldingWand(Player player) {
        return player.getGameMode() == GameMode.CREATIVE
                && isWand(player.getInventory().getItemInMainHand());
    }

    // ------------------------------------------------------------------ grab / release / scale

    /**
     * Right-click toggles grab/release; left-click resizes. Runs at {@link EventPriority#HIGH} and
     * cancels the event whenever the wand actually acts, so vanilla creative block-placement/break
     * never fires under the wand.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard — same idiom as every other wand/hammer listener here
        }
        Player player = event.getPlayer();
        if (!wieldingWand(player)) {
            return; // not our tool / not creative — leave the click entirely alone
        }
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) {
            return;
        }
        event.setCancelled(true);
        if (right) {
            toggleGrab(player);
        } else {
            adjustScale(player, player.isSneaking() ? +SCALE_STEP : -SCALE_STEP);
        }
    }

    /**
     * <b>Packet-interaction bridge (2026-07-04 fix — "la interaccion con contraptions es via packets,
     * no via eventos normales").</b> A contraption is a swarm of PACKET-ONLY fake {@code INTERACTION}
     * hitbox entities ({@code ContraptionHitboxSwarm}), so aiming at one and right-clicking makes the
     * CLIENT report an ENTITY interaction — firing {@link PlayerInteractEntityEvent} (and its
     * {@link PlayerInteractAtEntityEvent} sibling for the precise-hit sub-action), NOT the
     * {@link PlayerInteractEvent} the {@link #onInteract} handler above listens to. Without handling
     * these the wand never fires when actually looking at a contraption (the whole point). This is the
     * exact same landmine {@link ContraptionInteractionListener#onInteractEntity} documents; we run at
     * {@link EventPriority#LOW} (BEFORE that listener's {@code HIGH}) and cancel the event when the wand
     * acts, so the interaction listener's {@code ignoreCancelled=true} skips routing the click into the
     * captured block underneath.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        handleEntityInteract(event);
    }

    /** {@code INTERACT_AT} sub-action twin of {@link #onInteractEntity} (declares its own HandlerList — see
     *  {@code BearingHammerListener#onInteractAtEntity} for why both must be listened to). */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        handleEntityInteract(event);
    }

    private void handleEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard
        }
        Player player = event.getPlayer();
        if (!wieldingWand(player)) {
            return;
        }
        // Only consume the click if it's actually a contraption (raycast hit) or we're already grabbing
        // (so a right-click anywhere drops it) — otherwise leave real-entity right-clicks alone.
        if (!grabbed.containsKey(player.getUniqueId()) && aimedContraption(player) == null) {
            return;
        }
        event.setCancelled(true);
        toggleGrab(player);
    }

    /** Grabs the aimed contraption, or releases the currently-held one if already grabbing. */
    private void toggleGrab(Player player) {
        UUID id = player.getUniqueId();
        if (grabbed.containsKey(id)) {
            setKinematic(grabbed.remove(id), false);
            grabDistance.remove(id);
            player.sendActionBar(Component.text("Released contraption.", NamedTextColor.GRAY));
            return;
        }
        ContraptionState target = aimedContraption(player);
        if (target == null) {
            player.sendActionBar(Component.text("No contraption in sight to grab.", NamedTextColor.YELLOW));
            return;
        }
        Location eye = player.getEyeLocation();
        double dist = eye.toVector().distance(new Vector(target.x(), target.y(), target.z()));
        dist = clamp(dist, minGrabDistance(target), MAX_GRAB_DISTANCE);
        grabbed.put(id, target.id());
        grabDistance.put(id, dist);
        setKinematic(target.id(), true);
        player.sendActionBar(Component.text(
                "Grabbed — drag with your crosshair. Right-click to drop, scroll to resize, sneak+scroll to reach.",
                NamedTextColor.AQUA));
    }

    /**
     * Suspends or resumes physics for a contraption while it is held.
     *
     * <p>Without this the solver keeps integrating gravity and contacts against a body the drag is
     * simultaneously teleporting: every tick the wand places it and physics pulls it back down, so the
     * body fights the cursor and the accumulated velocity fires it away on release. Marking it
     * kinematic hands the transform entirely to the wand. Releasing wakes the body with zeroed
     * velocity so it drops from where it was let go rather than inheriting the drag's motion.
     */
    private static void setKinematic(UUID contraptionId, boolean kinematic) {
        if (contraptionId == null) {
            return;
        }
        // Pin via HELD, not by writing body.kinematic directly: sync/syncMain recompute kinematic from
        // state.isStalled() every tick and would clobber a direct write on the next pump (the "grabbed
        // contraption still falls" bug). HELD is OR'd into that computation, so the hold survives.
        dev.arubik.craftengine.contraption.physics.PhysicsWorld.setHeld(contraptionId, kinematic);
        var body = dev.arubik.craftengine.contraption.physics.PhysicsWorld.bodyOf(contraptionId);
        if (body == null) {
            return; // not a PhysContraption — nothing to suspend
        }
        body.kinematic = kinematic; // immediate effect this tick; HELD keeps it across the next sync
        if (!kinematic) {
            body.body.linearVelocity.zero();
            body.body.angularVelocity.zero();
            body.wakeUp();
        }
    }

    /**
     * Resize the grabbed contraption (or, if not grabbing, the aimed one) by {@code delta}, via
     * {@link ContraptionEntity#setScale} (which clamps to {@code [0.1, 10]} and invalidates the
     * render cache so the new size is resent next tick).
     */
    private void adjustScale(Player player, double delta) {
        ContraptionEntity entity = grabbedEntity(player.getUniqueId());
        if (entity == null) {
            ContraptionState aimed = aimedContraption(player);
            entity = aimed == null ? null : ContraptionManager.get(aimed.id());
        }
        if (entity == null) {
            player.sendActionBar(Component.text("No contraption to resize.", NamedTextColor.YELLOW));
            return;
        }
        double next = entity.state().scale() + delta;
        entity.setScale(next);
        player.sendActionBar(Component.text(
                String.format("Scale: %.2f", entity.state().scale()), NamedTextColor.LIGHT_PURPLE));
    }

    /**
     * Sneak + scroll while grabbing changes the grab distance (push/pull the held contraption along
     * the look vector); the hotbar slot change is cancelled so the wand stays selected. A non-sneak
     * scroll is left untouched — see {@link #onItemHeld}'s auto-release below.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (!grabbed.containsKey(id) || !wieldingWand(player)) {
            return; // not scrolling while grabbing — fall through to onItemHeld's release logic
        }
        // While grabbing, scroll is repurposed (slot change cancelled so the wand stays held): SNEAK +
        // scroll pushes/pulls the grab reach, a PLAIN scroll live-resizes the contraption. Left-click
        // can't be used for scale — aiming at a contraption's packet-only hitbox never delivers a
        // left-click to the server (see #handleEntityInteract) — so scroll is the reliable input.
        event.setCancelled(true);
        int delta = event.getNewSlot() - event.getPreviousSlot();
        if (delta > 4) {
            delta -= 9;
        } else if (delta < -4) {
            delta += 9;
        }
        if (player.isSneaking()) {
            ContraptionState held = grabbedState(player);
            double min = held == null ? ABSOLUTE_MIN_GRAB_DISTANCE : minGrabDistance(held);
            double step = held == null ? 0.25 : distanceStep(held);
            double dist = grabDistance.getOrDefault(id, min) + delta * step;
            dist = clamp(dist, min, MAX_GRAB_DISTANCE);
            grabDistance.put(id, dist);
            player.sendActionBar(Component.text(String.format("Grab reach: %.2f blocks", dist), NamedTextColor.AQUA));
        } else {
            adjustScale(player, delta * SCALE_STEP);
        }
    }

    /**
     * Switching the held slot away from the wand releases any grab (and stops the drag).
     *
     * <p><b>Runs at {@link EventPriority#HIGHEST}, deliberately (2026-07-04 fix — "al agarrar una
     * contraption con la wand y hacer scroll se suelta").</b> This handler used to run at the default
     * {@code NORMAL} priority — which Bukkit dispatches BEFORE {@code HIGH} — so on a scroll-while-
     * grabbing it fired FIRST, saw a newly-selected slot that isn't the wand, and released the grab
     * before {@link #onScroll} (at {@code HIGH}) ever got the chance to cancel the slot change and
     * turn the scroll into a resize. The {@code isCancelled} guard below was correct but useless at
     * that priority: nothing had cancelled the event yet. Running LAST means {@code onScroll} has
     * already cancelled a repurposed scroll, so this only ever releases on a REAL slot change away
     * from the wand.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return; // scroll-while-grabbing already handled it (see #onScroll)
        }
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        if (!isWand(next)) {
            release(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        release(event.getPlayer().getUniqueId());
    }

    // ------------------------------------------------------------------ per-tick drag

    /**
     * Move every grabbed contraption to a point {@code grabDistance} blocks in front of its grabber's
     * eye along their look direction, keeping the contraption's current yaw — the actual "it follows
     * my crosshair" behavior. Auto-releases any grab whose grabber logged off / left creative / put
     * the wand away, or whose contraption disassembled (null-checked via {@link ContraptionManager#get}).
     */
    private void tickDrag() {
        Iterator<Map.Entry<UUID, UUID>> it = grabbed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, UUID> e = it.next();
            UUID playerId = e.getKey();
            UUID contraptionId = e.getValue();
            Player player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player == null || !wieldingWand(player)) {
                // Logged off / left creative / stopped holding the wand — drop the grab. Physics must
                // resume, or a body released by disconnecting stays frozen in mid-air forever.
                setKinematic(contraptionId, false);
                it.remove();
                grabDistance.remove(playerId);
                continue;
            }
            ContraptionEntity entity = ContraptionManager.get(contraptionId);
            if (entity == null) {
                // Contraption disassembled/despawned out from under the grab — release cleanly.
                it.remove();
                grabDistance.remove(playerId);
                player.sendActionBar(Component.text("The grabbed contraption is gone.", NamedTextColor.GRAY));
                continue;
            }
            Location eye = player.getEyeLocation();
            Vector look = eye.getDirection(); // already normalized
            double dist = grabDistance.getOrDefault(playerId, minGrabDistance(entity.state()));
            double tx = eye.getX() + look.getX() * dist;
            double ty = eye.getY() + look.getY() * dist;
            double tz = eye.getZ() + look.getZ() * dist;
            // Keep the contraption's current yaw — a drag translates, it doesn't spin.
            //
            // SAME-WORLD drag must NOT teleport (2026-07-04 fix — "mientras drageo no se ve,
            // directamente desaparece"). ContraptionEntity#teleport is the CROSS-WORLD re-anchor: its
            // very first step is despawn(oldWorld viewers) — destroying every render/hitbox/furniture
            // satellite (and dismounting seated riders) before re-anchoring. Calling it once per tick
            // for an ordinary same-world drag tore the whole swarm down and rebuilt it 20x/second, so
            // the contraption was despawned for most of every tick — i.e. invisible while dragged.
            // A same-world move is just a position change: setPosition is exactly what the engine's own
            // kinematics path (stepKinematics) uses, and the renderer's own moved-detection picks the
            // new transform up next tick with no despawn at all. Only a genuine WORLD change needs the
            // full teleport (and its despawn), so gate on that.
            if (!player.getWorld().getUID().equals(entity.state().worldId())) {
                entity.teleport(player.getWorld(), tx, ty, tz, entity.state().yawRadians());
            } else {
                entity.state().setPosition(tx, ty, tz);
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    /** The state of the contraption this player is currently grabbing, or {@code null} if not grabbing. */
    private ContraptionState grabbedState(Player player) {
        UUID contraptionId = grabbed.get(player.getUniqueId());
        if (contraptionId == null) {
            return null;
        }
        ContraptionEntity entity = ContraptionManager.get(contraptionId);
        return entity == null ? null : entity.state();
    }

    /** The contraption whose cell the player is aiming at (nearest hit), or null — reuses the interaction raycast. */
    private static ContraptionState aimedContraption(Player player) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        ContraptionInteractionListener.Hit hit = ContraptionInteractionListener.raycast(sp);
        return hit == null ? null : hit.state();
    }

    /** The live {@link ContraptionEntity} a player is currently grabbing, or null if none / already gone. */
    private ContraptionEntity grabbedEntity(UUID playerId) {
        UUID id = grabbed.get(playerId);
        return id == null ? null : ContraptionManager.get(id);
    }

    private void release(UUID playerId) {
        grabbed.remove(playerId);
        grabDistance.remove(playerId);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
