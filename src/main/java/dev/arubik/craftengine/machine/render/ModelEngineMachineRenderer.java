package dev.arubik.craftengine.machine.render;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.logging.Logger;

/**
 * Reusable, reflection-guarded ModelEngine R4 render driver for machine block-entities.
 *
 * <p>All ModelEngine API calls are performed via reflection so this class compiles without the
 * ModelEngine jar on the classpath. Every call is wrapped in try/catch; on a server without
 * ModelEngine every method is a silent no-op and the machine keeps working.</p>
 *
 * <h2>Typical usage from a machine BE</h2>
 * <pre>{@code
 * private final ModelEngineMachineRenderer render = new ModelEngineMachineRenderer("crusher", "on");
 *
 * // every tick:
 * render.setLocation(world, x, y, z, facingYaw);
 * render.show();
 * if (isProcessing()) render.playLoop("on");
 * else                render.stopAnim();
 *
 * // on break / unregister:
 * render.close();
 * }</pre>
 */
public final class ModelEngineMachineRenderer {

    private static final Logger LOG = Logger.getLogger("CraftEnginePolyfills");

    private final String modelId;
    private final DoubleSupplier speedSupplier;

    /** Name of the animation currently looping, or {@code null} when at rest. */
    private String loopAnimation;

    /** Real Bukkit INTERACTION entity that ModelEngine attaches to. */
    private Entity dummyEntity;

    /** {@code com.ticxo.modelengine.api.entity.ModeledEntity} stored as Object to avoid compile dep. */
    private Object modeledEntity;

    /** {@code com.ticxo.modelengine.api.model.ActiveModel} stored as Object. */
    private Object activeModel;

    /** Set to false if reflection fails so we stop trying. */
    private boolean reflectionOk = true;

    // Last known placement
    private World world;
    private double x, y, z;
    private float yawDeg;

    public ModelEngineMachineRenderer(String modelId) {
        this(modelId, null);
    }

    /**
     * @param modelId       ModelEngine blueprint id (e.g. {@code "crusher"}).
     * @param speedSupplier live animation-speed multiplier; re-read on each {@link #playLoop} call.
     *                      {@code null} defaults to {@code () -> 1.0}.
     */
    public ModelEngineMachineRenderer(String modelId, DoubleSupplier speedSupplier) {
        this.modelId = modelId;
        this.speedSupplier = speedSupplier != null ? speedSupplier : () -> 1.0;
    }

    /** Model id this renderer drives. */
    public String modelId() {
        return modelId;
    }

    /** Current looping animation name, or {@code null} when at rest. */
    public String loopAnimation() {
        return loopAnimation;
    }

    /** True when the dummy entity is alive. */
    public boolean isShown() {
        return dummyEntity != null && !dummyEntity.isDead();
    }

