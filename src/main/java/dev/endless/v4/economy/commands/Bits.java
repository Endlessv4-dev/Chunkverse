package dev.endless.v4.economy.commands;

import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.utils.AmountParser;
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

        if (args.length == 0) {
            if (!(sender instanceof Player player)){
                sender.sendMessage("§c§lFAILED | §7You don't have your own bits since you are not a player.");
                return false;
            }
            double balance = BitsManager.bits.getOrDefault(player.getUniqueId(), 0.0);
            sender.sendMessage("§7Bits: §6" + balance);
            return false;
        }

        String cmd = args[0];
        if (cmd.equals("send")) {

            if (!(sender instanceof Player player)){
                sender.sendMessage("§c§lFAILED | §7Only players can send bits.");
                return false;
            }

            if (args.length != 3) {
                player.sendMessage("§c§lFAILED | §7Usage: /bits send <player> [amount]");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }

            double amount;
            try {
                amount = AmountParser.parse(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c§lFAILED | §7Wrong amount (example: 150, 1.5K, 2M)");
                return true;
            }

            if (BitsManager.send(player, target, amount)) {
                player.sendMessage("§7You have sent §6" + amount + " §7bits to §e" + target.getName() + "§7.");
                target.sendMessage("§7You received §6" + amount + " §7bits from §e" + player.getName() + "§7.");
            }
            else {
                player.sendMessage("§c§lFAILED | §7You cannot send more bits than the amount you have.");
            }

        }
        else {
            Player target = Bukkit.getPlayerExact(cmd);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }
            double balance = BitsManager.bits.getOrDefault(target.getUniqueId(), 0.0);

            if (target == sender) {
                sender.sendMessage("§7Bits: §6" + balance);
            }
            else {
                sender.sendMessage("§e" + target.getName() + "§7's bits: §6" + balance);
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("send");
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 2) {
            if (args[0].equals("send")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3) {
            String action = args[0].toLowerCase();
            if (action.equals("send")) {
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
