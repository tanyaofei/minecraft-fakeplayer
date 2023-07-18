package io.github.hello09x.fakeplayer;

import io.github.hello09x.fakeplayer.command.FakePlayerCommand;
import io.github.hello09x.fakeplayer.listener.PlayerDeathListener;
import io.github.hello09x.fakeplayer.listener.PlayerQuitListener;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
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

        registerListeners();
    }

    @Override
    public void onDisable() {
        FakePlayerManager.instance.removeFakePlayers();
    }


    private void registerListeners() {
        if (FakeplayerProperties.instance.isFollowQuiting()) {
            getServer().getPluginManager().registerEvents(PlayerQuitListener.instance, getInstance());
        }

        getServer().getPluginManager().registerEvents(PlayerDeathListener.instance, getInstance());
    }

}
