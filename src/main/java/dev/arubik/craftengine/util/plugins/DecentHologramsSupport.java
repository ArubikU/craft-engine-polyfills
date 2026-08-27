package dev.arubik.craftengine.util.plugins;

import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramManager;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

/**
 * Bridge to DecentHolograms' floating-text API. {@code decentholograms} is a {@code compileOnly}
 * dependency (via JitPack — see build.gradle.kts); every call is gated behind {@link
 * #isAvailable()} and wrapped in {@code catch (Throwable)}, so this addon compiles and runs fine
 * on a server with no DecentHolograms installed.
 */
public final class DecentHologramsSupport {

    private DecentHologramsSupport() {}

    public static boolean isAvailable() {
        try { return Bukkit.getPluginManager().isPluginEnabled("DecentHolograms") && DecentHologramsAPI.isRunning(); }
        catch (Throwable t) { return false; }
    }

    private static HologramManager manager() {
        return DecentHologramsAPI.get().getHologramManager();
    }

    /** Creates (or replaces, if {@code id} already exists) a single-page floating-text hologram
     *  at {@code loc} with one line per {@code lines} entry (MiniMessage/legacy color codes both
     *  work, same as any other text this addon sends). False if DecentHolograms is absent. */
    public static boolean createHologram(String id, Location loc, List<String> lines) {
        if (!isAvailable() || id == null || loc == null) return false;
        try {
            HologramManager manager = manager();
            Hologram existing = manager.getHologram(id);
            if (existing != null) existing.delete();

            Hologram hologram = new Hologram(id, loc);
            manager.registerHologram(hologram);
            HologramPage page = hologram.addPage();
            if (lines != null) {
                for (String line : lines) page.addLine(new HologramLine(page, loc, line == null ? "" : line));
            }
            page.realignLines();
            return true;
        } catch (Throwable ignored) { return false; }
    }

    /** Replaces every line of an EXISTING hologram's first page — false if {@code id} doesn't
     *  resolve to a hologram this addon (or anything else) already created. */
    public static boolean setLines(String id, List<String> lines) {
        if (!isAvailable() || id == null) return false;
        try {
            Hologram hologram = manager().getHologram(id);
            if (hologram == null) return false;
            Location loc = hologram.getLocation();
            HologramPage page = hologram.getPage(0);
            if (page == null) return false;
            while (!page.getLines().isEmpty()) page.removeLine(0);
            if (lines != null) {
                for (String line : lines) page.addLine(new HologramLine(page, loc, line == null ? "" : line));
            }
            page.realignLines();
            return true;
        } catch (Throwable ignored) { return false; }
    }

    public static boolean removeHologram(String id) {
        if (!isAvailable() || id == null) return false;
        try {
            Hologram hologram = manager().removeHologram(id);
            if (hologram != null) hologram.delete();
            return hologram != null;
        } catch (Throwable ignored) { return false; }
    }

    public static boolean exists(String id) {
        if (!isAvailable() || id == null) return false;
        try { return manager().containsHologram(id); } catch (Throwable ignored) { return false; }
    }
}
