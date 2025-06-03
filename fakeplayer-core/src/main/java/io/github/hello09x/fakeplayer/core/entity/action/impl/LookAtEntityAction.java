package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class LookAtEntityAction implements Action {


    @NotNull
    private final NMSServerPlayer player;

    public LookAtEntityAction(@NotNull NMSServerPlayer player) {
        this.player = player;
    }


    @Override
    public boolean tick() {
        var bukkitPlayer = player.getPlayer();
        var entities = bukkitPlayer.getLocation().getNearbyEntitiesByType(Damageable.class, 4.5, 4.5, 4.5);
        if (entities.isEmpty()) {
            return false;
        }

        var entity = this.getNearestEntity(bukkitPlayer, entities);
        if (entity == null) {
            return false;
        }

        bukkitPlayer.lookAt(entity, LookAnchor.EYES, LookAnchor.EYES);
        player.resetLastActionTime();
        return true;
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public void stop() {

    }

    private @Nullable Entity getNearestEntity(@NotNull Player player, @NotNull Collection<? extends Entity> entities) {
        if (entities.isEmpty()) {
            return null;
        }
        var loc = player.getLocation();
        Entity nearest = null;
        double distance = 0;
        for (var entity : entities) {
            if (nearest == null) {
                nearest = entity;
            } else {
                var d = loc.distance(entity.getLocation());
                if (d < distance) {
                    nearest = entity;
                    distance = d;
                }
            }
        }
        return nearest;
    }
}
