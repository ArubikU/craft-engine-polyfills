/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 */
package dev.arubik.craftengine.contraption.glue;

import dev.arubik.craftengine.contraption.glue.GlueGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public final class GlueRegistry {
    private static final Map<ResourceKey<Level>, GlueGraph> GRAPHS = new HashMap<ResourceKey<Level>, GlueGraph>();

    private GlueRegistry() {
    }

    public static GlueGraph graphFor(ResourceKey<Level> worldId) {
        return GRAPHS.computeIfAbsent(worldId, id -> new GlueGraph());
    }

    public static Set<BlockPos> structureAt(ResourceKey<Level> worldId, BlockPos pos) {
        GlueGraph graph = GRAPHS.get(worldId);
        if (graph == null || !graph.hasNode(pos)) {
            return Set.of(pos);
        }
        for (Set<BlockPos> component : graph.connectedComponents()) {
            if (!component.contains(pos)) continue;
            return component;
        }
        return Set.of(pos);
    }

    public static void clear(ResourceKey<Level> worldId) {
        GRAPHS.remove(worldId);
    }

    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag worlds = new ListTag();
        for (Map.Entry<ResourceKey<Level>, GlueGraph> entry : GRAPHS.entrySet()) {
            GlueGraph graph = entry.getValue();
            ArrayList<long[]> edges = new ArrayList<long[]>();
            for (BlockPos node : graph.nodes()) {
                long na = node.asLong();
                for (BlockPos neighbor : graph.neighbors(node)) {
                    long nb = neighbor.asLong();
                    if (na >= nb) continue;
                    edges.add(new long[]{na, nb});
                }
            }
            if (edges.isEmpty()) continue;
            long[] a = new long[edges.size()];
            long[] b = new long[edges.size()];
            for (int i = 0; i < edges.size(); ++i) {
                a[i] = ((long[])edges.get(i))[0];
                b[i] = ((long[])edges.get(i))[1];
            }
            CompoundTag w = new CompoundTag();
            w.putString("world", entry.getKey().identifier().getNamespace() + ":" + entry.getKey().identifier().getPath());
            w.putLongArray("a", a);
            w.putLongArray("b", b);
            worlds.add(w);
        }
        root.put("worlds", (Tag)worlds);
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        NbtIo.writeCompressed((CompoundTag)root, (Path)file);
    }

    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file, new LinkOption[0])) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed((Path)file, (NbtAccounter)NbtAccounter.unlimitedHeap());
        ListTag worlds = root.getListOrEmpty("worlds");
        for (int i = 0; i < worlds.size(); ++i) {
            ResourceKey worldKey;
            CompoundTag w = worlds.getCompoundOrEmpty(i);
            String worldStr = w.getString("world").orElse("");
            // saveAll always writes "namespace:path" (never a bare UUID) — this used to fall
            // through unconditionally into the UUID.fromString branch below even after already
            // resolving worldKey here, which always threw on a colon-containing string and
            // discarded the entry via `continue` before worldKey was ever used. That's why glue
            // never survived a restart: every saved world entry took this branch and was silently
            // dropped. The UUID branch is now an `else`, reached only when there's no colon.
            if (worldStr.contains(":")) {
                try {
                    Identifier loc = Identifier.parse((String)worldStr);
                    worldKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)loc);
                }
                catch (Exception e) {
                    continue;
                }
            } else {
                try {
                    UUID worldUuid = UUID.fromString(worldStr);
                    World bukkitWorld = Bukkit.getWorld((UUID)worldUuid);
                    if (bukkitWorld == null) continue;
                    worldKey = ((CraftWorld)bukkitWorld).getHandle().dimension();
                }
                catch (IllegalArgumentException bad) {
                    continue;
                }
            }
            long[] a = w.getLongArray("a").orElse(new long[0]);
            long[] b = w.getLongArray("b").orElse(new long[0]);
            GlueGraph graph = GlueRegistry.graphFor((ResourceKey<Level>)worldKey);
            int n = Math.min(a.length, b.length);
            for (int j = 0; j < n; ++j) {
                if (a[j] == b[j]) continue;
                graph.glue(BlockPos.of((long)a[j]), BlockPos.of((long)b[j]));
            }
        }
    }
}

