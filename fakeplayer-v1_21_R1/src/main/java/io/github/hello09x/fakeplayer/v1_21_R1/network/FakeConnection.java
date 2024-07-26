package io.github.hello09x.fakeplayer.v1_21_R1.network;

import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.logging.Logger;

public class FakeConnection extends Connection {

    private final static Logger log = Main.getInstance().getLogger();
    private final FakeplayerManager manager = Main.getInjector().getInstance(FakeplayerManager.class);

    public FakeConnection(@NotNull InetAddress address) {
        super(PacketFlow.SERVERBOUND);
        this.channel = new FakeChannel(null, address);
        this.address = this.channel.remoteAddress();
        Connection.configureSerialization(this.channel.pipeline(), PacketFlow.SERVERBOUND, false, null);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener listener) {

    }

    @Override
    public void send(Packet<?> packet) {

    }

//    public void setProtocolAttr(@NotNull ConnectionProtocol protocol) {
//        this.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(protocol.codec(PacketFlow.SERVERBOUND));
//        this.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(protocol.codec(PacketFlow.CLIENTBOUND));
//    }

}