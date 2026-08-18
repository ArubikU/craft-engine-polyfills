/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 */
package dev.arubik.craftengine.contraption.persistence;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public record ContraptionManifest(UUID id, ResourceKey<Level> worldId, double x, double y, double z, double yawRadians, double scale, boolean stalled, String nbtFileName) {
    public static ContraptionManifest of(ContraptionState state, String nbtFileName) {
        return new ContraptionManifest(state.id(), state.worldId(), state.x(), state.y(), state.z(), state.yawRadians(), state.scale(), state.isStalled(), nbtFileName);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id.toString());
        tag.putString("world", this.worldId.identifier().getNamespace() + ":" + this.worldId.identifier().getPath());
        tag.putDouble("x", this.x);
        tag.putDouble("y", this.y);
        tag.putDouble("z", this.z);
        tag.putDouble("yaw", this.yawRadians);
        tag.putDouble("scale", this.scale);
        tag.putBoolean("stalled", this.stalled);
        tag.putString("nbt", this.nbtFileName);
        return tag;
    }

    public static ContraptionManifest fromTag(CompoundTag tag) {
        ResourceKey worldKey;
        String worldStr = (String)tag.getString("world").orElseThrow();
        if (worldStr.contains(":")) {
            Identifier loc = Identifier.parse((String)worldStr);
            worldKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)loc);
        } else {
            UUID worldUuid = UUID.fromString(worldStr);
            World bukkitWorld = Bukkit.getWorld((UUID)worldUuid);
            if (bukkitWorld == null) {
                throw new IllegalStateException("Cannot migrate old manifest: world " + String.valueOf(worldUuid) + " not found");
            }
            worldKey = ((CraftWorld)bukkitWorld).getHandle().dimension();
        }
        return new ContraptionManifest(UUID.fromString((String)tag.getString("id").orElseThrow()), (ResourceKey<Level>)worldKey, tag.getDouble("x").orElse(0.0), tag.getDouble("y").orElse(0.0), tag.getDouble("z").orElse(0.0), tag.getDouble("yaw").orElse(0.0), tag.getDouble("scale").orElse(1.0), tag.getBoolean("stalled").orElse(false), (String)tag.getString("nbt").orElseThrow());
    }

    public byte[] toBytes() throws IOException {
        return ContraptionStorage.toBytes(this.toTag());
    }

    public static ContraptionManifest fromBytes(byte[] bytes) throws IOException {
        return ContraptionManifest.fromTag(ContraptionStorage.fromBytes(bytes));
    }

    public void save(Path file) throws IOException {
        Files.write(file, this.toBytes(), new OpenOption[0]);
    }

    public static ContraptionManifest load(Path file) throws IOException {
        return ContraptionManifest.fromBytes(Files.readAllBytes(file));
    }
}

