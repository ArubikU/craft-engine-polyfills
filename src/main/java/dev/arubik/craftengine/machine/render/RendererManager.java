package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import dev.arubik.craftengine.machine.render.formula.LocationClass;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

import java.util.*;

public final class RendererManager {

    // One entry per RendererSpec — shares the same index
    private final List<RendererSpec> specs;
    private final Map<String, VariableSpec> varSpecs;

    // Runtime renderer instances — null if type is ItemDisplay
    private final List<BetterModelMachineRenderer> bmRenderers;  // null entry if not BetterModel spec
    private final List<ModelEngineMachineRenderer> meRenderers;  // null entry if not ModelEngine spec

    // Current speed per renderer (mutable double for supplier closure)
    private final double[] currentSpeeds;  // length = specs.size()

    // Live item for ItemDisplay entries
    private final ItemStack[] currentItems;  // null except for ItemDisplaySpec entries

    // Per-spec particle emission cooldown (only meaningful for ParticleSpec entries)
    private final int[] particleCooldowns;

    // Per-spec fluid level display helper (only non-null for FluidTankSpec entries)
    private final FluidLevelDisplay[] fluidDisplays;

    // Per-spec text display helper (only non-null for TextDisplaySpec entries)
    private final TextLevelDisplay[] textDisplays;

    // Per-spec armor stand display helper (only non-null for ArmorStandSpec entries)
    private final ArmorStandDisplay[] armorStandDisplays;

    // Per-spec block display helper (only non-null for BlockDisplaySpec entries)
    private final BlockDisplayHelper[] blockDisplayHelpers;

    // ---- Per-spec cached evaluation state for ContraptionMachineRendererElement ----

    /**
     * Cached per-spec state snapshot populated by each {@link #tick} call.
     * Read by {@link dev.arubik.craftengine.contraption.element.ContraptionMachineRendererElement}
     * to render data-driven machine visuals inside moving contraptions with proper world-space
     * transforms applied via {@code ContraptionMath.renderPosition}.
     */
    public static final class EvalResult {
        /** Whether this spec's {@code whenExpr} evaluated true during the last tick. */
        public boolean active;

        // --- ItemDisplaySpec (non-null only while active) ---
        public RendererSpec.EvaluatedItemDisplay itemDisplay;

        // --- ParticleSpec: set after the cooldown fires, before the qualifying-players check ---
        /** True when the cooldown fired this tick (regardless of whether there were nearby players). */
        public boolean emittedThisTick;
        public int particleCount;
        public double pSpreadX, pSpreadY, pSpreadZ;
        public double pSpeed;
        public double pDvx, pDvy, pDvz;
        public String pType;
        public dev.arubik.craftengine.machine.render.ParticleUtils.Shape pShape;
        public dev.arubik.craftengine.machine.render.ParticleUtils.Direction pDir;

        // --- FluidTankSpec ---
        public int ceFluidLevel;
        public String fluidTypeValue;

        /**
         * Resolved position offset (relative to block centre) for the last tick.
         * Populated by FluidTankSpec and ParticleSpec for use by
         * {@link dev.arubik.craftengine.contraption.element.ContraptionMachineRendererElement},
         * which needs to apply the same offset in contraption-local space.
         * X and Z are relative to block centre; Y is relative to block bottom.
         */
        public double specRelX, specRelY, specRelZ;

        // --- TextDisplaySpec ---
        public String textContent;
        public double tdOx, tdOy, tdOz;
        public float textScale;
    }

    private final EvalResult[] evalResults;

    public RendererManager(List<RendererSpec> specs, Map<String, VariableSpec> varSpecs) {
        this.specs = List.copyOf(specs);
        this.varSpecs = varSpecs;
        int n = specs.size();
        this.bmRenderers = new ArrayList<>(n);
        this.meRenderers = new ArrayList<>(n);
        this.currentSpeeds = new double[n];
        Arrays.fill(currentSpeeds, 1.0);
        this.currentItems = new ItemStack[n];
        this.particleCooldowns = new int[n];
        this.fluidDisplays = new FluidLevelDisplay[n];
        this.textDisplays  = new TextLevelDisplay[n];
        this.armorStandDisplays = new ArmorStandDisplay[n];
        this.blockDisplayHelpers = new BlockDisplayHelper[n];
        this.evalResults = new EvalResult[n];
        for (int k = 0; k < n; k++) evalResults[k] = new EvalResult();

        for (int i = 0; i < n; i++) {
            final int idx = i;
            RendererSpec spec = specs.get(i);
            // Unwrap PositionedSpec for type dispatch — the wrapper only adds position metadata.
            RendererSpec actualSpec = spec instanceof RendererSpec.PositionedSpec ps ? ps.inner() : spec;
            if (actualSpec instanceof RendererSpec.BetterModelSpec bm) {
                bmRenderers.add(new BetterModelMachineRenderer(bm.modelId(), () -> currentSpeeds[idx]));
                meRenderers.add(null);
            } else if (actualSpec instanceof RendererSpec.ModelEngineSpec me) {
                bmRenderers.add(null);
                meRenderers.add(new ModelEngineMachineRenderer(me.modelId(), () -> currentSpeeds[idx]));
            } else {
                bmRenderers.add(null);
                meRenderers.add(null);
                if (actualSpec instanceof RendererSpec.FluidTankSpec) {
                    fluidDisplays[i] = new FluidLevelDisplay();
                } else if (actualSpec instanceof RendererSpec.TextDisplaySpec) {
                    textDisplays[i] = new TextLevelDisplay();
                } else if (actualSpec instanceof RendererSpec.ArmorStandSpec) {
                    armorStandDisplays[i] = new ArmorStandDisplay();
                } else if (actualSpec instanceof RendererSpec.BlockDisplaySpec) {
                    blockDisplayHelpers[i] = new BlockDisplayHelper();
                }
            }
        }
    }

