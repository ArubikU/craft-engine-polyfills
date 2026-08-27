package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.minecraft.core.BlockPos;
import net.momirealms.craftengine.core.util.Key;

/**
 * A data-defined multiblock: the shape its shell must match, plus which faces of
 * which cells accept or emit what.
 *
 * <p>
 * Schemas used to be built with Java builder calls whose cells were lambdas
 * ({@code state -> state.is(Blocks.IRON_BLOCK)}), and the matching I/O layout was
 * an anonymous {@code IOConfigurationProvider} beside it. Neither could be
 * expressed in a file, so every structure needed a class. Both halves now come
 * from {@code multiblocks/*.json}, with cells matched by vanilla
 * {@code BlockPredicate} — which also buys state-property and NBT matching that
 * the old {@code Predicate<BlockState>} lambdas did not have.
 *
 * <p>
 * Rebuilt on reload: a definition is a description, not an identity, and
 * behaviors re-read it when CraftEngine reconstructs them.
 */
public final class MultiBlockDefinition {

    /** Every data-defined multiblock. */
    public static final Registry<MultiBlockDefinition> REGISTRY = Registries.create("multiblock");

    /**
     * One alternative shape a core may form into.
     *
     * <p>
     * A list, because a single core block can legitimately assemble into different
     * machines depending on what was built around it — the copper shell makes a
     * chest, the iron shell makes a smelter. Modes are tried in order, so put the
     * most specific first.
     *
     * @param name    written to the core's {@code machine_type} blockstate, when it
     *                has one
     * @param schema  the shell this mode requires
     * @param io      per-cell I/O, or {@code null} to leave every face open
     * @param machine what the assembled structure DOES — slots, tanks, menu, buttons.
     *                A shape on its own is inert, so the machine lives inside the mode
     *                rather than in a file beside it: one core block can assemble into
     *                genuinely different machines depending on the shell built around
     *                it, and each of those needs its own menu
     * @param partBlockId the block a matched cell becomes while this mode is formed
     */
    /**
     * @param canForm optional script expression that must be truthy for the structure to assemble,
     *                evaluated with {@code World} bound to a {@link FormConditionClass} at the core.
     *                Null means no extra condition.
     */
    public record Mode(String name, MultiBlockSchema schema, IOSpec io,
            dev.arubik.craftengine.machine.MachineDefinition machine, String partBlockId,
            String canForm, String onFormScript, String onDisassembleScript) {

        public Mode(String name, MultiBlockSchema schema, IOSpec io,
                dev.arubik.craftengine.machine.MachineDefinition machine, String partBlockId) {
            this(name, schema, io, machine, partBlockId, null, null, null);
        }

        public Mode(String name, MultiBlockSchema schema, IOSpec io,
                dev.arubik.craftengine.machine.MachineDefinition machine, String partBlockId, String canForm) {
            this(name, schema, io, machine, partBlockId, canForm, null, null);
        }
    }

    private final Key id;
    private final List<Mode> modes;

    public MultiBlockDefinition(Key id, List<Mode> modes) {
        this.id = id;
        this.modes = List.copyOf(modes);
    }

    public Key id() {
        return id;
    }

    public List<Mode> modes() {
        return modes;
    }

    /** The first mode, which is what a single-shape multiblock has. */
    public Mode primary() {
        return modes.isEmpty() ? null : modes.get(0);
    }

