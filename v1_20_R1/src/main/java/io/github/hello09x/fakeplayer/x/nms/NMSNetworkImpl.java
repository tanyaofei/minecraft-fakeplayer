package io.github.hello09x.fakeplayer.x.nms;

import io.github.hello09x.fakeplayer.api.nms.NMSNetwork;
import io.github.hello09x.fakeplayer.x.network.EmptyConnection;
import io.github.hello09x.fakeplayer.x.network.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.x.network.EmptyServerGamePacketListener;
import net.minecraft.network.protocol.PacketFlow;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class NMSNetworkImpl implements NMSNetwork {

    @Override
    public void bindEmptyServerGamePacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address) {
        var connect = new EmptyConnection(PacketFlow.CLIENTBOUND, address);
        var listener = new EmptyServerGamePacketListener(
                ((CraftServer) server).getServer(),
                connect,
                ((CraftPlayer) player).getHandle()
        );
        connect.setListener(listener);
    }

    @Override
    public void bindEmptyLoginPacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address) {
        var connect = new EmptyConnection(PacketFlow.CLIENTBOUND, address);
        var listener = new EmptyLoginPacketListener(
                ((CraftServer) server).getServer(),
                connect
        );
        ((CraftServer) server).getHandle().placeNewPlayer(
                listener.connection,
                ((CraftPlayer) player).getHandle()
        );
        connect.setListener(listener);
    }

}
