package dev.arubik.craftengine.pipe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.momirealms.craftengine.core.util.Key;

/**
 * A kind of pipe: which block it places, what it carries, how fast, what it
 * connects to, and which preview models the wand draws.
 *
 * <p>
 * Previously this was split across three places that had to agree by hand — the
 * {@code CAPACITY}/{@code TRANSFER_PER_TICK} constants in {@code PipeBehavior}
 * and {@code GasPipeBehavior}, the connect sets hardcoded in those
 * constructors, and a private {@code PipeKind} enum in
 * {@link PipeWandListener} holding the block id and preview-item prefix. Adding
 * a pipe tier meant editing all three and recompiling.
 *
 * <p>
 * Like fluids and gases this is an identity registry: entries survive a reload
 * so placed blocks keep resolving, and only their {@link PipeProperties} are
 * swapped.
 */
public final class PipeType {

    /** Default namespace for pipe ids, so data files may write a bare {@code "copper"}. */
    public static final String NAMESPACE = "polyfills";

    /** Every known pipe kind. Identity-stable — see {@link Registry#clearsOnReload()}. */
    public static final Registry<PipeType> REGISTRY = Registries.create("pipe_type", false);

    /** What a pipe carries. Decides which carrier interface and engine drives it. */
    public enum Resource {
        FLUID,
        GAS,
        /** Discrete item transport (Mekanism-logistics-alike). See {@code pipe.item.ItemPipeBehavior}. */
        ITEM,
        /** CraftEnergy (Forge-Energy-alike). See {@code energy.behavior.EnergyCableBehavior}. */
        ENERGY
    }

    /**
     * The tunable half of a pipe kind.
     *
     * @param blockId          the custom block this pipe places
     * @param resource         fluid or gas
     * @param capacity         mB the pipe segment buffers
     * @param transferPerTick  mB/tick moved by the legacy per-block push path
     * @param conductance      edge weight in the network solver; higher flows more
     *                         freely, and this is what makes one tier faster than
     *                         another
     * @param tier             ordering hint for tooltips and upgrade paths
     * @param connectsTo       extra block ids this pipe always connects to, on top
     *                         of the face-aware carrier check
     * @param previewNamespace namespace of the wand's preview items
     * @param previewPrefix    preview item id prefix; the wand appends
     *                         {@code _<6-char mask>}
     * @param maskOrder        the six directions, in the order the pack's variant
     *                         table spells them — the default {@code s,w,n,e,u,d}
     *                         must match the block config or previews show the
     *                         wrong elbow
     */
    public record PipeProperties(
            Key blockId,
            Resource resource,
            int capacity,
            int transferPerTick,
            double conductance,
            int tier,
            Set<String> connectsTo,
            String previewNamespace,
            String previewPrefix,
            List<String> maskOrder,
            float[] previewOffset,
            float previewScale,
            List<String> replaceableBlocks,
            Key panel) {

        /** Blocks a route may be built through. Vanilla-ish "soft" blocks by default. */
        public static final List<String> DEFAULT_REPLACEABLE = List.of("minecraft:air", "minecraft:cave_air",
                "minecraft:void_air", "minecraft:water", "minecraft:lava", "minecraft:short_grass",
                "minecraft:tall_grass", "minecraft:snow");

        /** The direction order used by every pipe the plugin ships. */
        public static final List<String> DEFAULT_MASK_ORDER = List.of("south", "west", "north", "east", "up", "down");
    }

    private final Key id;
    private volatile PipeProperties properties;

    private PipeType(Key id, PipeProperties properties) {
        this.id = id;
        this.properties = properties;
    }

    // ------------------------------------------------------------- built-ins

        public static final PipeType COPPER = builtin("copper", Key.of("cml", "copper_pipe"), Resource.FLUID,
            1000, 100, 1000.0, 1, Set.of("cml:copper_valve", "cml:copper_tank", "cml:fluid_block_tank"),
            "pipe_preview_copper");

    public static final PipeType STEEL = builtin("steel", Key.of("cml", "iron_pipe"), Resource.GAS,
            1000, 100, 1000.0, 1, Set.of("cml:gas_pump", "cml:gas_valve", "cml:gas_tank"), "pipe_preview_steel");

