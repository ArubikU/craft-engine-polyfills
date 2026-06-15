package dev.arubik.craftengine.crafting;

/**
 * Engineer's-workbench recipes are now data-driven: defined in reloadable JSON under
 * {@code plugins/CraftEnginePolyfills/workbench_recipes/} and loaded by
 * {@link StationRecipeLoader} (on enable + on CraftEngine reload). This kept entry
 * point just triggers the initial load so existing call sites stay valid.
 */
public final class WorkbenchSamples {

    private static boolean loaded;

    private WorkbenchSamples() {
    }

    public static synchronized void registerDefaults() {
        if (loaded) {
            return;
        }
        loaded = true;
        StationRecipeLoader.load();
    }
}
