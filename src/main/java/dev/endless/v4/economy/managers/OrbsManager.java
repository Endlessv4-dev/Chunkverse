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

        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT uuid, orbs FROM profile")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                if (uuidStr == null || uuidStr.isEmpty()) {
                    continue;
                }
                UUID uuid = UUID.fromString(uuidStr);
                double currency = rs.getDouble("orbs");
                orbs.put(uuid, currency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void give(Player player, double amount) {
        double newBalance = orbs.compute(player.getUniqueId(), (k, current) -> (current == null ? 0 : current) + amount);
        update(player.getUniqueId(), newBalance);
    }

    public static void take(Player player, double amount) {
        double newBalance = orbs.compute(player.getUniqueId(), (k, current) -> {
            double newVal = (current == null ? 0 : current) - amount;
            return Math.max(newVal, 0);
        });
        update(player.getUniqueId(), newBalance);
    }

    public static void set(Player player, double amount) {
        orbs.put(player.getUniqueId(), amount);
        update(player.getUniqueId(), amount);
    }

    public static void reset(Player player) {
        orbs.put(player.getUniqueId(), 0.0);
        update(player.getUniqueId(), 0.0);
    }

    public static Double getOrbs(Player player) {
        return orbs.getOrDefault(player.getUniqueId(), 0.0);
    }

    private static void update(UUID uuid, double balance) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE economy SET orbs = ? WHERE uuid = ?")) {
            ps.setDouble(1, balance);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
