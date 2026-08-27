package dev.arubik.craftengine.util.plugins;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bridge to WorldEdit's session/editing API. {@code worldedit-bukkit} is a {@code compileOnly}
 * dependency (EngineHub's own repo — see build.gradle.kts); every call is gated behind {@link
 * #isAvailable()} and wrapped in {@code catch (Throwable)}, so this addon compiles and runs fine
 * on a server with no WorldEdit installed.
 *
 * <p>Works unchanged against FastAsyncWorldEdit (FAWE) too — FAWE IS WorldEdit (it replaces the
 * plugin entirely rather than sitting alongside it) and implements this exact same {@code
 * com.sk89q.worldedit.*} API, just with a faster queue-based {@link EditSession} under the hood;
 * {@link #isAvailable()} checks for FAWE's plugin name as well since a FAWE-only server has no
 * separate "WorldEdit" plugin loaded at all.
 */
public final class WorldEditSupport {

    private static volatile Boolean available;

    private WorldEditSupport() {}

    public static void reset() {
        available = null;
    }

    public static boolean isAvailable() {
        Boolean result = available;
        if (result == null) {
            result = Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                    || Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
            available = result;
        }
        return result;
    }

    /** True if the FAWE build is what's actually answering {@link #isAvailable()} (its own plugin,
     *  not the plain WorldEdit jar) — a script wanting to warn/branch on "no fast async backend
     *  installed" can check this instead of assuming one or the other. */
    public static boolean isFawe() {
        try { return Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit"); }
        catch (Throwable ignored) { return false; }
    }

    private static LocalSession session(Player player) {
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        return WorldEdit.getInstance().getSessionManager().get(wePlayer);
    }

    /** {@code player}'s current WorldEdit selection, as {@code {world, min:{x,y,z}, max:{x,y,z}}}
     *  (plain doubles — no dedicated script Location type needed for this). Null if WorldEdit is
     *  absent or the player has no complete selection right now. */
    public static Map<String, Object> getSelection(Player player) {
        if (!isAvailable() || player == null) return null;
        try {
            LocalSession session = session(player);
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());
            Region region = session.getSelection(weWorld);
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            Map<String, Object> minMap = new LinkedHashMap<>();
            minMap.put("x", (double) min.x());
            minMap.put("y", (double) min.y());
            minMap.put("z", (double) min.z());
            Map<String, Object> maxMap = new LinkedHashMap<>();
            maxMap.put("x", (double) max.x());
            maxMap.put("y", (double) max.y());
            maxMap.put("z", (double) max.z());
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("world", player.getWorld().getName());
            out.put("min", minMap);
            out.put("max", maxMap);
            return out;
        } catch (Throwable ignored) { return null; }
    }

    /** Fills {@code player}'s current WorldEdit selection with {@code blockId} (e.g.
     *  {@code "minecraft:stone"}) — the actual "editing" primitive, via a real {@link EditSession}
     *  so WorldEdit's own undo history/block-change limits apply exactly as if the player had run
     *  {@code //set} themselves. False if WorldEdit is absent, the player has no selection, or
     *  {@code blockId} doesn't resolve to a real block. */
    public static boolean fillSelection(Player player, String blockId) {
        if (!isAvailable() || player == null || blockId == null) return false;
        try {
            LocalSession session = session(player);
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());
            Region region = session.getSelection(weWorld);
            com.sk89q.worldedit.world.block.BlockType blockType = BlockTypes.get(
                    blockId.contains(":") ? blockId : "minecraft:" + blockId);
            if (blockType == null) return false;
            try (EditSession editSession = session.createEditSession(BukkitAdapter.adapt(player))) {
                editSession.setBlocks(region, blockType.getDefaultState());
                session.remember(editSession);
            }
            return true;
        } catch (Throwable ignored) { return false; }
    }

    /** Pastes {@code player}'s current WorldEdit clipboard at {@code x,y,z} in their current
     *  world. False if WorldEdit is absent or the player has nothing copied. */
    public static boolean pasteClipboard(Player player, double x, double y, double z, boolean ignoreAirBlocks) {
        if (!isAvailable() || player == null) return false;
        try {
            LocalSession session = session(player);
            ClipboardHolder holder = session.getClipboard();
            Clipboard clipboard = holder.getClipboard();
            com.sk89q.worldedit.function.operation.Operation operation = holder
                    .createPaste(session.createEditSession(BukkitAdapter.adapt(player)))
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .build();
            com.sk89q.worldedit.function.operation.Operations.complete(operation);
            return true;
        } catch (Throwable ignored) { return false; }
    }
}
