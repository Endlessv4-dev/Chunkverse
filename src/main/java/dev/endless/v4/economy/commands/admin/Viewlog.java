package dev.endless.v4.economy.commands.admin;

import dev.endless.v4.economy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Viewlog implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String @NotNull [] args) {

        File logFile = new File(Main.getInstance().getDataFolder(), "transactions.log");
        if (logFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(logFile.toPath());

                int start = Math.max(0, lines.size() - 10);
                for (int i = start; i < lines.size(); i++) {
                    sender.sendMessage("§7" + lines.get(i));
                }
            } catch (IOException e) {
                sender.sendMessage("§c§lFAILED | §7Error while trying to read the file.");
            }
        }

        return false;
    }
}
