package dev.arubik.craftengine.contraption.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Single entry point for all contraption serialization: level ↔ CompoundTag ↔ compressed bytes ↔ file/item.
 *
 * <p>Wire format: gzip-compressed vanilla CompoundTag ({@link NbtIo#writeCompressed}).
 * All paths (file persistence, item PDC, manifest) use this same encoding.
 */
public final class ContraptionStorage {

    private ContraptionStorage() {
    }

    // ─── Level ↔ CompoundTag ───────────────────────────────────────────────────

    public static CompoundTag dumpLevel(ContraptionLevel level) {
        ListTag blocks = new ListTag();
        for (BlockPos local : level.localPositions()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", local.getX());
            entry.putInt("y", local.getY());
            entry.putInt("z", local.getZ());
            entry.put("state", net.minecraft.nbt.NbtUtils.writeBlockState(level.getBlockState(local)));
            CompoundTag beTag = level.saveBlockEntity(local);
            if (beTag != null) {
                entry.put("be", beTag);
            }
            blocks.add(entry);
        }
        CompoundTag root = new CompoundTag();
        root.put("blocks", blocks);
        root.putDouble("scale", level.realScaleFactor());

        List<long[]> glue = level.glueEdgesLocal();
        long[] a = new long[glue.size()];
        long[] b = new long[glue.size()];
        for (int i = 0; i < glue.size(); i++) {
            a[i] = glue.get(i)[0];
            b[i] = glue.get(i)[1];
        }
        CompoundTag glueTag = new CompoundTag();
        glueTag.putLongArray("a", a);
        glueTag.putLongArray("b", b);
        root.put("glue", glueTag);

        ListTag furniture = new ListTag();
        for (ContraptionLevel.FurnitureRecord r : level.furnitureRecords()) {
            CompoundTag f = new CompoundTag();
            f.putString("id", r.definitionId());
            f.putString("variant", r.variantName());
            f.putDouble("lx", r.lx());
            f.putDouble("ly", r.ly());
            f.putDouble("lz", r.lz());
            f.putFloat("yaw", r.yaw());
            furniture.add(f);
        }
        root.put("furniture", furniture);
        return root;
    }

    public static void loadLevel(ContraptionLevel level, CompoundTag root) {
        level.setScaleFactor(root.getDouble("scale").orElse(1.0));
        var blockLookup = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        ListTag blocks = root.getListOrEmpty("blocks");
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag entry = blocks.getCompoundOrEmpty(i);
            BlockPos local = new BlockPos(
                entry.getInt("x").orElse(0),
                entry.getInt("y").orElse(0),
                entry.getInt("z").orElse(0));
            BlockState state = net.minecraft.nbt.NbtUtils.readBlockState(blockLookup, entry.getCompoundOrEmpty("state"));
            level.putBlock(local, state, true);
            if (entry.contains("be")) {
                level.putBlockEntity(local, entry.getCompoundOrEmpty("be"));
            }
        }
        if (root.contains("glue")) {
            CompoundTag glueTag = root.getCompoundOrEmpty("glue");
            long[] ga = glueTag.getLongArray("a").orElse(new long[0]);
            long[] gb = glueTag.getLongArray("b").orElse(new long[0]);
            java.util.ArrayList<long[]> edges = new java.util.ArrayList<>();
            int n = Math.min(ga.length, gb.length);
            for (int i = 0; i < n; i++) {
                edges.add(new long[] {ga[i], gb[i]});
            }
            level.setGlueEdgesLocal(edges);
        }
        if (root.contains("furniture")) {
            ListTag furniture = root.getListOrEmpty("furniture");
            java.util.ArrayList<ContraptionLevel.FurnitureRecord> records = new java.util.ArrayList<>();
            for (int i = 0; i < furniture.size(); i++) {
                CompoundTag f = furniture.getCompoundOrEmpty(i);
                records.add(new ContraptionLevel.FurnitureRecord(
                    f.getString("id").orElse(""),
                    f.getString("variant").orElse(""),
                    f.getDouble("lx").orElse(0.0),
                    f.getDouble("ly").orElse(0.0),
                    f.getDouble("lz").orElse(0.0),
                    f.getFloat("yaw").orElse(0.0f)));
            }
            level.setFurnitureRecords(records);
        }
    }

    // ─── CompoundTag ↔ compressed bytes ────────────────────────────────────────

    public static byte[] toBytes(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, baos);
        return baos.toByteArray();
    }

    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        return NbtIo.readCompressed(new ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap());
    }

    // ─── File I/O ──────────────────────────────────────────────────────────────

    public static void saveToFile(ContraptionLevel level, Path file) throws IOException {
        NbtIo.writeCompressed(dumpLevel(level), file);
    }

    public static void loadFromFile(ContraptionLevel level, Path file) throws IOException {
        loadLevel(level, NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()));
    }

    // ─── Item storage (inline vs external file) ────────────────────────────────

    private static final int MAX_INLINE_BYTES = 16 * 1024;
    private static final String KEY_FILE_ID = "contraption_file_id";
    private static final String KEY_STRUCTURE = "contraption_structure";

    /**
     * Stores structure bytes into a CustomData tag — inline if small, external file if large.
     * Caller is responsible for also putting any extra keys (block_count, anchor offsets, etc)
     * into the same tag via the returned updater pattern or after calling this.
     */
    public static void storeInItem(CompoundTag tag, byte[] bytes) throws IOException {
        if (bytes.length >= MAX_INLINE_BYTES) {
            UUID fileId = saveExternal(bytes);
            tag.putString(KEY_FILE_ID, fileId.toString());
        } else {
            tag.putByteArray(KEY_STRUCTURE, bytes);
        }
    }

    /**
     * Loads structure bytes from a CustomData tag (inline or external).
     * Returns null if tag contains neither key.
     */
    public static byte[] loadFromItem(CompoundTag tag) throws IOException {
        if (tag.contains(KEY_FILE_ID)) {
            String idStr = tag.getString(KEY_FILE_ID).orElse(null);
            if (idStr == null) return null;
            return loadExternal(UUID.fromString(idStr));
        } else if (tag.contains(KEY_STRUCTURE)) {
            return tag.getByteArray(KEY_STRUCTURE).orElse(null);
        }
        return null;
    }

    /**
     * Deletes external file if the tag used external storage. Call after successful restore.
     */
    public static void cleanupItem(CompoundTag tag) {
        if (tag.contains(KEY_FILE_ID)) {
            String idStr = tag.getString(KEY_FILE_ID).orElse(null);
            if (idStr != null) {
                deleteExternal(UUID.fromString(idStr));
            }
        }
    }

    // ─── External file helpers ─────────────────────────────────────────────────

    private static File storageDir() {
        File dir = new File(CraftEnginePolyfills.instance().getDataFolder(), "contraption_items");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static UUID saveExternal(byte[] bytes) throws IOException {
        UUID id = UUID.randomUUID();
        Files.write(new File(storageDir(), id + ".dat").toPath(), bytes);
        return id;
    }

    private static byte[] loadExternal(UUID id) throws IOException {
        File file = new File(storageDir(), id + ".dat");
        if (!file.exists()) {
            throw new IOException("Contraption file not found: " + id);
        }
        return Files.readAllBytes(file.toPath());
    }

    private static void deleteExternal(UUID id) {
        new File(storageDir(), id + ".dat").delete();
    }
}
