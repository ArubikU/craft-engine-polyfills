package dev.arubik.craftengine.script;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import dev.arubik.craftengine.script.types.primitive.*;
import dev.arubik.craftengine.script.types.world.*;
import dev.arubik.craftengine.script.types.entity.*;
import dev.arubik.craftengine.script.types.machine.*;
import dev.arubik.craftengine.script.types.resource.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evaluation context for script expressions. Holds named variables and
 * typed class instances. Completely NMS — no Bukkit types.
 */
public final class ScriptContext {

    private final Map<String, ScriptValue> vars;
    private final Map<String, ScriptValue> classInstances;

    private ScriptContext(Map<String, ScriptValue> vars, Map<String, ScriptValue> classInstances) {
        this.vars = vars;
        this.classInstances = classInstances;
    }

    /** Set only while {@link #beginTracking} is active — records every {@code getVar} name read
     *  during that window, so a caller can determine EXACTLY which context variables a given
     *  formula string actually depends on (used by {@code RendererManager}'s cross-instance shared-
     *  formula cache to build a precise cache key instead of guessing via a text denylist). A
     *  ThreadLocal, not an instance field, since a single formula string is evaluated against many
     *  DIFFERENT ScriptContext instances over its lifetime — tracking belongs to the CALL, not to
     *  any one context object. */
    private static final ThreadLocal<java.util.Set<String>> TRACKED_VARS = new ThreadLocal<>();

    /** Starts recording every {@code getVar} name read on this thread until {@link #endTracking}.
     *  Returns the (initially empty, then live-mutated) set that will hold them. */
    public static java.util.Set<String> beginTracking() {
        java.util.Set<String> set = new java.util.HashSet<>();
        TRACKED_VARS.set(set);
        return set;
    }

    public static void endTracking() {
        TRACKED_VARS.remove();
    }

    public ScriptValue getVar(String name) {
        java.util.Set<String> tracked = TRACKED_VARS.get();
        if (tracked != null) tracked.add(name);
        return vars.getOrDefault(name, ScriptValue.NULL);
    }

    /** Same lookup as {@link #getVar}, but never recorded by an active {@link #beginTracking}
     *  window. Used ONLY for {@code ScriptFormula#callBuiltin}'s user-function-override check,
     *  which does a getVar(functionName) on EVERY builtin call (sin(...), CraftEngineItem(...),
     *  tick(), ...) purely to see whether a script-defined function shadows it — that probe isn't
     *  a real data dependency and must not pollute dependency tracking with the function's own
     *  name, or a pure-literal call like {@code CraftEngineItem("cml:foo")} would wrongly look
     *  ctx-dependent. A genuine variable read (identifiers, not call targets) must still go
     *  through {@link #getVar} and be tracked EVEN when it resolves to NULL — a top-level Assign
     *  probed against an empty context (see {@code ScriptProgram#isTopLevelCacheable}) needs an
     *  unresolved name to still register as a real dependency, or the probe can't tell "this
     *  formula truly reads nothing new" apart from "this formula reads a real ctx var that just
     *  happened to miss during the probe" — the latter was a real caching-correctness bug. */
    public ScriptValue peekVar(String name) {
        return vars.getOrDefault(name, ScriptValue.NULL);
    }

    public ScriptValue getClassInstance(String name) {
        return classInstances.getOrDefault(name, ScriptValue.NULL);
    }

    /**
     * The one lookup every dot-access does: a class instance if there is one, otherwise a plain
     * variable. Exactly {@code getClassInstance(name)} falling back to {@code getVar(name)} — same
     * order, same dependency-tracking behaviour (only the {@code getVar} leg records a read, since
     * that is the only leg that reaches it).
     *
     * <p>Exists so the compiler can emit ONE call instead of open-coding the pair with a branch
     * between them. Reading a receiver is not a place that needs three statements and a jump in
     * the generated code; it is one question with one answer.
     */
    public ScriptValue getClassOrVar(String name) {
        ScriptValue sv = classInstances.getOrDefault(name, ScriptValue.NULL);
        return sv != ScriptValue.NULL ? sv : getVar(name);
    }

    /**
     * {@link #getClassOrVar} fused with the coercion, so a read that is only ever used as a number
     * never materialises a {@code ScriptValue} local at all.
     *
     * <p>Reading a variable to do arithmetic with it is one operation, and the generated code should
     * say so: {@code double rpm = ctx.getNum("BASE_RPM")}, not a boxed intermediate that exists for
     * exactly one {@code asNum()} call. Same lookup and same coercion as before — {@code asNum} is
     * total over every {@code ScriptValue} shape, including {@code NULL}, which yields 0.
     */
    public double getNum(String name) {
        return getClassOrVar(name).asNum();
    }

