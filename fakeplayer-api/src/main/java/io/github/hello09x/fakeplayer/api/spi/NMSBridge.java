package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public interface NMSBridge {

    @NotNull NMSEntity fromEntity(@NotNull Entity entity);

    @NotNull NMSServer fromServer(@NotNull Server server);

    @NotNull NMSServerLevel fromWorld(@NotNull World world);

    @NotNull NMSServerPlayer fromPlayer(@NotNull Player player);

    @NotNull NMSNetwork createNetwork(@NotNull InetAddress address);

    boolean isSupported();

    @NotNull ActionTicker createAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting);

}
