package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import io.github.hello09x.devtools.transaction.PluginTranslator;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.invsee.DefaultInvseeImpl;
import io.github.hello09x.fakeplayer.core.manager.invsee.Invsee;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class FakeplayerGuiceModule extends AbstractModule {

    private final Logger log = Main.getInstance().getLogger();

    @Override
    protected void configure() {
        super.bind(FakeplayerConfig.class).toProvider(this::fakeplayerConfig);
        super.bind(PluginTranslator.class).toProvider(this::pluginTranslator);
        super.bind(NMSBridge.class).toInstance(this.nmsBridge());
        super.bind(Invsee.class).toProvider(this::invsee);
    }

    private FakeplayerConfig fakeplayerConfig() {
        return new FakeplayerConfig(Main.getInstance(), "13");
    }

    private Invsee invsee() {
        return new DefaultInvseeImpl(Main.getInjector().getInstance(PluginTranslator.class));
    }

    private PluginTranslator pluginTranslator() {
        return PluginTranslator.of(Main.getInstance(), "message/message", Locale.of("zh"));
    }

    private NMSBridge nmsBridge() {
        var bridge = ServiceLoader
                .load(NMSBridge.class, NMSBridge.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(NMSBridge::isSupported)
                .findAny()
                .orElse(null);

        if (bridge == null) {
            throw new ExceptionInInitializerError("Unsupported Minecraft version: " + Bukkit.getMinecraftVersion());
        }
        return bridge;
    }

}
