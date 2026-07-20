package dev.arubik.craftengine.chainery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * Global cross-world registry of every placed {@link Chain} (CHAINERY), keyed by chain id, with a reverse
 * index from each endpoint {@code (world, pos)} to its chain so a block break can find and sever it in O(1).
 * Modelled on {@code GlueRegistry}: an in-memory map plus gzip'd-NBT {@link #saveAll}/{@link #loadAll} on
 * plugin disable/enable, so décor chains and contraption tethers both survive a restart.
 */
public final class ChainRegistry {

    private ChainRegistry() {
    }

    private static final Map<UUID, Chain> CHAINS = new HashMap<>();
    /** endpointKey(world,pos) -> the SET of chains anchored there (an anchor can host several chains). */
    private static final Map<String, java.util.Set<UUID>> BY_ENDPOINT = new HashMap<>();

    private static String endpointKey(UUID worldId, BlockPos pos) {
        return worldId + "|" + pos.asLong();
    }

    public static void register(Chain chain) {
        CHAINS.put(chain.id, chain);
        BY_ENDPOINT.computeIfAbsent(endpointKey(chain.worldId, chain.a), k -> new java.util.HashSet<>()).add(chain.id);
        BY_ENDPOINT.computeIfAbsent(endpointKey(chain.worldId, chain.b), k -> new java.util.HashSet<>()).add(chain.id);
    }

    public static Chain get(UUID id) {
        return CHAINS.get(id);
    }

    /** Any one chain anchored at {@code (worldId, pos)}, or null. */
    public static Chain at(UUID worldId, BlockPos pos) {
        java.util.Set<Chain> at = chainsAt(worldId, pos);
        return at.isEmpty() ? null : at.iterator().next();
    }

    /** Every chain anchored at {@code (worldId, pos)}. */
    public static java.util.Set<Chain> chainsAt(UUID worldId, BlockPos pos) {
        java.util.Set<UUID> ids = BY_ENDPOINT.get(endpointKey(worldId, pos));
        java.util.Set<Chain> out = new java.util.HashSet<>();
        if (ids != null) {
            for (UUID id : ids) {
                Chain c = CHAINS.get(id);
                if (c != null) {
                    out.add(c);
                }
            }
        }
        return out;
    }

    /** How many chains are anchored at {@code (worldId, pos)} (the per-anchor cap is enforced by callers). */
    public static int countAt(UUID worldId, BlockPos pos) {
        java.util.Set<UUID> ids = BY_ENDPOINT.get(endpointKey(worldId, pos));
        return ids == null ? 0 : ids.size();
    }

    /** The attach offset an existing chain uses at {@code (worldId, pos)}, so a REUSED anchor connects at the same
     *  point (not the centre). Null if none / centre. */
    public static org.joml.Vector3d offsetAt(UUID worldId, BlockPos pos) {
        for (Chain c : chainsAt(worldId, pos)) {
            if (c.a.equals(pos) && c.offsetA != null) {
                return c.offsetA;
            }
            if (c.b.equals(pos) && c.offsetB != null) {
                return c.offsetB;
            }
        }
        return null;
    }

    /** Whether a chain already runs directly between anchors {@code a} and {@code b} (prevents duplicate edges). */
    public static boolean existsBetween(UUID worldId, BlockPos a, BlockPos b) {
        for (Chain c : chainsAt(worldId, a)) {
            if ((c.a.equals(a) && c.b.equals(b)) || (c.a.equals(b) && c.b.equals(a))) {
                return true;
            }
        }
        return false;
    }

    /** Removes a chain from the registry (both endpoint index entries too). Does NOT touch the world. */
    public static Chain remove(UUID id) {
        Chain chain = CHAINS.remove(id);
        if (chain != null) {
            unindex(endpointKey(chain.worldId, chain.a), id);
            unindex(endpointKey(chain.worldId, chain.b), id);
        }
        return chain;
    }

    private static void unindex(String key, UUID id) {
        java.util.Set<UUID> ids = BY_ENDPOINT.get(key);
        if (ids != null) {
            ids.remove(id);
            if (ids.isEmpty()) {
                BY_ENDPOINT.remove(key);
            }
        }
    }

    /** Snapshot of every live chain — safe to iterate while chains are being removed. */
    public static Collection<Chain> all() {
        return new ArrayList<>(CHAINS.values());
    }

