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
package dev.arubik.craftengine.machine.render;

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

public final class BetterModelMachineRenderer {
    private final String modelId;
    private final DoubleSupplier speedSupplier;
    private DummyTracker tracker;
    private String currentAnim;
    private World world;
    private double x;
    private double y;
    private double z;
    private float yawDeg;

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

    public BetterModelMachineRenderer(String modelId, DoubleSupplier speedSupplier) {
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
        if (!BetterModelMachineRenderer.available() || this.world == null) {
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
        if (!BetterModelMachineRenderer.available() || animName == null) {
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
    }
}

