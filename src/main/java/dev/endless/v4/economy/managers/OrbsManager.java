package dev.endless.v4.economy.managers;

import dev.endless.v4.economy.utils.Database;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class OrbsManager {

    public static final HashMap<UUID, Double> orbs = new HashMap<>();

    public static void loadOrbs() {
        orbs.clear();
        if (Database.getConnection() == null) return;

        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT uuid, orbs FROM economy")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orbs.put(UUID.fromString(rs.getString("uuid")), rs.getDouble("orbs"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void give(Player player, double amount) {
        double current = orbs.getOrDefault(player.getUniqueId(), 0.0);
        set(player, current + amount);
    }

    public static void take(Player player, double amount) {
        double current = orbs.getOrDefault(player.getUniqueId(), 0.0);
        set(player, Math.max(0, current - amount));
    }

    public static void set(Player player, double amount) {
        orbs.put(player.getUniqueId(), amount);
        updateDatabase(player.getUniqueId(), amount);
    }

    public static void reset(Player player) {
        set(player, 0.0);
    }

    public static boolean send(Player sender, Player target, double amount) {
        double senderBalance = orbs.getOrDefault(sender.getUniqueId(), 0.0);
        if (senderBalance < amount || amount <= 0) {
            return false;
        }
        take(sender, amount);
        give(target, amount);
        return true;
    }

    private static void updateDatabase(UUID uuid, double balance) {
        String sql = "INSERT INTO economy (uuid, orbs) VALUES (?, ?) ON DUPLICATE KEY UPDATE orbs = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, balance);
            ps.setDouble(3, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}