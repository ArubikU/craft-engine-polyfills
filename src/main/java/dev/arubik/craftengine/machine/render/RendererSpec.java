/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.render;

import net.minecraft.world.item.ItemStack;

public sealed interface RendererSpec {
    public WhenCondition whenExpr();

    public UpdateWhen updateWhen();

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

    /** Stable id for a renderer entry — {@code null} unless the JSON entry declared one via
     *  {@code "id": "..."}. Only {@link ModelEngineSpec}/{@link BetterModelSpec} actually carry a
     *  real component for this (the two kinds a script needs to address later via
     *  {@code Machine.get_renderer(id)} — see MachineType.java); every other spec kind just
     *  inherits this default (always {@code null}), matching {@link #scriptRef}/{@link
     *  #locationExpr}'s own "default null, override if the record actually has it" shape. */
    default public String id() {
        return null;
    }

    public record EvaluatedItemDisplay(int specIndex, ItemStack item, double offsetX, double offsetY, double offsetZ, float scale, float rotX, float rotY, float rotZ) {
    }

    public record SlotDisplaySpec(int slot, float offsetX, float offsetY, float offsetZ, float scale, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record PositionedSpec(RendererSpec inner, String positionsExpr, String locationExprOverride) implements RendererSpec
    {
        @Override
        public WhenCondition whenExpr() {
            return this.inner.whenExpr();
        }

        @Override
        public UpdateWhen updateWhen() {
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

        @Override
        public String id() {
            return this.inner.id();
        }
    }

    public record BlockDisplaySpec(String blockStateExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    /**
     * Packet-only, invisible clickable hitbox (an {@code EntityType.INTERACTION} entity) — positioned/
     * sized like any other renderer element, firing {@code onInteractRef} when a player right-clicks or
     * left-clicks (attacks) it. Uses the SAME context-building convention as the whole-block
     * {@code on_interact} hook: {@code Machine} + {@code Player} + an {@code Event} object, via
     * {@code DataMachineBlockEntity.runInteractScript(scriptRef, player, "on_renderer_interact")}.
     * Doesn't use the {@link #scriptRef} var-injection mechanism other specs do.
     *
     * Example JSON:
     *   { "type": "interaction", "location": [0, 0.5, 0], "width": 0.6, "height": 0.6, "on_interact": "file.pf:func" }
     */
    public record InteractionSpec(String locationExpr, float width, float height, WhenCondition whenExpr, UpdateWhen updateWhen, String onInteractRef) implements RendererSpec
    {
        @Override
        public String scriptRef() {
            return null;
        }
    }

    public record ArmorStandSpec(String headItemExpr, String bodyItemExpr, String locationExpr, String rotX, String rotY, String rotZ, boolean small, boolean invisible, boolean marker, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record SoundSpec(String soundId, String volumeExpr, String pitchExpr, int interval, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record TextDisplaySpec(String textExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, int lineWidth, String backgroundExpr, boolean shadow, boolean seeThrough, String alignment, int opacity, WhenCondition whenExpr, UpdateWhen updateWhen, boolean global, String scriptRef) implements RendererSpec
    {
    }

    public record FluidTankSpec(String tankName, boolean isGas, String locationExpr, float maxHeight, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record ParticleSpec(String particle, String countExpr, String locationExpr, String spreadXExpr, String spreadYExpr, String spreadZExpr, String speedExpr, String dirXExpr, String dirYExpr, String dirZExpr, String shape, String directionMode, int interval, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) implements RendererSpec
    {
    }

    public record ItemDisplaySpec(String itemExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, WhenCondition whenExpr, UpdateWhen updateWhen, boolean global, String scriptRef, String cell) implements RendererSpec
    {
        public ItemDisplaySpec(String itemExpr, String locationExpr, String scale, String rotX, String rotY, String rotZ, String billboard, WhenCondition whenExpr, UpdateWhen updateWhen, boolean global, String scriptRef) {
            this(itemExpr, locationExpr, scale, rotX, rotY, rotZ, billboard, whenExpr, updateWhen, global, scriptRef, "auto");
        }
    }

    public record ModelEngineSpec(String modelId, String animation, String speedExpr, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef, String id) implements RendererSpec
    {
        /** Backward-compat convenience constructor for any call site built before {@code id}
         *  existed — same pattern as {@link ItemDisplaySpec}'s own two-constructor shape. */
        public ModelEngineSpec(String modelId, String animation, String speedExpr, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef) {
            this(modelId, animation, speedExpr, whenExpr, updateWhen, scriptRef, null);
        }
    }

    public record BetterModelSpec(String modelId, String animation, String speedExpr, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef, int lingerTicks, String id) implements RendererSpec
    {
        public BetterModelSpec(String modelId, String animation, String speedExpr, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef, int lingerTicks) {
            this(modelId, animation, speedExpr, whenExpr, updateWhen, scriptRef, lingerTicks, null);
        }
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
        WhenCondition whenExpr,
        UpdateWhen updateWhen,
        String scriptRef2,   // alias — same value, used as scriptRef() return
        String id
    ) implements RendererSpec {
        /** Backward-compat convenience constructor for any call site built before {@code id}
         *  existed — same pattern as {@link ItemDisplaySpec}/{@link ModelEngineSpec}. */
        public ProgrammaticSpec(String scriptRef, WhenCondition whenExpr, UpdateWhen updateWhen, String scriptRef2) {
            this(scriptRef, whenExpr, updateWhen, scriptRef2, null);
        }
        @Override public String scriptRef() { return scriptRef; }
        @Override public WhenCondition whenExpr() { return whenExpr; }
        @Override public UpdateWhen updateWhen() { return updateWhen; }
        @Override public String id() { return id; }
    }
}

