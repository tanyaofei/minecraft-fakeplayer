package io.github.hello09x.fakeplayer.x.network;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class EmptyConnection extends Connection {
    public EmptyConnection(@NotNull PacketFlow flag, @NotNull InetAddress address) {
        super(flag);
        this.channel = new EmptyChannel(null, address);
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