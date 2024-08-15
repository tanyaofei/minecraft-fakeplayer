package io.github.hello09x.fakeplayer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.hello09x.devtools.command.CommandModule;
import io.github.hello09x.devtools.core.TranslationModule;
import io.github.hello09x.devtools.core.translation.TranslationConfig;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.database.DatabaseModule;
import io.github.hello09x.fakeplayer.core.command.CommandRegistry;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerLifecycleListener;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerListener;
import io.github.hello09x.fakeplayer.core.listener.PlayerListener;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansion;
import io.github.hello09x.fakeplayer.core.util.update.UpdateChecker;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    private Injector injector;

    private long loadAt;

    @Override
    public void onLoad() {
        loadAt = System.currentTimeMillis();
        instance = this;
    }

    @Override
    public void onEnable() {
        injector = Guice.createInjector(
                new FakeplayerModule(),
                new CommandModule(),
                new DatabaseModule(),
                new TranslationModule(new TranslationConfig(
                        "message/message",
                        TranslatorUtils.getDefaultLocale(Main.getInstance())))
        );

        injector.getInstance(CommandRegistry.class).register();
        {
            var messenger = getServer().getMessenger();
            messenger.registerIncomingPluginChannel(this, "BungeeCord", injector.getInstance(WildFakeplayerManager.class));
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        }

        {
            var manager = getServer().getPluginManager();
            manager.registerEvents(injector.getInstance(PlayerListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerLifecycleListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerAutofishManager.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerReplenishManager.class), this);
            manager.registerEvents(injector.getInstance(InvseeManager.class), this);
        }

        {
            var placeholderExpansion = injector.getInstance(FakeplayerPlaceholderExpansion.class);
            if (placeholderExpansion != null) {
                if (placeholderExpansion.register()) {
                    getServer().getPluginManager().registerEvents(placeholderExpansion, this);
                    getLogger().info("Successfully registered PlaceholderExpansion");
                }
            }
        }

        if (injector.getInstance(FakeplayerConfig.class).isCheckForUpdates()) {
            checkForUpdatesAsync();
        }

        getLogger().info("Enabled in %d ms".formatted(System.currentTimeMillis() - loadAt));
    }

    public void checkForUpdatesAsync() {
        CompletableFuture.runAsync(() -> {
            var meta = this.getPluginMeta();
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
                    log.info("New version: " + release.getTagName());
                    log.info("Address: " + meta.getWebsite());
                    log.info("Update Log");
                    for (var line : release.getBody().split("\n")) {
                        log.info("\t" + line);
                    }
                }

            } catch (Throwable e) {
                getLogger().warning("Error on checking for updates: " + e.getMessage());
            }
        });
    }

    @Override
    public void onDisable() {
        {
            Exceptions.suppress(this, () -> {
                var messenger = getServer().getMessenger();
                messenger.unregisterIncomingPluginChannel(this);
                messenger.unregisterOutgoingPluginChannel(this);
            });
        }
    }

    public static @NotNull Injector getInjector() {
        return instance.injector;
    }

}
