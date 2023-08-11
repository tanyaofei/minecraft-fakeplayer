package io.github.hello09x.fakeplayer.entity;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class FakeplayerTicker extends BukkitRunnable {

    private final FakePlayer player;
    private final long removeAt;

    public FakeplayerTicker(
            @NotNull FakePlayer player,
            @Nullable LocalDateTime removeAt
    ) {
        this.player = player;
        this.removeAt = removeAt == null ? 0 : removeAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public void run() {
        if (!getBukkitPlayer().isOnline()) {
            cancel();
            return;
        }

        var player = getServerPlayer();
        if (removeAt != 0 && player.tickCount % 20 == 0 && System.currentTimeMillis() > removeAt) {
            getBukkitPlayer().kick(text("[fakeplayer] 存活时间到期"));
            Optional.ofNullable(this.player.getCreatorPlayer())
                    .ifPresent(p -> p.sendMessage(textOfChildren(
                            text("假人 ", GRAY),
                            text(this.player.getName()),
                            text(" 存活时间到期, 已移除", GRAY)
                    )));
            cancel();
            return;
        }

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
