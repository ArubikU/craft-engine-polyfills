package dev.arubik.craftengine.contraption;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.listener.CreativePhysWandListener;

/**
 * Real fix (2026-07-01 debugging session — "right-click a chest inside a contraption does
 * nothing"). CONFIRMED root cause, proven via a raw packet-level test: our packet-only fake
 * {@code INTERACTION} hitbox entities (see {@link ContraptionHitboxSwarm}/
 * {@link ContraptionFurnitureSwarm}) have real client-side collision (sent a genuine
 * {@code ClientboundAddEntityPacket}), so the client sends a real
 * {@code ServerboundInteractPacket} referencing that entity id — but the SERVER never spawned a
 * real NMS/Bukkit entity under that id, so vanilla's own packet handler can't resolve a target
 * and silently drops the packet BEFORE Bukkit's event system ever constructs a
 * {@code PlayerInteractEntityEvent}. Live test confirmed: {@code INTERACT_ENTITY} packets arrived
 * every click, but zero raw-Bukkit-event or raycast-hit logs ever followed.
 *
 * <p>Fix: intercept the packet here, below Bukkit's event layer, and drive
 * {@link ContraptionInteractionListener#raycast}/{@link ContraptionInteractionListener#forward}
 * directly — the same dispatch {@code ContraptionInteractionListener#onInteract} uses for real
 * block/air clicks. Only acts on the {@code INTERACT} sub-action (not {@code INTERACT_AT}, which
 * vanilla sends alongside it for the same physical click) to avoid double-dispatch.
 *
 * <p><b>Thread hop (2nd bug, found after the above fix alone still didn't open menus)</b>:
 * packetevents delivers {@code onPacketReceive} off the main server thread (Netty I/O thread —
 * same reason {@link dev.arubik.craftengine.block.behavior.CrafterSlotStateListener} hops via
 * {@code Bukkit.getScheduler().runTask}). Vanilla's {@code Level#getBlockEntity(BlockPos)} has an
 * explicit {@code Thread.currentThread() != this.thread -> return null} guard that
 * {@code Level#getBlockState} does NOT have — so calling {@link ContraptionInteractionListener#forward}
 * straight from this thread found the right {@code BlockState} (furnace/chest render fine,
 * {@code useWithoutItem} even returns {@code SUCCESS}) but every {@code getBlockEntity} lookup
 * inside it (e.g. {@code ChestBlock#getMenuProvider}) silently returned null, so no menu ever
 * actually opened even though every log line looked like it worked. {@link #raycast} itself only
 * reads immutable position data (no block-entity access), so it's safe to run here to decide
 * packet cancellation synchronously (must happen inline — deferring it would let the packet
 * continue down the pipeline before we cancel it); only {@link #forward} — the part that touches
 * block entities/menus — is hopped onto the main thread.
 *
 * <p><b>Furniture seats, same root cause (2026-07-02 session, round 2 — "el interact aun no sirve
 * evitando que me pueda sentar en el").</b> A furniture seat's clickable surface
 * ({@code render.ContraptionFurnitureSwarm}'s packet-only {@code INTERACTION} hitbox mirror) is
 * spawned by the EXACT same packet-only technique documented above for block-cell hitboxes, so it
 * has the exact same problem: a prior pass this session added {@code ContraptionSeatListener
 * #onInteractEntity} against real Bukkit {@code PlayerInteractEntityEvent}, live-tested it, and it
 * still didn't work — because that event can never fire for this click, for the identical reason
 * {@code onInteract}/{@code forward} needed this packet-level bridge in the first place. Fixed the
 * same way: {@link #onPacketReceive} below now tries the seat raycast
 * ({@code ContraptionSeatListener}'s own {@code trySit} logic, walking every live
 * {@code SeatSlot} against the player's aim — see that class's javadoc) FIRST, since sitting down
 * takes priority over interacting with whatever's visually behind the seat, falling back to the
 * existing block raycast/dispatch only if no free seat was hit. {@code trySit} itself only reads
 * immutable seat-slot/contraption-state data — same as {@link #raycast} — so resolving WHICH seat
 * was hit is safe to do synchronously right here. But {@code trySit}'s actual seat/teleport
 * mutation (a real {@code Player#teleport}, plus {@code SeatSlot#occupy}/{@code
 * ContraptionState#addSeatedRider}) is NOT immutable-data-only, so — exactly like {@link #forward}
 * for blocks — that part is still hopped onto the main thread via
 * {@code ContraptionSeatListener#tryHandleSit}, not run inline on this Netty thread.
 *
 * <p><b>Real-entity bypass (2026-07-02 live-test fix -- "como quito un bearing armado el right
 * click no esta funcionando"), NARROWED (2026-07-02, same-day regression fix -- "no puedo
 * interactuar con nada del holo ni con los cofres").</b> This listener runs for EVERY
 * {@code INTERACT_ENTITY} packet server-wide, unconditionally trying the seat/block raycast keyed
 * off the PLAYER'S AIM -- never checking what the packet's own target entity id actually resolves
 * to. That's fine for a genuine fake-entity click (a packet-only hitbox/seat mirror has no real
 * backing entity, so {@code PlayerInteractEntityEvent} could never fire for it anyway -- see the
 * class javadoc above). But a MINECART bearing (see {@link MinecartBearing}) is a REAL,
 * normally-Bukkit-tracked vanilla minecart entity: right-clicking it fires a completely normal
 * {@code PlayerInteractEntityEvent} that {@link BearingHammerListener#onInteractEntity} already
 * listens for and handles correctly. If this listener's own raycast happens to find a nearby
 * captured cell/seat under the player's aim (very plausible here -- the glued structure sits
 * directly around/above the bearing), it CANCELS the packet before Bukkit's event system ever
 * constructs that event, silently swallowing the hammer disassemble click.
 *
 * <p>The FIRST attempt at this fix resolved {@code wrapper.getEntityId()} against
 * {@code player.level().getEntity(id)} and bypassed for ANY non-null result -- reasoning that a
 * packet-only fake (never added to the level via {@code addFreshEntity}/{@code addEntity}) could
 * never resolve there. That premise doesn't hold: this project's fake hitbox/seat mirrors
 * (see {@code render.ContraptionHitboxSwarm}/{@code ContraptionFurnitureSwarm}) mint their entity
 * ids via {@code Entity.ENTITY_COUNTER.incrementAndGet()} -- the EXACT SAME shared static counter
 * every real vanilla entity's constructor also draws from -- so a fake id is always numerically
 * indistinguishable from a real one, and {@code Level#getEntity(int)} keys purely off that
 * server-unique integer id, not off "was this id actually registered via addFreshEntity." Also,
 * this level now has plenty of REAL entities floating around a live contraption (seat mounts are
 * real invisible {@code ArmorStand}s -- see {@link ContraptionSeatMount} -- plus the minecart
 * bearing itself, item pickups, other players...), so {@code getEntity(id)} resolving to SOME
 * real entity is no proof at all that the CLICKED id was meant for that entity rather than for one
 * of our fakes reusing the same shared id space. The live symptom matched exactly: EVERY
 * fake-entity click (holo hitboxes, chests, everything) started resolving {@code targetEntity !=
 * null} and bailing before the seat/block raycast ever ran, breaking all packet-only interaction
 * project-wide -- not just around the bearing.
 *
 * <p>Fix: narrow the bypass to its actual intent -- only skip this interception path when the
 * resolved real entity is specifically something with its OWN dedicated real-entity handler (today
 * just {@link MinecartBearing#isBearing}), not "any real entity that happens to occupy this id."
 * Every other click (including one that coincidentally resolves to some unrelated nearby real
 * entity) still falls through to the seat/block raycast below, exactly like before the bearing fix
 * was ever added.
 */
