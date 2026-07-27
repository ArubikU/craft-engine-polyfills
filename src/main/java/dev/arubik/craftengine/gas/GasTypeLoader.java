package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;

import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code gas_types/*.json} into {@link GasType#REGISTRY}.
 *
 * <p>
 * Mirrors {@link dev.arubik.craftengine.fluid.FluidTypeLoader}: a file may
 * retune a built-in gas or introduce a new one, and every field is optional.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:steam",   // optional, defaults to the file name
 *   "display_name": "Steam",
 *   "translation_key": "polyfill.gas.steam",
 *   "color": "#E8F4F8",
 *   "density": 1.0,            // relative to steam; buoyancy and motor torque
 *   "tank_variant": "steam",
 *   "vent_particle": "minecraft:cloud",
 *   "vent_per_particle": 50,   // mB represented by one particle
 *   "vent_spread": 0.2,
 *   "vent_speed": 0.05
 * }
 * }</pre>
 */
public final class GasTypeLoader {

    private GasTypeLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("gas_types", Registries.PHASE_TYPES, GasTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("gas_types", GasTypeLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Applied " + count + " gas type definitions (" + GasType.REGISTRY.size() + " total).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id")
                ? view.key("id", GasType.NAMESPACE)
                : Key.of(GasType.NAMESPACE, stripExtension(fileName));

        GasType type = GasType.getOrCreate(id);
        if (type == null)
            throw view.error("gas registry is frozen; '" + id + "' cannot be defined now");

        GasType.GasProperties base = type.properties();
        type.applyProperties(new GasType.GasProperties(
                view.string("display_name", base.displayName()),
                view.string("translation_key", base.translationKey()),
                parseColor(view, base.color()),
                view.rangedDouble("density", base.density(), 0.0, 1000.0),
                view.string("tank_variant", base.tankVariant()),
                view.key("vent_particle", "minecraft", base.ventParticle()),
                view.rangedInt("vent_per_particle", base.ventPerParticle(), 1, 100000),
                view.rangedDouble("vent_spread", base.ventSpread(), 0.0, 8.0),
                view.rangedDouble("vent_speed", base.ventSpeed(), 0.0, 8.0)));
    }

    /** Accepts {@code "#RRGGBB"}, {@code "#AARRGGBB"} or a raw integer. */
    private static int parseColor(JsonView view, int fallback) {
        if (!view.has("color"))
            return fallback;
        String raw = view.string("color").trim();
        if (!raw.startsWith("#"))
            return view.integer("color", fallback);
        try {
            long value = Long.parseLong(raw.substring(1), 16);
            return raw.length() <= 7 ? (int) (0xFF000000L | value) : (int) value;
        } catch (NumberFormatException e) {
            throw view.error("color '" + raw + "' is not a hex value like #RRGGBB");
        }
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
