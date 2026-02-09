package dev.endless.v4.economy.commands.admin;

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
            return false;
        }

        File logFile = new File(Main.getInstance().getDataFolder(), "transactions.log");
        if (logFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(logFile.toPath());

                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta meta = (BookMeta) book.getItemMeta();

                for (String line : lines) {
                    meta.addPages(Component.text(line));
                }

                meta.setTitle("Transaction Log");
                meta.setAuthor("System");
                book.setItemMeta(meta);

                player.openBook(book);
            } catch (IOException e) {
                sender.sendMessage("§c§lFAILED | §7Error while trying to read the file.");
            }
        }

        return false;
    }
}
