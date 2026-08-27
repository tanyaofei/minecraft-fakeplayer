package io.github.hello09x.fakeplayer.v26_1_2.network;

import io.github.hello09x.fakeplayer.core.network.FakeChannel;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;

public class FakeConnection extends Connection {

    public FakeConnection(@NotNull InetAddress address) {
        super(PacketFlow.SERVERBOUND);
        this.channel = new FakeChannel(null, address);
        this.address = this.channel.remoteAddress();
        Connection.configureSerialization(this.channel.pipeline(), PacketFlow.SERVERBOUND, false, null);
    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener listener) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush) {
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void disconnect(net.minecraft.network.chat.Component reason) {
        if (this.channel instanceof FakeChannel fakeChannel) {
            fakeChannel.close();
        }
        super.disconnect(reason);
    }

    @Override
    public void disconnect(net.minecraft.network.DisconnectionDetails details) {
        if (this.channel instanceof FakeChannel fakeChannel) {
            fakeChannel.close();
        }
        super.disconnect(details);
    }
}
