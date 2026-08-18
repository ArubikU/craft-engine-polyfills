/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.gas.GasType;
import net.momirealms.craftengine.core.util.Key;

public final class GasTypeLoader {
    private GasTypeLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("gas_types", 0, GasTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("gas_types", GasTypeLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger().info("Applied " + count + " gas type definitions (" + GasType.REGISTRY.size() + " total).");
        }
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills") : Key.of((String)"polyfills", (String)GasTypeLoader.stripExtension(fileName));
        GasType type = GasType.getOrCreate(id);
        if (type == null) {
            throw view.error("gas registry is frozen; '" + String.valueOf(id) + "' cannot be defined now");
        }
        GasType.GasProperties base = type.properties();
        type.applyProperties(new GasType.GasProperties(view.string("display_name", base.displayName()), view.string("translation_key", base.translationKey()), GasTypeLoader.parseColor(view, base.color()), view.rangedDouble("density", base.density(), 0.0, 1000.0), view.string("tank_variant", base.tankVariant()), view.key("vent_particle", "minecraft", base.ventParticle()), view.rangedInt("vent_per_particle", base.ventPerParticle(), 1, 100000), view.rangedDouble("vent_spread", base.ventSpread(), 0.0, 8.0), view.rangedDouble("vent_speed", base.ventSpeed(), 0.0, 8.0)));
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

