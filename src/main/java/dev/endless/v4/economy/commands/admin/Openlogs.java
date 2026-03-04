package dev.endless.v4.economy.commands.admin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Openlogs implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c§lFAILED | §7Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("chunkverse.economy.logs")) {
            player.sendMessage("§c§lFAILED | §7You don't have permission to do this.");
            return true;
        }

        player.sendMessage("§e§lLOADING | §7Fetching transactin logs from database...");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("REQUEST_LOGS");
        out.writeUTF(player.getUniqueId().toString());

        player.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
        return true;
    }
}
