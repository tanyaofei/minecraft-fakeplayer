package io.github.hello09x.fakeplayer.core;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class EmptyConnection extends Connection {
    public EmptyConnection(PacketFlow flag) {
        super(flag);
        this.channel = new EmptyChannel(null);
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