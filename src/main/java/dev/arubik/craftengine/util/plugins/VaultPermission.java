package dev.arubik.craftengine.util.plugins;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Bridge to Vault's {@code Permission} service — the permission-plugin-agnostic counterpart of
 * {@link LuckPermsSupport} (works with WHATEVER permission plugin Vault is hooked into —
 * PermissionsEx, GroupManager, LuckPerms's own Vault hook, ...), for a server that isn't
 * necessarily running LuckPerms specifically. Same {@code compileOnly} + {@link #isAvailable()} +
 * {@code catch (Throwable)} idiom as every other bridge here.
 */
public final class VaultPermission {

    private static volatile Permission permission;
    private static volatile boolean resolved = false;

    private VaultPermission() {}

    public static void reset() {
        resolved = false;
        permission = null;
    }

    private static Permission permission() {
        if (!resolved) {
            synchronized (VaultPermission.class) {
                if (!resolved) {
                    Permission found = null;
                    try {
                        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                            RegisteredServiceProvider<Permission> reg =
                                    Bukkit.getServicesManager().getRegistration(Permission.class);
                            if (reg != null) found = reg.getProvider();
                        }
                    } catch (Throwable ignored) {}
                    permission = found;
                    resolved = true;
                }
            }
        }
        return permission;
    }

    public static boolean isAvailable() {
        return permission() != null;
    }

    public static boolean has(Player player, String node) {
        Permission p = permission();
        if (p == null || player == null || node == null) return false;
        try { return p.playerHas(player, node); } catch (Throwable t) { return false; }
    }

    public static boolean add(Player player, String node) {
        Permission p = permission();
        if (p == null || player == null || node == null) return false;
        try { return p.playerAdd(player, node); } catch (Throwable t) { return false; }
    }

    public static boolean remove(Player player, String node) {
        Permission p = permission();
        if (p == null || player == null || node == null) return false;
        try { return p.playerRemove(player, node); } catch (Throwable t) { return false; }
    }

    public static boolean inGroup(Player player, String group) {
        Permission p = permission();
        if (p == null || player == null || group == null) return false;
        try { return p.playerInGroup(player, group); } catch (Throwable t) { return false; }
    }

    public static boolean addGroup(Player player, String group) {
        Permission p = permission();
        if (p == null || player == null || group == null) return false;
        try { return p.playerAddGroup(player, group); } catch (Throwable t) { return false; }
    }

    public static boolean removeGroup(Player player, String group) {
        Permission p = permission();
        if (p == null || player == null || group == null) return false;
        try { return p.playerRemoveGroup(player, group); } catch (Throwable t) { return false; }
    }

    public static String primaryGroup(Player player) {
        Permission p = permission();
        if (p == null || player == null) return "";
        try {
            String group = p.getPrimaryGroup(player);
            return group != null ? group : "";
        } catch (Throwable t) { return ""; }
    }

    public static String[] groups(Player player) {
        Permission p = permission();
        if (p == null || player == null) return new String[0];
        try {
            String[] groups = p.getPlayerGroups(player);
            return groups != null ? groups : new String[0];
        } catch (Throwable t) { return new String[0]; }
    }
}
