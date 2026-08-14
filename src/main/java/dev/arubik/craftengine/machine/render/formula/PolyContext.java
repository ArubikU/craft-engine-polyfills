package dev.arubik.craftengine.machine.render.formula;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable variable-resolution scope passed to {@link PolyFormula#evaluate}.
 *
 * <p>A context holds two namespaces:
 * <ul>
 *   <li><b>vars</b> — flat {@code name → PolyValue} entries for simple variables
 *       ({@code rpm}, {@code processing}, …)</li>
 *   <li><b>classes</b> — named {@link PolyClass} objects addressable as
 *       {@code ClassName.property} or {@code ClassName.method(args)} in
 *       expressions</li>
 * </ul>
 *
 * <h3>Standard machine variables</h3>
 * <pre>
 *   rpm, overclock, efficiency, progress, max_progress, tier
 *   processing, powered, overclocked, has_fuel          (booleans)
 *   upgrade_count, overclock_count, upgrade_efficiency   (upgrade counts)
 *   upgrade_{type}                                       (per-type count)
 * </pre>
 *
 * <h3>Standard classes</h3>
 * <pre>
 *   Inventory  — see {@link InventoryClass}
 *   Upgrades   — see {@link UpgradesClass}
 * </pre>
 *
 * <h3>Example formula usage</h3>
 * <pre>{@code
 * // Renderer speed driven by overclock level
 * { "speed": "overclock" }
 * // Show glow layer only when overclocked beyond 1.5×
 * { "when": "overclock > 1.5" }
 * // Scale an item display by overclock level
 * { "scale": "0.3 + overclock * 0.05" }
 * // Show only when a specific item is in slot 0
 * { "when": "is_not_empty(slot(0)) && matches(slot(0), 'minecraft:gold_ingot')" }
 * // Overclock upgrade count via Upgrades class
 * { "when": "Upgrades.count('overclock') >= 2" }
 * }</pre>
 *
 * <p>Build via {@link #builder()} or one of the convenience factories:
 * {@link #forMachine forMachine(…)}, {@link #forMotor forMotor(…)},
 * {@link #forMultiblock forMultiblock(…)}.</p>
 */
public final class PolyContext {

    private final Map<String, PolyValue> vars;
    private final Map<String, PolyClass> classes;

    private PolyContext(Map<String, PolyValue> vars, Map<String, PolyClass> classes) {
        this.vars    = vars;
        this.classes = classes;
    }

    /** Look up a simple variable by name.  Returns {@link PolyValue#NULL} if absent. */
    public PolyValue get(String name) {
        return vars.getOrDefault(name, PolyValue.NULL);
    }

    /** Look up a class object by name.  Returns {@code null} if absent. */
    public PolyClass getClass(String name) {
        return classes.get(name);
    }

    // Package-private accessors used by PolyScript to copy vars/classes into a new builder.
    Map<String, PolyValue> vars()    { return vars; }
    Map<String, PolyClass> classes() { return classes; }

    // ---- Builder -----------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final LinkedHashMap<String, PolyValue> vars    = new LinkedHashMap<>();
        private final LinkedHashMap<String, PolyClass> classes = new LinkedHashMap<>();
        private Map<String, Integer> upgrades = null;
        private org.bukkit.inventory.Inventory upgradeInventory = null;
        /** Tank name → [level, capacity] for fluid tanks. */
        private Map<String, double[]> fluidTanks = Map.of();
        /** Tank name → [level, capacity] for gas tanks. */
        private Map<String, double[]> gasTanks   = Map.of();

        /**
         * Copy all variables and classes from an existing {@link PolyContext} into
         * this builder.  Existing builder entries are overwritten by the copied values.
         * Commonly used by {@link PolyScript} to seed a new context from the original
         * machine context before layering script-computed values on top.
         */
        public Builder copyFrom(PolyContext other) {
            vars.putAll(other.vars());
            classes.putAll(other.classes());
            return this;
        }

        public Builder num(String name, double v)                         { vars.put(name, PolyValue.of(v));      return this; }
        public Builder bool(String name, boolean v)                       { vars.put(name, PolyValue.of(v));      return this; }
        public Builder str(String name, String v)                         { vars.put(name, PolyValue.of(v));      return this; }
        public Builder item(String name, org.bukkit.inventory.ItemStack s){ vars.put(name, PolyValue.ofItem(s)); return this; }
        public Builder val(String name, PolyValue v)                      { vars.put(name, v);                   return this; }
        public Builder cls(String name, PolyClass c)                      { classes.put(name, c);                return this; }

        /**
         * Register a workbench context exposing inventory slot access.
         * Adds a {@code "Workbench"} class supporting {@code Workbench.input(n)},
         * {@code Workbench.output(n)}, {@code Workbench.tool(n)} and the shorthand
         * builtins {@code input(n)}, {@code output(n)}, {@code tool(n)}.
         */
        public Builder workbench(dev.arubik.craftengine.crafting.WorkbenchDefinition def,
                org.bukkit.inventory.Inventory inv) {
            if (def != null) classes.put("Workbench", new WorkbenchClass(def, inv));
            return this;
        }

        /**
         * Register machine block position and facing as a {@code Machine} class.
         *
         * <p>After this call, expressions can use {@code Machine.facing},
         * {@code Machine.x}, {@code player_facing("north")},
         * {@code Machine.player_in_range(8)}, etc.</p>
         *
         * <p>Also injects flat convenience vars {@code facing} and {@code facing_angle}
         * so expressions can compare {@code facing == "north"} without the class prefix.</p>
         *
         * @param x          block centre X (pos.getX() + 0.5)
         * @param y          block centre Y
         * @param z          block centre Z
         * @param facing     direction name: "north"|"south"|"east"|"west"|"up"|"down"
         * @param facingYaw  yaw in degrees: south=0, west=90, north=180, east=270
         * @param world      the Bukkit world the machine lives in
         */
        public Builder machinePos(double x, double y, double z,
                                  String facing, float facingYaw,
                                  org.bukkit.World world) {
            classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, world));
            vars.putIfAbsent("facing",       PolyValue.of(facing != null ? facing : "north"));
            vars.putIfAbsent("facing_angle", PolyValue.of(facingYaw));
            return this;
        }

        /**
         * Single-player variant of {@link #machinePos}. All player queries in the
         * registered {@code Machine} class ({@code player_facing}, {@code player_in_range},
         * {@code player_above}, {@code player_below}) check only {@code player} instead
         * of iterating the whole world. Used by
         * {@link dev.arubik.craftengine.machine.render.RendererManager} for per-viewer
         * condition evaluation.
         *
         * @param player the single player to test; null produces a no-match class
         */
        /** Register MultiBlock context — exposes rel_x/y/z, is_master, part_count, formed. */
        public Builder multiBlock(int relX, int relY, int relZ, boolean isMaster, int partCount, boolean formed) {
            classes.put("MultiBlock", new MultiBlockClass(relX, relY, relZ, isMaster, partCount, formed));
            vars.putIfAbsent("part_count", PolyValue.of(partCount));
            vars.putIfAbsent("formed",     PolyValue.of(formed));
            return this;
        }

        public Builder machinePosForPlayer(double x, double y, double z,
                                            String facing, float facingYaw,
                                            org.bukkit.entity.Player player) {
            classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, player));
            vars.putIfAbsent("facing",       PolyValue.of(facing != null ? facing : "north"));
            vars.putIfAbsent("facing_angle", PolyValue.of(facingYaw));
            return this;
        }

        /**
         * Register a player context.
         * After this call, expressions can use {@code Player.name}, {@code Player.health},
         * {@code Player.is_sneaking}, etc.
         *
         * @param p the NMS server player; null is silently ignored
         */
        public Builder player(net.minecraft.server.level.ServerPlayer p) {
            if (p != null) classes.put("Player", new PlayerClass(p));
            return this;
        }

        /**
         * Register a world context.
         * After this call, expressions can use {@code World.time}, {@code World.is_day},
         * {@code World.biome(x,y,z)}, etc.
         *
         * <p>If {@code w} is a contraption sub-level, {@link WorldClass#forLevel} resolves it
         * to the real parent level automatically.</p>
         *
         * @param w the NMS server level; null is silently ignored
         */
        public Builder world(net.minecraft.server.level.ServerLevel w) {
            if (w != null) classes.put("World", WorldClass.forLevel(w));
            return this;
        }

        /**
         * Register a location context for the given level + coordinates.
         *
         * <p>After this call, expressions can use {@code Location.x}, {@code Location.y},
         * {@code Location.z}, {@code Location.biome}, {@code Location.distance(x,y,z)}, etc.</p>
         *
         * <p>If {@code level} is a contraption sub-level, coordinates are automatically projected
         * to real-world space via
         * {@link LocationClass#forLevel(net.minecraft.server.level.ServerLevel, double, double, double)}.
         * Expressions always see the real block location — never contraption-local coords.</p>
         *
         * @param level the level the coordinates belong to (may be a contraption sub-level)
         * @param x     X in that level's space
         * @param y     Y
         * @param z     Z
         */
        public Builder location(net.minecraft.server.level.ServerLevel level,
                                double x, double y, double z) {
            if (level != null) classes.put("Location", LocationClass.forLevel(level, x, y, z));
            return this;
        }

        /**
         * Register upgrade counts by type name.
         * Automatically exposes:
         * <ul>
         *   <li>{@code upgrade_count} — total across all types</li>
         *   <li>{@code upgrade_{type}} — count for each individual type key</li>
         *   <li>{@code overclock_count} — alias for the {@code "overclock"} type entry</li>
         *   <li>{@code upgrade_efficiency} — alias for the {@code "efficiency"} type entry</li>
         *   <li>An {@code Upgrades} class for {@code Upgrades.count("type")} queries</li>
         * </ul>
         */
        public Builder upgradeMap(Map<String, Integer> upgradesByType) {
            this.upgrades = upgradesByType;
            return this;
        }

        /**
         * Attach the physical inventory that holds upgrade items.
         * When set, {@code Upgrades.inventory} returns an {@link PolyValue.ItemList}
         * of the upgrade container's non-empty contents, and {@code Upgrades.slot(n)}
         * returns the item at slot n.
         */
        public Builder upgradeInventory(org.bukkit.inventory.Inventory inv) {
            this.upgradeInventory = inv;
            return this;
        }

        /**
         * Register fluid tank data.
         * Each entry maps a tank name to a two-element array {@code [level, capacity]}.
         * When non-empty this automatically:
         * <ul>
         *   <li>Registers a {@code FluidTanks} class for formula access</li>
         *   <li>Injects flat vars {@code fluid_level}, {@code fluid_capacity},
         *       {@code fluid_fraction} for the first tank</li>
         * </ul>
         */
        public Builder fluidTanks(Map<String, double[]> tanks) {
            this.fluidTanks = tanks != null ? tanks : Map.of();
            return this;
        }

        /**
         * Register gas tank data.
         * Each entry maps a tank name to a two-element array {@code [level, capacity]}.
         * When non-empty this automatically:
         * <ul>
         *   <li>Registers a {@code GasTanks} class for formula access</li>
         *   <li>Injects flat vars {@code gas_level}, {@code gas_capacity},
         *       {@code gas_fraction} for the first tank</li>
         * </ul>
         */
        public Builder gasTanks(Map<String, double[]> tanks) {
            this.gasTanks = tanks != null ? tanks : Map.of();
            return this;
        }

        /**
         * Register redstone power level variables.
         *
         * <p>After this call, expressions can use:
         * <ul>
         *   <li>{@code redstone}       — signal strength 0..15</li>
         *   <li>{@code powered}        — true when signal &gt; 0</li>
         *   <li>{@code redstone_power} — alias for {@code redstone}</li>
         * </ul>
         *
         * @param power redstone signal strength (0-15)
         */
        public Builder redstone(int power) {
            vars.put("redstone", PolyValue.of(power));
            vars.put("powered", PolyValue.of(power > 0));
            vars.put("redstone_power", PolyValue.of(power));
            return this;
        }

        /**
         * Register a block context exposing block entity data and block state properties.
         *
         * <p>After this call, expressions can use {@code Block.has_entity},
         * {@code Block.slot(n)}, {@code Block.property("name")}, {@code Block.nbt("key")},
         * etc.</p>
         *
         * @param level the server level
         * @param pos   the block position
         */
        public Builder block(net.minecraft.server.level.ServerLevel level,
                             net.minecraft.core.BlockPos pos) {
            if (level != null && pos != null) {
                classes.put("Block", BlockClass.of(level, pos));
            }
            return this;
        }

        /**
         * Register a contraption context. Auto-detects whether the given level is a
         * {@link dev.arubik.craftengine.contraption.core.ContraptionLevel}.
         *
         * <p>After this call, expressions can use {@code Contraption.is_contraption},
         * {@code Contraption.yaw}, {@code Contraption.speed}, etc. When not in a
         * contraption, {@code Contraption.is_contraption} returns NULL (falsy).</p>
         *
         * @param level the level object; if it's a ContraptionLevel, properties are live
         */
        public Builder contraption(Object level) {
            classes.put("Contraption", ContraptionClass.of(level));
            return this;
        }

        public PolyContext build() {
            // Bake fluid-tank data into a FluidTanks class + convenient flat vars
            if (!fluidTanks.isEmpty()) {
                classes.put("FluidTanks", new FluidTanksClass(fluidTanks));
                double[] first = fluidTanks.values().iterator().next();
                vars.putIfAbsent("fluid_level",    PolyValue.of(first[0]));
                vars.putIfAbsent("fluid_capacity", PolyValue.of(first[1]));
                vars.putIfAbsent("fluid_fraction",
                        PolyValue.of(first[1] > 0 ? first[0] / first[1] : 0));
            }
            // Bake gas-tank data into a GasTanks class + convenient flat vars
            if (!gasTanks.isEmpty()) {
                classes.put("GasTanks", new GasTanksClass(gasTanks));
                double[] first = gasTanks.values().iterator().next();
                vars.putIfAbsent("gas_level",    PolyValue.of(first[0]));
                vars.putIfAbsent("gas_capacity", PolyValue.of(first[1]));
                vars.putIfAbsent("gas_fraction",
                        PolyValue.of(first[1] > 0 ? first[0] / first[1] : 0));
            }
            // Bake upgrade map into vars + classes
            if (upgrades != null && !upgrades.isEmpty()) {
                int total = upgrades.values().stream().mapToInt(i -> i != null ? i : 0).sum();
                vars.put("upgrade_count", PolyValue.of(total));
                upgrades.forEach((type, count) -> {
                    vars.put("upgrade_" + type, PolyValue.of(count != null ? count : 0));
                });
                // Convenient aliases
                vars.putIfAbsent("overclock_count",
                        PolyValue.of(upgrades.getOrDefault("overclock", 0)));
                vars.putIfAbsent("upgrade_efficiency",
                        PolyValue.of(upgrades.getOrDefault("efficiency", 0)));
                classes.put("Upgrades", new UpgradesClass(upgrades, upgradeInventory));
            } else {
                vars.putIfAbsent("upgrade_count",     PolyValue.of(0));
                vars.putIfAbsent("overclock_count",   PolyValue.of(0));
                vars.putIfAbsent("upgrade_efficiency", PolyValue.of(0));
                if (upgradeInventory != null) {
                    // No type map but we still have an inventory — expose it
                    classes.put("Upgrades", new UpgradesClass(Map.of(), upgradeInventory));
                }
            }
            return new PolyContext(Map.copyOf(vars), Map.copyOf(classes));
        }
    }

    // ---- Convenience factories ---------------------------------------------

    /**
     * Full machine context with inventory and optional upgrade map.
     *
     * <p>Standard numeric variables: {@code rpm}, {@code overclock},
     * {@code efficiency}, {@code progress}, {@code max_progress}, {@code tier}.<br>
     * Standard boolean variables: {@code processing}, {@code powered},
     * {@code overclocked}, {@code has_fuel}.<br>
     * Upgrade variables and {@code Upgrades} class when {@code upgradesByType}
     * is non-null/non-empty.<br>
     * {@code Inventory} class when {@code inventory} is non-null.</p>
     */
    public static PolyContext forMachine(
            double rpm, double overclock, double efficiency,
            double progress, double maxProgress, double tier,
            boolean processing, boolean powered, boolean overclocked, boolean hasFuel,
            org.bukkit.inventory.Inventory inventory,
            Map<String, Integer> upgradesByType) {

        Builder b = builder()
                .num("rpm",          rpm)
                .num("overclock",    overclock)
                .num("efficiency",   efficiency)
                .num("progress",     progress)
                .num("max_progress", maxProgress)
                .num("tier",         tier)
                .bool("processing",  processing)
                .bool("powered",     powered)
                .bool("overclocked", overclocked)
                .bool("has_fuel",    hasFuel);

        if (inventory != null) b.cls("Inventory", new InventoryClass(inventory));
        if (upgradesByType != null && !upgradesByType.isEmpty()) b.upgradeMap(upgradesByType);
        return b.build();
    }

    /**
     * Machine context without upgrade information (backward-compatible overload).
     *
     * @see #forMachine(double, double, double, double, double, double,
     *                  boolean, boolean, boolean, boolean,
     *                  org.bukkit.inventory.Inventory, Map)
     */
    public static PolyContext forMachine(
            double rpm, double overclock, double efficiency,
            double progress, double maxProgress, double tier,
            boolean processing, boolean powered, boolean overclocked, boolean hasFuel,
            org.bukkit.inventory.Inventory inventory) {
        return forMachine(rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel, inventory, null);
    }

    /**
     * Full machine context with inventory, upgrade map, and tank data.
     *
     * <p>Exposes all standard machine variables plus:
     * <ul>
     *   <li>A {@code FluidTanks} class and {@code fluid_level/capacity/fraction}
     *       flat vars when {@code fluidTanks} is non-empty.</li>
     *   <li>A {@code GasTanks} class and {@code gas_level/capacity/fraction}
     *       flat vars when {@code gasTanks} is non-empty.</li>
     * </ul>
     *
     * @param fluidTanks map of fluid-tank name → {@code [level, capacity]};
     *                   pass {@code null} or empty to skip
     * @param gasTanks   map of gas-tank name → {@code [level, capacity]};
     *                   pass {@code null} or empty to skip
     */
    public static PolyContext forMachine(
            double rpm, double overclock, double efficiency,
            double progress, double maxProgress, double tier,
            boolean processing, boolean powered, boolean overclocked, boolean hasFuel,
            org.bukkit.inventory.Inventory inventory,
            Map<String, Integer> upgradesByType,
            Map<String, double[]> fluidTanks,
            Map<String, double[]> gasTanks) {

        Builder b = builder()
                .num("rpm",          rpm)
                .num("overclock",    overclock)
                .num("efficiency",   efficiency)
                .num("progress",     progress)
                .num("max_progress", maxProgress)
                .num("tier",         tier)
                .bool("processing",  processing)
                .bool("powered",     powered)
                .bool("overclocked", overclocked)
                .bool("has_fuel",    hasFuel)
                .fluidTanks(fluidTanks)
                .gasTanks(gasTanks);

        if (inventory != null) b.cls("Inventory", new InventoryClass(inventory));
        if (upgradesByType != null && !upgradesByType.isEmpty()) b.upgradeMap(upgradesByType);
        return b.build();
    }

    /**
     * Context for a rotational motor/generator.
     *
     * <p>Variables: {@code rpm}, {@code torque} (stress units), {@code overclock},
     * {@code efficiency}, {@code overstressed} (bool), {@code has_fuel} (bool).</p>
     */
    public static PolyContext forMotor(
            double rpm, double torque, double overclock, double efficiency,
            boolean overstressed, boolean hasFuel) {
        return builder()
                .num("rpm",         rpm)
                .num("torque",      torque)
                .num("su",          torque)   // alias
                .num("overclock",   overclock)
                .num("efficiency",  efficiency)
                .bool("overstressed", overstressed)
                .bool("has_fuel",   hasFuel)
                .build();
    }

    /**
     * Context for a multiblock structure.
     *
     * <p>Includes all standard machine variables plus:
     * {@code part_count} (number of blocks in the structure) and
     * {@code formed} (boolean: is the structure fully formed).</p>
     */
    public static PolyContext forMultiblock(
            double rpm, double overclock, double efficiency,
            double progress, double maxProgress, double tier,
            boolean processing, boolean powered, boolean overclocked, boolean hasFuel,
            org.bukkit.inventory.Inventory inventory,
            Map<String, Integer> upgradesByType,
            int partCount, boolean formed) {

        Builder b = builder()
                .num("rpm",          rpm)
                .num("overclock",    overclock)
                .num("efficiency",   efficiency)
                .num("progress",     progress)
                .num("max_progress", maxProgress)
                .num("tier",         tier)
                .bool("processing",  processing)
                .bool("powered",     powered)
                .bool("overclocked", overclocked)
                .bool("has_fuel",    hasFuel)
                .num("part_count",   partCount)
                .bool("formed",      formed);

        if (inventory != null) b.cls("Inventory", new InventoryClass(inventory));
        if (upgradesByType != null && !upgradesByType.isEmpty()) b.upgradeMap(upgradesByType);
        return b.build();
    }
}
