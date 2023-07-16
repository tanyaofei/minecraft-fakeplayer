package io.github.hello09x.fakeplayer.manager;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.core.EmptyNetworkManager;
import net.kyori.adventure.sound.Sound;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;

public class FakePlayerSpawner {

    public final static String META_KEY_CREATOR = "fakeplayer::creator";

    public static @NotNull Player spawn(
            @NotNull UUID uniqueId,
            @NotNull String name,
            @NotNull Player creator,
            @NotNull Location at
    ) {
        var craftServer = (CraftServer) Bukkit.getServer();
        var server = craftServer.getServer();
        var gameProfile = new GameProfile(uniqueId, name);
        var world = ((CraftWorld) at.getWorld()).getHandle();

        var entityPlayer = new EntityPlayer(server, world, gameProfile);
        var craftPlayer = entityPlayer.getBukkitEntity();


        entityPlayer.f = 0; // ping
        entityPlayer.c = new PlayerConnection(
                server,
                new EmptyNetworkManager(EnumProtocolDirection.a),
                entityPlayer
        );

        craftPlayer.setFirstPlayed(System.currentTimeMillis());
        craftServer.getHandle().respawn(
                entityPlayer,
                true,
                PlayerRespawnEvent.RespawnReason.PLUGIN
        );
        refreshTabList(entityPlayer);

        var holder = Objects.requireNonNull(Bukkit.getPlayer(uniqueId));
        holder.setViewDistance(9);
        holder.setAffectsSpawning(true);
        holder.setSleepingIgnored(true);
        holder.setPersistent(false);
        holder.setGameMode(GameMode.CREATIVE);
        holder.setAllowFlight(true);
        holder.setMetadata(META_KEY_CREATOR, new FixedMetadataValue(Main.getInstance(), creator.getUniqueId().toString()));
        holder.teleport(at, PlayerTeleportEvent.TeleportCause.SPECTATE);
        creator.playSound(Sound.sound(
                ENTITY_ENDERMAN_TELEPORT.key(),
                Sound.Source.PLAYER,
                1.0F,
                1.0F
        ));

        CompletableFuture.runAsync(() -> {
            System.out.println(holder);
            System.out.println(craftPlayer);
        });
        return holder;
    }

    private static void refreshTabList(EntityPlayer player) {
        try {
            var field = EntityPlayer.class.getDeclaredField("cU");
            field.setAccessible(true);
            field.set(player, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (var online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().c.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, player)); //ADD_PLAYER
            ((CraftPlayer) online).getHandle().c.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.d, player)); //UPDATE_LISTED
        }
    }


}
