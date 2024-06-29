package io.github.hello09x.fakeplayer.v1_21_R1.network;

import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundCustomReportDetailsPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DummyServerGamePacketListenerImpl extends ServerGamePacketListenerImpl implements NMSServerGamePacketListener {

    private final static FakeplayerManager manager = FakeplayerManager.instance;

    public DummyServerGamePacketListenerImpl(
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
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            this.handleCustomPayloadPacket(p);
        }
    }

    private void handleCustomPayloadPacket(@NotNull ClientboundCustomPayloadPacket packet) {
        var payload = packet.payload();
        var resourceLocation = payload.type().id();
        var channel = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
        if (!channel.equals(BUNGEE_CORD_CHANNEL)) {
            return;
        }

        var recipient = Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(manager::notContains)
                .findAny()
                .orElse(null);
        if (recipient == null) {
            return;
        }

        var buf = RegistryFriendlyByteBuf.decorator(server.registryAccess()).apply(Unpooled.buffer(0, 1048576));
        ClientboundCustomPayloadPacket.GAMEPLAY_STREAM_CODEC.encode(buf, packet);

        var message = buf.array();
        recipient.sendPluginMessage(Main.getInstance(), channel, message);
    }

}