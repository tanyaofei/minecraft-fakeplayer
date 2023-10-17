package io.github.hello09x.fakeplayer.core;

import io.github.hello09x.fakeplayer.api.spi.VersionSupport;
import io.github.hello09x.fakeplayer.core.command.CommandRegistry;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.listener.PlayerListeners;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.core.util.update.UpdateChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private static VersionSupport versionSupport;

    @Override
    public void onLoad() {
        versionSupport = VersionSupport.getInstance();
        if (versionSupport == null) {
            throw new ExceptionInInitializerError("不支持当前 minecraft 版本: " + Bukkit.getMinecraftVersion());
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        CommandRegistry.register();
        {
            var messenger = getServer().getMessenger();
            messenger.registerIncomingPluginChannel(this, "BungeeCord", WildFakeplayerManager.instance);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
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
            var meta = getPluginMeta();
            var checker = new UpdateChecker("tanyaofei", "minecraft-fakeplayer");
            try {
                var release = checker.getLastRelease();

                var current = meta.getVersion();
                var other = release.getTagName();
                if (other.charAt(0) == 'v') {
                    other = other.substring(1);
                }

                if (UpdateChecker.isNew(current, other)) {
                    var log = getLogger();
                    log.info("检测到新的版本: " + release.getTagName());
                    log.info("前往此处下载 " + meta.getWebsite());
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
        FakeplayerManager.instance.removeAll("plugin disabled");
        UsedIdRepository.instance.saveAll();
        FakeplayerManager.instance.onDisable();
        WildFakeplayerManager.instance.onDisable();

        {
            var messenger = getServer().getMessenger();
            messenger.unregisterIncomingPluginChannel(this);
            messenger.unregisterOutgoingPluginChannel(this);
        }
    }

}
