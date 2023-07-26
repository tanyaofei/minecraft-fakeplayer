package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyAdvancements;
import io.github.hello09x.fakeplayer.core.EmptyConnection;
import io.github.hello09x.fakeplayer.core.EmptyLoginPacketListener;
import io.github.hello09x.fakeplayer.core.EmptyServerGamePacketListener;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.util.ReflectionUtils;
import io.papermc.paper.entity.LookAnchor;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlayer {

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
            @NotNull String name,
            @NotNull Location spawnAt
    ) {
        this.creator = creator;
        this.handle = new ServerPlayer(server, Objects.requireNonNull(server.getLevel(ServerLevel.OVERWORLD), "缺少 overworld 世界"), new GameProfile(uniqueId, name));
        this.bukkitPlayer = this.handle.getBukkitEntity();
        ((CraftPlayer) this.bukkitPlayer).readExtraData(new CompoundTag()); // set play before avoiding first join message
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

    public void spawn(
            boolean invulnerable,
            boolean collidable,
            boolean lookAtEntity,
            boolean pickupItems
    ) {
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
            ((CraftServer) Bukkit.getServer()).getHandle().placeNewPlayer(
                    listener.connection,
                    handle
            );
            connection.setListener(listener);
        }

        bukkitPlayer.setInvulnerable(invulnerable);
        bukkitPlayer.setCollidable(collidable);
        bukkitPlayer.setCanPickupItems(pickupItems);
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
    }

}
