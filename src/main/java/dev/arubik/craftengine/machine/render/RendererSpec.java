/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.render;

import net.minecraft.world.item.ItemStack;

public sealed interface RendererSpec {
    public String whenExpr();

    public String updateWhen();

    default public String scriptRef() {
        return null;
    }

    default public String locationExpr() {
        return null;
    }

    default public String positionsExpr() {
        return null;
    }

    default public String locationsExpr() {
        return this.positionsExpr();
    }

    public record EvaluatedItemDisplay(int specIndex, ItemStack item, double offsetX, double offsetY, double offsetZ, float scale, float rotX, float rotY, float rotZ) {
    }

    public record SlotDisplaySpec(int slot, float offsetX, float offsetY, float offsetZ, float scale, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record PositionedSpec(RendererSpec inner, String positionsExpr, String locationExprOverride) implements RendererSpec
    {
        @Override
        public String whenExpr() {
            return this.inner.whenExpr();
        }

        @Override
        public String updateWhen() {
            return this.inner.updateWhen();
        }

        @Override
        public String scriptRef() {
            return this.inner.scriptRef();
        }

        @Override
        public String locationExpr() {
            return this.locationExprOverride != null ? this.locationExprOverride : this.inner.locationExpr();
        }
    }

    public record BlockDisplaySpec(String blockStateExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record ArmorStandSpec(String headItemExpr, String bodyItemExpr, String locationExpr, String rotX, String rotY, String rotZ, boolean small, boolean invisible, boolean marker, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record SoundSpec(String soundId, String volumeExpr, String pitchExpr, int interval, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record TextDisplaySpec(String textExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, int lineWidth, String backgroundExpr, boolean shadow, boolean seeThrough, String alignment, int opacity, String whenExpr, String updateWhen, boolean global, String scriptRef) implements RendererSpec
    {
    }

    public record FluidTankSpec(String tankName, boolean isGas, String locationExpr, float maxHeight, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record ParticleSpec(String particle, String countExpr, String locationExpr, String spreadXExpr, String spreadYExpr, String spreadZExpr, String speedExpr, String dirXExpr, String dirYExpr, String dirZExpr, String shape, String directionMode, int interval, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record ItemDisplaySpec(String itemExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, String whenExpr, String updateWhen, boolean global, String scriptRef, String cell) implements RendererSpec
    {
        public ItemDisplaySpec(String itemExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, String whenExpr, String updateWhen, boolean global, String scriptRef) {
            this(itemExpr, locationExpr, scale, rotX, rotY, rotZ, billboard, whenExpr, updateWhen, global, scriptRef, "auto");
        }
    }

    public record ModelEngineSpec(String modelId, String animation, String speedExpr, String whenExpr, String updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record BetterModelSpec(String modelId, String animation, String speedExpr, String whenExpr, String updateWhen, String scriptRef, int lingerTicks) implements RendererSpec
    {
    }

    /**
     * Programmatic renderer — calls a script function every tick to get display state.
     * The function returns a Map with keys: item, pos (Vector), rot_x, rot_y, rot_z, scale, billboard.
     * Multiple display entities: return an Array of such Maps.
     *
     * Example JSON:
     *   { "type": "programmatic", "run": "crusher.pf:get_render_state", "when": "running" }
     *
     * Example script:
     *   def get_render_state() {
     *       t = ticks_alive * 0.05
     *       return { "item": "minecraft:stone", "pos": vec(sin(t)*0.5, 0.5, cos(t)*0.5), "scale": "0.3" }
     *   }
     */
    public record ProgrammaticSpec(
        String scriptRef,    // e.g. "crusher.pf:get_render_state"
        String whenExpr,
        String updateWhen,
        String scriptRef2    // alias — same value, used as scriptRef() return
    ) implements RendererSpec {
        @Override public String scriptRef() { return scriptRef; }
        @Override public String whenExpr()   { return whenExpr;  }
        @Override public String updateWhen() { return updateWhen; }
    }
}

