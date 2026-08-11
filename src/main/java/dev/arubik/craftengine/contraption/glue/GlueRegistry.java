package dev.arubik.craftengine.contraption.glue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.level.Level;

/**
 * Per-world {@link GlueGraph} registry (CONTRAPTIONS.md §1 "Structure detection" — explicit
 * glue, not flood-fill). One graph per dimension, keyed by ResourceKey<Level> so restarts /
 * multiple worlds never cross-contaminate each other's structures.
 *
 * <p>Migration note (2026-08-10): Changed from UUID to ResourceKey<Level> for type-safe dimension
 * identification. Old files with UUID are automatically migrated on load.
 */
public final class GlueRegistry {

    private GlueRegistry() {
    }

    private static final Map<ResourceKey<Level>, GlueGraph> GRAPHS = new HashMap<>();

    public static GlueGraph graphFor(ResourceKey<Level> worldId) {
        return GRAPHS.computeIfAbsent(worldId, id -> new GlueGraph());
    }

    /** The full connected structure (glue-component) containing {@code pos}, or a singleton set if unglued. */
    public static Set<BlockPos> structureAt(ResourceKey<Level> worldId, BlockPos pos) {
        GlueGraph graph = GRAPHS.get(worldId);
        if (graph == null || !graph.hasNode(pos)) {
            return Set.of(pos);
        }
        for (Set<BlockPos> component : graph.connectedComponents()) {
            if (component.contains(pos)) {
                return component;
            }
        }
        return Set.of(pos);
    }

    /** Removes a world's glue graph entirely (e.g. on world unload), for test/administrative use. */
    public static void clear(ResourceKey<Level> worldId) {
        GRAPHS.remove(worldId);
    }

    /**
     * Persists every world's glue graph to {@code file} (2026-07-03 session — "has que las glue
     * persista al apagar o reiniciar el sv"). The in-memory {@link #GRAPHS} is otherwise lost on
     * restart, so glued-but-unassembled structures in the real world would forget their glue and
     * only capture as singletons after a reboot. Stored as one gzip'd NBT compound: a list of
     * per-world entries, each holding two parallel LongArray columns of packed {@link BlockPos}
     * edge endpoints (undirected, deduped a&lt;b). Assembled contraptions carry their OWN glue in
     * their structure NBT (see {@code ContraptionCapture#captureGlueEdges}); this covers the loose
     * world graph. Called on {@code onDisable}; the mirror {@link #loadAll} runs on {@code onEnable}.
     */
    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag worlds = new ListTag();
        for (Map.Entry<ResourceKey<Level>, GlueGraph> entry : GRAPHS.entrySet()) {
            GlueGraph graph = entry.getValue();
            List<long[]> edges = new ArrayList<>();
            for (BlockPos node : graph.nodes()) {
                long na = node.asLong();
                for (BlockPos neighbor : graph.neighbors(node)) {
                    long nb = neighbor.asLong();
                    if (na < nb) { // dedup the undirected edge (emit once)
                        edges.add(new long[] {na, nb});
                    }
                }
            }
            if (edges.isEmpty()) {
                continue;
            }
            long[] a = new long[edges.size()];
            long[] b = new long[edges.size()];
            for (int i = 0; i < edges.size(); i++) {
                a[i] = edges.get(i)[0];
                b[i] = edges.get(i)[1];
            }
            CompoundTag w = new CompoundTag();
            w.putString("world", entry.getKey().identifier().getNamespace() + ":" + entry.getKey().identifier().getPath());
            w.putLongArray("a", a);
            w.putLongArray("b", b);
            worlds.add(w);
        }
        root.put("worlds", worlds);
        Files.createDirectories(file.getParent());
        NbtIo.writeCompressed(root, file);
    }

    /**
     * Mirror of {@link #saveAll} — repopulates {@link #GRAPHS} from {@code file}.
     * No-op if the file is absent. Automatically migrates old UUID format to ResourceKey.
     */
    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        ListTag worlds = root.getListOrEmpty("worlds");
        for (int i = 0; i < worlds.size(); i++) {
            CompoundTag w = worlds.getCompoundOrEmpty(i);
            String worldStr = w.getString("world").orElse("");
            ResourceKey<Level> worldKey;

            // Migration: try parsing as Identifier first, fall back to UUID (old format)
            if (worldStr.contains(":")) {
                // New format: "minecraft:overworld"
                try {
                    Identifier loc = Identifier.parse(worldStr);
                    worldKey = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, loc);
                } catch (Exception e) {
                    continue;
                }
            } else {
                // Old format: UUID string - convert via Bukkit World lookup
                try {
                    UUID worldUuid = UUID.fromString(worldStr);
                    org.bukkit.World bukkitWorld = Bukkit.getWorld(worldUuid);
                    if (bukkitWorld == null) {
                        continue; // world not found, skip this entry
                    }
                    worldKey = ((CraftWorld) bukkitWorld).getHandle().dimension();
                } catch (IllegalArgumentException bad) {
                    continue;
                }
            }

            long[] a = w.getLongArray("a").orElse(new long[0]);
            long[] b = w.getLongArray("b").orElse(new long[0]);
            GlueGraph graph = graphFor(worldKey);
            int n = Math.min(a.length, b.length);
            for (int j = 0; j < n; j++) {
                if (a[j] != b[j]) {
                    graph.glue(BlockPos.of(a[j]), BlockPos.of(b[j]));
                }
            }
        }
    }
}
