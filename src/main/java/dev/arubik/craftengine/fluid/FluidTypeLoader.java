/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.fluid.FluidType;
import net.momirealms.craftengine.core.util.Key;

public final class FluidTypeLoader {
    private FluidTypeLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("fluid_types", 0, FluidTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("fluid_types", FluidTypeLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger().info("Applied " + count + " fluid type definitions (" + FluidType.REGISTRY.size() + " total).");
        }
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills") : Key.of((String)"polyfills", (String)FluidTypeLoader.stripExtension(fileName));
        FluidType type = FluidType.getOrCreate(id);
        if (type == null) {
            throw view.error("fluid registry is frozen; '" + String.valueOf(id) + "' cannot be defined now");
        }
        FluidType.FluidProperties base = type.properties();
        type.applyProperties(new FluidType.FluidProperties(view.rangedInt("unit_mb", base.unitMb(), 0, Integer.MAX_VALUE), view.rangedInt("mb_per_full_block", base.mbPerFullBlock(), 0, Integer.MAX_VALUE), view.rangedInt("block_collect_delay", base.blockCollectDelay(), 1, 1200), view.rangedInt("carrier_io_delay", base.carrierIoDelay(), 1, 1200), view.string("render_family", base.renderFamily()), FluidTypeLoader.parseColor(view, base.color()), view.rangedDouble("density", base.density(), 0.0, 1000.0), view.rangedDouble("viscosity", base.viscosity(), 0.001, 1000.0), view.string("fill_sound", base.fillSound()), view.string("drain_sound", base.drainSound()), view.has("vanilla_fluid") ? view.key("vanilla_fluid", "minecraft") : base.vanillaFluid(), view.string("tank_variant", base.tankVariant()), view.string("translation_key", base.translationKey())));
    }

    private static int parseColor(JsonView view, int fallback) {
        if (!view.has("color")) {
            return fallback;
        }
        String raw = view.string("color").trim();
        if (!raw.startsWith("#")) {
            return view.integer("color", fallback);
        }
        try {
            long value = Long.parseLong(raw.substring(1), 16);
            return raw.length() <= 7 ? (int)(0xFF000000L | value) : (int)value;
        }
        catch (NumberFormatException e) {
            throw view.error("color '" + raw + "' is not a hex value like #RRGGBB");
        }
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf(47) + 1);
        int dot = name.lastIndexOf(46);
        return dot < 0 ? name : name.substring(0, dot);
    }
}