    public static MultiBlockDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }

    @Override
    public String toString() {
        return id + "(" + modes.size() + " mode(s))";
    }

    // ------------------------------------------------------------------- I/O

    /**
     * Data-driven {@link IOConfigurationProvider}: an ordered list of rules, each
     * selecting cells by position and granting them input/output types on chosen
     * faces. The first matching rule wins, so a general rule can sit at the bottom
     * as a default.
     */
    public static final class IOSpec implements IOConfigurationProvider {

        /**
         * One rule.
         *
         * @param at       exact relative cell this applies to, or {@code null} for any
         * @param y        exact relative layer, or {@code null} for any
         * @param yAbove   applies when the cell's y is strictly above this
         * @param yBelow   applies when the cell's y is strictly below this
         * @param inputs   types accepted, per face group
         * @param outputs  types emitted, per face group
         * @param closed   when true the cell rejects everything, ignoring the rest
         */
        public record Rule(BlockPos at, Integer y, Integer yAbove, Integer yBelow,
                List<Grant> inputs, List<Grant> outputs, boolean closed) {

            boolean matches(BlockPos relative) {
                if (at != null && !at.equals(relative))
                    return false;
                if (y != null && relative.getY() != y)
                    return false;
                if (yAbove != null && relative.getY() <= yAbove)
                    return false;
                if (yBelow != null && relative.getY() >= yBelow)
                    return false;
                return true;
            }
        }

        /** A set of resource types granted on a set of faces. */
        public record Grant(List<IOConfiguration.IOType> types, List<net.minecraft.core.Direction> faces) {
        }

        private final List<Rule> rules;
        private final boolean defaultOpen;

        public IOSpec(List<Rule> rules, boolean defaultOpen) {
            this.rules = List.copyOf(rules);
            this.defaultOpen = defaultOpen;
        }

        public List<Rule> rules() {
            return rules;
        }

        /**
         * Parses the same {@code {default, rules: [...]}} shape {@code MultiBlockLoader} already
         * reads for assembled multiblocks — shared so a {@code CelledMachineDefinition}'s {@code
         * cell_io} block (auto-placing multi-cell machines) uses the identical rule language instead
         * of a second one, keyed by cell offset instead of assembled-shell position either way.
         */
        public static IOSpec parse(dev.arubik.craftengine.data.JsonView view) {
            boolean defaultOpen = !"closed".equalsIgnoreCase(view.string("default", "open"));
            List<Rule> rules = new ArrayList<>();
            for (dev.arubik.craftengine.data.JsonView ruleView : view.objectList("rules")) {
                BlockPos at = null;
                if (ruleView.has("at")) {
                    List<Integer> cell = ruleView.intList("at");
                    if (cell.size() != 3)
                        throw ruleView.error("'at' must be [x, y, z]");
                    at = new BlockPos(cell.get(0), cell.get(1), cell.get(2));
                }
                rules.add(new Rule(
                        at,
                        ruleView.has("y") ? ruleView.integer("y") : null,
                        ruleView.has("y_above") ? ruleView.integer("y_above") : null,
                        ruleView.has("y_below") ? ruleView.integer("y_below") : null,
                        parseGrants(ruleView, "input"),
                        parseGrants(ruleView, "output"),
                        ruleView.bool("closed", false)));
            }
            return new IOSpec(rules, defaultOpen);
        }

        /** Accepts a single grant object or an array of them. */
        private static List<Grant> parseGrants(dev.arubik.craftengine.data.JsonView view, String field) {
            if (!view.has(field))
                return List.of();
            var raw = view.raw().get(field);
            List<dev.arubik.craftengine.data.JsonView> entries = new ArrayList<>();
            if (raw.isJsonArray())
                entries.addAll(view.objectList(field));
            else
                entries.add(view.object(field));

            List<Grant> grants = new ArrayList<>();
            for (dev.arubik.craftengine.data.JsonView entry : entries) {
                List<IOConfiguration.IOType> types = new ArrayList<>();
                for (String typeName : entry.stringList("types")) {
                    // Multiblocks have no kinetic-network RPM system today (unlike single-block
                    // machines — see MachineDefinitionLoader#parseUnifiedRpmIo) — skip a "rpm" entry
                    // by name rather than rejecting it outright, so the shared io.input/output JSON
                    // shape stays forward-compatible if/when multiblock RPM support is added.
                    if ("rpm".equalsIgnoreCase(typeName)) continue;
                    IOConfiguration.IOType type = null;
                    for (IOConfiguration.IOType candidate : IOConfiguration.IOType.values())
                        if (candidate.name().equalsIgnoreCase(typeName))
                            type = candidate;
                    if (type == null)
                        throw entry.error("unknown io type '" + typeName + "'");
                    types.add(type);
                }
                if (types.isEmpty()) continue;
                List<net.minecraft.core.Direction> faces = new ArrayList<>();
                for (String faceName : entry.stringList("faces")) {
                    List<net.minecraft.core.Direction> group = faceGroup(faceName);
                    if (group == null)
                        throw entry.error("unknown face or face group '" + faceName + "'");
                    faces.addAll(group);
                }
                if (faces.isEmpty())
                    faces.addAll(List.of(net.minecraft.core.Direction.values()));
                grants.add(new Grant(types, faces));
            }
            return grants;
        }

        @Override
        public IOConfiguration configurePartIO(BlockPos relativePos) {
            for (Rule rule : rules) {
                if (!rule.matches(relativePos))
                    continue;
                if (rule.closed())
                    return new IOConfiguration.Closed();
                IOConfiguration.Simple config = new IOConfiguration.Simple();
                for (Grant grant : rule.inputs())
                    for (IOConfiguration.IOType type : grant.types())
                        for (net.minecraft.core.Direction face : grant.faces())
                            config.addInput(type, face);
                for (Grant grant : rule.outputs())
                    for (IOConfiguration.IOType type : grant.types())
                        for (net.minecraft.core.Direction face : grant.faces())
                            config.addOutput(type, face);
                return config;
            }
            return defaultOpen ? new IOConfiguration.Open() : new IOConfiguration.Closed();
        }

        /** Face-group names a data file may use instead of listing six directions. */
        public static List<net.minecraft.core.Direction> faceGroup(String name) {
            List<net.minecraft.core.Direction> out = new ArrayList<>();
            switch (name.toLowerCase(java.util.Locale.ROOT)) {
                case "all" -> out.addAll(List.of(net.minecraft.core.Direction.values()));
                case "horizontal" -> out.addAll(List.of(net.minecraft.core.Direction.NORTH,
                        net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.EAST,
                        net.minecraft.core.Direction.WEST));
                case "vertical" -> out.addAll(List.of(net.minecraft.core.Direction.UP,
                        net.minecraft.core.Direction.DOWN));
                case "all_but_down" -> {
                    for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
                        if (d != net.minecraft.core.Direction.DOWN)
                            out.add(d);
                }
                case "all_but_up" -> {
                    for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
                        if (d != net.minecraft.core.Direction.UP)
                            out.add(d);
                }
                default -> {
                    net.minecraft.core.Direction single = net.minecraft.core.Direction
                            .byName(name.toLowerCase(java.util.Locale.ROOT));
                    if (single == null)
                        return null;
                    out.add(single);
                }
            }
            return out;
        }
    }
}
