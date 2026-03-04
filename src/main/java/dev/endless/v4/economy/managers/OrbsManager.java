package dev.endless.v4.economy.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class OrbsManager {

    public static final HashMap<UUID, Double> orbs = new HashMap<>();

    public static void requestBalanceSync(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GET_BALANCE");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF("orbs");

        player.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
    }

    public static boolean send(Player sender, Player target, double amount) {
        double senderBalance = orbs.getOrDefault(sender.getUniqueId(), 0.0);

        if (senderBalance < amount || amount <= 0) {
            return false;
        }

        orbs.put(sender.getUniqueId(), senderBalance - amount);
        double targetBalance = orbs.getOrDefault(target.getUniqueId(), 0.0);
        orbs.put(target.getUniqueId(), targetBalance + amount);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TRANSFER_BALANCE");
        out.writeUTF(sender.getUniqueId().toString());
        out.writeUTF(target.getUniqueId().toString());
        out.writeUTF("orbs");
        out.writeDouble(amount);

        sender.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
        return true;
    }

}