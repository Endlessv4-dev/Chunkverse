package dev.endless.v4.economy;

import dev.endless.v4.economy.commands.admin.Bits;
import dev.endless.v4.economy.commands.admin.Orbs;
import dev.endless.v4.economy.managers.OrbsManager;
import dev.endless.v4.economy.utils.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Database.connect();

        OrbsManager.loadOrbs();
        registerCommands();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("orbs")).setExecutor(new Orbs());
        Objects.requireNonNull(getCommand("orbs")).setExecutor(new Bits());
    }

    @Override
    public void onDisable() {
        Database.disconnect();
    }

}
