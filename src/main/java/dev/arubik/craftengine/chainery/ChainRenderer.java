package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Dynamic render of a {@link Chain} span (CHAINERY — "usando el motor de renderizado renderizar la cadena de
 * manera dinámica"). Strings a row of {@link ItemDisplay}s showing the chain block's own model along a
 * slack catenary between the two endpoint centres, and {@link #update(Chain, World, org.joml.Vector3d,
 * org.joml.Vector3d)} repositions them each tick so a chain tied to a moving contraption follows its ends.
 *
 * <p>v1 uses vanilla display entities (robust, no dependency on the contraption swarm renderer); a chain
 * link is a small item-display oriented along its local segment, sagging by however much the rope is slack.
 */
public final class ChainRenderer {

    private ChainRenderer() {
    }

    /** PDC marker so orphaned link displays (from a crash mid-life) can be swept on enable. */
    public static final org.bukkit.NamespacedKey LINK_TAG =
            org.bukkit.NamespacedKey.fromString("polyfills:chain_link");

    private static final float LINK_SCALE = 0.6f;

    /**
     * Draws {@code chain} from its verlet {@link ChainRope}: one display link per rope segment, positioned at
     * the segment midpoint and oriented along it — so the render shows the real sag/ground-rest the sim
     * produced, and follows it every tick. Spawns/despawns links to match the segment count.
     */
    public static void syncRope(Chain chain, World world, ChainRope rope) {
        int segs = Math.max(0, rope.particleCount() - 1);
        if (segs == 0) {
            return;
        }
        if (chain.renderEntities.size() != segs) {
            despawn(chain, world);
            org.bukkit.inventory.ItemStack model = modelItem(chain.material.blockId());
            for (int i = 0; i < segs; i++) {
                org.joml.Vector3d p = rope.particle(i);
                ItemDisplay disp = world.spawn(new Location(world, p.x, p.y, p.z), ItemDisplay.class, d -> {
                    d.setItemStack(model);
                    d.setBillboard(Billboard.FIXED);
                    d.getPersistentDataContainer().set(LINK_TAG, PersistentDataType.STRING, chain.id.toString());
                });
                chain.renderEntities.add(disp.getUniqueId());
            }
        }
        for (int i = 0; i < segs; i++) {
            Entity e = world.getEntity(chain.renderEntities.get(i));
            if (!(e instanceof ItemDisplay disp)) {
                continue;
            }
            org.joml.Vector3d p0 = rope.particle(i);
            org.joml.Vector3d p1 = rope.particle(i + 1);
            disp.teleport(new Location(world, (p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0));
            disp.setTransformation(orient(p0, p1));
        }
    }

    /** A transformation whose rotation maps the model's up axis onto the segment direction, scaled down. */
    private static Transformation orient(org.joml.Vector3d a, org.joml.Vector3d b) {
        Vector3f dir = new Vector3f((float) (b.x - a.x), (float) (b.y - a.y), (float) (b.z - a.z));
        if (dir.lengthSquared() < 1.0e-6f) {
            dir.set(0, 1, 0);
        }
        dir.normalize();
        Quaternionf rot = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), dir);
        return new Transformation(new Vector3f(0, 0, 0), rot,
                new Vector3f(LINK_SCALE, LINK_SCALE, LINK_SCALE), new Quaternionf());
    }

    /** Removes every display entity of this chain (by stored UUID), clearing the handle list. */
    public static void despawn(Chain chain, World world) {
        for (UUID id : chain.renderEntities) {
            Entity e = world == null ? null : world.getEntity(id);
            if (e != null) {
                e.remove();
            }
        }
        chain.renderEntities.clear();
    }

    private static org.bukkit.inventory.ItemStack modelItem(String blockId) {
        // The rope links render as the VANILLA iron chain model (user: "para el chain base usa el modelo de
        // iron_chain vanilla"). matchMaterial dodges a mapping quirk where Material.CHAIN isn't a compile const.
        org.bukkit.Material chain = org.bukkit.Material.matchMaterial("CHAIN");
        if (chain != null) {
            return new org.bukkit.inventory.ItemStack(chain);
        }
        try {
            var def = CraftEngineItems.byId(Key.of(blockId));
            if (def != null) {
                return def.buildBukkitItem();
            }
        } catch (Throwable ignored) {
        }
        return new org.bukkit.inventory.ItemStack(org.bukkit.Material.IRON_INGOT);
    }

    /** Sweeps orphaned chain-link displays (whose chain no longer exists) across all loaded worlds. */
    public static void sweepOrphans() {
        List<UUID> live = new ArrayList<>();
        for (Chain c : ChainRegistry.all()) {
            live.addAll(c.renderEntities);
        }
        for (World w : org.bukkit.Bukkit.getWorlds()) {
            for (Entity e : w.getEntitiesByClass(ItemDisplay.class)) {
                String tag = e.getPersistentDataContainer().get(LINK_TAG, PersistentDataType.STRING);
                if (tag != null && !live.contains(e.getUniqueId())) {
                    e.remove();
                }
            }
        }
    }
}
