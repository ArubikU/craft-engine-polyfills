package dev.arubik.craftengine.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.VanillaData;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code multiblocks/*.json} into {@link MultiBlockDefinition#REGISTRY}.
 *
 * <p>
 * The shape is written as ASCII layers with a key legend, which reads like the
 * structure it describes — bottom layer first:
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:industrial_smelter",
 *   "keys": {
 *     "I": { "blocks": "minecraft:iron_block" },
 *     "M": { "custom_block": "cml:refinery_mixer" },
 *     "#": { "blocks": "#minecraft:logs" },
 *     ".": "any"
 *   },
 *   "modes": [
 *     {
 *       "name": "smelter",
 *       "core": [1, 1, 1],
 *       "layers": [
 *         ["III", "III", "III"],
 *         ["III", "I.I", "III"],
 *         ["III", "III", "III"]
 *       ],
 *       "part_block_id": "cml:smelter_part",
 *       "machine": { ...the machines/*.json body: slots, tanks, bars, buttons... },
 *       "io": {
 *         "default": "open",
 *         "rules": [
 *           { "y_above": 0, "input":  { "types": ["item"], "faces": "all_but_down" } },
 *           { "y": 0,       "input":  { "types": ["item"], "faces": "horizontal" } },
 *           { "y_below": 0, "output": { "types": ["item"], "faces": "down" } }
 *         ]
 *       }
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <p>
 * Within {@code layers}, the outer index is Y (bottom up), the row index is Z and
 * the character index is X. {@code core} names the cell holding the core block;
 * every other coordinate is interpreted relative to it. A key mapped to
 * {@code "any"} is a don't-care cell (typically the hollow interior); every other
 * key is a vanilla {@code BlockPredicate}, so state properties and NBT are
 * matchable, not just block identity.
 */
public final class MultiBlockLoader {

