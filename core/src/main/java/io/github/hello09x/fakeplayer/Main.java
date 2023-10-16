package io.github.hello09x.fakeplayer;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.hello09x.fakeplayer.command.CommandRegistry;
import io.github.hello09x.fakeplayer.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.listener.PlayerListeners;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.util.update.UpdateChecker;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

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

        CommandRegistry.register();
        CommandAPI.onEnable();

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
        CommandAPI.onDisable();
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
