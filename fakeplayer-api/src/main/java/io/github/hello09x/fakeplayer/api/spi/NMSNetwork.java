package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSNetwork {

    /**
     * 绑定一个虚拟的游戏连接
     *
     * @param server 服务器
     * @param player 假人玩家
     */
    @NotNull NMSServerGamePacketListener placeNewPlayer(@NotNull Server server, @NotNull Player player);

    /**
     * 获取服务侧游戏数据包监听器
     * <p>在获取之前需要先执行了 {@link #placeNewPlayer(Server, Player)} 才会初始化值</p>
     */
    @NotNull
    NMSServerGamePacketListener getServerGamePacketListener() throws IllegalStateException;

}
