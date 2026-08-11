package dev.arubik.craftengine.contraption.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.level.Level;

/**
 * Light per-contraption manifest (CONTRAPTIONS.md §1 "Persistence"): UUID, world,
 * absolute position+rotation, stalled state, and a reference to the heavy `.nbt` capture
 * blob — NOT the captured blocks themselves. Scanned cheaply on boot to re-instantiate
 * facades; the heavy blob only loads into RAM when a contraption actually needs to
 * simulate. Saved on stop/interval/{@code onDisable} only — never every tick.
 *
 * <p>Migration note (2026-08-10): {@code worldId} changed from Bukkit UUID to NMS ResourceKey.
 * Serialized as ResourceLocation string (e.g. "minecraft:overworld"). Old files with UUID are
 * automatically converted via Bukkit.getWorld(uuid).getHandle().dimension() on load.
 */
public record ContraptionManifest(UUID id, ResourceKey<Level> worldId, double x, double y, double z, double yawRadians,
        double scale, boolean stalled, String nbtFileName) {

    public static ContraptionManifest of(ContraptionState state, String nbtFileName) {
        return new ContraptionManifest(state.id(), state.worldId(), state.x(), state.y(), state.z(),
                state.yawRadians(), state.scale(), state.isStalled(), nbtFileName);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putString("world", worldId.identifier().getNamespace() + ":" + worldId.identifier().getPath());
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        tag.putDouble("yaw", yawRadians);
        // Per-contraption uniform SCALE (roadmap item #9). Persisted so a scaled contraption survives a
        // restart at its rendered size; absent in files written before this field existed, which
        // fromTag defaults to 1.0 (an old contraption loads back exactly un-scaled, as it was saved).
        tag.putDouble("scale", scale);
        tag.putBoolean("stalled", stalled);
        tag.putString("nbt", nbtFileName);
        return tag;
    }

    public static ContraptionManifest fromTag(CompoundTag tag) {
        String worldStr = tag.getString("world").orElseThrow();
        ResourceKey<Level> worldKey;

        // Migration: try parsing as Identifier first, fall back to UUID (old format)
        if (worldStr.contains(":")) {
            // New format: "minecraft:overworld"
            Identifier loc = Identifier.parse(worldStr);
            worldKey = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, loc);
        } else {
            // Old format: UUID string - convert via Bukkit World lookup
            UUID worldUuid = UUID.fromString(worldStr);
            org.bukkit.World bukkitWorld = org.bukkit.Bukkit.getWorld(worldUuid);
            if (bukkitWorld == null) {
                throw new IllegalStateException("Cannot migrate old manifest: world " + worldUuid + " not found");
            }
            worldKey = ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle().dimension();
        }

        return new ContraptionManifest(
                UUID.fromString(tag.getString("id").orElseThrow()),
                worldKey,
                tag.getDouble("x").orElse(0.0),
                tag.getDouble("y").orElse(0.0),
                tag.getDouble("z").orElse(0.0),
                tag.getDouble("yaw").orElse(0.0),
                tag.getDouble("scale").orElse(1.0), // absent in pre-scale files → un-scaled
                tag.getBoolean("stalled").orElse(false),
                tag.getString("nbt").orElseThrow());
    }

    public byte[] toBytes() throws IOException {
        return ContraptionStorage.toBytes(toTag());
    }

    public static ContraptionManifest fromBytes(byte[] bytes) throws IOException {
        return fromTag(ContraptionStorage.fromBytes(bytes));
    }

    public void save(Path file) throws IOException {
        Files.write(file, toBytes());
    }

    public static ContraptionManifest load(Path file) throws IOException {
        return fromBytes(Files.readAllBytes(file));
    }
}
