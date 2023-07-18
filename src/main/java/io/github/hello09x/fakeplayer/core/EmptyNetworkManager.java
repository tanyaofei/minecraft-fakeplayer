package io.github.hello09x.fakeplayer.core;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

import java.io.IOException;

public class EmptyNetworkManager extends Connection {
    public EmptyNetworkManager(PacketFlow flag) throws IOException {
        super(flag);
        this.channel = new EmptyChannel(null);
        this.address = this.channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet packet, PacketSendListener listener) {
    }

    @Override
    public void handleDisconnection() {
        super.handleDisconnection();
    }
}