    // ---- persistence (mirror of GlueRegistry.saveAll/loadAll) ----

    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (Chain chain : CHAINS.values()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", chain.id.toString());
            c.putString("world", chain.worldId.toString());
            c.putLong("a", chain.a.asLong());
            c.putLong("b", chain.b.asLong());
            if (chain.offsetA != null) {
                c.putDouble("offAx", chain.offsetA.x);
                c.putDouble("offAy", chain.offsetA.y);
                c.putDouble("offAz", chain.offsetA.z);
            }
            if (chain.offsetB != null) {
                c.putDouble("offBx", chain.offsetB.x);
                c.putDouble("offBy", chain.offsetB.y);
                c.putDouble("offBz", chain.offsetB.z);
            }
            c.putInt("blocks", chain.blocks);
            c.putString("mat_anchor", chain.material.anchorBlock());
            c.putString("mat_link", chain.material.linkItem());
            c.putInt("mat_max", chain.material.maxBlocks());
            c.putDouble("mat_stretch", chain.material.stretch());
            c.putDouble("mat_tension", chain.material.maxTension());
            c.putDouble("mat_pull", chain.material.pull());
            if (chain.contraptionA != null && chain.localA != null) {
                c.putString("conA", chain.contraptionA.toString());
                c.putLong("locA", chain.localA.asLong());
            }
            if (chain.contraptionB != null && chain.localB != null) {
                c.putString("conB", chain.contraptionB.toString());
                c.putLong("locB", chain.localB.asLong());
            }
            if (!chain.data.isEmpty()) {
                c.put("data", chain.data.copy());
            }
            list.add(c);
        }
        root.put("chains", list);
        Files.createDirectories(file.getParent());
        NbtIo.writeCompressed(root, file);
    }

    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        ListTag list = root.getListOrEmpty("chains");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompoundOrEmpty(i);
            try {
                UUID id = UUID.fromString(c.getString("id").orElseThrow());
                UUID world = UUID.fromString(c.getString("world").orElseThrow());
                BlockPos a = BlockPos.of(c.getLong("a").orElse(0L));
                BlockPos b = BlockPos.of(c.getLong("b").orElse(0L));
                org.joml.Vector3d offA = c.contains("offAx") ? new org.joml.Vector3d(
                        c.getDouble("offAx").orElse(0.0), c.getDouble("offAy").orElse(0.0),
                        c.getDouble("offAz").orElse(0.0)) : null;
                org.joml.Vector3d offB = c.contains("offBx") ? new org.joml.Vector3d(
                        c.getDouble("offBx").orElse(0.0), c.getDouble("offBy").orElse(0.0),
                        c.getDouble("offBz").orElse(0.0)) : null;
                int blocks = c.getInt("blocks").orElse(0);
                ChainMaterial mat = new ChainMaterial(
                        c.getString("mat_anchor").orElse(ChainMaterial.DEFAULT.anchorBlock()),
                        c.getString("mat_link").orElse(ChainMaterial.DEFAULT.linkItem()),
                        c.getInt("mat_max").orElse(ChainMaterial.DEFAULT.maxBlocks()),
                        c.getDouble("mat_stretch").orElse(ChainMaterial.DEFAULT.stretch()),
                        c.getDouble("mat_tension").orElse(ChainMaterial.DEFAULT.maxTension()),
                        c.getDouble("mat_pull").orElse(ChainMaterial.DEFAULT.pull()));
                CompoundTag data = c.getCompound("data").orElseGet(CompoundTag::new);
                Chain chain = new Chain(id, world, a, b, offA, offB, mat, blocks, data);
                if (c.contains("conA")) {
                    chain.contraptionA = UUID.fromString(c.getString("conA").orElseThrow());
                    chain.localA = BlockPos.of(c.getLong("locA").orElse(0L));
                }
                if (c.contains("conB")) {
                    chain.contraptionB = UUID.fromString(c.getString("conB").orElseThrow());
                    chain.localB = BlockPos.of(c.getLong("locB").orElse(0L));
                }
                register(chain);
            } catch (Throwable bad) {
                // skip a corrupt entry rather than abort the whole load
            }
        }
    }

    /** For diagnostics/tests: number of live chains. */
    public static int size() {
        return CHAINS.size();
    }

    static List<Chain> snapshot() {
        return new ArrayList<>(CHAINS.values());
    }
}
