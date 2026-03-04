package dev.endless.v4.economy.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.managers.OrbsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.UUID;

public class ProxyListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("chunkverse:economy")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if  (subChannel.equals("UPDATE_CACHE")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String currency =  in.readUTF();
            double newBalance = in.readDouble();

            if (currency.equals("bits")) {
                BitsManager.bits.put(uuid, newBalance);
            }
            else if (currency.equals("orbs")) {
                OrbsManager.orbs.put(uuid, newBalance);
            }
        }
        if (subChannel.equals("REWARD_INFO_RESPONSE")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String lastRewardStr = in.readUTF();
            int currentStreak = in.readInt();

            Player target = Bukkit.getPlayer(uuid);
            if (target == null) return;

            LocalDate today = LocalDate.now();
            LocalDate lastReward = lastRewardStr.equals("none") ? null : LocalDate.parse(lastRewardStr);
            LocalDate yestarday = today.minusDays(1);

            if (lastReward != null && lastReward.equals(today))  {
                target.sendMessage("§7You have already gotten today's login reward.");
                return;
            }

            int newStreak = (lastReward != null && lastReward.equals(yestarday)) ? currentStreak + 1 : 1;
            double amount = (newStreak % 10 == 0) ? 600 : 100;

            BitsManager.giveReward(target, amount);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("UPDATE_REWARD_DATA");
            out.writeUTF(uuid.toString());
            out.writeUTF(today.toString());
            out.writeInt(newStreak);
            target.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());

            target.sendMessage("§7Daily reward: §b" + amount + " §7Bits");
            if (newStreak % 10 == 0) {
                target.sendMessage("§6§lBONUS! §7You've reached a §e" + newStreak + " §7day streak!");
                target.sendMessage("§b+500");
            }
            target.sendMessage("§7Current streak: §b" + newStreak + " §7day.");
        }
        if (subChannel.equals("LOGS_RESPONSE")) {
            UUID requesterUUID = UUID.fromString(in.readUTF());
            Player target = Bukkit.getPlayer(requesterUUID);
            if (target == null) return;

            int logCount = in.readInt();

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy. MM. dd. HH:mm");

            for (int i = 0; i < logCount; i++) {
                int id = in.readInt();
                String execRaw = in.readUTF();
                String targetRaw  = in.readUTF();
                String type = in.readUTF();
                String currency = in.readUTF();
                double amount = in.readDouble();
                long timestamp = in.readLong();

                String execName = resolveName(execRaw);
                String targetName = resolveName(targetRaw);
                String dateStr = dateFormat.format(new Date(timestamp));

                Component page = Component.text()
                        .append(Component.text("Transaction #" + id + "\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                        .append(Component.text("-------------------\n", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Executor: ", NamedTextColor.DARK_AQUA)).append(Component.text(execName + "\n", NamedTextColor.BLACK))
                        .append(Component.text("Target: ", NamedTextColor.DARK_AQUA)).append(Component.text(targetName + "\n", NamedTextColor.BLACK))
                        .append(Component.text("Type: ", NamedTextColor.DARK_AQUA)).append(Component.text(type.toUpperCase() + "\n", NamedTextColor.BLACK))
                        .append(Component.text("Currency: ", NamedTextColor.DARK_AQUA)).append(Component.text(currency.toUpperCase() + "\n", NamedTextColor.BLACK))
                        .append(Component.text("Amount: ", NamedTextColor.DARK_AQUA)).append(Component.text(String.format("%.2f\n", amount), NamedTextColor.BLACK))
                        .append(Component.text("-------------------\n", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Date:\n", NamedTextColor.DARK_AQUA)).append(Component.text(dateStr, NamedTextColor.DARK_GRAY))
                        .build();
                meta.addPages(page);
            }

            if (logCount == 0) {
                meta.addPages(Component.text("No transaction found in the database."));
            }

            meta.setTitle("Transaction Logs");
            meta.setAuthor("System");
            book.setItemMeta(meta);

            target.openBook(book);
        }
    }

    private String resolveName(String raw) {
        if (raw.equalsIgnoreCase("SERVER")) {
            return raw;
        }
        try {
            UUID uuid = UUID.fromString(raw);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            return offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
        } catch (IllegalArgumentException e) {
            return raw;
        }
    }
}
