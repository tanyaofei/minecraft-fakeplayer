package io.github.hello09x.fakeplayer.api.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropInventoryAction implements Action {


    @NotNull
    private final NMSServerPlayer player;

    public DropInventoryAction(@NotNull NMSServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean tick() {
        var inventory = player.getPlayer().getInventory();
        for (int i = inventory.getSize(); i >= 0; i--) {
            player.drop(i, false, true);
        }
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
