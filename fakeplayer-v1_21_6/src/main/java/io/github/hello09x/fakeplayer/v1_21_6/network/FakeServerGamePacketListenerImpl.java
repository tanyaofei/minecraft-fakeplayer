package io.github.hello09x.fakeplayer.v1_21_6.network;

import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import lombok.Lombok;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.logging.Logger;

public class FakeServerGamePacketListenerImpl extends ServerGamePacketListenerImpl implements NMSServerGamePacketListener {

    private final FakeplayerManager manager = Main.getInjector().getInstance(FakeplayerManager.class);
    private final static Logger log = Main.getInstance().getLogger();

    public FakeServerGamePacketListenerImpl(
            @NotNull MinecraftServer server,
            @NotNull Connection connection,
            @NotNull ServerPlayer player,
            @NotNull CommonListenerCookie cookie
    ) {
        super(server, connection, player, cookie);
        Optional.ofNullable(Bukkit.getPlayer(player.getUUID()))
                .ifPresent(p -> this.addChannel(p, BUNGEE_CORD_CORRECTED_CHANNEL));
    }

    private boolean addChannel(@NotNull Player player, @NotNull String channel) {
        try {
            var method = player.getClass().getMethod("addChannel", String.class);
            var ret = method.invoke(player, channel);
            if (ret instanceof Boolean success) {
                return success;
            }
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    @Override
    public void send(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            this.handleCustomPayloadPacket(p);
        } else if (packet instanceof ClientboundSetEntityMotionPacket p) {
            this.handleClientboundSetEntityMotionPacket(p);
        }
    }

    /**
     * 玩家被击退的动作由客户端完成, 假人没有客户端因此手动完成这个动作
     */
    public void handleClientboundSetEntityMotionPacket(@NotNull ClientboundSetEntityMotionPacket packet) {
        if (packet.getId() == this.player.getId() && this.player.hurtMarked) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                this.player.hurtMarked = true;
                this.player.lerpMotion(packet.getXa(), packet.getYa(), packet.getZa());
            });
        }
    }

    private void handleCustomPayloadPacket(@NotNull ClientboundCustomPayloadPacket packet) {
        var payload = packet.payload();
        var resourceLocation = payload.type().id();
        var channel = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();

        if (!channel.equals(BUNGEE_CORD_CORRECTED_CHANNEL)) {
            return;
        }

        if (!(payload instanceof DiscardedPayload discardedPayload)) {
            return;
        }

        var recipient = Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(manager::isNotFake)
                .findAny()
                .orElse(null);

        if (recipient == null) {
            log.warning("Failed to forward a plugin message cause non real players in the server");
            return;
        }

        var message = getDiscardedPayloadData(discardedPayload);
        recipient.sendPluginMessage(Main.getInstance(), BUNGEE_CORD_CHANNEL, message);
    }

    private byte[] getDiscardedPayloadData(@NotNull DiscardedPayload payload) {
        try {
            return payload.data().array();
        } catch (NoSuchMethodError e) {
            try {
                return (byte[]) payload.getClass().getMethod("data").invoke(payload);   // 1.21.5 actual is  `public final byte[] data() {}`
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw Lombok.sneakyThrow(e);
            }
        }
    }

}