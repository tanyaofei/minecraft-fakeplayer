package io.github.hello09x.fakeplayer.api.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class JumpAction implements Action {

    @NotNull
    private final NMSServerPlayer player;

    public JumpAction(@NotNull NMSServerPlayer player) {
        this.player = player;
    }


    @Override
    public boolean tick() {
        if (player.onGround()) {
            player.jumpFromGround();
        } else {
            player.setJumping(true);
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
