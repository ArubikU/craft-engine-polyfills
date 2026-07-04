package dev.arubik.craftengine.contraption;

import java.io.IOException;

import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import net.momirealms.craftengine.libraries.nbt.NBT;

/**
 * Byte-blob round-trip for a captured structure's {@link CompoundTag} manifest
 * (CONTRAPTIONS.md §1 "Capture" / §4 spike #2). Thin wrapper over CraftEngine's own
 * {@code NBT.toBytes}/{@code NBT.fromBytes} — NOT {@link CompoundTag#write}, which only
 * serializes the compound's raw body (no leading type-id byte) and is therefore NOT
 * byte-compatible with {@code NBT.fromBytes}'s expectations. Kept as its own tiny class
 * (rather than inlined in {@link ContraptionCapture}) so the wire-format choice is one
 * obvious place to change if it ever needs to (e.g. gzip-compressed like a chunk region).
 */
public final class ContraptionNbt {

    private ContraptionNbt() {
    }

    public static byte[] toBytes(CompoundTag tag) throws IOException {
        return NBT.toBytes(tag);
    }

    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        return NBT.fromBytes(bytes);
    }
}
