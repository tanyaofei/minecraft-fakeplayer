package io.github.hello09x.fakeplayer.api.spi;

import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

public interface VersionSupport {

    static @Nullable VersionSupport getInstance() {
        return ServiceLoader
                .load(VersionSupport.class, VersionSupport.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(VersionSupport::isSupported)
                .findAny()
                .orElse(null);
    }

    @NotNull NMSEntity entity(@NotNull Entity entity);

    @NotNull NMSServer server(@NotNull Server server);

    @NotNull NMSServerLevel world(@NotNull World world);

    @NotNull NMSServerPlayer player(@NotNull Player player);

    @NotNull NMSNetwork network();

    boolean isSupported();

    @NotNull ActionTicker createAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting);

}
