package io.github.hello09x.fakeplayer.v1_20_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.v1_20_R1.network.EmptyConnection;
import io.github.hello09x.fakeplayer.v1_20_R1.network.EmptyServerGamePacketListenerImpl;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class NMSNetworkImpl implements NMSNetwork {

    private final @NotNull EmptyConnection connection;

    public NMSNetworkImpl(
            @NotNull InetAddress address
    ) {
        this.connection = new EmptyConnection(address);
    }

    @Override
    public @NotNull NMSServerGamePacketListener placeNewPlayer(@NotNull Server server, @NotNull Player player) {
        var handle = ((CraftPlayer) player).getHandle();
        var listener = new EmptyServerGamePacketListenerImpl(
                ((CraftServer) server).getServer(),
                this.connection,
                handle
        );
        handle.connection = listener;
        ((CraftServer) server).getHandle().placeNewPlayer(
                this.connection,
                handle
        );
        return listener;
    }

}
