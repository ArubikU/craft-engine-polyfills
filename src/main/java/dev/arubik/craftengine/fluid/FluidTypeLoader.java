package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;

import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code fluid_types/*.json} into {@link FluidType#REGISTRY}.
 *
 * <p>
 * A file may either retune a built-in liquid or introduce a new one; both use
 * the same shape, and every field is optional so a file that only wants to make
 * lava thinner can say exactly that:
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:lava",     // optional, defaults to the file name
 *   "unit_mb": 1000,            // mB in one item unit
 *   "mb_per_full_block": 1000,  // mB to place one block in the world
 *   "block_collect_delay": 12,  // ticks between pulls when harvesting world blocks
 *   "carrier_io_delay": 8,      // ticks between carrier-to-carrier transfers
 *   "render_family": "lava",    // pack model family: water | lava | xp
 *   "tank_variant": "lava",     // value written to the tank's `fluidtype` blockstate
 *   "color": "#CF5A16",
 *   "density": 3.0,             // relative to water; drives gravity head
 *   "viscosity": 12.0,          // relative to water; scales pipe conductance
 *   "fill_sound": "minecraft:item.bucket.empty_lava",
 *   "drain_sound": "minecraft:item.bucket.fill_lava",
 *   "vanilla_fluid": "minecraft:lava",   // omit for a fluid with no world form
 *   "translation_key": "polyfill.liquid.lava"
 * }
 * }</pre>
 */
public final class FluidTypeLoader {

    private FluidTypeLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("fluid_types", Registries.PHASE_TYPES, FluidTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("fluid_types", FluidTypeLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Applied " + count + " fluid type definitions (" + FluidType.REGISTRY.size() + " total).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id")
                ? view.key("id", FluidType.NAMESPACE)
                : Key.of(FluidType.NAMESPACE, stripExtension(fileName));

        FluidType type = FluidType.getOrCreate(id);
        if (type == null)
            throw view.error("fluid registry is frozen; '" + id + "' cannot be defined now");

        // Start from what the fluid already has, so a file only states what it changes.
        FluidType.FluidProperties base = type.properties();
        type.applyProperties(new FluidType.FluidProperties(
                view.rangedInt("unit_mb", base.unitMb(), 0, Integer.MAX_VALUE),
                view.rangedInt("mb_per_full_block", base.mbPerFullBlock(), 0, Integer.MAX_VALUE),
                view.rangedInt("block_collect_delay", base.blockCollectDelay(), 1, 1200),
                view.rangedInt("carrier_io_delay", base.carrierIoDelay(), 1, 1200),
                view.string("render_family", base.renderFamily()),
                parseColor(view, base.color()),
                view.rangedDouble("density", base.density(), 0.0, 1000.0),
                view.rangedDouble("viscosity", base.viscosity(), 0.001, 1000.0),
                view.string("fill_sound", base.fillSound()),
                view.string("drain_sound", base.drainSound()),
                view.has("vanilla_fluid") ? view.key("vanilla_fluid", "minecraft") : base.vanillaFluid(),
                view.string("tank_variant", base.tankVariant()),
                view.string("translation_key", base.translationKey()),
                view.string("bar_item_template", base.barItemTemplate()),
                view.rangedInt("bar_levels", base.barLevels(), 1, 64)));
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
