package io.github.hello09x.fakeplayer.api.spi;

import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.ServiceLoader;

public interface NMSBridge {

    static @Nullable NMSBridge getInstance() {
        return ServiceLoader
                .load(NMSBridge.class, NMSBridge.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(NMSBridge::isSupported)
                .findAny()
                .orElse(null);
    }

    @NotNull NMSEntity fromEntity(@NotNull Entity entity);

    @NotNull NMSServer fromServer(@NotNull Server server);

    @NotNull NMSServerLevel fromWorld(@NotNull World world);

    @NotNull NMSServerPlayer fromPlayer(@NotNull Player player);

    @NotNull NMSNetwork createNetwork(@NotNull InetAddress address);

    boolean isSupported();

    @NotNull ActionTicker createAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting);

}
