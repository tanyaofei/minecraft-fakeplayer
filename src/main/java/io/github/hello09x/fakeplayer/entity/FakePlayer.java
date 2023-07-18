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
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlayer extends ServerPlayer {

    private final static Field advancements = ReflectionUtils.getFirstFieldByType(ServerPlayer.class, PlayerAdvancements.class, false);
    private final static Field distanceManager = ReflectionUtils.getFirstFieldByAssignFromType(ChunkMap.class, DistanceManager.class, false);
    private final static Logger log = Main.getInstance().getLogger();

    @Getter
    private @NotNull
    final Location spawnLocation;

    @Getter
    private final String creator;

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
        super(server, world, new GameProfile(uniqueId, name));
        this.creator = creator;
        this.spawnLocation = at;

        try {
            var networkManager = new EmptyNetworkManager(PacketFlow.CLIENTBOUND);
            this.connection = new EmptyConnection(server, networkManager, this);
            networkManager.setListener(this.connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (advancements != null) {
            try {
                advancements.set(
                        this,
                        new EmptyAdvancements(
                                server.getFixerUpper(),
                                server.getPlayerList(),
                                server.getAdvancements(),
                                Main.getInstance().getDataFolder().getParentFile().toPath(),
                                this
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

        bukkitPlayer = Objects.requireNonNull(Bukkit.getPlayer(this.uuid));
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
                tickCount++;
            }
        }.runTaskTimer(Main.getInstance(), 0, tickPeriod);
        return bukkitPlayer;
    }

    /**
     * 通知其他玩家加入假人
     */
    @SuppressWarnings("all")
    public void boardcast() {
        this.updateOptions(new ServerboundClientInformationPacket(
                "en_us",
                Bukkit.getServer().getViewDistance(),
                ChatVisiblity.FULL,
                false,
                0,
                HumanoidArm.LEFT,
                false,
                true
        ));

        var entity = this.getBukkitEntity();
        var handle = (ServerPlayer) ((CraftEntity) entity).getHandle();
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
        var entity = this.getBukkitEntity();
        var handle = (ServerPlayer) ((CraftEntity) entity).getHandle();

        if (!isChunkLoaded(spawnLocation)) {
            spawnLocation.getChunk().load();
        }


        handle.level().addFreshEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ((CraftServer) Bukkit.getServer()).getHandle().respawn(
                this,
                true,
                PlayerRespawnEvent.RespawnReason.PLUGIN
        );

        // move directly
        getBukkitEntity().teleport(spawnLocation);
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    @Override
    public void doTick() {
        super.doTick();
        super.baseTick();
        this.tickChunks();
        this.tickLookAt();
        this.tickNonsense();
    }

    /**
     * 看向最近的实体
     */
    public void tickLookAt() {
        if (this.tickCount % 20 != 0) {
            return;
        }

        var nearby = this.bukkitPlayer.getNearbyEntities(3, 3, 3);
        if (nearby.isEmpty()) {
            return;
        }
        bukkitPlayer.lookAt(nearby.get(0), LookAnchor.EYES, LookAnchor.EYES);
    }

    /**
     * 胡言乱语
     */
    public void tickNonsense() {
        if (this.tickCount == 0 || (this.tickCount & (8192 - 1)) != 0) {
            // 每 6 分钟
            return;
        }

        if ((this.tickCount & 1) != 0) {
            // 1/2 的几率
            return;
        }

        var nonsense = FakeplayerProperties.instance.getNonsense();
        if (nonsense.isEmpty()) {
            return;
        }

        bukkitPlayer.chat(nonsense.get(RandomUtils.nextInt(0, nonsense.size())));
    }

    /**
     * 刷新区块
     */
    @SneakyThrows
    public void tickChunks() {
        if (this.dm == null) {
            var chunkMap = ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getChunkSource().chunkMap;
            this.dm = (DistanceManager) distanceManager.get(chunkMap);
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


}
