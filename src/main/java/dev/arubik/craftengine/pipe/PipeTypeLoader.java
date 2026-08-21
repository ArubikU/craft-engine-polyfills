package dev.arubik.craftengine.pipe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code pipe_types/*.json} into {@link PipeType#REGISTRY}.
 *
 * <p>
 * Every field is optional, so a file may add a whole new tier or just make
 * copper pipes carry more:
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:copper",       // optional, defaults to the file name
 *   "block": "cml:copper_pipe",     // the custom block this pipe places
 *   "resource": "fluid",            // fluid | gas
 *   "capacity": 1000,               // mB buffered per segment
 *   "transfer_per_tick": 100,
 *   "conductance": 1000.0,          // solver edge weight; higher = faster tier
 *   "tier": 1,
 *   "connects_to": ["cml:copper_valve", "cml:copper_tank"],
 *   "preview_namespace": "cml",
 *   "preview_prefix": "pipe_preview_copper",
 *   "mask_order": ["south", "west", "north", "east", "up", "down"]
 * }
 * }</pre>
 *
 * <p>
 * <b>Ordering note.</b> CraftEngine constructs block behaviors while loading its
 * own packs, which happens before {@code CraftEngineReloadEvent} — so
 * {@link #load()} is also called eagerly during {@code onEnable}, ensuring the
 * behaviors are built against current pipe data rather than the built-in
 * defaults.
 */
public final class PipeTypeLoader {

    private PipeTypeLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("pipe_types", Registries.PHASE_TYPES, PipeTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("pipe_types", PipeTypeLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Applied " + count + " pipe type definitions (" + PipeType.REGISTRY.size() + " total).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id")
                ? view.key("id", PipeType.NAMESPACE)
                : Key.of(PipeType.NAMESPACE, stripExtension(fileName));

        PipeType type = PipeType.getOrCreate(id);
        if (type == null)
            throw view.error("pipe registry is frozen; '" + id + "' cannot be defined now");

        PipeType.PipeProperties base = type.properties();

        Set<String> connectsTo = view.has("connects_to")
                ? new LinkedHashSet<>(view.stringList("connects_to"))
                : base.connectsTo();

        List<String> maskOrder = base.maskOrder();
        if (view.has("mask_order")) {
            maskOrder = view.stringList("mask_order");
            if (maskOrder.size() != 6)
                throw view.error("mask_order must list exactly 6 directions, found " + maskOrder.size());
        }

        float[] previewOffset = base.previewOffset();
        if (view.has("preview_offset")) {
            var raw = view.raw().get("preview_offset").getAsJsonArray();
            if (raw.size() != 3)
                throw view.error("preview_offset must be [x, y, z]");
            previewOffset = new float[] { raw.get(0).getAsFloat(), raw.get(1).getAsFloat(),
                    raw.get(2).getAsFloat() };
        }

        type.applyProperties(new PipeType.PipeProperties(
                view.key("block", "cml", base.blockId()),
                view.enumValue("resource", PipeType.Resource.class, base.resource()),
                view.rangedInt("capacity", base.capacity(), 1, Integer.MAX_VALUE),
                view.rangedInt("transfer_per_tick", base.transferPerTick(), 1, Integer.MAX_VALUE),
                view.rangedDouble("conductance", base.conductance(), 0.001, 1_000_000.0),
                view.rangedInt("tier", base.tier(), 0, 100),
                Set.copyOf(connectsTo),
                view.string("preview_namespace", base.previewNamespace()),
                view.string("preview_prefix", base.previewPrefix()),
                List.copyOf(maskOrder),
                previewOffset,
                view.floating("preview_scale", base.previewScale()),
                view.has("replaceable_blocks") ? List.copyOf(view.stringList("replaceable_blocks"))
                        : base.replaceableBlocks(),
                view.has("panel") ? view.key("panel", PipeType.NAMESPACE) : base.panel()));
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
