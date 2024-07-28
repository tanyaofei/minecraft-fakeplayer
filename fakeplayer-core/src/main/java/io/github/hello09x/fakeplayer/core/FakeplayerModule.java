package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.manager.invsee.DefaultInvseeImpl;
import io.github.hello09x.fakeplayer.core.manager.invsee.Invsee;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.sql.DataSource;
import java.io.File;
import java.util.ServiceLoader;

public class FakeplayerModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(Plugin.class).toInstance(Main.getInstance());
        super.bind(NMSBridge.class).toInstance(this.nmsBridge());
        super.bind(Invsee.class).to(DefaultInvseeImpl.class);
    }

    private DataSource dataSource() {
        var config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        config.setJdbcUrl("jdbc:sqlite:" + new File(Main.getInstance().getDataFolder(), "data.db").getAbsolutePath());
        config.setConnectionTimeout(1000L);
        return new HikariDataSource(config);
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