    private static PipeType builtin(String path, Key blockId, Resource resource, int capacity, int transferPerTick,
            double conductance, int tier, Set<String> connectsTo, String previewPrefix) {
        Key id = Key.of(NAMESPACE, path);
        PipeType type = new PipeType(id, new PipeProperties(blockId, resource, capacity, transferPerTick, conductance,
                tier, Set.copyOf(connectsTo), "cml", previewPrefix, PipeProperties.DEFAULT_MASK_ORDER,
                new float[] { 0.5f, 0.5f, 0.5f }, 1.0f, PipeProperties.DEFAULT_REPLACEABLE, null));
        return REGISTRY.register(id, type);
    }

    /**
     * Looks a pipe kind up, creating it if the load phase is still open. Returns
     * {@code null} for an unknown id once the registry is frozen.
     */
    public static PipeType getOrCreate(Key id) {
        PipeType existing = REGISTRY.get(id);
        if (existing != null)
            return existing;
        if (REGISTRY.isFrozen())
            return null;
        PipeType created = new PipeType(id, new PipeProperties(id, Resource.FLUID, 1000, 100, 1000.0, 1,
                Set.of(), "cml", "pipe_preview_" + id.value(), PipeProperties.DEFAULT_MASK_ORDER,
                new float[] { 0.5f, 0.5f, 0.5f }, 1.0f, PipeProperties.DEFAULT_REPLACEABLE, null));
        return REGISTRY.register(id, created);
    }

    /** Replaces this pipe kind's tunables. Load-phase only, called by the loader. */
    public void applyProperties(PipeProperties properties) {
        if (REGISTRY.isFrozen())
            throw new IllegalStateException("Cannot retune pipe type '" + id + "' after the load phase");
        this.properties = properties;
    }

    /**
     * The pipe kind that places a given block, or {@code null}.
     *
     * <p>
     * This is how the wand identifies what the player is holding, replacing the old
     * "sniff the behavior class, then hardcode the block id" pair.
     */
    public static PipeType byBlockId(Key blockId) {
        if (blockId == null)
            return null;
        for (PipeType type : REGISTRY.values())
            if (type.properties.blockId().equals(blockId))
                return type;
        return null;
    }

    /** Resolves by legacy name or namespaced id; {@code null} if unknown. */
    public static PipeType byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        if (trimmed.indexOf(':') >= 0)
            return REGISTRY.get(Key.of(trimmed));
        return REGISTRY.get(Key.of(NAMESPACE, trimmed.toLowerCase(Locale.ROOT)));
    }

    // ------------------------------------------------------------- accessors

    public Key id() {
        return id;
    }

    public PipeProperties properties() {
        return properties;
    }

    public Key blockId() {
        return properties.blockId();
    }

    public Resource resource() {
        return properties.resource();
    }

    public int capacity() {
        return properties.capacity();
    }

    public int transferPerTick() {
        return properties.transferPerTick();
    }

    public double conductance() {
        return properties.conductance();
    }

    public int tier() {
        return properties.tier();
    }

    /** A fresh mutable copy, because the connect set is handed to a behavior that owns it. */
    public Set<String> connectsTo() {
        return new LinkedHashSet<>(properties.connectsTo());
    }

    public String previewNamespace() {
        return properties.previewNamespace();
    }

    public String previewPrefix() {
        return properties.previewPrefix();
    }

    public List<String> maskOrder() {
        return properties.maskOrder();
    }

    /** Where inside the cell the ghost pipe is drawn. */
    public float[] previewOffset() {
        return properties.previewOffset().clone();
    }

    public float previewScale() {
        return properties.previewScale();
    }

    /** Block ids a route may be carved through. */
    public List<String> replaceableBlocks() {
        return properties.replaceableBlocks();
    }

    /**
     * The {@code MachineDefinition} id this pipe's segments use as their block-entity controller's
     * config panel (pages/scripts/live per-instance IOConfiguration — see {@code ItemPipeBehavior}),
     * or {@code null} for a plain conduit (today's fluid/gas pipes, which use a bare
     * {@code PersistentBlockEntity} and the always-open static {@code IOConfiguration.Open()}).
     * Any pipe resource may opt into a panel this way — nothing here is item-specific.
     */
    public Key panel() {
        return properties.panel();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
