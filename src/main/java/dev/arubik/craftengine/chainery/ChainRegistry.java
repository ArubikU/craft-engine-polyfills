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
            c.putByte("faceA", (byte) (chain.faceA == null ? -1 : chain.faceA.get3DDataValue()));
            c.putByte("faceB", (byte) (chain.faceB == null ? -1 : chain.faceB.get3DDataValue()));
            c.putInt("blocks", chain.blocks);
            c.putString("mat_anchor", chain.material.anchorBlock());
            c.putString("mat_link", chain.material.linkItem());
            c.putInt("mat_max", chain.material.maxBlocks());
            c.putDouble("mat_stretch", chain.material.stretch());
            c.putDouble("mat_tension", chain.material.maxTension());
            c.putDouble("mat_pull", chain.material.pull());
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
                net.minecraft.core.Direction faceA = faceFrom(c.getByte("faceA").orElse((byte) -1));
                net.minecraft.core.Direction faceB = faceFrom(c.getByte("faceB").orElse((byte) -1));
                int blocks = c.getInt("blocks").orElse(0);
                ChainMaterial mat = new ChainMaterial(
                        c.getString("mat_anchor").orElse(ChainMaterial.DEFAULT.anchorBlock()),
                        c.getString("mat_link").orElse(ChainMaterial.DEFAULT.linkItem()),
                        c.getInt("mat_max").orElse(ChainMaterial.DEFAULT.maxBlocks()),
                        c.getDouble("mat_stretch").orElse(ChainMaterial.DEFAULT.stretch()),
                        c.getDouble("mat_tension").orElse(ChainMaterial.DEFAULT.maxTension()),
                        c.getDouble("mat_pull").orElse(ChainMaterial.DEFAULT.pull()));
                CompoundTag data = c.getCompound("data").orElseGet(CompoundTag::new);
                register(new Chain(id, world, a, b, faceA, faceB, mat, blocks, data));
            } catch (Throwable bad) {
                // skip a corrupt entry rather than abort the whole load
            }
        }
    }

    /** Decodes a persisted 3D-data face value, or null for the -1 sentinel (no face / centre). */
    private static net.minecraft.core.Direction faceFrom(byte v) {
        return v < 0 ? null : net.minecraft.core.Direction.from3DDataValue(v);
    }

    /** For diagnostics/tests: number of live chains. */
    public static int size() {
        return CHAINS.size();
    }

    static List<Chain> snapshot() {
        return new ArrayList<>(CHAINS.values());
    }
}
