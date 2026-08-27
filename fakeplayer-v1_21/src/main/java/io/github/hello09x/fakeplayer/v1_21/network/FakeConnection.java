package io.github.hello09x.fakeplayer.v1_21.network;

import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.network.FakeChannel;
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
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener listener) {

    }

    @Override
    public void send(Packet<?> packet) {

    }

    @Override
    public void disconnect(net.minecraft.network.chat.Component reason) {
        if (this.channel instanceof FakeChannel) {
            ((FakeChannel) this.channel).close();
        }
        super.disconnect(reason);
    }

    @Override
    public void disconnect(net.minecraft.network.DisconnectionDetails details) {
        if (this.channel instanceof FakeChannel) {
            ((FakeChannel) this.channel).close();
        }
        super.disconnect(details);
    }



}