package io.github.hello09x.fakeplayer.v1_20_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.v1_20_R1.network.FakeConnection;
import io.github.hello09x.fakeplayer.v1_20_R1.network.FakeServerGamePacketListenerImpl;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class NMSNetworkImpl implements NMSNetwork {

    private final @NotNull FakeConnection connection;

    private NMSServerGamePacketListener serverGamePacketListener;

    public NMSNetworkImpl(
            @NotNull InetAddress address
    ) {
        this.connection = new FakeConnection(address);
    }

    @Override
    public @NotNull NMSServerGamePacketListener placeNewPlayer(@NotNull Server server, @NotNull Player player) {
        var handle = ((CraftPlayer) player).getHandle();
        ((CraftServer) server).getHandle().placeNewPlayer(
                this.connection,
                handle
        );
        var listener = new FakeServerGamePacketListenerImpl(
                ((CraftServer) server).getServer(),
                this.connection,
                handle
        );
        this.serverGamePacketListener = listener;
        handle.connection = listener;
        return listener;
    }

    @NotNull
    @Override
    public NMSServerGamePacketListener getServerGamePacketListener() throws IllegalStateException {
        if (this.serverGamePacketListener == null) {
            throw new IllegalStateException("not initialized");
        }
        return this.serverGamePacketListener;
    }

}
