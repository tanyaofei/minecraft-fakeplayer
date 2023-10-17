package io.github.hello09x.fakeplayer.api.nms;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public interface NMSNetwork {

    /**
     * 绑定一个虚拟的游戏连接
     *
     * @param server  服务器
     * @param player  假人玩家
     * @param address 虚拟地址
     */
    void bindEmptyServerGamePacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address);

    /**
     * 绑定一个虚拟的登陆连接
     *
     * @param server  服务器
     * @param player  家人物价
     * @param address 虚拟地址
     */
    void bindEmptyLoginPacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address);

}
