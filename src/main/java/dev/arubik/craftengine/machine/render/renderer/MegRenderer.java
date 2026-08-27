/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Interaction
 */
package dev.arubik.craftengine.machine.render.renderer;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.joml.Quaternionf;

public final class MegRenderer {
    private static final Logger LOG = Logger.getLogger("CraftEnginePolyfills");
    private final String modelId;
    private final DoubleSupplier speedSupplier;
    private String loopAnimation;
    private Entity dummyEntity;
    private Object modeledEntity;
    // NOTE: bone lookup (getBone) and yaw rotation (setYaw) below call ActiveModel/ModelBone
    // DIRECTLY now — ModelEngine is already a real compileOnly dependency (see build.gradle.kts),
    // confirmed via javap against the actual R4.0.7 jar this session, so there was no reason to
    // keep guessing method names through reflection for these two calls specifically. Animation
    // playback (playLoop/stopAnim/attachModelEngine below) stays reflective for now — untouched,
    // already working, and out of scope for this pass (bones/IK only).
    private ActiveModel activeModel;
    private boolean reflectionOk = true;
    private World world;
    private double x;
    private double y;
    private double z;
    private float yawDeg;

    public MegRenderer(String modelId) {
        this(modelId, null);
    }

    public MegRenderer(String modelId, DoubleSupplier speedSupplier) {
        this.modelId = modelId;
        this.speedSupplier = speedSupplier != null ? speedSupplier : () -> 1.0;
    }

    public String modelId() {
        return this.modelId;
    }

    public String loopAnimation() {
        return this.loopAnimation;
    }

    public boolean isShown() {
        return this.dummyEntity != null && !this.dummyEntity.isDead();
    }

