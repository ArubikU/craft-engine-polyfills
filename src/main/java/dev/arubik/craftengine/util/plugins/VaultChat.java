package dev.arubik.craftengine.util.plugins;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Bridge to Vault's {@code Chat} service — a permission-plugin-agnostic prefix/suffix lookup (the
 * same role {@link LuckPermsSupport#metaValue} plays, but sourced from whatever chat/permission
 * plugin Vault is hooked into instead of assuming LuckPerms specifically). Same {@code
 * compileOnly} + {@link #isAvailable()} + {@code catch (Throwable)} idiom as every other bridge
 * here.
 */
public final class VaultChat {

    private static volatile Chat chat;
    private static volatile boolean resolved = false;

    private VaultChat() {}

    public static void reset() {
        resolved = false;
        chat = null;
    }

    private static Chat chat() {
        if (!resolved) {
            synchronized (VaultChat.class) {
                if (!resolved) {
                    Chat found = null;
                    try {
                        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                            RegisteredServiceProvider<Chat> reg =
                                    Bukkit.getServicesManager().getRegistration(Chat.class);
                            if (reg != null) found = reg.getProvider();
                        }
                    } catch (Throwable ignored) {}
                    chat = found;
                    resolved = true;
                }
            }
        }
        return chat;
    }

    public static boolean isAvailable() {
        return chat() != null;
    }

    public static String prefix(Player player) {
        Chat c = chat();
        if (c == null || player == null) return "";
        try {
            String prefix = c.getPlayerPrefix(player);
            return prefix != null ? prefix : "";
        } catch (Throwable t) { return ""; }
    }

    public static boolean setPrefix(Player player, String prefix) {
        Chat c = chat();
        if (c == null || player == null) return false;
        try { c.setPlayerPrefix(player, prefix != null ? prefix : ""); return true; }
        catch (Throwable t) { return false; }
    }

    public static String suffix(Player player) {
        Chat c = chat();
        if (c == null || player == null) return "";
        try {
            String suffix = c.getPlayerSuffix(player);
            return suffix != null ? suffix : "";
        } catch (Throwable t) { return ""; }
    }

    public static boolean setSuffix(Player player, String suffix) {
        Chat c = chat();
        if (c == null || player == null) return false;
        try { c.setPlayerSuffix(player, suffix != null ? suffix : ""); return true; }
        catch (Throwable t) { return false; }
    }
}
