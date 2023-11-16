package io.github.hello09x.fakeplayer.v1_20_R2.network;

import io.github.hello09x.fakeplayer.api.spi.NMSGamePacketListener;
import io.github.hello09x.fakeplayer.api.utils.ClientboundSystemChatPackets;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmptyServerGamePacketListener extends ServerGamePacketListenerImpl implements NMSGamePacketListener {

    private final static FakeplayerManager manager = FakeplayerManager.instance;

    @NotNull
    private final LinkedList<ReceivedMessage> messages = new LinkedList<>();

    private int messageId = 0;

    public EmptyServerGamePacketListener(
            @NotNull MinecraftServer server,
            @NotNull Connection connection,
            @NotNull ServerPlayer player,
            @NotNull CommonListenerCookie cookie
    ) {
        super(server, connection, player, cookie);
        Optional.ofNullable(Bukkit.getPlayer(player.getUUID()))
                .map(CraftPlayer.class::cast)
                .ifPresent(p -> p.addChannel(StandardMessenger.validateAndCorrectChannel(BUNGEE_CORD_CHANNEL)));
    }

    @Override
    public void send(Packet<?> packet) {
        if (packet instanceof ClientboundSystemChatPacket p) {
            this.handleSystemChatPacket(p);
        } else if (packet instanceof ClientboundCustomPayloadPacket p) {
            this.handleCustomPayloadPacket(p.payload());
        }
    }

    private void handleSystemChatPacket(@NotNull ClientboundSystemChatPacket packet) {
        if (this.messages.size() >= MESSAGE_HISTORY_SIZE) {
            this.messages.removeFirst();
        }

        var content = ClientboundSystemChatPackets.getAdventureContent(packet);
        if (content == null) {
            content = Optional.ofNullable(packet.content()).map(Component::text).orElse(null);
        }
        if (content == null) {
            return;
        }

        this.messages.addLast(new ReceivedMessage(++messageId, content));
    }

    private void handleCustomPayloadPacket(@NotNull CustomPacketPayload payload) {
        var channel = payload.id().getNamespace() + ":" + payload.id().getPath();
        if (!channel.equals(BUNGEE_CORD_CHANNEL)) {
            return;
        }

        var recipient = Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(p -> !manager.isFake(p))
                .findAny()
                .orElse(null);
        if (recipient == null) {
            return;
        }

        var buf = new FriendlyByteBuf(Unpooled.buffer(0, 1048576));
        payload.write(buf);
        var message = buf.array();

        recipient.sendPluginMessage(Main.getInstance(), channel, message);
    }

    @Override
    public @Nullable ReceivedMessage getLastMessage() {
        if (this.messages.isEmpty()) {
            return null;
        }
        return this.messages.getLast();
    }

    @Override
    public @NotNull List<ReceivedMessage> getRecentMessages(int skip, int size) {
        var stream = this.messages.stream();
        if (skip > 0) {
            stream = stream.skip(skip);
        }
        if (size != Integer.MAX_VALUE) {
            stream = stream.limit(size);
        }
        return stream.collect(Collectors.toList());
    }
}