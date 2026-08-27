/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 */
package dev.arubik.craftengine.contraption.persistence;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.RealMotorLink;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public final class BlockAnchoredContraptionStore {
    private static final Map<UUID, Record> INDEX = new HashMap<UUID, Record>();
    private static volatile ExecutorService writer = BlockAnchoredContraptionStore.newWriter();
    private static final Map<UUID, CompoundTag> PENDING = new ConcurrentHashMap<UUID, CompoundTag>();
    private static final Map<UUID, CompoundTag> LAST_WRITTEN = new ConcurrentHashMap<UUID, CompoundTag>();

    private BlockAnchoredContraptionStore() {
    }

    private static ExecutorService newWriter() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "cep-contraption-writer");
            t.setDaemon(true);
            return t;
        });
    }

    private static Path dir() {
        return CraftEnginePolyfills.instance().getDataFolder().toPath().resolve("contraptions");
    }

    private static Path fileFor(UUID id) {
        return BlockAnchoredContraptionStore.dir().resolve(id.toString() + ".dat");
    }

    public static Key typeToPersist(ContraptionState state, Level level, BlockPos anchor) {
        Key recorded;
        Key key = recorded = state == null ? null : state.bearingType();
        if (recorded != null) {
            return recorded;
        }
        Key fromBlock = level == null ? null : BearingBlockBehavior.typeAt(level, anchor);
        return fromBlock != null ? fromBlock : Key.of((String)"polyfills", (String)"rotational");
    }

    public static void save(ContraptionState state, BlockPos bearingPos, Key type, double rpm, double suPerBlock) {
        CompoundTag root;
        if (state == null || state.level() == null) {
            return;
        }
        UUID id = state.id();
        try {
            root = new CompoundTag();
            root.putString("id", id.toString());
            root.putString("world", state.worldId().identifier().getNamespace() + ":" + state.worldId().identifier().getPath());
            root.putInt("bx", bearingPos.getX());
            root.putInt("by", bearingPos.getY());
            root.putInt("bz", bearingPos.getZ());
            root.putDouble("x", state.x());
            root.putDouble("y", state.y());
            root.putDouble("z", state.z());
            root.putDouble("yaw", state.yawRadians());
            root.putDouble("pitch", state.pitchRadians());
            root.putDouble("roll", state.rollRadians());
            root.putBoolean("stalled", state.isStalled());
            root.putString("type", type.namespace() + ":" + type.value());
            root.putDouble("rpm", rpm);
            root.putDouble("su", suPerBlock);
            root.putDouble("explosionProof", state.explosionProof());
            root.put("structure", (Tag)ContraptionStorage.dumpLevel(state.level()));
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to serialize block-anchored contraption " + String.valueOf(id) + ": " + String.valueOf(t));
            return;
        }
        INDEX.put(id, new Record(id, state.worldId(), bearingPos, state.x(), state.y(), state.z(), state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.isStalled(), type, rpm, suPerBlock, state.explosionProof()));
        if (root.equals(LAST_WRITTEN.get(id))) {
            return;
        }
        LAST_WRITTEN.put(id, root);
        Path file = BlockAnchoredContraptionStore.fileFor(id);
        PENDING.put(id, root);
        writer.execute(() -> BlockAnchoredContraptionStore.writeNow(id, file, root));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void writeNow(UUID id, Path file, CompoundTag root) {
        try {
            Files.createDirectories(file.getParent(), new FileAttribute[0]);
            Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
            NbtIo.writeCompressed((CompoundTag)root, (Path)tmp);
            try {
                Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (AtomicMoveNotSupportedException noAtomic) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to write block-anchored contraption " + String.valueOf(id) + ": " + String.valueOf(t));
            LAST_WRITTEN.remove(id, root);
        }
        finally {
            PENDING.remove(id, root);
        }
    }

    public static void flush() {
        ExecutorService w = writer;
        w.shutdown();
        try {
            if (!w.awaitTermination(30L, TimeUnit.SECONDS)) {
                CraftEnginePolyfills.instance().getLogger().warning("[Contraption] block-anchored writer did not drain within 30s; forcing stop (" + PENDING.size() + " write(s) may be incomplete).");
                w.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            w.shutdownNow();
        }
        writer = BlockAnchoredContraptionStore.newWriter();
    }

    public static void delete(UUID id) {
        INDEX.remove(id);
        LAST_WRITTEN.remove(id);
        PENDING.remove(id);
        Path file = BlockAnchoredContraptionStore.fileFor(id);
        writer.execute(() -> {
            try {
                Files.deleteIfExists(file);
                Files.deleteIfExists(file.resolveSibling(file.getFileName().toString() + ".tmp"));
            }
            catch (Throwable t) {
                CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to delete block-anchored contraption file " + String.valueOf(id) + ": " + String.valueOf(t));
            }
        });
    }

    public static void loadIndex() {
        INDEX.clear();
        Path dir = BlockAnchoredContraptionStore.dir();
        if (!Files.isDirectory(dir, new LinkOption[0])) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.dat");){
            for (Path file : stream) {
                try {
                    CompoundTag root = NbtIo.readCompressed((Path)file, (NbtAccounter)NbtAccounter.unlimitedHeap());
                    Record rec = BlockAnchoredContraptionStore.readManifest(root);
                    if (rec == null) continue;
                    INDEX.put(rec.id(), rec);
                }
                catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().warning("[Contraption] skipping unreadable block-anchored file " + String.valueOf(file) + ": " + String.valueOf(t));
                }
            }
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to scan block-anchored contraption dir: " + String.valueOf(t));
        }
        if (!INDEX.isEmpty()) {
            CraftEnginePolyfills.instance().getLogger().info("[Contraption] indexed " + INDEX.size() + " persisted block-anchored contraption(s).");
        }
    }

    private static Record readManifest(CompoundTag root) {
        Key type;
        UUID id = UUID.fromString((String)root.getString("id").orElseThrow());
        String worldStr = (String)root.getString("world").orElseThrow();
        // A save() bug (fixed) once wrote ResourceKey#toString()'s own debug format here instead of
        // a plain "namespace:path" identifier — e.g. "ResourceKey[minecraft:dimension / minecraft:overworld]" —
        // which Identifier.parse can never accept. Recover the identifier out of that old broken
        // shape rather than discarding the whole save (and the machine it describes) on every boot.
        if (worldStr.startsWith("ResourceKey[") && worldStr.contains("/")) {
            String afterSlash = worldStr.substring(worldStr.indexOf('/') + 1).trim();
            if (afterSlash.endsWith("]")) afterSlash = afterSlash.substring(0, afterSlash.length() - 1).trim();
            worldStr = afterSlash;
        }
        Identifier worldLoc = Identifier.parse((String)worldStr);
        ResourceKey world = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)worldLoc);
        BlockPos bearingPos = new BlockPos(root.getInt("bx").orElse(0).intValue(), root.getInt("by").orElse(0).intValue(), root.getInt("bz").orElse(0).intValue());
        String typeStr = root.getString("type").orElse("ROTATIONAL");
        if (typeStr.contains(":")) {
            String[] parts = typeStr.split(":", 2);
            type = Key.of((String)parts[0], (String)parts[1]);
        } else {
            type = Key.of((String)"polyfills", (String)typeStr.toLowerCase());
        }
        return new Record(id, (ResourceKey<Level>)world, bearingPos, root.getDouble("x").orElse(0.0), root.getDouble("y").orElse(0.0), root.getDouble("z").orElse(0.0), root.getDouble("yaw").orElse(0.0), root.getDouble("pitch").orElse(0.0), root.getDouble("roll").orElse(0.0), root.getBoolean("stalled").orElse(false), type, root.getDouble("rpm").orElse(0.0), root.getDouble("su").orElse(0.0), root.getDouble("explosionProof").orElse(0.0));
    }

    public static void rehydrateChunk(World world, int chunkX, int chunkZ) {
        ServerLevel realLevel = ((CraftWorld)world).getHandle();
        ResourceKey worldKey = realLevel.dimension();
        ArrayList<Record> hits = new ArrayList<Record>();
        for (Record rec : INDEX.values()) {
            if (!rec.worldId().equals(worldKey) || rec.bearingPos().getX() >> 4 != chunkX || rec.bearingPos().getZ() >> 4 != chunkZ) continue;
            hits.add(rec);
        }
        for (Record rec : hits) {
            BlockAnchoredContraptionStore.rehydrate(world, rec);
        }
    }

    public static void rehydrate(World world, Record rec) {
        CompoundTag structure;
        if (ContraptionManager.get(rec.id()) != null) {
            return;
        }
        ServerLevel realLevel = ((CraftWorld)world).getHandle();
        CompoundTag pending = PENDING.get(rec.id());
        if (pending != null) {
            structure = pending.getCompoundOrEmpty("structure");
        } else {
            try {
                CompoundTag root = NbtIo.readCompressed((Path)BlockAnchoredContraptionStore.fileFor(rec.id()), (NbtAccounter)NbtAccounter.unlimitedHeap());
                structure = root.getCompoundOrEmpty("structure");
            }
            catch (Throwable t) {
                CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to read block-anchored structure for " + String.valueOf(rec.id()) + ": " + String.valueOf(t));
                return;
            }
        }
        try {
            ContraptionLevel level = ContraptionLevel.create((Level)realLevel, rec.x(), rec.y(), rec.z(), 0.0);
            ContraptionStorage.loadLevel(level, structure);
            ContraptionState state = new ContraptionState(rec.id(), rec.worldId(), level, rec.x(), rec.y(), rec.z());
            state.setYawRadians(rec.yawRadians());
            state.setPitchRadians(rec.pitchRadians());
            state.setRollRadians(rec.rollRadians());
            state.setScale(level.realScaleFactor());
            state.setStalled(rec.stalled());
            state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
            for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
                state.addBehavior(autoBehavior);
            }
            BlockPos motorPos = RealMotorLink.findAdjacentMotor((Level)realLevel, rec.bearingPos());
            ContraptionType typeImpl = ContraptionTypeRegistry.get(rec.type());
            if (typeImpl != null) {
                typeImpl.attachBehaviors(state, (Level)realLevel, rec.bearingPos());
            } else {
                ContraptionAssembler.attachDefaultBehavior((Level)realLevel, rec.bearingPos(), state, rec.type(), rec.rpm(), rec.suPerBlock());
            }
            state.setExplosionProof(rec.explosionProof());
            ContraptionManager.register(new ContraptionEntity(state));
            BearingHammerListener.markAssembled(rec.worldId(), rec.bearingPos(), rec.id());
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to rehydrate block-anchored contraption " + String.valueOf(rec.id()) + ": " + String.valueOf(t));
        }
    }

    public record Record(UUID id, ResourceKey<Level> worldId, BlockPos bearingPos, double x, double y, double z, double yawRadians, double pitchRadians, double rollRadians, boolean stalled, Key type, double rpm, double suPerBlock, double explosionProof) {
    }
}

