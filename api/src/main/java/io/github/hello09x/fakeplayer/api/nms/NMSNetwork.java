package io.github.hello09x.fakeplayer.api.nms;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSNetwork {

    void bindEmptyServerGamePacketListener(@NotNull Server server, @NotNull Player player);

    void bindEmptyLoginPacketListener(@NotNull Server server, @NotNull Player player);

}
