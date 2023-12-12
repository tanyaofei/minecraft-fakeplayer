package io.github.hello09x.fakeplayer.v1_20_R1.network;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class DummyConnection extends Connection {
    public DummyConnection(@NotNull InetAddress address) {
        super(PacketFlow.SERVERBOUND);
        this.channel = new DummyChannel(null, address);
        this.address = this.channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet<?> packet, PacketSendListener listener) {
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void handleDisconnection() {
        super.handleDisconnection();
    }
}