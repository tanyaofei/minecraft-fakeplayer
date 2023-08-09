package io.github.hello09x.fakeplayer.util.nms;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMS {

    static NMS getInstance() {
        return InstanceHolder.instance;
    }

    @NotNull ServerPlayer getServerPlayer(@NotNull Player player);

    @NotNull ServerLevel getServerLevel(@NotNull World world);

    @NotNull MinecraftServer getMinecraftServer(@NotNull Server server);

    @NotNull PlayerList getPlayerList(@NotNull Server server);

    @NotNull BlockPos getBlockPos(@NotNull Location location);

    void setPlayBefore(@NotNull Player player);

    void unpersistAdvancements(@NotNull Player player);


    class InstanceHolder {

        private final static NMS instance;

        static {
            instance = switch (Bukkit.getMinecraftVersion()) {
                case "1.20.1" -> new NMS_1_20_R1();
                default -> throw new UnsupportedOperationException(String.format(
                        "Unsupported minecraft version: %s",
                        Bukkit.getMinecraftVersion()
                ));
            };
        }
    }

}
