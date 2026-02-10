package dev.endless.v4.economy.listeners;

import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.UUID;

public class AuthListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        claimDailyReward(player);
    }

    private void claimDailyReward(Player player) {
        UUID uuid = player.getUniqueId();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
           LocalDate lastClaimed = Database.getLastRewardDate(uuid);
           int currentStreak = Database.getRewardStreak(uuid);

           if (lastClaimed != null && lastClaimed.equals(today)) {
               player.sendMessage("§7You have already gotten today's login reward.");
               return;
           }

           int newStreak = 1;
           if (lastClaimed != null && lastClaimed.equals(yesterday)) {
               newStreak = currentStreak += 1;
           }

           double amount = 100;
           if (newStreak % 10  == 0) {
               amount += 500;
               player.sendMessage("§7Daily reward: §b100 Bit");
               player.sendMessage("§6§lCONGRATUALITONS! §7You have reached a 10 streak login bonus (§b+500 Bit§7)!");
           } else {
               player.sendMessage("§7Daily reward: §b100 Bit");
           }

           Database.updateRewardData(uuid, today, newStreak);
           BitsManager.giveReward(player, amount);
           player.sendMessage("§7Daily reward received! Current streak: §b" + (newStreak == 0 ? 10 : newStreak) + " §7day.");
        });
    }
}
