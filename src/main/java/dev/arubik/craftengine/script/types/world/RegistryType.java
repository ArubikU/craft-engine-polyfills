package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
        // Every property here yields exactly ONE instance of one known PolyType, so each is
        // declared with TypeCodecs.polyType and the handler hands back the real instance:
        // PolyCodec.encode is ScriptValue.ofObj(name, value), byte-for-byte what each wrap()
        // helper below did (RecipeRegistryType.wrap → ofObj("RecipeRegistry", INSTANCE);
        // RegistryCollectionType.wrap(s) → ofObj("RegistryCollection", new Ref(s))).
        PolyTypeRegistry.define("Registry")
            .propertyTyped("recipes", TypeCodecs.polyType("RecipeRegistry", Object.class),
                obj -> dev.arubik.craftengine.script.types.machine.RecipeRegistryType.INSTANCE)
            // --- Hardcoded (BuiltInRegistries) — resolvable at JVM boot, no server/world needed ---
            .propertyTyped("items",       COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.ITEM))
            .propertyTyped("blocks",      COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.BLOCK))
            .propertyTyped("entity_types",COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.ENTITY_TYPE))
            .propertyTyped("sounds",      COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.SOUND_EVENT))
            .propertyTyped("particles",   COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.PARTICLE_TYPE))
            .propertyTyped("fluids",      COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.FLUID))
            .propertyTyped("potions",     COLLECTION, obj -> new RegistryCollectionType.Ref(() -> BuiltInRegistries.POTION))
            // --- Data-driven (datapack) registries — only exist once the server has loaded its
            // datapacks, so each is resolved LAZILY (inside the supplier) rather than read here at
            // script-bootstrap time, which happens before any of that exists. A call before the
            // server is up (there isn't really a legitimate way to trigger that from a machine
            // script, but belt-and-suspenders) just throws inside the supplier, which
            // RegistryCollectionType's own try/catch already treats as "empty/false/null". ---
            .propertyTyped("biomes",      COLLECTION, obj -> new RegistryCollectionType.Ref(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.BIOME)))
            .propertyTyped("enchantments",COLLECTION, obj -> new RegistryCollectionType.Ref(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)))
            .propertyTyped("damage_types",COLLECTION, obj -> new RegistryCollectionType.Ref(() ->
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)));
    }

    /** Shared return codec for every {@code RegistryCollection}-valued property above. */
    private static final PolyType.TypeCodec<RegistryCollectionType.Ref> COLLECTION =
            TypeCodecs.polyType("RegistryCollection", RegistryCollectionType.Ref.class);

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("Registry", INSTANCE);
    }
}
