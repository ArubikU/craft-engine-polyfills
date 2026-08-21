package dev.arubik.craftengine.machine.render;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tracks the item_display entity IDs spawned by a {@link RendererSpec.ProgrammaticSpec}.
 * One state instance per ProgrammaticSpec per machine.
 */
public final class ProgrammaticRendererState {

    private final List<UUID> entityIds = new CopyOnWriteArrayList<>();

    public List<UUID> entityIds() { return entityIds; }

    public void addEntity(UUID id) { entityIds.add(id); }

    /** Remove all tracked display entities from the world. */
    public void clear(net.minecraft.server.level.ServerLevel level) {
        for (UUID id : entityIds) {
            try {
                net.minecraft.world.entity.Entity e = level.getEntity(id);
                if (e != null) e.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            } catch (Throwable ignored) {}
        }
        entityIds.clear();
    }

    /** Spawn a standalone ephemeral item_display at worldPos (for script-driven programmatic renderers). */
    public static net.minecraft.world.entity.Display.ItemDisplay spawnItemDisplay(
            net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.phys.Vec3 worldPos,
            String itemId,
            float scale) {
        try {
            net.minecraft.world.entity.Display.ItemDisplay display =
                new net.minecraft.world.entity.Display.ItemDisplay(
                    net.minecraft.world.entity.EntityType.ITEM_DISPLAY, level);
            display.setPos(worldPos.x, worldPos.y, worldPos.z);
            display.setNoGravity(true);
            // Set item
            net.minecraft.resources.Identifier id2 = net.minecraft.resources.Identifier.parse(
                itemId.contains(":") ? itemId : "minecraft:" + itemId);
            net.minecraft.world.item.Item item = (net.minecraft.world.item.Item)
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(id2);
            if (item != null) display.setItemStack(new net.minecraft.world.item.ItemStack(item));
            // Set scale
            if (scale != 1.0f) {
                display.setTransformation(new com.mojang.math.Transformation(
                    null, null, new org.joml.Vector3f(scale, scale, scale), null));
            }
            level.addFreshEntity(display);
            return display;
        } catch (Throwable ignored) { return null; }
    }

    /** Update or spawn a single item_display entity at the given world position. */
    public net.minecraft.world.entity.Display.ItemDisplay getOrSpawn(
            net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.phys.Vec3 worldPos,
            int index) {
        if (index < entityIds.size()) {
            UUID id = entityIds.get(index);
            net.minecraft.world.entity.Entity existing = level.getEntity(id);
            if (existing instanceof net.minecraft.world.entity.Display.ItemDisplay d) return d;
            entityIds.remove(index); // stale — fall through to spawn
        }
        // Spawn new item_display
        net.minecraft.world.entity.Display.ItemDisplay display =
            new net.minecraft.world.entity.Display.ItemDisplay(
                net.minecraft.world.entity.EntityType.ITEM_DISPLAY, level);
        display.setPos(worldPos.x, worldPos.y, worldPos.z);
        display.setNoGravity(true);
        level.addFreshEntity(display);
        if (index >= entityIds.size()) entityIds.add(display.getUUID());
        else entityIds.set(index, display.getUUID());
        return display;
    }
}
