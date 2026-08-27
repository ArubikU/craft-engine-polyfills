package dev.arubik.craftengine.util.plugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

/**
 * Bridge to Dynmap's marker API. {@code DynmapCoreAPI} is a {@code compileOnly} dependency (see
 * build.gradle.kts); every call is gated behind {@link #isAvailable()} and wrapped in {@code catch
 * (Throwable)}, so this addon compiles and runs fine on a server with no Dynmap installed.
 */
public final class DynmapSupport {

    private static volatile DynmapCommonAPI api;
    private static volatile boolean resolved = false;

    private DynmapSupport() {}

    public static void reset() {
        resolved = false;
        api = null;
    }

    private static DynmapCommonAPI api() {
        if (!resolved) {
            synchronized (DynmapSupport.class) {
                if (!resolved) {
                    DynmapCommonAPI found = null;
                    try {
                        Plugin plugin = Bukkit.getPluginManager().getPlugin("dynmap");
                        if (plugin != null && plugin.isEnabled() && plugin instanceof DynmapCommonAPI dynmapApi) {
                            found = dynmapApi;
                        }
                    } catch (Throwable ignored) {}
                    api = found;
                    resolved = true;
                }
            }
        }
        return api;
    }

    public static boolean isAvailable() {
        return api() != null;
    }

    private static MarkerSet markerSet(String setId, String setLabel) {
        MarkerAPI markerApi = api().getMarkerAPI();
        MarkerSet set = markerApi.getMarkerSet(setId);
        if (set == null) set = markerApi.createMarkerSet(setId, setLabel != null ? setLabel : setId, null, false);
        return set;
    }

    /** Creates (or replaces, if {@code markerId} already exists) a point marker. {@code icon} is
     *  a Dynmap icon name (e.g. "greenflag") — falls back to Dynmap's own default icon if unknown.
     *  False if Dynmap is absent or the marker set/icon can't be resolved. */
    public static boolean addMarker(String setId, String setLabel, String markerId, String label,
            String world, double x, double y, double z, String icon) {
        if (!isAvailable()) return false;
        try {
            MarkerSet set = markerSet(setId, setLabel);
            if (set == null) return false;
            MarkerIcon markerIcon = icon != null ? api().getMarkerAPI().getMarkerIcon(icon) : null;
            if (markerIcon == null) markerIcon = api().getMarkerAPI().getMarkerIcon("default");
            Marker existing = set.findMarker(markerId);
            if (existing != null) existing.deleteMarker();
            set.createMarker(markerId, label != null ? label : markerId, world, x, y, z, markerIcon, false);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    public static boolean removeMarker(String setId, String markerId) {
        if (!isAvailable()) return false;
        try {
            MarkerSet set = api().getMarkerAPI().getMarkerSet(setId);
            if (set == null) return false;
            Marker marker = set.findMarker(markerId);
            if (marker == null) return false;
            marker.deleteMarker();
            return true;
        } catch (Throwable ignored) { return false; }
    }
}
