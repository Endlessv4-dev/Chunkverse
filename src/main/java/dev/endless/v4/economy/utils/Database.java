package dev.endless.v4.economy.utils;

import org.bukkit.Bukkit;

import java.sql.*;
import java.time.LocalDate;
import java.util.PropertyResourceBundle;
import java.util.UUID;

public class Database {

    private static Connection connection;

    public static void connect(String host, int port, String database, String user, String password) {
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";

            connection = DriverManager.getConnection(url, user, password);

            Bukkit.getLogger().info("§a[Economy] Sikeresen csatlakozva az adatbázishoz!");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("§c[Economy] NEM SIKERÜLT csatlakozni az adatbázishoz! Ellenőrizd a config.yml-t.");
            e.printStackTrace();
        }
    }


    public static void createTables() {
        if (connection == null) return;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS economy (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "bits DOUBLE DEFAULT 0, " +
                    "orbs DOUBLE DEFAULT 0, " +
                    "last_reward DATE DEFAULT NULL, " +
                    "reward_streak INT(11) DEFAULT 0)");
            Bukkit.getLogger().info("§a[Economy] Adatbázis tábla (economy) ellenőrizve/létrehozva.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("§c[Economy] Hiba történt a tábla létrehozásakor!");
            e.printStackTrace();
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS logs (" +
                    "id INT(11) AUTO_INCREMENT PRIMARY KEY, " +
                    "sender VARCHAR(36), " +
                    "target VARCHAR(36), " +
                    "type VARCHAR(16), " +
                    "currency VARCHAR(16), " +
                    "amount DOUBLE, " +
                    "old_balance DOUBLE, " +
                    "date TIMESTAMP)");
            Bukkit.getLogger().info("[Economy] Adatbázis tábla (logs) létrehozva/ellenőrízve.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Economy] Hiba történt a tábla létrehozásakor!");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("Adatbázis kapcsolat lezárva.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LocalDate getLastRewardDate(UUID uuid) {
        String sql = "SELECT last_reward FROM economy WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("last_reward");
                    return (sqlDate != null) ? sqlDate.toLocalDate() : null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRewardStreak(UUID uuid) {
        String sql = "SELECT reward_streak FROM economy WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("reward_streak");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateRewardData(UUID uuid, LocalDate date, int streak) {
        String sql = "UPDATE economy SET last_reward = ?, reward_streak = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, streak);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLastRewardDate(UUID uuid, LocalDate date) {
        String sql = "UPDATE economy SET last_reward = ? WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, uuid.toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        return connection;
    }
}