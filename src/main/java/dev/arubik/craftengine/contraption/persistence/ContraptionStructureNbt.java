package dev.arubik.craftengine.contraption.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * The heavy `.nbt` half of CONTRAPTIONS.md §1 "Persistence": a captured
 * {@link ContraptionLevel}'s CURRENT live block/block-entity state (not a frozen snapshot
 * from capture time — a furnace that's burned down since capture saves burned-down, exactly
 * like a normal chunk save would), dumped to/loaded from vanilla {@code NbtIo.writeCompressed}
 * — the same gzip'd compound-tag format a region file uses, per the doc's own wording.
 * Kept in plain vanilla {@link net.minecraft.nbt.CompoundTag} throughout (not CraftEngine's
 * own NBT type, unlike {@link dev.arubik.craftengine.contraption.ContraptionNbt}'s light
 * manifest wrapper) since every value here already comes from vanilla
 * {@code BlockState}/{@code BlockEntity} APIs — no cross-library conversion needed.
 */
public final class ContraptionStructureNbt {

    private ContraptionStructureNbt() {
    }

    /** Dumps every currently-occupied local position's live block state + block-entity NBT (if any). */
    public static CompoundTag dump(ContraptionLevel level) {
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
        // Internal glue topology, LOCAL coords (2026-07-03 — "persistir los glue block en el nbt
        // del contraption"). Two parallel LongArray columns of packed BlockPos endpoints so the
        // glue survives restart in the bearing's saved NBT (the in-memory GlueRegistry does not);
        // restored to the world graph on disassemble — see ContraptionAssembler#restoreGlue.
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
        // Captured CraftEngine furniture metadata (2026-07-03 — "todos los craft engine furnitures
        // se pierden al reiniciar el sv"). The live furniture objects live in this throwaway,
        // never-saved dimension; their definition/variant/local-offset/yaw are persisted here so a
        // rehydrate can re-place them — see ContraptionFurnitureCapture#restoreIntoFakeLevel.
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

    /** Repopulates a freshly-created (empty) {@link ContraptionLevel} from a dumped tag — the mirror of {@link #dump}. */
    public static void load(ContraptionLevel level, CompoundTag root) {
        var blockLookup = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        ListTag blocks = root.getListOrEmpty("blocks");
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag entry = blocks.getCompoundOrEmpty(i);
            BlockPos local = new BlockPos(entry.getInt("x").orElse(0), entry.getInt("y").orElse(0), entry.getInt("z").orElse(0));
            BlockState state = net.minecraft.nbt.NbtUtils.readBlockState(blockLookup, entry.getCompoundOrEmpty("state"));
            // quiet=true -- same batch-placement support-pop hazard as ContraptionCapture#capture
            // (list iteration order isn't guaranteed to match structural dependency order either).
            level.putBlock(local, state, true);
            if (entry.contains("be")) {
                level.putBlockEntity(local, entry.getCompoundOrEmpty("be"));
            }
        }
        // Glue topology (mirror of dump) — see the "glue" write in #dump.
        if (root.contains("glue")) {
            CompoundTag glueTag = root.getCompoundOrEmpty("glue");
            long[] a = glueTag.getLongArray("a").orElse(new long[0]);
            long[] b = glueTag.getLongArray("b").orElse(new long[0]);
            java.util.List<long[]> edges = new java.util.ArrayList<>();
            int n = Math.min(a.length, b.length);
            for (int i = 0; i < n; i++) {
                edges.add(new long[] {a[i], b[i]});
            }
            level.setGlueEdgesLocal(edges);
        }
        // Furniture metadata (mirror of dump).
        if (root.contains("furniture")) {
            ListTag furniture = root.getListOrEmpty("furniture");
            java.util.List<ContraptionLevel.FurnitureRecord> records = new java.util.ArrayList<>();
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

    public static void save(ContraptionLevel level, Path file) throws IOException {
        NbtIo.writeCompressed(dump(level), file);
    }

    public static void load(ContraptionLevel level, Path file) throws IOException {
        load(level, NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()));
    }

    /**
     * Compressed byte-blob round-trip (mirrors {@link dev.arubik.craftengine.contraption.ContraptionNbt}'s
     * convention for the CE-library NBT type) — used by {@link dev.arubik.craftengine.contraption.MinecartBearing}
     * to embed a dumped structure inside a real vanilla entity's Bukkit
     * {@code PersistentDataContainer}, whose values are typed primitives/byte-arrays only
     * (a raw {@link CompoundTag} can't be stored directly).
     */
    public static byte[] toBytes(CompoundTag tag) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, baos);
        return baos.toByteArray();
    }

    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        return NbtIo.readCompressed(new java.io.ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap());
    }
}
