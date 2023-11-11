package io.github.hello09x.fakeplayer.v1_20_R2.network;

import io.github.hello09x.fakeplayer.api.spi.NMSGamePacketListener;
import io.github.hello09x.fakeplayer.api.utils.ClientboundSystemChatPackets;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class EmptyServerGamePacketListener extends ServerGamePacketListenerImpl implements NMSGamePacketListener {

    private final LinkedList<ReceivedMessage> messages = new LinkedList<>();
    private final MutableInt messageId = new MutableInt();

    public EmptyServerGamePacketListener(MinecraftServer minecraftServer, Connection networkManager, ServerPlayer entityPlayer, CommonListenerCookie commonListenerCookie) {
        super(minecraftServer, networkManager, entityPlayer, commonListenerCookie);
    }

    @Override
    public void send(Packet<?> packet) {
        if (packet instanceof ClientboundSystemChatPacket chat) {
            this.handleSystemChatPacket(chat);
        }
    }

    private void handleSystemChatPacket(@NotNull ClientboundSystemChatPacket packet) {
        if (this.messages.size() >= MESSAGE_HISTORY_SIZE) {
            this.messages.removeFirst();
        }

        var content = ClientboundSystemChatPackets.getAdventureContent(packet);
        if (content == null) {
            content = text(packet.content());
        }

        this.messages.addLast(new ReceivedMessage(messageId.incrementAndGet(), content));
    }

    @Override
    public @Nullable ReceivedMessage getLastMessage() {
        if (this.messages.isEmpty()) {
            return null;
        }
        return this.messages.getLast();
    }

    @Override
    public @NotNull List<ReceivedMessage> getRecentMessages() {
        return new ArrayList<>(this.messages);
    }

}