/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.adventure.PaperAdventure
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.minimessage.MiniMessage
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Display$TextDisplay
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$BlockDisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$TextDisplayData
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.machine.render.renderer.BetterModelRenderer;
import dev.arubik.craftengine.machine.render.renderer.MegRenderer;
import dev.arubik.craftengine.machine.render.ParticleUtils;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.LocationType;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import dev.arubik.craftengine.util.MNms;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class RendererManager {
    private final List<RendererSpec> specs;
    private final Map<String, VariableSpec> varSpecs;
    private final List<BetterModelRenderer> bmRenderers;
    private final List<MegRenderer> meRenderers;
    private final double[] currentSpeeds;
    private final net.minecraft.world.item.ItemStack[] currentItems;
    private final int[] particleCooldowns;
    private final int[] lingerCountdowns;
    private int[][] currentFootprint;
    private final FluidLevelDisplay[] fluidDisplays;
    private final TextLevelDisplay[] textDisplays;
    private final ArmorStandDisplay[] armorStandDisplays;
    private final BlockDisplayHelper[] blockDisplayHelpers;
    private final MachineInteraction[] interactionMarkers;
    private final EvalResult[] evalResults;
    /** Callback into the owning machine block entity's {@code runInteractScript} — wired via
     *  {@link #setInteractRunner}, {@code null} on any machine entity class that has no equivalent
     *  method (e.g. multi-block machines today), in which case {@code InteractionSpec} clicks just
     *  no-op. See {@link MachineInteractPacketListener}. */
    private InteractScriptRunner interactRunner;

    /** Runs an interact script with the given hook name — kept as a tiny callback here so {@code
     *  RendererManager} doesn't need to depend on any one machine-entity class (it's shared by
     *  several). {@code offsetX/Y/Z} is the clicked {@code InteractionSpec} marker's resolved
     *  location minus the machine's own {@code (x+0.5, y, z+0.5)} — the same relative offset every
     *  other per-spec renderer already computes (see {@code resolveSpecLocation}); a single-block
     *  machine entity's implementation can just ignore it, a multi-block one uses it to work out
     *  WHICH physical part was actually touched (see {@code DataMultiBlockMachineBlockEntity
     *  .runInteractScript}). */
    @FunctionalInterface
    public interface InteractScriptRunner {
        void run(String scriptRef, ServerPlayer player, String hookName, double offsetX, double offsetY, double offsetZ);
    }

    public void setInteractRunner(InteractScriptRunner r) {
        this.interactRunner = r;
    }

    public InteractScriptRunner interactRunner() {
        return this.interactRunner;
    }
    /** Per-spec tick counter backing the integer-interval form of {@code update_when} — see
     *  {@link #shouldUpdateThisTick}. */
    private final int[] updateTickCount;
    /** Per-spec last-observed block state backing {@code update_when: "on_change"} — see
     *  {@link #shouldUpdateThisTick}. Lazily populated (null until the first tick actually checks
     *  it), so a spec that never uses "on_change" never pays for this at all. */
    private final net.minecraft.world.level.block.state.BlockState[] lastSeenBlockState;
    private static final double LOC_NO_ROT = Double.NaN;

    /** Context variable names a renderer VALUE formula (rot_x/rot_y/rot_z/scale, a bracket-literal
     *  location's x/y/z, ...) is allowed to read and still be considered safe to SHARE its computed
     *  result across every machine instance whose block sits in the same {@code BlockState} — see
     *  {@link #computeSignature}, which determines PER FORMULA STRING (not guessed from its text)
     *  exactly which of these it actually touches. Anything read that ISN'T in this set (Inventory
     *  contents, Network/Contraption references, a get_typed lookup, a random draw, ...) is
     *  genuinely per-instance or non-deterministic and must never be shared — {@code
     *  computeSignature} rejects the WHOLE formula the moment it sees one such name, not just that
     *  one read. */
    private static final java.util.Set<String> SAFE_SHARED_VAR_NAMES = java.util.Set.of(
        "rpm", "overclock", "efficiency", "progress", "max_progress", "tier",
        "processing", "powered", "overclocked", "has_fuel",
        "redstone", "redstone_power", "facing_dx", "facing_dy", "facing_dz", "facing_angle", "tick"
    );

    /** Whether a formula string is safe to share, and — if so — EXACTLY which context variable
     *  names its result depends on (the cache key's value tuple). Determined ONCE per DISTINCT
     *  formula string — the set of distinct strings in use is fixed by static JSON config, not by
     *  machine count or tick count — by running one real evaluation with {@link
     *  ScriptContext#beginTracking} active and inspecting precisely which variable names it read.
     *  This is real dependency analysis, not a guessed text denylist: a formula that references an
     *  unrecognized bare name (a custom {@code $variable}, an unlisted builtin, ...) is correctly
     *  rejected without needing to know about it in advance, and one that only reads recognized
     *  names is correctly accepted even if a naive substring search would have been fooled (e.g. a
     *  variable literally named "my_progress_marker" would wrongly trip a "contains progress"
     *  denylist; real tracking sees the EXACT name "my_progress_marker", not "progress"). */
    private record FormulaSignature(boolean cacheable, java.util.List<String> keyVars) {}
    private static final java.util.Map<String, FormulaSignature> FORMULA_SIGNATURES = new java.util.concurrent.ConcurrentHashMap<>();

    private static FormulaSignature computeSignature(String expr, MachineRenderContext evalCtx) {
        java.util.Set<String> tracked = ScriptContext.beginTracking();
        try {
            ScriptFormula.compile(expr).evaluate(evalCtx.toScriptContext());
        } catch (Throwable ignored) {
        } finally {
            ScriptContext.endTracking();
        }
        if (!SAFE_SHARED_VAR_NAMES.containsAll(tracked)) return new FormulaSignature(false, java.util.List.of());
        return new FormulaSignature(true, java.util.List.copyOf(tracked));
    }

    /** (formula string, exact BlockState, values of every {@link FormulaSignature#keyVars}) — two
     *  machine instances agreeing on ALL of that (e.g. 10 energy_windmills all facing north — NMS
     *  interns identical property combinations, so their BlockState objects are literally the same
     *  reference and neither formula reads anything else — or 10 shafts all spinning at the same
     *  rpm on the same kinetic network, where rpm is simply one more tracked/keyed variable) share
     *  one cached evaluation instead of each re-walking the same formula every tick. */
    /**
     * The three things every sharedEval in one machine's tick agrees on, computed once at the top of
     * {@link #tick} instead of per formula.
     *
     * <p>They were recomputed inside sharedEval, which meant a WORLD BLOCK-STATE LOOKUP for every
     * formula — and one item_display renderer evaluates six (item, rot_x, rot_y, rot_z, scale,
     * location). Across a few hundred ticking machines that is thousands of lookups a tick to build
     * a cache key, for a cache whose whole purpose is to avoid work.
     *
     * <p>Safe to hoist because every sharedEval call site in a tick passes that tick's own machine
     * position, so the state is the same block by construction; the tick number obviously does not
     * change within a tick; and the context is already cached per MachineRenderContext.
     */
    private ScriptContext scopeCtx;
    private net.minecraft.world.level.block.state.BlockState scopeState;
    private int scopeTick = -1;

    /**
     * Whether this tick is one on which a rotation packet will actually be sent.
     *
     * <p>A continuously spinning display already has its metadata packet throttled to every
     * ROTATION_PACKET_INTERVAL ticks, with the client's interpolation window widened to match, so it
     * smooths across the gap. The EVALUATION was not throttled with it: {@code rot_x/rot_y/rot_z}
     * were recomputed every tick and three out of four results were thrown away.
     *
     * <p>Set by the caller from the SAME counter the send decision uses. That alignment is the whole
     * safety argument — the value is refreshed on exactly the ticks it is consumed, so it can never
     * be stale when sent. An earlier attempt skipped on a schedule of its own and silently broke
     * interpolation by sometimes skipping the send tick itself.
     *
     * <p>Defaults true so any caller that does not set it keeps evaluating every tick.
     */
    private boolean rotationTick = true;

    /** Last evaluated rotation per spec, reused on the ticks in between. NaN means "never yet". */
    private float[] lastRotX, lastRotY, lastRotZ;

    public void setRotationTick(boolean sendingThisTick) { this.rotationTick = sendingThisTick; }

    /**
     * The cache key, built without allocating for the values.
     *
     * <p>It used to carry a {@code List<Object>} of the key variables, which meant an ArrayList, a
     * boxed Double per variable, and a list hash on EVERY lookup — including the 92.6% that hit.
     * At ~2150 lookups per tick the key was costing more than the evaluation it saved: renderers
     * measured 1.5us per sharedEval call, where a hit should be a hash and a compare.
     *
     * <p>Nearly every cacheable formula keys on none or one variable (a shaft's rotation on rpm), so
     * two are held inline as raw {@code doubleToLongBits} - exact, not a hash, so there is no
     * collision to reason about. Anything wider falls back to the list, which is then rare enough
     * not to matter.
     */
    private record SharedFormulaKey(String expr, net.minecraft.world.level.block.state.BlockState state,
                                     long k0, long k1, java.util.List<Object> rest) {}
    private record SharedFormulaEntry(int tick, ScriptValue value) {}

    /**
     * Whether the cross-instance cache is actually collapsing work, which is the only thing that
     * makes it worth its key-building cost. Server-thread only, so plain longs.
     *
     * <p>Worth counting rather than assuming: seventy-nine shafts on one kinetic network at the
     * same rpm SHOULD evaluate each formula once between them. If they are missing instead, every
     * one of them pays the full evaluation AND the key, which is worse than having no cache.
     */
    private static long SHARED_HITS, SHARED_MISSES, SHARED_UNCACHEABLE;

    public static String sharedCacheStats() {
        long total = SHARED_HITS + SHARED_MISSES;
        if (total == 0 && SHARED_UNCACHEABLE == 0) return "shared formula cache: no evaluations";
        return String.format(java.util.Locale.ROOT,
                "shared formula cache: %d hits, %d misses (%.1f%% hit), %d refused as uncacheable",
                SHARED_HITS, SHARED_MISSES, total == 0 ? 0 : 100.0 * SHARED_HITS / total,
                SHARED_UNCACHEABLE);
    }

    public static void resetSharedCacheStats() {
        SHARED_HITS = SHARED_MISSES = SHARED_UNCACHEABLE = 0;
    }
    private static final int SHARED_FORMULA_CACHE_MAX = 4096;
    private static final java.util.LinkedHashMap<SharedFormulaKey, SharedFormulaEntry> SHARED_FORMULA_CACHE =
        new java.util.LinkedHashMap<>(512, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<SharedFormulaKey, SharedFormulaEntry> eldest) {
                return size() > SHARED_FORMULA_CACHE_MAX;
            }
        };

    /** Core of the cross-instance shared-formula cache. Evaluates {@code expr} against {@code
     *  evalCtx} like normal, but if {@link #computeSignature} finds it depends only on known-safe,
     *  boundable variables, the RESULT is shared with every other machine instance whose block is
     *  in the exact same BlockState AND has the exact same values for those specific variables.
     *  Works for both scalar (rot_x/y/z, scale) and array-returning (a bracket-literal {@code
     *  location}) formulas alike — the cache stores the raw {@link ScriptValue}, not a pre-typed
     *  double. */
    private ScriptValue sharedEval(ServerLevel serverLevel, double x, double y, double z, String expr, MachineRenderContext evalCtx) {
        if (expr == null) return ScriptValue.NULL;
        ScriptContext sctx = scopeCtx != null ? scopeCtx : evalCtx.toScriptContext();
        if (serverLevel == null) return ScriptFormula.compile(expr).evaluate(sctx);
        FormulaSignature sig = FORMULA_SIGNATURES.computeIfAbsent(expr, e -> computeSignature(e, evalCtx));
        if (!sig.cacheable()) { SHARED_UNCACHEABLE++; return ScriptFormula.compile(expr).evaluate(sctx); }
        int tick = scopeTick;
        net.minecraft.world.level.block.state.BlockState state = scopeState;
        if (state == null) {
            // Reached only from a caller outside tick(), which has no scope to inherit.
            try { tick = serverLevel.getServer().getTickCount(); }
            catch (Throwable ignored) { return ScriptFormula.compile(expr).evaluate(sctx); }
            state = serverLevel.getBlockState(net.minecraft.core.BlockPos.containing(x, y, z));
        }
        java.util.List<String> keyVars = sig.keyVars();
        int n = keyVars.size();
        long k0 = 0L, k1 = 0L;
        java.util.List<Object> rest = null;
        if (n > 0) k0 = Double.doubleToLongBits(sctx.getVar(keyVars.get(0)).asNum());
        if (n > 1) k1 = Double.doubleToLongBits(sctx.getVar(keyVars.get(1)).asNum());
        if (n > 2) {
            rest = new java.util.ArrayList<>(n - 2);
            for (int i = 2; i < n; i++) rest.add(sctx.getVar(keyVars.get(i)).asNum());
        }
        SharedFormulaKey key = new SharedFormulaKey(expr, state, k0, k1, rest);
        SharedFormulaEntry cached = SHARED_FORMULA_CACHE.get(key);
        if (cached != null && cached.tick() == tick) { SHARED_HITS++; return cached.value(); }
        SHARED_MISSES++;
        ScriptValue val = ScriptFormula.compile(expr).evaluate(sctx);
        SHARED_FORMULA_CACHE.put(key, new SharedFormulaEntry(tick, val));
        return val;
    }

    private double sharedEvalNum(ServerLevel serverLevel, double x, double y, double z, String expr, MachineRenderContext evalCtx) {
        return expr == null ? 0 : this.sharedEval(serverLevel, x, y, z, expr, evalCtx).asNum();
    }

    /** Item-display "item" expressions are frequently a fully CONSTANT call — {@code
     *  CraftEngineItem("cml:energy_windmill_rotor_hub_render")} with no real context dependency at
     *  all — so this is exactly as shareable as rot_x/y/z, often even more so (the function-name
     *  self-lookup callBuiltin does to check for a user override no longer counts as a dependency —
     *  see ScriptContext#getVar's own note — so a pure item-id constant like this ends up with an
     *  EMPTY keyVars list, meaning every instance in the same BlockState shares one lookup). */
    private net.minecraft.world.item.ItemStack sharedEvalItem(ServerLevel serverLevel, double x, double y, double z, String expr, MachineRenderContext evalCtx) {
        if (expr == null) return null;
        ScriptValue sv = this.sharedEval(serverLevel, x, y, z, expr, evalCtx);
        return sv instanceof ScriptValue.Item item ? item.stack() : null;
    }

    /** Whether spec {@code i} should do ANY work this tick — {@code spec.updateWhen()} is already a
     *  parsed {@link UpdateWhen} (classified once at machine-DEFINITION load time, in {@link
     *  dev.arubik.craftengine.machine.MachineDefinitionLoader} — never re-parsed here), so this is
     *  just a switch, never a string re-parse:
     *  <ul>
     *    <li>{@code Always}/{@code Never} — the obvious extremes ("never" still runs its very first
     *        tick, or the display would sit forever in whatever default/empty state it started in).</li>
     *    <li>{@code OnBlockChange} — a cheap {@code BlockState} equality check against what was last
     *        seen; no script engine involved.</li>
     *    <li>{@code FastProperty} — one of the 4 known {@link MachineRenderContext} booleans read
     *        directly (no block lookup at all), OR — for any other bare name — a real block-state
     *        property read via {@link dev.arubik.craftengine.script.types.world.BlockType#readProperty}
     *        (still no script engine, just one block-state lookup).</li>
     *    <li>{@code Interval} — plain int arithmetic, re-evaluate once every N ticks.</li>
     *    <li>{@code ScriptGate} — the general fallback, routed through the normal script engine
     *        ({@code MachineRenderContext#evalBool}, which already understands both a plain boolexpr
     *        and a {@code "file.pf:func"} script-call reference).</li>
     *  </ul> */
    private boolean shouldUpdateThisTick(int i, RendererSpec spec, MachineRenderContext ctx,
                                          ServerLevel serverLevel, double x, double y, double z) {
        return switch (spec.updateWhen()) {
            case UpdateWhen.Always ignored -> true;
            case UpdateWhen.Never ignored -> this.updateTickCount[i]++ == 0;
            case UpdateWhen.OnBlockChange ignored -> {
                if (serverLevel == null) yield true;
                net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(x, y, z);
                net.minecraft.world.level.block.state.BlockState current = serverLevel.getBlockState(pos);
                net.minecraft.world.level.block.state.BlockState last = this.lastSeenBlockState[i];
                if (last == null || !current.equals(last)) {
                    this.lastSeenBlockState[i] = current;
                    yield true;
                }
                yield false;
            }
            case UpdateWhen.FastProperty fp -> switch (fp.name()) {
                case "processing" -> ctx.processing();
                case "powered" -> ctx.powered();
                case "overclocked" -> ctx.overclocked();
                case "has_fuel" -> ctx.hasFuel();
                default -> {
                    if (serverLevel == null) yield true;
                    net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(x, y, z);
                    net.minecraft.world.level.block.state.BlockState state = serverLevel.getBlockState(pos);
                    String val = dev.arubik.craftengine.script.types.world.BlockType.readProperty(state, fp.name());
                    yield val != null && !val.equalsIgnoreCase("false");
                }
            };
            case UpdateWhen.Interval iv -> {
                int cur = ++this.updateTickCount[i];
                if (cur >= iv.ticks()) { this.updateTickCount[i] = 0; yield true; }
                yield false;
            }
            case UpdateWhen.ScriptGate sg -> ctx.evalBool(sg.expr(), null);
        };
    }

    public RendererManager(List<RendererSpec> specs, Map<String, VariableSpec> varSpecs) {
        this.specs = List.copyOf(specs);
        this.varSpecs = varSpecs;
        int n = specs.size();
        this.bmRenderers = new ArrayList<BetterModelRenderer>(n);
        this.meRenderers = new ArrayList<MegRenderer>(n);
        this.currentSpeeds = new double[n];
        Arrays.fill(this.currentSpeeds, 1.0);
        this.currentItems = new net.minecraft.world.item.ItemStack[n];
        this.particleCooldowns = new int[n];
        this.lingerCountdowns = new int[n];
        this.fluidDisplays = new FluidLevelDisplay[n];
        this.textDisplays = new TextLevelDisplay[n];
        this.armorStandDisplays = new ArmorStandDisplay[n];
        this.blockDisplayHelpers = new BlockDisplayHelper[n];
        this.interactionMarkers = new MachineInteraction[n];
        this.evalResults = new EvalResult[n];
        for (int k = 0; k < n; ++k) {
            this.evalResults[k] = new EvalResult();
        }
        this.updateTickCount = new int[n];
        this.lastSeenBlockState = new net.minecraft.world.level.block.state.BlockState[n];
        for (int i = 0; i < n; ++i) {
            int idx = i;
            RendererSpec spec = specs.get(i);
            RendererSpec actualSpec;
            if (spec instanceof RendererSpec.PositionedSpec ps) {
                actualSpec = ps.inner();
            } else {
                actualSpec = spec;
            }
            if (actualSpec instanceof RendererSpec.BetterModelSpec) {
                RendererSpec.BetterModelSpec bm = (RendererSpec.BetterModelSpec)actualSpec;
                this.bmRenderers.add(new BetterModelRenderer(bm.modelId(), () -> this.currentSpeeds[idx]));
                this.meRenderers.add(null);
                continue;
            }
            if (actualSpec instanceof RendererSpec.ModelEngineSpec) {
                RendererSpec.ModelEngineSpec me = (RendererSpec.ModelEngineSpec)actualSpec;
                this.bmRenderers.add(null);
                this.meRenderers.add(new MegRenderer(me.modelId(), () -> this.currentSpeeds[idx]));
                continue;
            }
            this.bmRenderers.add(null);
            this.meRenderers.add(null);
            if (actualSpec instanceof RendererSpec.FluidTankSpec) {
                this.fluidDisplays[i] = new FluidLevelDisplay();
                continue;
            }
            if (actualSpec instanceof RendererSpec.TextDisplaySpec) {
                this.textDisplays[i] = new TextLevelDisplay();
                continue;
            }
            if (actualSpec instanceof RendererSpec.ArmorStandSpec) {
                this.armorStandDisplays[i] = new ArmorStandDisplay();
                continue;
            }
            if (actualSpec instanceof RendererSpec.InteractionSpec is) {
                this.interactionMarkers[i] = new MachineInteraction(is.onInteractRef(), this);
                this.interactionMarkers[i].setSize(is.width(), is.height());
                continue;
            }
            if (!(actualSpec instanceof RendererSpec.BlockDisplaySpec)) continue;
            this.blockDisplayHelpers[i] = new BlockDisplayHelper();
        }
    }

    public void tick(MachineRenderContext ctx, ServerLevel serverLevel, double x, double y, double z, float yaw) {
        this.tick(ctx, serverLevel, x, y, z, yaw, (String) null, (int[][]) null);
    }

    public void tick(MachineRenderContext ctx, ServerLevel serverLevel, double x, double y, double z, float yaw, int[] ... footprint) {
        this.tick(ctx, serverLevel, x, y, z, yaw, (String) null, footprint);
    }

    /**
     * Same as the other overloads, but {@code facing} (a real direction NAME — "north".."down") is
     * used directly for the bare "facing" script var instead of being re-derived from {@code yaw}
     * via {@link #yawToFacing}. That derivation can ONLY ever produce a horizontal result — a plain
     * yaw angle has no way to express "up"/"down" — so any renderer "when" condition checking
     * {@code facing == "up"}/{@code "down"} against the OLD overloads always saw a horizontal
     * direction instead, no matter which way the machine actually faced. Pass {@code null} for
     * {@code facing} to keep the old yaw-derived (horizontal-only) behavior.
     */
    public void tick(MachineRenderContext ctx, ServerLevel serverLevel, double x, double y, double z, float yaw, String facing, int[] ... footprint) {
        CraftWorld bukkitWorld;
        this.currentFootprint = footprint != null && footprint.length > 0 ? footprint : null;
        CraftWorld craftWorld = bukkitWorld = serverLevel != null ? serverLevel.getWorld() : null;
        if (serverLevel != null) {
            String facingValue = facing != null ? facing : RendererManager.yawToFacing(yaw);
            ScriptContext.Builder augB = ScriptContext.builder().facing(facingValue, yaw);
            ctx = ctx.augmented(augB.buildOver(ctx.toScriptContext()));
        }
        if (this.varSpecs != null && !this.varSpecs.isEmpty()) {
            ScriptContext.Builder varsB = ScriptContext.builder();
            for (var entry : this.varSpecs.entrySet()) {
                if (entry.getValue() instanceof dev.arubik.craftengine.machine.render.variable.VariableSpec.Formula f) {
                    try { varsB.val(entry.getKey(), ScriptFormula.compile(f.expr()).evaluate(ctx.toScriptContext())); }
                    catch (Throwable ignored) {}
                }
            }
            ctx = ctx.augmented(varsB.buildOver(ctx.toScriptContext()));
        }
        // Establish this tick's shared evaluation scope ONCE. Every sharedEval below inherits it
        // rather than re-deriving the block state and tick number per formula - see scopeCtx.
        this.scopeCtx = ctx.toScriptContext();
        if (serverLevel != null) {
            try {
                this.scopeTick = serverLevel.getServer().getTickCount();
                this.scopeState = serverLevel.getBlockState(
                        net.minecraft.core.BlockPos.containing(x, y, z));
            } catch (Throwable ignored) {
                this.scopeState = null;
            }
        }
        try {
        for (int i = 0; i < this.specs.size(); ++i) {
            String blockId;
            boolean active;
            RendererSpec rendererSpec;
            RendererSpec spec = this.specs.get(i);
            if (spec instanceof RendererSpec.PositionedSpec) {
                RendererSpec.PositionedSpec ps = (RendererSpec.PositionedSpec)spec;
                rendererSpec = ps.inner();
            } else {
                rendererSpec = spec;
            }
            RendererSpec actualSpec = rendererSpec;
            // update_when throttle gate — skip this spec's ENTIRE per-tick work (script-ref eval,
            // whenExpr, value formulas, render update) when it says not to refresh this tick. A
            // throttled tick leaves every already-shown display exactly as it was on the last tick
            // that DID update — see shouldUpdateThisTick's own doc for the 3 supported forms.
            if (!this.shouldUpdateThisTick(i, spec, ctx, serverLevel, x, y, z)) {
                continue;
            }
            MachineRenderContext evalCtx = ctx;
            String scriptRef = spec.scriptRef();
            ScriptProgram script2 = scriptRef != null ? ScriptRegistry.get(scriptRef) : null;
            if (script2 != null) {
                ScriptContext augmented = script2.evaluate(ctx.toScriptContext());
                evalCtx = ctx.augmented(augmented);
            }
            this.evalResults[i].active = active = evaluateWhen(spec.whenExpr(), evalCtx, serverLevel, x, y, z);
            this.evalResults[i].emittedThisTick = false;
            if (actualSpec instanceof RendererSpec.BetterModelSpec) {
                RendererSpec.BetterModelSpec bm = (RendererSpec.BetterModelSpec)actualSpec;
                BetterModelRenderer r = this.bmRenderers.get(i);
                if (r == null) continue;
                boolean effectiveActive = active;
                if (active) {
                    this.lingerCountdowns[i] = bm.lingerTicks();
                } else if (this.lingerCountdowns[i] > 0) {
                    int n = i;
                    this.lingerCountdowns[n] = this.lingerCountdowns[n] - 1;
                    effectiveActive = true;
                }
                if (effectiveActive) {
                    this.currentSpeeds[i] = evalCtx.evalNum(bm.speedExpr() != null ? bm.speedExpr() : "1.0", null);
                    r.setLocation((World)bukkitWorld, x, y, z, yaw);
                    r.show();
                    if (bm.animation() != null) {
                        r.playLoop(bm.animation());
                        continue;
                    }
                    r.stopAnim();
                    continue;
                }
                r.stopAnim();
                r.close();
                continue;
            }
            if (actualSpec instanceof RendererSpec.ModelEngineSpec) {
                RendererSpec.ModelEngineSpec me = (RendererSpec.ModelEngineSpec)actualSpec;
                MegRenderer r = this.meRenderers.get(i);
                if (r == null) continue;
                if (active) {
                    this.currentSpeeds[i] = evalCtx.evalNum(me.speedExpr() != null ? me.speedExpr() : "1.0", null);
                    r.setLocation((World)bukkitWorld, x, y, z, yaw);
                    r.show();
                    if (me.animation() != null) {
                        r.playLoop(me.animation());
                        continue;
                    }
                    r.stopAnim();
                    continue;
                }
                r.close();
                continue;
            }
            if (actualSpec instanceof RendererSpec.ItemDisplaySpec) {
                boolean idPerPlayer;
                RendererSpec.ItemDisplaySpec id = (RendererSpec.ItemDisplaySpec)actualSpec;
                WhenCondition idWhen = spec.whenExpr();
                String idWhenRaw = idWhen.raw();
                boolean bl = idPerPlayer = !id.global() && !idWhenRaw.equals("always") && (idWhenRaw.contains("player_facing") || idWhenRaw.contains("player_in_range"));
                if (idPerPlayer && serverLevel != null) {
                    HashSet<UUID> qualifying = new HashSet<UUID>();
                    for (ServerPlayer sp : serverLevel.players()) {
                        MachineRenderContext playerCtx = this.buildPlayerContext(evalCtx, sp, x, y, z, yaw);
                        if (!idWhen.evaluate(playerCtx)) continue;
                        qualifying.add(sp.getUUID());
                    }
                    this.evalResults[i].qualifyingPlayers = qualifying;
                    this.evalResults[i].active = active = !qualifying.isEmpty();
                } else {
                    this.evalResults[i].qualifyingPlayers = null;
                }
                if (active) {
                    boolean isFormula;
                    String expr = id.itemExpr();
                    boolean bl2 = isFormula = expr.startsWith("$") || expr.contains("(") || expr.contains(".");
                    if (isFormula) {
                        this.currentItems[i] = this.sharedEvalItem(serverLevel, x, y, z, expr, evalCtx);
                    } else if (this.currentItems[i] == null) {
                        try {
                            Object full = expr.contains(":") ? expr : "minecraft:" + expr;
                            Item nmsItem = (Item)BuiltInRegistries.ITEM.getValue(Identifier.parse((String)full));
                            this.currentItems[i] = nmsItem != null && nmsItem != Items.AIR ? new net.minecraft.world.item.ItemStack((ItemLike)nmsItem) : null;
                        }
                        catch (Throwable full) {
                            // empty catch block
                        }
                    }
                    try {
                        double[] wp2 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                        double relX = wp2[0] - (x + 0.5);
                        double relY = wp2[1] - y;
                        double relZ = wp2[2] - (z + 0.5);
                        // sharedEvalNum, not a plain evalCtx.evalNum — rot_x/rot_y/rot_z/scale are
                        // frequently PURE functions of the block itself (e.g. energy_windmill's
                        // rot_y, computed only from Machine.block.property("facing")), in which
                        // case every machine instance sharing that exact BlockState (10 windmills
                        // all facing north, say) reuses one cached evaluation instead of each
                        // re-walking the same formula every tick. Falls through to the normal
                        // per-instance eval for anything that reads real instance state (rpm,
                        // progress, ...) — see DYNAMIC_MARKERS.
                        float rotX = !Double.isNaN(wp2[3]) ? (float)wp2[3]
                                : rotationValue(i, 0, id.rotX(), serverLevel, x, y, z, evalCtx);
                        float rotY = !Double.isNaN(wp2[4]) ? (float)wp2[4]
                                : rotationValue(i, 1, id.rotY(), serverLevel, x, y, z, evalCtx);
                        float rotZ = !Double.isNaN(wp2[5]) ? (float)wp2[5]
                                : rotationValue(i, 2, id.rotZ(), serverLevel, x, y, z, evalCtx);
                        this.evalResults[i].itemDisplay = new RendererSpec.EvaluatedItemDisplay(i, this.currentItems[i], relX, relY, relZ, (float) this.sharedEvalNum(serverLevel, x, y, z, id.scale() != null && !id.scale().isEmpty() ? id.scale() : "1", evalCtx), rotX, rotY, rotZ);
                    }
                    catch (Throwable wp2) {}
                    continue;
                }
                this.currentItems[i] = null;
                this.evalResults[i].itemDisplay = null;
                continue;
            }
            if (actualSpec instanceof RendererSpec.ParticleSpec) {
                RendererSpec.ParticleSpec ps = (RendererSpec.ParticleSpec)actualSpec;
                if (!active) {
                    this.particleCooldowns[i] = 0;
                    continue;
                }
                int n = i;
                this.particleCooldowns[n] = this.particleCooldowns[n] + 1;
                if (this.particleCooldowns[i] < ps.interval()) continue;
                this.particleCooldowns[i] = 0;
                int count = (int)Math.max(0.0, evalCtx.evalNum(ps.countExpr() != null ? ps.countExpr() : "1", null));
                if (count == 0) continue;
                double spreadX = evalCtx.evalNum(ps.spreadXExpr() != null ? ps.spreadXExpr() : "0", null);
                double spreadY = evalCtx.evalNum(ps.spreadYExpr() != null ? ps.spreadYExpr() : "0", null);
                double spreadZ = evalCtx.evalNum(ps.spreadZExpr() != null ? ps.spreadZExpr() : "0", null);
                double spd = evalCtx.evalNum(ps.speedExpr() != null ? ps.speedExpr() : "0.05", null);
                double dvx = evalCtx.evalNum(ps.dirXExpr() != null ? ps.dirXExpr() : "0", null);
                double dvy = evalCtx.evalNum(ps.dirYExpr() != null ? ps.dirYExpr() : "0", null);
                double dvz = evalCtx.evalNum(ps.dirZExpr() != null ? ps.dirZExpr() : "0", null);
                ParticleUtils.Shape emitShape = ParticleUtils.Shape.fromName(ps.shape(), ParticleUtils.Shape.POINT);
                ParticleUtils.Direction emitDir = ps.directionMode() != null && !ps.directionMode().isEmpty() ? ParticleUtils.Direction.fromName(ps.directionMode(), ParticleUtils.Direction.RANDOM) : (dvx != 0.0 || dvy != 0.0 || dvz != 0.0 ? ParticleUtils.Direction.CUSTOM : ParticleUtils.Direction.RANDOM);
                this.evalResults[i].emittedThisTick = true;
                this.evalResults[i].particleCount = count;
                this.evalResults[i].pSpreadX = spreadX;
                this.evalResults[i].pSpreadY = spreadY;
                this.evalResults[i].pSpreadZ = spreadZ;
                this.evalResults[i].pSpeed = spd;
                this.evalResults[i].pDvx = dvx;
                this.evalResults[i].pDvy = dvy;
                this.evalResults[i].pDvz = dvz;
                this.evalResults[i].pType = ps.particle();
                this.evalResults[i].pShape = emitShape;
                this.evalResults[i].pDir = emitDir;
                double[] ep0 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                this.evalResults[i].specRelX = ep0[0] - (x + 0.5);
                this.evalResults[i].specRelY = ep0[1] - y;
                this.evalResults[i].specRelZ = ep0[2] - (z + 0.5);
                try {
                    ParticleOptions particleType = RendererManager.resolveParticle(ps.particle());
                    if (particleType == null) continue;
                    List<double[]> emitPositions = this.resolvePositions(spec, evalCtx, x, y, z);
                    if (emitPositions.isEmpty()) {
                        emitPositions = List.of(this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z));
                    }
                    double RANGE_SQ = 2304.0;
                    WhenCondition whenExpr = spec.whenExpr();
                    for (double[] ep : emitPositions) {
                        double wx = ep[0];
                        double wy = ep[1];
                        double wz = ep[2];
                        ArrayList<ServerPlayer> qualifying = new ArrayList<>();
                        for (ServerPlayer sp : serverLevel.players()) {
                            MachineRenderContext playerCtx;
                            double ddz;
                            double ddy;
                            double ddx = sp.getX() - wx;
                            if (ddx * ddx + (ddy = sp.getY() - wy) * ddy + (ddz = sp.getZ() - wz) * ddz > 2304.0 || !whenExpr.evaluate(playerCtx = this.buildPlayerContext(evalCtx, (ServerPlayer)sp, x, y, z, yaw))) continue;
                            qualifying.add(sp);
                        }
                        if (qualifying.isEmpty()) continue;
                        if (emitShape == ParticleUtils.Shape.POINT && emitDir == ParticleUtils.Direction.RANDOM) {
                            ClientboundLevelParticlesPacket pkt = new ClientboundLevelParticlesPacket(particleType, false, false, wx, wy, wz, (float)spreadX, (float)spreadY, (float)spreadZ, (float)spd, count);
                            for (ServerPlayer sp2 : qualifying) {
                                try {
                                    sp2.connection.send((Packet)pkt);
                                }
                                catch (Throwable throwable) {}
                            }
                            continue;
                        }
                        ThreadLocalRandom rng = ThreadLocalRandom.current();
                        for (int k = 0; k < count; ++k) {
                            double[] samplePos = ParticleUtils.sampleShape(rng, emitShape, spreadX, spreadY, spreadZ);
                            double lx = samplePos[0];
                            double ly = samplePos[1];
                            double lz = samplePos[2];
                            double ppx = wx + lx;
                            double ppy = wy + ly;
                            double ppz = wz + lz;
                            double[] vel = ParticleUtils.computeDirection(rng, emitDir, lx, ly, lz, dvx, dvy, dvz, spd);
                            ClientboundLevelParticlesPacket pkt = emitDir == ParticleUtils.Direction.RANDOM ? new ClientboundLevelParticlesPacket(particleType, false, false, ppx, ppy, ppz, (float)spreadX, (float)spreadY, (float)spreadZ, (float)spd, 1) : new ClientboundLevelParticlesPacket(particleType, false, false, ppx, ppy, ppz, (float)vel[0], (float)vel[1], (float)vel[2], (float)spd, 0);
                            for (ServerPlayer serverPlayer : qualifying) {
                                try {
                                    serverPlayer.connection.send((Packet)pkt);
                                }
                                catch (Throwable throwable) {}
                            }
                        }
                    }
                    continue;
                }
                catch (Throwable throwable) {
                    continue;
                }
            }
            if (actualSpec instanceof RendererSpec.FluidTankSpec) {
                RendererSpec.FluidTankSpec ft = (RendererSpec.FluidTankSpec)actualSpec;
                FluidLevelDisplay display = this.fluidDisplays[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                String varKey = (ft.isGas() ? "gas_" : "fluid_") + (ft.tankName().isEmpty() ? "0" : ft.tankName());
                int ceLevel = (int)Math.max(0.0, Math.min(16.0, evalCtx.evalNum(varKey, null)));
                if (ceLevel <= 0) {
                    display.despawnAll(serverLevel);
                    this.evalResults[i].ceFluidLevel = 0;
                    continue;
                }
                String fluidTypeValue = ft.isGas() ? "steam" : "water";
                try {
                    ScriptValue tankVal = evalCtx.toScriptContext().getVar(varKey + "_type");
                    if (tankVal instanceof ScriptValue.Str s && !s.value().isEmpty()) {
                        fluidTypeValue = s.value();
                    }
                }
                catch (Throwable tankName) {
                    // empty catch block
                }
                Key fluidKey = Key.of((String)"cml", (String)("fluidlvl_" + fluidTypeValue + "_" + ceLevel));
                float scaleY = ft.maxHeight() * ((float)ceLevel / 16.0f);
                double[] wp3 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                this.evalResults[i].ceFluidLevel = ceLevel;
                this.evalResults[i].fluidTypeValue = fluidTypeValue;
                this.evalResults[i].specRelX = wp3[0] - (x + 0.5);
                this.evalResults[i].specRelY = wp3[1] - y;
                this.evalResults[i].specRelZ = wp3[2] - (z + 0.5);
                display.update(serverLevel, wp3[0], wp3[1], wp3[2], fluidKey, scaleY);
                continue;
            }
            if (actualSpec instanceof RendererSpec.TextDisplaySpec) {
                String text;
                boolean perPlayer;
                RendererSpec.TextDisplaySpec td = (RendererSpec.TextDisplaySpec)actualSpec;
                TextLevelDisplay display = this.textDisplays[i];
                if (display == null) continue;
                String tdWhen = td.whenExpr().raw();
                boolean bl = perPlayer = !td.global() && tdWhen != null && !tdWhen.equals("always") && (tdWhen.contains("player_facing") || tdWhen.contains("player_in_range") || tdWhen.contains("is_player_looking_at"));
                if (perPlayer) {
                    String text2;
                    HashSet<UUID> qualifying = new HashSet<UUID>();
                    for (ServerPlayer sp : serverLevel.players()) {
                        MachineRenderContext playerCtx = this.buildPlayerContext(evalCtx, sp, x, y, z, yaw);
                        if (!td.whenExpr().evaluate(playerCtx)) continue;
                        qualifying.add(sp.getUUID());
                    }
                    this.evalResults[i].qualifyingPlayers = qualifying;
                    if (qualifying.isEmpty()) {
                        display.despawnAll(serverLevel);
                        continue;
                    }
                    try {
                        text2 = ScriptFormula.compile(td.textExpr() != null && !td.textExpr().isEmpty() ? td.textExpr() : "\"\"").evaluateStr(evalCtx.toScriptContext());
                    }
                    catch (Throwable ignored) {
                        text2 = td.textExpr() != null ? td.textExpr() : "";
                    }
                    double[] wp4 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                    float scaleVal = (float)evalCtx.evalNum(td.scale() != null && !td.scale().isEmpty() ? td.scale() : "0.1", null);
                    this.evalResults[i].textContent = text2;
                    this.evalResults[i].tdOx = wp4[0] - (x + 0.5);
                    this.evalResults[i].tdOy = wp4[1] - y;
                    this.evalResults[i].tdOz = wp4[2] - (z + 0.5);
                    this.evalResults[i].textScale = scaleVal;
                    display.update(serverLevel, wp4[0], wp4[1], wp4[2], text2, scaleVal, td.billboard(), td.lineWidth(), td.backgroundExpr(), td.shadow(), td.seeThrough(), td.alignment(), td.opacity(), qualifying);
                    continue;
                }
                this.evalResults[i].qualifyingPlayers = null;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                try {
                    text = ScriptFormula.compile(td.textExpr() != null && !td.textExpr().isEmpty() ? td.textExpr() : "\"\"").evaluateStr(evalCtx.toScriptContext());
                }
                catch (Throwable ignored) {
                    text = td.textExpr() != null ? td.textExpr() : "";
                }
                double[] wp = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                float scaleVal = (float)evalCtx.evalNum(td.scale() != null && !td.scale().isEmpty() ? td.scale() : "0.1", null);
                this.evalResults[i].textContent = text;
                this.evalResults[i].tdOx = wp[0] - (x + 0.5);
                this.evalResults[i].tdOy = wp[1] - y;
                this.evalResults[i].tdOz = wp[2] - (z + 0.5);
                this.evalResults[i].textScale = scaleVal;
                display.update(serverLevel, wp[0], wp[1], wp[2], text, scaleVal, td.billboard(), td.lineWidth(), td.backgroundExpr(), td.shadow(), td.seeThrough(), td.alignment(), td.opacity());
                continue;
            }
            if (actualSpec instanceof RendererSpec.SoundSpec) {
                RendererSpec.SoundSpec ss = (RendererSpec.SoundSpec)actualSpec;
                if (!active) {
                    this.particleCooldowns[i] = 0;
                    continue;
                }
                int n = i;
                this.particleCooldowns[n] = this.particleCooldowns[n] + 1;
                if (ss.interval() > 0 && this.particleCooldowns[i] < ss.interval()) continue;
                this.particleCooldowns[i] = 0;
                try {
                    float vol = (float)evalCtx.evalNum(ss.volumeExpr(), null);
                    float pitch = (float)evalCtx.evalNum(ss.pitchExpr(), null);
                    for (ServerPlayer sp : serverLevel.players()) {
                        if (sp.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > 2304.0) continue;
                        sp.connection.send((Packet)new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvent.createVariableRangeEvent((Identifier)Identifier.tryParse((String)ss.soundId()))), SoundSource.BLOCKS, x + 0.5, y + 0.5, z + 0.5, vol, pitch, serverLevel.random.nextLong()));
                    }
                    continue;
                }
                catch (Throwable vol) {
                    continue;
                }
            }
            if (actualSpec instanceof RendererSpec.ArmorStandSpec) {
                RendererSpec.ArmorStandSpec as = (RendererSpec.ArmorStandSpec)actualSpec;
                ArmorStandDisplay display = this.armorStandDisplays[i];
                if (display == null) continue;
                if (!active) {
                    display.despawnAll(serverLevel);
                    continue;
                }
                double[] wp5 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                display.update(serverLevel, wp5[0], wp5[1], wp5[2], as.small(), as.invisible(), as.marker());
                continue;
            }
            if (actualSpec instanceof RendererSpec.InteractionSpec) {
                MachineInteraction marker = this.interactionMarkers[i];
                if (marker == null) continue;
                if (!active) {
                    marker.despawnAll(serverLevel);
                    continue;
                }
                double[] wp7 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
                // Same relative-offset convention every other per-spec renderer already uses (e.g.
                // ParticleSpec's specRelX/Y/Z) — stashed on the marker so a click can report back
                // WHERE (relative to the machine) it was clicked, for DataMultiBlockMachineBlockEntity
                // .runInteractScript to resolve into a schema-relative MultiBlock part.
                marker.setLastOffset(wp7[0] - (x + 0.5), wp7[1] - y, wp7[2] - (z + 0.5));
                marker.render(serverLevel, wp7[0], wp7[1], wp7[2]);
                continue;
            }
            if (!(actualSpec instanceof RendererSpec.BlockDisplaySpec)) continue;
            RendererSpec.BlockDisplaySpec bd = (RendererSpec.BlockDisplaySpec)actualSpec;
            BlockDisplayHelper display = this.blockDisplayHelpers[i];
            if (display == null) continue;
            if (!active) {
                display.despawnAll(serverLevel);
                continue;
            }
            double[] wp6 = this.resolveSpecLocation(spec, evalCtx, serverLevel, x, y, z);
            float scaleVal = (float)evalCtx.evalNum(bd.scale() != null && !bd.scale().isEmpty() ? bd.scale() : "1.0", null);
            try {
                blockId = ScriptFormula.compile(bd.blockStateExpr()).evaluateStr(evalCtx.toScriptContext());
            }
            catch (Throwable ignored) {
                blockId = bd.blockStateExpr();
            }
            display.update(serverLevel, wp6[0], wp6[1], wp6[2], blockId, scaleVal);
        }
        } finally {
            // Cleared so a sharedEval reached from OUTSIDE a tick cannot read a stale block state
            // from whenever this manager last ticked.
            this.scopeCtx = null;
            this.scopeState = null;
            this.scopeTick = -1;
        }
    }

    public void close() {
        for (BetterModelRenderer betterModelMachineRenderer : this.bmRenderers) {
            if (betterModelMachineRenderer == null) continue;
            betterModelMachineRenderer.close();
        }
        for (MegRenderer modelEngineMachineRenderer : this.meRenderers) {
            if (modelEngineMachineRenderer == null) continue;
            modelEngineMachineRenderer.close();
        }
        Arrays.fill(this.currentItems, null);
        if (this.fluidDisplays != null) for (FluidLevelDisplay d : this.fluidDisplays) {
            if (d == null) continue;
            d.despawnAll(null);
        }
        if (this.textDisplays != null) for (TextLevelDisplay d : this.textDisplays) {
            if (d == null) continue;
            d.despawnAll(null);
        }
        if (this.armorStandDisplays != null) for (ArmorStandDisplay d : this.armorStandDisplays) {
            if (d == null) continue;
            d.despawnAll(null);
        }
        if (this.blockDisplayHelpers != null) for (BlockDisplayHelper d : this.blockDisplayHelpers) {
            if (d == null) continue;
            d.despawnAll(null);
        }
        if (this.interactionMarkers != null) for (MachineInteraction m : this.interactionMarkers) {
            if (m == null) continue;
            m.despawnAll(null);
            m.discard(); // drop from the global click-lookup map so a stale id never resolves again
        }
    }

    public List<BetterModelRenderer> betterModelRenderers() {
        return Collections.unmodifiableList(this.bmRenderers);
    }

    public List<MegRenderer> modelEngineRenderers() {
        return Collections.unmodifiableList(this.meRenderers);
    }

    /** Index of the renderer entry declared with {@code "id": "<id>"} in JSON (see
     *  RendererSpec#id/MachineDefinitionLoader), or -1 if no entry has that id — {@code null}/blank
     *  never matches anything, since an unset id is {@code null} on every spec. Used by
     *  {@code Machine.get_renderer(id)} (MachineType.java) to resolve a script-facing handle. */
    public int indexOfId(String id) {
        if (id == null || id.isBlank()) return -1;
        for (int i = 0; i < this.specs.size(); i++) {
            if (id.equals(this.specs.get(i).id())) return i;
        }
        return -1;
    }

    /** The {@link BetterModelRenderer} declared with {@code "id": "<id>"}, or {@code null}
     *  if no such id exists OR the entry with that id isn't a {@code "bettermodel"} renderer. */
    public BetterModelRenderer betterModelRendererById(String id) {
        int i = indexOfId(id);
        return i >= 0 ? this.bmRenderers.get(i) : null;
    }

    /** The {@link MegRenderer} declared with {@code "id": "<id>"}, or {@code null}
     *  if no such id exists OR the entry with that id isn't a {@code "modelengine"} renderer. */
    public MegRenderer modelEngineRendererById(String id) {
        int i = indexOfId(id);
        return i >= 0 ? this.meRenderers.get(i) : null;
    }

    public net.minecraft.world.item.ItemStack[] currentItems() {
        return this.currentItems;
    }

    public List<RendererSpec> specs() {
        return this.specs;
    }

    public boolean isEmpty() {
        return this.specs.isEmpty();
    }

    public EvalResult evalResult(int i) {
        return i >= 0 && i < this.evalResults.length ? this.evalResults[i] : null;
    }

    public static double[] resolveLocation(ScriptValue val, double machX, double machY, double machZ) {
        double nan = Double.NaN;
        if (val == null || val instanceof ScriptValue.Null) return null;
        if (val instanceof ScriptValue.Obj obj && obj.instance() instanceof LocationType.LocationRef lc) {
            return new double[]{lc.x(), lc.y(), lc.z(), nan, nan, nan};
        }
        // A Vector here (e.g. Machine.get_renderer(id).bone_location(name)) is already an ABSOLUTE
        // world position, same as LocationRef above — NOT a machine-relative offset like the plain
        // Array/Num cases below, since that's what bone_location's own live bone-tracking returns.
        if (val instanceof ScriptValue.Obj obj && obj.instance() instanceof org.joml.Vector3d v) {
            return new double[]{v.x, v.y, v.z, nan, nan, nan};
        }
        if (val instanceof ScriptValue.Array a && a.elements().size() >= 3) {
            var e = a.elements();
            double rx = e.size() >= 5 ? e.get(3).asNum() : nan;
            double ry = e.size() == 4 ? e.get(3).asNum() : (e.size() >= 5 ? e.get(4).asNum() : nan);
            double rz = e.size() >= 6 ? e.get(5).asNum() : nan;
            return new double[]{machX + 0.5 + e.get(0).asNum(), machY + e.get(1).asNum(), machZ + 0.5 + e.get(2).asNum(), rx, ry, rz};
        }
        if (val instanceof ScriptValue.Num n) {
            return new double[]{machX + 0.5, machY + n.value(), machZ + 0.5, nan, nan, nan};
        }
        return null;
    }

    /** Matches the {@code "{rendererid}:meg:{bone_name}"} / {@code "{rendererid}:bm:{bone_name}"}
     *  item-display location syntax — a stable renderer id (see {@link RendererSpec#id}), a literal
     *  engine tag picking which id-namespace to look the id up in, then the target bone's name.
     *  Bone names themselves may contain ':' (colon-separated group paths in some model exports), so
     *  this only splits the first two colons and takes everything after as the bone name. */
    private static final java.util.regex.Pattern BONE_LOCATION_PATTERN =
        java.util.regex.Pattern.compile("^([^:]+):(meg|bm):(.+)$");

    /** Resolves the {@code "{rendererid}:meg/bm:{bone_name}"} bone-path location syntax to that
     *  bone's LIVE world position, or {@code null} if {@code locExpr} doesn't match the syntax, no
     *  renderer with that id/engine exists, or the bone can't be found right now (e.g. the model
     *  isn't currently shown). Lets an item_display (or any other positioned renderer) track a bone
     *  on a DIFFERENT renderer entry declared elsewhere in the same machine's config, addressed by
     *  its stable id — see {@code Machine.get_renderer(id)} (MachineType.java) for the script-facing
     *  counterpart of the same id namespace. */
    /**
     * What a renderer's {@code location} string IS — decided once per distinct string.
     *
     * <p>Classifying it is a pure function of the text: a bone reference matches a regex, a literal
     * offset splits and parses to numbers, anything else is an expression. None of that can change
     * between ticks, and all of it was being redone per renderer per tick — the regex alone was
     * 1.61% of server wall time in a profile, and the literal path ran a substring, a split and up
     * to six parseDouble calls on top of it.
     */
    private record LocationPlan(Kind kind, String rendererId, String engine, String boneName,
                                 double[] offsets) {
        enum Kind { BONE, LITERAL, EXPRESSION }
    }

    /** Keyed by the location string, which is what the plan depends on and nothing else. */
    private static final java.util.concurrent.ConcurrentHashMap<String, LocationPlan> LOCATION_PLANS =
            new java.util.concurrent.ConcurrentHashMap<>();

    private static LocationPlan planFor(String locExpr) {
        var m = BONE_LOCATION_PATTERN.matcher(locExpr);
        if (m.matches()) {
            return new LocationPlan(LocationPlan.Kind.BONE, m.group(1), m.group(2), m.group(3), null);
        }
        // The literal case is pre-checked with isPlainNumberArrayLocation rather than letting
        // parseDouble throw, for the reason recorded where that helper is defined — but now it runs
        // once per distinct string instead of once per renderer per tick, so the throw it avoids
        // was never the whole cost anyway.
        if (locExpr.startsWith("[") && locExpr.lastIndexOf(93) > 0) {
            String[] parts = locExpr.substring(1, locExpr.lastIndexOf(93)).split(",");
            if (parts.length >= 3 && isPlainNumberArrayLocation(parts)) {
                double rx = parts.length >= 5 ? Double.parseDouble(parts[3].trim()) : Double.NaN;
                double ry = parts.length == 4 ? Double.parseDouble(parts[3].trim())
                        : (parts.length >= 5 ? Double.parseDouble(parts[4].trim()) : Double.NaN);
                double rz = parts.length >= 6 ? Double.parseDouble(parts[5].trim()) : Double.NaN;
                return new LocationPlan(LocationPlan.Kind.LITERAL, null, null, null, new double[]{
                        Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()),
                        Double.parseDouble(parts[2].trim()), rx, ry, rz});
            }
        }
        return new LocationPlan(LocationPlan.Kind.EXPRESSION, null, null, null, null);
    }

    /**
     * A rotation component, recomputed only on the ticks its value is actually sent.
     *
     * <p>Throttled ONLY for a formula that depends on {@code tick} — a continuously spinning one.
     * Anything else produces the same value every tick anyway and goes through the shared cache, so
     * throttling it would save nothing and only add a way to be wrong. A formula whose signature is
     * not computed yet is evaluated normally, so the first pass is never throttled.
     */
    private float rotationValue(int specIndex, int axis, String expr, ServerLevel serverLevel,
                                 double x, double y, double z, MachineRenderContext evalCtx) {
        String e = expr != null ? expr : "0";
        if (this.lastRotX == null) {
            int n = this.specs.size();
            this.lastRotX = new float[n]; this.lastRotY = new float[n]; this.lastRotZ = new float[n];
            java.util.Arrays.fill(this.lastRotX, Float.NaN);
            java.util.Arrays.fill(this.lastRotY, Float.NaN);
            java.util.Arrays.fill(this.lastRotZ, Float.NaN);
        }
        float[] store = axis == 0 ? this.lastRotX : axis == 1 ? this.lastRotY : this.lastRotZ;
        if (!this.rotationTick && !Float.isNaN(store[specIndex]) && dependsOnTick(e)) {
            return store[specIndex];
        }
        float v = (float) this.sharedEvalNum(serverLevel, x, y, z, e, evalCtx);
        store[specIndex] = v;
        return v;
    }

    private static boolean dependsOnTick(String expr) {
        FormulaSignature sig = FORMULA_SIGNATURES.get(expr);
        return sig != null && sig.cacheable() && sig.keyVars().contains("tick");
    }

    /**
     * A renderer's {@code when}, through the cross-instance cache like every other formula.
     *
     * <p>It was not. The value formulas — item, rotations, scale, location — all went through
     * sharedEval and hit 92.6% of the time, but the gate deciding whether to evaluate them at all
     * went straight to {@code ScriptFormula.compile(...).evaluateBool(...)}. So seventy-nine shafts
     * each evaluated all three of their {@code Machine.axis == "..." && Machine.activated}
     * conditions from scratch every tick, uncached, when the answer depends only on the block state
     * they share. That is 237 full evaluations a tick, each doing a PolyClass property dispatch, to
     * compute a handful of distinct results.
     *
     * <p>The keyword forms — always/never, and the fast property reads — never touched the script
     * engine and still do not; only a real expression is routed. Non-cacheable expressions fall
     * through inside sharedEval exactly as they do everywhere else.
     */
    private boolean evaluateWhen(dev.arubik.craftengine.machine.render.WhenCondition when,
                                  MachineRenderContext evalCtx, ServerLevel serverLevel,
                                  double x, double y, double z) {
        if (!(when instanceof dev.arubik.craftengine.machine.render.WhenCondition.ScriptGate gate)
                || serverLevel == null) {
            return when.evaluate(evalCtx);
        }
        return this.sharedEval(serverLevel, x, y, z, gate.expr(), evalCtx).asBool();
    }

    private double[] resolveBoneLocation(LocationPlan plan) {
        String rendererId = plan.rendererId();
        String engine = plan.engine();
        String boneName = plan.boneName();
        double[] pos;
        if ("bm".equals(engine)) {
            dev.arubik.craftengine.machine.render.renderer.BetterModelRenderer r = this.betterModelRendererById(rendererId);
            pos = r == null ? null : r.boneWorldPosition(boneName);
        } else {
            dev.arubik.craftengine.machine.render.renderer.MegRenderer r = this.modelEngineRendererById(rendererId);
            pos = r == null ? null : r.boneWorldPosition(boneName);
        }
        if (pos == null) return null;
        return new double[]{pos[0], pos[1], pos[2], Double.NaN, Double.NaN, Double.NaN};
    }

    private double[] resolveSpecLocation(RendererSpec spec, MachineRenderContext evalCtx, ServerLevel serverLevel, double machX, double machY, double machZ) {
        String locExpr = spec.locationExpr();
        if (locExpr != null && !locExpr.isEmpty()) {
            LocationPlan plan = LOCATION_PLANS.computeIfAbsent(locExpr, RendererManager::planFor);
            if (plan.kind() == LocationPlan.Kind.BONE) {
                double[] boneLoc = this.resolveBoneLocation(plan);
                if (boneLoc != null) return boneLoc;
            } else if (plan.kind() == LocationPlan.Kind.LITERAL) {
                double[] o = plan.offsets();
                return new double[]{machX + 0.5 + o[0], machY + o[1], machZ + 0.5 + o[2],
                        o[3], o[4], o[5]};
            }
            try {
                // sharedEval, not a plain compile+evaluate — a bracket-literal "location":
                // {x,y,z} (parsed into one combined "[exprX, exprY, exprZ]" array-literal formula
                // by MachineDefinitionLoader#parseLocationExpr) is exactly as likely to be a PURE
                // function of the block itself (energy_windmill's offset only depends on facing)
                // as rot_x/y/z are — see sharedEval's own doc for the windmill/shaft examples.
                ScriptValue val = this.sharedEval(serverLevel, machX, machY, machZ, locExpr, evalCtx);
                double[] pos = RendererManager.resolveLocation(val, machX, machY, machZ);
                if (pos != null) {
                    return pos;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return new double[]{machX + 0.5, machY, machZ + 0.5, Double.NaN, Double.NaN, Double.NaN};
    }

    /** True only if every one of {@code parts} (a bracket-array location's comma-split pieces) is
     *  cheaply recognizable as a plain numeric literal (optional sign, digits, at most one dot) —
     *  used to skip the fast literal-offset parse path WITHOUT ever calling {@link
     *  Double#parseDouble} on something that isn't one, instead of the old
     *  try-parseDouble-and-catch-the-failure approach (see {@link #resolveSpecLocation}'s own note
     *  on why that was a real per-tick cost). Deliberately conservative: anything this returns
     *  false for still gets a fully correct answer via the ScriptFormula fallback right below —
     *  this is purely an optimization to skip the common all-literal case's overhead, never a
     *  correctness gate. */
    private static boolean isPlainNumberArrayLocation(String[] parts) {
        int checkUpTo = Math.min(parts.length, 6);
        for (int i = 0; i < checkUpTo; i++) {
            if (!looksLikePlainNumber(parts[i].trim())) return false;
        }
        return true;
    }

    private static boolean looksLikePlainNumber(String s) {
        if (s.isEmpty()) return false;
        int i = 0;
        char c0 = s.charAt(0);
        if (c0 == '-' || c0 == '+') i = 1;
        if (i >= s.length()) return false;
        boolean sawDigit = false;
        boolean sawDot = false;
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') { sawDigit = true; continue; }
            if (c == '.' && !sawDot) { sawDot = true; continue; }
            return false;
        }
        return sawDigit;
    }

    private List<double[]> resolvePositions(RendererSpec spec, MachineRenderContext evalCtx, double machX, double machY, double machZ) {
        String posExpr = spec.positionsExpr();
        if (posExpr == null || posExpr.isEmpty()) {
            return List.of();
        }
        try {
            ScriptValue val = ScriptFormula.compile(posExpr).evaluate(evalCtx.toScriptContext());
            if (!(val instanceof ScriptValue.Array arr)) {
                return List.of();
            }
            ArrayList<double[]> result = new ArrayList<>(arr.elements().size());
            for (ScriptValue elem : arr.elements()) {
                double[] pos = RendererManager.resolveLocation(elem, machX, machY, machZ);
                if (pos == null) continue;
                result.add(pos);
            }
            return result;
        }
        catch (Throwable ignored) {
            return List.of();
        }
    }

    private static float locRotX(double[] loc, float defaultVal) {
        return loc.length >= 4 && !Double.isNaN(loc[3]) ? (float)loc[3] : defaultVal;
    }

    private static float locRotY(double[] loc, float defaultVal) {
        return loc.length >= 5 && !Double.isNaN(loc[4]) ? (float)loc[4] : defaultVal;
    }

    private static float locRotZ(double[] loc, float defaultVal) {
        return loc.length >= 6 && !Double.isNaN(loc[5]) ? (float)loc[5] : defaultVal;
    }

    private MachineRenderContext buildPlayerContext(MachineRenderContext base, ServerPlayer sp, double x, double y, double z, float yaw) {
        CraftPlayer bukkit = sp.getBukkitEntity();
        String facing = RendererManager.yawToFacing(yaw);
        ScriptContext playerCtx = ScriptContext.builder().copyFrom(base.toScriptContext())
            .player(sp)
            .facing(facing, yaw)
            .build();
        return base.augmented(playerCtx);
    }

    private static String yawToFacing(float yaw) {
        float n = (yaw % 360.0f + 360.0f) % 360.0f;
        if (n < 45.0f || n >= 315.0f) {
            return "south";
        }
        if (n < 135.0f) {
            return "west";
        }
        if (n < 225.0f) {
            return "north";
        }
        return "east";
    }

    private static ParticleOptions resolveParticle(String name) {
        try {
            Identifier rl;
            Object id = name.toLowerCase(Locale.ROOT);
            if (!((String)id).contains(":")) {
                id = "minecraft:" + (String)id;
            }
            if ((rl = Identifier.tryParse((String)id)) == null) {
                return null;
            }
            ParticleType type = BuiltInRegistries.PARTICLE_TYPE.get(rl).map(Holder.Reference::value).orElse(null);
            if (type instanceof SimpleParticleType) {
                SimpleParticleType sp = (SimpleParticleType)type;
                return sp;
            }
            return null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    static final class FluidLevelDisplay {
        private static final double VIEW_DISTANCE = 48.0;
        private static final double VIEW_DISTANCE_SQ = 2304.0;
        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<UUID>();
        private Key lastFluidKey;
        private float lastScaleY = -1.0f;

        FluidLevelDisplay() {
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz, Key fluidKey, float scaleY) {
            boolean metaDirty;
            if (serverLevel == null) {
                return;
            }
            boolean itemChanged = !Objects.equals(fluidKey, this.lastFluidKey);
            boolean scaleChanged = Float.compare(scaleY, this.lastScaleY) != 0;
            boolean bl = metaDirty = itemChanged || scaleChanged;
            if (metaDirty) {
                this.lastFluidKey = fluidKey;
                this.lastScaleY = scaleY;
            }
            List<Object> metaValues = metaDirty ? this.buildMetadata(fluidKey, scaleY) : null;
            HashSet<UUID> stillVisible = new HashSet<UUID>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dz;
                double dy;
                double dx = sp.getX() - wx;
                if (dx * dx + (dy = sp.getY() - wy) * dy + (dz = sp.getZ() - wz) * dz > 2304.0) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    FluidLevelDisplay.sendPacket(sp, this.despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!this.shownTo.contains(sp.getUUID())) {
                    this.spawn(sp, wx, wy, wz);
                    if (metaValues == null) continue;
                    FluidLevelDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
                    continue;
                }
                if (!metaDirty) continue;
                FluidLevelDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
            }
            this.shownTo.retainAll(stillVisible);
            this.shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (this.shownTo.isEmpty()) {
                return;
            }
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    FluidLevelDisplay.sendPacket(sp, this.despawnPacket);
                }
            } else {
                for (Player bp : Bukkit.getOnlinePlayers()) {
                    if (!this.shownTo.contains(bp.getUniqueId())) continue;
                    try {
                        ServerPlayer sp = ((CraftPlayer)bp).getHandle();
                        sp.connection.send((Packet)this.despawnPacket);
                    }
                    catch (Throwable throwable) {}
                }
            }
            this.shownTo.clear();
            this.lastFluidKey = null;
            this.lastScaleY = -1.0f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            Object spawnPkt = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, wx, wy, wz, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            FluidLevelDisplay.sendPacket(sp, spawnPkt);
        }

        private List<Object> buildMetadata(Key fluidKey, float scaleY) {
            ArrayList<Object> values = new ArrayList<Object>();
            try {
                ItemStack bukkit;
                BukkitItemDefinition def = CraftEngineItems.byId((Key)fluidKey);
                if (def != null && (bukkit = def.buildBukkitItem()) != null) {
                    net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy((ItemStack)bukkit);
                    DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            DisplayData.Scale.addEntityData(new Vector3f(1.0f, scaleY, 1.0f), values);
            return values;
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet)packet);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    static final class TextLevelDisplay {
        private static final double VIEW_DISTANCE = 48.0;
        private static final double VIEW_DISTANCE_SQ = 2304.0;
        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<UUID>();
        private String lastText;
        private float lastScale = -1.0f;

        TextLevelDisplay() {
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz, String text, float scale, String billboard, int lineWidth, String backgroundExpr, boolean shadow, boolean seeThrough, String alignment, int opacity) {
            this.update(serverLevel, wx, wy, wz, text, scale, billboard, lineWidth, backgroundExpr, shadow, seeThrough, alignment, opacity, null);
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz, String text, float scale, String billboard, int lineWidth, String backgroundExpr, boolean shadow, boolean seeThrough, String alignment, int opacity, @Nullable Set<UUID> allowedPlayers) {
            boolean metaDirty;
            if (serverLevel == null) {
                return;
            }
            boolean textChanged = !Objects.equals(text, this.lastText);
            boolean scaleChanged = Float.compare(scale, this.lastScale) != 0;
            boolean bl = metaDirty = textChanged || scaleChanged;
            if (metaDirty) {
                this.lastText = text;
                this.lastScale = scale;
            }
            List<Object> metaValues = metaDirty ? this.buildMetadata(text, scale, billboard, lineWidth, backgroundExpr, shadow, seeThrough, alignment, opacity) : null;
            HashSet<UUID> stillVisible = new HashSet<UUID>();
            for (ServerPlayer sp : serverLevel.players()) {
                boolean allowed;
                double dz;
                double dy;
                double dx = sp.getX() - wx;
                boolean inRange = dx * dx + (dy = sp.getY() - wy) * dy + (dz = sp.getZ() - wz) * dz <= 2304.0;
                boolean bl2 = allowed = inRange && (allowedPlayers == null || allowedPlayers.contains(sp.getUUID()));
                if (!allowed) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    TextLevelDisplay.sendPacket(sp, this.despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!this.shownTo.contains(sp.getUUID())) {
                    this.spawn(sp, wx, wy, wz);
                    if (metaValues == null) continue;
                    TextLevelDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
                    continue;
                }
                if (!metaDirty) continue;
                TextLevelDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
            }
            this.shownTo.retainAll(stillVisible);
            this.shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (this.shownTo.isEmpty()) {
                return;
            }
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    TextLevelDisplay.sendPacket(sp, this.despawnPacket);
                }
            } else {
                for (Player bp : Bukkit.getOnlinePlayers()) {
                    if (!this.shownTo.contains(bp.getUniqueId())) continue;
                    try {
                        ServerPlayer sp = ((CraftPlayer)bp).getHandle();
                        sp.connection.send((Packet)this.despawnPacket);
                    }
                    catch (Throwable throwable) {}
                }
            }
            this.shownTo.clear();
            this.lastText = null;
            this.lastScale = -1.0f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            TextLevelDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, wx, wy, wz, 0.0f, 0.0f, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0));
        }

        private List<Object> buildMetadata(String text, float scale, String billboard, int lineWidth, String backgroundExpr, boolean shadow, boolean seeThrough, String alignment, int opacity) {
            EntityDataAccessor acc;
            ArrayList<Object> values = new ArrayList<Object>();
            try {
                MutableComponent nmsTextComp;
                try {
                    Component adv = MiniMessage.miniMessage().deserialize((text != null ? text : ""));
                    nmsTextComp = (net.minecraft.network.chat.MutableComponent) PaperAdventure.asVanilla((Component)adv);
                }
                catch (Throwable ignored2) {
                    nmsTextComp = net.minecraft.network.chat.Component.literal((String)(text != null ? text : ""));
                }
                DisplayData.TextDisplayData.Text.addEntityData(nmsTextComp, values);
            }
            catch (Throwable nmsTextComp) {
                // empty catch block
            }
            DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
            byte bbByte = TextLevelDisplay.billboardByte(billboard);
            try {
                DisplayData.BillboardConstraints.addEntityData(bbByte, values);
            }
            catch (Throwable ignored2) {
                // empty catch block
            }
            int bgColor = TextLevelDisplay.parseColor(backgroundExpr);
            try {
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(bgColor, values);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                byte styleFlags = 0;
                if (shadow) {
                    styleFlags = (byte)(styleFlags | 1);
                }
                if (seeThrough) {
                    styleFlags = (byte)(styleFlags | 2);
                }
                Field styleFlagsField = Display.TextDisplay.class.getDeclaredField("DATA_STYLE_FLAGS");
                styleFlagsField.setAccessible(true);
                EntityDataAccessor acc2 = (EntityDataAccessor)styleFlagsField.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc2, styleFlags));
            }
            catch (Throwable styleFlags) {
                // empty catch block
            }
            try {
                Field opacityField = Display.TextDisplay.class.getDeclaredField("DATA_TEXT_OPACITY");
                opacityField.setAccessible(true);
                acc = (EntityDataAccessor)opacityField.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, ((byte)opacity)));
            }
            catch (Throwable opacityField) {
                // empty catch block
            }
            try {
                Field lwField = Display.TextDisplay.class.getDeclaredField("DATA_LINE_WIDTH");
                lwField.setAccessible(true);
                acc = (EntityDataAccessor)lwField.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, lineWidth));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            return values;
        }

        private static byte billboardByte(String mode) {
            if (mode == null) {
                return 3;
            }
            return switch (mode.toLowerCase(Locale.ROOT)) {
                case "none" -> 0;
                case "vertical" -> 1;
                case "horizontal" -> 2;
                default -> 3;
            };
        }

        private static int parseColor(String expr) {
            if (expr == null || expr.equals("0") || expr.isBlank()) {
                return 0;
            }
            try {
                String s = expr.startsWith("#") ? expr.substring(1) : expr;
                return (int)Long.parseLong(s, 16);
            }
            catch (Throwable ignored) {
                return 0;
            }
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet)packet);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    static final class ArmorStandDisplay {
        private static final double VIEW_DISTANCE = 48.0;
        private static final double VIEW_DISTANCE_SQ = 2304.0;
        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<UUID>();

        ArmorStandDisplay() {
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz, boolean small, boolean invisible, boolean marker) {
            if (serverLevel == null) {
                return;
            }
            HashSet<UUID> stillVisible = new HashSet<UUID>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dz;
                double dy;
                double dx = sp.getX() - wx;
                if (dx * dx + (dy = sp.getY() - wy) * dy + (dz = sp.getZ() - wz) * dz > 2304.0) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    ArmorStandDisplay.sendPacket(sp, this.despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (this.shownTo.contains(sp.getUUID())) continue;
                this.spawn(sp, wx, wy, wz);
                this.sendMetadata(sp, small, invisible, marker);
            }
            this.shownTo.retainAll(stillVisible);
            this.shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (this.shownTo.isEmpty()) {
                return;
            }
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    ArmorStandDisplay.sendPacket(sp, this.despawnPacket);
                }
            } else {
                for (Player bp : Bukkit.getOnlinePlayers()) {
                    if (!this.shownTo.contains(bp.getUniqueId())) continue;
                    try {
                        ServerPlayer sp = ((CraftPlayer)bp).getHandle();
                        sp.connection.send((Packet)this.despawnPacket);
                    }
                    catch (Throwable throwable) {}
                }
            }
            this.shownTo.clear();
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            ArmorStandDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, wx, wy, wz, 0.0f, 0.0f, EntityType.ARMOR_STAND, 0, Vec3.ZERO, 0.0));
        }

        private void sendMetadata(ServerPlayer sp, boolean small, boolean invisible, boolean marker) {
            ArrayList<SynchedEntityData.DataValue> values = new ArrayList<SynchedEntityData.DataValue>();
            byte flags = 0;
            if (small) {
                flags = (byte)(flags | 1);
            }
            if (marker) {
                flags = (byte)(flags | 0x10);
            }
            flags = (byte)(flags | 8);
            try {
                EntityDataAccessor acc;
                if (invisible) {
                    Field sharedField = Entity.class.getDeclaredField("DATA_SHARED_FLAGS_ID");
                    sharedField.setAccessible(true);
                    acc = (EntityDataAccessor)sharedField.get(null);
                    // DATA_SHARED_FLAGS_ID is a byte-typed field — the bare int literal 32 autoboxes
                    // to Integer, and SynchedEntityData's write path casts it straight to Byte with
                    // no coercion, crashing the client's packet decoder (ClassCastException: Integer
                    // cannot be cast to Byte) the moment this entity's metadata syncs.
                    values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, (byte)32));
                }
                Field asFlags = ArmorStand.class.getDeclaredField("DATA_CLIENT_FLAGS");
                asFlags.setAccessible(true);
                acc = (EntityDataAccessor)asFlags.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, flags));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!values.isEmpty()) {
                ArmorStandDisplay.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values));
            }
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet)packet);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    static final class BlockDisplayHelper {
        private static final double VIEW_DISTANCE = 48.0;
        private static final double VIEW_DISTANCE_SQ = 2304.0;
        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = new HashSet<UUID>();
        private String lastBlockId;
        private float lastScale = -1.0f;

        BlockDisplayHelper() {
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void update(ServerLevel serverLevel, double wx, double wy, double wz, String blockId, float scale) {
            boolean metaDirty;
            if (serverLevel == null) {
                return;
            }
            boolean blockChanged = !Objects.equals(blockId, this.lastBlockId);
            boolean scaleChanged = Float.compare(scale, this.lastScale) != 0;
            boolean bl = metaDirty = blockChanged || scaleChanged;
            if (metaDirty) {
                this.lastBlockId = blockId;
                this.lastScale = scale;
            }
            List<Object> metaValues = metaDirty ? this.buildMetadata(blockId, scale) : null;
            HashSet<UUID> stillVisible = new HashSet<UUID>();
            for (ServerPlayer sp : serverLevel.players()) {
                double dz;
                double dy;
                double dx = sp.getX() - wx;
                if (dx * dx + (dy = sp.getY() - wy) * dy + (dz = sp.getZ() - wz) * dz > 2304.0) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    BlockDisplayHelper.sendPacket(sp, this.despawnPacket);
                    continue;
                }
                stillVisible.add(sp.getUUID());
                if (!this.shownTo.contains(sp.getUUID())) {
                    this.spawn(sp, wx, wy, wz);
                    if (metaValues == null) continue;
                    BlockDisplayHelper.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
                    continue;
                }
                if (!metaDirty) continue;
                BlockDisplayHelper.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, metaValues));
            }
            this.shownTo.retainAll(stillVisible);
            this.shownTo.addAll(stillVisible);
        }

        void despawnAll(ServerLevel serverLevel) {
            if (this.shownTo.isEmpty()) {
                return;
            }
            if (serverLevel != null) {
                for (ServerPlayer sp : serverLevel.players()) {
                    if (!this.shownTo.contains(sp.getUUID())) continue;
                    BlockDisplayHelper.sendPacket(sp, this.despawnPacket);
                }
            } else {
                for (Player bp : Bukkit.getOnlinePlayers()) {
                    if (!this.shownTo.contains(bp.getUniqueId())) continue;
                    try {
                        ServerPlayer sp = ((CraftPlayer)bp).getHandle();
                        sp.connection.send((Packet)this.despawnPacket);
                    }
                    catch (Throwable throwable) {}
                }
            }
            this.shownTo.clear();
            this.lastBlockId = null;
            this.lastScale = -1.0f;
        }

        private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
            BlockDisplayHelper.sendPacket(sp, MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, wx, wy, wz, 0.0f, 0.0f, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0.0));
        }

        private List<Object> buildMetadata(String blockId, float scale) {
            ArrayList<Object> values = new ArrayList<Object>();
            try {
                Block block;
                Identifier rl;
                Object id = blockId.toLowerCase(Locale.ROOT);
                if (!((String)id).contains(":")) {
                    id = "minecraft:" + (String)id;
                }
                if ((rl = Identifier.tryParse((String)id)) != null && (block = (Block)BuiltInRegistries.BLOCK.get(rl).map(Holder.Reference::value).orElse(null)) != null) {
                    DisplayData.BlockDisplayData.BlockState.addEntityData(block.defaultBlockState(), values);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
            return values;
        }

        private static void sendPacket(ServerPlayer sp, Object packet) {
            try {
                sp.connection.send((Packet)packet);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public static final class EvalResult {
        public boolean active;
        public RendererSpec.EvaluatedItemDisplay itemDisplay;
        public boolean emittedThisTick;
        public int particleCount;
        public double pSpreadX;
        public double pSpreadY;
        public double pSpreadZ;
        public double pSpeed;
        public double pDvx;
        public double pDvy;
        public double pDvz;
        public String pType;
        public ParticleUtils.Shape pShape;
        public ParticleUtils.Direction pDir;
        public int ceFluidLevel;
        public String fluidTypeValue;
        public double specRelX;
        public double specRelY;
        public double specRelZ;
        public String textContent;
        public double tdOx;
        public double tdOy;
        public double tdOz;
        public float textScale;
        public Set<UUID> qualifyingPlayers;
    }
}