    /**
     * Called each machine tick. Evaluates conditions, updates speeds, shows/hides renderers.
     *
     * <p>Particle specs are evaluated <em>per-viewer</em>: the spec's {@code whenExpr} is
     * re-evaluated for each nearby {@link ServerPlayer} with a context whose
     * {@code Machine.player_facing()} / {@code player_in_range()} functions check only that
     * specific player. Particles are only sent to players whose condition passes.</p>
     *
     * @param ctx         evaluated variable snapshot
     * @param serverLevel the NMS ServerLevel the machine lives in
     * @param x           block X position
     * @param y           block Y position
     * @param z           block Z position
     * @param yaw         facing yaw degrees (south=0, west=90, north=180, east=270)
     */
    public void tick(MachineRenderContext ctx, ServerLevel serverLevel, double x, double y, double z, float yaw) {
        // BetterModel / ModelEngine renderers still use the Bukkit World wrapper.
        World bukkitWorld = serverLevel != null ? serverLevel.getWorld() : null;

        for (int i = 0; i < specs.size(); i++) {
            RendererSpec spec = specs.get(i);
            // Unwrap PositionedSpec for type dispatch while keeping spec for locationExpr/positionsExpr.
            RendererSpec actualSpec = spec instanceof RendererSpec.PositionedSpec ps ? ps.inner() : spec;

            MachineRenderContext evalCtx = ctx;
            String scriptRef = spec.scriptRef();
            if (scriptRef != null) {
                dev.arubik.craftengine.machine.render.formula.PolyScript script =
                        dev.arubik.craftengine.machine.render.formula.PolyScriptRegistry.get(scriptRef);
                if (script != null) {
                    dev.arubik.craftengine.machine.render.formula.PolyContext augmented =
                            script.evaluate(ctx.toPolyContext());
                    evalCtx = ctx.augmented(augmented);
                }
            }

            // Global condition — used for BM/ME/Item/Fluid/Text (not per-player).
            // Particle specs still use this as a fast-path skip (if globally false, nobody
            // sees particles), and then additionally evaluate per-player below.
            boolean active = evalCtx.evalBool(spec.whenExpr(), varSpecs);
            // Cache active state and reset per-tick flags for ContraptionMachineRendererElement.
            evalResults[i].active = active;
            evalResults[i].emittedThisTick = false;

            if (actualSpec instanceof RendererSpec.BetterModelSpec bm) {
                BetterModelMachineRenderer r = bmRenderers.get(i);
                if (r == null) continue;
                if (active) {
                    currentSpeeds[i] = evalCtx.evalNum(bm.speedExpr() != null ? bm.speedExpr() : "1.0", varSpecs);
                    r.setLocation(bukkitWorld, x, y, z, yaw);
                    r.show();
                    if (bm.animation() != null) r.playLoop(bm.animation());
                    else r.stopAnim();
                } else {
                    r.stopAnim();
                    r.close();
                }
            } else if (actualSpec instanceof RendererSpec.ModelEngineSpec me) {
                ModelEngineMachineRenderer r = meRenderers.get(i);
                if (r == null) continue;
                if (active) {
                    currentSpeeds[i] = evalCtx.evalNum(me.speedExpr() != null ? me.speedExpr() : "1.0", varSpecs);
                    r.setLocation(bukkitWorld, x, y, z, yaw);
                    r.show();
                    if (me.animation() != null) r.playLoop(me.animation());
                    else r.stopAnim();
                } else {
                    r.close();
                }
            } else if (actualSpec instanceof RendererSpec.ItemDisplaySpec id) {
                if (active) {
                    if (id.itemExpr().startsWith("$")) {
                        currentItems[i] = evalCtx.evalItem(id.itemExpr(), varSpecs);
                    } else {
                        if (currentItems[i] == null) {
                            try {
                                org.bukkit.Material mat = org.bukkit.Material.matchMaterial(id.itemExpr());
                                currentItems[i] = mat != null ? new ItemStack(mat) : null;
                            } catch (Throwable ignored) {}
                        }
                    }
                    // Resolve world position (and optional rotation) from locationExpr.
                    // Store as relative offset for ContraptionMachineRendererElement.
                    try {
                        double[] wp = resolveSpecLocation(spec, evalCtx, x, y, z);
                        double relX = wp[0] - (x + 0.5);
                        double relY = wp[1] - y;
                        double relZ = wp[2] - (z + 0.5);
                        // Use rotation from location array if provided; otherwise fall back to spec fields.
                        float rotX = !Double.isNaN(wp[3]) ? (float) wp[3]
                                : (float) evalCtx.evalNum(id.rotX() != null ? id.rotX() : "0", varSpecs);
                        float rotY = !Double.isNaN(wp[4]) ? (float) wp[4]
                                : (float) evalCtx.evalNum(id.rotY() != null ? id.rotY() : "0", varSpecs);
                        float rotZ = !Double.isNaN(wp[5]) ? (float) wp[5]
                                : (float) evalCtx.evalNum(id.rotZ() != null ? id.rotZ() : "0", varSpecs);
                        evalResults[i].itemDisplay = new RendererSpec.EvaluatedItemDisplay(
                                i, currentItems[i],
                                relX, relY, relZ,
                                (float) evalCtx.evalNum(id.scale() != null && !id.scale().isEmpty() ? id.scale() : "1", varSpecs),
                                rotX, rotY, rotZ
                        );
                    } catch (Throwable ignored) {}
                } else {
                    currentItems[i] = null;
                    evalResults[i].itemDisplay = null;
                }
            } else if (actualSpec instanceof RendererSpec.ParticleSpec ps) {
                if (!active) { particleCooldowns[i] = 0; continue; }
                particleCooldowns[i]++;
                if (particleCooldowns[i] < ps.interval()) continue;
                particleCooldowns[i] = 0;

                int count = (int) Math.max(0, evalCtx.evalNum(ps.countExpr() != null ? ps.countExpr() : "1", varSpecs));
                if (count == 0) continue;

                double spreadX = evalCtx.evalNum(ps.spreadXExpr() != null ? ps.spreadXExpr() : "0", varSpecs);
                double spreadY = evalCtx.evalNum(ps.spreadYExpr() != null ? ps.spreadYExpr() : "0", varSpecs);
                double spreadZ = evalCtx.evalNum(ps.spreadZExpr() != null ? ps.spreadZExpr() : "0", varSpecs);
                double spd = evalCtx.evalNum(ps.speedExpr() != null ? ps.speedExpr() : "0.05", varSpecs);
                double dvx = evalCtx.evalNum(ps.dirXExpr() != null ? ps.dirXExpr() : "0", varSpecs);
                double dvy = evalCtx.evalNum(ps.dirYExpr() != null ? ps.dirYExpr() : "0", varSpecs);
                double dvz = evalCtx.evalNum(ps.dirZExpr() != null ? ps.dirZExpr() : "0", varSpecs);

                ParticleUtils.Shape emitShape = ParticleUtils.Shape.fromName(
                        ps.shape(), ParticleUtils.Shape.POINT);
                ParticleUtils.Direction emitDir;
                if (ps.directionMode() != null && !ps.directionMode().isEmpty()) {
                    emitDir = ParticleUtils.Direction.fromName(
                            ps.directionMode(), ParticleUtils.Direction.RANDOM);
                } else {
                    emitDir = (dvx != 0 || dvy != 0 || dvz != 0)
                            ? ParticleUtils.Direction.CUSTOM
                            : ParticleUtils.Direction.RANDOM;
                }

                // Cache evaluated params for ContraptionMachineRendererElement.
                evalResults[i].emittedThisTick = true;
                evalResults[i].particleCount = count;
                evalResults[i].pSpreadX = spreadX; evalResults[i].pSpreadY = spreadY; evalResults[i].pSpreadZ = spreadZ;
                evalResults[i].pSpeed = spd;
                evalResults[i].pDvx = dvx; evalResults[i].pDvy = dvy; evalResults[i].pDvz = dvz;
                evalResults[i].pType = ps.particle();
                evalResults[i].pShape = emitShape;
                evalResults[i].pDir = emitDir;

                // Store resolved relative offset for ContraptionMachineRendererElement.
                {
                    double[] ep0 = resolveSpecLocation(spec, evalCtx, x, y, z);
                    evalResults[i].specRelX = ep0[0] - (x + 0.5);
                    evalResults[i].specRelY = ep0[1] - y;
                    evalResults[i].specRelZ = ep0[2] - (z + 0.5);
                }

                try {
                    net.minecraft.core.particles.ParticleOptions particleType = resolveParticle(ps.particle());
                    if (particleType == null) continue;

                    // Determine emission positions: multi-position list takes priority, then single location.
                    java.util.List<double[]> emitPositions = resolvePositions(spec, evalCtx, x, y, z);
                    if (emitPositions.isEmpty()) {
                        emitPositions = java.util.List.of(resolveSpecLocation(spec, evalCtx, x, y, z));
                    }

                    final double RANGE_SQ = 48.0 * 48.0;
                    final String whenExpr = spec.whenExpr();

                    for (double[] ep : emitPositions) {
                        final double wx = ep[0], wy = ep[1], wz = ep[2];
                        // ---- Per-player condition evaluation --------------------------------
                        List<ServerPlayer> qualifying = new ArrayList<>();
                        for (ServerPlayer sp : serverLevel.players()) {
                            double ddx = sp.getX() - wx, ddy = sp.getY() - wy, ddz = sp.getZ() - wz;
                            if (ddx * ddx + ddy * ddy + ddz * ddz > RANGE_SQ) continue;
                            MachineRenderContext playerCtx = buildPlayerContext(evalCtx, sp, x, y, z, yaw);
                            if (playerCtx.evalBool(whenExpr, varSpecs)) qualifying.add(sp);
                        }
                        if (qualifying.isEmpty()) continue;

                        // ---- Particle emission -----------------------------------------------
                        if (emitShape == ParticleUtils.Shape.POINT && emitDir == ParticleUtils.Direction.RANDOM) {
                            ClientboundLevelParticlesPacket pkt = new ClientboundLevelParticlesPacket(
                                    particleType, false, false, wx, wy, wz,
                                    (float) spreadX, (float) spreadY, (float) spreadZ, (float) spd, count);
                            for (ServerPlayer sp : qualifying) {
                                try { sp.connection.send(pkt); } catch (Throwable ignored) {}
                            }
                        } else {
                            java.util.concurrent.ThreadLocalRandom rng =
                                    java.util.concurrent.ThreadLocalRandom.current();
                            for (int k = 0; k < count; k++) {
                                double[] samplePos = ParticleUtils.sampleShape(rng, emitShape, spreadX, spreadY, spreadZ);
                                double lx = samplePos[0], ly = samplePos[1], lz = samplePos[2];
                                double ppx = wx + lx, ppy = wy + ly, ppz = wz + lz;
                                double[] vel = ParticleUtils.computeDirection(rng, emitDir,
                                        lx, ly, lz, dvx, dvy, dvz, spd);
                                ClientboundLevelParticlesPacket pkt;
                                if (emitDir == ParticleUtils.Direction.RANDOM) {
                                    pkt = new ClientboundLevelParticlesPacket(
                                            particleType, false, false, ppx, ppy, ppz,
                                            (float) spreadX, (float) spreadY, (float) spreadZ, (float) spd, 1);
                                } else {
                                    pkt = new ClientboundLevelParticlesPacket(
                                            particleType, false, false, ppx, ppy, ppz,
                                            (float) vel[0], (float) vel[1], (float) vel[2], (float) spd, 0);
                                }
                                for (ServerPlayer sp : qualifying) {
                                    try { sp.connection.send(pkt); } catch (Throwable ignored) {}
                                }
                            }
                        }
                    }
                } catch (Throwable ignored) {}

            } else if (actualSpec instanceof RendererSpec.FluidTankSpec ft) {
                FluidLevelDisplay display = fluidDisplays[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                String varKey = (ft.isGas() ? "gas_" : "fluid_")
                        + (ft.tankName().isEmpty() ? "0" : ft.tankName());
                int ceLevel = (int) Math.max(0, Math.min(16,
                        evalCtx.evalNum(varKey, varSpecs)));

                String fluidTypeValue = ft.isGas() ? "steam" : "water";
                Key fluidKey = Key.of("cml", "fluidlvl_" + fluidTypeValue + "_" + ceLevel);

                float scaleY = ceLevel <= 0 ? 0f : ft.maxHeight() * (ceLevel / 16f);

                double[] wp = resolveSpecLocation(spec, evalCtx, x, y, z);

                // Cache for ContraptionMachineRendererElement.
                evalResults[i].ceFluidLevel = ceLevel;
                evalResults[i].fluidTypeValue = fluidTypeValue;
                evalResults[i].specRelX = wp[0] - (x + 0.5);
                evalResults[i].specRelY = wp[1] - y;
                evalResults[i].specRelZ = wp[2] - (z + 0.5);

                display.update(serverLevel, wp[0], wp[1], wp[2], fluidKey, scaleY);
            } else if (actualSpec instanceof RendererSpec.TextDisplaySpec td) {
                TextLevelDisplay display = textDisplays[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                String text;
                try {
                    text = dev.arubik.craftengine.machine.render.formula.PolyFormula
                            .compile(td.textExpr() != null && !td.textExpr().isEmpty() ? td.textExpr() : "\"\"")
                            .evaluateStr(evalCtx.toPolyContext());
                } catch (Throwable ignored) {
                    text = td.textExpr() != null ? td.textExpr() : "";
                }
                double[] wp = resolveSpecLocation(spec, evalCtx, x, y, z);
                float scaleVal = (float) evalCtx.evalNum(td.scale() != null && !td.scale().isEmpty() ? td.scale() : "0.1", varSpecs);
                // Cache for ContraptionMachineRendererElement — store as relative offset.
                evalResults[i].textContent = text;
                evalResults[i].tdOx = wp[0] - (x + 0.5);
                evalResults[i].tdOy = wp[1] - y;
                evalResults[i].tdOz = wp[2] - (z + 0.5);
                evalResults[i].textScale = scaleVal;
                // Rotation from location is not currently consumed by TextLevelDisplay but is available via EvalResult.
                display.update(serverLevel, wp[0], wp[1], wp[2],
                        text, scaleVal,
                        td.billboard(), td.lineWidth(), td.backgroundExpr(),
                        td.shadow(), td.seeThrough(), td.alignment(), td.opacity());
            } else if (actualSpec instanceof RendererSpec.SoundSpec ss) {
                if (!active) { particleCooldowns[i] = 0; continue; }
                particleCooldowns[i]++;
                if (ss.interval() > 0 && particleCooldowns[i] < ss.interval()) continue;
                particleCooldowns[i] = 0;
                try {
                    float vol = (float) evalCtx.evalNum(ss.volumeExpr(), varSpecs);
                    float pitch = (float) evalCtx.evalNum(ss.pitchExpr(), varSpecs);
                    for (ServerPlayer sp : serverLevel.players()) {
                        if (sp.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > 48 * 48) continue;
                        sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSoundPacket(
                                net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(
                                        net.minecraft.sounds.SoundEvent.createVariableRangeEvent(
                                                net.minecraft.resources.Identifier.tryParse(ss.soundId()))),
                                net.minecraft.sounds.SoundSource.BLOCKS,
                                x + 0.5, y + 0.5, z + 0.5, vol, pitch, serverLevel.random.nextLong()));
                    }
                } catch (Throwable ignored) {}
            } else if (actualSpec instanceof RendererSpec.ArmorStandSpec as) {
                ArmorStandDisplay display = armorStandDisplays[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                double[] wp = resolveSpecLocation(spec, evalCtx, x, y, z);
                display.update(serverLevel, wp[0], wp[1], wp[2],
                        as.small(), as.invisible(), as.marker());
            } else if (actualSpec instanceof RendererSpec.BlockDisplaySpec bd) {
                BlockDisplayHelper display = blockDisplayHelpers[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                double[] wp = resolveSpecLocation(spec, evalCtx, x, y, z);
                float scaleVal = (float) evalCtx.evalNum(bd.scale() != null && !bd.scale().isEmpty() ? bd.scale() : "1.0", varSpecs);
                String blockId;
                try {
                    blockId = dev.arubik.craftengine.machine.render.formula.PolyFormula
                            .compile(bd.blockStateExpr())
                            .evaluateStr(evalCtx.toPolyContext());
                } catch (Throwable ignored) {
                    blockId = bd.blockStateExpr();
                }
                display.update(serverLevel, wp[0], wp[1], wp[2], blockId, scaleVal);
            }
        }
    }

    /** Close all renderers (on block break / BE unload). */
    public void close() {
        for (BetterModelMachineRenderer r : bmRenderers) { if (r != null) r.close(); }
        for (ModelEngineMachineRenderer r : meRenderers) { if (r != null) r.close(); }
        Arrays.fill(currentItems, null);
        for (FluidLevelDisplay d : fluidDisplays) {
            if (d != null) d.despawnAll(null);
        }
        for (TextLevelDisplay d : textDisplays) {
            if (d != null) d.despawnAll(null);
        }
        for (ArmorStandDisplay d : armorStandDisplays) {
            if (d != null) d.despawnAll(null);
        }
        for (BlockDisplayHelper d : blockDisplayHelpers) {
            if (d != null) d.despawnAll(null);
        }
    }

    /** All BetterModel renderers (some may be null if spec is not BM type). */
    public List<BetterModelMachineRenderer> betterModelRenderers() {
        return Collections.unmodifiableList(bmRenderers);
    }

    /** All ModelEngine renderers (some may be null if spec is not ME type). */
    public List<ModelEngineMachineRenderer> modelEngineRenderers() {
        return Collections.unmodifiableList(meRenderers);
    }

    /** Live item stack per ItemDisplaySpec entry (null if that spec is not ItemDisplay or not active). */
    public ItemStack[] currentItems() { return currentItems; }

    /** The renderer specs list. */
    public List<RendererSpec> specs() { return specs; }

    public boolean isEmpty() { return specs.isEmpty(); }

    /**
     * Cached per-spec evaluation state from the last {@link #tick} call, for use by
     * {@link dev.arubik.craftengine.contraption.element.ContraptionMachineRendererElement}.
     * Returns {@code null} for an out-of-range index.
     */
    public EvalResult evalResult(int i) {
        return (i >= 0 && i < evalResults.length) ? evalResults[i] : null;
    }

    // -------------------------------------------------------------------------
    // Position resolution helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves a {@link PolyValue} (the evaluated result of a {@code locationExpr}) to a
     * 6-element {@code [x, y, z, rotX, rotY, rotZ]} array where the last three may be
     * {@link Double#NaN} when no rotation was provided.
     *
     * <h3>Array element conventions</h3>
     * <ul>
     *   <li>{@code Array[3]} — {@code [x, y, z]}, no rotation</li>
     *   <li>{@code Array[4]} — {@code [x, y, z, rotY]}, yaw only (most common)</li>
     *   <li>{@code Array[5]} — {@code [x, y, z, rotX, rotY]}</li>
     *   <li>{@code Array[6+]} — {@code [x, y, z, rotX, rotY, rotZ]}</li>
     * </ul>
     * Relative arrays add to machine centre ({@code machX+0.5}, {@code machY},
     * {@code machZ+0.5}).
     *
     * <h3>Other value types</h3>
     * <ul>
     *   <li>{@code Obj(LocationClass)} → absolute world coordinates, no rotation</li>
     *   <li>{@code Num} → Y offset only; X/Z at machine centre, no rotation</li>
     *   <li>{@code null} / empty → {@code null} (caller falls back to machine centre)</li>
     * </ul>
     */
    public static double[] resolveLocation(PolyValue val, double machX, double machY, double machZ) {
        double nan = Double.NaN;
        if (val == null || val instanceof PolyValue.Null) return null;
        if (val instanceof PolyValue.Obj o && o.inner() instanceof LocationClass lc) {
            return new double[]{ lc.x(), lc.y(), lc.z(), nan, nan, nan };
        }
        if (val instanceof PolyValue.Array a && a.elements().size() >= 3) {
            java.util.List<PolyValue> e = a.elements();
            double rx = e.size() >= 5 ? e.get(3).asNum() : nan; // rotX (index 3 in 5/6-elem form)
            double ry = e.size() == 4 ? e.get(3).asNum() : (e.size() >= 5 ? e.get(4).asNum() : nan); // rotY
            double rz = e.size() >= 6 ? e.get(5).asNum() : nan;
            return new double[]{
                machX + 0.5 + e.get(0).asNum(),
                machY       + e.get(1).asNum(),
                machZ + 0.5 + e.get(2).asNum(),
                rx, ry, rz
            };
        }
        if (val instanceof PolyValue.Num n) {
            return new double[]{ machX + 0.5, machY + n.value(), machZ + 0.5, nan, nan, nan };
        }
        return null;
    }

    /** NaN placeholder indicating "rotation not provided by locationExpr". */
    private static final double LOC_NO_ROT = Double.NaN;

    /**
     * Evaluates a spec's {@code locationExpr} and returns a 6-element
     * {@code [x, y, z, rotX, rotY, rotZ]} world position+rotation array.
     * Falls back to machine centre with NaN rotation when the expression is null or fails.
     * Callers should check {@code Double.isNaN(pos[3])} etc. before using rotation values.
     */
    private double[] resolveSpecLocation(RendererSpec spec, MachineRenderContext evalCtx,
            double machX, double machY, double machZ) {
        String locExpr = spec.locationExpr();
        if (locExpr != null && !locExpr.isEmpty()) {
            try {
                PolyValue val = dev.arubik.craftengine.machine.render.formula.PolyFormula
                        .compile(locExpr).evaluate(evalCtx.toPolyContext());
                double[] pos = resolveLocation(val, machX, machY, machZ);
                if (pos != null) return pos;
            } catch (Throwable ignored) {}
        }
        return new double[]{ machX + 0.5, machY, machZ + 0.5, LOC_NO_ROT, LOC_NO_ROT, LOC_NO_ROT };
    }

    /**
     * Resolves the multi-position list from a spec's {@code positionsExpr}/{@code locationsExpr}.
     * Returns an empty list when no expression is set or evaluation fails.
     * Each returned element is a 6-element {@code [x, y, z, rotX, rotY, rotZ]} world position;
     * rotation components may be {@link Double#NaN} when not provided.
     */
    private java.util.List<double[]> resolvePositions(RendererSpec spec, MachineRenderContext evalCtx,
            double machX, double machY, double machZ) {
        String posExpr = spec.positionsExpr();
        if (posExpr == null || posExpr.isEmpty()) return java.util.List.of();
        try {
            PolyValue val = dev.arubik.craftengine.machine.render.formula.PolyFormula
                    .compile(posExpr).evaluate(evalCtx.toPolyContext());
            if (!(val instanceof PolyValue.Array arr)) return java.util.List.of();
            java.util.List<double[]> result = new java.util.ArrayList<>(arr.elements().size());
            for (PolyValue elem : arr.elements()) {
                double[] pos = resolveLocation(elem, machX, machY, machZ);
                if (pos != null) result.add(pos);
            }
            return result;
        } catch (Throwable ignored) {
            return java.util.List.of();
        }
    }

    // ---- Convenience: extract rotation component from resolved location array ----

    /** Returns the rotX from a 6-element resolved location, or {@code defaultVal} if NaN. */
    private static float locRotX(double[] loc, float defaultVal) {
        return (loc.length >= 4 && !Double.isNaN(loc[3])) ? (float) loc[3] : defaultVal;
    }
    /** Returns the rotY from a 6-element resolved location, or {@code defaultVal} if NaN. */
    private static float locRotY(double[] loc, float defaultVal) {
        return (loc.length >= 5 && !Double.isNaN(loc[4])) ? (float) loc[4] : defaultVal;
    }
    /** Returns the rotZ from a 6-element resolved location, or {@code defaultVal} if NaN. */
    private static float locRotZ(double[] loc, float defaultVal) {
        return (loc.length >= 6 && !Double.isNaN(loc[5])) ? (float) loc[5] : defaultVal;
    }

    // -------------------------------------------------------------------------
    // Per-player context helpers
    // -------------------------------------------------------------------------

    /**
     * Build a {@link MachineRenderContext} where the {@code Machine} class checks only
     * {@code sp} for all player queries ({@code player_facing}, {@code player_in_range},
     * etc.). The returned context otherwise inherits all variables from {@code base}.
     *
     * @param base the globally-evaluated context for this tick
     * @param sp   the specific viewer to test against
     * @param x    block X (not centre — the +0.5 offset is added internally)
     * @param y    block Y
     * @param z    block Z
     * @param yaw  facing yaw degrees
     */
    private MachineRenderContext buildPlayerContext(MachineRenderContext base, ServerPlayer sp,
                                                    double x, double y, double z, float yaw) {
        org.bukkit.entity.Player bukkit = (org.bukkit.craftbukkit.entity.CraftPlayer) sp.getBukkitEntity();
        String facing = yawToFacing(yaw);
        PolyContext playerPoly = PolyContext.builder()
                .copyFrom(base.toPolyContext())
                .machinePosForPlayer(x + 0.5, y + 0.5, z + 0.5, facing, yaw, bukkit)
                .build();
        return base.augmented(playerPoly);
    }

    /**
     * Maps a facing yaw (degrees, 0 = south) to the conventional direction name
     * used by {@code Machine.facing} expressions.
     */
    private static String yawToFacing(float yaw) {
        float n = ((yaw % 360f) + 360f) % 360f;
        if (n < 45f || n >= 315f) return "south";
        if (n < 135f)              return "west";
        if (n < 225f)              return "north";
        return "east";
    }

    // -------------------------------------------------------------------------
    // NMS particle type resolver
    // -------------------------------------------------------------------------

    /**
     * Resolves a particle name string (e.g. {@code "flame"} or {@code "minecraft:flame"})
     * to an NMS {@link net.minecraft.core.particles.ParticleOptions} using the built-in
     * particle type registry. Returns {@code null} when the name is unrecognised or the
     * type is not a {@link net.minecraft.core.particles.SimpleParticleType}.
     */
    private static net.minecraft.core.particles.ParticleOptions resolveParticle(String name) {
        try {
            String id = name.toLowerCase(java.util.Locale.ROOT);
            if (!id.contains(":")) id = "minecraft:" + id;
            net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.tryParse(id);
            if (rl == null) return null;
            net.minecraft.core.particles.ParticleType<?> type =
                    net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.get(rl)
                        .map(net.minecraft.core.Holder.Reference::value).orElse(null);
            if (type instanceof net.minecraft.core.particles.SimpleParticleType sp) return sp;
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Inner helper: manages one packet-only ITEM_DISPLAY for a FluidTankSpec
    // -------------------------------------------------------------------------

    /**
     * Manages a single packet-only {@code ITEM_DISPLAY} entity used to show a fluid
     * level above a machine block. Tracks which players currently see the entity so it
     * can send incremental spawn/update/despawn packets without broadcasting to everyone.
     */
    static final class FluidLevelDisplay {

        private static final double VIEW_DISTANCE = 48.0;
        private static final double VIEW_DISTANCE_SQ = VIEW_DISTANCE * VIEW_DISTANCE;

        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<>();

        private Key lastFluidKey;
        private float lastScaleY = -1f;

        FluidLevelDisplay() {
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz,
                    Key fluidKey, float scaleY) {
            if (serverLevel == null) return;

            boolean itemChanged = !Objects.equals(fluidKey, lastFluidKey);
            boolean scaleChanged = Float.compare(scaleY, lastScaleY) != 0;
            boolean metaDirty = itemChanged || scaleChanged;

            if (metaDirty) {
                lastFluidKey = fluidKey;
                lastScaleY = scaleY;
            }

            List<Object> metaValues = metaDirty ? buildMetadata(fluidKey, scaleY) : null;

            Set<UUID> stillVisible = new HashSet<>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dx = sp.getX() - wx;
                double dy = sp.getY() - wy;
                double dz = sp.getZ() - wz;
                if (dx * dx + dy * dy + dz * dz > VIEW_DISTANCE_SQ) {
                    if (shownTo.contains(sp.getUUID())) {
                        sendPacket(sp, despawnPacket);
                    }
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!shownTo.contains(sp.getUUID())) {
                    spawn(sp, wx, wy, wz);
                    if (metaValues != null) {
                        sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                                entityId, metaValues));
                    }
                } else if (metaDirty) {
                    sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                            entityId, metaValues));
                }
            }
            shownTo.retainAll(stillVisible);
            shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (shownTo.isEmpty()) return;
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (shownTo.contains(sp.getUUID())) {
                        sendPacket(sp, despawnPacket);
                    }
                }
            } else {
                for (org.bukkit.entity.Player bp : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (shownTo.contains(bp.getUniqueId())) {
                        try {
                            ServerPlayer sp = ((CraftPlayer) bp).getHandle();
                            sp.connection.send((Packet<?>) despawnPacket);
                        } catch (Throwable ignored) {}
                    }
                }
            }
            shownTo.clear();
            lastFluidKey = null;
            lastScaleY = -1f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            Object spawnPkt = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, entityUuid, wx, wy, wz, 0f, 0f,
                    EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            sendPacket(sp, spawnPkt);
        }

