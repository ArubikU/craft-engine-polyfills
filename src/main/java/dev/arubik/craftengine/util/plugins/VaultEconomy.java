package dev.arubik.craftengine.util.plugins;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Bridge to Vault's {@code Economy} service (the de-facto standard economy API every money
 * plugin — EssentialsX, CMI, GringottsEco, ... — registers behind). {@code VaultAPI} is a
 * {@code compileOnly} dependency (see build.gradle.kts) — every call here is gated behind {@link
 * #isAvailable()} (which itself checks {@code Bukkit.getPluginManager().isPluginEnabled("Vault")}
 * before ever touching a Vault class) and wrapped in {@code catch (Throwable)}, so this addon
 * compiles and runs fine on a server with no Vault/economy plugin installed at all.
 */
public final class VaultEconomy {

    private static volatile Economy economy;
    private static volatile boolean resolved = false;

    private VaultEconomy() {}

    /** Forces the next call to re-resolve the Economy provider — for a {@code /cep reload} style
     *  command, in case Vault or the underlying economy plugin was (re)loaded since last lookup. */
    public static void reset() {
        resolved = false;
        economy = null;
    }

    private static Economy economy() {
        if (!resolved) {
            synchronized (VaultEconomy.class) {
                if (!resolved) {
                    Economy found = null;
                    try {
                        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                            RegisteredServiceProvider<Economy> reg =
                                    Bukkit.getServicesManager().getRegistration(Economy.class);
                            if (reg != null) found = reg.getProvider();
                        }
                    } catch (Throwable ignored) {}
                    economy = found;
                    resolved = true;
                }
            }
        }
        return economy;
    }

    public static boolean isAvailable() {
        return economy() != null;
    }

    public static double balance(OfflinePlayer player) {
        Economy econ = economy();
        if (econ == null || player == null) return 0.0;
        try { return econ.getBalance(player); } catch (Throwable t) { return 0.0; }
    }

    public static boolean has(OfflinePlayer player, double amount) {
        Economy econ = economy();
        if (econ == null || player == null) return false;
        try { return econ.has(player, amount); } catch (Throwable t) { return false; }
    }

    /** Deposits {@code amount} into {@code player}'s account. False if Vault/an economy is
     *  unavailable, the amount isn't positive, or the underlying plugin reports failure. */
    public static boolean deposit(OfflinePlayer player, double amount) {
        Economy econ = economy();
        if (econ == null || player == null || amount <= 0) return false;
        try {
            EconomyResponse response = econ.depositPlayer(player, amount);
            return response != null && response.transactionSuccess();
        } catch (Throwable t) { return false; }
    }

    /** Withdraws {@code amount} from {@code player}'s account — pre-checked with {@link #has} so
     *  this never drives a balance negative even if the underlying plugin itself would allow it. */
    public static boolean withdraw(OfflinePlayer player, double amount) {
        if (amount <= 0 || !has(player, amount)) return false;
        Economy econ = economy();
        try {
            EconomyResponse response = econ.withdrawPlayer(player, amount);
            return response != null && response.transactionSuccess();
        } catch (Throwable t) { return false; }
    }

    /** Formats {@code amount} using the economy plugin's own currency display (symbol, decimals,
     *  singular/plural currency name, ...) — falls back to the plain number as text if unavailable. */
    public static String format(double amount) {
        Economy econ = economy();
        if (econ == null) return String.valueOf(amount);
        try { return econ.format(amount); } catch (Throwable t) { return String.valueOf(amount); }
    }

    public static String currencyNamePlural() {
        Economy econ = economy();
        if (econ == null) return "";
        try { return econ.currencyNamePlural(); } catch (Throwable t) { return ""; }
    }

    public static String currencyNameSingular() {
        Economy econ = economy();
        if (econ == null) return "";
        try { return econ.currencyNameSingular(); } catch (Throwable t) { return ""; }
    }
}
