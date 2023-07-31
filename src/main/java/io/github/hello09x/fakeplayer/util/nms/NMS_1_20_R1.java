package io.github.hello09x.fakeplayer.util.nms;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMS_1_20_R1 implements NMS {

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
    public void setPlayBefore(@NotNull Player player) {
        ((CraftPlayer) player).readExtraData(new CompoundTag());
    }
}
