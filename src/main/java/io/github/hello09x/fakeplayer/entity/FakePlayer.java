package io.github.hello09x.fakeplayer.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import io.github.hello09x.bedrock.task.Tasks;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.manager.action.Action;
import io.github.hello09x.fakeplayer.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.manager.action.ActionSetting;
import io.github.hello09x.fakeplayer.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.network.EmptyConnection;
import io.github.hello09x.fakeplayer.network.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.network.EmptyServerGamePacketListener;
import io.github.hello09x.fakeplayer.util.Teleportor;
import io.github.hello09x.fakeplayer.util.Worlds;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import lombok.Getter;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakePlayer {

    private final static Logger log = Main.getInstance().getLogger();
    private final static InetAddress fakeAddress = InetAddress.getLoopbackAddress();
    private final static NMS nms = Main.getNms();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    @NotNull
    private final MinecraftServer server;

    @NotNull
    @Getter
    private final String creator;

    @NotNull
    @Getter
    private final ServerPlayer handle;

    @NotNull
    @Getter
    private final Player bukkitPlayer;

    @NotNull
    @Getter
    private final String creatorIp;

    @NotNull
    @Getter
    private final SequenceName sequenceName;

    @NotNull
    private final FakeplayerTicker ticker;

    @NotNull
    @Getter
    private final String name;

    @NotNull
    private final UUID uuid;

    public FakePlayer(
            @NotNull String creator,
            @NotNull String creatorIp,
            @NotNull SequenceName sequenceName,
            @Nullable LocalDateTime removeAt
    ) {
        this.name = sequenceName.name();
        this.uuid = sequenceName.uuid();

        this.creator = creator;
        this.creatorIp = creatorIp;
        this.sequenceName = sequenceName;
        this.server = nms.getMinecraftServer(Bukkit.getServer());
        this.handle = new ServerPlayer(
                this.server,
                nms.getOverworld(),
                new GameProfile(uuid, name)
        );
        this.bukkitPlayer = this.handle.getBukkitEntity();
        this.ticker = new FakeplayerTicker(this, removeAt);

        bukkitPlayer.setPersistent(false);
        bukkitPlayer.setSleepingIgnored(true);
        nms.setPlayBefore(bukkitPlayer);    // 可避免一些插件的第一次入服欢迎信息
        nms.unpersistAdvancements(bukkitPlayer);
    }

    /**
     * 让假人诞生
     */
    public void spawn(@NotNull SpawnOption option) {
        if (config.isDropInventoryOnQuiting()) {
            // 跨服背包同步插件可能导致假人既丢弃了一份到地上，在重新生成的时候又回来了
            // 因此在生成的时候清空一次背包
            // 但无法解决登陆后延迟同步背包的情况
            bukkitPlayer.getInventory().clear();
        }

        bukkitPlayer.setInvulnerable(option.invulnerable());
        bukkitPlayer.setCollidable(option.collidable());
        bukkitPlayer.setCanPickupItems(option.pickupItems());
        if (option.lookAtEntity()) {
            ActionManager.instance.setAction(bukkitPlayer, Action.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
        }
        if (option.skin()) {
            Optional.ofNullable(Bukkit.getPlayerExact(this.creator))
                    .map(nms::getServerPlayer)
                    .map(p -> p.getGameProfile().getProperties().get("textures"))
                    .map(textures -> Iterables.getFirst(textures, null))
                    .ifPresent(texture -> handle.getGameProfile().getProperties().put("textures", texture));
        }

        if (config.isSimulateLogin()) {
            Tasks.runAsync(Main.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new AsyncPlayerPreLoginEvent(
                        handle.getGameProfile().getName(),
                        fakeAddress,
                        fakeAddress,
                        handle.getUUID(),
                        bukkitPlayer.getPlayerProfile(),
                        fakeAddress.getHostName()
                ));
            });

            Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(
                    bukkitPlayer,
                    fakeAddress.getHostName(),
                    fakeAddress
            ));
        }

        {
            var connection = new EmptyConnection(PacketFlow.CLIENTBOUND);
            var listener = new EmptyServerGamePacketListener(this.server, connection, this.handle);
            this.handle.connection = listener;
            connection.setListener(listener);
        }

        {
            var connection = new EmptyConnection(PacketFlow.CLIENTBOUND);
            var listener = new EmptyLoginPacketListener(server, connection);
            nms.getPlayerList(Bukkit.getServer()).placeNewPlayer(
                    listener.connection,
                    handle
            );
            connection.setListener(listener);
        }

        var spawnAt = option.spawnAt().clone();
        if (Worlds.isOverworld(spawnAt.getWorld())) {
            // 创建在主世界时需要跨越一次世界才能拥有刷怪能力
            teleportToSpawnpointAfterChangingDimension(spawnAt);
        } else {
            teleportToSpawnpoint(spawnAt);
        }

        ticker.runTaskTimer(Main.getInstance(), 0, 1);
    }

    /**
     * 让假人完成一次维度旅行, 只有跨越过维度的假人才有刷怪能力(bug)
     *
     * @param spawnpoint 最终目的地, 即出生点
     */
    private void teleportToSpawnpointAfterChangingDimension(@NotNull Location spawnpoint) {
        var world = Worlds.getNonOverworld();
        if (world == null || !bukkitPlayer.teleport(world.getSpawnLocation())) {
            Optional.ofNullable(Bukkit.getPlayerExact(creator))
                    .ifPresentOrElse(
                            c -> c.sendMessage(textOfChildren(
                                    text(bukkitPlayer.getName(), WHITE),
                                    text(" 需要你手动帮他跨越过一次世界才具有刷怪能力", GRAY)
                            )),
                            () -> log.warning(String.format("假人 %s 需要手动跨越一次世界才具有刷怪能力", bukkitPlayer.getName()))
                    );
            return;
        }

        Tasks.runNextTick(Main.getInstance(), () -> teleportToSpawnpoint(spawnpoint));
    }

    private void teleportToSpawnpoint(@NotNull Location spawnpoint) {
        if (!Teleportor.teleportAndSound(bukkitPlayer, spawnpoint)) {
            Optional.ofNullable(Bukkit.getPlayerExact(creator))
                    .ifPresentOrElse(
                            p -> p.sendMessage(textOfChildren(
                                    text(bukkitPlayer.getName(), WHITE),
                                    text(" 传送到你身边失败: 可能由于第三方插件影响", GRAY, ITALIC)
                            )),
                            () -> log.warning(String.format("假人 %s 可能由于第三方插件而传送到创建者身边失败", bukkitPlayer.getName())));
        }
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
    }

    public boolean isOnline() {
        return this.bukkitPlayer.isOnline();
    }

    public @Nullable Player getCreatorPlayer() {
        return Bukkit.getPlayerExact(this.creator);
    }

    public int getTickCount() {
        return this.handle.tickCount;
    }

}
