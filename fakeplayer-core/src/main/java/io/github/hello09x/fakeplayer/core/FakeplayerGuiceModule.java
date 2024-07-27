package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import io.github.hello09x.devtools.transaction.PluginTranslator;
import io.github.hello09x.devtools.transaction.TranslatorUtils;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.invsee.DefaultInvseeImpl;
import io.github.hello09x.fakeplayer.core.manager.invsee.Invsee;
import org.bukkit.Bukkit;

import java.util.ServiceLoader;

public class FakeplayerGuiceModule extends AbstractModule {


    @Override
    protected void configure() {
        var pluginTranslator = this.pluginTranslator();

        super.bind(FakeplayerConfig.class).toInstance(this.fakeplayerConfig());
        super.bind(PluginTranslator.class).toInstance(pluginTranslator);
        super.bind(NMSBridge.class).toInstance(this.nmsBridge());
        super.bind(Invsee.class).toInstance(new DefaultInvseeImpl(pluginTranslator));
    }

    private FakeplayerConfig fakeplayerConfig() {
        return new FakeplayerConfig(Main.getInstance(), "13");
    }


    private PluginTranslator pluginTranslator() {
        return PluginTranslator.of(
                Main.getInstance(),
                "message/message",
                TranslatorUtils.getDefaultLocale(Main.getInstance())
        );
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
