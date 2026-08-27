package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;

/**
 * Registry — read-only access to Minecraft's OWN vanilla registries (as opposed to this addon's
 * own {@code RecipeType}/{@code dev.arubik.craftengine.machine.recipe.loader.RecipeManager}, which
 * is this project's machine-processing recipes, or CraftEngine's own custom item/block ids, which
 * {@code Item.create}/{@code Block.id} already cover). Global singleton, same pattern as
 * ContraptionManager/Server.
 *
 * <p>{@code Registry.recipes} (see RecipeRegistryType) exposes vanilla recipe TYPES (stonecutting,
 * smelting, ...) with each recipe adapted through the SAME "Recipe" script type this addon's own
 * {@code Machine.recipes} uses — see {@code RecipeType.VanillaRecipeRef}. Every OTHER property
 * here (items/blocks/biomes/...) is a {@link RegistryCollectionType} — a plain id list (all/exists/
 * random), deliberately NOT trying to hand back a fully-adapted script object for every kind
 * (a bare item/block id is already what {@code Item.create}/{@code World.get_block} want to
 * receive, so there is nothing more to "adapt" there — see each property's own comment for
 * anything less obvious).
 */
public final class RegistryType {

    /** Singleton sentinel instance */
    public static final Object INSTANCE = new Object();

    private RegistryType() {}

    public static void register() {
        PolyTypeRegistry.define("Registry")
            .property("recipes", obj -> dev.arubik.craftengine.script.types.machine.RecipeRegistryType.wrap())
            // --- Hardcoded (BuiltInRegistries) — resolvable at JVM boot, no server/world needed ---
            .property("items",       obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.ITEM))
            .property("blocks",      obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.BLOCK))
            .property("entity_types",obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.ENTITY_TYPE))
            .property("sounds",      obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.SOUND_EVENT))
            .property("particles",   obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.PARTICLE_TYPE))
            .property("fluids",      obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.FLUID))
            .property("potions",     obj -> RegistryCollectionType.wrap(() -> BuiltInRegistries.POTION))
            // --- Data-driven (datapack) registries — only exist once the server has loaded its
            // datapacks, so each is resolved LAZILY (inside the supplier) rather than read here at
            // script-bootstrap time, which happens before any of that exists. A call before the
            // server is up (there isn't really a legitimate way to trigger that from a machine
            // script, but belt-and-suspenders) just throws inside the supplier, which
            // RegistryCollectionType's own try/catch already treats as "empty/false/null". ---
            .property("biomes",      obj -> RegistryCollectionType.wrap(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.BIOME)))
            .property("enchantments",obj -> RegistryCollectionType.wrap(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)))
            .property("damage_types",obj -> RegistryCollectionType.wrap(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)));
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("Registry", INSTANCE);
    }
}
