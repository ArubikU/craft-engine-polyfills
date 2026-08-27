package dev.arubik.craftengine.conveyor.belt;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.momirealms.craftengine.core.util.Key;

/** A kind of conveyor belt — belt-side counterpart of {@code PipeType}, same identity-registry/
 *  data-driven-JSON shape ({@code belt_types/*.json}, loaded by {@link BeltTypeLoader}). */
public final class BeltType {

    public static final String NAMESPACE = "polyfills";
    public static final Registry<BeltType> REGISTRY = Registries.create("belt_type", false);

    /** What (if anything) a belt needs to move items. */
    public enum EnergyKind {
        /** Free kinetic transport (default). */
        NONE,
        /** Requires nonzero driving RPM (already how belts move; just documents/enforces it). */
        KINETIC,
        /** Draws {@code energyPerTick} CraftEnergy from its own buffer each active tick, stalling
         *  when it can't. Chargeable by a neighboring cable via {@link
         *  dev.arubik.craftengine.energy.EnergyCarrier}. */
        ELECTRIC
    }

    public record BeltProperties(
            Key blockId,
            float height,
            Set<String> connectsTo,
            EnergyKind energyType,
            int energyPerTick,
            int energyCapacity,
            int baseTravelTicks,
            float baseRpm,
            float stressImpact,
            String speedFormula,
            float itemScale,
            int slots,
            double pickupRadius,
            int maxLength) {

        public static final float DEFAULT_HEIGHT = ConveyorMath.BELT_TOP_Y;
        public static final int DEFAULT_BASE_TRAVEL_TICKS = 16;
        public static final float DEFAULT_BASE_RPM = 64.0f;
        public static final float DEFAULT_STRESS_IMPACT = 4.0f;
        public static final float DEFAULT_ITEM_SCALE = 0.5f;
        public static final int DEFAULT_SLOTS = 4;
        public static final double DEFAULT_PICKUP_RADIUS = 0.75;
        public static final int DEFAULT_MAX_LENGTH = 64;
    }

    private final Key id;
    private volatile BeltProperties properties;

    private BeltType(Key id, BeltProperties properties) {
        this.id = id;
        this.properties = properties;
    }

    public static BeltType getOrCreate(Key id) {
        BeltType existing = REGISTRY.get(id);
        if (existing != null)
            return existing;
        if (REGISTRY.isFrozen())
            return null;
        BeltType created = new BeltType(id, new BeltProperties(id, BeltProperties.DEFAULT_HEIGHT,
                Set.of(), EnergyKind.NONE, 0, 0,
                BeltProperties.DEFAULT_BASE_TRAVEL_TICKS, BeltProperties.DEFAULT_BASE_RPM,
                BeltProperties.DEFAULT_STRESS_IMPACT, null, BeltProperties.DEFAULT_ITEM_SCALE,
                BeltProperties.DEFAULT_SLOTS, BeltProperties.DEFAULT_PICKUP_RADIUS,
                BeltProperties.DEFAULT_MAX_LENGTH));
        return REGISTRY.register(id, created);
    }

    public void applyProperties(BeltProperties properties) {
        if (REGISTRY.isFrozen())
            throw new IllegalStateException("Cannot retune belt type '" + id + "' after the load phase");
        this.properties = properties;
    }

    /** {@code null} if unmatched — callers treat that as NONE/default-height. */
    public static BeltType byBlockId(Key blockId) {
        if (blockId == null)
            return null;
        for (BeltType type : REGISTRY.values())
            if (type.properties.blockId().equals(blockId))
                return type;
        return null;
    }

    public static BeltType byName(String name) {
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

    public BeltProperties properties() {
        return properties;
    }

    public Key blockId() {
        return properties.blockId();
    }

    public float height() {
        return properties.height();
    }

    public Set<String> connectsTo() {
        return new LinkedHashSet<>(properties.connectsTo());
    }

    public EnergyKind energyType() {
        return properties.energyType();
    }

    public int energyPerTick() {
        return properties.energyPerTick();
    }

    public int energyCapacity() {
        return properties.energyCapacity();
    }

    public int baseTravelTicks() {
        return properties.baseTravelTicks();
    }

    public float baseRpm() {
        return properties.baseRpm();
    }

    public float stressImpact() {
        return properties.stressImpact();
    }

    /** {@code null} means use {@code baseTravelTicks}/{@code baseRpm} the normal way. */
    public String speedFormula() {
        return properties.speedFormula();
    }

    public float itemScale() {
        return properties.itemScale();
    }

    public int slots() {
        return properties.slots();
    }

    public double pickupRadius() {
        return properties.pickupRadius();
    }

    public int maxLength() {
        return properties.maxLength();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
