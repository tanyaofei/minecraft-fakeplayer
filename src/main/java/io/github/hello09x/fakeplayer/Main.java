package io.github.hello09x.fakeplayer;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.hello09x.fakeplayer.command.Commands;
import io.github.hello09x.fakeplayer.listener.PlayerListeners;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private static NMS nms;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        try {
            nms = NMS.getInstance();
        } catch (Throwable e) {
            if (e instanceof UnsupportedOperationException) {
                throw new ExceptionInInitializerError(String.format("不支持的核心版本, minecraftVersion:%s, bukkitVersion: %s, version:%s", Bukkit.getMinecraftVersion(), Bukkit.getBukkitVersion(), Bukkit.getVersion()));
            } else {
                throw new ExceptionInInitializerError(e);
            }
        }

        Commands.register();
        CommandAPI.onEnable();

        {
            getServer().getMessenger().registerIncomingPluginChannel(Main.getInstance(), "BungeeCord", WildFakeplayerManager.instance);
            getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        }

        {
            getServer().getPluginManager().registerEvents(PlayerListeners.instance, this);
        }

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

}
