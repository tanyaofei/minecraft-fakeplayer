package io.github.hello09x.fakeplayer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zaxxer.hikari.HikariConfig;
import io.github.hello09x.bedrock.util.RegistrablePlugin;
import io.github.hello09x.devtools.core.PluginEventModule;
import io.github.hello09x.devtools.core.TranslationModule;
import io.github.hello09x.devtools.core.transaction.TranslationConfig;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.Lambdas;
import io.github.hello09x.devtools.database.DatabaseModule;
import io.github.hello09x.fakeplayer.core.command.CommandRegistry;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerListener;
import io.github.hello09x.fakeplayer.core.listener.PlayerListener;
import io.github.hello09x.fakeplayer.core.listener.ReplenishListener;
import io.github.hello09x.fakeplayer.core.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.core.util.update.UpdateChecker;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public final class Main extends RegistrablePlugin {

    @Getter
    private static Main instance;

    private Injector injector;

    @Override
    public void onLoad() {
        instance = this;

        injector = Guice.createInjector(
                new PluginEventModule(this),
                new DatabaseModule(this, Lambdas.configure(new HikariConfig(), config -> {
                    config.setDriverClassName("org.sqlite.JDBC");
                    config.setMaximumPoolSize(1);
                    config.setJdbcUrl("jdbc:sqlite:" + new File(this.getDataFolder(), "data.db").getAbsolutePath());
                    config.setConnectionTimeout(1000L);
                })),
                new TranslationModule(Main.getInstance(), new TranslationConfig(
                        "message/message",
                        TranslatorUtils.getDefaultLocale(Main.getInstance()))),
                new FakeplayerModule()
        );
    }

    @Override
    public void onEnable() {
        injector.injectMembers(this);
        injector.getInstance(CommandRegistry.class).register();
        {
            var messenger = getServer().getMessenger();
            messenger.registerIncomingPluginChannel(this, "BungeeCord", injector.getInstance(WildFakeplayerManager.class));
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        }

        {
            var manager = getServer().getPluginManager();
            manager.registerEvents(injector.getInstance(PlayerListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerListener.class), this);
            manager.registerEvents(injector.getInstance(ReplenishListener.class), this);
        }

        if (injector.getInstance(FakeplayerConfig.class).isCheckForUpdates()) {
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
        super.onDisable();
        {
            var messenger = getServer().getMessenger();
            messenger.unregisterIncomingPluginChannel(this);
            messenger.unregisterOutgoingPluginChannel(this);
        }
    }

    public static @NotNull Injector getInjector() {
        return instance.injector;
    }

}
