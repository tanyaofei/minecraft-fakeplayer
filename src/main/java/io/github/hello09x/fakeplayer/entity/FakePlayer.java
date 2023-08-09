package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyConnection;
import io.github.hello09x.fakeplayer.core.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.core.EmptyServerGamePacketListener;
import io.github.hello09x.fakeplayer.manager.NameManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.util.Tasker;
import io.github.hello09x.fakeplayer.util.Teleportor;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import io.papermc.paper.entity.LookAnchor;
import lombok.Getter;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakePlayer {

    private final static String WORLD_OVERWORLD = "world";

    private final static Logger log = Main.getInstance().getLogger();
    private final static InetAddress fakeAddress = InetAddress.getLoopbackAddress();
    private final static NMS nms = Main.getNms();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final MinecraftServer server;

    @Getter
    private final String creator;

    @Getter
    private final ServerPlayer handle;

    @Getter
    private final Player bukkitPlayer;

    @Getter
    private final String creatorIp;

    @Getter
    private final NameManager.SequenceName sequenceName;

    public FakePlayer(
            @NotNull String creator,
            @NotNull String creatorIp,
            @NotNull NameManager.SequenceName sequenceName
    ) {
        var uniqueId = sequenceName.uniqueId();
        var name = sequenceName.name();

        this.creator = creator;
        this.creatorIp = creatorIp;
        this.sequenceName = sequenceName;
        this.server = nms.getMinecraftServer(Bukkit.getServer());
        this.handle = new ServerPlayer(this.server, Objects.requireNonNull(this.server.getLevel(ServerLevel.OVERWORLD), "缺少 overworld 世界"), new GameProfile(uniqueId, name));
        this.bukkitPlayer = this.handle.getBukkitEntity();

        bukkitPlayer.setPersistent(false);
        bukkitPlayer.setSleepingIgnored(true);
        nms.setPlayBefore(bukkitPlayer);
        nms.unpersistAdvancements(bukkitPlayer);
    }

    /**
     * 判断世界是否是主世界
     *
     * @param world 世界
     * @return 该世界是否是主世界
     */
    private static boolean isOverworld(@NotNull World world) {
        return world.getName().equals(WORLD_OVERWORLD);
    }

    private static @Nullable World getNonOverworld() {
        return Bukkit.getWorlds().stream().filter(w -> !w.getName().equals(WORLD_OVERWORLD)).findAny().orElse(null);
    }

    /**
     * 让假人诞生
     */
    public void spawn(@NotNull SpawnOption option) {
        if (properties.isSimulateLogin()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    var preLoginEvent = new AsyncPlayerPreLoginEvent(
                            handle.getGameProfile().getName(),
                            fakeAddress,
                            fakeAddress,
                            handle.getUUID(),
                            bukkitPlayer.getPlayerProfile(),
                            fakeAddress.getHostName()
                    );
                    Bukkit.getPluginManager().callEvent(preLoginEvent);
                }
            }.runTaskAsynchronously(Main.getInstance());

            {
                Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(
                        bukkitPlayer,
                        fakeAddress.getHostName(),
                        fakeAddress
                ));
            }
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

        bukkitPlayer.setInvulnerable(option.invulnerable());
        bukkitPlayer.setCollidable(option.collidable());
        bukkitPlayer.setCanPickupItems(option.pickupItems());
        var spawnAt = option.spawnAt().clone();
        var lookAtEntity = option.lookAtEntity();

        new BukkitRunnable() {
            @Override
            @SuppressWarnings("UnstableApiUsage")
            public void run() {
                if (!bukkitPlayer.isOnline()) {
                    // not online
                    cancel();
                    return;
                }

                // tick() 伤害计算
                // doTick() 移动计算
                handle.doTick();
                if (lookAtEntity) {
                    var entities = bukkitPlayer.getNearbyEntities(4.5, 4.5, 4.5);
                    if (!entities.isEmpty()) {
                        bukkitPlayer.lookAt(entities.get(0), LookAnchor.EYES, LookAnchor.EYES);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);

        if (isOverworld(spawnAt.getWorld())) {
            // 创建在主世界时需要跨越一次世界才能拥有刷怪能力
            teleportToSpawnpointAfterChangingDimension(spawnAt);
        } else {
            teleportToSpawnpoint(spawnAt);
        }

        // 防止别的插件把假人带走, 比如 multiverse, clearfog
        fixPosition(spawnAt);
    }

    /**
     * 延迟检测假人的位置是否是目标位置, 如果发生了变化则传送过去
     *
     * @param spawnpoint 目标位置
     */
    private void fixPosition(@NotNull Location spawnpoint) {
        Tasker.later(() -> {
            if (spawnpoint.getWorld().equals(bukkitPlayer.getLocation().getWorld()) && spawnpoint.distance(bukkitPlayer.getLocation()) < 16) {
                return;
            }

            bukkitPlayer.teleport(spawnpoint.getWorld().getSpawnLocation());
            Tasker.nextTick(() -> bukkitPlayer.teleport(spawnpoint));
        }, 4);
    }

    /**
     * 让假人完成一次维度旅行, 只有跨越过维度的假人才有刷怪能力(bug)
     *
     * @param spawnpoint 最终目的地, 即出生点
     */
    private void teleportToSpawnpointAfterChangingDimension(@NotNull Location spawnpoint) {
        Tasker.nextTick(() -> {
            var world = getNonOverworld();
            if (world == null || !bukkitPlayer.teleport(world.getSpawnLocation())) {
                Optional.ofNullable(Bukkit.getPlayerExact(creator)).ifPresentOrElse(
                        c -> c.sendMessage(textOfChildren(
                                text(bukkitPlayer.getName(), WHITE),
                                text(" 需要你手动帮他跨越过一次世界才具有刷怪能力", GRAY)
                        )),
                        () -> log.warning(String.format("假人 %s 需要手动跨越一次世界才具有刷怪能力", bukkitPlayer.getName())));
                return;
            }

            Tasker.nextTick(() -> teleportToSpawnpoint(spawnpoint));
        });
    }

    private void teleportToSpawnpoint(@NotNull Location spawnpoint) {
        if (!Teleportor.teleportAndSound(bukkitPlayer, spawnpoint)) {
            Optional.ofNullable(Bukkit.getPlayerExact(creator)).ifPresentOrElse(
                    p -> p.sendMessage(textOfChildren(
                            text(bukkitPlayer.getName(), WHITE),
                            text(" 传送到你身边失败: 可能由于第三方插件影响", GRAY, ITALIC)
                    )),
                    () -> log.warning(String.format("假人 %s 可能由于第三方插件而传送到创建者身边失败", bukkitPlayer.getName())));
        }
    }

    public @NotNull String getName() {
        return this.bukkitPlayer.getName();
    }

    public @NotNull UUID getUniqueId() {
        return this.bukkitPlayer.getUniqueId();
    }

    public boolean isOnline() {
        return this.bukkitPlayer.isOnline();
    }

}