    /** True only when ModelEngine is present + enabled. Never throws. */
    public static boolean available() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("ModelEngine");
        } catch (Throwable t) {
            return false;
        }
    }

    /** Record/refresh the world position + facing yaw. Cheap; call each tick. */
    public void setLocation(World world, double blockX, double blockY, double blockZ, float yawDeg) {
        this.world = world;
        this.x = blockX;
        this.y = blockY;
        this.z = blockZ;
        this.yawDeg = yawDeg;
        if (dummyEntity != null && !dummyEntity.isDead()) {
            try {
                dummyEntity.teleport(new Location(world, x + 0.5, y, z + 0.5, yawDeg, 0f));
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Spawn the INTERACTION entity and attach a ModeledEntity to it if not already present.
     * Idempotent. No-op when ModelEngine is absent or no location has been set yet.
     */
    public void show() {
        if (!available() || !reflectionOk || world == null)
            return;
        try {
            if (dummyEntity == null || dummyEntity.isDead()) {
                spawnDummyEntity();
                attachModelEngine();
            } else {
                dummyEntity.teleport(new Location(world, x + 0.5, y, z + 0.5, yawDeg, 0f));
            }
        } catch (Throwable t) {
            logReflectionFailure(t);
        }
    }

    /**
     * Ensure the given looping animation is playing. Spawns first if needed.
     * If the same animation is already looping this is a no-op.
     */
    public void playLoop(String animName) {
        if (!available() || !reflectionOk || animName == null)
            return;
        show();
        if (activeModel == null)
            return;
        try {
            if (animName.equals(loopAnimation))
                return;
            double spd = speedSupplier.getAsDouble();
            if (Double.isNaN(spd) || Double.isInfinite(spd)) spd = 1.0;
            spd = Math.max(0.05, spd);
            Object handler = activeModel.getClass().getMethod("getAnimationHandler").invoke(activeModel);
            // playAnimation(name, lerpIn, lerpOut, speed, priority, force)
            Method playAnim = findMethod(handler.getClass(), "playAnimation",
                    String.class, double.class, double.class, double.class, int.class, boolean.class);
            if (playAnim != null) {
                playAnim.invoke(handler, animName, 0.0, 0.0, spd, 0, true);
            }
            loopAnimation = animName;
        } catch (Throwable t) {
            logReflectionFailure(t);
        }
    }

    /**
     * Stop the current looping animation. Model stays visible.
     */
    public void stopAnim() {
        if (activeModel == null || loopAnimation == null)
            return;
        try {
            Object handler = activeModel.getClass().getMethod("getAnimationHandler").invoke(activeModel);
            Method stopAnim = findMethod(handler.getClass(), "stopAnimation", String.class);
            if (stopAnim != null) {
                stopAnim.invoke(handler, loopAnimation);
            }
        } catch (Throwable ignored) {
        } finally {
            loopAnimation = null;
        }
    }

    /** Remove model and entity entirely. Safe to repeat. */
    public void close() {
        loopAnimation = null;
        if (activeModel != null) {
            try {
                Object handler = activeModel.getClass().getMethod("getAnimationHandler").invoke(activeModel);
                Method stopAll = findMethod(handler.getClass(), "destroyAllAnimations");
                if (stopAll != null) stopAll.invoke(handler);
            } catch (Throwable ignored) {}
            activeModel = null;
        }
        if (modeledEntity != null) {
            try {
                Method destroy = findMethod(modeledEntity.getClass(), "destroy");
                if (destroy != null) destroy.invoke(modeledEntity);
            } catch (Throwable ignored) {}
            modeledEntity = null;
        }
        if (dummyEntity != null) {
            try {
                dummyEntity.remove();
            } catch (Throwable ignored) {}
            dummyEntity = null;
        }
    }

    // ---- internals ----

    @SuppressWarnings("deprecation")
    private void spawnDummyEntity() {
        org.bukkit.entity.Interaction entity = (org.bukkit.entity.Interaction)
                world.spawnEntity(new Location(world, x + 0.5, y, z + 0.5, yawDeg, 0f), EntityType.INTERACTION);
        entity.setInteractionWidth(0.0f);
        entity.setInteractionHeight(0.0f);
        entity.setResponsive(false);
        entity.setPersistent(false);
        this.dummyEntity = entity;
    }

    private void attachModelEngine() throws Throwable {
        Object api = Class.forName("com.ticxo.modelengine.api.ModelEngineAPI")
                .getMethod("api").invoke(null);

        Object blueprintMgr = api.getClass().getMethod("getBlueprintManager").invoke(api);
        @SuppressWarnings("unchecked")
        Optional<?> opt = (Optional<?>) blueprintMgr.getClass()
                .getMethod("get", String.class).invoke(blueprintMgr, modelId);
        Object blueprint = opt.orElse(null);
        if (blueprint == null) return;

        Object activeModel = blueprint.getClass().getMethod("generateModel").invoke(blueprint);

        Object modelMgr = api.getClass().getMethod("getModelManager").invoke(api);
        Object modeledEntity = modelMgr.getClass()
                .getMethod("createModeledEntity", Entity.class).invoke(modelMgr, dummyEntity);

        // addModel(ActiveModel, boolean generateHitbox)
        Method addModel = findMethod(modeledEntity.getClass(), "addModel");
        if (addModel == null) {
            // Try with two-arg variant
            for (Method m : modeledEntity.getClass().getMethods()) {
                if (m.getName().equals("addModel") && m.getParameterCount() == 2) {
                    m.invoke(modeledEntity, activeModel, false);
                    break;
                }
            }
        } else {
            addModel.invoke(modeledEntity, activeModel, false);
        }

        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            // walk declared methods on the class and supers
            Class<?> c = clazz;
            while (c != null && c != Object.class) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.getName().equals(name) && (params.length == 0 || matchParams(m, params))) {
                        m.setAccessible(true);
                        return m;
                    }
                }
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private static boolean matchParams(Method m, Class<?>[] params) {
        if (m.getParameterCount() != params.length) return false;
        Class<?>[] actual = m.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (!actual[i].isAssignableFrom(params[i])) return false;
        }
        return true;
    }

    private void logReflectionFailure(Throwable t) {
        LOG.warning("[CraftEnginePolyfills] ModelEngineMachineRenderer reflection failed for model '"
                + modelId + "': " + t.getMessage() + " — disabling ModelEngine render for this instance.");
        reflectionOk = false;
        // Clean up any partially-spawned entity
        if (dummyEntity != null) {
            try { dummyEntity.remove(); } catch (Throwable ignored) {}
            dummyEntity = null;
        }
        activeModel = null;
        modeledEntity = null;
    }
}
