package io.github.hello09x.fakeplayer.v1_21_4.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.v1_21_4.network.FakeConnection;
import io.github.hello09x.fakeplayer.v1_21_4.network.FakeServerGamePacketListenerImpl;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class NMSNetworkImpl implements NMSNetwork {

    @NotNull
    private final FakeConnection connection;

    private NMSServerGamePacketListener serverGamePacketListener;

    public NMSNetworkImpl(
            @NotNull InetAddress address
    ) {
        this.connection = new FakeConnection(address);
    }

    @NotNull
    @Override
    public NMSServerGamePacketListener placeNewPlayer(
            @NotNull Server server,
            @NotNull Player player
    ) {
        var handle = ((CraftPlayer) player).getHandle();
        var cookie = CommonListenerCookie.createInitial(((CraftPlayer) player).getProfile(), false);

        ((CraftServer) server).getHandle().placeNewPlayer(
                this.connection,
                handle,
                cookie
        );

        var listener = new FakeServerGamePacketListenerImpl(
                ((CraftServer) server).getServer(),
                this.connection,
                handle,
                cookie
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
