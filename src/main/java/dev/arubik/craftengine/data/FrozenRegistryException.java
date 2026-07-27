package dev.arubik.craftengine.data;

/**
 * Thrown when something tries to mutate a {@link Registry} after the load phase
 * has ended and {@link Registries#freezeAll()} has sealed it.
 */
public class FrozenRegistryException extends IllegalStateException {

    public FrozenRegistryException(String registryName, Object id) {
        super("Registry '" + registryName + "' is frozen; cannot register/remove '" + id
                + "'. Registration is only allowed during the data-load phase "
                + "(CraftEngineReloadEvent), not at runtime.");
    }
}
