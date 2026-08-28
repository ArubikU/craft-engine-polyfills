package dev.arubik.craftengine.debug;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Keeps chunks loaded and ticking with no player present, so machine ticks, action scripts and
 * renderers can be exercised — and profiled — without a human standing there.
 *
 * <h2>Why not a fake player</h2>
 * {@code FakePlayerUtil} already builds a {@code ServerPlayer}, but it says what it is: "NOT
 * registered with the server — create, use, discard". An unregistered player holds no chunk ticket
 * and is nobody's viewer, so it loads nothing and ticks nothing. Making one that DOES requires
 * putting a synthetic connection through {@code PlayerList#placeNewPlayer}, which is
 * version-specific, and leaves a half-real player that every other plugin can see and act on.
 *
 * <p>Forced chunks are the supported mechanism for exactly this. They make block entities tick,
 * which is what runs {@code action_script} and the renderer pass — the parts worth profiling. What
 * they do NOT reproduce is anything needing a real VIEWER: display entities are sent to players, so
 * a renderer's per-player work is still only exercised with someone actually looking. That limit is
 * real and this class does not pretend otherwise.
 *
 * <p>Written against Bukkit's {@code World} rather than NMS deliberately — chunk forcing is one of
 * the few things the stable API does exactly as well, and a debug tool is the last place worth
 * paying a version-coupling cost.
 */
public final class DebugLoader {

    private DebugLoader() {}

    private record Chunk(String world, int x, int z) {}

    /** Only what THIS class forced, so releasing cannot unload something another system holds. */
    private static final Set<Chunk> FORCED = new LinkedHashSet<>();

    /**
     * Forces every chunk within {@code radius} chunks of the block at {@code (bx, bz)}. Returns how
     * many were NEWLY forced — one already forced by someone else is left alone and not recorded,
     * so a later release leaves it exactly as it was found.
     */
    public static synchronized int force(World world, int bx, int bz, int radius) {
        int cx0 = bx >> 4, cz0 = bz >> 4;
        int added = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int cx = cx0 + dx, cz = cz0 + dz;
                if (world.isChunkForceLoaded(cx, cz)) continue;
                world.setChunkForceLoaded(cx, cz, true);
                FORCED.add(new Chunk(world.getName(), cx, cz));
                added++;
            }
        }
        return added;
    }

    /** Releases only what {@link #force} forced. Returns how many were released. */
    public static synchronized int releaseAll() {
        int released = 0;
        for (Chunk c : new LinkedHashSet<>(FORCED)) {
            World w = Bukkit.getWorld(c.world());
            if (w != null) {
                w.setChunkForceLoaded(c.x(), c.z(), false);
                released++;
            }
            FORCED.remove(c);
        }
        return released;
    }

    public static synchronized int forcedCount() { return FORCED.size(); }
}
