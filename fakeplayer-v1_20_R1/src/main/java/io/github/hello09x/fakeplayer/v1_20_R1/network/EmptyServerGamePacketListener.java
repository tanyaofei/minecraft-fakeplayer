package io.github.hello09x.fakeplayer.v1_20_R1.network;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class EmptyServerGamePacketListener extends ServerGamePacketListenerImpl {


    public EmptyServerGamePacketListener(@NotNull MinecraftServer minecraftServer, @NotNull Connection networkManager, @NotNull ServerPlayer player) {
        super(minecraftServer, networkManager, player);
    }

    @Override
    public void send(Packet<?> packet) {
    }

}