package io.github.hello09x.fakeplayer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.hello09x.fakeplayer.command.RootCommand;
import io.github.hello09x.fakeplayer.listener.PlayerDeathListener;
import io.github.hello09x.fakeplayer.listener.PlayerPreLoginListener;
import io.github.hello09x.fakeplayer.listener.PlayerQuitListener;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.repository.UsedUUIDRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        {
            getServer().getPluginCommand("fakeplayer").setExecutor(RootCommand.instance);
        }

        registerListeners();
    }

    @Override
    public void onDisable() {
        FakePlayerManager.instance.removeAll();
        UsedUUIDRepository.instance.save();
    }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(PlayerPreLoginListener.instance, getInstance());
        getServer().getPluginManager().registerEvents(PlayerQuitListener.instance, getInstance());
        getServer().getPluginManager().registerEvents(PlayerDeathListener.instance, getInstance());
    }

}
