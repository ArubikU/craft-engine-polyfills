package dev.arubik.craftengine.fluid.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * Config-driven, packet-based fluid renderer built on CraftEngine's {@link ConstantBlockEntityElement}
 * pipeline — CraftEngine drives {@code show/hide/update} per tracked player (chunk load, join, view distance),
 * so this element only builds the packets. It renders a column of {@code minecraft:item_display} entities
 * (one per footprint cell per filled layer, the {@code fluidlvl_*} level models) sized to a width×height×length
 * box, with an arbitrary rotation about a pivot. The fluid (type + amount + max) and dimensions are resolved by
 * {@link FluidDisplayElementConfig} at create time (from the block or from explicit config values).
 */
public final class FluidDisplayElement implements ConstantBlockEntityElement {

    private final int[] entityIds;
    private final List<Object> spawnPackets; // one per slot
    private final List<Object> dataPackets;  // one per slot (static: item + scale + rotation + brightness)
    private final Object despawnPacket;      // removes all slots at once

    FluidDisplayElement(FluidDisplayElementConfig cfg, List<FluidDisplayElementConfig.Slot> slots) {
        int n = slots.size();
        this.entityIds = new int[n];
        this.spawnPackets = new ArrayList<>(n);
        this.dataPackets = new ArrayList<>(n);
        IntList ids = new IntArrayList(n);
        for (int i = 0; i < n; i++) {
            FluidDisplayElementConfig.Slot s = slots.get(i);
            int id = nextEntityId();
            entityIds[i] = id;
            ids.add(id);
            spawnPackets.add(MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    id, UUID.randomUUID(), s.x, s.y, s.z, 0f, 0f,
                    net.minecraft.world.entity.EntityType.ITEM_DISPLAY, 0,
                    net.minecraft.world.phys.Vec3.ZERO, 0));
            dataPackets.add(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(id,
                    metadata(s.nmsItem, s.scale, cfg.rotation, cfg.blockLight, cfg.skyLight)));
        }
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(ids);
    }

    private static List<Object> metadata(Object nmsItem, Vector3f scale, Quaternionf rotation,
            int blockLight, int skyLight) {
        List<Object> v = new ArrayList<>();
        if (nmsItem != null)
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, v);
        DisplayData.Scale.addEntityData(scale, v);
        DisplayData.LeftRotation.addEntityData(rotation, v);
        if (blockLight >= 0 && skyLight >= 0)
            DisplayData.BrightnessOverride.addEntityData((blockLight << 4) | (skyLight << 20), v);
        return v;
    }

    @Override
    public void showInternal(Player player) {
        List<Object> all = new ArrayList<>(spawnPackets.size() * 2);
        for (int i = 0; i < entityIds.length; i++) {
            all.add(spawnPackets.get(i));
            all.add(dataPackets.get(i));
        }
        player.sendPackets(all, false);
    }

    @Override
    public void hide(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    @Override
    public void update(Player player) {
        if (!dataPackets.isEmpty())
            player.sendPackets(new ArrayList<>(dataPackets), false);
    }

    @Override
    public boolean supportsTransform() {
        return true;
    }

    static Object toNms(org.bukkit.inventory.ItemStack bukkit) {
        return bukkit == null ? null : CraftItemStack.asNMSCopy(bukkit);
    }

    // ---- fresh server-unique fake entity ids (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            ENTITY_COUNTER = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
