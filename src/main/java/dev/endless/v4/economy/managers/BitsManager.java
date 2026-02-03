package dev.endless.v4.economy.managers;

import dev.endless.v4.economy.utils.Database;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class BitsManager {

    public static final HashMap<UUID, Double> bits = new HashMap<>();


    public static void loadBits() {
        bits.clear();

        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT uuid, bits FROM profile")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                if (uuidStr == null || uuidStr.isEmpty()) {
                    continue;
                }
                UUID uuid = UUID.fromString(uuidStr);
                double currency = rs.getDouble("bits");
                bits.put(uuid, currency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void give(Player player, double amount) {
        double newBalance = bits.compute(player.getUniqueId(), (k, current) -> (current == null ? 0 : current) + amount);
        update(player.getUniqueId(), newBalance);
    }

    public static void take(Player player, double amount) {
        double newBalance = bits.compute(player.getUniqueId(), (k, current) -> {
            double newVal = (current == null ? 0 : current) - amount;
            return Math.max(newVal, 0);
        });
        update(player.getUniqueId(), newBalance);
    }

    public static void set(Player player, double amount) {
        bits.put(player.getUniqueId(), amount);
        update(player.getUniqueId(), amount);
    }

    public static void reset(Player player) {
        bits.put(player.getUniqueId(), 0.0);
        update(player.getUniqueId(), 0.0);
    }

    public static Double getBits(Player player) {
        return bits.getOrDefault(player.getUniqueId(), 0.0);
    }

    private static void update(UUID uuid, double balance) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE economy SET bits = ? WHERE uuid = ?")) {
            ps.setDouble(1, balance);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}