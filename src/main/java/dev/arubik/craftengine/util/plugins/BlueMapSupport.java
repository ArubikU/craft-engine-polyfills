package dev.arubik.craftengine.util.plugins;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;

import java.util.Optional;

/**
 * Bridge to BlueMap's marker API. {@code bluemap-api} is a {@code compileOnly} dependency (see
 * build.gradle.kts); every call is gated behind {@link #isAvailable()} and wrapped in {@code catch
 * (Throwable)}, so this addon compiles and runs fine on a server with no BlueMap installed.
 *
 * <p>Unlike the other bridges here, BlueMap doesn't expose a synchronous "give me the API right
 * now" accessor — {@link BlueMapAPI#getInstance()} returns empty until BlueMap has finished
 * loading every map, which can happen well after this addon's own {@code onEnable}. Every method
 * below re-checks it fresh rather than caching a resolved instance for exactly that reason.
 */
public final class BlueMapSupport {

    private BlueMapSupport() {}

    public static boolean isAvailable() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("BlueMap") && BlueMapAPI.getInstance().isPresent();
        } catch (Throwable t) { return false; }
    }

    private static MarkerSet markerSet(BlueMapMap map, String setId, String setLabel) {
        MarkerSet set = map.getMarkerSets().get(setId);
        if (set == null) {
            set = MarkerSet.builder().label(setLabel != null ? setLabel : setId).build();
            map.getMarkerSets().put(setId, set);
        }
        return set;
    }

    /** Creates (or replaces, if {@code markerId} already exists) a point-of-interest marker on
     *  BlueMap map {@code mapId} (BlueMap's own per-world-view id, e.g. "world"/"world_nether").
     *  False if BlueMap is absent or {@code mapId} doesn't resolve to a loaded map. */
    public static boolean addMarker(String mapId, String setId, String setLabel, String markerId,
            String label, double x, double y, double z) {
        if (!isAvailable()) return false;
        try {
            Optional<BlueMapMap> mapOpt = BlueMapAPI.getInstance().flatMap(api -> api.getMap(mapId));
            if (mapOpt.isEmpty()) return false;
            BlueMapMap map = mapOpt.get();
            MarkerSet set = markerSet(map, setId, setLabel);
            POIMarker marker = POIMarker.builder()
                    .label(label != null ? label : markerId)
                    .position(x, y, z)
                    .build();
            set.getMarkers().put(markerId, marker);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    public static boolean removeMarker(String mapId, String setId, String markerId) {
        if (!isAvailable()) return false;
        try {
            Optional<BlueMapMap> mapOpt = BlueMapAPI.getInstance().flatMap(api -> api.getMap(mapId));
            if (mapOpt.isEmpty()) return false;
            MarkerSet set = mapOpt.get().getMarkerSets().get(setId);
            if (set == null) return false;
            return set.getMarkers().remove(markerId) != null;
        } catch (Throwable ignored) { return false; }
    }
}
