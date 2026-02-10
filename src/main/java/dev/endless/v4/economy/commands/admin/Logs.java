package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.utils.Database;
import dev.endless.v4.economy.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Logs implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!sender.hasPermission("chunkverse.economy.logs")) {
            sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage("§c§lFAILED | §7Usage: /logs <command>");
            return false;
        }

        String cmd = args[0];

        if (cmd.equals("update")) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                String sql = "SELECT * FROM logs";
                try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
                    ResultSet rs = ps.executeQuery();

                    while(rs.next()) {
                        int id = rs.getInt("id");
                        String admin = Objects.equals(rs.getString("executor"), "server") ? "server" : Bukkit.getPlayer(UUID.fromString(rs.getString("executor"))).getName();
                        String target = Bukkit.getPlayer(UUID.fromString(rs.getString("target"))).getName();
                        String type = rs.getString("type");
                        String currency = rs.getString("currency");
                        double amount = rs.getDouble("amount");
                        Timestamp date = rs.getTimestamp("date");

                        Logger.toFile(admin, target, type, currency, amount, date);
                    }

                    Component firstHalf = Component.text("§a§lSUCCESS | §7The log file ");

                    Component file = Component.text("[Transactions]")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true)
                            .clickEvent(ClickEvent.runCommand("/viewlog"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to check the last 10 transactions", NamedTextColor.GRAY)));

                    Component lastHalf = Component.text(" §7has been updated.");

                    Component full = Component.text("[Open]")
                            .color(NamedTextColor.BLUE)
                            .decoration(TextDecoration.BOLD, true)
                            .decoration(TextDecoration.ITALIC, true)
                            .clickEvent(ClickEvent.runCommand("/openlogs"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to see the full logs. ", NamedTextColor.GRAY)));

                    Component finalMessage = firstHalf
                            .append(file)
                            .append(lastHalf)
                            .append(full);

                    sender.sendMessage(finalMessage);
                } catch (SQLException e) {
                    sender.sendMessage("§c§lERROR | §7Error while trying to reach the database.");
                    e.printStackTrace();
                }
            });
        }

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("chunkverse.economy.logs")) {
                completions.add("update");
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
