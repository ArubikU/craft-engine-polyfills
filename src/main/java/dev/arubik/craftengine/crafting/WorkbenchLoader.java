package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code workbenches/*.json} into {@link WorkbenchDefinition#REGISTRY}.
 *
 * <p>
 * A workbench file declares only its UI and its footprint; recipes are separate
 * files that name this id.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:forge",
 *   "title": "<gold>Forge",
 *   "size": 54,                     // container slots, must be a multiple of 9
 *   "grid": { "width": 3, "height": 2 },
 *   "slots": {
 *     "input":  [21, 22, 23, 30, 31, 32],   // in reading order, mapped onto the grid
 *     "output": [25],
 *     "tool":   [19],
 *     "custom": [10],
 *     "render": [ { "slot": 19, "position": [4, 13.5, 4], "scale": 0.5 } ]
 *   },
 *   "structure": { "type": "horizontal_double" }
 *   // or  "structure": { "type": "multiblock", "multiblock": "polyfills:industrial_smelter" }
 *   // or  "structure": { "type": "single" }
 * }
 * }</pre>
 *
 * <p>
 * {@code input} is listed in reading order and mapped onto the grid row by row,
 * so its length must equal {@code width * height}. Every slot not otherwise named
 * is background.
 */
public final class WorkbenchLoader {

    private WorkbenchLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("workbenches", Registries.PHASE_DEFINITIONS, WorkbenchLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("workbenches", WorkbenchLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + WorkbenchDefinition.REGISTRY.size() + " workbench definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));

        int size = view.rangedInt("size", 54, 9, 54);
        if (size % 9 != 0)
            throw view.error("size must be a multiple of 9, found " + size);

        JsonView grid = view.has("grid") ? view.object("grid") : null;
        int width = grid == null ? 3 : grid.rangedInt("width", 3, 1, 9);
        int height = grid == null ? 3 : grid.rangedInt("height", 3, 1, 6);

        JsonView slots = view.has("slots") ? view.object("slots") : view;
        List<Integer> inputs = slots.intList("input");
        List<Integer> outputs = slots.intList("output");
        List<Integer> tools = slots.intList("tool");
        List<Integer> customs = slots.intList("custom");


        if (!inputs.isEmpty() && inputs.size() != width * height)
            throw view.error("'input' lists " + inputs.size() + " slots but the grid is "
                    + width + "x" + height + " (" + (width * height) + ")");

        SlotLayout.Builder builder = SlotLayout.builder(size).grid(width, height);
        for (int i = 0; i < inputs.size(); i++) {
            int slot = requireInRange(view, inputs.get(i), size, "input");
            builder.input(slot, i % width, i / width);
        }
        for (int slot : outputs)
            builder.output(requireInRange(view, slot, size, "output"));
        // A tool is a CUSTOM slot to the menu framework — not a grid ingredient — but
        // the definition remembers which ones they are so recipes can demand a tool.
        for (int slot : tools)
            builder.custom(requireInRange(view, slot, size, "tool"));
        for (int slot : customs)
            builder.custom(requireInRange(view, slot, size, "custom"));

        WorkbenchDefinition.Structure structure = WorkbenchDefinition.Structure.SINGLE;
        Key multiblock = null;
        if (view.has("structure")) {
            JsonView structureView = view.object("structure");
            structure = structureView.enumValue("type", WorkbenchDefinition.Structure.class,
                    WorkbenchDefinition.Structure.SINGLE);
            if (structure == WorkbenchDefinition.Structure.MULTIBLOCK) {
                if (!structureView.has("multiblock"))
                    throw structureView.error("structure type 'multiblock' needs a 'multiblock' id");
                multiblock = structureView.key("multiblock", "polyfills");
            }
        }

        // Each render entry carries the same params as an item renderer, minus the item.
        List<WorkbenchDefinition.RenderSlot> renderSlots = new ArrayList<>();
        for (JsonView entry : slots.objectList("render")) {
            int slot = requireInRange(view, entry.integer("slot"), size, "render.slot");
            renderSlots.add(new WorkbenchDefinition.RenderSlot(slot,
                    vec(entry, "position", new float[] { 4f, 13.5f, 4f }),
                    vec(entry, "rotation", new float[] { 0f, 0f, 0f }),
                    entry.floating("scale", 0.375f),
                    // A horizontal-double station has two cells; `half: right` draws on the
                    // second one. Ignored by single-block and multiblock stations.
                    "right".equalsIgnoreCase(entry.string("half", "master"))));
        }

        WorkbenchDefinition.REGISTRY.register(id, new WorkbenchDefinition(id,
                view.string("title", id.value()), size, builder.build(), structure, multiblock,
                tools, renderSlots));
    }

    /** Reads a 3-component vector, defaulting component-wise. */
    private static float[] vec(JsonView view, String field, float[] fallback) {
        if (!view.has(field))
            return fallback;
        List<Integer> ints = view.intList(field);
        if (ints.size() == 3)
            return new float[] { ints.get(0), ints.get(1), ints.get(2) };
        var raw = view.raw().get(field);
        if (raw.isJsonArray() && raw.getAsJsonArray().size() == 3) {
            var array = raw.getAsJsonArray();
            return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat() };
        }
        throw view.error("'" + field + "' must be [x, y, z]");
    }

    private static int requireInRange(JsonView view, int slot, int size, String field) {
        if (slot < 0 || slot >= size)
            throw view.error("'" + field + "' slot " + slot + " is outside the container (size " + size + ")");
        return slot;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
