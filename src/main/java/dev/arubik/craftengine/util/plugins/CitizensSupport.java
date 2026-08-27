package dev.arubik.craftengine.util.plugins;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Bridge to Citizens' NPC API. {@code citizens-main} is a {@code compileOnly} dependency
 * (Citizens' own repo — see build.gradle.kts); every call is gated behind {@link #isAvailable()}
 * and wrapped in {@code catch (Throwable)}, so this addon compiles and runs fine on a server with
 * no Citizens installed.
 */
public final class CitizensSupport {

    private CitizensSupport() {}

    public static boolean isAvailable() {
        try { return Bukkit.getPluginManager().isPluginEnabled("Citizens"); }
        catch (Throwable t) { return false; }
    }

    public static boolean isNpc(Entity entity) {
        if (!isAvailable() || entity == null) return false;
        try { return CitizensAPI.getNPCRegistry().isNPC(entity); } catch (Throwable ignored) { return false; }
    }

    private static NPC npcOf(Entity entity) {
        if (!isAvailable() || entity == null) return null;
        try {
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            return registry.isNPC(entity) ? registry.getNPC(entity) : null;
        } catch (Throwable ignored) { return null; }
    }

    /** Citizens' own numeric NPC id, or -1 if {@code entity} isn't an NPC/Citizens is absent. */
    public static int npcId(Entity entity) {
        NPC npc = npcOf(entity);
        try { return npc != null ? npc.getId() : -1; } catch (Throwable ignored) { return -1; }
    }

    /** The NPC's configured name (color-code stripped, matching what {@code /npc rename} sets) —
     *  "" if {@code entity} isn't an NPC or Citizens is absent. */
    public static String npcName(Entity entity) {
        NPC npc = npcOf(entity);
        try { return npc != null ? npc.getName() : ""; } catch (Throwable ignored) { return ""; }
    }

    /** Spawns a brand-new NPC of {@code entityType} (e.g. "VILLAGER") named {@code name} at
     *  {@code loc}, returning its live Bukkit entity — null if Citizens is absent or the entity
     *  type/name is rejected. */
    public static Entity createNpc(String entityType, String name, Location loc) {
        if (!isAvailable() || entityType == null || name == null || loc == null) return null;
        try {
            EntityType type = EntityType.valueOf(entityType.toUpperCase(java.util.Locale.ROOT));
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name, loc);
            return npc != null ? npc.getEntity() : null;
        } catch (Throwable ignored) { return null; }
    }

    /** Permanently removes the NPC {@code entity} is (a no-op if it isn't one). */
    public static boolean removeNpc(Entity entity) {
        NPC npc = npcOf(entity);
        if (npc == null) return false;
        try { npc.destroy(); return true; } catch (Throwable ignored) { return false; }
    }
}
