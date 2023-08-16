package io.github.hello09x.fakeplayer.util.nms;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.network.EmptyAdvancements;
import io.github.hello09x.fakeplayer.util.Reflections;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class NMS_1_20_R1 implements NMS {

    private final static Logger log = Main.getInstance().getLogger();

    private final static Field advancements = Reflections.getFirstFieldByType(ServerPlayer.class, PlayerAdvancements.class, false);

    @Override
    public @NotNull ServerPlayer getServerPlayer(@NotNull Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    @Override
    public @NotNull ServerLevel getServerLevel(@NotNull World world) {
        return ((CraftWorld) world).getHandle();
    }

    @Override
    public @NotNull MinecraftServer getMinecraftServer(@NotNull Server server) {
        return ((CraftServer) server).getServer();
    }

    @Override
    public @NotNull PlayerList getPlayerList(@NotNull Server server) {
        return ((CraftServer) server).getHandle();
    }

    @Override
    public @NotNull BlockPos getBlockPos(@NotNull Location location) {
        return CraftLocation.toBlockPosition(location);
    }

    @Override
    public void setPlayBefore(@NotNull Player player) {
        ((CraftPlayer) player).readExtraData(new CompoundTag());
    }

    @Override
    public void unpersistAdvancements(@NotNull Player player) {
        if (advancements == null) {
            log.warning("Failed to unpersist player advancements profile: " + player.getUniqueId());
            return;
        }

        var handle = getServerPlayer(player);
        var server = getMinecraftServer(Bukkit.getServer());
        try {
            advancements.set(
                    getServerPlayer(player),
                    new EmptyAdvancements(
                            server.getFixerUpper(),
                            server.getPlayerList(),
                            server.getAdvancements(),
                            Main.getInstance().getDataFolder().getParentFile().toPath(),
                            handle
                    )
            );
        } catch (IllegalAccessException ignored) {
        }
    }
}
