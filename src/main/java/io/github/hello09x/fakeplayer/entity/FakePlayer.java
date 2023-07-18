package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyAdvancements;
import io.github.hello09x.fakeplayer.core.EmptyConnection;
import io.github.hello09x.fakeplayer.core.EmptyNetworkManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
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
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
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
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlayer {

    private final static Field advancements = ReflectionUtils.getFirstFieldByType(ServerPlayer.class, PlayerAdvancements.class, false);
    private final static Field distanceManager = ReflectionUtils.getFirstFieldByAssignFromType(ChunkMap.class, DistanceManager.class, false);
    private final static Logger log = Main.getInstance().getLogger();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    @Getter
    private final Location spawnLocation;

    @Getter
    private final String creator;

    @Getter
    private final ServerPlayer handle;

    private Player bukkitPlayer;

    private volatile DistanceManager dm;

    public FakePlayer(
            @NotNull String creator,
            @NotNull MinecraftServer server,
            @NotNull ServerLevel world,
            @NotNull UUID uniqueId,
            @NotNull String name,
            @NotNull Location at
    ) {
        this.handle = new ServerPlayer(server, world, new GameProfile(uniqueId, name));
        this.creator = creator;
        this.spawnLocation = at;

        try {
            var networkManager = new EmptyNetworkManager(PacketFlow.CLIENTBOUND);
            this.handle.connection = new EmptyConnection(server, networkManager, this.handle);
            networkManager.setListener(this.handle.connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public @NotNull Player spawn(long tickPeriod) {
        this.boardcast();
        this.addEntityToWorld();

        bukkitPlayer = Objects.requireNonNull(Bukkit.getPlayer(this.handle.getUUID()));
        bukkitPlayer.setSleepingIgnored(true);
        bukkitPlayer.setPersistent(false);
        bukkitPlayer.setInvulnerable(true);

        new BukkitRunnable() {
            @Override
            @SneakyThrows
            public void run() {
                if (!bukkitPlayer.isOnline()) {
                    cancel();
                }
                doTick();
            }
        }.runTaskTimer(Main.getInstance(), 0, tickPeriod);

        if (properties.isSimulateLogin()) {
            simulateLogin();
        }
        return bukkitPlayer;
    }

    /**
     * 通知其他玩家加入假人
     */
    @SuppressWarnings("all")
    public void boardcast() {
        handle.updateOptions(new ServerboundClientInformationPacket(
                "en_us",
                Bukkit.getServer().getViewDistance(),
                ChatVisiblity.FULL,
                false,
                0,
                HumanoidArm.LEFT,
                false,
                true
        ));

        var entity = this.handle.getBukkitEntity();
        if (handle.level() == null) {
            return;
        }

        var playerList = handle.level().players();
        if (!playerList.contains(handle)) {
            ((List) playerList).add(handle);
        }

        try {
            var updatePlayerStatus = ChunkMap.class.getDeclaredMethod(
                    "a",
                    ServerPlayer.class,
                    boolean.class
            );
            updatePlayerStatus.setAccessible(true);
            updatePlayerStatus.invoke(
                    ((ServerLevel) handle.level()).getChunkSource().chunkMap,
                    handle,
                    true
            );
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for(var online: Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, handle));
            ((CraftPlayer) online).getHandle().connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, handle));
        }

    }

    /**
     * 将实体加入到世界
     */
    @SuppressWarnings("resource")
    public void addEntityToWorld() {
        var entity = this.handle.getBukkitEntity();
        if (!isChunkLoaded(spawnLocation)) {
            spawnLocation.getChunk().load();
        }


        this.handle.level().addFreshEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ((CraftServer) Bukkit.getServer()).getHandle().respawn(
                this.handle,
                true,
                PlayerRespawnEvent.RespawnReason.PLUGIN
        );

        // move directly
        this.handle.getBukkitEntity().teleport(spawnLocation);
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    public void doTick() {
        this.handle.doTick();
        this.handle.baseTick();
        this.tickChunks();
        this.tickLookAt();
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
     * 刷新区块
     */
    @SneakyThrows
    public void tickChunks() {
        if (this.dm == null) {
            var chunkMap = ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getChunkSource().chunkMap;
            if (distanceManager != null) {
                this.dm = (DistanceManager) distanceManager.get(chunkMap);
            }
        }

        var pos = ((CraftPlayer) bukkitPlayer).getHandle().chunkPosition();
        dm.addRegionTicketAtDistance(TicketType.PLAYER, pos, Bukkit.getServer().getSimulationDistance(), pos);
    }

    public @NotNull Player getBukkitPlayer() {
        if (this.bukkitPlayer == null) {
            throw new IllegalStateException("fake player never spawn");
        }
        return this.bukkitPlayer;
    }

    public List<Chunk> getNearbyChunks() {
        var distance = Math.min(Bukkit.getSimulationDistance(), this.bukkitPlayer.getSimulationDistance());

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
                                InetAddress.getLoopbackAddress(),
                                InetAddress.getLoopbackAddress(),
                                handle.getUUID(),
                                bukkitPlayer.getPlayerProfile()
                        ));
            }
        }.runTaskAsynchronously(Main.getInstance());

        Bukkit.getPluginManager().callEvent(
                new PlayerLoginEvent(
                        bukkitPlayer,
                        InetAddress.getLoopbackAddress().getHostName(),
                        InetAddress.getLoopbackAddress(),
                        InetAddress.getLoopbackAddress())
        );

        Bukkit.getPluginManager().callEvent(
                new PlayerJoinEvent(bukkitPlayer,
                        (Component) null)
        );
    }


}
