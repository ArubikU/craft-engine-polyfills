package dev.arubik.craftengine.gas;

import java.util.Locale;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;

import net.momirealms.craftengine.core.util.Key;

/**
 * A kind of gas.
 *
 * <p>
 * Converted from a closed {@code enum} to an identity in {@link #REGISTRY} for
 * the same reasons as {@link dev.arubik.craftengine.fluid.FluidType}: the
 * per-gas numbers (density, how it vents, what it is worth to a motor) belong in
 * {@code gas_types/*.json}, not in Java. The built-in constants keep their
 * identity across reloads so live block entities and {@link GasKeys} save data
 * stay valid, and only their {@link GasProperties} are swapped.
 */
public final class GasType {

    /** Default namespace for gas ids, so data files may write a bare {@code "steam"}. */
    public static final String NAMESPACE = "polyfills";

    /** Every known gas. Identity-stable across reloads — see {@link Registry#clearsOnReload()}. */
    public static final Registry<GasType> REGISTRY = Registries.create("gas_type", false);

    /**
     * The tunable half of a gas.
     *
     * @param displayName    legacy plain-text name, kept for the deprecated accessor
     * @param translationKey client-side i18n key
     * @param color          ARGB tint for tooltips and particles
     * @param density        relative to steam; the network solver uses it for
     *                       buoyancy and a motor for torque per unit
     * @param tankVariant    value written to a tank's {@code gastype} blockstate to
     *                       pick its appearance; plain text, since that property is
     *                       CraftEngine's {@code string} type declared in the pack
     * @param ventParticle   particle spawned when venting to air
     * @param ventPerParticle how many mB one vent particle represents
     * @param ventSpread     particle spread in blocks
     * @param ventSpeed      particle speed
     */
    public record GasProperties(
            String displayName,
            String translationKey,
            int color,
            double density,
            String tankVariant,
            Key ventParticle,
            int ventPerParticle,
            double ventSpread,
            double ventSpeed) {

        /** Neutral defaults, used as the base a data file overrides field by field. */
        public static GasProperties defaults(String path) {
            return new GasProperties(path, "polyfill.gas." + path, 0xFFFFFFFF, 1.0, "empty",
                    Key.of("minecraft", "cloud"), 50, 0.2, 0.05);
        }
    }

    private final Key id;
    private final String legacyName;
    private volatile GasProperties properties;

    private GasType(Key id, String legacyName, GasProperties properties) {
        this.id = id;
        this.legacyName = legacyName;
        this.properties = properties;
    }

    // ------------------------------------------------------------- built-ins

    public static final GasType EMPTY = builtin("empty", "Empty", 0x00000000, 0.0, "empty");
    public static final GasType STEAM = builtin("steam", "Steam", 0xFFE8F4F8, 1.0, "steam");
    public static final GasType HEAVY_STEAM = builtin("heavy_steam", "Heavy Steam", 0xFFBFD4DC, 2.0,
            "heavy_steam");
    public static final GasType NITROGEN = builtin("nitrogen", "Nitrogen", 0xFFA8C8E8, 0.97, "nitrogen");

    private static GasType builtin(String path, String displayName, int color, double density, String variant) {
        Key id = Key.of(NAMESPACE, path);
        GasType type = new GasType(id, path.toUpperCase(Locale.ROOT),
                new GasProperties(displayName, "polyfill.gas." + path, color, density, variant,
                        Key.of("minecraft", "cloud"), 50, 0.2, 0.05));
        return REGISTRY.register(id, type);
    }

    /**
     * Looks a gas up, creating it if the load phase is still open. Returns
     * {@code null} for an unknown id once the registry is frozen.
     */
    public static GasType getOrCreate(Key id) {
        GasType existing = REGISTRY.get(id);
        if (existing != null)
            return existing;
        if (REGISTRY.isFrozen())
            return null;
        GasType created = new GasType(id, id.value().toUpperCase(Locale.ROOT), GasProperties.defaults(id.value()));
        return REGISTRY.register(id, created);
    }

    /** Replaces this gas's tunables. Load-phase only, called by the loader. */
    public void applyProperties(GasProperties properties) {
        if (REGISTRY.isFrozen())
            throw new IllegalStateException("Cannot retune gas '" + id + "' after the load phase");
        this.properties = properties;
    }

    // -------------------------------------------------------------- identity

    public Key id() {
        return id;
    }

    /** The legacy uppercase name ({@code "STEAM"}), which {@link GasKeys} persists. */
    public String name() {
        return legacyName;
    }

    @Override
    public String toString() {
        return legacyName;
    }

    /** Drop-in for the old {@code enum} method, including its throw on an unknown value. */
    public static GasType valueOf(String name) {
        GasType type = byName(name);
        if (type == null)
            throw new IllegalArgumentException("No gas type '" + name + "'; known: " + REGISTRY.keys());
        return type;
    }

    /** Resolves by legacy name or namespaced id; {@code null} if unknown. */
    public static GasType byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        if (trimmed.indexOf(':') >= 0)
            return REGISTRY.get(Key.of(trimmed));
        GasType byId = REGISTRY.get(Key.of(NAMESPACE, trimmed.toLowerCase(Locale.ROOT)));
        if (byId != null)
            return byId;
        for (GasType type : REGISTRY.values())
            if (type.legacyName.equalsIgnoreCase(trimmed))
                return type;
        return null;
    }

    /** Parses a config name (case-insensitive); unknown or blank yields {@link #EMPTY}. */
    public static GasType fromName(String s) {
        GasType type = byName(s);
        return type != null ? type : EMPTY;
    }

    /** All registered gases. Drop-in for the old {@code enum} method. */
    public static GasType[] values() {
        return REGISTRY.values().toArray(new GasType[0]);
    }

    // ------------------------------------------------------------ properties

    public GasProperties properties() {
        return properties;
    }

    /** @deprecated prefer {@link #translationKey()} + a translatable component (client i18n). */
    @Deprecated
    public String getDisplayName() {
        return properties.displayName();
    }

    /** Minecraft i18n key for this gas, resolved client-side from the pack lang. */
    public String translationKey() {
        return properties.translationKey();
    }

    public int color() {
        return properties.color();
    }

    public double density() {
        return properties.density();
    }

    public String tankVariant() {
        return properties.tankVariant();
    }


    public boolean isEmpty() {
        return this == EMPTY;
    }

    /** The gas a stored tank appearance maps back to; see {@code FluidType#byTankVariant}. */
    public static GasType byTankVariant(String variant) {
        if (variant == null)
            return EMPTY;
        for (GasType type : REGISTRY.values())
            if (variant.equalsIgnoreCase(type.properties.tankVariant()))
                return type;
        return EMPTY;
    }

    /**
     * Vents this gas into the air, spawning its particle. Returns {@code true} if
     * anything was vented (always so for a non-empty gas).
     */
    public boolean vent(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, int amount) {
        if (isEmpty())
            return false;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            GasProperties p = properties;
            var particle = net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE
                    .getOptional(net.minecraft.resources.Identifier.parse(p.ventParticle().toString()))
                    .orElse(net.minecraft.core.particles.ParticleTypes.CLOUD);
            if (particle instanceof net.minecraft.core.particles.SimpleParticleType simple) {
                serverLevel.sendParticles(simple,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        Math.max(1, amount / Math.max(1, p.ventPerParticle())),
                        p.ventSpread(), p.ventSpread(), p.ventSpread(), p.ventSpeed());
            }
        }
        return true;
    }
}
