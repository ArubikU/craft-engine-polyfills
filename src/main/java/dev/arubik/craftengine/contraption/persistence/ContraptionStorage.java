/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.arubik.craftengine.contraption.persistence;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

public final class ContraptionStorage {
    private static final int MAX_INLINE_BYTES = 16384;
    private static final String KEY_FILE_ID = "contraption_file_id";
    private static final String KEY_STRUCTURE = "contraption_structure";

    private ContraptionStorage() {
    }

    public static CompoundTag dumpLevel(ContraptionLevel level) {
        ListTag blocks = new ListTag();
        for (BlockPos local : level.localPositions()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", local.getX());
            entry.putInt("y", local.getY());
            entry.putInt("z", local.getZ());
            entry.put("state", (Tag)NbtUtils.writeBlockState((BlockState)level.getBlockState(local)));
            CompoundTag beTag = level.saveBlockEntity(local);
            if (beTag != null) {
                entry.put("be", (Tag)beTag);
            }
            blocks.add(entry);
        }
        CompoundTag root = new CompoundTag();
        root.put("blocks", (Tag)blocks);
        root.putDouble("scale", level.realScaleFactor());
        List<long[]> glue = level.glueEdgesLocal();
        long[] a = new long[glue.size()];
        long[] b = new long[glue.size()];
        for (int i = 0; i < glue.size(); ++i) {
            a[i] = glue.get(i)[0];
            b[i] = glue.get(i)[1];
        }
        CompoundTag glueTag = new CompoundTag();
        glueTag.putLongArray("a", a);
        glueTag.putLongArray("b", b);
        root.put("glue", (Tag)glueTag);
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
        root.put("furniture", (Tag)furniture);
        return root;
    }

    public static void loadLevel(ContraptionLevel level, CompoundTag root) {
        level.setScaleFactor(root.getDouble("scale").orElse(1.0));
        Registry blockLookup = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        ListTag blocks = root.getListOrEmpty("blocks");
        for (int i = 0; i < blocks.size(); ++i) {
            CompoundTag entry = blocks.getCompoundOrEmpty(i);
            BlockPos local = new BlockPos(entry.getInt("x").orElse(0).intValue(), entry.getInt("y").orElse(0).intValue(), entry.getInt("z").orElse(0).intValue());
            BlockState state = NbtUtils.readBlockState((HolderGetter)blockLookup, (CompoundTag)entry.getCompoundOrEmpty("state"));
            level.putBlock(local, state, true);
            if (!entry.contains("be")) continue;
            level.putBlockEntity(local, entry.getCompoundOrEmpty("be"));
        }
        if (root.contains("glue")) {
            CompoundTag glueTag = root.getCompoundOrEmpty("glue");
            long[] ga = glueTag.getLongArray("a").orElse(new long[0]);
            long[] gb = glueTag.getLongArray("b").orElse(new long[0]);
            ArrayList<long[]> edges = new ArrayList<long[]>();
            int n = Math.min(ga.length, gb.length);
            for (int i = 0; i < n; ++i) {
                edges.add(new long[]{ga[i], gb[i]});
            }
            level.setGlueEdgesLocal(edges);
        }
        if (root.contains("furniture")) {
            ListTag furniture = root.getListOrEmpty("furniture");
            ArrayList<ContraptionLevel.FurnitureRecord> records = new ArrayList<ContraptionLevel.FurnitureRecord>();
            for (int i = 0; i < furniture.size(); ++i) {
                CompoundTag f = furniture.getCompoundOrEmpty(i);
                records.add(new ContraptionLevel.FurnitureRecord(f.getString("id").orElse(""), f.getString("variant").orElse(""), f.getDouble("lx").orElse(0.0), f.getDouble("ly").orElse(0.0), f.getDouble("lz").orElse(0.0), f.getFloat("yaw").orElse(Float.valueOf(0.0f)).floatValue()));
            }
            level.setFurnitureRecords(records);
        }
    }

    public static byte[] toBytes(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NbtIo.writeCompressed((CompoundTag)tag, (OutputStream)baos);
        return baos.toByteArray();
    }

    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        return NbtIo.readCompressed((InputStream)new ByteArrayInputStream(bytes), (NbtAccounter)NbtAccounter.unlimitedHeap());
    }

    public static void saveToFile(ContraptionLevel level, Path file) throws IOException {
        NbtIo.writeCompressed((CompoundTag)ContraptionStorage.dumpLevel(level), (Path)file);
    }

    public static void loadFromFile(ContraptionLevel level, Path file) throws IOException {
        ContraptionStorage.loadLevel(level, NbtIo.readCompressed((Path)file, (NbtAccounter)NbtAccounter.unlimitedHeap()));
    }

    public static void storeInItem(CompoundTag tag, byte[] bytes) throws IOException {
        if (bytes.length >= 16384) {
            UUID fileId = ContraptionStorage.saveExternal(bytes);
            tag.putString(KEY_FILE_ID, fileId.toString());
        } else {
            tag.putByteArray(KEY_STRUCTURE, bytes);
        }
    }

    public static byte[] loadFromItem(CompoundTag tag) throws IOException {
        if (tag.contains(KEY_FILE_ID)) {
            String idStr = tag.getString(KEY_FILE_ID).orElse(null);
            if (idStr == null) {
                return null;
            }
            return ContraptionStorage.loadExternal(UUID.fromString(idStr));
        }
        if (tag.contains(KEY_STRUCTURE)) {
            return tag.getByteArray(KEY_STRUCTURE).orElse(null);
        }
        return null;
    }

    public static void cleanupItem(CompoundTag tag) {
        String idStr;
        if (tag.contains(KEY_FILE_ID) && (idStr = (String)tag.getString(KEY_FILE_ID).orElse(null)) != null) {
            ContraptionStorage.deleteExternal(UUID.fromString(idStr));
        }
    }

    private static File storageDir() {
        File dir = new File(CraftEnginePolyfills.instance().getDataFolder(), "contraption_items");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static UUID saveExternal(byte[] bytes) throws IOException {
        UUID id = UUID.randomUUID();
        Files.write(new File(ContraptionStorage.storageDir(), String.valueOf(id) + ".dat").toPath(), bytes, new OpenOption[0]);
        return id;
    }

    private static byte[] loadExternal(UUID id) throws IOException {
        File file = new File(ContraptionStorage.storageDir(), String.valueOf(id) + ".dat");
        if (!file.exists()) {
            throw new IOException("Contraption file not found: " + String.valueOf(id));
        }
        return Files.readAllBytes(file.toPath());
    }

    private static void deleteExternal(UUID id) {
        new File(ContraptionStorage.storageDir(), String.valueOf(id) + ".dat").delete();
    }
}