        private List<Object> buildMetadata(Key fluidKey, float scaleY) {
            List<Object> values = new ArrayList<>();
            try {
                var def = CraftEngineItems.byId(fluidKey);
                if (def != null) {
                    org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                    if (bukkit != null) {
                        Object nmsItem = CraftItemStack.asNMSCopy(bukkit);
                        DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
                    }
                }
            } catch (Throwable ignored) {}
            DisplayData.Scale.addEntityData(new Vector3f(1f, scaleY, 1f), values);
            return values;
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet<?>) packet);
            } catch (Throwable ignored) {}
        }
    }

    // -------------------------------------------------------------------------
    // Inner helper: manages one packet-only TEXT_DISPLAY for a TextDisplaySpec
    // -------------------------------------------------------------------------

    /**
     * Manages a single packet-only {@code TEXT_DISPLAY} entity used to show dynamic text
     * near a machine block. Tracks which players currently see the entity so it can
     * send incremental spawn/update/despawn packets without broadcasting to everyone.
     */
    static final class TextLevelDisplay {

        private static final double VIEW_DISTANCE    = 48.0;
        private static final double VIEW_DISTANCE_SQ = VIEW_DISTANCE * VIEW_DISTANCE;

        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<>();

        private String lastText;
        private float  lastScale = -1f;

        TextLevelDisplay() {
            this.entityId     = Entity.nextEntityId();
            this.entityUuid   = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz,
                    String text, float scale,
                    String billboard, int lineWidth, String backgroundExpr,
                    boolean shadow, boolean seeThrough, String alignment, int opacity) {
            if (serverLevel == null) return;

            boolean textChanged  = !Objects.equals(text, lastText);
            boolean scaleChanged = Float.compare(scale, lastScale) != 0;
            boolean metaDirty    = textChanged || scaleChanged;

            if (metaDirty) {
                lastText  = text;
                lastScale = scale;
            }

            List<Object> metaValues = metaDirty
                    ? buildMetadata(text, scale, billboard, lineWidth, backgroundExpr,
                                    shadow, seeThrough, alignment, opacity)
                    : null;

            Set<UUID> stillVisible = new HashSet<>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dx = sp.getX() - wx, dy = sp.getY() - wy, dz = sp.getZ() - wz;
                if (dx * dx + dy * dy + dz * dz > VIEW_DISTANCE_SQ) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!shownTo.contains(sp.getUUID())) {
                    spawn(sp, wx, wy, wz);
                    if (metaValues != null)
                        sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                                entityId, metaValues));
                } else if (metaDirty) {
                    sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                            entityId, metaValues));
                }
            }
            shownTo.retainAll(stillVisible);
            shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (shownTo.isEmpty()) return;
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                }
            } else {
                for (org.bukkit.entity.Player bp : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (shownTo.contains(bp.getUniqueId())) {
                        try {
                            ServerPlayer sp = ((CraftPlayer) bp).getHandle();
                            sp.connection.send((Packet<?>) despawnPacket);
                        } catch (Throwable ignored) {}
                    }
                }
            }
            shownTo.clear();
            lastText  = null;
            lastScale = -1f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, entityUuid, wx, wy, wz, 0f, 0f,
                    EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0));
        }

        private List<Object> buildMetadata(String text, float scale,
                                            String billboard, int lineWidth, String backgroundExpr,
                                            boolean shadow, boolean seeThrough,
                                            String alignment, int opacity) {
            List<Object> values = new ArrayList<>();
            try {
                DisplayData.TextDisplayData.Text.addEntityData(
                        net.minecraft.network.chat.Component.literal(text != null ? text : ""), values);
            } catch (Throwable ignored) {}
            DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
            byte bbByte = billboardByte(billboard);
            try {
                DisplayData.BillboardConstraints.addEntityData(bbByte, values);
            } catch (Throwable ignored) {}
            int bgColor = parseColor(backgroundExpr);
            try {
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(bgColor, values);
            } catch (Throwable ignored) {}
            try {
                byte styleFlags = 0;
                if (shadow)     styleFlags |= 0x01;
                if (seeThrough) styleFlags |= 0x02;
                var styleFlagsField = net.minecraft.world.entity.Display.TextDisplay.class
                        .getDeclaredField("DATA_STYLE_FLAGS");
                styleFlagsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) styleFlagsField.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, styleFlags));
            } catch (Throwable ignored) {}
            try {
                var opacityField = net.minecraft.world.entity.Display.TextDisplay.class
                        .getDeclaredField("DATA_TEXT_OPACITY");
                opacityField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) opacityField.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, (byte) opacity));
            } catch (Throwable ignored) {}
            try {
                var lwField = net.minecraft.world.entity.Display.TextDisplay.class
                        .getDeclaredField("DATA_LINE_WIDTH");
                lwField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) lwField.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, lineWidth));
            } catch (Throwable ignored) {}
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            return values;
        }

        private static byte billboardByte(String mode) {
            if (mode == null) return 3;
            return switch (mode.toLowerCase(java.util.Locale.ROOT)) {
                case "none"       -> (byte) 0;
                case "vertical"   -> (byte) 1;
                case "horizontal" -> (byte) 2;
                default           -> (byte) 3;
            };
        }

        private static int parseColor(String expr) {
            if (expr == null || expr.equals("0") || expr.isBlank()) return 0;
            try {
                String s = expr.startsWith("#") ? expr.substring(1) : expr;
                return (int) Long.parseLong(s, 16);
            } catch (Throwable ignored) {
                return 0;
            }
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet<?>) packet);
            } catch (Throwable ignored) {}
        }
    }

    // -------------------------------------------------------------------------
    // Inner helper: manages one packet-only ARMOR_STAND for an ArmorStandSpec
    // -------------------------------------------------------------------------

    /**
     * Manages a single packet-only {@code ARMOR_STAND} entity for equipment display
     * near a machine block. Follows the same spawn/despawn/tracking pattern as
     * {@link FluidLevelDisplay} and {@link TextLevelDisplay}.
     */
    static final class ArmorStandDisplay {

        private static final double VIEW_DISTANCE    = 48.0;
        private static final double VIEW_DISTANCE_SQ = VIEW_DISTANCE * VIEW_DISTANCE;

        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<>();

        ArmorStandDisplay() {
            this.entityId     = Entity.nextEntityId();
            this.entityUuid   = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz,
                    boolean small, boolean invisible, boolean marker) {
            if (serverLevel == null) return;

            Set<UUID> stillVisible = new HashSet<>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dx = sp.getX() - wx, dy = sp.getY() - wy, dz = sp.getZ() - wz;
                if (dx * dx + dy * dy + dz * dz > VIEW_DISTANCE_SQ) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!shownTo.contains(sp.getUUID())) {
                    spawn(sp, wx, wy, wz);
                    sendMetadata(sp, small, invisible, marker);
                }
            }
            shownTo.retainAll(stillVisible);
            shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (shownTo.isEmpty()) return;
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                }
            } else {
                for (org.bukkit.entity.Player bp : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (shownTo.contains(bp.getUniqueId())) {
                        try {
                            ServerPlayer sp = ((org.bukkit.craftbukkit.entity.CraftPlayer) bp).getHandle();
                            sp.connection.send((Packet<?>) despawnPacket);
                        } catch (Throwable ignored) {}
                    }
                }
            }
            shownTo.clear();
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, entityUuid, wx, wy, wz, 0f, 0f,
                    EntityType.ARMOR_STAND, 0, Vec3.ZERO, 0));
        }

        private void sendMetadata(ServerPlayer sp, boolean small, boolean invisible, boolean marker) {
            List<Object> values = new ArrayList<>();
            // Armor stand shared flags byte (index 15 in 1.21): 0x01=small, 0x04=arms, 0x08=no-baseplate, 0x10=marker
            byte flags = 0;
            if (small)  flags |= 0x01;
            if (marker) flags |= 0x10;
            flags |= 0x08; // no base plate always
            try {
                // Entity invisible flag (index 0 shared byte, bit 0x20)
                if (invisible) {
                    var sharedField = net.minecraft.world.entity.Entity.class.getDeclaredField("DATA_SHARED_FLAGS_ID");
                    sharedField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) sharedField.get(null);
                    values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, (byte) 0x20));
                }
                // Armor stand flags
                var asFlags = net.minecraft.world.entity.decoration.ArmorStand.class
                        .getDeclaredField("DATA_CLIENT_FLAGS");
                asFlags.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) asFlags.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, flags));
            } catch (Throwable ignored) {}
            if (!values.isEmpty()) {
                sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values));
            }
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try { sp.connection.send((Packet<?>) packet); }
            catch (Throwable ignored) {}
        }
    }

    // -------------------------------------------------------------------------
    // Inner helper: manages one packet-only BLOCK_DISPLAY for a BlockDisplaySpec
    // -------------------------------------------------------------------------

    /**
     * Manages a single packet-only {@code BLOCK_DISPLAY} entity for showing a fake block
     * near a machine. Same spawn/despawn/tracking pattern as the other display helpers.
     */
    static final class BlockDisplayHelper {

        private static final double VIEW_DISTANCE    = 48.0;
        private static final double VIEW_DISTANCE_SQ = VIEW_DISTANCE * VIEW_DISTANCE;

        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<>();

        private String lastBlockId;
        private float  lastScale = -1f;

        BlockDisplayHelper() {
            this.entityId     = Entity.nextEntityId();
            this.entityUuid   = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz,
                    String blockId, float scale) {
            if (serverLevel == null) return;

            boolean blockChanged = !Objects.equals(blockId, lastBlockId);
            boolean scaleChanged = Float.compare(scale, lastScale) != 0;
            boolean metaDirty    = blockChanged || scaleChanged;

            if (metaDirty) {
                lastBlockId = blockId;
                lastScale   = scale;
            }

            List<Object> metaValues = metaDirty ? buildMetadata(blockId, scale) : null;

            Set<UUID> stillVisible = new HashSet<>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dx = sp.getX() - wx, dy = sp.getY() - wy, dz = sp.getZ() - wz;
                if (dx * dx + dy * dy + dz * dz > VIEW_DISTANCE_SQ) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!shownTo.contains(sp.getUUID())) {
                    spawn(sp, wx, wy, wz);
                    if (metaValues != null)
                        sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                                entityId, metaValues));
                } else if (metaDirty) {
                    sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                            entityId, metaValues));
                }
            }
            shownTo.retainAll(stillVisible);
            shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (shownTo.isEmpty()) return;
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (shownTo.contains(sp.getUUID()))
                        sendPacket(sp, despawnPacket);
                }
            } else {
                for (org.bukkit.entity.Player bp : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (shownTo.contains(bp.getUniqueId())) {
                        try {
                            ServerPlayer sp = ((org.bukkit.craftbukkit.entity.CraftPlayer) bp).getHandle();
                            sp.connection.send((Packet<?>) despawnPacket);
                        } catch (Throwable ignored) {}
                    }
                }
            }
            shownTo.clear();
            lastBlockId = null;
            lastScale   = -1f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, entityUuid, wx, wy, wz, 0f, 0f,
                    EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0));
        }

        private List<Object> buildMetadata(String blockId, float scale) {
            List<Object> values = new ArrayList<>();
            try {
                // Resolve the block state from the id string
                String id = blockId.toLowerCase(java.util.Locale.ROOT);
                if (!id.contains(":")) id = "minecraft:" + id;
                net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.tryParse(id);
                if (rl != null) {
                    net.minecraft.world.level.block.Block block =
                            net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(rl)
                                .map(net.minecraft.core.Holder.Reference::value).orElse(null);
                    if (block != null) {
                        DisplayData.BlockDisplayData.BlockState.addEntityData(
                                block.defaultBlockState(), values);
                    }
                }
            } catch (Throwable ignored) {}
            DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
            return values;
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try { sp.connection.send((Packet<?>) packet); }
            catch (Throwable ignored) {}
        }
    }
}
