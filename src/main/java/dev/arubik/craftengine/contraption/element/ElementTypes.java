package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.nbt.CompoundTag;
import net.momirealms.craftengine.core.util.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registry of element type keys. Persistent elements additionally register a deserializer
 * so they can be restored from saved NBT (ephemeral elements are rebuilt by ElementBuilder
 * and never serialized).
 */
public final class ElementTypes {

    private ElementTypes() {}

    public static final Key BLOCK = Key.of("polyfills", "block");
    public static final Key FURNITURE = Key.of("polyfills", "furniture");
    public static final Key SEAT = Key.of("polyfills", "seat");
    public static final Key ENTITY = Key.of("polyfills", "entity");
    public static final Key HITBOX = Key.of("polyfills", "hitbox");
    public static final Key INTERACTION = Key.of("polyfills", "interaction");
    public static final Key ITEM_FRAME = Key.of("polyfills", "item_frame");
    public static final Key PISTON_SHAFT = Key.of("polyfills", "piston_shaft");
    public static final Key CAMPFIRE = Key.of("polyfills", "campfire");
    public static final Key ENTITY_RENDERER = Key.of("polyfills", "entity_renderer");
    public static final Key JUKEBOX = Key.of("polyfills", "jukebox");
    public static final Key SKULL = Key.of("polyfills", "skull");
    public static final Key SIGN = Key.of("polyfills", "sign");
    public static final Key FLUID = Key.of("polyfills", "fluid");
    public static final Key MARKER = ContraptionMarkerElement.TYPE;

    private static final Map<Key, BiFunction<CompoundTag, ContraptionLevel, ContraptionElement>> DESERIALIZERS
            = new HashMap<>();

    static {
        DESERIALIZERS.put(MARKER, (tag, level) -> ContraptionMarkerElement.fromNbt(tag));
    }

    public static void register(Key type, BiFunction<CompoundTag, ContraptionLevel, ContraptionElement> deserializer) {
        DESERIALIZERS.put(type, deserializer);
    }

    public static ContraptionElement fromNbt(CompoundTag tag, ContraptionLevel level) {
        String typeStr = tag.getString("type").orElseThrow();
        Key type = Key.of(typeStr);
        BiFunction<CompoundTag, ContraptionLevel, ContraptionElement> deser = DESERIALIZERS.get(type);
        if (deser == null) {
            throw new IllegalArgumentException("Unknown element type: " + type);
        }
        return deser.apply(tag, level);
    }
}
