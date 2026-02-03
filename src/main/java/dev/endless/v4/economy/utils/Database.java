package dev.endless.v4.economy.utils;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection connection;

    public static void connect() {
        try {
            String host = "localhost";
            String database = "chunkversetest";
            String username = "chunkverse-db";
            String password = "";
            int port = 3306;

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password
            );

            Bukkit.getLogger().info("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Could not connect to database.");
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

}
