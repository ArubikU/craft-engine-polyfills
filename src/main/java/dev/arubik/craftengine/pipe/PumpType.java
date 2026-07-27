package dev.arubik.craftengine.pipe;

import java.util.Locale;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.momirealms.craftengine.core.util.Key;

/**
 * A kind of pump: how much it holds, how fast it draws from the world and how
 * hard it pushes into a network.
 *
 * <p>
 * Pumps keep their Java block entities because their core loop — scanning the
 * world for a fluid or gas vein — is genuinely bespoke and not expressible as
 * data. Their <em>numbers</em> are another matter: they were spread across
 * per-behavior config keys with defaults hardcoded in each factory, so two pumps
 * could not share a tier and a new one meant another factory. Those numbers now
 * live here, in {@code pump_types/*.json}, next to the pipe tiers they feed.
 *
 * <p>
 * Identity-stable like {@link PipeType}: placed pumps hold references, so a
 * reload swaps properties in place rather than replacing entries.
 */
public final class PumpType {

    /** Default namespace for pump ids. */
    public static final String NAMESPACE = "polyfills";

    /** Every known pump kind. Identity-stable — see {@link Registry#clearsOnReload()}. */
    public static final Registry<PumpType> REGISTRY = Registries.create("pump_type", false);

    /**
     * The tunable half of a pump.
     *
     * @param blockId         the custom block this pump is
     * @param resource        whether it moves fluid or gas
     * @param capacity        internal buffer in mB
     * @param extractPerTick  mB drawn from the source per extraction
     * @param pushPerTick     mB pushed into the network per tick
     * @param pressure        head the pump adds, which is what makes it lift
     * @param extractTickRate ticks between extractions from the world
     * @param maxPoints       extraction points a single pump may claim
     * @param maxVeinBlocks   safety cap on how far a vein scan walks
     * @param mbPerPoint      mB one extraction point of a vein is worth (gas pumps)
     */
    public record PumpProperties(
            Key blockId,
            PipeType.Resource resource,
            int capacity,
            int extractPerTick,
            int pushPerTick,
            int pressure,
            int extractTickRate,
            double maxPoints,
            int maxVeinBlocks,
            int mbPerPoint) {
    }

    private final Key id;
    private volatile PumpProperties properties;

    private PumpType(Key id, PumpProperties properties) {
        this.id = id;
        this.properties = properties;
    }

    // ------------------------------------------------------------- built-ins

    /** Values match the defaults the pump factories used before this registry existed. */
    public static final PumpType MACHINE_PUMP = builtin("machine_pump", Key.of("cml", "iron_pump"),
            PipeType.Resource.FLUID, 8000, 1000, 1000, 10, 10, 8.0, 512, 10);

    public static final PumpType GAS_PUMP = builtin("gas_pump", Key.of("cml", "gas_pump"),
            PipeType.Resource.GAS, 4000, 1000, 1000, 8, 20, 8.0, 512, 10);

    private static PumpType builtin(String path, Key blockId, PipeType.Resource resource, int capacity,
            int extractPerTick, int pushPerTick, int pressure, int extractTickRate, double maxPoints,
            int maxVeinBlocks, int mbPerPoint) {
        Key id = Key.of(NAMESPACE, path);
        return REGISTRY.register(id, new PumpType(id, new PumpProperties(blockId, resource, capacity,
                extractPerTick, pushPerTick, pressure, extractTickRate, maxPoints, maxVeinBlocks, mbPerPoint)));
    }

    /** Looks a pump up, creating it if the load phase is still open. */
    public static PumpType getOrCreate(Key id) {
        PumpType existing = REGISTRY.get(id);
        if (existing != null)
            return existing;
        if (REGISTRY.isFrozen())
            return null;
        return REGISTRY.register(id, new PumpType(id, new PumpProperties(id, PipeType.Resource.FLUID,
                8000, 1000, 1000, 10, 10, 8.0, 512, 10)));
    }

    /** Replaces this pump's tunables. Load-phase only. */
    public void applyProperties(PumpProperties properties) {
        if (REGISTRY.isFrozen())
            throw new IllegalStateException("Cannot retune pump type '" + id + "' after the load phase");
        this.properties = properties;
    }

    /** The pump kind a given block is, or {@code null}. */
    public static PumpType byBlockId(Key blockId) {
        if (blockId == null)
            return null;
        for (PumpType type : REGISTRY.values())
            if (type.properties.blockId().equals(blockId))
                return type;
        return null;
    }

    public static PumpType byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed)
                : Key.of(NAMESPACE, trimmed.toLowerCase(Locale.ROOT)));
    }

    // ------------------------------------------------------------- accessors

    public Key id() {
        return id;
    }

    public PumpProperties properties() {
        return properties;
    }

    public Key blockId() {
        return properties.blockId();
    }

    public PipeType.Resource resource() {
        return properties.resource();
    }

    public int capacity() {
        return properties.capacity();
    }

    public int extractPerTick() {
        return properties.extractPerTick();
    }

    public int pushPerTick() {
        return properties.pushPerTick();
    }

    public int pressure() {
        return properties.pressure();
    }

    public int extractTickRate() {
        return properties.extractTickRate();
    }

    public double maxPoints() {
        return properties.maxPoints();
    }

    public int maxVeinBlocks() {
        return properties.maxVeinBlocks();
    }

    /** mB one extraction point of a vein is worth; used by the gas pump. */
    public int mbPerPoint() {
        return properties.mbPerPoint();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
