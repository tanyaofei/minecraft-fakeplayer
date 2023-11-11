package io.github.hello09x.fakeplayer.api.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropStackAction implements Action {

    @NotNull
    private final NMSServerPlayer player;

    public DropStackAction(@NotNull NMSServerPlayer player) {
        this.player = player;
    }


    @Override
    public boolean tick() {
        player.drop(true);
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
