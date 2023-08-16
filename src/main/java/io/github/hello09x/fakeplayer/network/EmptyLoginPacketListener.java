package io.github.hello09x.fakeplayer.network;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class EmptyLoginPacketListener extends ServerLoginPacketListenerImpl {
    public EmptyLoginPacketListener(MinecraftServer server, Connection connection) {
        super(server, connection);
    }

}
