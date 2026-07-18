package dev.arubik.craftengine.contraption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

/**
 * Byte-blob round-trip for a captured structure's {@link CompoundTag} manifest
 * (CONTRAPTIONS.md §1 "Capture" / §4 spike #2). Thin wrapper over vanilla
 * {@link NbtIo#writeAnyTag}/{@link NbtIo#readAnyTag} — the nameless "network" tag format:
 * a single leading type-id byte followed by the raw tag body, with NO root-name UTF and NO
 * gzip. This is byte-for-byte identical to CraftEngine's former
 * {@code NBT.toBytes}/{@code NBT.fromBytes} (which wrote {@code writeUnnamedTag(tag, out, false)}
 * over a plain {@code DataOutputStream}), so manifest blobs written before the swap still load
 * unchanged. NOT {@link NbtIo#write}, which prefixes an empty root-name UTF, and NOT
 * {@link NbtIo#writeCompressed}, which gzips — either would change the on-disk bytes. Kept as its
 * own tiny class (rather than inlined in {@link ContraptionCapture}) so the wire-format choice is
 * one obvious place to change if it ever needs to (e.g. gzip-compressed like a chunk region).
 */
public final class ContraptionNbt {

    private ContraptionNbt() {
    }

    public static byte[] toBytes(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(baos)) {
            NbtIo.writeAnyTag(tag, out);
        }
        return baos.toByteArray();
    }

    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            Tag tag = NbtIo.readAnyTag(in, NbtAccounter.unlimitedHeap());
            if (tag instanceof CompoundTag compound) {
                return compound;
            }
            throw new IOException("Root tag must be CompoundTag");
        }
    }
}