    public static boolean available() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("ModelEngine");
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
        if (this.dummyEntity != null && !this.dummyEntity.isDead()) {
            try {
                this.dummyEntity.teleport(new Location(world, this.x + 0.5, this.y, this.z + 0.5, yawDeg, 0.0f));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public void show() {
        if (!MegRenderer.available() || !this.reflectionOk || this.world == null) {
            return;
        }
        try {
            if (this.dummyEntity == null || this.dummyEntity.isDead()) {
                this.spawnDummyEntity();
                this.attachModelEngine();
            } else {
                this.dummyEntity.teleport(new Location(this.world, this.x + 0.5, this.y, this.z + 0.5, this.yawDeg, 0.0f));
            }
        }
        catch (Throwable t) {
            this.logReflectionFailure(t);
        }
    }

    public void playLoop(String animName) {
        if (!MegRenderer.available() || !this.reflectionOk || animName == null) {
            return;
        }
        this.show();
        if (this.activeModel == null) {
            return;
        }
        try {
            if (animName.equals(this.loopAnimation)) {
                return;
            }
            double spd = this.speedSupplier.getAsDouble();
            if (Double.isNaN(spd) || Double.isInfinite(spd)) {
                spd = 1.0;
            }
            spd = Math.max(0.05, spd);
            Object handler = this.activeModel.getClass().getMethod("getAnimationHandler", new Class[0]).invoke(this.activeModel, new Object[0]);
            Method playAnim = MegRenderer.findMethod(handler.getClass(), "playAnimation", String.class, Double.TYPE, Double.TYPE, Double.TYPE, Integer.TYPE, Boolean.TYPE);
            if (playAnim != null) {
                playAnim.invoke(handler, animName, 0.0, 0.0, spd, 0, true);
            }
            this.loopAnimation = animName;
        }
        catch (Throwable t) {
            this.logReflectionFailure(t);
        }
    }

    public void stopAnim() {
        if (this.activeModel == null || this.loopAnimation == null) {
            return;
        }
        try {
            Object handler = this.activeModel.getClass().getMethod("getAnimationHandler", new Class[0]).invoke(this.activeModel, new Object[0]);
            Method stopAnim = MegRenderer.findMethod(handler.getClass(), "stopAnimation", String.class);
            if (stopAnim != null) {
                stopAnim.invoke(handler, this.loopAnimation);
            }
        }
        catch (Throwable throwable) {
        }
        finally {
            this.loopAnimation = null;
        }
    }

    public void close() {
        this.loopAnimation = null;
        if (this.activeModel != null) {
            try {
                Object handler = this.activeModel.getClass().getMethod("getAnimationHandler", new Class[0]).invoke(this.activeModel, new Object[0]);
                Method stopAll = MegRenderer.findMethod(handler.getClass(), "destroyAllAnimations", new Class[0]);
                if (stopAll != null) {
                    stopAll.invoke(handler, new Object[0]);
                }
            }
            catch (Throwable handler) {
                // empty catch block
            }
            this.activeModel = null;
        }
        if (this.modeledEntity != null) {
            try {
                Method destroy = MegRenderer.findMethod(this.modeledEntity.getClass(), "destroy", new Class[0]);
                if (destroy != null) {
                    destroy.invoke(this.modeledEntity, new Object[0]);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.modeledEntity = null;
        }
        if (this.dummyEntity != null) {
            try {
                this.dummyEntity.remove();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.dummyEntity = null;
        }
        this.ikCurrentAngles.clear();
    }

    private void spawnDummyEntity() {
        Interaction entity = (Interaction)this.world.spawnEntity(new Location(this.world, this.x + 0.5, this.y, this.z + 0.5, this.yawDeg, 0.0f), EntityType.INTERACTION);
        entity.setInteractionWidth(0.0f);
        entity.setInteractionHeight(0.0f);
        entity.setResponsive(false);
        entity.setPersistent(false);
        this.dummyEntity = entity;
    }

    private void attachModelEngine() throws Throwable {
        Object api = Class.forName("com.ticxo.modelengine.api.ModelEngineAPI").getMethod("api", new Class[0]).invoke(null, new Object[0]);
        Object blueprintMgr = api.getClass().getMethod("getBlueprintManager", new Class[0]).invoke(api, new Object[0]);
        Optional opt = (Optional)blueprintMgr.getClass().getMethod("get", String.class).invoke(blueprintMgr, this.modelId);
        Object blueprint = opt.orElse(null);
        if (blueprint == null) {
            return;
        }
        Object activeModel = blueprint.getClass().getMethod("generateModel", new Class[0]).invoke(blueprint, new Object[0]);
        Object modelMgr = api.getClass().getMethod("getModelManager", new Class[0]).invoke(api, new Object[0]);
        Object modeledEntity = modelMgr.getClass().getMethod("createModeledEntity", Entity.class).invoke(modelMgr, this.dummyEntity);
        Method addModel = MegRenderer.findMethod(modeledEntity.getClass(), "addModel", new Class[0]);
        if (addModel == null) {
            for (Method m : modeledEntity.getClass().getMethods()) {
                if (!m.getName().equals("addModel") || m.getParameterCount() != 2) continue;
                m.invoke(modeledEntity, activeModel, false);
                break;
            }
        } else {
            addModel.invoke(modeledEntity, activeModel, false);
        }
        this.activeModel = (ActiveModel) activeModel;
        this.modeledEntity = modeledEntity;
    }

    /** Bone lookup by name via ModelEngine's real compile-time API — {@code ActiveModel#getBone
     *  (String)} returns {@code Optional<ModelBone>}, confirmed via {@code javap} against the actual
     *  ModelEngine-R4.0.7 jar. No reflection. Returns {@code null} if the model is unattached, the
     *  name doesn't exist, or the {@code Optional} is empty. */
    private ModelBone findBone(String boneName) {
        if (this.activeModel == null || boneName == null) {
            return null;
        }
        return this.activeModel.getBone(boneName).orElse(null);
    }

    /** Live world position of a named bone — backs the {@code "{rendererid}:meg:{bone_name}"}
     *  item-display location syntax (see {@code RendererManager#resolveSpecLocation}), via the real
     *  {@code ModelBone#getLocation()} (confirmed via javap). {@code null} if untracked or the bone
     *  doesn't exist. */
    public double[] boneWorldPosition(String boneName) {
        ModelBone bone = this.findBone(boneName);
        if (bone == null) return null;
        try {
            org.bukkit.Location loc = bone.getLocation();
            return loc == null ? null : new double[]{loc.getX(), loc.getY(), loc.getZ()};
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** PSEUDO bone-rotation support — ModelEngine's public API has NO dedicated IK solver (confirmed
     *  this session: {@code ModelBone}/{@code BoneBehavior} give raw transform hooks, not a built-in
     *  chain solver), so this sets a named bone's yaw directly via its real {@code setYaw(float)}
     *  setter — no reflection, confirmed via {@code javap} that {@code ModelBone.setYaw(float)} is a
     *  real public method. Degrees. Returns {@code false} if the bone can't be found. */
    public boolean setBoneYaw(String boneName, float yawDeg) {
        ModelBone bone = this.findBone(boneName);
        if (bone == null) {
            return false;
        }
        bone.setYaw(yawDeg);
        return true;
    }

    /** One bone's rotation limits within a {@link #playIk} chain — {@code minYaw}/{@code maxYaw}/
     *  {@code minPitch}/{@code maxPitch} are degrees, relative to the bone's own rest pose (both
     *  bounds equal, e.g. {@code 0, 0}, pins that axis — the classic "this joint only bends, it
     *  doesn't twist" constraint an elbow-like bone would want). */
    public record BoneRange(String boneName, float minYaw, float maxYaw, float minPitch, float maxPitch) {}

    /** SIMULATED multi-bone "aim" IK — explicitly NOT a real inverse-kinematics chain solver (MEG
     *  has none publicly — see {@link #findBone}'s own doc). Given an ORDERED chain of bones (base
     *  to tip), each with its own yaw/pitch range (see {@link BoneRange}), this works out the total
     *  yaw/pitch needed to face {@code (targetX, targetY, targetZ)} from this renderer's current
     *  position, then distributes it across the chain: each bone absorbs as much of the REMAINING
     *  angle as its own range allows (clamped), passing whatever it couldn't absorb on to the next
     *  bone — e.g. a shoulder with a wide range takes most of a big turn, leaving only a little for
     *  a tightly-clamped elbow after it. This is a constrained successive-clamp distribution, not
     *  iterative position-based CCD (MEG's public API doesn't expose per-bone world position
     *  cheaply enough for that to be worth it here). Yaw always applies via the confirmed {@code
     *  setYaw(float)}; pitch applies via {@code setPitch(float)} ONLY if that setter actually exists
     *  on the bone (unconfirmed on MEG's public API at research time — silently skipped per-bone if
     *  absent, everything else still applies). Returns how many bones in the chain were actually
     *  found and had at least yaw applied (0 if none — e.g. all names wrong, or MEG unavailable). */
    /** Per-bone CURRENT interpolated yaw/pitch ({@code [yaw, pitch]}) for {@code timeToArriveSeconds}
     *  easing — see {@link BetterModelRenderer}'s identical field for the full rationale (kept
     *  separate per class since the two renderers share no common base to hang it off of). */
    private final java.util.Map<String, float[]> ikCurrentAngles = new java.util.HashMap<>();

    /** {@code timeToArriveSeconds} is how long the chain should take to reach a NEWLY-set target —
     *  each call (meant to be invoked once per game tick) steps every bone's CURRENT angle toward
     *  its clamped target by at most {@code 360° / (timeToArriveSeconds * 20 ticks)}, so a slow
     *  value reads as the limb easing into place over that many seconds instead of snapping. {@code
     *  <= 0} disables easing entirely (snaps straight to target). See {@link BetterModelRenderer
     *  #playIk} for the identical mechanism on the other engine. */
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
            ModelBone bone = this.findBone(br.boneName());
            if (bone == null) {
                continue;
            }
            float targetYaw = clampDeg(remainingYaw, br.minYaw(), br.maxYaw());
            float targetPitch = clampDeg(remainingPitch, br.minPitch(), br.maxPitch());
            float[] cur = this.ikCurrentAngles.computeIfAbsent(br.boneName(), k -> new float[2]);
            cur[0] = stepToward(cur[0], targetYaw, maxStep);
            cur[1] = stepToward(cur[1], targetPitch, maxStep);
            // ModelBone has no setPitch (confirmed via javap on the real API) — pitch is tracked in
            // ikCurrentAngles for future use but only yaw can actually be applied on this engine.
            bone.setYaw(cur[0]);
            // we have left rotation its a quaternion
            Quaternionf leftRotation = bone.getGlobalLeftRotation();
            bone.setGlobalLeftRotation(leftRotation.rotateX((float) Math.toRadians(cur[1])));
            applied++;
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

    private static Method findMethod(Class<?> clazz, String name, Class<?> ... params) {
        try {
            return clazz.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Method m : c.getDeclaredMethods()) {
                    if (!m.getName().equals(name) || params.length != 0 && !MegRenderer.matchParams(m, params)) continue;
                    m.setAccessible(true);
                    return m;
                }
            }
            return null;
        }
    }

    private static boolean matchParams(Method m, Class<?>[] params) {
        if (m.getParameterCount() != params.length) {
            return false;
        }
        Class<?>[] actual = m.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            if (actual[i].isAssignableFrom(params[i])) continue;
            return false;
        }
        return true;
    }

    private void logReflectionFailure(Throwable t) {
        LOG.warning("[CraftEnginePolyfills] MegRenderer reflection failed for model '" + this.modelId + "': " + t.getMessage() + " \u2014 disabling ModelEngine render for this instance.");
        this.reflectionOk = false;
        if (this.dummyEntity != null) {
            try {
                this.dummyEntity.remove();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.dummyEntity = null;
        }
        this.activeModel = null;
        this.modeledEntity = null;
    }
}

