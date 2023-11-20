package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSNetwork {

    /**
     * 绑定一个虚拟的游戏连接
     *
     * @param server  服务器
     * @param player  假人玩家
     */
    @NotNull NMSServerGamePacketListener placeNewPlayer(@NotNull Server server, @NotNull Player player);

}
