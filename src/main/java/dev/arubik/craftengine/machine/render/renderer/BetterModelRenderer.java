/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kr.toxicity.model.api.BetterModel
 *  kr.toxicity.model.api.animation.AnimationIterator$Type
 *  kr.toxicity.model.api.animation.AnimationModifier
 *  kr.toxicity.model.api.bukkit.platform.BukkitAdapter
 *  kr.toxicity.model.api.tracker.DummyTracker
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.machine.render.renderer;

import java.util.function.DoubleSupplier;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.DummyTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class BetterModelRenderer {
    private final String modelId;
    private final DoubleSupplier speedSupplier;
    private DummyTracker tracker;
    private String currentAnim;
    private World world;
    private double x;
    private double y;
    private double z;
    private float yawDeg;
    /** Current per-bone target LOCAL rotation for the simulated IK chain (see {@link #playIk}) —
     *  read live by each bone's own registered modifier function below, so re-calling playIk every
     *  tick just UPDATES these values instead of registering a new modifier each time (BetterModel's
     *  {@code RenderedBone#addRotationModifier} is an ADD, not a SET/replace — calling it repeatedly
     *  with a fresh lambda would stack an unbounded number of modifiers over time; registering the
     *  modifier function exactly ONCE per bone, reading from this mutable map each frame, avoids
     *  that entirely). */
    private final java.util.Map<String, org.joml.Quaternionf> ikBoneTargets = new java.util.HashMap<>();
    /** Bone names that already have their (single, permanent) IK modifier function registered —
     *  guards {@link #ikBoneTargets}'s "register once" invariant above. */
    private final java.util.Set<String> ikBonesRegistered = new java.util.HashSet<>();

    public String modelId() {
        return this.modelId;
    }

    public boolean isShown() {
        DummyTracker t = this.tracker;
        return t != null && !t.isClosed();
    }

    public String currentAnimation() {
        return this.currentAnim;
    }

    /** Live world position of a named bone — backs the {@code "{rendererid}:bm:{bone_name}"}
     *  item-display location syntax (see {@code RendererManager#resolveSpecLocation}), via the real
     *  {@code RenderedBone#worldPosition()}. {@code null} if untracked or the bone doesn't exist. */
    public double[] boneWorldPosition(String boneName) {
        DummyTracker t = this.tracker;
        if (t == null || boneName == null) return null;
        try {
            var bone = t.bone(boneName);
            if (bone == null) return null;
            org.joml.Vector3f pos = bone.worldPosition();
            return pos == null ? null : new double[]{pos.x(), pos.y(), pos.z()};
        } catch (Throwable ignored) {
            return null;
        }
    }

    public BetterModelRenderer(String modelId, DoubleSupplier speedSupplier) {
        this.modelId = modelId;
        this.speedSupplier = speedSupplier == null ? () -> 1.0 : speedSupplier;
    }

    public static boolean available() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("BetterModel");
        }
        catch (Throwable t) {
            return false;
        }
    }

    public void setLocation(World world, double blockX, double blockY, double blockZ, float yawDeg) {
        this.world = world;
        this.x = blockX;
        this.y = blockY;
        this.z = blockZ;
        this.yawDeg = yawDeg;
    }

    private float speed() {
        double s = this.speedSupplier.getAsDouble();
        if (Double.isNaN(s) || Double.isInfinite(s)) {
            s = 1.0;
        }
        return (float)Math.max(0.05, s);
    }

    public void show() {
        if (!BetterModelRenderer.available() || this.world == null) {
            return;
        }
        try {
            Location loc = new Location(this.world, this.x + 0.5, this.y, this.z + 0.5, this.yawDeg, 0.0f);
            if (this.tracker == null || this.tracker.isClosed()) {
                this.currentAnim = null;
                this.tracker = BetterModel.model((String)this.modelId).map(r -> r.create(BukkitAdapter.adapt((Location)loc))).orElse(null);
            } else {
                this.tracker.location(BukkitAdapter.adapt((Location)loc));
            }
            this.spawnForNearby(loc);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void spawnForNearby(Location loc) {
        DummyTracker t = this.tracker;
        if (t == null) {
            return;
        }
        double range = 48.0;
        double rangeSq = 2304.0;
        for (Player p : this.world.getPlayers()) {
            try {
                if (p.getLocation().distanceSquared(loc) > 2304.0 || t.isSpawned(BukkitAdapter.adapt((Player)p))) continue;
                t.spawn(BukkitAdapter.adapt((Player)p));
            }
            catch (Throwable throwable) {}
        }
    }

    public void playLoop(String animName) {
        if (!BetterModelRenderer.available() || animName == null) {
            return;
        }
        this.show();
        DummyTracker t = this.tracker;
        if (t == null) {
            return;
        }
        try {
            if (animName.equals(this.currentAnim)) {
                return;
            }
            AnimationModifier mod = new AnimationModifier(0, 0, AnimationIterator.Type.LOOP, this::speed);
            t.animate(animName, mod);
            this.currentAnim = animName;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void stopAnim() {
        DummyTracker t = this.tracker;
        if (t == null || this.currentAnim == null) {
            return;
        }
        try {
            t.stopAnimation(this.currentAnim);
        }
        catch (Throwable throwable) {
        }
        finally {
            this.currentAnim = null;
        }
    }

    public void close() {
        this.currentAnim = null;
        DummyTracker t = this.tracker;
        this.tracker = null;
        if (t == null) {
            return;
        }
        try {
            t.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.ikBonesRegistered.clear();
        this.ikBoneTargets.clear();
    }

    /** Real, direct (non-reflective — BetterModel is a compile-time dependency, unlike MEG) bone
     *  lookup + rotation set, via BetterModel's own public API: {@code Tracker#bone(String)}
     *  (confirmed {@code @Nullable RenderedBone bone(@NotNull String name)}, non-internal) then
     *  {@code RenderedBone#addRotationModifier(Predicate, Function)} — the actual confirmed method
     *  name (checked directly against the compiled dependency's {@code javap} output this session;
     *  BetterModel's docs/naming conventions suggested "addLocalRotModifier", which doesn't exist —
     *  see {@link #ikBoneTargets}'s doc for why the modifier FUNCTION is only ever registered ONCE
     *  per bone name and updated via that map from then on, rather than re-registered on every
     *  call). Returns {@code false} if the tracker isn't shown or the named bone doesn't exist. */
    public boolean setBoneRotation(String boneName, org.joml.Quaternionf localRotation) {
        DummyTracker t = this.tracker;
        if (t == null || boneName == null) {
            return false;
        }
        try {
            var bone = t.bone(boneName);
            if (bone == null) {
                return false;
            }
            this.ikBoneTargets.put(boneName, localRotation);
            if (this.ikBonesRegistered.add(boneName)) {
                bone.addRotationModifier(b -> true, q -> this.ikBoneTargets.getOrDefault(boneName, q));
            }
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** Sets (or, with no {@code rgb} arg, clears) a named bone's tint — direct wrapper over
     *  {@code RenderedBone#tint(Predicate)}/{@code tint(Predicate, int)}, both confirmed real public
     *  methods. {@code rgb} is a packed {@code 0xRRGGBB} color. Returns {@code false} if the tracker
     *  isn't shown or the bone doesn't exist. */
    public boolean setBoneTint(String boneName, int rgb) {
        DummyTracker t = this.tracker;
        if (t == null || boneName == null) {
            return false;
        }
        try {
            var bone = t.bone(boneName);
            return bone != null && bone.tint(b -> true, rgb);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** Script-friendly wrapper over {@link #setBoneRotation} — builds the local rotation
     *  Quaternionf from plain yaw/pitch degrees the same way {@link #playIk} itself does (rotateY
     *  then rotateX), so a script never has to construct a Quaternionf by hand. */
    public boolean setBoneYawPitch(String boneName, float yawDeg, float pitchDeg) {
        org.joml.Quaternionf rot = new org.joml.Quaternionf()
                .rotateY((float) Math.toRadians(yawDeg))
                .rotateX((float) Math.toRadians(pitchDeg));
        return this.setBoneRotation(boneName, rot);
    }

    /** Clears a named bone's tint back to its default (no {@code rgb} override) — see
     *  {@link #setBoneTint}. */
    public boolean clearBoneTint(String boneName) {
        DummyTracker t = this.tracker;
        if (t == null || boneName == null) {
            return false;
        }
        try {
            var bone = t.bone(boneName);
            return bone != null && bone.tint(b -> true);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** One bone's rotation limits within a {@link #playIk} chain — same shape/semantics as {@link
     *  MegRenderer.BoneRange} (kept as a separate type since the two renderers have no common
     *  supertype to share it from, but the constraint meaning is identical: degrees relative to the
     *  bone's own rest pose, equal min/max pins that axis). */
    public record BoneRange(String boneName, float minYaw, float maxYaw, float minPitch, float maxPitch) {}

    /** Per-bone CURRENT interpolated yaw/pitch ({@code [yaw, pitch]}) for {@link #playIk}'s
     *  {@code timeToArriveSeconds} easing — kept separate from {@link #ikBoneTargets} (the raw
     *  quaternion actually applied each call) so repeated calls step smoothly toward a moving
     *  target instead of snapping. */
    private final java.util.Map<String, float[]> ikCurrentAngles = new java.util.HashMap<>();

    /** SIMULATED multi-bone "aim" IK using BetterModel's real, public, non-internal bone-rotation
     *  API (see {@link #setBoneRotation}) — NOT BetterModel's own {@code BoneIKSolver} (confirmed
     *  {@code @ApiStatus.Internal}, takes an internal per-tracker bone-UUID map no external plugin
     *  can construct — see this session's research). Same successive-clamp distribution as {@link
     *  MegRenderer#playIk}: works out the total yaw/pitch to face {@code (targetX, targetY,
     *  targetZ)}, then each bone in the ORDERED chain (base to tip) absorbs as much of the
     *  REMAINING angle as its own range allows, passing the rest on.
     *
     *  <p>{@code timeToArriveSeconds} is how long the chain should take to reach a NEWLY-set target
     *  — each call (meant to be invoked once per game tick, matching {@code RendererManager.tick()}
     *  's own cadence) steps every bone's CURRENT angle toward its clamped target by at most
     *  {@code 360° / (timeToArriveSeconds * 20 ticks)}, so a slow value reads as the limb easing
     *  into place over that many seconds instead of snapping instantly. {@code <= 0} disables
     *  easing entirely (snaps straight to target, the old behavior). Returns how many bones were
     *  found and had a rotation applied (0 if none). */
    public int playIk(java.util.List<BoneRange> chain, double targetX, double targetY, double targetZ,
                       float timeToArriveSeconds) {
        if (chain == null || chain.isEmpty()) {
            return 0;
        }
        double dx = targetX - (this.x + 0.5);
        double dz = targetZ - (this.z + 0.5);
        double dy = targetY - this.y;
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        float remainingYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float remainingPitch = (float) Math.toDegrees(Math.atan2(dy, horizDist));
        float maxStep = timeToArriveSeconds > 0f ? 360f / (timeToArriveSeconds * 20f) : Float.MAX_VALUE;

        int applied = 0;
        for (BoneRange br : chain) {
            float targetYaw = clampDeg(remainingYaw, br.minYaw(), br.maxYaw());
            float targetPitch = clampDeg(remainingPitch, br.minPitch(), br.maxPitch());
            float[] cur = this.ikCurrentAngles.computeIfAbsent(br.boneName(), k -> new float[2]);
            cur[0] = stepToward(cur[0], targetYaw, maxStep);
            cur[1] = stepToward(cur[1], targetPitch, maxStep);
            org.joml.Quaternionf rot = new org.joml.Quaternionf()
                    .rotateY((float) Math.toRadians(cur[0]))
                    .rotateX((float) Math.toRadians(cur[1]));
            if (this.setBoneRotation(br.boneName(), rot)) {
                applied++;
            }
            remainingYaw -= targetYaw;
            remainingPitch -= targetPitch;
        }
        return applied;
    }

    private static float clampDeg(float value, float min, float max) {
        float lo = Math.min(min, max);
        float hi = Math.max(min, max);
        return Math.max(lo, Math.min(hi, value));
    }

    /** Steps {@code current} toward {@code target} by at most {@code maxStep} degrees, always
     *  taking the shorter way around the circle (never overshoots past {@code target}). */
    private static float stepToward(float current, float target, float maxStep) {
        float diff = ((target - current + 180f) % 360f + 360f) % 360f - 180f;
        if (Math.abs(diff) <= maxStep) {
            return current + diff;
        }
        return current + Math.copySign(maxStep, diff);
    }
}

