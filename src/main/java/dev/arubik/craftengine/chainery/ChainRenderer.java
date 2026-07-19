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

    /** Blocks between successive links along the span — model repeats every this many blocks. */
    private static final double LINK_SPACING = 0.5;
    private static final float LINK_SCALE = 0.6f;

    /**
     * (Re)builds the display entities for {@code chain}. Despawns any it already had, then spawns a fresh
     * row between the two endpoint world-centres. Returns with {@code chain.renderEntities} repopulated.
     */
    public static void rebuild(Chain chain, World world) {
        despawn(chain, world);
        org.joml.Vector3d a = center(chain.a);
        org.joml.Vector3d b = center(chain.b);
        spawnAlong(chain, world, a, b);
    }

    /** Moves this chain's existing links onto the segment {@code a→b} (world coords), rebuilding if the
     *  link count no longer matches (endpoint moved far). */
    public static void update(Chain chain, World world, org.joml.Vector3d a, org.joml.Vector3d b) {
        int want = linkCount(a.distance(b), chain.blocks);
        if (chain.renderEntities.size() != want) {
            despawn(chain, world);
            spawnAlong(chain, world, a, b);
            return;
        }
        placeLinks(chain, world, a, b);
    }

    private static void spawnAlong(Chain chain, World world, org.joml.Vector3d a, org.joml.Vector3d b) {
        org.bukkit.inventory.ItemStack model = modelItem(chain.material.blockId());
        int count = linkCount(a.distance(b), chain.blocks);
        for (int i = 0; i < count; i++) {
            Location loc = new Location(world, a.x, a.y, a.z);
            ItemDisplay disp = world.spawn(loc, ItemDisplay.class, d -> {
                d.setItemStack(model);
                d.setBillboard(Billboard.FIXED);
                d.getPersistentDataContainer().set(LINK_TAG, PersistentDataType.STRING, chain.id.toString());
            });
            chain.renderEntities.add(disp.getUniqueId());
        }
        placeLinks(chain, world, a, b);
    }

    private static void placeLinks(Chain chain, World world, org.joml.Vector3d a, org.joml.Vector3d b) {
        int count = chain.renderEntities.size();
        if (count == 0) {
            return;
        }
        // Slack sag: how much longer the rope is than the straight gap, dropped into a shallow parabola.
        double straight = a.distance(b);
        double sag = Math.max(0.0, chain.blocks - straight) * 0.5;
        for (int i = 0; i < count; i++) {
            double t = count == 1 ? 0.5 : (double) i / (count - 1);
            double x = a.x + (b.x - a.x) * t;
            double y = a.y + (b.y - a.y) * t - sag * (4.0 * t * (1.0 - t)); // parabola, 0 at ends, max mid
            double z = a.z + (b.z - a.z) * t;
            Entity e = world.getEntity(chain.renderEntities.get(i));
            if (!(e instanceof ItemDisplay disp)) {
                continue;
            }
            disp.teleport(new Location(world, x, y, z));
            disp.setTransformation(orient(a, b));
        }
    }

    /** A transformation whose rotation maps the model's up axis onto the chain direction, scaled down. */
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

    private static int linkCount(double straight, int blocks) {
        double span = Math.max(straight, blocks); // slack rope is longer than the gap
        return Math.max(1, (int) Math.round(span / LINK_SPACING));
    }

    private static org.joml.Vector3d center(net.minecraft.core.BlockPos p) {
        return new org.joml.Vector3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
    }

    private static org.bukkit.inventory.ItemStack modelItem(String blockId) {
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
