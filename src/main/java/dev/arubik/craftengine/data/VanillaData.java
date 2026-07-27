package dev.arubik.craftengine.data;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * Bridge to Minecraft's own data-driven vocabulary.
 *
 * <p>
 * Rather than invent a private mini-DSL for "which item is this", "which block
 * counts as a wall", or "how much does an XP bottle give", the data files in
 * this plugin speak vanilla: {@code ItemPredicate}, {@code BlockPredicate},
 * {@code NumberProvider} and {@code LootItemCondition}, all parsed through their
 * official codecs. Pack authors already know the syntax from loot tables,
 * advancements and predicates, and the plugin inherits the whole feature set —
 * tags, data components, NBT, state properties, entity targets — for free.
 *
 * <p>
 * The codecs are registry-aware (an item tag has to resolve against the server's
 * registries), so parsing goes through {@link RegistryOps}. That means data must
 * be read while a server is running, which is exactly when
 * {@code CraftEngineReloadEvent} fires.
 */
public final class VanillaData {

    private VanillaData() {
    }

    /** The server's frozen registries, or {@code null} before the server exists. */
    public static HolderLookup.Provider registries() {
        var server = net.minecraft.server.MinecraftServer.getServer();
        return server == null ? null : server.registryAccess();
    }

    /** The overworld, used as the context level for rolls and world-free predicates. */
    public static ServerLevel anyLevel() {
        var server = net.minecraft.server.MinecraftServer.getServer();
        if (server == null)
            return null;
        for (ServerLevel level : server.getAllLevels())
            return level;
        return null;
    }

    /**
     * Parses a vanilla codec out of a JSON element.
     *
     * @param where breadcrumb used in the error message, so a bad predicate names its
     *              file and field like every other data error in this plugin
     */
    public static <T> T parse(Codec<T> codec, JsonElement json, String where) {
        if (json == null || json.isJsonNull())
            throw new JsonView.MalformedDataException(where + ": required value is missing");
        HolderLookup.Provider provider = registries();
        if (provider == null)
            throw new JsonView.MalformedDataException(
                    where + ": server registries are not available yet; data must load with a server running");
        var ops = RegistryOps.create(JsonOps.INSTANCE, provider);
        return codec.parse(ops, json)
                .getOrThrow(error -> new JsonView.MalformedDataException(where + ": " + error));
    }

    /** Parses a codec from a named field of {@code view}. */
    public static <T> T parseField(Codec<T> codec, JsonView view, String field) {
        return parse(codec, view.raw().get(field), view.path() + " > " + field);
    }

    /** Parses an optional field, or {@link Optional#empty()} if absent. */
    public static <T> Optional<T> parseOptional(Codec<T> codec, JsonView view, String field) {
        if (!view.has(field))
            return Optional.empty();
        return Optional.of(parseField(codec, view, field));
    }

    /**
     * A minimal {@link LootContext} for rolling a {@link NumberProvider}.
     *
     * <p>
     * Uses the empty parameter set, so constant/uniform/binomial providers work
     * while ones that read an entity or block would have nothing to read — that is
     * the correct trade for "how many mB does this bottle hold".
     */
    public static LootContext lootContext(ServerLevel level) {
        if (level == null)
            return null;
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    /** Rolls a number provider, falling back if no server level is available. */
    public static int roll(NumberProvider provider, int fallback) {
        if (provider == null)
            return fallback;
        LootContext context = lootContext(anyLevel());
        if (context == null)
            return fallback;
        try {
            return provider.getInt(context);
        } catch (Throwable ignored) {
            // A provider that needs context the empty set does not carry.
            return fallback;
        }
    }
}
