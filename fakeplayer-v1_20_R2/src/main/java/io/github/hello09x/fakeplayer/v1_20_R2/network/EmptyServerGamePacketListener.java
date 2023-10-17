package io.github.hello09x.fakeplayer.v1_20_R2.network;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class EmptyServerGamePacketListener extends ServerGamePacketListenerImpl {
    public EmptyServerGamePacketListener(MinecraftServer minecraftServer, Connection networkManager, ServerPlayer entityPlayer, CommonListenerCookie commonListenerCookie) {
        super(minecraftServer, networkManager, entityPlayer, commonListenerCookie);
    }

    @Override
    public void send(Packet<?> packet) {
    }

}