package io.github.hello09x.fakeplayer.entity;

import com.google.common.base.Throwables;
import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyAdvancements;
import io.github.hello09x.fakeplayer.core.EmptyConnection;
import io.github.hello09x.fakeplayer.core.EmptyNetworkManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.util.ChunkCoord;
import io.github.hello09x.fakeplayer.util.ReflectionUtils;
import io.papermc.paper.entity.LookAnchor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static java.lang.Math.min;

public class FakePlayer {

    private final static Field advancements = ReflectionUtils.getFirstFieldByType(ServerPlayer.class, PlayerAdvancements.class, false);
    private final static Field distanceManager = ReflectionUtils.getFirstFieldByAssignFromType(ChunkMap.class, DistanceManager.class, false);
    private final static Logger log = Main.getInstance().getLogger();
    private final static InetAddress fakeAddress = InetAddress.getLoopbackAddress();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    @Getter
    private final Location spawnLocation;

    @Getter
    private final String creator;

    @Getter
    private final ServerPlayer handle;

    private Player bukkitPlayer;

    private final int distance;

    private ChunkCoord lastCoord;

    public FakePlayer(
            @NotNull String creator,
            @NotNull MinecraftServer server,
            @NotNull ServerLevel world,
            @NotNull UUID uniqueId,
            @NotNull String name,
            @NotNull Location at
    ) {
        this.creator = creator;
        this.handle = new ServerPlayer(server, world, new GameProfile(uniqueId, name));
        this.spawnLocation = at.clone();

        try {
            var networkManager = new EmptyNetworkManager(PacketFlow.CLIENTBOUND);
            this.handle.connection = new EmptyConnection(server, networkManager, this.handle);
            networkManager.setListener(this.handle.connection);
        } catch (IOException e) {
            log.warning("无法为假人创建虚拟的网络连接\n" + Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }

        var distance = properties.getDistance();
        if (distance <= 0) {
            distance = Bukkit.getSimulationDistance();
        }
        this.distance = distance;

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

    private static boolean isChunkLoaded(@NotNull Location at) {
        if (at.getWorld() == null) {
            return false;
        }
        int x = at.getBlockX() >> 4;
        int z = at.getBlockZ() >> 4;
        return at.getWorld().isChunkLoaded(x, z);
    }

    @SneakyThrows
    private static DistanceManager getDistanceManager(@NotNull World world) {
        var chunkMap = ((CraftWorld) world).getHandle().getChunkSource().chunkMap;
        return (DistanceManager) Objects.requireNonNull(distanceManager).get(chunkMap);
    }

    public @NotNull Player spawn(
            long tickPeriod,
            boolean invulnerable,
            boolean lookAtEntity,
            boolean collidable
    ) {
        this.broadcast();
        this.addEntityToWorld();

        bukkitPlayer = Objects.requireNonNull(Bukkit.getPlayer(this.handle.getUUID()));
        bukkitPlayer.setSleepingIgnored(true);
        bukkitPlayer.setPersistent(false);
        bukkitPlayer.setInvulnerable(invulnerable);
        bukkitPlayer.setCollidable(collidable);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!bukkitPlayer.isOnline()) {
                    removeLastChunkTicket();
                    cancel();
                    return;
                }
                doTick(lookAtEntity);
            }
        }.runTaskTimer(Main.getInstance(), 0, tickPeriod);

        if (properties.isSimulateLogin()) {
            simulateLogin();
        }

        return bukkitPlayer;
    }

    /**
     * 将实体加入到世界
     */
    @SuppressWarnings("resource")
    public void addEntityToWorld() {
        handle.level().addFreshEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ((CraftServer) Bukkit.getServer()).getHandle().respawn(
                this.handle,
                ((CraftWorld) this.spawnLocation.getWorld()).getHandle(),
                true,   // keep player inventory
                this.spawnLocation,
                properties.isAvoidSuffocation(),
                PlayerRespawnEvent.RespawnReason.PLUGIN
        );
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    /**
     * 玩家每 tick 要做的事情
     */
    public void doTick(boolean tickLookAt) {
        this.handle.doTick();
        this.handle.baseTick();
        this.tickChunks();
        if (tickLookAt) {
            this.tickLookAt();
        }
        this.handle.tickCount++;
    }

    /**
     * 看向最近的实体
     */
    public void tickLookAt() {
        if (this.handle.tickCount % 20 != 0) {
            return;
        }

        var nearby = this.bukkitPlayer.getNearbyEntities(3, 3, 3);
        if (nearby.isEmpty()) {
            return;
        }
        bukkitPlayer.lookAt(nearby.get(0), LookAnchor.EYES, LookAnchor.EYES);
    }

    /**
     * 通知其他玩家加入假人
     */
    public void broadcast() {
        handle.updateOptions(new ServerboundClientInformationPacket(
                "en_us",
                Bukkit.getServer().getViewDistance(),
                ChatVisiblity.FULL,
                false,
                0,
                HumanoidArm.LEFT,
                false,
                true                // allow listing
        ));

        for (var online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, handle));
            ((CraftPlayer) online).getHandle().connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, handle));
        }

    }

    public void removeLastChunkTicket() {
        if (lastCoord == null) {
            return;
        }
        getDistanceManager(lastCoord.world()).removeRegionTicketAtDistance(
                TicketType.PLUGIN_TICKET,
                lastCoord.pos(),
                this.distance + 2,
                Main.getInstance()
        );
    }

    /**
     * 刷新区块
     */
    @SneakyThrows
    public void tickChunks() {
        removeLastChunkTicket();
        var pos = ((CraftPlayer) bukkitPlayer).getHandle().chunkPosition();
        getDistanceManager(bukkitPlayer.getWorld()).addRegionTicketAtDistance(
                TicketType.PLUGIN_TICKET,
                pos,
                this.distance + 2,
                Main.getInstance()
        );
        this.lastCoord = new ChunkCoord(
                bukkitPlayer.getWorld(),
                pos
        );
    }

    public @NotNull Player getBukkitPlayer() {
        if (this.bukkitPlayer == null) {
            throw new IllegalStateException("fake player never spawned");
        }
        return this.bukkitPlayer;
    }

    public List<Chunk> getNearbyChunks() {
        var distance = min(Bukkit.getSimulationDistance(), this.bukkitPlayer.getSimulationDistance());

        var center = bukkitPlayer.getChunk();
        var minX = center.getX() - distance;
        var maxX = center.getX() + distance;
        var minZ = center.getZ() - distance;
        var maxZ = center.getZ() + distance;

        var world = this.bukkitPlayer.getWorld();
        var ret = new ArrayList<Chunk>(((distance * 2) + 1) * (distance * 2) + 1);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                ret.add(world.getChunkAt(x, z));
            }
        }
        return ret;
    }

    public void simulateLogin() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(
                        new AsyncPlayerPreLoginEvent(
                                handle.getGameProfile().getName(),
                                fakeAddress,
                                fakeAddress,
                                handle.getUUID(),
                                bukkitPlayer.getPlayerProfile(),
                                fakeAddress.getHostName()
                        ));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(
                                new PlayerLoginEvent(
                                        bukkitPlayer,
                                        fakeAddress.getHostName(),
                                        fakeAddress,
                                        fakeAddress
                                )
                        );
                    }
                }.runTaskLater(Main.getInstance(), 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(
                                new PlayerJoinEvent(bukkitPlayer,
                                        (Component) null)
                        );
                    }
                }.runTaskLater(Main.getInstance(), 2);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }


}
