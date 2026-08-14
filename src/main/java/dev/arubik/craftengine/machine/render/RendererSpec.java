package dev.arubik.craftengine.machine.render;

/**
 * One entry in the JSON {@code "renderers"} array.
 *
 * <p>Each variant describes a different renderer type. The {@link #whenExpr()} field
 * is a condition expression (or {@code "$varname"} reference) that controls whether
 * the renderer is active this tick. Use {@code "always"} (or omit the field in JSON)
 * to make a renderer unconditionally active.</p>
 *
 * <p>Speed / animation fields accept:</p>
 * <ul>
 *   <li>A {@code "$varname"} reference to a {@link variable.VariableSpec.NumExpr} variable.</li>
 *   <li>A bare numeric literal, e.g. {@code "1.5"}.</li>
 *   <li>An inline expression evaluated by {@link SpeedFormula}, e.g. {@code "rpm / 20"}.</li>
 *   <li>{@code null} — treated as {@code "1.0"} by evaluators.</li>
 * </ul>
 *
 * <h3>Position resolution (locationExpr)</h3>
 * <p>All position-bearing spec records carry a single {@code locationExpr} (String, nullable)
 * that replaces the legacy three-field {@code offset_x/y/z} system. The expression is evaluated
 * at runtime via PolyFormula and the result is resolved according to the following rules:</p>
 * <ul>
 *   <li>{@code Obj(LocationClass)} — absolute world coordinates (machine position is NOT added).</li>
 *   <li>{@code Array[3]} of numbers — relative offset added to machine centre (x+0.5, y, z+0.5).</li>
 *   <li>{@code null} or blank — render at machine centre (0 offset).</li>
 * </ul>
 * <p>JSON examples:</p>
 * <pre>
 *   { "location": [0.5, 1.0, 0] }                 → relative offset
 *   { "location": {"x": "0.5", "y": "rpm * 0.1", "z": "0"} } → expression-per-axis
 *   { "location": "Machine.location" }             → absolute (evaluates to LocationClass)
 *   { "locations": "entities(x,y,z,5).map_prop('location')" } → multi-position
 * </pre>
 */
public sealed interface RendererSpec {

    /**
     * The condition that must be true for this renderer to be active.
     * {@code "always"} when the JSON field is omitted.
     * Evaluated each tick by
     * {@link variable.MachineRenderContext#evalBool(String, java.util.Map)}.
     */
    String whenExpr();

    /**
     * Controls how often this renderer re-evaluates its condition and visuals.
     *
     * <table>
     *   <tr><th>Value</th><th>Meaning</th></tr>
     *   <tr><td>{@code "always"}</td><td>Re-evaluate every tick (default).</td></tr>
     *   <tr><td>{@code "inventory"}</td><td>Re-evaluate when the machine's inventory changes.</td></tr>
     *   <tr><td>{@code "state"}</td><td>Re-evaluate when the block state changes.</td></tr>
     *   <tr><td>{@code "processing"}</td><td>Re-evaluate when processing starts or stops.</td></tr>
     *   <tr><td>{@code "never"}</td><td>Static — render once and never re-evaluate.</td></tr>
     * </table>
     *
     * <p>Actual event-driven wiring is deferred; the field is parsed and stored
     * so future renderer passes can honour it.
     */
    String updateWhen();

    /**
     * Optional {@code .pf} script reference.  The value is the script base-name
     * (without the {@code .pf} suffix), e.g. {@code "crusher"} for
     * {@code scripts/crusher.pf}.  Defaults to {@code null} (no script).
     * Records that carry a {@code scriptRef} field override this default.
     */
    default String scriptRef() { return null; }

    /**
     * Optional single-position expression (JSON key {@code "location"} string form).
     * When non-null, evaluated each tick as a PolyFormula.  Result is resolved per the
     * absolute/relative rule described in the class Javadoc.
     * Defaults to {@code null}; records that carry a {@code locationExpr} field override this.
     */
    default String locationExpr() { return null; }