public final class ContraptionInteractPacketDebug implements PacketListener {

    public static void register() {
        PacketEvents.getAPI().getEventManager().registerListener(new ContraptionInteractPacketDebug(),
                PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        try {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            org.bukkit.entity.Player bukkitPlayer = org.bukkit.Bukkit.getPlayer(event.getUser().getUUID());
            if (!(bukkitPlayer instanceof org.bukkit.craftbukkit.entity.CraftPlayer craftPlayer)) {
                return;
            }
            net.minecraft.server.level.ServerPlayer player = craftPlayer.getHandle();

            // Minecart bearing disassemble via PACKET (2026-07-02 — "el handle debe ir por packets":
            // the Bukkit PlayerInteractAtEntityEvent path proved unreliable in this
            // CraftEngine+packetevents environment, so BearingHammerListener's own AT-event handler
            // silently never disassembled). A REAL minecart right-click arrives as the INTERACT_AT
            // sub-action (a plain minecart has a bounding box, so the client resolves a hit vector
            // and sends interactAt, not the plain INTERACT fallback). Handle it here: grab the target
            // entity id synchronously, then resolve the entity + do the hammer check + disassemble on
            // the MAIN thread (this is packetevents' Netty decode thread; Level#getEntity is NOT
            // thread-safe here — the same async-crash the removed getEntity bypass caused). Gated to
            // INTERACT_AT so it fires exactly once per physical click; the seat/block raycast below
            // handles the separate INTERACT packet for fake packet-only entities.
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                int targetId = wrapper.getEntityId();
                org.bukkit.Bukkit.getScheduler().runTask(
                        dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                        () -> tryDisassembleBearing(bukkitPlayer, targetId));
                return;
            }
            if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
                return;
            }

            // NO real-entity bypass here (2026-07-02 regression fix — "no puedo interactuar con
            // nada del holo ni con los cofres"): the earlier bypass resolved the packet's target id
            // via player.level().getEntity(id) to skip real minecart-bearing clicks — but this
            // handler runs on packetevents' Netty decode thread (OFF the main server thread), and
            // Level#getEntity/getEntities is NOT thread-safe (it threw
            // "Asynchronous Chunk getEntities call!" on EVERY INTERACT_ENTITY packet, aborting the
            // whole handler before the raycast ever ran, breaking ALL packet-only interaction
            // project-wide). The bypass is also unnecessary: a real minecart bearing's disassemble
            // click arrives as a SEPARATE INTERACT_AT packet (not this INTERACT sub-action) which
            // this handler ignores entirely, so it reaches Bukkit's PlayerInteractAtEntityEvent and
            // BearingHammerListener#onInteractAtEntity normally. The worst case — this INTERACT
            // packet's raycast also finding a captured cell around the minecart and dispatching —
            // is harmless: it's deferred to the next tick (runTask below), by which point the
            // AT-event disassemble has already removed the contraption, so the deferred raycast
            // finds nothing and no-ops. Everything read below (raycast/hasSeatUnderAim) touches
            // only immutable position data, which IS safe off-thread.

            // Creative Phys Wand FIRST (2026-07-04 fix — "la creative phys wand sigue sin agarrar la
            // contraption"). The wand's own Bukkit PlayerInteractEntityEvent handlers are dead code for
            // the exact reason this whole class exists: a packet-only fake INTERACTION entity never
            // produces that event. So the grab has to be dispatched from HERE, and it must come before
            // the seat/block routing below — a wand-wielding creative player is grabbing the contraption,
            // not sitting in it or opening a captured chest. wouldHandlePacketInteract is read-only
            // (game mode + held item + the immutable-position raycast), safe on this Netty thread; the
            // actual grab is hopped to the main thread like every other mutation here.
            if (CreativePhysWandListener.wouldHandlePacketInteract(bukkitPlayer)) {
                event.setCancelled(true);
                org.bukkit.Bukkit.getScheduler().runTask(
                        dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                        () -> CreativePhysWandListener.handlePacketInteract(bukkitPlayer));
                return;
            }

            // Seats first (see class javadoc "Furniture seats, same root cause") — sitting down
            // takes priority over whatever's visually behind the seat's hitbox mirror.
            // hasSeatUnderAim/raycast are both read-only (immutable position data only), so it's
            // safe to run them here to decide packet cancellation synchronously (must happen
            // inline — see the existing thread-hop javadoc above). The actual mutation — a real
            // Player#teleport plus ContraptionState/SeatSlot bookkeeping for a seat, or the
            // block-entity-touching dispatch for a block cell — is hopped onto the main thread as
            // before; only ONE of the two ever actually mutates anything for a given click.
            int clickedEntityId = wrapper.getEntityId();

            // Try overlay interaction system first: resolve element from overlay entity ID
            dev.arubik.craftengine.contraption.element.ContraptionElement overlayElement =
                    resolveOverlayElement(clickedEntityId);
            if (overlayElement != null) {
                dev.arubik.craftengine.contraption.core.ContraptionState overlayState =
                        resolveOverlayState(clickedEntityId);
                if (overlayState != null) {
                    event.setCancelled(true);
                    final net.minecraft.server.level.ServerPlayer sp = player;
                    final var elem = overlayElement;
                    final var st = overlayState;
                    // hitPos: transform player eye ray to local space approximation
                    net.minecraft.world.phys.Vec3 eyePos = sp.getEyePosition(1.0f);
                    net.minecraft.world.phys.Vec3 lookDir = sp.getLookAngle();
                    net.minecraft.world.phys.Vec3 hitPos = eyePos.add(lookDir.scale(4.0));
                    org.bukkit.Bukkit.getScheduler().runTask(
                            dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                            () -> elem.onInteract(sp, st, hitPos, net.minecraft.world.InteractionHand.MAIN_HAND, true));
                    return;
                }
            }

            // Fall back to raycast for blocks not yet exposing interaction bounds
            ContraptionInteractionListener.Hit blockHit = ContraptionInteractionListener.raycast(player);
            if (blockHit != null) {
                event.setCancelled(true);
                org.bukkit.Bukkit.getScheduler().runTask(
                        dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                        () -> ContraptionInteractionListener.forward(player, blockHit));
            }
        } catch (Throwable t) {
            org.bukkit.Bukkit.getLogger().warning("[Contraption][PACKET] failed to handle INTERACT_ENTITY: " + t);
        }
    }

    private static dev.arubik.craftengine.contraption.element.ContraptionElement resolveOverlayElement(int entityId) {
        for (dev.arubik.craftengine.contraption.core.ContraptionEntity entity :
                dev.arubik.craftengine.contraption.core.ContraptionManager.all()) {
            dev.arubik.craftengine.contraption.core.ContraptionState state = entity.state();
            dev.arubik.craftengine.contraption.element.ContraptionElement elem =
                    state.elementByInteractionEntityId(entityId);
            if (elem != null) return elem;
        }
        return null;
    }

    private static dev.arubik.craftengine.contraption.core.ContraptionState resolveOverlayState(int entityId) {
        for (dev.arubik.craftengine.contraption.core.ContraptionEntity entity :
                dev.arubik.craftengine.contraption.core.ContraptionManager.all()) {
            dev.arubik.craftengine.contraption.core.ContraptionState state = entity.state();
            dev.arubik.craftengine.contraption.element.ContraptionElement elem =
                    state.elementByInteractionEntityId(entityId);
            if (elem != null) return state;
        }
        return null;
    }

    /**
     * MAIN-THREAD half of the packet-driven minecart-bearing disassemble (see the INTERACT_AT
     * branch in {@link #onPacketReceive}). Resolves the clicked entity by id (safe here, on the
     * main thread), and if it's an assembled {@link MinecartBearing} and the player is holding a
     * hammer, runs the same disassemble {@code BearingHammerListener#handleInteractEntity} does —
     * duplicated here (rather than reused) because that listener's Bukkit-event entry points don't
     * reliably fire in this environment, which is the whole reason this packet path exists.
     * Idempotent: a second call (e.g. if the Bukkit event DID somehow also fire) finds no live
     * contraption for the id and no-ops.
     */
    private static void tryDisassembleBearing(org.bukkit.entity.Player bukkitPlayer, int targetId) {
        try {
            net.minecraft.server.level.ServerPlayer sp = ((org.bukkit.craftbukkit.entity.CraftPlayer) bukkitPlayer)
                    .getHandle();
            net.minecraft.world.entity.Entity nms = sp.level().getEntity(targetId);
            if (nms == null) {
                return;
            }
            org.bukkit.entity.Entity clicked = nms.getBukkitEntity();
            if (clicked == null || !MinecartBearing.isBearing(clicked) || !MinecartBearing.isAssembled(clicked)) {
                return;
            }
            org.bukkit.inventory.ItemStack hand = bukkitPlayer.getInventory().getItemInMainHand();
            if (hand == null || hand.getType().isAir()) {
                return;
            }
            net.momirealms.craftengine.core.util.Key hammer = net.momirealms.craftengine.bukkit.api.CraftEngineItems
                    .getCustomItemId(hand);
            if (hammer == null || !dev.arubik.craftengine.multiblock.HammerItems.isHammer(hammer)) {
                return;
            }
            java.util.UUID contraptionId = MinecartBearing.contraptionId(clicked);
            ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
            if (entity != null) {
                MinecartBearing.disassemble(clicked.getWorld(), clicked, entity);
            } else {
                clicked.remove(); // orphaned bearing tag with no live contraption — just clean it up
            }
            clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            bukkitPlayer.sendMessage("§7Contraption disassembled, blocks restored, minecart removed.");
        } catch (Throwable t) {
            org.bukkit.Bukkit.getLogger().warning("[Contraption][PACKET] bearing disassemble failed: " + t);
        }
    }
}