    private MultiBlockLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("multiblocks", Registries.PHASE_DEFINITIONS, MultiBlockLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("multiblocks", MultiBlockLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + MultiBlockDefinition.REGISTRY.size() + " multiblock definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));

        // A null value marks a don't-care cell; BlockPredicate has no "match anything"
        // form, so absence is how the interior of a hollow box is expressed.
        Map<Character, MultiBlockSchema.PartMatcher> keys = parseKeys(view);

        List<MultiBlockDefinition.Mode> modes = new ArrayList<>();
        for (JsonView modeView : view.objectList("modes"))
            modes.add(parseMode(modeView, keys, id));
        if (modes.isEmpty())
            throw view.error("needs at least one entry in 'modes'");

        MultiBlockDefinition.REGISTRY.register(id, new MultiBlockDefinition(id, modes));
    }

    /**
     * Parses the {@code keys} legend of a structure body.
     *
     * <p>
     * Exposed so {@code multiblock_machines/*.json} can describe its shape with the
     * same grammar rather than inventing a second one.
     */
    public static Map<Character, MultiBlockSchema.PartMatcher> parseKeys(JsonView view) {
        Map<Character, MultiBlockSchema.PartMatcher> keys = new HashMap<>();
        for (var entry : view.object("keys").raw().entrySet()) {
            if (entry.getKey().length() != 1)
                throw view.error("key '" + entry.getKey() + "' must be exactly one character");
            char symbol = entry.getKey().charAt(0);
            var value = entry.getValue();
            String where = view.path() + " > keys > " + entry.getKey();

            if (value.isJsonPrimitive() && isAny(value.getAsString())) {
                keys.put(symbol, null); // don't-care cell
                continue;
            }
            // A CraftEngine custom block is not in the vanilla block registry, so
            // BlockPredicate cannot name one. `custom_block` matches by CE block id
            // instead, which is what a structure made of this plugin's own blocks needs.
            if (value.isJsonObject() && value.getAsJsonObject().has("custom_block")) {
                Key blockId = Key.of(value.getAsJsonObject().get("custom_block").getAsString());
                keys.put(symbol, (level, pos) -> {
                    try {
                        var custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                                .getOptionalCustomBlockState(level.getBlockState(pos));
                        return custom.isPresent() && custom.get().owner() != null
                                && blockId.equals(custom.get().owner().value().id());
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
                continue;
            }
            BlockPredicate predicate = VanillaData.parse(BlockPredicate.CODEC, value, where);
            keys.put(symbol, (level, pos) -> level instanceof net.minecraft.server.level.ServerLevel serverLevel
                    && predicate.matches(serverLevel, pos));
        }
        return keys;
    }

    /** Parses one shape + its per-cell I/O. Shared with the multiblock-machine loader. */
    public static MultiBlockDefinition.Mode parseMode(JsonView view,
            Map<Character, MultiBlockSchema.PartMatcher> keys, Key ownerId) {
        List<Integer> core = view.intList("core");
        if (core.size() != 3)
            throw view.error("'core' must be [x, y, z]");
        BlockPos coreOffset = new BlockPos(core.get(0), core.get(1), core.get(2));
        MultiBlockSchema schema = new MultiBlockSchema(coreOffset);

        var rawLayers = view.raw().get("layers");
        if (rawLayers == null || !rawLayers.isJsonArray())
            throw view.error("'layers' must be an array of layers");
        int y = 0;
        for (var layerElement : rawLayers.getAsJsonArray()) {
            if (!layerElement.isJsonArray())
                throw view.error("layer " + y + " must be an array of rows");
            int z = 0;
            for (var rowElement : layerElement.getAsJsonArray()) {
                String row = rowElement.getAsString();
                for (int x = 0; x < row.length(); x++) {
                    char symbol = row.charAt(x);
                    if (!keys.containsKey(symbol))
                        throw view.error("layer " + y + " row " + z + " uses key '" + symbol
                                + "' which is not in 'keys'");
                    MultiBlockSchema.PartMatcher matcher = keys.get(symbol);
                    if (matcher == null)
                        continue; // don't-care cell
                    schema.addPart(x, y, z, matcher);
                }
                z++;
            }
            y++;
        }

        MultiBlockDefinition.IOSpec io = view.has("io") ? parseIO(view.object("io")) : null;
        String name = view.string("name", "default");
        // A mode may carry the machine the assembled structure becomes. Its id is the
        // multiblock's id plus the mode name, so two modes of one core do not collide.
        dev.arubik.craftengine.machine.MachineDefinition machine = null;
        if (view.has("machine")) {
            Key machineId = Key.of("polyfills", ownerId.value() + "_" + name);
            machine = dev.arubik.craftengine.machine.MachineDefinitionLoader
                    .parse(view.object("machine"), machineId);
            dev.arubik.craftengine.machine.MachineDefinition.REGISTRY.register(machineId, machine);
        }
        return new MultiBlockDefinition.Mode(name, schema, io, machine,
                view.string("part_block_id", "craftengine:multiblock_part"));
    }

    private static MultiBlockDefinition.IOSpec parseIO(JsonView view) {
        boolean defaultOpen = !"closed".equalsIgnoreCase(view.string("default", "open"));
        List<MultiBlockDefinition.IOSpec.Rule> rules = new ArrayList<>();
        for (JsonView ruleView : view.objectList("rules")) {
            BlockPos at = null;
            if (ruleView.has("at")) {
                List<Integer> cell = ruleView.intList("at");
                if (cell.size() != 3)
                    throw ruleView.error("'at' must be [x, y, z]");
                at = new BlockPos(cell.get(0), cell.get(1), cell.get(2));
            }
            rules.add(new MultiBlockDefinition.IOSpec.Rule(
                    at,
                    ruleView.has("y") ? ruleView.integer("y") : null,
                    ruleView.has("y_above") ? ruleView.integer("y_above") : null,
                    ruleView.has("y_below") ? ruleView.integer("y_below") : null,
                    parseGrants(ruleView, "input"),
                    parseGrants(ruleView, "output"),
                    ruleView.bool("closed", false)));
        }
        return new MultiBlockDefinition.IOSpec(rules, defaultOpen);
    }

    /** Accepts a single grant object or an array of them. */
    private static List<MultiBlockDefinition.IOSpec.Grant> parseGrants(JsonView view, String field) {
        if (!view.has(field))
            return List.of();
        var raw = view.raw().get(field);
        List<JsonView> entries = new ArrayList<>();
        if (raw.isJsonArray())
            entries.addAll(view.objectList(field));
        else
            entries.add(view.object(field));

        List<MultiBlockDefinition.IOSpec.Grant> grants = new ArrayList<>();
        for (JsonView entry : entries) {
            List<IOConfiguration.IOType> types = new ArrayList<>();
            for (String typeName : entry.stringList("types")) {
                IOConfiguration.IOType type = null;
                for (IOConfiguration.IOType candidate : IOConfiguration.IOType.values())
                    if (candidate.name().equalsIgnoreCase(typeName))
                        type = candidate;
                if (type == null)
                    throw entry.error("unknown io type '" + typeName + "'");
                types.add(type);
            }
            List<net.minecraft.core.Direction> faces = new ArrayList<>();
            for (String faceName : entry.stringList("faces")) {
                List<net.minecraft.core.Direction> group = MultiBlockDefinition.IOSpec.faceGroup(faceName);
                if (group == null)
                    throw entry.error("unknown face or face group '" + faceName + "'");
                faces.addAll(group);
            }
            if (faces.isEmpty())
                faces.addAll(List.of(net.minecraft.core.Direction.values()));
            grants.add(new MultiBlockDefinition.IOSpec.Grant(types, faces));
        }
        return grants;
    }

    private static boolean isAny(String value) {
        return value.equalsIgnoreCase("any") || value.equals("*");
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
