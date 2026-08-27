package dev.arubik.craftengine.script.event;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.LocationType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.level.ServerLevel;

/**
 * Small conversion helpers shared by the dedicated per-event {@link ScriptEvent} wrappers in this
 * package (PlayerJoinWrapper, PlayerMoveWrapper, ...) so all of them treat "a message a script can
 * get/set" and "a location a script can get/set" the exact same way instead of each one
 * reinventing its own Component/Location plumbing.
 *
 * <p>Text convention mirrors {@code PlayerType#send_message} exactly: MiniMessage when the text
 * looks tagged ({@code <...>} present), legacy ampersand codes otherwise — the same heuristic a
 * script author already relies on for every other player-facing text method in this codebase.
 */
final class ScriptEventUtil {
    private ScriptEventUtil() {}

    /**
     * Wraps a Bukkit {@code HumanEntity} (the type {@code getWhoClicked()}/{@code getPlayer()}
     * actually return on the inventory events, since a non-player "human" — an NPC plugin standing
     * in for one, for instance — is technically possible) as the richer {@code Player} PolyType when
     * it really is a player, falling back to the generic {@code Entity} PolyType otherwise so a
     * script never gets a null just because the click didn't come from a real player.
     */
    static ScriptValue wrapHumanEntity(org.bukkit.entity.HumanEntity human) {
        if (human == null) return ScriptValue.NULL;
        if (human instanceof org.bukkit.entity.Player p) {
            return dev.arubik.craftengine.script.types.entity.PlayerType.wrap(
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
        }
        return dev.arubik.craftengine.script.types.entity.EntityType.wrap(
                ((org.bukkit.craftbukkit.entity.CraftEntity) human).getHandle());
    }

    static Component parseComponent(String text) {
        if (text != null && text.contains("<") && text.contains(">")) {
            try { return MiniMessage.miniMessage().deserialize(text); } catch (Throwable ignored) {}
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text == null ? "" : text);
    }

    /** Serializes a nullable Adventure component back to a MiniMessage string for a script to read
     *  (and, if it wants, feed straight back into a {@code set_*} method unchanged). */
    static String componentToString(Component c) {
        return c == null ? null : MiniMessage.miniMessage().serialize(c);
    }

    /** Bukkit {@code Location} -> script {@code Location}, {@code NULL} if either the location or
     *  its world is missing (e.g. a not-yet-loaded respawn location). */
    static ScriptValue wrapLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) loc.getWorld()).getHandle();
        return LocationType.wrap(level, loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Script {@code Location} -> Bukkit {@code Location}, or {@code null} if {@code value} isn't
     * one (a script passed something else, or {@code NULL}). {@code template} supplies yaw/pitch
     * (and a fallback world) since {@code LocationType.LocationRef} only tracks world+xyz — a
     * script redirecting {@code event.to} only means to move the destination point, not to also
     * reset the look direction the player would land facing.
     */
    static Location toBukkitLocation(ScriptValue value, Location template) {
        if (!(value instanceof ScriptValue.Obj o) || !(o.instance() instanceof LocationType.LocationRef ref)) {
            return null;
        }
        ServerLevel level = ref.level();
        org.bukkit.World world = level != null ? level.getWorld() : (template != null ? template.getWorld() : null);
        if (world == null) return null;
        Location loc = template != null ? template.clone() : new Location(world, 0, 0, 0);
        loc.setWorld(world);
        loc.setX(ref.x());
        loc.setY(ref.y());
        loc.setZ(ref.z());
        return loc;
    }
}
