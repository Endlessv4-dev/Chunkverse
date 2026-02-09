package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.utils.AmountParser;
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
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class Bits implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

//        if (!(sender instanceof Player player)) {
//            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
//            return false;
//        }

        if (args.length == 0) {
            sender.sendMessage("§c§lFAILED | §7Usage: /bitsa <give|take|set|reset> <player> [amount]");
            return true;
        }

        String cmd = args[0].toLowerCase();
        if (cmd.equals("give")) {

            if (!sender.hasPermission("chunkverse.bits.give")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /bitsa give <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This players doesn't exist or is not online.");
                return false;
            }

            double amount;
            try {
                amount = AmountParser.parse(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c§lFAILED | §7Wrong amount (example: 150, 1.5K, 2M)");
                return true;
            }

            if (sender instanceof Player player) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.give(player, target, amount);
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.give("server", target, amount);
                });
            }
            sender.sendMessage("§a§lSUCCESS | §7You have given §6" + amount + " §7to §e" + target.getName() + "§7.");
            return true;

        }
        else if (cmd.equals("take")) {

            if (!sender.hasPermission("chunkverse.bits.take")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /bitsa take <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            double amount;
            try {
                amount = AmountParser.parse(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c§lFAILED | §7Wrong amount (example: 150, 1.5K, 2M)");
                return true;
            }

            if (sender instanceof Player player) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.take(player, target, amount);
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.take("server", target, amount);
                });
            }
            sender.sendMessage("§a§lSUCCESS | §7You have taken §6" + amount + " §7from §e" + target.getName() + "§7.");
            return true;

        }
        else if (cmd.equals("set")) {

            if (!sender.hasPermission("chunkverse.bits.set")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /bitsa set <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            double amount;
            try {
                amount = AmountParser.parse(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c§lFAILED | §7Wrong amount (example: 150, 1.5K, 2M)");
                return true;
            }

            if (sender instanceof Player player) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.set(player, target, amount);
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.set("server", target, amount);
                });
            }
            sender.sendMessage("§a§lSUCCESS | §7You have set 'bits' for §e" + target.getName() + " §7to §6" + amount + "§7.");
            return true;

        }
        else if (cmd.equals("reset")) {

            if (!sender.hasPermission("chunkverse.bits.reset")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 2) {
                sender.sendMessage("§c§lFAILED | §7Usage: /bitsa reset <player>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            if (sender instanceof Player player) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.reset(player, target);
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    BitsManager.reset("server", target);
                });
            }
            sender.sendMessage("§a§lSUCCESS | §7You have reset §e" + target.getName() + "§7's bits.");
            return true;

        }
        else if (cmd.equals("logs")) {

            if (!sender.hasPermission("chunkverse.bits.logs")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                String sql = "SELECT * FROM log";
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

                    Component full = Component.text("[Full]")
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
            return true;
        }

        sender.sendMessage("§c§lFAILED | §7Usage: /bitsa <give|take|set|reset> <player> [amount]");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("chunkverse.bits.give")) {
                completions.add("give");
            }
            if (sender.hasPermission("chunkverse.bits.take")) {
                completions.add("take");
            }
            if (sender.hasPermission("chunkverse.bits.set")) {
                completions.add("set");
            }
            if (sender.hasPermission("chunkverse.bits.reset")) {
                completions.add("reset");
            }
            if (sender.hasPermission("chunkverse.bits.logs")) {
                completions.add("logs");
            }
        } else if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3) {
            String action = args[0].toLowerCase();
            if (action.equals("give") || action.equals("take") || action.equals("set")) {
                completions.addAll(Arrays.asList("1", "10", "100", "1000"));
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



