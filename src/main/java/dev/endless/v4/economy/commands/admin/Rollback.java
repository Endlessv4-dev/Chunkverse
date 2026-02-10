package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.managers.OrbsManager;
import dev.endless.v4.economy.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Rollback implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!sender.hasPermission("chunkverse.economy.rollback")) {
            sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage("§c§lFAILED | §7Usage: /rollback <id>");
            return false;
        }

        int id;

        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c§lFAILED | §7Invalid id.");
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Connection conn = Database.getConnection();

            String sql = "SELECT * FROM logs WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String type = rs.getString("type");
                        String currency = rs.getString("currency");
                        String executorStr = rs.getString("executor");
                        String targetStr = rs.getString("target");
                        double amount = rs.getDouble("amount");
                        double old_balance = rs.getDouble("old_balance");

                        OfflinePlayer executor = executorStr.equals("server") ? null : Bukkit.getOfflinePlayer(UUID.fromString(executorStr));
                        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(targetStr));

                        if (currency.equals("bits")) {
                            switch (type) {
                                case "give" -> BitsManager.take(target, amount);
                                case "take" -> BitsManager.give(target, amount);
                                case "set", "reset" -> BitsManager.set(target, old_balance);
                                case "send" -> {
                                    BitsManager.give(executor, amount);
                                    BitsManager.take(target, amount);
                                }
                            }
                        }
                        else if (currency.equals("orbs")) {
                            switch (type) {
                                case "give" -> OrbsManager.take(target, amount);
                                case "take" -> OrbsManager.give(target, amount);
                                case "set", "reset" -> OrbsManager.set(target, old_balance);
                                case "send" -> {
                                    OrbsManager.give(executor, amount);
                                    OrbsManager.take(target, amount);
                                }
                            }
                        }

                        try (PreparedStatement del = conn.prepareStatement("DELETE FROM logs WHERE id = ?")) {
                            del.setInt(1, id);
                            del.executeUpdate();
                        }
                        sender.sendMessage("§a§lSUCCESS | §7Transaction §e#" + id + " §7rolled back.");
                    } else {
                        sender.sendMessage("§c§lFAILED | §7ID not found.");
                    }
                }
            } catch (SQLException e) {
                sender.sendMessage("§c§lERROR | §7Database error.");
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        List<String> ids = new ArrayList<>();
        try (ResultSet rs = Database.getConnection().createStatement().executeQuery("SELECT id FROM logs")) {
            while (rs.next()) ids.add(String.valueOf(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }

        if (args.length == 1) {
            if (sender.hasPermission("chunkverse.economy.rollback")) {
                completions.addAll(ids);
            }
        }

        if (args.length == 0) {
            return completions;
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
