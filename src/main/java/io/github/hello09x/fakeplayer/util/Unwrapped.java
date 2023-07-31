package io.github.hello09x.fakeplayer.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Unwrapped {

    public static @NotNull ServerPlayer getServerPlayer(@NotNull Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public static @NotNull ServerLevel getServerLevel(@NotNull World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static @NotNull MinecraftServer getMinecraftServer(@NotNull Server server) {
        return ((CraftServer) server).getServer();
    }

    public static @NotNull BlockState getBlockState(@NotNull Block block) {
        return ((CraftBlock) block).getNMS();
    }

    public static @NotNull BlockPos getBlockPos(@NotNull Block block) {
        return ((CraftBlock) block).getPosition();
    }

    public static @NotNull Entity getEntity(@NotNull org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    public static @NotNull PlayerList getPlayerList(@NotNull Server server) {
        return ((CraftServer) server).getHandle();
    }


}
