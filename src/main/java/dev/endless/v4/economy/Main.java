package dev.endless.v4.economy;

import dev.endless.v4.economy.commands.OrbAccept;
import dev.endless.v4.economy.commands.OrbDeny;
import dev.endless.v4.economy.commands.admin.*;
import dev.endless.v4.economy.listeners.AuthListener;
import dev.endless.v4.economy.listeners.ProxyListener;
import dev.endless.v4.economy.placeholders.BitsExpansion;
import dev.endless.v4.economy.placeholders.OrbsExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {


    private static Main instance;

    @Override
    public void onEnable() {

        instance = this;

//        saveDefaultConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "chunkverse:economy");
        getServer().getMessenger().registerIncomingPluginChannel(this, "chunkverse:economy", new ProxyListener());

        registerListeners();
        registerCommands();
        registerPlaceholders();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("orbsa")).setExecutor(new Orbs());
        Objects.requireNonNull(getCommand("bitsa")).setExecutor(new Bits());
        Objects.requireNonNull(getCommand("orbs")).setExecutor(new dev.endless.v4.economy.commands.Orbs());
        Objects.requireNonNull(getCommand("bits")).setExecutor(new dev.endless.v4.economy.commands.Bits());
        Objects.requireNonNull(getCommand("orbaccept")).setExecutor(new OrbAccept());
        Objects.requireNonNull(getCommand("orbdeny")).setExecutor(new OrbDeny());
        Objects.requireNonNull(getCommand("openlogs")).setExecutor(new Openlogs());
//        Objects.requireNonNull(getCommand("rollback")).setExecutor(new Rollback());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AuthListener(), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI not found. Placeholders will be unavailable.");
            return;
        }

        new OrbsExpansion(this).register();
        new BitsExpansion(this).register();
        getLogger().info("PlaceholderAPI hooked. Placeholders: %orbs_balance%, %bits_balance%");
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }
}
