package dev.endless.v4.economy.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endless.v4.economy.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BitsManager {

    public static final HashMap<UUID, Double> bits = new HashMap<>();

    public static void requestBalanceSync(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GET_BALANCE");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF("bits");

        player.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
    }

    public static boolean send(Player sender, Player target, double amount) {
        double senderBalance = bits.getOrDefault(sender.getUniqueId(), 0.0);

        if (senderBalance < amount || amount <= 0) {
            return false;
        }

        bits.put(sender.getUniqueId(), senderBalance - amount);
        double targetBalance = bits.getOrDefault(target.getUniqueId(), 0.0);
        bits.put(target.getUniqueId(), targetBalance + amount);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TRANSFER_BALANCE");
        out.writeUTF(sender.getUniqueId().toString());
        out.writeUTF(target.getUniqueId().toString());
        out.writeUTF("bits");
        out.writeDouble(amount);

        sender.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
        return true;
    }

    public static void giveReward(Player player, double amount) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MODIFY_BALANCE");
        out.writeUTF("SERVER");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF("bits");
        out.writeUTF("daily");
        out.writeDouble(amount);

        player.sendPluginMessage(Main.getInstance(), "chunkverse:economy", out.toByteArray());
    }
}