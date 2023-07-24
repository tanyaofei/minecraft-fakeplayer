package io.github.hello09x.fakeplayer.core;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class EmptyServerGamePacketListener extends ServerGamePacketListenerImpl {
    public EmptyServerGamePacketListener(MinecraftServer minecraftServer, Connection networkManager, ServerPlayer entityPlayer) {
        super(minecraftServer, networkManager, entityPlayer);
    }

    @Override
    public void send(Packet<?> packet) {
    }

}