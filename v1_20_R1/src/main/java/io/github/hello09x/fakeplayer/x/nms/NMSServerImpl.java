package io.github.hello09x.fakeplayer.x.nms;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.api.nms.NMSServer;
import io.github.hello09x.fakeplayer.api.nms.NMSServerPlayer;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NMSServerImpl implements NMSServer {


    @Getter
    private final MinecraftServer handle;

    public NMSServerImpl(@NotNull Server server) {
        this.handle = ((CraftServer) server).getServer();
    }


    @Override
    public @NotNull NMSServerPlayer newPlayer(@NotNull UUID uuid, @NotNull String name) {
        var handle = new ServerPlayer(
                new NMSServerImpl(Bukkit.getServer()).getHandle(),
                NMSServerLevelImpl.OVERWORLD.getHandle(),
                new GameProfile(uuid, name)
        );
        return new NMSServerPlayerImpl(handle.getBukkitEntity());
    }
}
