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
package dev.arubik.craftengine.machine.render;

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

public final class ModelEngineMachineRenderer {
    private static final Logger LOG = Logger.getLogger("CraftEnginePolyfills");
    private final String modelId;
    private final DoubleSupplier speedSupplier;
    private String loopAnimation;
    private Entity dummyEntity;
    private Object modeledEntity;
    private Object activeModel;
    private boolean reflectionOk = true;
    private World world;
    private double x;
    private double y;
    private double z;
    private float yawDeg;

    public ModelEngineMachineRenderer(String modelId) {
        this(modelId, null);
    }

    public ModelEngineMachineRenderer(String modelId, DoubleSupplier speedSupplier) {
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
        if (!ModelEngineMachineRenderer.available() || !this.reflectionOk || this.world == null) {
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
        if (!ModelEngineMachineRenderer.available() || !this.reflectionOk || animName == null) {
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
            Method playAnim = ModelEngineMachineRenderer.findMethod(handler.getClass(), "playAnimation", String.class, Double.TYPE, Double.TYPE, Double.TYPE, Integer.TYPE, Boolean.TYPE);
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
            Method stopAnim = ModelEngineMachineRenderer.findMethod(handler.getClass(), "stopAnimation", String.class);
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
                Method stopAll = ModelEngineMachineRenderer.findMethod(handler.getClass(), "destroyAllAnimations", new Class[0]);
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
                Method destroy = ModelEngineMachineRenderer.findMethod(this.modeledEntity.getClass(), "destroy", new Class[0]);
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
        Method addModel = ModelEngineMachineRenderer.findMethod(modeledEntity.getClass(), "addModel", new Class[0]);
        if (addModel == null) {
            for (Method m : modeledEntity.getClass().getMethods()) {
                if (!m.getName().equals("addModel") || m.getParameterCount() != 2) continue;
                m.invoke(modeledEntity, activeModel, false);
                break;
            }
        } else {
            addModel.invoke(modeledEntity, activeModel, false);
        }
        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?> ... params) {
        try {
            return clazz.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Method m : c.getDeclaredMethods()) {
                    if (!m.getName().equals(name) || params.length != 0 && !ModelEngineMachineRenderer.matchParams(m, params)) continue;
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
        LOG.warning("[CraftEnginePolyfills] ModelEngineMachineRenderer reflection failed for model '" + this.modelId + "': " + t.getMessage() + " \u2014 disabling ModelEngine render for this instance.");
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

