package dev.arubik.craftengine.util;

import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundMap {
    private final Sound openSound;
    private final Sound closeSound;

    public SoundMap(Sound openSound, Sound closeSound) {
        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    public void playOpen(Location location) {
        if (openSound != null && location != null) {
            location.getWorld().playSound(openSound, location.getX(), location.getY(), location.getZ());
        }
    }

    public void playClose(Location location) {
        if (closeSound != null && location != null) {
            location.getWorld().playSound(closeSound, location.getX(), location.getY(), location.getZ());
        }
    }

    public void playOpent(Player player) {
        if (openSound != null && player != null) {
            player.playSound(openSound);
        }
    }

    public void playClose(Player player) {
        if (closeSound != null && player != null) {
            player.playSound(closeSound);
        }
    }

    public static SoundMap fromMap(Map<String, Object> map) {
        Sound open = parseSound(map.get("open"));
        Sound close = parseSound(map.get("close"));
        return new SoundMap(open, close);
    }

    /**
     * Parses {@code "namespace:path"} (e.g. {@code minecraft:block.barrel.open}) or
     * {@code "namespace:path:volume:pitch"}. A plain sound key already contains one colon
     * (namespace:path), so splitting naively on every ":" and treating part[1] as a volume
     * silently dropped the actual sound path — reconstruct the key from the first two parts.
     */
    private static Sound parseSound(Object obj) {
        if (obj instanceof String str) {
            String[] parts = str.split(":");
            if (parts.length < 2)
                return null;
            Key key = Key.key(parts[0], parts[1]);
            float volume = 1f;
            float pitch = 1f;
            try {
                if (parts.length > 2)
                    volume = Float.parseFloat(parts[2]);
                if (parts.length > 3)
                    pitch = Float.parseFloat(parts[3]);
            } catch (NumberFormatException ignored) {
            }
            return Sound.sound(key, Sound.Source.BLOCK, volume, pitch);
        }
        return null;
    }
}
