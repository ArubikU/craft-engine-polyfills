package dev.arubik.craftengine.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.bukkit.world.BukkitWorldManager;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Cached CraftEngine world wrappers.
 *
 * <h2>Why this exists</h2>
 * Constructing a {@code BukkitWorld} looks free and is not. Profiling the live server put a measurable
 * share of the entire server thread inside its constructor — specifically in {@code File.toPath} and
 * the platform path parser — reached from the fluid graph, which resolves a block entity per block,
 * per component, per tick. Every one of those calls built a wrapper, used it once, and dropped it.
 *
 * <p>The wrapper is worth keeping rather than rebuilding: decompiling it shows {@code storageWorld()}
 * is a lazy cache (it resolves the {@link CEWorld} once and stores it), and it holds a
 * {@code WeakReference} to the Bukkit world plus a resolved world height. Building one per call throws
 * all of that away every time and pays for it again.
 *
 * <p>This is the ONLY place that should construct one.
 */
public final class CeWorlds {

    private CeWorlds() {
    }

    private static final Map<UUID, BukkitWorld> CACHE = new ConcurrentHashMap<>();

    /**
     * The cached wrapper for {@code world}, constructed once per world.
     *
     * <p>Never hand the result somewhere that outlives the world: it is shared, not owned by the
     * caller.
     */
    public static BukkitWorld of(org.bukkit.World world) {
        if (world == null) {
            return null;
        }
        BukkitWorld cached = CACHE.get(world.getUID());
        // A reloaded world reuses its UUID but is a different object, and the wrapper only holds a weak
        // reference to the old one — so identity, not the key, decides whether the entry is still good.
        if (cached != null && cached.platformWorld() == world) {
            return cached;
        }
        BukkitWorld fresh = new BukkitWorld(world);
        CACHE.put(world.getUID(), fresh);
        return fresh;
    }

    /**
     * The {@link CEWorld} for {@code world}, straight from CraftEngine's own manager.
     *
     * <p>Equivalent to going through a wrapper's {@code storageWorld()} — that method is nothing but a
     * lazy cache over this exact call — minus the allocation and the path parsing. Prefer this when
     * only the {@code CEWorld} is wanted.
     */
    public static CEWorld ce(org.bukkit.World world) {
        return world == null ? null : BukkitWorldManager.instance().getWorld(world.getUID());
    }

    /** Drops the cache. Called on disable so a reload cannot leak wrappers pointing at dead worlds. */
    public static void clear() {
        CACHE.clear();
    }
}
