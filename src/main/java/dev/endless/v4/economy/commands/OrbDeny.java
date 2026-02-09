package dev.endless.v4.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OrbDeny implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
            return false;
        }

        if (!Orbs.awaiting.contains(player.getUniqueId())) {
            player.sendMessage("§c§lFAILED | §7There is nothing to deny.");
            return false;
        }

        Orbs.awaiting.remove(player.getUniqueId());
        player.sendMessage("§7You have cancelled the sending of orbs.");

        return false;
    }
}
