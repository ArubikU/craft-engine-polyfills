package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.util.MNms;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import dev.arubik.craftengine.virtualui.model.CursorStateConfig;
import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import dev.arubik.craftengine.virtualui.model.Widget;
import dev.arubik.craftengine.virtualui.model.VirtualUIScreen;
import dev.arubik.craftengine.virtualui.render.ItemIdVisual;
import dev.arubik.craftengine.virtualui.render.ItemResolution;
import dev.arubik.craftengine.virtualui.render.TextVisual;
import dev.arubik.craftengine.virtualui.render.WidgetVisual;
import dev.arubik.craftengine.virtualui.render.WidgetVisualRegistry;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.core.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Owns every open VirtualUI session — camera-lock lifecycle (the "camera-locked cursor" that
 * gives  its name), per-widget renderer bookkeeping, and the per-tick render pass.
 * Sibling systems ({@link VirtualUIClickSystem}) read/mutate a session's live state but never
 * create or destroy one — {@link #show}/{@link #hide} are the only entry/exit points, matching
 * 's {@code CameraSystem.startCameraSession}/{@code closeCameraSession}.
 *
 * <p>The "lock" itself is REAL {@code GameMode.SPECTATOR} (port of 's own mechanism — a
 * {@code ClientboundSetCameraPacket}-only spoof looks right but the client silently stops
 * reporting live mouse-look rotation for its own movement packets unless it's actually in
 * Spectator) plus a teleport to a fixed point in front of the player at UI-open time;
 * {@link VirtualUIListener#onMove} then pins position back to that point every tick while letting
 * yaw/pitch through, so the player is visually frozen but can still freely look around — which is
 * what lets {@link VirtualUIClickSystem} compute a cursor offset from "how far have you turned
 * away from where you were looking when the UI opened".
 */
public final class VirtualUICameraSystem {

    private static final Map<UUID, CameraSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, VirtualUIWidgetRenderer>> WIDGET_RENDERERS = new ConcurrentHashMap<>();
    private static final Map<UUID, VirtualUIWidgetRenderer> CURSOR_RENDERERS = new ConcurrentHashMap<>();
    private static final Map<UUID, VirtualUIWidgetRenderer> TOOLTIP_RENDERERS = new ConcurrentHashMap<>();

    private VirtualUICameraSystem() {}

    public static boolean isOpen(org.bukkit.entity.Player player) {
        return SESSIONS.containsKey(player.getUniqueId());
    }

    public static CameraSession session(org.bukkit.entity.Player player) {
        return SESSIONS.get(player.getUniqueId());
    }

    public static java.util.Collection<CameraSession> allSessions() {
        return SESSIONS.values();
    }

    /** Opens {@code screen} for {@code player}, anchoring the UI in front of wherever they're
     *  currently looking — WITHOUT moving them; their position is never touched at open time.
     *  Returns {@code false} (no-op) if a session is already open — call {@link #hide} first to
     *  switch screens.
     *
     *  <p>Port of 's own mechanism for Java clients: real {@code GameMode.SPECTATOR} (no
     *  collision, invisible/non-interactive, and — confirmed against its actual reference source —
     *  the ONLY thing it does beyond that for a Java player; it doesn't fight movement either).
     *  Click still works fine while genuinely spectating: left-click is Paper's own
     *  {@code PlayerAnimationEvent} ({@code VirtualUIListener#onSwing}), right-click reads raw
     *  packets ({@link VirtualUIClickPacketListener}) — both fire regardless of gamemode; it's only
     *  Bukkit's higher-level {@code PlayerInteractEvent} that never fires for spectators. On top of
     *  that  baseline, this also freezes position while the session is open as a deliberate
     *  addition  itself doesn't have, so the player can't fly off —
     *  {@link VirtualUIMovementPacketListener} rewrites every incoming move packet's position fields
     *  back to {@link CameraSession#originalLocation()} before vanilla ever sees them (instant, no
     *  round-trip slip; {@link VirtualUIListener#onMove} is just a defensive backstop) — plus forced
     *  invulnerable so a frozen spectator can't take damage. */
    public static boolean show(org.bukkit.entity.Player player, VirtualUIScreen screen) {
        if (isOpen(player)) return false;

        // The anchor IS the player's own eye, full stop — they're never teleported anywhere, so
        // there's no separate "camera point X blocks away" to push out to (that's what
        // {@code cameraDistance} used to mean, back when the player got teleported to it; pushing
        // the anchor out by it now just placed every widget/the cursor that many blocks away from
        // wherever the player actually is standing — screenPlaneZ's own cursorDistance already
        // handles "how far in front of the eye should content sit").
        Location anchorLoc = player.getEyeLocation();

        org.bukkit.GameMode originalGameMode = player.getGameMode();
        boolean wasInvulnerable = player.isInvulnerable();
        Location originalLocation = player.getLocation();

        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        player.setInvulnerable(true);

        CameraSession session = new CameraSession(
                player.getUniqueId(), screen, anchorLoc, originalLocation,
                originalGameMode, wasInvulnerable, anchorLoc.getYaw(), anchorLoc.getPitch());
        SESSIONS.put(player.getUniqueId(), session);
        WIDGET_RENDERERS.put(player.getUniqueId(), new ConcurrentHashMap<>());
        CURSOR_RENDERERS.put(player.getUniqueId(), new VirtualUIWidgetRenderer());
        TOOLTIP_RENDERERS.put(player.getUniqueId(), new VirtualUIWidgetRenderer());

        if (screen.playerInvisibleOnStart()) {
            player.setInvisible(true);
        }

        applyHeldItemOverride(player);
        spawnClickMarker(BukkitAdaptor.adapt(player), session);

        renderTick(player, session);
        return true;
    }

    private static float clickMarkerSize() {
        return (float) VirtualUIConfig.get().clickMarkerSize();
    }

    /** This whole coordinate system is FLAT/orthographic, not a true perspective projection — a
     *  given (x, y) is a literal world-unit offset along the anchor's right/up axes, held CONSTANT
     *  regardless of z-depth (see {@link #worldPosFor}). That means two entities sharing the same
     *  (x, y) but sitting at very different depths are NOT on the same line-of-sight ray from the
     *  eye — they diverge to different visual angles the closer one is (angle ~= atan(offset /
     *  depth)). The click marker was originally placed much closer than the widget plane
     *  (0.4 vs. the ~2.0 default {@code cursor.distance}) purely to win the "nearest thing on the
     *  ray" race against widget displays — but at that big a depth gap, the SAME (cx, cy) the
     *  cursor glyph/widgets use put the marker at a visibly different angle than the crosshair
     *  actually is once the cursor drifts from center, which read as clicks/hitboxes "not always
     *  registering" the further the cursor moved. Fixed by placing the marker at the EXACT SAME
     *  depth the cursor glyph itself renders at ({@link #cursorRenderDistance()}) — close enough in
     *  front of the widget plane (still wins the nearest-entity race, thanks to
     *  {@link #cursorFrontMargin()}) while sharing geometry with what's actually drawn on screen, so
     *  the marker and the visible cursor dot are ALWAYS the same point, at every offset. */
    private static double cursorRenderDistance() {
        return VirtualUIConfig.get().cursorDistance() - cursorFrontMargin();
    }

    /** The raw/predicted cursor offset as tracked by {@link CameraSession} — same choice
     *  {@code VirtualUIClickSystem#resolveHit} makes. Defined in the same LOGICAL, unscaled
     *  screen-space every widget's {@code offsetX}/{@code offsetY} is — always feed this into
     *  {@link #screenPosFor}, never {@link #worldPosFor} directly, so depth-scaling happens
     *  consistently (see {@link #scaleForDepth}). */
    private static double[] cursorLocalPos(CameraSession session) {
        boolean predicted = VirtualUIConfig.get().predictionEnabled();
        return new double[] {
                predicted ? session.predictedCursorX() : session.cursorX(),
                predicted ? session.predictedCursorY() : session.cursorY()
        };
    }

    /** PERSPECTIVE FIX: {@link #worldPosFor} places {@code (x, y)} as a literal world-unit offset
     *  along the anchor's right/up axes, held CONSTANT regardless of {@code z} — it does NOT do
     *  perspective projection on its own. Two points sharing the same (x, y) but rendered at
     *  different depths are NOT on the same line-of-sight ray from the eye — they diverge to
     *  different visual angles the closer one is (angle ~= atan(offset / depth)). This is what
     *  {@link #screenPosFor} corrects for, uniformly, for every widget/cursor/marker/tooltip — the
     *  generic version of this scaling math, for any {@code (x, y)}, not just the cursor's own
     *  offset. Every widget used to skip this (their {@code offsetX}/
     *  {@code offsetY} went straight into {@link #worldPosFor} unscaled), which was fine as long as
     *  they all rendered at nearly the same depth, but a widget with real {@code offsetZ} layering
     *  (e.g. a background/frame icon placed behind another widget via a negative {@code z}) was
     *  QUIETLY placed at the wrong angle relative to the foreground content it's supposed to line up
     *  with — the same bug the cursor/marker/tooltip had, just smaller since offsetZ deltas are
     *  usually small. Routing EVERYTHING (widgets included) through this makes the whole screen
     *  consistently "flat" — every part of it reads as being exactly where its (x, y) says it is,
     *  regardless of which depth it happens to render at, matching how the cursor itself already
     *  behaves. */
    private static double[] scaleForDepth(double x, double y, double depth) {
        double reference = VirtualUIConfig.get().cursorDistance();
        double scale = reference > 1.0e-6 ? depth / reference : 1.0;
        return new double[] { x * scale, y * scale };
    }

    /** The ONE place screen-space {@code (x, y, z)} becomes a world position — every widget part,
     *  the cursor glyph, the click marker, the tooltip, and the hitbox debug outlines all go through
     *  this now, so the whole screen shares one consistent, perspective-correct placement rule (see
     *  {@link #scaleForDepth}). {@code z} keeps the existing more-negative-is-further-away
     *  convention (see {@link #worldPosFor}'s own doc on that sign). */
    private static double[] screenPosFor(CameraSession session, double x, double y, double z) {
        double[] scaled = scaleForDepth(x, y, -z);
        return worldPosFor(session, scaled[0], scaled[1], z);
    }

    /** Spawns {@code session}'s packet-only {@code EntityType.INTERACTION} click-detection marker
     *  (see {@link CameraSession#clickMarkerEntityId()}'s doc) — same technique as
     *  {@code ContraptionSecondaryHitbox}/{@code ChainInteraction} elsewhere in this codebase. */
    private static void spawnClickMarker(Player cePlayer, CameraSession session) {
        double[] cursor = cursorLocalPos(session);
        double[] pos = screenPosFor(session, cursor[0], cursor[1], -cursorRenderDistance());
        java.util.List<Object> metadata = new java.util.ArrayList<>();
        float markerSize = clickMarkerSize();
        net.momirealms.craftengine.bukkit.entity.data.InteractionData.Width
                .addEntityData(markerSize, metadata);
        net.momirealms.craftengine.bukkit.entity.data.InteractionData.Height
                .addEntityData(markerSize, metadata);
        net.momirealms.craftengine.bukkit.entity.data.InteractionData.Response
                .addEntityData(true, metadata);
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                session.clickMarkerEntityId(), session.clickMarkerUuid(), pos[0], pos[1], pos[2],
                0f, 0f, net.minecraft.world.entity.EntityType.INTERACTION, 0,
                net.minecraft.world.phys.Vec3.ZERO, 0.0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(session.clickMarkerEntityId(), metadata);
        cePlayer.sendPackets(java.util.List.of(add, data), false);
    }

    private static void repositionClickMarker(Player cePlayer, CameraSession session) {
        double[] cursor = cursorLocalPos(session);
        double[] pos = screenPosFor(session, cursor[0], cursor[1], -cursorRenderDistance());
        cePlayer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                session.clickMarkerEntityId(), pos[0], pos[1], pos[2], 0f, 0f, false), false);
    }

    private static void despawnClickMarker(Player cePlayer, CameraSession session) {
        cePlayer.sendPacket(MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                it.unimi.dsi.fastutil.ints.IntList.of(session.clickMarkerEntityId())), false);
    }

    private static final org.bukkit.Particle.DustOptions HITBOX_DUST =
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.YELLOW, 1.0f);
    private static final org.bukkit.Particle.DustOptions MARKER_DUST =
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.0f);
    private static final int HITBOX_SAMPLES_PER_EDGE = 8;

    /** {@code /cep virtualui show} debug aid — traces every widget's interaction rectangle (the
     *  EXACT bounds {@link VirtualUIClickSystem#resolveHit} hit-tests against — same offsetX/Y/
     *  width/height, same plane) with yellow dust particles, plus the click marker's own box in
     *  red, so "why didn't that click register" is visible instead of guessed at. Client-only
     *  (spawnParticle's own per-player overload), so this never touches other players' views. */
    private static void drawHitboxes(org.bukkit.entity.Player player, CameraSession session) {
        org.bukkit.World world = player.getWorld();
        for (Widget w : session.liveWidgets().values()) {
            if (w.width() <= 0 || w.height() <= 0) continue;
            double halfW = w.width() / 2.0, halfH = w.height() / 2.0;
            double z = screenPlaneZ(w.offsetZ());
            drawRectOutline(player, world, session, w.offsetX() - halfW, w.offsetY() - halfH,
                    w.offsetX() + halfW, w.offsetY() + halfH, z, HITBOX_DUST);
        }
        double[] cursor = cursorLocalPos(session);
        double markerHalf = clickMarkerSize() / 2.0;
        drawRectOutline(player, world, session, cursor[0] - markerHalf, cursor[1] - markerHalf,
                cursor[0] + markerHalf, cursor[1] + markerHalf, -cursorRenderDistance(), MARKER_DUST);
    }

    private static void drawRectOutline(org.bukkit.entity.Player player, org.bukkit.World world, CameraSession session,
                                         double x0, double y0, double x1, double y1, double z,
                                         org.bukkit.Particle.DustOptions dust) {
        drawEdge(player, world, session, x0, y0, x1, y0, z, dust);
        drawEdge(player, world, session, x1, y0, x1, y1, z, dust);
        drawEdge(player, world, session, x1, y1, x0, y1, z, dust);
        drawEdge(player, world, session, x0, y1, x0, y0, z, dust);
    }

    private static void drawEdge(org.bukkit.entity.Player player, org.bukkit.World world, CameraSession session,
                                  double x0, double y0, double x1, double y1, double z,
                                  org.bukkit.Particle.DustOptions dust) {
        for (int i = 0; i <= HITBOX_SAMPLES_PER_EDGE; i++) {
            double t = (double) i / HITBOX_SAMPLES_PER_EDGE;
            double[] pos = screenPosFor(session, x0 + (x1 - x0) * t, y0 + (y1 - y0) * t, z);
            player.spawnParticle(org.bukkit.Particle.DUST, pos[0], pos[1], pos[2], 1, 0, 0, 0, 0, dust);
        }
    }

    /** Closes {@code player}'s session (if any): despawns every widget display, restores their
     *  real gamemode/location/invulnerability/visibility, and — if the screen declared one — runs
     *  its {@code onCloseRef} script action. */
    public static void hide(org.bukkit.entity.Player player) {
        CameraSession session = SESSIONS.remove(player.getUniqueId());
        if (session == null) return;

        Player cePlayer = BukkitAdaptor.adapt(player);
        Map<String, VirtualUIWidgetRenderer> renderers = WIDGET_RENDERERS.remove(player.getUniqueId());
        if (renderers != null) renderers.values().forEach(r -> r.despawn(cePlayer));
        VirtualUIWidgetRenderer cursor = CURSOR_RENDERERS.remove(player.getUniqueId());
        if (cursor != null) cursor.despawn(cePlayer);
        VirtualUIWidgetRenderer tooltip = TOOLTIP_RENDERERS.remove(player.getUniqueId());
        if (tooltip != null) tooltip.despawn(cePlayer);
        despawnClickMarker(cePlayer, session);

        player.teleport(session.originalLocation());
        player.setGameMode(session.originalGameMode());
        player.setInvulnerable(session.wasInvulnerable());
        player.setInvisible(false);
        // Forces a full real-inventory resync, overwriting any applyHeldItemOverride(...) slot
        // fake from show() — simplest correct "undo" since it doesn't need to remember what was
        // really there before (which may have changed mid-session anyway).
        player.updateInventory();

        if (session.screen().onCloseRef() != null && !session.screen().onCloseRef().isBlank()) {
            VirtualUIClickSystem.runScriptRef(session.screen().onCloseRef(), player, session, null);
        }
    }

    /** CLIENT-ONLY: if {@code camera.hide_held_item_as} is configured, visually swaps the player's
     *  held hotbar slot AND offhand slot to that item — see {@code MNms}'s doc on why a fake
     *  {@code ClientboundContainerSetSlotPacket} (not an equipment packet) is what actually changes
     *  a player's OWN first-person hand render. Their real inventory is never touched. */
    private static void applyHeldItemOverride(org.bukkit.entity.Player player) {
        String id = VirtualUIConfig.get().hideHeldItemAs();
        if (id == null || id.isBlank()) return;
        net.minecraft.world.item.ItemStack fake = ItemResolution.resolve(id);
        if (fake == null) return;
        net.minecraft.server.level.ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        int hotbarSlot = 36 + player.getInventory().getHeldItemSlot(); // player-inventory-menu numbering
        nmsPlayer.connection.send((net.minecraft.network.protocol.Packet)
                MNms.INSTANCE.constructor$ClientboundContainerSetSlotPacket(0, 0, hotbarSlot, fake));
        nmsPlayer.connection.send((net.minecraft.network.protocol.Packet)
                MNms.INSTANCE.constructor$ClientboundContainerSetSlotPacket(0, 0, 45, fake)); // offhand
    }

    public static void hideAll() {
        for (UUID id : java.util.List.copyOf(SESSIONS.keySet())) {
            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(id);
            if (p != null) hide(p);
        }
    }

    private static int debugTickCounter = 0;

    /** Called once per server tick for every open session — both the cursor offset AND the panel's
     *  own facing direction are updated event-driven, straight off raw rotation (see
     *  {@link #accumulateCursorFromRawRotation}); this loop just updates hover state, drags any
     *  armed scrollbar, and re-renders every widget + the cursor against whatever those two
     *  currently are. Deliberately does NOT force the player's look direction back to any fixed
     *  value every tick — the head is genuinely free to turn (within {@code max_yaw_degrees/max_pitch_degrees},
     *  see {@code VirtualUIMovementPacketListener}), and the whole panel re-centers to follow
     *  wherever they're currently looking rather than staying pinned to wherever they were facing
     *  at open time — the cursor's own (x, y) offset is a separate, smaller-scale accumulator
     *  layered on top of that live-following plane, clamped via {@code maxOffsetX}/{@code
     *  maxOffsetY} in {@link #accumulateCursorFromRawRotation}. */
    public static void tick() {
        debugTickCounter++;
        for (CameraSession session : SESSIONS.values()) {
            org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(session.player());
            if (player == null || !player.isOnline()) { continue; }
            session.incrementTicksOpen();
            VirtualUIClickSystem.updateHover(session, player);
            VirtualUIClickSystem.updateDraggingScrollbar(session, player);
            renderTick(player, session);
        }
    }

    /** Accumulates cursor offset from a RAW incoming rotation ({@code yaw}/{@code pitch} straight
     *  off the wire, already clamped to the rotation cone — see
     *  {@code VirtualUIMovementPacketListener} — never {@code player.getLocation()}) and, from
     *  there, ALSO live-updates {@link CameraSession#cameraLocation()}'s own facing direction to
     *  match (see below). The player's canonical Location keeps its POSITION permanently frozen
     *  (see that listener's doc) once a session is open, so {@code player.getLocation()} would
     *  report the same constant X/Y/Z forever, but rotation is genuinely free within the cone —
     *  this is the ONLY place that ever sees it, giving this system full ownership of both "what
     *  does the cursor do right now" and "which way is the panel currently facing" instead of
     *  depending on the entity's mutable server-side state staying in sync with either. */
    static void accumulateCursorFromRawRotation(org.bukkit.entity.Player player, CameraSession session, float yaw, float pitch) {
        boolean logThisTick = debugTickCounter % 20 == 0;
        double deltaYaw = normalizeDegrees(yaw - session.lastYaw());
        double deltaPitch = pitch - session.lastPitch();
        double sensX = VirtualUIConfig.get().sensitivityX();
        double sensY = VirtualUIConfig.get().sensitivityY();
        VirtualUIScreen screen = session.screen();
        double maxX = screen.maxOffsetX() > 0 ? screen.maxOffsetX() : VirtualUIConfig.get().maxOffsetX();
        double maxY = screen.maxOffsetY() > 0 ? screen.maxOffsetY() : VirtualUIConfig.get().maxOffsetY();
        double prevX = session.cursorX(), prevY = session.cursorY();
        double nextX = clamp(prevX - deltaYaw * sensX, -maxX, maxX);
        double nextY = clamp(prevY - deltaPitch * sensY, -maxY, maxY);
        session.setCursor(nextX, nextY);
        session.setLastRotation(yaw, pitch);

        // Live-track the anchor's own facing direction to whatever the player is currently looking
        // at (within the rotation-cone clamp — "yaw"/"pitch" here are already post-clamp, see
        // VirtualUIMovementPacketListener), so the whole panel stays centered in front of the
        // player as they turn their head instead of staying pinned to wherever they were facing at
        // open time. cameraLocation's X/Y/Z (the frozen eye position) is untouched — only its
        // yaw/pitch move, which is exactly what worldPosFor's forward/right basis reads. The
        // cursor's own (x, y) offset above is a SEPARATE accumulator layered on top of that live
        // plane, so it keeps moving independently rather than being swallowed by the re-centering.
        session.cameraLocation().setYaw(yaw);
        session.cameraLocation().setPitch(pitch);

        if (logThisTick) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                    "[VirtualUI][debug] player=" + player.getName()
                            + " gameMode=" + player.getGameMode()
                            + " loc.yaw=" + yaw + " loc.pitch=" + pitch
                            + " deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch
                            + " cursor=(" + nextX + "," + nextY + ")"
                            + " predicted=(" + session.predictedCursorX() + "," + session.predictedCursorY() + ")");
        }

        // Predictive cursor ( port): smooth the raw per-tick delta into a velocity, then
        // extrapolate the DRAWN cursor a fraction of a tick ahead of it. Hit-testing/clicks always
        // use the raw (nextX, nextY) above, never this — a click must land exactly where the
        // visible cursor was, so prediction only ever affects what's rendered, not what registers.
        if (VirtualUIConfig.get().predictionEnabled()) {
            double alpha = VirtualUIConfig.get().predictionSmoothing();
            double rawVx = nextX - prevX, rawVy = nextY - prevY;
            double vx = session.velocityX() * (1 - alpha) + rawVx * alpha;
            double vy = session.velocityY() * (1 - alpha) + rawVy * alpha;
            session.setVelocity(vx, vy);
            double strength = VirtualUIConfig.get().predictionStrength();
            session.setPredictedCursor(
                    clamp(nextX + vx * strength, -maxX, maxX),
                    clamp(nextY + vy * strength, -maxY, maxY));
        } else {
            session.setPredictedCursor(nextX, nextY);
        }

        // Click marker reposition moved HERE (event-driven, same as the cursor offset itself) —
        // it used to only happen once per server tick in renderTick(), up to ~50ms behind the
        // logical cursor position on a fast/rapid head turn (raw rotation packets can arrive
        // faster than 20/s). The marker is what a click's raycast actually has to land on, so any
        // staleness there is exactly the kind of "sometimes doesn't register" flakiness a tick-rate
        // update introduces — repositioning it on every packet instead removes that window entirely.
        repositionClickMarker(BukkitAdaptor.adapt(player), session);
    }

    private static void renderTick(org.bukkit.entity.Player player, CameraSession session) {
        Player cePlayer = BukkitAdaptor.adapt(player);
        VirtualUIScreen screen = session.screen();

        // Click marker reposition is now event-driven (see accumulateCursorFromRawRotation) — no
        // per-tick call needed here anymore, it's already at the latest position by the time this
        // runs. Hitbox debug outlines stay tick-based (particles, purely visual, no reason to spam
        // them per-packet).
        if (session.showHitboxes()) drawHitboxes(player, session);

        Map<String, VirtualUIWidgetRenderer> renderers = WIDGET_RENDERERS.get(player.getUniqueId());
        if (renderers == null) return;

        for (Widget widget : session.liveWidgets().values()) {
            Widget effective = resolveEffectiveWidget(session, widget);
            boolean dragging = widget.id().equals(session.draggingScrollbarId());
            boolean hovered = widget.id().equals(session.hoveredWidgetId()) || dragging;
            double liveValue = effective instanceof Widget.ScrollbarWidget sbv ? sbv.value()
                    : effective instanceof Widget.ToggleWidget tgv ? (tgv.value() ? 1.0 : 0.0)
                    : effective instanceof Widget.SelectWidget selv ? selv.index()
                    : effective instanceof Widget.ProgressWidget prv ? prv.value() : 0.0;
            int slotIdx = effective instanceof Widget.SlotWidget slw ? slw.slotIndex() : -1;
            String stringValue = effective instanceof Widget.SelectWidget selsv ? selsv.current() : "";
            dev.arubik.craftengine.virtualui.model.WidgetRenderContext renderCtx =
                    new dev.arubik.craftengine.virtualui.model.WidgetRenderContext(
                            widget.id(), hovered, dragging, liveValue, slotIdx, session.ticksOpen(), stringValue);
            java.util.List<dev.arubik.craftengine.virtualui.render.PositionedVisual> parts =
                    WidgetVisualRegistry.build(effective, new Quaternionf(), hovered, player, renderCtx);
            double[] thumbPos = effective instanceof Widget.ScrollbarWidget sb ? thumbOffset(sb) : null;
            double baseX = thumbPos != null ? thumbPos[0] : effective.offsetX();
            double baseY = thumbPos != null ? thumbPos[1] : effective.offsetY();
            for (int i = 0; i < parts.size(); i++) {
                dev.arubik.craftengine.virtualui.render.PositionedVisual part = parts.get(i);
                VirtualUIWidgetRenderer renderer = renderers.computeIfAbsent(
                        widget.id() + '#' + i, id -> new VirtualUIWidgetRenderer());
                double[] pos = screenPosFor(session, baseX + part.dx(), baseY + part.dy(), screenPlaneZ(effective.offsetZ() + part.dz()));
                renderer.render(cePlayer, part.visual(), pos[0], pos[1], pos[2]);
            }
        }

        // cursor glyph — rendered at the PREDICTED position (see updateCursorFromLook). Its
        // appearance is picked from the cursor-state provider (see resolveCursorVisual): a script
        // override wins, then scrollbar-drag orientation, then hover, else "normal" — for which an
        // explicit per-screen cursorItemId/cursorIcon overrides the virtualui.yml default.
        VirtualUIWidgetRenderer cursorRenderer = CURSOR_RENDERERS.get(player.getUniqueId());
        if (cursorRenderer != null) {
            // "cx, cy" (reference-depth units) still feed resolveCursorVisual's own hologram-text
            // offset fields (a separate, cosmetic per-line-transform concern, not world placement);
            // the actual world position goes through screenPosFor, same as every widget now, so the
            // glyph renders at the correct visual angle for where the cursor logically is.
            double[] cursor = cursorLocalPos(session);
            double cx = cursor[0], cy = cursor[1];
            double cursorDistance = VirtualUIConfig.get().cursorDistance();
            double[] pos = screenPosFor(session, cx, cy, -cursorRenderDistance());
            WidgetVisual cursorVisual = resolveCursorVisual(session, screen, cx, cy, cursorDistance);
            cursorRenderer.render(cePlayer, cursorVisual, pos[0], pos[1], pos[2]);
            if (!SESSIONS.isEmpty() && debugTickCounter % 20 == 0) {
                dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                        "[VirtualUI][debug] cursor render pos=(" + pos[0] + "," + pos[1] + "," + pos[2]
                                + ") anchorYaw=" + session.cameraLocation().getYaw()
                                + " state=" + activeCursorState(session));
            }
        }

        // Cursor-following tooltip (port of Create's TooltipArea) — a script-controlled floating
        // label independent of any widget, shown/hidden via VirtualUI.show_tooltip/hide_tooltip,
        // typically from a widget's onHover/onUnhover. Tracks the same predicted cursor position
        // the cursor glyph itself uses, offset by tooltipOffsetX/Y.
        VirtualUIWidgetRenderer tooltipRenderer = TOOLTIP_RENDERERS.get(player.getUniqueId());
        if (tooltipRenderer != null) {
            if (session.tooltipText() != null) {
                // Half the cursor's own front margin — stays ahead of every widget's z=0 plane
                // without out-fighting the cursor glyph itself for the exact same depth.
                double tooltipDistance = VirtualUIConfig.get().cursorDistance() - cursorFrontMargin() * 0.5;
                double[] cursor = cursorLocalPos(session);
                // tooltipOffsetX/Y is a small fixed nudge (e.g. "just above the cursor") added in
                // the same unscaled logical space as the cursor itself, THEN the whole thing goes
                // through screenPosFor together — consistent with every other screen-space point now.
                double tx = cursor[0] + session.tooltipOffsetX(), ty = cursor[1] + session.tooltipOffsetY();
                double[] pos = screenPosFor(session, tx, ty, -tooltipDistance);
                HologramLineConfig style = HologramLineConfig.text(session.tooltipText(), tx, ty, -tooltipDistance, 0.6f);
                tooltipRenderer.render(cePlayer, new TextVisual(style, new Quaternionf()), pos[0], pos[1], pos[2]);
            } else {
                tooltipRenderer.despawn(cePlayer); // no-op if it was never spawned
            }
        }
    }

    /** Picks the active named cursor state (script override > scrollbar-drag orientation > hover >
     *  {@code "normal"}) and resolves it to a renderable visual: {@code "normal"} first checks the
     *  screen's own {@code cursorItemId}/{@code cursorIcon} (per-screen override), then any other
     *  state (including {@code "normal"} with no per-screen override) checks
     *  {@code VirtualUIConfig}'s {@code cursor.states.<name>} entry, falling back to the engine's
     *  built-in default text glyph if nothing is configured for that name. */
    private static WidgetVisual resolveCursorVisual(CameraSession session, VirtualUIScreen screen,
                                                      double cx, double cy, double cursorDistance) {
        String state = activeCursorState(session);

        if ("normal".equals(state)) {
            if (screen.cursorItemId() != null && !screen.cursorItemId().isBlank()) {
                return new ItemIdVisual(screen.cursorItemId(), 0.3f, new Quaternionf());
            }
            if (screen.cursorIcon() != null) {
                return new TextVisual(screen.cursorIcon().withOffset(cx, cy, -cursorDistance), new Quaternionf());
            }
        }

        CursorStateConfig configured = VirtualUIConfig.get().cursorState(state);
        if (configured == null && !"normal".equals(state)) configured = VirtualUIConfig.get().cursorState("normal");
        if (configured != null) {
            if (configured.isItem()) {
                return new ItemIdVisual(configured.itemId(), 0.3f, new Quaternionf());
            }
            return new TextVisual(configured.text().withOffset(cx, cy, -cursorDistance), new Quaternionf());
        }

        HologramLineConfig fallback = VirtualUIConfig.get().cursorDisplay().withOffset(cx, cy, -cursorDistance);
        return new TextVisual(fallback, new Quaternionf());
    }

    /** {@code cursorStateOverride} wins outright (see {@code VirtualUI.set_cursor_state}); else a
     *  dragging scrollbar picks {@code "scroll_vertical"}/{@code "scroll_horizontal"} by its own
     *  orientation; else a hovered widget picks its own {@code .hover_state(name)} override if it
     *  declared one (see {@code VirtualUIScreen#hoverCursorStates}), else the generic
     *  {@code "hover"}; else {@code "normal"}. */
    private static String activeCursorState(CameraSession session) {
        if (session.cursorStateOverride() != null && !session.cursorStateOverride().isBlank()) {
            return session.cursorStateOverride();
        }
        if (session.draggingScrollbarId() != null) {
            Widget dragging = session.liveWidgets().get(session.draggingScrollbarId());
            if (dragging instanceof Widget.ScrollbarWidget sb) {
                return sb.vertical() ? "scroll_vertical" : "scroll_horizontal";
            }
        }
        if (session.hoveredWidgetId() != null) {
            String perWidget = session.screen().hoverCursorStates().get(session.hoveredWidgetId());
            return perWidget != null && !perWidget.isBlank() ? perWidget : "hover";
        }
        return "normal";
    }

    /** Applies any per-widget dynamic overrides before it's handed to the renderer: a
     *  {@code VirtualUI.change_hologram(...)} style-swap for a Button/Label, or a scrollbar's live
     *  (possibly mid-drag) value in place of its immutable initial one. */
    private static Widget resolveEffectiveWidget(CameraSession session, Widget widget) {
        HologramLineConfig override = session.liveHolograms().get(widget.id());
        if (override != null && widget instanceof Widget.ButtonWidget b) {
            return new Widget.ButtonWidget(b.id(), override, b.width(), b.height(), b.onClick(), b.onHover(), b.onUnhover(), b.priority());
        }
        if (override != null && widget instanceof Widget.LabelWidget l) {
            return new Widget.LabelWidget(l.id(), override);
        }
        if (widget instanceof Widget.ScrollbarWidget sb) {
            double live = session.scrollbarValues().getOrDefault(sb.id(), sb.value());
            return new Widget.ScrollbarWidget(sb.id(), sb.vertical(), live, sb.offsetX(), sb.offsetY(), sb.offsetZ(),
                    sb.width(), sb.height(), sb.tracker(), sb.thumbText(),
                    sb.onChange(), sb.onHover(), sb.onUnhover(), sb.priority());
        }
        if (widget instanceof Widget.ToggleWidget tg) {
            boolean live = session.toggleValues().getOrDefault(tg.id(), tg.value());
            return new Widget.ToggleWidget(tg.id(), live, tg.onLabel(), tg.offLabel(), tg.width(), tg.height(),
                    tg.onIcon(), tg.offIcon(), tg.iconOffsetX(), tg.iconOffsetY(), tg.iconScale(),
                    tg.onChange(), tg.onHover(), tg.onUnhover(), tg.priority());
        }
        if (widget instanceof Widget.SelectWidget sel) {
            int live = session.selectIndices().getOrDefault(sel.id(), sel.index());
            return new Widget.SelectWidget(sel.id(), sel.options(), live, sel.template(),
                    sel.width(), sel.height(), sel.onChange(), sel.onHover(), sel.onUnhover(), sel.priority());
        }
        if (widget instanceof Widget.ProgressWidget pr) {
            double live = session.progressValues().getOrDefault(pr.id(), pr.value());
            return new Widget.ProgressWidget(pr.id(), live, pr.offsetX(), pr.offsetY(), pr.offsetZ(),
                    pr.width(), pr.height(), pr.vertical(), pr.tracker(), pr.onHover(), pr.onUnhover(), pr.priority());
        }
        return widget;
    }

    /** The scrollbar's thumb-space (x, y) offset: {@code value} shifts it along its track's own
     *  axis (the interaction area's width for a horizontal bar, height for a vertical one),
     *  centered on the widget's declared {@code offsetX}/{@code offsetY} — so the rendered thumb
     *  entity actually travels across the track instead of sitting fixed while only a text
     *  character changed (see {@code Widget.ScrollbarWidget}'s doc on width/height being the
     *  interaction area the thumb is mapped across). */
    private static double[] thumbOffset(Widget.ScrollbarWidget sb) {
        double t = Math.max(0.0, Math.min(1.0, sb.value())) - 0.5;
        if (sb.vertical()) {
            return new double[] { sb.offsetX(), sb.offsetY() + t * sb.height() };
        }
        return new double[] { sb.offsetX() + t * sb.width(), sb.offsetY() };
    }

    /** A widget's declared {@code offsetZ} is a small tweak on top of a mandatory base push in
     *  front of the anchor — reusing {@code VirtualUIConfig.cursorDistance()} as that base, exactly
     *  like 's own reference implementation adds its cursor hologram's configured
     *  {@code distance} to every hologram/widget's Z before placing it (see
     *  {@code CursorSystem}'s widget-restore code: {@code forward*distance + right*offsetX +
     *  forward*offsetZ}). Without this, {@code offsetZ}'s default of 0 places a widget exactly AT
     *  the camera anchor itself — degenerate/invisible up close in first person, and only visible
     *  in third person (F5) once the pulled-back camera finally has room to see past its own head. */
    /** The cursor glyph always renders this much closer to the camera than the z=0 widget plane
     *  (see {@link #screenPlaneZ}'s clamp) — guarantees nothing a widget/script can configure ever
     *  renders in front of the cursor, matching every other cursor-locked UI's expected z-order.
     *  Configurable via {@code cursor.front_margin} (see {@code VirtualUIConfig#cursorFrontMargin}),
     *  reload live with {@code /cep virtualui reload}. */
    private static double cursorFrontMargin() {
        return VirtualUIConfig.get().cursorFrontMargin();
    }

    private static double screenPlaneZ(double offsetZ) {
        // Clamped to <= 0: a widget's z may push it further BEHIND the z=0 plane (more negative,
        // e.g. a background/frame layer) but never closer to the camera than that plane — otherwise
        // a widget could configure its way in front of the cursor glyph itself, which must always
        // stay the frontmost thing on screen (see cursorFrontMargin()).
        return Math.min(offsetZ, 0.0) - VirtualUIConfig.get().cursorDistance();
    }

    /** World position (in front of the locked anchor, in the same flat screen-plane basis
     *  {@link #renderTick} uses) for a given widget-space (x,y,z) — shared with
     *  {@link VirtualUIClickSystem} so hit-testing and rendering never drift apart. */
    /** World position (in front of the anchor, on its OWN screen-plane basis) for widget-space
     *  {@code (x, y, z)} — {@code x} along the anchor's right axis, {@code y} along its CAMERA-
     *  RELATIVE up axis (not world-up — see below), {@code z} depth (more negative = further away,
     *  see the sign note below).
     *
     *  <p>PITCH BUG FIX: this used to build right/forward from YAW ONLY, silently treating the
     *  anchor as if it were always looking dead-level (pitch 0) no matter what {@code
     *  anchor.getPitch()} actually was — {@code y} was applied as a flat world-Y offset instead of
     *  along a real "up, relative to where you're looking" axis. That was invisible while the panel
     *  was pinned to wherever the player was facing at OPEN time (pitch barely drifted from
     *  whatever it started at, in the old fixed-anchor design), but once the anchor started
     *  live-tracking the player's current look direction every tick (see
     *  {@code accumulateCursorFromRawRotation}), any real pitch — which is to say, almost all
     *  normal play — made the whole basis wrong: widgets, the cursor glyph, and the click marker
     *  were all still being placed as though the player were looking perfectly level, so the second
     *  they tilted their head at all, everything drifted out of alignment with where they were
     *  actually looking. Fixed by building a proper 3-axis camera basis (forward/right/up) from
     *  BOTH yaw and pitch — right stays level (no roll, matching vanilla's own camera model), up
     *  tilts to match pitch exactly like a real first-person camera's "up on screen" does. */
    static double[] worldPosFor(CameraSession session, double x, double y, double z) {
        Location anchor = session.cameraLocation();
        double yawRad = Math.toRadians(anchor.getYaw());
        double pitchRad = Math.toRadians(anchor.getPitch());
        double cosYaw = Math.cos(yawRad), sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad), sinPitch = Math.sin(pitchRad);

        // Forward: Minecraft's own convention (yaw 0 = +Z, positive pitch = looking down).
        double fx = -sinYaw * cosPitch, fy = -sinPitch, fz = cosYaw * cosPitch;
        // Right: horizontal only — no roll, same as the real camera.
        double rx = cosYaw, rz = sinYaw;
        // Up: camera-relative, tilts with pitch (at pitch=0 this is exactly world-up (0,1,0), which
        // is why nothing looked wrong before — every session used to sit near pitch 0 in practice).
        double ux = -sinYaw * sinPitch, uy = cosPitch, uz = cosYaw * sinPitch;

        // z ("depth from anchor") is documented as more-negative-is-further-away (Widget's own
        // doc) — i.e. more negative should push FURTHER ALONG the anchor's forward direction, away
        // from the camera. That means the forward contribution is `-fx*z`/`-fz*z`, not `+fx*z`: with
        // the '+' sign a typical negative offsetZ (every built-in widget/cursor uses one) placed the
        // entity BEHIND the anchor instead of in front — invisible in first person (the camera sits
        // exactly at the anchor) and visible only in third person (F5), where the pulled-back camera
        // can see past its own head. Fixed here so first-person is the normal, correct view.
        return new double[] {
                anchor.getX() + rx * x + ux * y - fx * z,
                anchor.getY() + uy * y - fy * z,
                anchor.getZ() + rz * x + uz * y - fz * z
        };
    }

    private static double normalizeDegrees(double d) {
        d %= 360.0;
        if (d >= 180.0) d -= 360.0;
        if (d < -180.0) d += 360.0;
        return d;
    }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
}
