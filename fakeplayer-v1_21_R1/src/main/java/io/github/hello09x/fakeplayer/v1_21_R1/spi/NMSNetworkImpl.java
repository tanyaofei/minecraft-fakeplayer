package io.github.hello09x.fakeplayer.v1_21_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.v1_21_R1.network.FakeConnection;
import io.github.hello09x.fakeplayer.v1_21_R1.network.FakeServerGamePacketListenerImpl;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
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
//        this.connection.setProtocolAttr(ConnectionProtocol.PLAY);
        var handle = ((CraftPlayer) player).getHandle();

        // false 应该是 1.21 新增的玩家跨服标识符
        var cookie = CommonListenerCookie.createInitial(((CraftPlayer) player).getProfile(), false);

        var listener = new FakeServerGamePacketListenerImpl(
                ((CraftServer) server).getServer(),
                this.connection,
                handle,
                cookie
        );
        handle.connection = listener;
        ((CraftServer) server).getHandle().placeNewPlayer(
                this.connection,
                handle,
                cookie
        );
        this.serverGamePacketListener = listener;
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
