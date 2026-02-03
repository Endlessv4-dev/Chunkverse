package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.managers.BitsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bits implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
            return false;
        }

        String cmd = args[0];
        if (cmd.equals("give")) {

            if (!player.hasPermission("chunkverse.bits.give")) {
                player.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                player.sendMessage("§c§lFAILED | §7Usage: /orb give <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c§lFAILED | §7This players doesn't exist or is not online.");
                return false;
            }

            double amount = 0.0;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("§c§lFAILED | §7Wrong amount (it has to be a number)");
                e.printStackTrace();
            }

            BitsManager.give(target, amount);
            player.sendMessage("§a§lSUCCESS | §7You have given §6" + amount + " §7to §e" + target.getName() + "§7.");

        }
        else if (cmd.equals("take")) {

            if (!player.hasPermission("chunkverse.bits.take")) {
                player.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                player.sendMessage("§c§lFAILED | §7Usage: /orb take <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            double amount = 0.0;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("§c§lFAILED | §7Wrong amount (it has to be a number)");
                e.printStackTrace();
            }

            BitsManager.take(target, amount);
            player.sendMessage("§a§lSUCCESS | §7You have taken §6" + amount + " §7from §e" + target.getName() + "§7.");

        }
        else if (cmd.equals("set")) {

            if (!player.hasPermission("chunkverse.bits.set")) {
                player.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 3) {
                player.sendMessage("§c§lFAILED | §7Usage: /orb set <player> <amount>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            double amount = 0.0;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("§c§lFAILED | §7Wrong amount (it has to be a number)");
                e.printStackTrace();
            }

            BitsManager.set(target, amount);
            player.sendMessage("§a§lSUCCESS | §7You have set 'bits' for §e" + target.getName() + " §7to §6" + amount + "§7.");

        }
        else if (cmd.equals("reset")) {

            if (!player.hasPermission("chunkverse.bits.reset")) {
                player.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
                return false;
            }

            if (args.length != 2) {
                player.sendMessage("§c§lFAILED | §7Usage: /orb reset <player>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            BitsManager.reset(target);
            player.sendMessage("§a§lSUCCESS | §7You have reset §e" + target.getName() + "§7's bits.");

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            if (sender.hasPermission("chunkverse.bits.give") && sender.hasPermission("chunkverse.bits.take") &&
                    sender.hasPermission("chunkverse.bits.set") && sender.hasPermission("chunkverse.bits.reset")) {
                completions.addAll(Arrays.asList("give, take, set, reset"));
            }
        }
        else if (args.length == 3) {
            if (sender.hasPermission("chunkverse.bits.give") && sender.hasPermission("chunkverse.bits.take") &&
                    sender.hasPermission("chunkverse.bits.set") && sender.hasPermission("chunkverse.bits.reset")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        }
        else if (args.length == 4) {
            if (sender.hasPermission("chunkverse.bits.give") && sender.hasPermission("chunkverse.bits.take") &&
                    sender.hasPermission("chunkverse.bits.set") && sender.hasPermission("chunkverse.bits.reset")) {
                String action = args[3];
                if (action.equals("give") || action.equals("take") || action.equals("set")) {
                    completions.addAll(Arrays.asList("1", "10", "100", "1000"));
                }
            }
        }

        return completions.stream().filter(s -> s.toLowerCase().startsWith(args[args.length -1].toLowerCase())).toList();
    }
}

