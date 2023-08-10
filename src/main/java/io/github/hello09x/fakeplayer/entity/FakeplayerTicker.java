package io.github.hello09x.fakeplayer.entity;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FakeplayerTicker extends BukkitRunnable {

    private final FakePlayer player;

    public FakeplayerTicker(@NotNull FakePlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!getBukkitPlayer().isOnline()) {
            cancel();
            return;
        }

        var player = getServerPlayer();
        if (player.tickCount == 0) {
            var x = player.getX();
            var y = player.getY();
            var z = player.getZ();

            // 将本 tick 的移动取消
            player.xo = x;
            player.yo = y;
            player.zo = z;
            player.doTick();

            // clearFog 插件会在第一次传送的时候改变了玩家的位置, 因此必须进行一次传送
            getBukkitPlayer().teleport(new Location(getBukkitPlayer().getWorld(), x, y, z, player.getYRot(), player.getXRot()));
            player.absMoveTo(x, y, z, player.getYRot(), player.getXRot());
        } else {
            player.doTick();
        }
    }

    private @NotNull Player getBukkitPlayer() {
        return this.player.getBukkitPlayer();
    }

    private @NotNull ServerPlayer getServerPlayer() {
        return this.player.getHandle();
    }

}
