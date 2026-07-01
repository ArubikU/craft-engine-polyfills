package dev.arubik.craftengine.util;

/**
 * Primitive NBT type marker used by {@link TypedKey}/{@link CustomDataType} to select which
 * {@code CompoundTag} accessor to call. Nothing in this codebase touches Bukkit's
 * {@code PersistentDataContainer} anymore (see the chunk-PDC-to-block-entity migration) — every
 * TypedKey read/write goes straight to a block entity's own CE tag, so this replaces the old
 * {@code org.bukkit.persistence.PersistentDataType} marker with a native equivalent.
 */
public enum NbtType {
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BYTE_ARRAY,
    INTEGER_ARRAY,
    LONG_ARRAY,
    BOOLEAN
}
