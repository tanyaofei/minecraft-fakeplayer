package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyAdvancements;
import io.github.hello09x.fakeplayer.core.EmptyConnection;
import io.github.hello09x.fakeplayer.core.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.core.EmptyServerGamePacketListener;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.util.ReflectionUtils;
import io.github.hello09x.fakeplayer.util.Tasker;
import io.github.hello09x.fakeplayer.util.Teleportor;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import io.papermc.paper.entity.LookAnchor;
import lombok.Getter;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlayer {

    private final static String WORLD_OVERWORLD = "world";
    private final static String WORLD_NETHER = "world_nether";
    private final static String WORLD_THE_END = "world_the_end";


    private final static Field advancements = ReflectionUtils.getFirstFieldByType(ServerPlayer.class, PlayerAdvancements.class, false);
    private final static Logger log = Main.getInstance().getLogger();
    private final static InetAddress fakeAddress = InetAddress.getLoopbackAddress();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final MinecraftServer server;

    @Getter
    private final String creator;

    @Getter
    private final ServerPlayer handle;

    @Getter
    private final Player bukkitPlayer;

    public FakePlayer(
            @NotNull String creator,
            @NotNull MinecraftServer server,
            @NotNull UUID uniqueId,
            @NotNull String name
    ) {
        this.creator = creator;
        this.handle = new ServerPlayer(server, Objects.requireNonNull(server.getLevel(ServerLevel.OVERWORLD), "缺少 overworld 世界"), new GameProfile(uniqueId, name));
        this.bukkitPlayer = this.handle.getBukkitEntity();
        NMS.getInstance().setPlayBefore(this.bukkitPlayer);
        this.server = server;

        this.bukkitPlayer.setPersistent(false);
        if (advancements != null) {
            try {
                advancements.set(
                        this.handle,
                        new EmptyAdvancements(
                                server.getFixerUpper(),
                                server.getPlayerList(),
                                server.getAdvancements(),
                                Main.getInstance().getDataFolder().getParentFile().toPath(),
                                this.handle
                        ));
            } catch (IllegalAccessException ignored) {
            }
        }
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
                        this.bukkitPlayer,
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
            NMS.getInstance().getPlayerList(Bukkit.getServer()).placeNewPlayer(
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
            public void run() {
                if (!bukkitPlayer.isOnline()) {
                    cancel();
                    return;
                }

                // tick() 伤害计算
                // doTick() 移动计算
                handle.doTick();
                if (lookAtEntity) {
                    var entities = bukkitPlayer.getNearbyEntities(3, 3, 3);
                    if (!entities.isEmpty()) {
                        bukkitPlayer.lookAt(entities.get(0), LookAnchor.EYES, LookAnchor.EYES);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);

        if (isOverworld(spawnAt.getWorld())) {
            // 如果假人出生点是主世界
            // 那么需要让他跨一次世界再传送过去
            Tasker.nextTick(() -> {
                var nether = Optional
                        .ofNullable(Bukkit.getWorld(WORLD_NETHER))
                        .orElseGet(() -> Bukkit.getWorld(WORLD_THE_END));
                if (nether == null) {
                    log.warning(String.format("由于缺少地狱世界: %s, 当前创建的假人 %s 可能无法刷新怪物, 需要玩家手动将他传送到其他世界后再传送回来", WORLD_NETHER, bukkitPlayer.getName()));
                    return;
                }
                Tasker.nextTick(() -> Teleportor.teleportAndSound(bukkitPlayer, spawnAt));
            });
        } else {
            // 不是主世界直接传送即可
            Teleportor.teleportAndSound(bukkitPlayer, spawnAt);
        }

        // 防止别的插件把假人带走, 比如 multiverse
        Tasker.later(() -> {
            if (
                    spawnAt.getWorld().equals(bukkitPlayer.getLocation().getWorld())
                            && spawnAt.distance(bukkitPlayer.getLocation()) < 16
            ) {
                return;
            }

            bukkitPlayer.teleport(spawnAt.getWorld().getSpawnLocation());
            Tasker.nextTick(() -> bukkitPlayer.teleport(spawnAt));
        }, 3);
    }


}
