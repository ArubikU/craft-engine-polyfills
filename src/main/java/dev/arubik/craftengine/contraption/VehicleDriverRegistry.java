package dev.arubik.craftengine.contraption;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Who is piloting which VEHICLE contraption (the steer-vehicle feature). A one-driver-per-contraption,
 * one-contraption-per-driver mapping read every tick by {@code VehicleControlBehavior} to turn the driver's
 * live movement input into steering thrust.
 *
 * <p>Deliberately just an in-memory index — a driver seat is a live, session-scoped thing (nobody is "still
 * driving" across a restart), so nothing here persists. A driver that logs off is dropped ({@link #onQuit}),
 * and taking a new vehicle transparently releases the old one.
 */
public final class VehicleDriverRegistry implements Listener {

    private VehicleDriverRegistry() {
    }

    public static final VehicleDriverRegistry INSTANCE = new VehicleDriverRegistry();

    /**
     * Furniture ids whose seats are DRIVER seats ("vehicle_seat" — the marker the user asked for). Sitting in
     * one of these on a VEHICLE contraption takes the helm. Seeded with {@code cml:vehicle_seat}; register more
     * with {@link #registerVehicleSeat}. If a vehicle has NO marked seat at all, any seat drives (see the sit
     * hook in {@code ContraptionSeatListener}) — so the "first to sit drives" default works with any furniture.
     */
    private static final java.util.Set<String> VEHICLE_SEAT_IDS = ConcurrentHashMap.newKeySet();

    /**
     * (Re)loads the driver-seat furniture ids from {@code vehicle-seats.yml} in the plugin data folder,
     * writing the starter copy on first run. Called on enable and on CraftEngine reload. Best-effort: a
     * malformed file logs and keeps the previous set rather than throwing.
     */
    public static void load() {
        try {
            dev.arubik.craftengine.CraftEnginePolyfills plugin = dev.arubik.craftengine.CraftEnginePolyfills.instance();
            java.io.File file = new java.io.File(plugin.getDataFolder(), "vehicle-seats.yml");
            if (!file.exists()) {
                plugin.saveResource("vehicle-seats.yml", false);
            }
            org.bukkit.configuration.file.YamlConfiguration yaml =
                    org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
            java.util.Set<String> parsed = java.util.concurrent.ConcurrentHashMap.newKeySet();
            for (String id : yaml.getStringList("seats")) {
                if (id != null && !id.isBlank()) {
                    parsed.add(id.trim());
                }
            }
            VEHICLE_SEAT_IDS.clear();
            VEHICLE_SEAT_IDS.addAll(parsed);
        } catch (Throwable t) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                    .warning("[vehicle-seats.yml] failed to load, keeping previous values: " + t);
        }
    }

    /** Marks a furniture id (e.g. {@code "cml:helm"}) as a driver seat at runtime (config is the primary source). */
    public static void registerVehicleSeat(String furnitureId) {
        if (furnitureId != null) {
            VEHICLE_SEAT_IDS.add(furnitureId);
        }
    }

    /** Whether {@code furnitureId} is a registered driver seat. */
    public static boolean isVehicleSeat(String furnitureId) {
        return furnitureId != null && VEHICLE_SEAT_IDS.contains(furnitureId);
    }

    /** contraptionId -> driver player UUID. */
    private static final Map<UUID, UUID> DRIVER_OF = new ConcurrentHashMap<>();
    /** driver player UUID -> contraptionId (the reverse index, so one player drives at most one vehicle). */
    private static final Map<UUID, UUID> VEHICLE_OF = new ConcurrentHashMap<>();

    /** Makes {@code player} the sole driver of {@code contraptionId}, releasing whatever either was bound to. */
    public static void setDriver(UUID contraptionId, UUID player) {
        clearDriver(player);
        UUID prev = DRIVER_OF.put(contraptionId, player);
        if (prev != null) {
            VEHICLE_OF.remove(prev);
        }
        VEHICLE_OF.put(player, contraptionId);
    }

    /** Stops {@code player} driving whatever they were driving (no-op if they weren't). Returns the freed vehicle id. */
    public static UUID clearDriver(UUID player) {
        UUID vehicle = VEHICLE_OF.remove(player);
        if (vehicle != null) {
            DRIVER_OF.remove(vehicle);
        }
        return vehicle;
    }

    /** The player currently driving {@code contraptionId}, or null. */
    public static UUID driverOf(UUID contraptionId) {
        return DRIVER_OF.get(contraptionId);
    }

    /** The vehicle {@code player} is driving, or null. */
    public static UUID vehicleOf(UUID player) {
        return VEHICLE_OF.get(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearDriver(event.getPlayer().getUniqueId());
    }
}
