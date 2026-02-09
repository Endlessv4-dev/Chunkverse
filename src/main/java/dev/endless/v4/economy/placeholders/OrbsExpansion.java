package dev.endless.v4.economy.placeholders;

import dev.endless.v4.economy.managers.OrbsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class OrbsExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;

    public OrbsExpansion(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "orbs";
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

        double value = OrbsManager.orbs.getOrDefault(player.getUniqueId(), 0.0);
        return BalanceFormat.format(value);
    }
}
