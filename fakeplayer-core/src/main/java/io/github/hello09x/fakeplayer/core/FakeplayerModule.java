package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.DefaultInvseeManagerImpl;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ServiceLoader;

public class FakeplayerModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(Plugin.class).toInstance(Main.getInstance());
    }

    @Provides
    @Singleton
    public InvseeManager invseeManager(FakeplayerManager fakeplayerManager, FakeplayerList fakeplayerList) {
        return new DefaultInvseeManagerImpl(fakeplayerManager, fakeplayerList);
    }

    @Provides
    @Singleton
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
