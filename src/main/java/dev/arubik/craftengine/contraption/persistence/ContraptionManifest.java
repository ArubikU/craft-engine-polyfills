package dev.arubik.craftengine.contraption.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import dev.arubik.craftengine.contraption.ContraptionNbt;
import dev.arubik.craftengine.contraption.ContraptionState;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Light per-contraption manifest (CONTRAPTIONS.md §1 "Persistence"): UUID, world,
 * absolute position+rotation, stalled state, and a reference to the heavy `.nbt` capture
 * blob — NOT the captured blocks themselves. Scanned cheaply on boot to re-instantiate
 * facades; the heavy blob only loads into RAM when a contraption actually needs to
 * simulate. Saved on stop/interval/{@code onDisable} only — never every tick.
 */
public record ContraptionManifest(UUID id, UUID worldId, double x, double y, double z, double yawRadians,
        boolean stalled, String nbtFileName) {

    public static ContraptionManifest of(ContraptionState state, String nbtFileName) {
        return new ContraptionManifest(state.id(), state.worldId(), state.x(), state.y(), state.z(),
                state.yawRadians(), state.isStalled(), nbtFileName);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putString("world", worldId.toString());
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        tag.putDouble("yaw", yawRadians);
        tag.putBoolean("stalled", stalled);
        tag.putString("nbt", nbtFileName);
        return tag;
    }

    public static ContraptionManifest fromTag(CompoundTag tag) {
        return new ContraptionManifest(
                UUID.fromString(tag.getString("id")),
                UUID.fromString(tag.getString("world")),
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z"),
                tag.getDouble("yaw"),
                tag.getBoolean("stalled"),
                tag.getString("nbt"));
    }

    public byte[] toBytes() throws IOException {
        return ContraptionNbt.toBytes(toTag());
    }

    public static ContraptionManifest fromBytes(byte[] bytes) throws IOException {
        return fromTag(ContraptionNbt.fromBytes(bytes));
    }

    public void save(Path file) throws IOException {
        Files.write(file, toBytes());
    }

    public static ContraptionManifest load(Path file) throws IOException {
        return fromBytes(Files.readAllBytes(file));
    }
}
