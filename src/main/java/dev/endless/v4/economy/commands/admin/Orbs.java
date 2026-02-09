package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.managers.OrbsManager;
import dev.endless.v4.economy.utils.AmountParser;
import dev.endless.v4.economy.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Orbs implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

//        if (!(sender instanceof Player player)) {
//            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
//            return false;
//        }

        if (args.length == 0) {
            sender.sendMessage("§c§lFAILED | §7Usage: /orbsa <give|take|set|reset> <player> [amount]");
            return true;
        }

        String cmd = args[0].toLowerCase();
        if (cmd.equals("give")) {

            if (!sender.hasPermission("chunkverse.orbs.give")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /orbsa give <player> <amount>");
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

            OrbsManager.give(target, amount);
            sender.sendMessage("§a§lSUCCESS | §7You have given §6" + amount + " §7to §e" + target.getName() + "§7.");
            return true;

        }
        else if (cmd.equals("take")) {

            if (!sender.hasPermission("chunkverse.orbs.take")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /orbsa take <player> <amount>");
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

            OrbsManager.take(target, amount);
            sender.sendMessage("§a§lSUCCESS | §7You have taken §6" + amount + " §7from §e" + target.getName() + "§7.");
            return true;

        }
        else if (cmd.equals("set")) {

            if (!sender.hasPermission("chunkverse.orbs.set")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /orbsa set <player> <amount>");
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

            OrbsManager.set(target, amount);
            sender.sendMessage("§a§lSUCCESS | §7You have set 'orbs' for §e" + target.getName() + " §7to §6" + amount + "§7.");
            return true;

        }
        else if (cmd.equals("reset")) {

            if (!sender.hasPermission("chunkverse.orbs.reset")) {
                sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 2) {
                sender.sendMessage("§c§lFAILED | §7Usage: /orbsa reset <player>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            OrbsManager.reset(target);
            sender.sendMessage("§a§lSUCCESS | §7You have reset §e" + target.getName() + "§7's orbs.");
            return true;

        }

        sender.sendMessage("§c§lFAILED | §7Usage: /orbsa <give|take|set|reset> <player> [amount]");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("chunkverse.orbs.give")) {
                completions.add("give");
            }
            if (sender.hasPermission("chunkverse.orbs.take")) {
                completions.add("take");
            }
            if (sender.hasPermission("chunkverse.orbs.set")) {
                completions.add("set");
            }
            if (sender.hasPermission("chunkverse.orbs.reset")) {
                completions.add("reset");
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




