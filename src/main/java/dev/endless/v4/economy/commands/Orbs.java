package dev.endless.v4.economy.commands;

import dev.endless.v4.economy.managers.OrbsManager;
import dev.endless.v4.economy.utils.AmountParser;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Orbs implements TabExecutor {

    public static List<UUID> awaiting = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)){
                sender.sendMessage("§c§lFAILED | §7You don't have your own orbs since you are not a player.");
                return false;
            }
            double balance = OrbsManager.orbs.getOrDefault(player.getUniqueId(), 0.0);
            sender.sendMessage("§7Orbs: §6" + balance);
            return false;
        }

        String cmd = args[0];
        if (cmd.equals("send")) {

            if (!(sender instanceof Player player)){
                sender.sendMessage("§c§lFAILED | §7Only players can send orbs.");
                return false;
            }

            if (args.length != 3) {
                player.sendMessage("§c§lFAILED | §7Usage: /orbs send <player> [amount]");
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

            awaiting.add(player.getUniqueId());

            Component acceptBtn = Component.text("[Yes]")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true)
                    .clickEvent(ClickEvent.runCommand("/orbaccept " + target.getName() + " " + amount))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to send the orbs", NamedTextColor.GRAY)));

            Component spacer = Component.text(" ");

            Component noBtn = Component.text("[Cancel]")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.BOLD, true)
                    .clickEvent(ClickEvent.runCommand("/orbdeny"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to cancel", NamedTextColor.GRAY)));

            Component finalMessage = Component.text("§7Are you sure you want to send premium currency?\n")
                            .append(acceptBtn)
                            .append(spacer)
                            .append(noBtn);

            player.sendMessage(finalMessage);
        }
        else {
            Player target = Bukkit.getPlayerExact(cmd);
            if (target == null) {
                sender.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
                return false;
            }
            double balance = OrbsManager.orbs.getOrDefault(target.getUniqueId(), 0.0);

            if (target == sender) {
                sender.sendMessage("§7Orbs: §6" + balance);
            }
            else {
                sender.sendMessage("§e" + target.getName() + "§7's orbs: §6" + balance);
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