    /** {@link #getNum}'s boolean counterpart. */
    public boolean getBool(String name) {
        return getClassOrVar(name).asBool();
    }

    /** {@link #getNum}'s string counterpart. */
    public String getStr(String name) {
        return getClassOrVar(name).asStr();
    }

    public boolean hasVar(String name) {
        return vars.containsKey(name);
    }

    public boolean hasClass(String name) {
        return classInstances.containsKey(name);
    }

    public Map<String, ScriptValue> vars() { return vars; }
    public Map<String, ScriptValue> classInstances() { return classInstances; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final LinkedHashMap<String, ScriptValue> vars = new LinkedHashMap<>();
        private final LinkedHashMap<String, ScriptValue> classes = new LinkedHashMap<>();

        public Builder num(String name, double v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder bool(String name, boolean v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder str(String name, String v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder item(String name, ItemStack s) {
            vars.put(name, s == null || s.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(s));
            return this;
        }

        public Builder val(String name, ScriptValue v) {
            vars.put(name, v != null ? v : ScriptValue.NULL);
            return this;
        }

        /** Bulk-merges a precomputed name->value map straight into {@code vars} — the vars-side
         *  counterpart of {@link #typedAll} for {@code classes}. Used by {@link ScriptProgram}'s
         *  cached pure-defs fast path (see {@code ScriptProgram#cachedDefsContext}) to splice a
         *  once-computed set of function bindings into a live per-call context cheaply. */
        public Builder valsAll(Map<String, ScriptValue> precomputed) {
            if (precomputed != null) vars.putAll(precomputed);
            return this;
        }

        public Builder typed(String className, Object instance) {
            if (instance != null) {
                classes.put(className, ScriptValue.ofObj(className, instance));
            }
            return this;
        }

        /** Bulk-merges a precomputed name->value map straight in — for callers on a hot per-tick
         *  path binding a fixed set of global singletons (see {@code DataMachineBlockEntity
         *  #GLOBAL_SCRIPT_SINGLETONS}) that never change between calls, so there's no reason to
         *  re-wrap each one via {@link #typed} (a fresh {@code ScriptValue.ofObj} allocation) every
         *  single tick for every machine. */
        public Builder typedAll(Map<String, ScriptValue> precomputed) {
            if (precomputed != null) classes.putAll(precomputed);
            return this;
        }

        public Builder player(ServerPlayer p) {
            if (p != null) classes.put("Player", PlayerType.wrap(p));
            return this;
        }

        /** Binds the {@code event} variable (lowercase — read like {@code item}/{@code slot}, not a
         *  global class like {@code Player.*}) for an {@code on_*} hook's cancel/adjust object. */
        public Builder event(dev.arubik.craftengine.script.event.ScriptEvent e) {
            if (e != null) vars.put("event", dev.arubik.craftengine.script.types.event.EventType.wrap(e));
            return this;
        }

        public Builder world(ServerLevel level) {
            if (level != null) classes.put("World", WorldType.wrap(level));
            return this;
        }

        public Builder block(ServerLevel level, BlockPos pos) {
            if (level != null && pos != null) classes.put("Block", BlockType.wrap(level, pos));
            return this;
        }

        /**
         * Binds a "Machine" class instance for whatever data-driven machine block entity sits at
         * {@code pos} (a no-op if none is loaded there) — for a script that needs to reach into a
         * block it just placed/found rather than the machine it's already running on, e.g. an
         * item behavior's {@code on_place_block} handing item-carried typed state/inventory onto
         * the freshly placed block via {@code Machine.set_typed}/{@code push_item_to_inventory}.
         */
        public Builder machineAt(ServerLevel level, BlockPos pos) {
            if (level == null || pos == null) return this;
            try {
                var be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
                if (be != null && be.controller instanceof dev.arubik.craftengine.block.entity.PersistentBlockEntity pbe) {
                    classes.put("Machine", dev.arubik.craftengine.script.types.machine.MachineType.wrap(level, pos, "north", pbe));
                }
            } catch (Throwable ignored) {
            }
            return this;
        }

        public Builder entity(net.minecraft.world.entity.Entity e) {
            if (e != null) classes.put("Entity", EntityType.wrap(e));
            return this;
        }

        public Builder vector(String name, double x, double y, double z) {
            classes.put(name, VectorType.wrap(x, y, z));
            return this;
        }

        public Builder redstone(int power) {
            vars.put("redstone", ScriptValue.of(power));
            vars.put("powered", ScriptValue.of(power > 0));
            vars.put("redstone_power", ScriptValue.of(power));
            return this;
        }

        public Builder machine(double rpm, double overclock, double efficiency,
                              double progress, double maxProgress, double tier,
                              boolean processing, boolean powered, boolean overclocked, boolean hasFuel) {
            vars.put("rpm", ScriptValue.of(rpm));
            vars.put("overclock", ScriptValue.of(overclock));
            vars.put("efficiency", ScriptValue.of(efficiency));
            vars.put("progress", ScriptValue.of(progress));
            vars.put("max_progress", ScriptValue.of(maxProgress));
            vars.put("tier", ScriptValue.of(tier));
            vars.put("processing", ScriptValue.of(processing));
            vars.put("powered", ScriptValue.of(powered));
            vars.put("overclocked", ScriptValue.of(overclocked));
            vars.put("has_fuel", ScriptValue.of(hasFuel));
            return this;
        }

        // Deliberately does NOT bind a bare "facing" global — it collided with Machine.facing (now
        // also removed) and was easy to shadow/misread against a script's own local var of the same
        // name. Scripts read facing_dx/dy/dz below directly, or go through Machine.block.property
        // ("facing")/("face") for the string form.
        public Builder facing(String facing, float facingYaw) {
            vars.put("facing_angle", ScriptValue.of(facingYaw));
            int[] off = facingOffset(facing);
            vars.put("facing_dx", ScriptValue.of(off[0]));
            vars.put("facing_dy", ScriptValue.of(off[1]));
            vars.put("facing_dz", ScriptValue.of(off[2]));
            return this;
        }

        public Builder copyFrom(ScriptContext other) {
            vars.putAll(other.vars);
            classes.putAll(other.classInstances);
            return this;
        }

        // MUST still be a real defensive copy, NOT a live view over this builder's own maps —
        // ScriptProgram.runStatements calls build() repeatedly on the SAME builder while
        // continuing to mutate it afterward (e.g. a FunctionDef snapshots definingCtx = b.build()
        // and then later top-level statements keep calling b.val(...)); a live view would let that
        // later mutation leak backward into the earlier "snapshot", corrupting lexical closures.
        // What we DO avoid is Map.copyOf's specific overhead (entries-array + hash-probing
        // ImmutableCollections.MapN construction, confirmed via profiling as ~7.6% of total
        // server-thread time in the DataMachineBlockEntity.tick -> runActionScript hot path) —
        // a plain LinkedHashMap copy-constructor is a real independent copy too, just without that
        // immutable-map-specific construction cost.
        public ScriptContext build() {
            return new ScriptContext(
                java.util.Collections.unmodifiableMap(new LinkedHashMap<>(vars)),
                java.util.Collections.unmodifiableMap(new LinkedHashMap<>(classes)));
        }

        /** Zero-copy "read this builder's CURRENT state right now" view — a live wrapper over this
         *  builder's own {@code vars}/{@code classes} maps, NOT a defensive copy. Only safe where
         *  the returned {@link ScriptContext} is read synchronously and then discarded BEFORE the
         *  next mutation to this builder — e.g. {@code ScriptProgram.runStatements}' per-statement
         *  "evaluate this one formula against current state" snapshots (an {@code Assign}'s RHS, an
         *  {@code if}/{@code while} condition, a {@code for}'s iterable expr), which are consumed in
         *  the same statement they're built for and never held onward. Do NOT use this for anything
         *  captured long-term across future mutations (a {@code FunctionDef}'s {@code definingCtx}
         *  closure, or a context returned out of {@code evaluate()} to a caller) — those need
         *  {@link #build()}'s real copy, or a later mutation would silently corrupt them. Profiling
         *  showed the per-statement {@code build()} calls in that hot loop (one full defensive copy
         *  of the WHOLE accumulated context per statement, for a value read once and thrown away)
         *  costing real server-thread time via {@code LinkedHashMap}'s copy-constructor. */
        public ScriptContext peek() {
            return new ScriptContext(
                java.util.Collections.unmodifiableMap(vars),
                java.util.Collections.unmodifiableMap(classes));
        }

        private static int[] facingOffset(String facing) {
            if (facing == null) return new int[]{0, 0, -1};
            return switch (facing.toLowerCase()) {
                case "south" -> new int[]{0, 0, 1};
                case "east" -> new int[]{1, 0, 0};
                case "west" -> new int[]{-1, 0, 0};
                case "up" -> new int[]{0, 1, 0};
                case "down" -> new int[]{0, -1, 0};
                default -> new int[]{0, 0, -1};
            };
        }
    }
}