    /**
     * Optional multi-position expression (JSON key {@code "positions"} or {@code "locations"}).
     * When non-null the renderer fires once per resolved position.
     * Accepts a PolyFormula returning an {@code Array} of {@code Obj(LocationClass)} or
     * relative {@code Array[3]} elements.
     */
    default String positionsExpr() { return null; }

    /**
     * Alias for {@link #positionsExpr()}.  JSON key {@code "locations"} maps to this.
     * The default delegates to {@link #positionsExpr()} so both names are equivalent.
     */
    default String locationsExpr() { return positionsExpr(); }

    // ---- Concrete variants -------------------------------------------------

    /**
     * Render using <a href="https://github.com/toxicity188/BetterModel">BetterModel</a>.
     *
     * @param modelId   BetterModel model id (e.g. {@code "crusher"}).
     * @param animation Animation name to loop while active, or {@code null} for idle pose only.
     * @param speedExpr Animation playback speed expression, or {@code null} for {@code 1.0}.
     * @param whenExpr  Condition; {@code "always"} if omitted.
     */
    record BetterModelSpec(
            String modelId,
            String animation,
            String speedExpr,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Render using ModelEngine R4 (via reflection — no compile-time dependency).
     *
     * @param modelId   ModelEngine blueprint id.
     * @param animation Animation name to loop while active, or {@code null} for idle pose only.
     * @param speedExpr Animation playback speed expression, or {@code null} for {@code 1.0}.
     * @param whenExpr  Condition; {@code "always"} if omitted.
     */
    record ModelEngineSpec(
            String modelId,
            String animation,
            String speedExpr,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Show a floating item display entity above (or near) the machine block.
     *
     * @param itemExpr     {@code "$varname"} referencing a {@link variable.VariableSpec.ItemSlot},
     *                     a PolyFormula expression such as {@code "slot(9)"}, or a literal item
     *                     material name (e.g. {@code "DIAMOND"}).
     * @param locationExpr Position expression — see class Javadoc for resolution rules.
     *                     {@code null} renders at machine centre.
     * @param scale        Uniform display scale expression. Default {@code "1.0"}.
     * @param rotX         Pitch rotation expression (degrees). Default {@code "0"}.
     * @param rotY         Yaw rotation expression (degrees). Default {@code "0"}.
     * @param rotZ         Roll rotation expression (degrees). Default {@code "0"}.
     * @param billboard    Billboard mode: {@code "none"}, {@code "vertical"},
     *                     {@code "horizontal"}, or {@code "center"}.
     * @param whenExpr     Condition; {@code "always"} if omitted.
     */
    record ItemDisplaySpec(
            String itemExpr,
            String locationExpr,
            String scale,
            String rotX,
            String rotY,
            String rotZ,
            String billboard,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Emits particles at a position near the machine block each tick (or at {@code interval}).
     * All numeric fields accept PolyFormula expressions evaluated against the machine context.
     *
     * @param particle      Bukkit Particle name (case-insensitive), e.g. {@code "FLAME"}.
     * @param countExpr     Number of particles per emission; default {@code "1"}.
     * @param locationExpr  Position expression — see class Javadoc. Default: machine centre.
     * @param spreadXExpr   Extra random spread radius on each axis; default {@code "0.0"} each.
     * @param spreadYExpr   (see spreadXExpr)
     * @param spreadZExpr   (see spreadXExpr)
     * @param speedExpr     Particle speed; default {@code "0.05"}.
     * @param dirXExpr      Directional velocity override (dx/dy/dz). Default {@code "0"} each.
     * @param dirYExpr      (see dirXExpr)
     * @param dirZExpr      (see dirXExpr)
     * @param shape         Emitter shape — one of {@link ParticleUtils.Shape}'s names.
     * @param directionMode Direction mode — one of {@link ParticleUtils.Direction}'s names.
     * @param interval      Ticks between emissions; default {@code 1}.
     * @param whenExpr      Condition; {@code "always"} if omitted.
     * @param updateWhen    Re-evaluation trigger; {@code "always"} if omitted.
     */
    record ParticleSpec(
            String particle,
            String countExpr,
            String locationExpr,
            String spreadXExpr,
            String spreadYExpr,
            String spreadZExpr,
            String speedExpr,
            String dirXExpr,
            String dirYExpr,
            String dirZExpr,
            String shape,
            String directionMode,
            int interval,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Renders a CE fluid level indicator above the machine using an ITEM_DISPLAY entity.
     * Finds the named fluid (or gas) tank and renders the {@code cml:fluidlvl_<type>_<level>} item.
     *
     * @param tankName     Name of the fluid/gas tank ({@code ""} = first available tank).
     * @param isGas        {@code false} = fluid tank, {@code true} = gas tank.
     * @param locationExpr Position expression — see class Javadoc. Default: machine centre.
     * @param maxHeight    Maximum Y scale when the tank is full; default {@code 0.875}.
     * @param whenExpr     Condition; {@code "always"} if omitted.
     * @param updateWhen   Re-evaluation trigger; {@code "always"} if omitted.
     */
    record FluidTankSpec(
            String tankName,
            boolean isGas,
            String locationExpr,
            float maxHeight,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Show a floating TEXT_DISPLAY entity with dynamically-evaluated text.
     *
     * @param textExpr         PolyFormula expression for the displayed text.
     * @param locationExpr     Position expression — see class Javadoc. Default: machine centre.
     * @param scale            Uniform scale expression. Default {@code "0.1"}.
     * @param rotX             Pitch expression (degrees). Default {@code "0"}.
     * @param rotY             Yaw expression (degrees). Default {@code "0"}.
     * @param rotZ             Roll expression (degrees). Default {@code "0"}.
     * @param billboard        {@code "none"}, {@code "vertical"}, {@code "horizontal"},
     *                         or {@code "center"} (default).
     * @param lineWidth        Maximum line width in pixels. Default {@code 200}.
     * @param backgroundExpr   ARGB hex colour string or {@code "0"} for transparent. Default {@code "0"}.
     * @param shadow           Whether text casts a drop shadow. Default {@code false}.
     * @param seeThrough       Whether text renders through walls. Default {@code false}.
     * @param alignment        {@code "left"}, {@code "center"} (default), or {@code "right"}.
     * @param opacity          Text opacity 0–255. Default {@code 255}.
     * @param whenExpr         Condition; {@code "always"} if omitted.
     * @param updateWhen       Re-evaluation trigger; {@code "always"} if omitted.
     * @param scriptRef        Optional {@code .pf} script reference.
     */
    record TextDisplaySpec(
            String textExpr,
            String locationExpr,
            String scale,
            String rotX,
            String rotY,
            String rotZ,
            String billboard,
            int lineWidth,
            String backgroundExpr,
            boolean shadow,
            boolean seeThrough,
            String alignment,
            int opacity,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Plays a sound at the machine position periodically or once on activation.
     *
     * @param soundId      Sound resource location (e.g. "minecraft:block.anvil.use").
     * @param volumeExpr   Volume expression (0.0–1.0+). Default {@code "1.0"}.
     * @param pitchExpr    Pitch expression (0.5–2.0). Default {@code "1.0"}.
     * @param interval     Ticks between plays (0 = play once on activate). Default {@code 20}.
     * @param whenExpr     Condition; {@code "always"} if omitted.
     * @param updateWhen   Re-evaluation trigger; {@code "always"} if omitted.
     * @param scriptRef    Optional {@code .pf} script reference.
     */
    record SoundSpec(
            String soundId,
            String volumeExpr,
            String pitchExpr,
            int interval,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Spawn a packet-only armor stand for equipment display near the machine.
     *
     * @param headItemExpr Item to show as head (helmet slot) — formula or literal. May be {@code null}.
     * @param bodyItemExpr Chestplate slot item. May be {@code null}.
     * @param locationExpr Position expression — see class Javadoc. Default: machine centre.
     * @param rotX         Pitch rotation expression (degrees).
     * @param rotY         Yaw rotation expression (degrees).
     * @param rotZ         Roll rotation expression (degrees).
     * @param small        Whether to use a small armor stand.
     * @param invisible    Whether the armor stand body is invisible.
     * @param marker       Whether the armor stand has no hitbox.
     * @param whenExpr     Condition; {@code "always"} if omitted.
     * @param updateWhen   Re-evaluation trigger; {@code "always"} if omitted.
     * @param scriptRef    Optional {@code .pf} script reference.
     */
    record ArmorStandSpec(
            String headItemExpr,
            String bodyItemExpr,
            String locationExpr,
            String rotX,
            String rotY,
            String rotZ,
            boolean small,
            boolean invisible,
            boolean marker,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Show a fake block display entity near the machine.
     *
     * @param blockStateExpr Block state expression (e.g. "minecraft:stone" or formula).
     * @param locationExpr   Position expression — see class Javadoc. Default: machine centre.
     * @param scale          Uniform scale expression.
     * @param rotX           Pitch rotation expression (degrees).
     * @param rotY           Yaw rotation expression (degrees).
     * @param rotZ           Roll rotation expression (degrees).
     * @param whenExpr       Condition; {@code "always"} if omitted.
     * @param updateWhen     Re-evaluation trigger; {@code "always"} if omitted.
     * @param scriptRef      Optional {@code .pf} script reference.
     */
    record BlockDisplaySpec(
            String blockStateExpr,
            String locationExpr,
            String scale,
            String rotX,
            String rotY,
            String rotZ,
            String whenExpr,
            String updateWhen,
            String scriptRef
    ) implements RendererSpec {}

    /**
     * Wrapper that adds multi-position support ({@link #positionsExpr()}) and optionally
     * overrides the single-position {@link #locationExpr()} on any inner spec.
     * Created by the loader when the JSON {@code "positions"}, {@code "locations"}, or a
     * string-form {@code "location"} field is present.
     * All other interface methods delegate to {@link #inner()}.
     *
     * <p>In {@link RendererManager#tick} the spec is unwrapped via
     * {@code spec instanceof PositionedSpec ps ? ps.inner() : spec} for type dispatch,
     * while {@code spec.positionsExpr()} / {@code spec.locationExpr()} are used for
     * position resolution.</p>
     */
    record PositionedSpec(
            RendererSpec inner,
            String positionsExpr,
            String locationExprOverride
    ) implements RendererSpec {
        @Override public String whenExpr()    { return inner.whenExpr(); }
        @Override public String updateWhen()  { return inner.updateWhen(); }
        @Override public String scriptRef()   { return inner.scriptRef(); }
        /** Returns the override if set, otherwise delegates to the inner spec's locationExpr. */
        @Override public String locationExpr() {
            return locationExprOverride != null ? locationExprOverride : inner.locationExpr();
        }
    }

    /**
     * A snapshot of one {@link ItemDisplaySpec}'s evaluated display fields, computed by
     * {@link RendererManager} each tick with the live machine context. Used by
     * {@code ContraptionMachineRendererElement} to render item displays in moving contraptions
     * with formula-evaluated offsets/scale/rotation rather than re-evaluating against an empty
     * context.
     *
     * <p>Without this, formula-based location expressions like {@code "rpm * 0.01"} would always
     * evaluate to 0 inside the contraption element, because the element has no machine context
     * at render time.</p>
     *
     * <p>{@code offsetX/Y/Z} are always RELATIVE to the block centre — even when {@code locationExpr}
     * resolved to an absolute {@code LocationClass}, the absolute coords are converted to a relative
     * offset so the contraption element's transform logic is unaffected.</p>
     */
    record EvaluatedItemDisplay(
            int specIndex,
            org.bukkit.inventory.ItemStack item,
            double offsetX, double offsetY, double offsetZ,
            float scale, float rotX, float rotY, float rotZ
    ) {}
}
