package dev.endless.v4.economy.commands;

import dev.endless.v4.economy.managers.OrbsManager;
import dev.endless.v4.economy.utils.AmountParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OrbAccept implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
            return false;
        }

        if (!Orbs.awaiting.contains(player.getUniqueId())) {
            player.sendMessage("§c§lFAILED | §7There is nothing to accept.");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§c§lFAILED | §7This player doesn't exist or is not online.");
            return false;
        }

        double amount;
        try {
            amount = AmountParser.parse(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c§lFAILED | §7Wrong amount (example: 150, 1.5K, 2M)");
            return true;
        }

        if (OrbsManager.send(player, target, amount)) {
            player.sendMessage("§7You have sent §6" + amount + " §7orbs to §e" + target.getName() + "§7.");
            target.sendMessage("§7You received §6" + amount + " §7orbs from §e" + player.getName() + "§7.");
            Orbs.awaiting.remove(player.getUniqueId());
        }
        else {
            player.sendMessage("§c§lFAILED | §7You cannot send more orbs than the amount you have.");
        }

        return false;
    }
}
