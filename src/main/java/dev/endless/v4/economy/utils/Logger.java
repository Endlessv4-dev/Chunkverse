package dev.endless.v4.economy.utils;

import dev.endless.v4.economy.Main;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public class Logger {

    public static void toFile(String admin, String target, String type, String currency, double amount, Timestamp date) {

        File logFile = new File(Main.getInstance().getDataFolder(), "transactions.log");

        try {
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }

            String logLine = String.format("[%s] EXECUTOR: %s | TARGET: %s | TYPE: %s | CURRENCY: %s | AMOUNT %.2f%n",
                                           date.toString(), admin, target, type, currency, amount);

            Files.write(logFile.toPath(), logLine.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            Bukkit.getLogger().severe("Nem sikerült írni a log fájlba.");
            e.printStackTrace();
        }

    }

}
