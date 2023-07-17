package io.github.hello09x.fakeplayer;

import io.github.hello09x.fakeplayer.command.FakePlayerCommand;
import io.github.hello09x.fakeplayer.listener.PlayerDeathListener;
import io.github.hello09x.fakeplayer.listener.PlayerQuitListener;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        {
            getServer().getPluginCommand("fakeplayer").setExecutor(FakePlayerCommand.instance);
        }

        {
            getServer().getPluginManager().registerEvents(PlayerQuitListener.instance, Main.getInstance());
            getServer().getPluginManager().registerEvents(PlayerDeathListener.instance, Main.getInstance());
        }

    }

    @Override
    public void onDisable() {
        FakePlayerManager.instance.removeFakePlayers();
    }
}
