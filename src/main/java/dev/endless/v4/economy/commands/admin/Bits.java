package dev.endless.v4.economy.commands.admin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c§lFAILED | §7Usage: /bitsa <give|take|set|reset> <player> [amount]");
            return true;
        }

        String action = args[0].toLowerCase();

        List<String> validActions  = Arrays.asList("give", "take", "set", "reset");
        if (!validActions.contains(action)) {
            sender.sendMessage("§c§lFAILED | §7Invalid action! Use: give, take, set or reset.");
            return true;
        }

        String targetName = args[1];

        if (!sender.hasPermission("chunkverse.bits." + action)) {
            sender.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage("§c§lFAILED | §7This player is not online.");
            return true;
        }

        double amount = 0.0;
        if (!action.equals("reset")) {
            if (args.length < 3) {
                sender.sendMessage("§c§lFAILED | §7Usage: /bitsa " + action + " <player> <amount>");
                return true;
            }
            try {
                amount = AmountParser.parse(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c§lFAILED | §7Invalid amount (e.g., 100, 1.5K, 2M)");
                return true;
            }
        }

        sendVelocityUpdate(sender, target, action, amount);
        return true;
    }

    private void sendVelocityUpdate(CommandSender sender, Player target, String action, double amount) {
        String executor = (sender instanceof Player p) ? p.getUniqueId().toString() : "SERVER";

        String velocityAction = action.equals("reset") ? "set" : action;
        double finalAmount = action.equals("reset") ? 0.0 : amount;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MODIFY_BALANCE");
        out.writeUTF(executor);
        out.writeUTF(target.getUniqueId().toString());
        out.writeUTF("bits");
        out.writeUTF(velocityAction);
        out.writeDouble(finalAmount);

        target.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
        sender.sendMessage("§a§lSUCCESS | §7Bits request sent to Proxy.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("give", "take", "set", "reset");
            for (String s : subCommands) {
                if (sender.hasPermission("chunkverse.bits." + s)) completions.add(s);
            }
        } else if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        } else if (args.length == 3 && !args[0].equalsIgnoreCase("reset")) {
            completions.addAll(Arrays.asList("1", "10", "100", "1000", "1k", "10k"));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}