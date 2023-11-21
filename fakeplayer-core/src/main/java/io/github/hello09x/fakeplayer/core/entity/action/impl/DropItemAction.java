package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropItemAction implements Action {

    @NotNull
    private final NMSServerPlayer player;

    public DropItemAction(@NotNull NMSServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean tick() {
        player.drop(false);
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
