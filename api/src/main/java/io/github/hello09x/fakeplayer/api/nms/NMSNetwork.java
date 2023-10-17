package io.github.hello09x.fakeplayer.api.nms;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public interface NMSNetwork {

    void bindEmptyServerGamePacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address);

    void bindEmptyLoginPacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address);

}
