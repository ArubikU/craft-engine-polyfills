package dev.arubik.craftengine.virtualui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Live per-player state for an open VirtualUI screen — port of 's {@code CameraSession}
 * (the value type held per-player while a camera-locked cursor is active). Mutable; owned and
 * mutated exclusively by {@code VirtualUICameraSystem} and {@code VirtualUIClickSystem} on the
 * main thread (Folia region-thread — a session is always driven by the owning player's own task).
 */
public final class CameraSession {

    private final UUID player;
    private final VirtualUIScreen screen;
    private final org.bukkit.Location cameraLocation;
    private final org.bukkit.Location originalLocation;
    private final org.bukkit.GameMode originalGameMode;
    private final boolean wasInvulnerable;
    /** Packet-only {@code EntityType.INTERACTION} entity id kept permanently centered a short
     *  distance in front of the live camera anchor (see {@code VirtualUICameraSystem}'s marker
     *  reposition step) — the robust click-detection mechanism: any left/right click ALWAYS raycasts
     *  onto this (it's the closest thing along the look ray, closer than any widget), so
     *  {@code VirtualUIClickPacketListener} gets a guaranteed {@code INTERACT_ENTITY} packet for
     *  every click instead of depending on the client's {@code ANIMATION} packet/
     *  {@code PlayerAnimationEvent} alone (same technique this codebase already uses for chain-link
     *  and contraption secondary hitboxes — see {@code ContraptionSecondaryHitbox}/
     *  {@code ChainInteraction}). */
    private final int clickMarkerEntityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID clickMarkerUuid = UUID.randomUUID();
    /** Debounce state for the rotation-clamp corrective teleport (see
     *  {@code VirtualUIMovementPacketListener}) — while a player holds their look input against the
     *  {@code camera.max_yaw_degrees/max_pitch_degrees} cone, EVERY incoming packet keeps reporting a raw
     *  rotation past the clamp, which used to schedule a fresh {@code player.teleport()} correction
     *  EVERY single time (dozens per second) — enough teleport+ack traffic to trip Paper's own
     *  packet-rate-limit kick. Skipping a correction whose target is unchanged from the last one
     *  actually sent (plus a small time floor as a backstop) cuts that down to one correction per
     *  genuinely NEW clamp target, not one per packet. */
    private float lastCorrectedYaw = Float.NaN;
    private float lastCorrectedPitch = Float.NaN;
    private long lastCorrectionNanos = 0L;

    /** Current (unpredicted) cursor offset in "world units at camera distance" — same space as
     *  widget bounds; this is what hit-testing uses. */
    private double cursorX;
    private double cursorY;
    private float lastYaw;
    private float lastPitch;

    /** Per-tick cursor velocity, exponentially smoothed — feeds predictive rendering (see
     *  {@code VirtualUICameraSystem}'s prediction block). Not used for hit-testing; only for where
     *  the cursor GLYPH is drawn, so extrapolation never desyncs clicks from what's on screen. */
    private double velocityX;
    private double velocityY;
    private double predictedCursorX;
    private double predictedCursorY;

    private String hoveredWidgetId;
    private final Map<String, Widget> liveWidgets = new HashMap<>();
    private final Map<String, HologramLineConfig> liveHolograms = new HashMap<>();
    /** Live scrollbar values (widget id -> 0.0-1.0), seeded from each ScrollbarWidget's initial
     *  {@code value()} and mutated while a script (or the built-in drag gesture) moves it. */
    private final Map<String, Double> scrollbarValues = new HashMap<>();
    /** Live toggle values (widget id -> on/off), seeded from each ToggleWidget's initial
     *  {@code value()} and flipped on click. */
    private final Map<String, Boolean> toggleValues = new HashMap<>();
    /** Live select-widget indices (widget id -> current option index), seeded from each
     *  SelectWidget's initial {@code index()} and advanced on click/scroll. */
    private final Map<String, Integer> selectIndices = new HashMap<>();
    /** Live progress-widget values (widget id -> 0.0-1.0), seeded from each ProgressWidget's initial
     *  {@code value()} and pushed forward exclusively by {@code VirtualUI.set_progress} (no built-in
     *  gesture touches this one — see {@link Widget.ProgressWidget}). */
    private final Map<String, Double> progressValues = new HashMap<>();
    /** Active cursor-following tooltip text (port of Create's {@code TooltipArea}), or
     *  {@code null} if none — set/cleared via {@code VirtualUI.show_tooltip}/{@code hide_tooltip},
     *  rendered every tick at the cursor's own position plus {@link #tooltipOffsetX}/
     *  {@link #tooltipOffsetY}. Independent of any widget's hover state — a script decides when to
     *  show/hide it, typically from a widget's own {@code onHover}/{@code onUnhover}. */
    private String tooltipText;
    private double tooltipOffsetX;
    private double tooltipOffsetY = 0.4;
    /** Id of the scrollbar currently armed for dragging (click-to-arm/click-to-release, port of
     *  's hotbar-scrollbar gesture), or {@code null} if none. */
    private String draggingScrollbarId;
    private Object taskHandle;
    /** Hotbar slot the client last reported via a (cancelled) {@code HELD_ITEM_CHANGE} packet, or
     *  {@code -1} if none seen yet this session — used purely to derive scroll-wheel DIRECTION
     *  (the packet only ever reports an absolute new slot). See
     *  {@code VirtualUIScrollPacketListener}/{@code VirtualUIClickSystem#nudgeHoveredScrollbar}. */
    private int lastHeldSlot = -1;
    /** Server ticks since this screen opened — a time axis for script-driven animation (pulsing
     *  scale, cycling colors, whatever) via the existing {@code ${expr}} dynamic-field pipeline, e.g.
     *  {@code "<yellow>" + round((sin(VUIWidget.ticks_open() * 0.1) + 1) * 50) + "%"}. Incremented
     *  once per tick in {@code VirtualUICameraSystem.tick()}; every widget kind's context type
     *  exposes it via {@code VUIWidget.ticks_open()} (inherited by every {@code VUI*} subtype). */
    private int ticksOpen = 0;
    /** Script-settable cursor-state override (e.g. {@code "processing"}) — wins over whatever the
     *  hover/drag context would otherwise pick; {@code null} means "let context decide". Set via
     *  {@code VirtualUI.set_cursor_state(player, state)}, cleared via {@code .clear_cursor_state}. */
    private String cursorStateOverride;
    /** Debug toggle — while {@code true}, {@code VirtualUICameraSystem} traces every widget's
     *  interaction bounds (the same rectangle {@code VirtualUIClickSystem#resolveHit} hit-tests
     *  against) with particles each tick. Off by default; flipped via {@code /cep virtualui show}. */
    private boolean showHitboxes = false;
    /** Yaw/pitch the player was actually looking at the instant this screen opened — immutable for
     *  the life of the session, unlike {@link #lastYaw}/{@link #lastPitch} which track frame-to-
     *  frame deltas. The rotation-limit clamp (see {@code VirtualUIMovementPacketListener}) measures
     *  deviation from THIS, not from whatever the view currently is, so the allowed look-cone stays
     *  fixed to where the player was facing when they opened the UI. */
    private final float openYaw;
    private final float openPitch;

    public CameraSession(UUID player, VirtualUIScreen screen,
                          org.bukkit.Location cameraLocation, org.bukkit.Location originalLocation,
                          org.bukkit.GameMode originalGameMode, boolean wasInvulnerable,
                          float startYaw, float startPitch) {
        this.player = player;
        this.screen = screen;
        this.cameraLocation = cameraLocation;
        this.originalLocation = originalLocation;
        this.originalGameMode = originalGameMode;
        this.wasInvulnerable = wasInvulnerable;
        this.lastYaw = startYaw;
        this.lastPitch = startPitch;
        this.openYaw = startYaw;
        this.openPitch = startPitch;
        for (Widget w : screen.widgets()) {
            // A PlayerRenderWidget with no explicit target renders "the viewer" — resolved here,
            // once per session, so the same VirtualUIScreen shown to many players renders each
            // their own face rather than sharing one baked-in target.
            if (w instanceof Widget.PlayerRenderWidget prw && prw.targetPlayer() == null) {
                w = new Widget.PlayerRenderWidget(prw.id(), player, prw.offsetX(), prw.offsetY(), prw.offsetZ(),
                        prw.scale(), prw.onClick(), prw.onHover(), prw.onUnhover(), prw.priority());
            }
            liveWidgets.put(w.id(), w);
            if (w instanceof Widget.ScrollbarWidget sb) scrollbarValues.put(sb.id(), sb.value());
            if (w instanceof Widget.ToggleWidget tg) toggleValues.put(tg.id(), tg.value());
            if (w instanceof Widget.SelectWidget sel) selectIndices.put(sel.id(), sel.index());
            if (w instanceof Widget.ProgressWidget pr) progressValues.put(pr.id(), pr.value());
        }
    }

    public UUID player() { return player; }
    public VirtualUIScreen screen() { return screen; }
    public org.bukkit.Location cameraLocation() { return cameraLocation; }
    public org.bukkit.Location originalLocation() { return originalLocation; }
    public org.bukkit.GameMode originalGameMode() { return originalGameMode; }
    public boolean wasInvulnerable() { return wasInvulnerable; }

    public double cursorX() { return cursorX; }
    public double cursorY() { return cursorY; }
    public void setCursor(double x, double y) { this.cursorX = x; this.cursorY = y; }

    public boolean showHitboxes() { return showHitboxes; }
    public void setShowHitboxes(boolean v) { this.showHitboxes = v; }
    /** Returns {@code true} (and records this as the last-sent correction) only if {@code (yaw,
     *  pitch)} genuinely differs from the last rotation-clamp correction actually sent, or enough
     *  time (100ms) has passed since the last one — see {@link #lastCorrectedYaw}'s doc. Call this
     *  BEFORE sending a corrective teleport, never after. */
    public boolean shouldSendRotationCorrection(float yaw, float pitch) {
        long now = System.nanoTime();
        boolean sameTarget = Math.abs(yaw - lastCorrectedYaw) < 0.01f && Math.abs(pitch - lastCorrectedPitch) < 0.01f;
        boolean tooSoon = now - lastCorrectionNanos < 100_000_000L; // hard floor: at most ~10/s, period
        if (sameTarget || tooSoon) return false;
        lastCorrectedYaw = yaw;
        lastCorrectedPitch = pitch;
        lastCorrectionNanos = now;
        return true;
    }

    public int clickMarkerEntityId() { return clickMarkerEntityId; }
    public UUID clickMarkerUuid() { return clickMarkerUuid; }
    public float openYaw() { return openYaw; }
    public float openPitch() { return openPitch; }
    public float lastYaw() { return lastYaw; }
    public float lastPitch() { return lastPitch; }
    public void setLastRotation(float yaw, float pitch) { this.lastYaw = yaw; this.lastPitch = pitch; }

    public double velocityX() { return velocityX; }
    public double velocityY() { return velocityY; }
    public void setVelocity(double vx, double vy) { this.velocityX = vx; this.velocityY = vy; }

    public double predictedCursorX() { return predictedCursorX; }
    public double predictedCursorY() { return predictedCursorY; }
    public void setPredictedCursor(double x, double y) { this.predictedCursorX = x; this.predictedCursorY = y; }

    public String hoveredWidgetId() { return hoveredWidgetId; }
    public void setHoveredWidgetId(String id) { this.hoveredWidgetId = id; }

    public Map<String, Widget> liveWidgets() { return liveWidgets; }
    public Map<String, HologramLineConfig> liveHolograms() { return liveHolograms; }
    public Map<String, Double> scrollbarValues() { return scrollbarValues; }
    public Map<String, Boolean> toggleValues() { return toggleValues; }
    public Map<String, Integer> selectIndices() { return selectIndices; }
    public Map<String, Double> progressValues() { return progressValues; }

    public String tooltipText() { return tooltipText; }
    public double tooltipOffsetX() { return tooltipOffsetX; }
    public double tooltipOffsetY() { return tooltipOffsetY; }
    public void showTooltip(String text, double offsetX, double offsetY) {
        this.tooltipText = text;
        this.tooltipOffsetX = offsetX;
        this.tooltipOffsetY = offsetY;
    }
    public void hideTooltip() { this.tooltipText = null; }

    public String draggingScrollbarId() { return draggingScrollbarId; }
    public void setDraggingScrollbarId(String id) { this.draggingScrollbarId = id; }

    public Object taskHandle() { return taskHandle; }
    public void setTaskHandle(Object handle) { this.taskHandle = handle; }

    public String cursorStateOverride() { return cursorStateOverride; }
    public void setCursorStateOverride(String state) { this.cursorStateOverride = state; }

    public int lastHeldSlot() { return lastHeldSlot; }
    public void setLastHeldSlot(int slot) { this.lastHeldSlot = slot; }

    public int ticksOpen() { return ticksOpen; }
    public void incrementTicksOpen() { this.ticksOpen++; }
}
