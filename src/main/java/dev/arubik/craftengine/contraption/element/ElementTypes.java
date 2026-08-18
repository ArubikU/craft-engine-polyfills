/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ContraptionMarkerElement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.momirealms.craftengine.core.util.Key;

public final class ElementTypes {
    public static final Key BLOCK = Key.of((String)"polyfills", (String)"block");
    public static final Key FURNITURE = Key.of((String)"polyfills", (String)"furniture");
    public static final Key SEAT = Key.of((String)"polyfills", (String)"seat");
    public static final Key ENTITY = Key.of((String)"polyfills", (String)"entity");
    public static final Key HITBOX = Key.of((String)"polyfills", (String)"hitbox");
    public static final Key INTERACTION = Key.of((String)"polyfills", (String)"interaction");
    public static final Key ITEM_FRAME = Key.of((String)"polyfills", (String)"item_frame");
    public static final Key PISTON_SHAFT = Key.of((String)"polyfills", (String)"piston_shaft");
    public static final Key CAMPFIRE = Key.of((String)"polyfills", (String)"campfire");
    public static final Key ENTITY_RENDERER = Key.of((String)"polyfills", (String)"entity_renderer");
    public static final Key JUKEBOX = Key.of((String)"polyfills", (String)"jukebox");
    public static final Key SKULL = Key.of((String)"polyfills", (String)"skull");
    public static final Key SIGN = Key.of((String)"polyfills", (String)"sign");
    public static final Key FLUID = Key.of((String)"polyfills", (String)"fluid");
    public static final Key BETTER_MODEL = Key.of((String)"polyfills", (String)"better_model");
    public static final Key MARKER = ContraptionMarkerElement.TYPE;
    public static final Key MODEL_ENGINE = Key.of((String)"polyfills", (String)"model_engine");
    public static final Key MACHINE_RENDERER = Key.of((String)"polyfills", (String)"machine_renderer");
    private static final Map<Key, BiFunction<CompoundTag, ContraptionLevel, ContraptionElement>> DESERIALIZERS = new HashMap<Key, BiFunction<CompoundTag, ContraptionLevel, ContraptionElement>>();

    private ElementTypes() {
    }

    public static void register(Key type, BiFunction<CompoundTag, ContraptionLevel, ContraptionElement> deserializer) {
        DESERIALIZERS.put(type, deserializer);
    }

    public static ContraptionElement fromNbt(CompoundTag tag, ContraptionLevel level) {
        String typeStr = (String)tag.getString("type").orElseThrow();
        Key type = Key.of((String)typeStr);
        BiFunction<CompoundTag, ContraptionLevel, ContraptionElement> deser = DESERIALIZERS.get(type);
        if (deser == null) {
            throw new IllegalArgumentException("Unknown element type: " + String.valueOf(type));
        }
        return deser.apply(tag, level);
    }

    static {
        DESERIALIZERS.put(MARKER, (tag, level) -> ContraptionMarkerElement.fromNbt(tag));
    }
}

