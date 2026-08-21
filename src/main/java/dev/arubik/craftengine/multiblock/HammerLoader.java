/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.multiblock;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.multiblock.HammerItems;
import net.momirealms.craftengine.core.util.Key;

public final class HammerLoader {
    private HammerLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("hammers", 0, HammerLoader::load);
    }

    public static void load() {
        HammerItems.clear();
        int count = DataFiles.loadDirectory("hammers", HammerLoader::apply);
        CraftEnginePolyfills.instance().getLogger().info("Loaded " + count + " hammer definition(s).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "cml") : Key.of((String)"cml", (String)HammerLoader.stripExtension(fileName));
        HammerItems.register(id);
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf(46);
        return dot >= 0 ? name.substring(0, dot) : name;
    }
}

