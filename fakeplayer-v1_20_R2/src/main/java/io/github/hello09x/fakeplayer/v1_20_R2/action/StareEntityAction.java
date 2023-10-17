package io.github.hello09x.fakeplayer.v1_20_R2.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.papermc.paper.entity.LookAnchor;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.jetbrains.annotations.NotNull;

public class StareEntityAction implements Action {

    private final ServerPlayer player;

    public StareEntityAction(@NotNull ServerPlayer player) {
        this.player = player;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean tick() {
        var bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        if (bukkitPlayer == null) {
            return true;
        }

        var target = bukkitPlayer
                .getNearbyEntities(4.5, 4.5, 4.5)
                .stream()
                .filter(e -> e instanceof Damageable)
                .findAny()
                .orElse(null);

        if (target == null) {
            return false;
        }

        bukkitPlayer.lookAt(target, LookAnchor.EYES, LookAnchor.EYES);
        player.resetLastActionTime();
        return true;
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public void stop() {

    }
}
