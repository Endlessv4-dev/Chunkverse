package dev.endless.v4.economy.placeholders;

import dev.endless.v4.economy.managers.BitsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class BitsExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;

    public BitsExpansion(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "bits";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "";
        }

        if (!params.equalsIgnoreCase("balance")) {
            return null;
        }

        double value = BitsManager.bits.getOrDefault(player.getUniqueId(), 0.0);
        return BalanceFormat.format(value);
    }
}
