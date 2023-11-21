package io.github.hello09x.fakeplayer.core.entity.action.impl;

import com.google.common.collect.Iterables;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.entity.Damageable;
import org.jetbrains.annotations.NotNull;

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

        bukkitPlayer.lookAt(Iterables.getFirst(entities, null), LookAnchor.EYES, LookAnchor.EYES);
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
