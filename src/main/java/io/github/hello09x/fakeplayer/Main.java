package io.github.hello09x.fakeplayer;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.hello09x.fakeplayer.command.Commands;
import io.github.hello09x.fakeplayer.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.listener.PlayerListeners;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import io.github.hello09x.fakeplayer.util.update.UpdateChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

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

        if (FakeplayerConfig.instance.isCheckForUpdates()) {
            checkForUpdatesAsync();
        }
    }

    public void checkForUpdatesAsync() {
        CompletableFuture.runAsync(() -> {
            var checker = new UpdateChecker("tanyaofei", "minecraft-fakeplayer");
            try {
                var release = checker.getLastRelease();
                if (!release.getTagName().equals(getPluginMeta().getVersion())) {
                    var log = getLogger();
                    log.info("检测到新的版本: " + release.getTagName());
                    log.info("前往此处下载 https://github.com/tanyaofei/minecraft-fakeplayer");
                    log.info("更新日志");
                    for (var line : release.getBody().split("\n")) {
                        log.info("\t" + line);
                    }
                }
            } catch (Throwable e) {
                getLogger().warning("检测新版本发生异常: " + e.getMessage());
            }
        });
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
