package dev.endless.v4.economy.managers;

import dev.endless.v4.economy.utils.Database;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class BitsManager {

    public static final HashMap<UUID, Double> bits = new HashMap<>();

    public static void loadBits() {
        bits.clear();
        if (Database.getConnection() == null) return;

        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT uuid, bits FROM economy")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bits.put(UUID.fromString(rs.getString("uuid")), rs.getDouble("bits"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void give(Player sender, Player player, double amount) {
        double current = bits.getOrDefault(player.getUniqueId(), 0.0);
        bits.put(player.getUniqueId(), current + amount);
        updateDatabase(player.getUniqueId(), current + amount);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, sender.getUniqueId().toString());
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "give");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void give(String server, Player player, double amount) {
        double current = bits.getOrDefault(player.getUniqueId(), 0.0);
        bits.put(player.getUniqueId(), current + amount);
        updateDatabase(player.getUniqueId(), current + amount);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, server);
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "give");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void take(Player sender, Player player, double amount) {
        double current = bits.getOrDefault(player.getUniqueId(), 0.0);
        bits.put(player.getUniqueId(), Math.max(0, current - amount));
        updateDatabase(player.getUniqueId(), Math.max(0, current - amount));

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, sender.getUniqueId().toString());
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "take");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void take(String server, Player player, double amount) {
        double current = bits.getOrDefault(player.getUniqueId(), 0.0);
        bits.put(player.getUniqueId(), Math.max(0, current - amount));
        updateDatabase(player.getUniqueId(), Math.max(0, current - amount));

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, server);
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "take");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void set(Player sender, Player player, double amount) {
        bits.put(player.getUniqueId(), amount);
        updateDatabase(player.getUniqueId(), amount);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, sender.getUniqueId().toString());
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "set");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void set(String server, Player player, double amount) {
        bits.put(player.getUniqueId(), amount);
        updateDatabase(player.getUniqueId(), amount);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, server);
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "set");
            ps.setString(4, "bits");
            ps.setDouble(5, amount);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reset(Player sender, Player player) {
        bits.put(player.getUniqueId(), 0.0);
        updateDatabase(player.getUniqueId(), 0.0);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, sender.getUniqueId().toString());
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "reset");
            ps.setString(4, "bits");
            ps.setDouble(5, 0.0);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void reset(String server, Player player) {
        bits.put(player.getUniqueId(), 0.0);
        updateDatabase(player.getUniqueId(), 0.0);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "INSERT INTO log (executor, target, type, currency, amount, date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, server);
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, "reset");
            ps.setString(4, "bits");
            ps.setDouble(5, 0.0);
            ps.setTimestamp(6, sqlDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean send(Player sender, Player target, double amount) {
        double senderBalance = bits.getOrDefault(sender.getUniqueId(), 0.0);
        if (senderBalance < amount || amount <= 0) {
            return false;
        }
        take(target, sender, amount);
        give(sender, target, amount);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlDate = Timestamp.valueOf(now);

        String sql = "UPDATE log SET type = ? WHERE executor = ? AND target = ? AND date = ? AND type = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, "received");
            ps.setString(2, sender.getUniqueId().toString());
            ps.setString(3, target.getUniqueId().toString());
            ps.setTimestamp(4, sqlDate);
            ps.setString(5, "give");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql2 = "UPDATE log SET type = ? WHERE executor = ? AND target = ? AND date = ? AND type = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql2)) {
            ps.setString(1, "send");
            ps.setString(2, sender.getUniqueId().toString());
            ps.setString(3, target.getUniqueId().toString());
            ps.setTimestamp(4, sqlDate);
            ps.setString(5, "take");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void updateDatabase(UUID uuid, double balance) {
        String sql = "INSERT INTO economy (uuid, bits) VALUES (?, ?) ON DUPLICATE KEY UPDATE bits = ?";
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