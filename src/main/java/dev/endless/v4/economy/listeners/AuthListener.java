package dev.endless.v4.economy.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
import dev.endless.v4.economy.managers.BitsManager;
import dev.endless.v4.economy.managers.OrbsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AuthListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!player.isOnline()) return;

            BitsManager.requestBalanceSync(player);
            OrbsManager.requestBalanceSync(player);

            claimDailyReward(player);
        }, 10L);
    }

    private void claimDailyReward(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GET_REWARD_INFO");
        out.writeUTF(player.getUniqueId().toString());

        player.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
    }
}
