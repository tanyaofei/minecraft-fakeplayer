package io.github.hello09x.fakeplayer;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.hello09x.fakeplayer.command.Commands;
import io.github.hello09x.fakeplayer.listener.PlayerListeners;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.optional.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;
        Commands.register();
        CommandAPI.onEnable();

        {
            getServer().getMessenger().registerIncomingPluginChannel(Main.getInstance(), "BungeeCord", WildFakeplayerManager.instance);
            getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        }

        registerListeners();
    }

    @Override
    public void onDisable() {
        FakeplayerManager.instance.removeAll();
        UsedIdRepository.instance.saveAll();
        FakeplayerManager.instance.onDisable();
        WildFakeplayerManager.instance.onDisable();

        {
            getServer().getMessenger().unregisterIncomingPluginChannel(this);
            getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        }

        CommandAPI.onDisable();
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(PlayerListeners.instance, this);
    }

}
