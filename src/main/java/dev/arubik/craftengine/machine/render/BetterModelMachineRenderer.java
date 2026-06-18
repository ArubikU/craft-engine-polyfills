package dev.arubik.craftengine.machine.render;

import java.util.function.DoubleSupplier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.DummyTracker;
import org.bukkit.entity.Player;

/**
 * Reusable, guarded BetterModel render driver for machine block-entities.
 *
 * <p>Any machine BE can own one of these to display a BetterModel model on its block and drive a
 * looping animation (e.g. an "on" / running animation) whose playback speed tracks the machine's
 * live effective speed multiplier. The model is shown for the WHOLE lifetime of the block (idle and
 * running); the animation only loops while the machine asks it to. This is intentionally generic so
 * several machines (crusher, future mills, etc.) can reuse it, including overclock-driven animation
 * speed — the caller simply passes a {@link DoubleSupplier} that returns the current speed multiplier.</p>
 *
 * <h2>Safety</h2>
 * <p>EVERY BetterModel API access is gated by {@link #available()} (a plugin-enabled check) and
 * wrapped in try/catch, so on a server WITHOUT BetterModel every method is a silent no-op and the
 * machine keeps working. BetterModel classes are {@code compileOnly}; they are only touched behind
 * the {@link #available()} guard, so the classloader never needs them when the plugin is absent.</p>
 *
 * <h2>Typical usage from a machine BE</h2>
 * <pre>{@code
 * // field:
 * private final BetterModelMachineRenderer render =
 *         new BetterModelMachineRenderer("crusher", () -> (1 + curGeneration) * (1 + overclock));
 *
 * // every server tick, after computing position/facing:
 * render.setLocation(world, x, y, z, facingYaw);   // keep position/facing current
 * render.show();                                   // spawn if absent (idle pose), idempotent
 * if (isProcessing()) render.playLoop("on");       // loop running anim at supplier speed
 * else                render.stopAnim();            // back to rest/base pose, model stays visible
 *
 * // on break / chunk unload / BE unregister:
 * render.close();
 * }</pre>
 *
 * <p>{@link #show()} is idempotent and cheap, so calling it every tick transparently re-spawns the
 * tracker after a chunk reload or BE re-init and keeps the model visible as players come into view.</p>
 */
public final class BetterModelMachineRenderer {

    private final String modelId;
    private final DoubleSupplier speedSupplier;

    private DummyTracker tracker;
    /** Name of the animation currently looping, or {@code null} when at rest. */
    private String currentAnim;

    // Last known placement; show() re-spawns the model here.
    private World world;
    private double x, y, z;
    private float yawDeg;

    /**
     * @param modelId       BetterModel model id (e.g. {@code "crusher"}).
     * @param speedSupplier live animation-speed multiplier; re-read each time the loop (re)starts and
     *                      every tick to push speed changes (overclock/upgrades) without restarting.
     */
    public BetterModelMachineRenderer(String modelId, DoubleSupplier speedSupplier) {
        this.modelId = modelId;
        this.speedSupplier = speedSupplier == null ? () -> 1.0 : speedSupplier;
    }

    /** True only when BetterModel is present + enabled. Never throws. */
    public static boolean available() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("BetterModel");
        } catch (Throwable t) {
            return false;
        }
    }

    /** Record/refresh the world position + facing yaw the model should sit at. Cheap; call each tick. */
    public void setLocation(World world, double blockX, double blockY, double blockZ, float yawDeg) {
        this.world = world;
        this.x = blockX;
        this.y = blockY;
        this.z = blockZ;
        this.yawDeg = yawDeg;
    }

    private float speed() {
        double s = speedSupplier.getAsDouble();
        if (Double.isNaN(s) || Double.isInfinite(s))
            s = 1.0;
        return (float) Math.max(0.05, s);
    }

    /**
     * Spawn the {@link DummyTracker} for the model at the recorded location if it is not already
     * present. Idempotent and safe to call every tick — this is what survives chunk reload / players
     * coming into view. No-op when BetterModel is absent or no location has been set yet.
     */
    public void show() {
        if (!available() || world == null)
            return;
        try {
            Location loc = new Location(world, x + 0.5, y, z + 0.5, yawDeg, 0f);
            if (tracker == null || tracker.isClosed()) {
                currentAnim = null;
                this.tracker = BetterModel.model(modelId)
                        .map(r -> r.create(BukkitAdapter.adapt(loc)))
                        .orElse(null);
            } else {
                // Keep the existing model at the live location/facing.
                tracker.location(BukkitAdapter.adapt(loc));
            }
            // A DummyTracker (location-based, not entity-bound) does NOT auto-spawn for clients —
            // it must be explicitly spawned to each viewing player. Spawn for everyone in range and
            // re-run each tick so players newly entering view also see it.
            spawnForNearby(loc);
        } catch (Throwable ignored) {
            // BetterModel hiccup — never crash the machine tick.
        }
    }

    /** Spawn the tracker for every player within tracking range that hasn't received it yet. */
    private void spawnForNearby(Location loc) {
        DummyTracker t = this.tracker;
        if (t == null)
            return;
        final double range = 48.0;
        final double rangeSq = range * range;
        for (Player p : world.getPlayers()) {
            try {
                if (p.getLocation().distanceSquared(loc) > rangeSq)
                    continue;
                if (!t.isSpawned(BukkitAdapter.adapt(p)))
                    t.spawn(BukkitAdapter.adapt(p));
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Ensure the given looping animation is playing at the current supplier speed. Spawns the model
     * first if needed. If a different animation is already looping it is switched; if the same one is
     * already looping this only refreshes the live speed. No-op without BetterModel.
     */
    public void playLoop(String animName) {
        if (!available() || animName == null)
            return;
        show();
        DummyTracker t = this.tracker;
        if (t == null)
            return;
        try {
            if (animName.equals(currentAnim)) {
                // Same animation already looping: just keep speed live (FloatSupplier re-reads it).
                return;
            }
            AnimationModifier mod = new AnimationModifier(0, 0, AnimationIterator.Type.LOOP, this::speed);
            t.animate(animName, mod);
            currentAnim = animName;
        } catch (Throwable ignored) {
        }
    }

    /**
     * Stop the looping animation and return the model to its rest/base pose. The model STAYS visible
     * (the tracker is not closed). No-op if nothing is animating or BetterModel is absent.
     */
    public void stopAnim() {
        DummyTracker t = this.tracker;
        if (t == null || currentAnim == null)
            return;
        try {
            t.stopAnimation(currentAnim);
        } catch (Throwable ignored) {
        } finally {
            currentAnim = null;
        }
    }

    /** Despawn the model entirely. Call on break / chunk unload / BE unregister. Safe to repeat. */
    public void close() {
        currentAnim = null;
        DummyTracker t = this.tracker;
        this.tracker = null;
        if (t == null)
            return;
        try {
            t.close();
        } catch (Throwable ignored) {
        }
    }
}
