package io.github.hello09x.fakeplayer.v1_20_R2.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.v1_20_R2.network.EmptyConnection;
import io.github.hello09x.fakeplayer.v1_20_R2.network.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.v1_20_R2.network.EmptyServerGamePacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class NMSNetworkImpl implements NMSNetwork {

    @Override
    public @NotNull EmptyServerGamePacketListener bindEmptyServerGamePacketListener(@NotNull Server server, @NotNull Player player, @NotNull InetAddress address) {
        var connect = new EmptyConnection(PacketFlow.CLIENTBOUND, address);
        var listener = new EmptyServerGamePacketListener(
                ((CraftServer) server).getServer(),
                connect,
                ((CraftPlayer) player).getHandle(),
                CommonListenerCookie.createInitial(((CraftPlayer) player).getProfile())
        );
        connect.setListener(listener);
        return listener;
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
                ((CraftPlayer) player).getHandle(),
                CommonListenerCookie.createInitial(((CraftPlayer) player).getProfile())
        );
        connect.setListener(listener);
    }

}
