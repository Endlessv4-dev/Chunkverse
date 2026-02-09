package dev.endless.v4.economy.utils;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
                    "orbs DOUBLE DEFAULT 0)");
            Bukkit.getLogger().info("§a[Economy] Adatbázis tábla (economy) ellenőrizve/létrehozva.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("§c[Economy] Hiba történt a tábla létrehozásakor!");
            e.printStackTrace();
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS log (" +
                    "id INT(11) AUTO_INCREMENT PRIMARY KEY, " +
                    "sender VARCHAR(36), " +
                    "target VARCHAR(36), " +
                    "type VARCHAR(16), " +
                    "currency VARCHAR(16), " +
                    "amount DOUBLE," +
                    "date TIMESTAMP)");
            Bukkit.getLogger().info("[Economy] Adatbázis tábla (log) létrehozva/ellenőrízve.");
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

    public static Connection getConnection() {
        return connection;
    }
}