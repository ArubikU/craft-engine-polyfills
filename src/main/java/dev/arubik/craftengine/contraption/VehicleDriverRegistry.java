/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.CraftEnginePolyfills;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class VehicleDriverRegistry
implements Listener {
    public static final VehicleDriverRegistry INSTANCE = new VehicleDriverRegistry();
    private static final Set<String> VEHICLE_SEAT_IDS = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, UUID> DRIVER_OF = new ConcurrentHashMap<UUID, UUID>();
    private static final Map<UUID, UUID> VEHICLE_OF = new ConcurrentHashMap<UUID, UUID>();

    private VehicleDriverRegistry() {
    }

    public static void load() {
        try {
            CraftEnginePolyfills plugin = CraftEnginePolyfills.instance();
            File file = new File(plugin.getDataFolder(), "vehicle-seats.yml");
            if (!file.exists()) {
                plugin.saveResource("vehicle-seats.yml", false);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration((File)file);
            ConcurrentHashMap.KeySetView parsed = ConcurrentHashMap.newKeySet();
            for (String id : yaml.getStringList("seats")) {
                if (id == null || id.isBlank()) continue;
                parsed.add(id.trim());
            }
            VEHICLE_SEAT_IDS.clear();
            VEHICLE_SEAT_IDS.addAll(parsed);
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[vehicle-seats.yml] failed to load, keeping previous values: " + String.valueOf(t));
        }
    }

    public static void registerVehicleSeat(String furnitureId) {
        if (furnitureId != null) {
            VEHICLE_SEAT_IDS.add(furnitureId);
        }
    }

    public static boolean isVehicleSeat(String furnitureId) {
        return furnitureId != null && VEHICLE_SEAT_IDS.contains(furnitureId);
    }

    public static void setDriver(UUID contraptionId, UUID player) {
        VehicleDriverRegistry.clearDriver(player);
        UUID prev = DRIVER_OF.put(contraptionId, player);
        if (prev != null) {
            VEHICLE_OF.remove(prev);
        }
        VEHICLE_OF.put(player, contraptionId);
    }

    public static UUID clearDriver(UUID player) {
        UUID vehicle = VEHICLE_OF.remove(player);
        if (vehicle != null) {
            DRIVER_OF.remove(vehicle);
        }
        return vehicle;
    }

    public static UUID driverOf(UUID contraptionId) {
        return DRIVER_OF.get(contraptionId);
    }

    public static UUID vehicleOf(UUID player) {
        return VEHICLE_OF.get(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        VehicleDriverRegistry.clearDriver(event.getPlayer().getUniqueId());
    }
}

